package com.itmo.runtime;

import com.itmo.commands.*;
import com.itmo.managers.*;
import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;
import com.itmo.util.exceptions.LoadException;
import com.itmo.util.exceptions.RuntimeInitException;
import com.itmo.util.request.Request;
import com.itmo.util.request.InitRequest;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


// Обработчик серверной части (Обрабатывает запросы на исполнение комманд)

public class RemoteRuntime {
    private final DumpManager dumpManager;
    private final CollectionManager<Element> collectionManager;
    private final CommandManager commandManager;

    public RemoteRuntime(String fileName) throws RuntimeInitException {
        this.commandManager = new CommandManager();
        this.dumpManager = new DumpManager(fileName);

        Collection<Element> collection = new ArrayList<>();
        try {
            collection = dumpManager.readCollectionFromFile();
        } catch (LoadException e) {
            throw new RuntimeInitException(e.getMessage());
        }
        this.collectionManager = new CollectionManager<Element>(collection);
    }


    public void registerCommands() {
        commandManager.register("help", new Help(commandManager));
        commandManager.register("info", new Info(collectionManager));
        commandManager.register("show", new Show(collectionManager));
        commandManager.register("add", new Add(collectionManager));
        commandManager.register("update", new Update(collectionManager));
        commandManager.register("remove_by_id", new RemoveById(collectionManager));
        commandManager.register("clear", new Clear(collectionManager));
        commandManager.register("save", new Save(collectionManager, dumpManager));
        commandManager.register("execute_script", new ExecuteScript());
        commandManager.register("exit", new Exit());
        commandManager.register("add_if_max", new AddIfMax(collectionManager));
        commandManager.register("remove_greater", new RemoveGreater(collectionManager));
        commandManager.register("remove_lower", new RemoveLower(collectionManager));
        commandManager.register("min_by_best_album", new MinByBestAlbum(collectionManager));
        commandManager.register("max_by_number_of_participants", new MaxByNumberOfParticipants(collectionManager));
        commandManager.register("filter_by_number_of_participants", new FilterByNumberOfParticipants(collectionManager));
    }

    public Response<?> proccessRequest(Request request) {
        if (request instanceof InitRequest) {
            Map<String, Class<? extends Request>> attributes = new HashMap<>(commandManager.getCommandAttributes());
            attributes.remove("save");
            return new Response<>(List.of(attributes));
        } else if (request instanceof StandartRequest) {
            return executeCommand((StandartRequest) request);
        } else {
            return new Response<>(List.of("Неизвестный запрос"), Status.ERROR);
        }
    }

    public Status saveCollection() {
        return collectionManager.saveCollection(dumpManager);
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


