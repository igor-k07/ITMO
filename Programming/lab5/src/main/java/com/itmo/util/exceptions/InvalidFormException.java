package com.itmo.util.exceptions;

// Исключение для ошибок заполнения формы нового элемента коллекции

public class InvalidFormException extends Exception {
    public InvalidFormException(String message) {
        super(message);
    }
}


