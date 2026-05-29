package com.itmo.runtime;

import com.itmo.network.ClientTransport;
import com.itmo.util.RecursionController;
import com.itmo.util.Status;
import com.itmo.util.console.StandardConsole;
import com.itmo.util.exceptions.IncorrectRequestException;
import com.itmo.util.exceptions.RuntimeInitException;
import com.itmo.util.exceptions.ScriptSyntaxException;
import com.itmo.util.request.Request;
import com.itmo.util.request.RequestBuilder;
import com.itmo.util.request.InitRequest;
import com.itmo.util.response.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

// Обработчик клиентской части (Считывает команды из консоли или скрипта, выводит результат)

public class LocalRuntime {
    private final StandardConsole console;
    private final Scanner scanner;
    private final ClientTransport transport;
    private Map<String, Class<? extends Request>> commandsAttributes = new HashMap<>(); 
    private final List<String> scriptStack = new ArrayList<>();
    private RecursionController recursionController;

    public LocalRuntime(ClientTransport transport, RecursionController recursionController) {
        this.recursionController = recursionController;
        this.console = new StandardConsole();
        console.setUserScanner(new Scanner(System.in));
        this.scanner = this.console.getUserScanner();
        this.transport = transport;
    }


    public void run(String... args) {
        switch (args[0].toLowerCase()) {
            case "interactive":
                runInteractiveMode();
                break;
            case "script":
                String fileName = args[1];
                if (fileName == null || fileName.isEmpty()) {
                    console.printError("Некорректное имя файла скрипта: " + fileName);
                    System.exit(0);
                }
                runScriptMode(fileName);
                break;
            default:
                console.printError("Некорректный режим запуска");
        }
    }
    

    private void runScriptMode(String fileName) {
        String[] userCommand = {"", ""};
        Status commandStatus = setCommandAttributes();
        scriptStack.add(fileName);

        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            console.println(String.format("--ЗАПУСК СКРИПТА: %s ...", fileName));

            File file = new File(fileName);
            if (!file.exists()) throw new FileNotFoundException("Файл не существует");
            if (!file.canRead()) throw new SecurityException("Нет прав на чтение файла: " + file.getAbsolutePath());
            if (!file.canWrite()) throw new SecurityException("Нет прав на запись в файл: " + file.getAbsolutePath());
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();

            Scanner tmpScanner = console.getUserScanner();
            console.setUserScanner(scriptScanner);
            console.setFileMode();

            while (commandStatus != Status.EXIT && scriptScanner.hasNextLine()) {
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (scriptScanner.hasNextLine() && userCommand[0].isEmpty()) {
                    userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                console.println(console.getScriptPromptSymbol() + String.join(" ", userCommand));

                String commandName = userCommand[0];
                List<?> args = List.of(userCommand[1]);
                if (args.size() == 1 && args.get(0) instanceof String && ((String) args.get(0)).isEmpty()) {
                    args = List.of();
                }
                commandStatus = executeCommand(commandName, args);
            }
            console.setUserScanner(tmpScanner);
            console.setUserMode();

            if (commandStatus == Status.ERROR && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                console.println("Некорректный скрипт");
            }

        } catch (FileNotFoundException exception) {
            console.printError("Файл не найден");
        } catch (NoSuchElementException exception) {
            console.printError("Файл пуст");
        } catch (IllegalStateException exception) {
            console.printError("Неизвестная ошибка");
            System.exit(0);
        } catch (SecurityException e) {
            console.printError(e.getMessage());
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
    }

    
    private void runInteractiveMode() {
        console.println("--ПРОГРАММА УСПЕШНО ЗАПУЩЕННА--");
        String[] userCommand = {"", ""};
        Status commandStatus = setCommandAttributes();

        while (commandStatus != Status.EXIT) {
            console.printPromptSymbol();

            userCommand = (scanner.nextLine().trim() + " ").split(" ", 2);
            userCommand[1] = userCommand[1].trim();
            
            String commandName = userCommand[0];
            List<?> args = List.of(userCommand[1]);
            if (args.size() == 1 && args.get(0) instanceof String && ((String) args.get(0)).isEmpty()) {
                args = List.of();
            }

            commandStatus = executeCommand(commandName, args);
        }
    };

    private Status executeCommand(String commandName, List<?> args) {
        if (commandName == null || commandName.isEmpty()) return Status.ERROR;
        
        if (commandName.equals("execute_script")) {
            Response<?> scriptResponse = new Response<>();
            String fileName = (String) args.get(0);
            if (recursionController.checkRecursion(fileName)) {
                scriptResponse = new Response<>(List.of("Обнаружена рекурсия в скрипте!"), Status.ERROR);
            } else {
                if (fileName == null || fileName.isEmpty()) {
                    scriptResponse = new Response<>(List.of("Некорректное имя скрипта"), Status.ERROR);
                }

                recursionController.pushScript(fileName);

                LocalRuntime localRuntime = new LocalRuntime(transport, recursionController);
                localRuntime.run("script", fileName);

                recursionController.popScript(fileName);

                scriptResponse  = new Response<>();
            }

            return processCommandResponse(scriptResponse);
        }

        return processCommandResponse(makeRequest(commandName, args));
    }

    private Status processCommandResponse(Response<?> response) {
        Status status = response.getStatus();
        if (status == Status.OK) {
            printCommandResponse(response.getBody());
        } else if (status == Status.ERROR) {
            if (response.getBody() != null && !response.getBody().isEmpty()) {
                console.printError(response.getBody().get(0).toString());
            } else {
                console.printError("Неизвестная ошибка");
            }
        }
        return status;
    }

    private void printCommandResponse(List<?> body) {
        if (!(body == null || body.isEmpty())) {
            body.forEach((element) -> {
                console.println(element);
            });
        }
    }

    @SuppressWarnings("unchecked")
    private Status setCommandAttributes() {
        try {
            Response<?> response = makeRequest("init", new ArrayList<>());

            List<?> body = response.getBody();
        
            if (body == null || body.isEmpty()) {
                throw new RuntimeInitException("Пустое тело ответа");
            }

            Object item = body.get(0);
            if (!(item instanceof Map<?, ?>)) {
                throw new RuntimeInitException("Ожидалась карта команд, получено: " + 
                    (item != null ? item.getClass().getSimpleName() : "пустое значение"));
            }

            commandsAttributes = (Map<String, Class<? extends Request>>) item;
            return Status.OK;
        } catch (RuntimeInitException e) {
            console.printError(e.getMessage());
            return Status.EXIT;
        }
    }

    private Response<?> makeRequest(String name, List<?> args) {
        Request request;
        if ("init".equals(name)) {
            request = new InitRequest();
        } else {
            try {
                RequestBuilder requestBuilder = new RequestBuilder(console);
                request = requestBuilder.buildRequest(commandsAttributes, name, args);   
            } catch (IncorrectRequestException e) {
                return new Response<>(List.of(e.getMessage()), Status.ERROR);
            } catch (ScriptSyntaxException e) {
                return new Response<>(List.of(e.getMessage()), Status.EXIT);
            }
        }
        return transport.send(request);
    }


}


