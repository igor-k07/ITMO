package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.Ask;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

import java.util.ArrayList;
import java.util.List;

public class RemoveLower extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemoveLower(Console console, CollectionManager collectionManager) {
        super("remove_by_id", "удалить группы с количеством " +
                "участников меньше, чем в заданной");
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
            console.println("Удаление группы с числом участников меньше, чем в заданной:");
            MusicBand greatBand = Ask.askId(console, collectionManager);
            if (greatBand != null) {
                int l = collectionManager.getSizeCollection();
                List<Integer> idsToRemove = new ArrayList<>();
                for (var band : collectionManager.getCollection()) {
                    if (greatBand.getNumberOfParticipants() > band.getNumberOfParticipants()) {
                        idsToRemove.add(band.getId());
                    }
                }

                for (int id : idsToRemove) {
                    collectionManager.remove(id);
                }

                if (l != collectionManager.getSizeCollection()) {
                    return new ExecutionResponse("Музыкальные группы с количеством " +
                            "участников меньше " +
                            greatBand.getNumberOfParticipants() +
                            " успешно удалены!");
                }

                return new ExecutionResponse(false,"Музыкальных групп с количеством " +
                        "участников меньше " +
                        greatBand.getNumberOfParticipants() +
                        " в коллекции нет");
            }
            return new ExecutionResponse(false, "");

        } catch (Ask.AskBreak e) {
            return new ExecutionResponse(false, "Отмена...");
        }
    }

}