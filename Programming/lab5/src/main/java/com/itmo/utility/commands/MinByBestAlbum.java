package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class MinByBestAlbum extends Command {
    private final CollectionManager collectionManager;

    public MinByBestAlbum(CollectionManager collectionManager) {
        super("min_by_best_album",
                "вывести любую музыкальную группу из набора, " +
                "с самым коротким лучшим альбомом");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        MusicBand bandWithMinAlbum = null;
        for (var band : collectionManager.getCollection()){
            if (bandWithMinAlbum == null ||
                    bandWithMinAlbum.getBestAlbum().getLength() > band.getBestAlbum().getLength()) {
                bandWithMinAlbum = band;
            }
        }
        if (bandWithMinAlbum == null) {
            return new ExecutionResponse("Музыкальных групп не обнаружено.");
        }
        return new ExecutionResponse(bandWithMinAlbum.toString());
    }
}