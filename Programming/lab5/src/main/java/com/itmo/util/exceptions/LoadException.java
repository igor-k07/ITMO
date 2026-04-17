package com.itmo.util.exceptions;

// Исключение для ошибок загрузки коллекции

public class CollectionLoadException extends Exception {
    public CollectionLoadException(String message) {
        super(message);
    }
}


