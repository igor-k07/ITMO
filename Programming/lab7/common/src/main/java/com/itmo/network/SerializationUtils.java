package com.itmo.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Utility for object serialization over the wire
public final class SerializationUtils {
    private SerializationUtils() {}

    public static byte[] serialize(Serializable object) throws IOException {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(object);
            out.flush();
            return bytes.toByteArray();
        }
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(data);
             ObjectInputStream in = new ObjectInputStream(bytes)) {
            return in.readObject();
        }
    }
}
