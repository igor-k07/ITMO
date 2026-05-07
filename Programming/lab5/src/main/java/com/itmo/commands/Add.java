package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Добавляет новый элемент в коллекцию
public class Add extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;

    public Add(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute(
            "add {элемент}", 
            "добавить новый элемент в коллекцию", 
            ElementRequest.class
            ));
        this.collectionManager = collectionManager;
    }
    
    public Response<?> execute(ElementRequest request) {
        collectionManager.addToCollection(request.getElement());
        return new Response<>(List.of("Элемент добавлен"));
    }
}


