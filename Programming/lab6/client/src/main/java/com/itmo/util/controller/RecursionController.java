package com.itmo.util.controller;

// Интерфейс для описания обработки рекурсии при исполнении скриптов

public interface RecursionController {
    void pushScript(String script);
    void popScript(String script);
    boolean checkRecursion(String script);
}
