package util.console;

import java.util.Scanner;


/**
 * Консоль воода-вывода.
 * @author Septyq
 */
public class IOConsole implements Console {
    private Scanner userScanner;
    private boolean fileMode = false;
    private final String promptSymbol = "$ ";
    private final String scriptPrompotySymbol = ":> ";

    public Scanner getUserScanner() {
        return this.userScanner;
    }

    public void setUserScanner(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    public boolean fileMode() {
        return fileMode;
    }

    public void setUserMode() {
        this.fileMode = false;
    }

    public void setFileMode() {
        this.fileMode = true;
    }

    public void print(Object obj) {
        System.out.print(obj.toString());
    }

    public void println(Object obj) {
        System.out.println(obj.toString());
    }

    public void printError(Object obj) {
        System.out.println("[Error]: " + obj.toString());
    }

    public void printPromptSymbol() {
        print(promptSymbol);
    }

    public String getPromptSymbol() {
        return promptSymbol;
    }

    public String getScriptPromptSymbol() {
        return scriptPrompotySymbol;
    }
}
