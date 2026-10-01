package com.itmo.runtime;

import com.itmo.commands.*;
import com.itmo.managers.*;
import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;
import com.itmo.util.exceptions.RuntimeInitException;
import com.itmo.util.request.Request;
import com.itmo.util.request.InitRequest;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


// Обработчик серверной части (Обрабатывает запросы на исполнение комманд)

public class RemoteRuntime {
    private final DatabaseManager databaseManager;
    private final UserManager userManager;
    private final CollectionManager<Element> collectionManager;
    private final CommandManager commandManager;

    public RemoteRuntime() throws RuntimeInitException {
        this.commandManager = new CommandManager();
        this.databaseManager = new DatabaseManager();
        this.userManager = new UserManager();

        try {
            Collection<Element> collection = databaseManager.loadCollection();
            this.collectionManager = new CollectionManager<>(collection);
        } catch (Exception e) {
            throw new RuntimeInitException("Не удалось загрузить коллекцию из базы данных: " + e.getMessage());
        }
    }


    public void registerCommands() {
        commandManager.register("register", new Register(userManager));
        commandManager.register("help", new Help(commandManager));
        commandManager.register("info", new Info(collectionManager));
        commandManager.register("show", new Show(collectionManager));
        commandManager.register("add", new Add(collectionManager, databaseManager));
        commandManager.register("update", new Update(collectionManager, databaseManager));
        commandManager.register("remove_by_id", new RemoveById(collectionManager, databaseManager));
        commandManager.register("clear", new Clear(collectionManager, databaseManager));
        commandManager.register("execute_script", new ExecuteScript());
        commandManager.register("exit", new Exit());
        commandManager.register("add_if_max", new AddIfMax(collectionManager, databaseManager));
        commandManager.register("remove_greater", new RemoveGreater(collectionManager, databaseManager));
        commandManager.register("remove_lower", new RemoveLower(collectionManager, databaseManager));
        commandManager.register("min_by_best_album", new MinByBestAlbum(collectionManager));
        commandManager.register("max_by_number_of_participants", new MaxByNumberOfParticipants(collectionManager));
        commandManager.register("filter_by_number_of_participants", new FilterByNumberOfParticipants(collectionManager));
    }

    public Response<?> proccessRequest(Request request) {
        if (request instanceof InitRequest) {
            Map<String, Class<? extends Request>> attributes = new HashMap<>(commandManager.getCommandAttributes());
            return new Response<>(List.of(attributes));
        } else if (request instanceof StandartRequest) {
            StandartRequest standartRequest = (StandartRequest) request;
            if ("register".equals(standartRequest.getName())) {
                return executeCommand(standartRequest);
            }
            return authorizeAndExecute(standartRequest);
        } else {
            return new Response<>(List.of("Неизвестный запрос"), Status.ERROR);
        }
    }

    private Response<?> authorizeAndExecute(StandartRequest request) {
        try {
            var userId = userManager.authenticate(request.getLogin(), request.getPassword());
            if (userId.isEmpty()) {
                return new Response<>(List.of("Неверный логин или пароль"), Status.ERROR);
            }
            request.setUserId(userId.get());
            return executeCommand(request);
        } catch (IllegalArgumentException e) {
            return new Response<>(List.of(e.getMessage()), Status.ERROR);
        } catch (Exception e) {
            return new Response<>(List.of("Ошибка авторизации: " + e.getMessage()), Status.ERROR);
        }
    }

    private Response<?> executeCommand(StandartRequest request){
        String commandName = request.getName();
        if (!validateCommandName(commandName)) {
            return new Response<>(List.of("Неизвестная команда"), Status.ERROR);
        }
        Command<?> command = commandManager.getCommands().get(commandName);
        commandManager.addToHistory(command.getAttribute().getName());
        
        @SuppressWarnings("unchecked")
        Command<StandartRequest> typedCommand = (Command<StandartRequest>) command;
        return typedCommand.execute(request);
    };

    private boolean validateCommandName(String command) {
        Set<String> commandsNames = commandManager.getCommands().keySet();
        return commandsNames.contains(command);
    }
}


