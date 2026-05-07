package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DumpManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Сохраняет коллекцию в файл

public class Save extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DumpManager dumpManager;

    public Save(CollectionManager<Element> collectionManager, DumpManager dumpManager) {
        super(new CommandAttribute(
            "save", 
            "сохранить коллекцию в файл",
            StandartRequest.class
            ));
        this.collectionManager = collectionManager; 
        this.dumpManager = dumpManager;
    }
    
    public Response<?> execute(StandartRequest request) {
        collectionManager.saveCollection(dumpManager);
        return new Response<>(List.of("Коллекция сохранена"));
    }
}


