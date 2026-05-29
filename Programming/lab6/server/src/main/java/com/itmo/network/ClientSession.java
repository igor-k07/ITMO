package com.itmo.network;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientSession {
    private final ByteBuffer lengthBuffer = ByteBuffer.allocate(Integer.BYTES);
    private ByteBuffer dataBuffer;
    private ByteBuffer writeBuffer;

    public byte[] tryReadMessage(SocketChannel channel) throws IOException {
        if (dataBuffer == null) {
            int read = channel.read(lengthBuffer);
            if (read == -1) {
                throw new EOFException("Client disconnected");
            }
            if (lengthBuffer.remaining() > 0) {
                return null;
            }
            lengthBuffer.flip();
            int length = lengthBuffer.getInt();
            lengthBuffer.clear();
            if (length <= 0) {
                throw new IOException("Invalid message length: " + length);
            }
            dataBuffer = ByteBuffer.allocate(length);
        }

        int read = channel.read(dataBuffer);
        if (read == -1) {
            throw new EOFException("Client disconnected");
        }
        if (dataBuffer.remaining() > 0) {
            return null;
        }

        dataBuffer.flip();
        byte[] payload = new byte[dataBuffer.remaining()];
        dataBuffer.get(payload);
        dataBuffer = null;
        return payload;
    }

    public void prepareResponse(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + payload.length);
        buffer.putInt(payload.length);
        buffer.put(payload);
        buffer.flip();
        this.writeBuffer = buffer;
    }

    public boolean hasPendingWrite() {
        return writeBuffer != null && writeBuffer.hasRemaining();
    }

    public void writeTo(SocketChannel channel) throws IOException {
        if (writeBuffer == null) {
            return;
        }
        channel.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            writeBuffer = null;
        }
    }
}
