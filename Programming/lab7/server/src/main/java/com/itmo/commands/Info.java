package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.time.LocalDateTime;


// Выводит информацию о коллекции

public class Info extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;

    public Info(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute(
            "info",
            "вывести информацию о коллекции",
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? "в этой сессии коллекция не инициализировалась" :
            lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();

        LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();
        String lastSaveTimeString = (lastSaveTime == null) ? "в этой сессии сохранение не выполнялось" :
            lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();

        Response<String> response = new Response<>();
  
        response.put("Информация о коллекции:");
        response.put(String.format("Тип коллекции (класс): %s", collectionManager.getCollectionType()));
        response.put(String.format("Размер коллекции: %d", collectionManager.getCollectionSize()));
        response.put(String.format("Последнее сохранение: %s", lastSaveTimeString));
        response.put(String.format("Последняя инициализация: %s", lastInitTimeString));

        return response;
        
    }
}


