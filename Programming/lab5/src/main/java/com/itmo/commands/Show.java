package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.ArrayList;

// Выводит все элементы коллекции

public class Show extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;

    public Show(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute(
            "show", 
            "вывести все элементы коллекции",
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        return new Response<>(new ArrayList<>(collectionManager.getCollection()));
    }
}


