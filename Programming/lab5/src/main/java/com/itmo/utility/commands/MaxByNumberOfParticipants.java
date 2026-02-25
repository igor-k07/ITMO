package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;

public class MaxByNumberOfParticipants extends Command {
    private final CollectionManager collectionManager;

    public MaxByNumberOfParticipants(CollectionManager collectionManager) {
        super("max_by_number_of_participants",
                "вывести любую музыкальную группу из набора, " +
                "с максимальным количеством участников");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        MusicBand bandWithMaxParticipants = null;
        for (var band : collectionManager.getCollection()){
            if (bandWithMaxParticipants == null ||
                    bandWithMaxParticipants.getNumberOfParticipants() < band.getNumberOfParticipants()) {
                bandWithMaxParticipants = band;
            }
        }
        if (bandWithMaxParticipants == null) {
            return new ExecutionResponse("Музыкальных групп не обнаружено.");
        }
        return new ExecutionResponse(bandWithMaxParticipants.toString());
    }
}