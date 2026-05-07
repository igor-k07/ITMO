package com.itmo.util.exceptions;


// Исключение для ошибок запуска основных модулей

public class RuntimeInitException extends Exception {
    public RuntimeInitException(String message) {
        super(message);
    }
}


