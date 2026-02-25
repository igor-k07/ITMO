package com.itmo.utility.commands;


import com.itmo.utility.Ask;
import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class Add extends Command {
    private final Console console;
    private CollectionManager collectionManager;

    public Add(Console console, CollectionManager collectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) {
                return new ExecutionResponse(false,
                        "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
            }
            console.println("Создание новой Музыкальной группы:");
            MusicBand band = Ask.askMusicBand(console, collectionManager.getFreeId());

            if (band != null && band.validate()) {
                collectionManager.add(band);
                return new ExecutionResponse("Музыкальная группа успешно добавлена!");
            } else {
                return new ExecutionResponse(false,
                        "Поля не валидны! Музыкальная группа не создана!");
            }
        } catch (Ask.AskBreak e) {
            return new ExecutionResponse(false, "Отмена...");
        }
    }

}