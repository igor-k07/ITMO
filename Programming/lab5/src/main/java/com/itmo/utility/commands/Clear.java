package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        collectionManager.clearCollection();
        return new ExecutionResponse("Коллекция очищена!");
    }
}