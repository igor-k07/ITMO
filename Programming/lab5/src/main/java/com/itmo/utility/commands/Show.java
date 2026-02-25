package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "Вывести все элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        return new ExecutionResponse(collectionManager.toString());
    }
}