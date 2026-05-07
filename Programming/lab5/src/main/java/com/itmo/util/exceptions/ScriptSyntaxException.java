package com.itmo.util.exceptions;

// Исключение для ошибок синтаксиса в скриптах

public class ScriptSyntaxException extends Exception {
    public ScriptSyntaxException(String message) {
        super(message);
    }
}
