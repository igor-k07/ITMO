package com.itmo.network;

import com.itmo.network.SerializationUtils;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server {
    private final int port;
    private final RemoteRuntime runtime;
    private final Map<SocketChannel, ClientSession> sessions = new HashMap<>();

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
            selector.select(250);
            processConsoleCommands();

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
                    handleRead(key);
                } else if (key.isWritable()) {
                    handleWrite(key);
                }
            }
        }

        shutdown();
    }

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

        try {
            byte[] payload = session.tryReadMessage(client);
            if (payload == null) {
                return;
            }

            Object obj = SerializationUtils.deserialize(payload);
            if (!(obj instanceof Request)) {
                Response<String> response = new Response<>(Status.ERROR);
                response.put("Некорректный формат запроса");
                session.prepareResponse(SerializationUtils.serialize(response));
                key.interestOps(SelectionKey.OP_WRITE);
                return;
            }

            Response<?> response = runtime.proccessRequest((Request) obj);
            session.prepareResponse(SerializationUtils.serialize(response));
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
            Response<String> response = new Response<>(Status.ERROR);
            response.put("Ошибка обработки запроса: " + e.getMessage());
            try {
                session.prepareResponse(SerializationUtils.serialize(response));
                key.interestOps(SelectionKey.OP_WRITE);
            } catch (IOException ioException) {
                closeClient(client);
            }
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
            if (command.equals("save")) {
                runtime.saveCollection();
            } else if (command.equals("exit")) {
                runtime.saveCollection();
                running = false;
            }
        } catch (IOException ignored) {
        }
    }

    private void shutdown() {
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
}
