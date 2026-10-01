package com.itmo.util.console;

// Интерфейс, определяющий поведение консоли

public interface Console {
    void print(Object obj);
    void println(Object obj);
    void printError(Object obj);
    void printPromptSymbol();
    String getPromptSymbol();
    String getScriptPromptSymbol();
}

