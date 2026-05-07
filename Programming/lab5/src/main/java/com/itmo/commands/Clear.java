package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.List;

// Очищает коллекцию

public class Clear extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;

    public Clear(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute(
            "clear", 
            "очистить коллекцию", 
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        collectionManager.clearCollection();
        return new Response<>(List.of("Коллекция очищена"));
    }
}


