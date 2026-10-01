package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DatabaseManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.Status;
import com.itmo.util.request.CombinedRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Обновляет значение элемента коллекции по ID

public class Update extends Command<CombinedRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DatabaseManager databaseManager;

    public Update(CollectionManager<Element> collectionManager, DatabaseManager databaseManager) {
        super(new CommandAttribute(
            "update <идентификатор> {элемент}", 
            "обновить значение элемента коллекции по идентификатору",
            CombinedRequest.class
            ));
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public Response<?> execute(CombinedRequest request) {
        try {
            Integer id = request.getId();
            Element element = request.getElement();
            if (request.getUserId() == null || !(element instanceof com.itmo.models.MusicBand band)) {
                return new Response<>(List.of("Некорректный пользователь или элемент"), Status.ERROR);
            }
            band.setId(id);
            band.setOwnerId(request.getUserId());
            if (!databaseManager.updateMusicBand(band, request.getUserId().intValue())) {
                return new Response<>(List.of("Элемент не найден или не принадлежит пользователю"), Status.ERROR);
            }
            
            if (collectionManager.getById(id) == null) {
                return new Response<>(List.of("Элемент не найден"), Status.ERROR);
            }   

            Status result = collectionManager.updateById(id, element);
            
            if (result == Status.OK) {
                return new Response<>(List.of("Элемент обновлен"));
            } else {
                return new Response<>(List.of("Элемент не найден"), result);
            }
        } catch (NumberFormatException e) {
            return new Response<>(List.of("Некорректный идентификатор"), Status.ERROR);
        } catch (Exception e) {
            return new Response<>(List.of("Не удалось обновить элемент: " + e.getMessage()), Status.ERROR);
        }
    }
}


