package com.itmo.network;

import com.itmo.util.Status;
import com.itmo.util.request.Request;
import com.itmo.util.response.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class ClientTransport implements RequestTransport {
    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int READ_TIMEOUT_MS = 3000;

    private final String host;
    private final int port;

    public ClientTransport(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response<?> send(Request request) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(READ_TIMEOUT_MS);

            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                byte[] payload = SerializationUtils.serialize((Serializable) request);
                out.writeInt(payload.length);
                out.write(payload);
                out.flush();

                int length = in.readInt();
                if (length <= 0) {
                    return new Response<>(List.of("Некорректная длина ответа"), Status.ERROR);
                }
                byte[] responseBytes = new byte[length];
                in.readFully(responseBytes);
                Object obj = SerializationUtils.deserialize(responseBytes);
                if (obj instanceof Response) {
                    return (Response<?>) obj;
                }
                return new Response<>(List.of("Некорректный формат ответа"), Status.ERROR);
            }
        } catch (SocketTimeoutException e) {
            return new Response<>(List.of("Сервер не отвечает"), Status.ERROR);
        } catch (IOException | ClassNotFoundException e) {
            return new Response<>(List.of("Сервер недоступен"), Status.ERROR);
        }
    }
}
