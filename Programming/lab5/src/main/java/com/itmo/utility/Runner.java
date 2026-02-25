package com.itmo.utility;

import com.itmo.managers.CommandManager;
import com.itmo.utility.abstracted.interfaces.Console;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Runner {
    private Console console;
    private final CommandManager commandManager;
    private final List<String> scriptStack = new ArrayList<>();
    private int lenghtRecursion = -1;

    public Runner(Console console, CommandManager commandManager) {
        this.console = console;
        this.commandManager = commandManager;
    }

    public void interactiveMode() {
        try {
            ExecutionResponse commandStatus;
            String[] userCommand = {"", ""};

            while (true) {
                console.prompt();
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                commandManager.addToHistory(userCommand[0]);
                commandStatus = launchCommand(userCommand);

                if (userCommand[0].equals("exit")) {
                    console.println(commandStatus.getMessage());
                    break;
                }
                console.println(commandStatus.getMessage());
            }
        } catch (NoSuchElementException e) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException e) {
            console.printError("Непредвиденная ошибка!");
        }
    }

    private boolean checkRecursion(String argument, Scanner scriptScanner) {
        var recStart = -1;
        var i = 0;
        for (String script : scriptStack) {
            i++;
            if (argument.equals(script)) {
                if (recStart < 0) {
                    recStart = i;
                }
                if (lenghtRecursion < 0) {
                    console.selectConsoleScanner();
                    console.println("Была замечена рекурсия! " +
                            "Введите максимальную глубину рекурсии (0...500");
                    while (lenghtRecursion < 0 || lenghtRecursion > 500) {
                        try {
                            console.print("> ");
                            lenghtRecursion = Integer.parseInt(console.readln().trim());
                        } catch (NumberFormatException e) {
                            console.println("неверная длинна");
                        }
                    }
                    console.selectFileScanner(scriptScanner);
                }
                if (i > recStart + lenghtRecursion || i > 500) {
                    return false;
                }
            }
        }
        return true;
    }

    private ExecutionResponse scriptMode(String argument) {
        String[] userCommand = {"", ""};
        StringBuilder executionOutput = new StringBuilder();

        if (!new File(argument).exists()) {
            return new ExecutionResponse(false, "Файла не существует!");
        }
        if (!Files.isReadable(Paths.get(argument))) {
            return new ExecutionResponse(false, "Прав для чтения нет!");
        }
        scriptStack.add(argument);

        try (FileInputStream fis = new FileInputStream(argument);
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
             Scanner scriptScanner = new Scanner(reader)) {

            ExecutionResponse commandStatus;

            if (!scriptScanner.hasNext()) {
                throw new NoSuchElementException();
            }
            console.selectFileScanner(scriptScanner);
            do {
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (console.isCanReadln() && userCommand[0].isEmpty()) {
                    userCommand = (console.readln().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                executionOutput.append(console.getPrompt() +
                        String.join(" ", userCommand) + "\n");
                var needLaunch = true;
                if (userCommand[0].equals("execute_script")) {
                    needLaunch = checkRecursion(userCommand[1], scriptScanner);
                }

                commandStatus = needLaunch ? launchCommand(userCommand) :
                        new ExecutionResponse("Превышена максимальная глубина рекурсии");
                if (userCommand[0].equals("execute_script")) {
                    console.selectFileScanner(scriptScanner);
                }
                executionOutput.append(commandStatus.getMessage() + "\n");
            } while (commandStatus.getExitCode()
                    && !commandStatus.getMessage().equals("exit")
                    && console.isCanReadln());

            console.selectConsoleScanner();
            if (!commandStatus.getExitCode()
                    && !(userCommand[0].equals("execute_script")
                    && !userCommand[1].isEmpty())) {
                executionOutput.append("Проверьте скрипт на корректность!");
            }
            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());
        } catch (FileNotFoundException e) {
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException e) {
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException | IOException e) {
            console.println("Непредвиденная ошибка!");
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return new ExecutionResponse("");
    }

    private ExecutionResponse launchCommand(String[] userCommand) {
        if (userCommand[0].equals("")) {
            return new ExecutionResponse("");
        }
        var command = commandManager.getCommands().get(userCommand[0]);

        if (command == null) {
            return new ExecutionResponse(false, "Команда '" +
                    userCommand[0] + "' не найдена. Наберите 'help' для справки");
        }

        switch (userCommand[0]) {
            case "execute_script" -> {
                ExecutionResponse tmp = commandManager.getCommands()
                        .get("execute_script")
                        .apply(userCommand);
                if (!tmp.getExitCode()) {
                    return tmp;
                }
                ExecutionResponse tmp2 = scriptMode(userCommand[1]);
                return new ExecutionResponse(tmp2.getExitCode(),
                        tmp.getMessage() + "\n" + tmp2.getMessage().trim());
            }
            default -> {
                return command.apply(userCommand);
            }
        }
    }
}
