package runtime;

import util.Status;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.console.IOConsole;
import util.controller.RecursionController;
import util.exceptions.IncorrectRequestException;
import util.exceptions.RuntimeInitException;
import util.exceptions.ScriptSyntaxException;
import util.transfer.request.Request;
import util.transfer.request.RequestBuilder;
import util.transfer.request.empty.InitRequest;
import util.transfer.response.Response;


/**
 * Считывает команды из консоли или скрипта, выводит результат.
 * @author Septyq
 */
public class LocalRuntime extends Runtime{
    private final IOConsole console;
    private final Scanner scanner;
    private final RemoteRuntime remoteRuntime;
    private Map<String, Class<? extends Request>> commandsAttributes = new HashMap<>(); 
    private final List<String> scriptStack = new ArrayList<>();
    private RecursionController recursionController;

    public LocalRuntime(RemoteRuntime remoteRuntime, RecursionController recursionController) {
        this.recursionController = recursionController;
        this.console = new IOConsole();
        console.setUserScanner(new Scanner(System.in));
        this.scanner = this.console.getUserScanner();
        this.remoteRuntime = remoteRuntime;
        this.remoteRuntime.registerCommands();
    }


    public void run(String... args) {
        switch (args[0].toLowerCase()) {
            case "interactive":
                runInteractiveMode();
                break;
            case "script":
                String fileName = args[1];
                if (fileName == "") {
                    console.printError("Invalid script file name: "+fileName);
                    System.exit(0);
                }
                runScriptMode(fileName);
                break;
            default:
                console.printError("Invalid local runtime mode");
        }
    }
    

    private void runScriptMode(String fileName) {
        String[] userCommand = {"", ""};
        Status commandStatus = setCommandAttributes();
        scriptStack.add(fileName);

        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            console.println(String.format("--> RUNNING SCRIPT: %s ...", fileName));

            File file = new File(fileName);
            if (!file.exists()) throw new FileNotFoundException("File does not exist");
            if (!file.canRead()) throw new SecurityException("No read permission for file: " + file.getAbsolutePath());
            if (!file.canWrite()) throw new SecurityException("No write permission for file: " + file.getAbsolutePath());
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
                if (args.size() == 1 && args.get(0) == "") {
                    args = List.of();
                }
                commandStatus = executeCommand(commandName, args);
            }
            console.setUserScanner(tmpScanner);
            console.setUserMode();

            if (commandStatus == Status.ERROR && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                console.println("Invalid script");
            }

        } catch (FileNotFoundException exception) {
            console.printError("File not found");
        } catch (NoSuchElementException exception) {
            console.printError("File is empty");
        } catch (IllegalStateException exception) {
            console.printError("Unknowm error");
            System.exit(0);
        } catch (SecurityException e) {
            console.printError(e.getMessage());
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
    }

    
    private void runInteractiveMode() {
        console.println("<----- COLLECTION MANAGER CLI STARTED ----->");

        String[] userCommand = {"", ""};
        Status commandStatus = setCommandAttributes();

        while (commandStatus != Status.EXIT) {
            console.printPromptSymbol();

            userCommand = (scanner.nextLine().trim() + " ").split(" ", 2);
            userCommand[1] = userCommand[1].trim();
            
            String commandName = userCommand[0];
            List<?> args = List.of(userCommand[1]);
            if (args.size() == 1 && args.get(0) == "") {
                args = List.of();
            }

            commandStatus = executeCommand(commandName, args);
        }
    };

    private Status executeCommand(String commandName, List<?> args) {
        if (commandName == "" || commandName == null) return Status.ERROR;

        console.println(commandName);
        if (commandName.equals("execute_script")) {
            Response<?> scriptResponse = new Response<>();
            String fileName = (String) args.get(0);
            if (recursionController.checkRecursion(fileName)) {
                scriptResponse = new Response<>(List.of("Script has recursion!"), Status.ERROR);
            } else {
                if (fileName == "") scriptResponse  = new Response<>(List.of("Invalid script name"), Status.ERROR);

                recursionController.pushScript(fileName);

                LocalRuntime localRuntime = new LocalRuntime(remoteRuntime, recursionController);
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
                console.printError("Unknown error");
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
                throw new RuntimeInitException("Empty response body");
            }

            Object item = body.get(0);
            if (!(item instanceof Map<?, ?>)) {
                throw new RuntimeInitException("Expected Map, got: " + 
                    (item != null ? item.getClass().getSimpleName() : "null"));
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
        if (name == "init") {
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
        return remoteRuntime.proccessRequest(request);
    }


}
