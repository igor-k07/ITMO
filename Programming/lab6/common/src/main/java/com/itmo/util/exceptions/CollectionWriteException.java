package com.itmo.util.exceptions;

// Исключение для ошибок выгрузки (записи) коллекции

public class CollectionWriteException extends Exception {
    public CollectionWriteException(String message) {
        super(message);
    }
}


