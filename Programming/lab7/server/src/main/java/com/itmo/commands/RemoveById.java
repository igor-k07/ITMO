package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DatabaseManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.Status;
import com.itmo.util.request.IdRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Удаляет элемент из коллекции по id

public class RemoveById extends Command<IdRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DatabaseManager databaseManager;

    public RemoveById(CollectionManager<Element> collectionManager, DatabaseManager databaseManager) {
        super(new CommandAttribute(
            "remove_by_id <идентификатор>", 
            "удалить элемент из коллекции по идентификатору",
            IdRequest.class
            ));
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public Response<?> execute(IdRequest request) {
        MusicBand bandToRemove = (MusicBand) collectionManager.getById(request.getId());
        if (bandToRemove == null) {
            return new Response<>(List.of("Элемент не найден"), Status.ERROR);
        }
        try {
            if (request.getUserId() == null
                || !databaseManager.deleteMusicBand(request.getId(), request.getUserId().intValue())) {
                return new Response<>(List.of("Элемент не найден или не принадлежит пользователю"), Status.ERROR);
            }
        } catch (Exception e) {
            return new Response<>(List.of("Не удалось удалить элемент: " + e.getMessage()), Status.ERROR);
        }
        collectionManager.removeFromCollection(bandToRemove);
        return new Response<>(List.of("Элемент удален"));
    }
}


