package com.itmo.utility.consoles;

import com.itmo.utility.abstracted.interfaces.Console;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class StandartConsole implements Console {
    private static final String P = "$ ";
    private static Scanner fileScanner = null;
    private static Scanner defScanner = new Scanner(System.in);

    @Override
    public void print(Object object){
        System.out.print(object);
    }

    @Override
    public void println(Object object){
        System.out.println(object);
    }

    @Override
    public void printError(Object object) {
        System.err.println("Error: " + object);
    }

    @Override
    public String readln() throws NoSuchElementException, IllegalStateException {
        return (fileScanner != null ? fileScanner: defScanner).nextLine();
    }

    @Override
    public boolean isCanReadln() throws IllegalStateException {
        return (fileScanner != null ? fileScanner:defScanner).hasNextLine();
    }

    @Override
    public void printTable(Object elementLeft, Object elementRight){
        System.out.printf(" %-35s%-1s%n", elementLeft, elementRight);
    }

    @Override
    public void prompt(){
        print(P);
    }

    @Override
    public String getPrompt(){
        return P;
    }

    @Override
    public void selectFileScanner(Scanner scanner){
        fileScanner = scanner;
    }

    @Override
    public void selectConsoleScanner(){
        fileScanner = null;
    }

    @Override
    public boolean isFileScanner() {
        return fileScanner != null;
    }
}
