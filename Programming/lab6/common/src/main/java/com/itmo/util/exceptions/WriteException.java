package com.itmo.util.exceptions;

// Исключение для ошибок выгрузки (записи) коллекции

public class WriteException extends Exception {
    public WriteException(String message) {
        super(message);
    }
}


