package com.itmo.network;

public class TransportUnavailableException extends RuntimeException {
    public TransportUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}