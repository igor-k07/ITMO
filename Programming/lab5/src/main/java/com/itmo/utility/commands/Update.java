package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.Ask;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

import java.nio.file.Path;

public class Update extends Command {
    private final CollectionManager collectionManager;
    private final Console console;

    public Update(Console console, CollectionManager collectionManager) {
        super("update <id> {element}",
                "обновить значение элемента из коллекции по id");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        try{
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

            var old = collectionManager.byId(id);
            if (old == null || !collectionManager.getCollection().contains(old)) {
                return new ExecutionResponse(false, "Несуществующий id");
            }

            console.println("Изменение музыкальной группы с id = " + id + ':');
            MusicBand newBand = Ask.askMusicBand(console, collectionManager.getFreeId());

            if (newBand != null && newBand.validate()) {
                collectionManager.remove(old.getId());
                collectionManager.add(newBand);
                return new ExecutionResponse("Обновлено!");
            } else {
                return new ExecutionResponse(false,
                        "Поля не валидны! Музыкальная группа не изменена!");
            }
        } catch (Ask.AskBreak e) {
            return new ExecutionResponse(false, "Отмена...");
        }
    }
}