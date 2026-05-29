package com.itmo.util.exceptions;

// Исключение для ошибок загрузки коллекции

public class LoadException extends Exception {
    public LoadException(String message) {
        super(message);
    }
}


