package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.Status;
import com.itmo.util.request.IdRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Удаляет элемент из коллекции по id

public class RemoveById extends Command<IdRequest> {
    private final CollectionManager<Element> collectionManager;

    public RemoveById(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute(
            "remove_by_id <идентификатор>", 
            "удалить элемент из коллекции по идентификатору",
            IdRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(IdRequest request) {
        MusicBand bandToRemove = (MusicBand) collectionManager.getById(request.getId());
        if (bandToRemove == null) {
            return new Response<>(List.of("Элемент не найден"), Status.ERROR);
        }
        collectionManager.removeFromCollection(bandToRemove);
        return new Response<>(List.of("Элемент удален"));
    }
}


