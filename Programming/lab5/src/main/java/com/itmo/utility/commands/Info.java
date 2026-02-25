package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Info extends Command {
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }

        LocalDateTime lastInittime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInittime == null) ?
                "в данной сессии инициализации еще не происходило" :
                lastInittime.toLocalDate().toString() + " " + lastInittime.toLocalTime().toString();

        LocalDateTime lastSavetime = collectionManager.getLastInitTime();
        String lastSaveTimeString = (lastSavetime == null) ?
                "в данной сессии инициализации еще не происходило" :
                lastSavetime.toLocalDate().toString() + " " + lastSavetime.toLocalTime().toString();

        StringBuilder s = new StringBuilder("Сведения о коллекциии:\n");
        s.append("Тип: ")
                .append(collectionManager.getCollection().getClass().toString())
                .append("\n")
                .append("Количество элементов: ")
                .append(collectionManager.getCollection().size())
                .append("\n")
                .append("Дата последнего сохранения: ")
                .append(lastSaveTimeString)
                .append("\n")
                .append("Дата последней инициализации: ")
                .append(lastInitTimeString);
        return new ExecutionResponse(s.toString());
    }
}