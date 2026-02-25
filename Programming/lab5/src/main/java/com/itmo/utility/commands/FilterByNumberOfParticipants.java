package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

import java.util.List;
import java.util.stream.Collectors;

public class FilterByNumberOfParticipants extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public FilterByNumberOfParticipants(Console console, CollectionManager collectionManager) {
        super("filter_by_number_of_participants <number_of_participants>",
                "вывести группы в которых число участников соответствует заданному");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        try {
            if (arguments[1].isEmpty() | arguments.length > 2) {
                return new ExecutionResponse(false,
                        "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
            }

            Long number = Long.parseLong(arguments[1].trim());
            var bands = filterByNumberOfParticipants(number);
            if (bands.isEmpty()) {
                return new ExecutionResponse(false, "Нет музыкальных групп с таким числом участников");
            }
            StringBuilder s = new StringBuilder();
            console.println("Найдено " + bands.size() +
                    " музыкальных групп с количеством участников: " + number);
            for (var band: bands) {
                s.append(band).append("\n");
            }
            return new ExecutionResponse(s.toString());

        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "Введено некорректное число");
        }
    }

    private List<MusicBand> filterByNumberOfParticipants(Long number) {
        return collectionManager.getCollection()
                .stream()
                .filter(musicBand -> (musicBand.getNumberOfParticipants() == number))
                .collect(Collectors.toList());
    }
}