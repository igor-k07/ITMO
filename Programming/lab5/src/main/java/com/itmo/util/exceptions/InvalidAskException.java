package com.itmo.util.exceptions;


// Исключение для ошибок заполнения формы нового элемента коллекции

public class InvalidAskException extends Exception {
    public InvalidAskException(String message) {
        super(message);
    }
}


