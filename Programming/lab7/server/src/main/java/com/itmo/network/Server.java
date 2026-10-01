package com.itmo.network;

import com.itmo.runtime.RemoteRuntime;
import com.itmo.util.Status;
import com.itmo.util.request.Request;
import com.itmo.util.response.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private final int port;
    private final RemoteRuntime runtime;
    private final Map<SocketChannel, ClientSession> sessions = new ConcurrentHashMap<>();
    private final ForkJoinPool readPool = new ForkJoinPool();
    private final ForkJoinPool processPool = new ForkJoinPool();

    // Очередь между этапом чтения и этапом обработки
    private final ConcurrentLinkedQueue<ReadResult> readResults = new ConcurrentLinkedQueue<>();
    // Очередь между этапом обработки и этапом отправки
    private final ConcurrentLinkedQueue<SendTask> sendTasks = new ConcurrentLinkedQueue<>();

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private boolean running = true;

    public Server(int port, RemoteRuntime runtime) {
        this.port = port;
        this.runtime = runtime;
    }

    public void start() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (running) {
            selector.select(50);
            processConsoleCommands();

            // Обработка событий Selector
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    handleAccept(key);
                } else if (key.isReadable()) {
                    // ЭТАП 1: отправляем чтение в readPool (из главного потока)
                    handleRead(key);
                } else if (key.isWritable()) {
                    handleWrite(key);
                }
            }

            // ЭТАП 2: забираем прочитанные запросы из очереди,
            // отправляем обработку в processPool (из главного потока)
            dispatchProcessing();

            // ЭТАП 3: забираем готовые ответы из очереди,
            // отправляем отправку через new Thread (из главного потока)
            dispatchSending();
        }

        shutdown();
    }

    // ==================== ЭТАП 1: Чтение (ForkJoinPool) ====================

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        if (client == null) {
            return;
        }
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        sessions.put(client, new ClientSession());
    }

    private void handleRead(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ClientSession session = sessions.get(client);
        if (session == null) {
            closeClient(client);
            return;
        }

        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        // Задача чтения отправляется в readPool из ГЛАВНОГО потока
        readPool.submit(() -> readRequest(key, client, session));
    }

    // Выполняется в потоке readPool.
    // НЕ вызывает другие пулы — кладёт результат в очередь.
    private void readRequest(SelectionKey key, SocketChannel client, ClientSession session) {
        try {
            byte[] payload = session.tryReadMessage(client);
            if (payload == null) {
                enableRead(key);
                return;
            }

            Object obj = SerializationUtils.deserialize(payload);
            if (!(obj instanceof Request request)) {
                // Ошибка чтения — кладём готовый ответ сразу в очередь отправки
                sendTasks.add(new SendTask(key, client, session, errorResponse("Некорректный формат запроса")));
                selector.wakeup();
                return;
            }

            // Кладём прочитанный запрос в очередь для обработки
            readResults.add(new ReadResult(key, client, session, request));
            selector.wakeup();
        } catch (Exception e) {
            sendTasks.add(new SendTask(key, client, session, errorResponse("Ошибка чтения запроса: " + e.getMessage())));
            selector.wakeup();
        }
    }

    // ==================== ЭТАП 2: Обработка (ForkJoinPool) ====================

    // Вызывается из ГЛАВНОГО потока — забирает из очереди и отправляет в processPool
    private void dispatchProcessing() {
        ReadResult result;
        while ((result = readResults.poll()) != null) {
            final ReadResult r = result;
            processPool.submit(() -> processRequest(r));
        }
    }

    // Выполняется в потоке processPool.
    // НЕ вызывает другие потоки — кладёт результат в очередь.
    private void processRequest(ReadResult readResult) {
        try {
            Response<?> response = runtime.proccessRequest(readResult.request);
            sendTasks.add(new SendTask(readResult.key, readResult.client, readResult.session, response));
            selector.wakeup();
        } catch (Exception e) {
            sendTasks.add(new SendTask(readResult.key, readResult.client, readResult.session,
                    errorResponse("Ошибка обработки запроса: " + e.getMessage())));
            selector.wakeup();
        }
    }

    // ==================== ЭТАП 3: Отправка (new Thread) ====================

    // Вызывается из главного потока — забирает из очереди и создаёт Thread
    private void dispatchSending() {
        SendTask task;
        while ((task = sendTasks.poll()) != null) {
            final SendTask t = task;
            Thread responseThread = new Thread(() -> {
                try {
                    t.session.prepareResponse(SerializationUtils.serialize(t.response));
                    t.key.interestOps(SelectionKey.OP_WRITE);
                    selector.wakeup();
                } catch (IOException e) {
                    closeClient(t.client);
                }
            }, "response-sender");
            responseThread.start();
        }
    }

    // ==================== Вспомогательные методы ====================

    private Response<String> errorResponse(String message) {
        Response<String> response = new Response<>(Status.ERROR);
        response.put(message);
        return response;
    }

    private void enableRead(SelectionKey key) {
        if (key.isValid()) {
            key.interestOps(SelectionKey.OP_READ);
            selector.wakeup();
        }
    }

    private void handleWrite(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ClientSession session = sessions.get(client);
        if (session == null) {
            closeClient(client);
            return;
        }

        try {
            session.writeTo(client);
            if (!session.hasPendingWrite()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            closeClient(client);
        }
    }

    private void closeClient(SocketChannel client) {
        try {
            sessions.remove(client);
            client.close();
        } catch (IOException ignored) {
        }
    }

    private void processConsoleCommands() {
        try {
            if (System.in.available() <= 0) {
                return;
            }
            byte[] buffer = System.in.readNBytes(1024);
            String input = new String(buffer, StandardCharsets.UTF_8).trim();
            if (input.isEmpty()) {
                return;
            }
            String command = input.split("\\s+", 2)[0].toLowerCase();
            if (command.equals("exit")) {
                running = false;
            }
        } catch (IOException ignored) {
        }
    }

    private void shutdown() {
        readPool.shutdownNow();
        processPool.shutdownNow();
        try {
            if (serverChannel != null) {
                serverChannel.close();
            }
            if (selector != null) {
                selector.close();
            }
        } catch (IOException ignored) {
        }
    }

    // ==================== Внутренние record-классы для очередей ====================

    // Результат чтения: прочитанный запрос + контекст клиента
    private record ReadResult(SelectionKey key, SocketChannel client, ClientSession session, Request request) {}

    // Задача на отправку: готовый ответ + контекст клиента
    private record SendTask(SelectionKey key, SocketChannel client, ClientSession session, Response<?> response) {}
}
