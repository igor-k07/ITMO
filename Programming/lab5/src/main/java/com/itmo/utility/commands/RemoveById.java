package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class RemoveById extends Command {
    private final CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id", "удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        int id = -1;
        try {
            id = Integer.parseInt(arguments[1].trim());
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "ID не распознан");
        }

        if (collectionManager.byId(id) == null ||
                !collectionManager.getCollection().contains(collectionManager.byId(id))) {
            return new ExecutionResponse(false, "Несуществующий id");
        }
        collectionManager.remove(id);
        return new ExecutionResponse("Музыкальная группа успешно удалена!");
    }
}