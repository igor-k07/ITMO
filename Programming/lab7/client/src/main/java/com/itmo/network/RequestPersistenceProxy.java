package com.itmo.network;

import com.itmo.util.Status;
import com.itmo.util.request.InitRequest;
import com.itmo.util.request.Request;
import com.itmo.util.response.Response;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestPersistenceProxy implements RequestTransport {
    private static final long RETRY_DELAY_MS = 1000L;

    private final Deque<Request> queuedRequests = new ArrayDeque<>();
    private final AtomicBoolean retryLoopStarted = new AtomicBoolean(false);
    private final RequestTransport delegate;

    public RequestPersistenceProxy(RequestTransport delegate) {
        this.delegate = delegate;
        startRetryLoop();
    }

    @Override
    public synchronized Response<?> send(Request request) {
        if (request instanceof InitRequest) {
            return callDelegate(request);
        }

        Response<?> flushedResponse = flushQueue();
        if (flushedResponse != null && !queuedRequests.isEmpty()) {
            queuedRequests.addLast(request);
            return new Response<>(List.of("Сервер недоступен, запрос отложен в памяти клиента"), Status.ERROR);
        }

        try {
            return delegate.send(request);
        } catch (TransportUnavailableException e) {
            queuedRequests.addLast(request);
            return new Response<>(List.of("Сервер недоступен, запрос отложен в памяти клиента"), Status.ERROR);
        }
    }

    private synchronized Response<?> flushQueue() {
        while (!queuedRequests.isEmpty()) {
            Request queuedRequest = queuedRequests.peekFirst();
            try {
                delegate.send(queuedRequest);
            } catch (TransportUnavailableException e) {
                return new Response<>(List.of(e.getMessage()), Status.ERROR);
            }
            queuedRequests.removeFirst();
        }

        return new Response<>(List.of("Очередь запросов успешно отправлена"), Status.OK);
    }

    private Response<?> callDelegate(Request request) {
        try {
            return delegate.send(request);
        } catch (TransportUnavailableException e) {
            return new Response<>(List.of(e.getMessage()), Status.ERROR);
        }
    }

    private void startRetryLoop() {
        if (!retryLoopStarted.compareAndSet(false, true)) {
            return;
        }

        Thread retryThread = new Thread(() -> {
//            Зачем создаем новый поток

//            Что такое ребут

//            проиндексировать классы чтобы быстро запускать локально
            while (true) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                    synchronized (RequestPersistenceProxy.this) {
                        if (!queuedRequests.isEmpty()) {
                            flushQueue();
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "client-request-retry-loop");
        retryThread.setDaemon(true);
        retryThread.start();
    }
}