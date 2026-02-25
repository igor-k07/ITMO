package com.itmo.utility.abstracted.interfaces;

import java.util.Scanner;

public interface Console {
    void print(Object object);
    void println(Object object);
    String readln();
    boolean isCanReadln();
    void printError(Object object);
    void printTable(Object object1, Object object2);
    void prompt();
    String getPrompt();
    void selectFileScanner(Scanner object);
    void selectConsoleScanner();
    boolean isFileScanner();

}
