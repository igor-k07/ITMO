package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DatabaseManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.List;

// Очищает коллекцию

public class Clear extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DatabaseManager databaseManager;

    public Clear(CollectionManager<Element> collectionManager, DatabaseManager databaseManager) {
        super(new CommandAttribute(
            "clear", 
            "очистить коллекцию", 
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public Response<?> execute(StandartRequest request) {
        try {
            if (request.getUserId() == null) {
                return new Response<>(List.of("Не удалось определить пользователя"), Status.ERROR);
            }
            databaseManager.clearOwned(request.getUserId().intValue());
            collectionManager.clearByOwner(request.getUserId());
            return new Response<>(List.of("Коллекция очищена"));
        } catch (Exception e) {
            return new Response<>(List.of("Не удалось очистить коллекцию: " + e.getMessage()), Status.ERROR);
        }
    }
}


