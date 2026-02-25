package com.itmo.utility.commands;


import com.itmo.utility.Ask;
import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class AddIfMax extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public AddIfMax(Console console, CollectionManager collectionManager) {
        super("add_if_max {element}",
                "добавить новый элемент в коллекцию, " +
                        "если его цена превышает максимальную цену этой коллекции");
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
            console.println("Создание новой Музыкальной группы (add_if_max):");
            MusicBand band = Ask.askMusicBand(console, collectionManager.getFreeId());
            var maxParticipants = maxParticipants();

            if (band != null && band.validate()) {
                if (band.getNumberOfParticipants() > maxParticipants) {
                    collectionManager.add(band);
                    return new ExecutionResponse("Музыкальная группа успешно добавлена");
                } else {
                    return new ExecutionResponse("Музыкальная группа недобавлена, " +
                            "количество ее участников не максимально (" +
                            band.getNumberOfParticipants() +
                            " < " + maxParticipants + ")");
                }
            } else {
                return new ExecutionResponse(false,
                        "Поля не валидны! Музыкальная группа не создана!");
            }
        } catch (Ask.AskBreak e) {
            return new ExecutionResponse(false, "Отмена...");
        }
    }

    private Long maxParticipants() {
        return collectionManager.getCollection()
                .stream()
                .map(MusicBand::getNumberOfParticipants)
                .mapToLong(Long::longValue)
                .max()
                .orElse(-1);
    }
}