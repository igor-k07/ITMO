package com.itmo.util.exceptions;


// Исключение для ошибок вызова команд

public class IncorrectRequestException extends Exception {
    public IncorrectRequestException(String message) {
        super(message);
    }
}


