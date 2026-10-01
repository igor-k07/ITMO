package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DatabaseManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.Status;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Добавляет новый элемент в коллекцию
public class Add extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DatabaseManager databaseManager;

    public Add(CollectionManager<Element> collectionManager, DatabaseManager databaseManager) {
        super(new CommandAttribute(
            "add {элемент}", 
            "добавить новый элемент в коллекцию", 
            ElementRequest.class
            ));
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }
    
    public Response<?> execute(ElementRequest request) {
        if (!(request.getElement() instanceof MusicBand band) || request.getUserId() == null) {
            return new Response<>(List.of("Не удалось определить пользователя или тип элемента"), Status.ERROR);
        }

        try {
            int id = databaseManager.insertMusicBand(band, request.getUserId().intValue());
            band.setId(id);
            band.setOwnerId(request.getUserId());
            Status status = collectionManager.addToCollection(band);
            if (status != Status.OK) {
                return new Response<>(List.of("Объект сохранен в БД, но не добавлен в память"), Status.ERROR);
            }
            return new Response<>(List.of("Элемент добавлен"));
        } catch (Exception e) {
            return new Response<>(List.of("Не удалось добавить элемент: " + e.getMessage()), Status.ERROR);
        }
    }
}


