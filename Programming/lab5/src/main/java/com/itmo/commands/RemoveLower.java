package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.Iterator;
import java.util.List;

// Удаляет из коллекции все элементы, меньшие, чем заданный

public class RemoveLower extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;

    public RemoveLower(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute(
            "remove_lower {элемент}", 
            "удалить из коллекции все элементы, меньшие, чем заданный",
            ElementRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand target = (MusicBand) request.getElement();
        
        Iterator<Element> iterator = collectionManager.getCollection().iterator();
        int removedCount = 0;
        
        while (iterator.hasNext()) {
            MusicBand band = (MusicBand) iterator.next();
            if (band.compareTo(target) < 0) {
                iterator.remove();
                removedCount++;
            }
        }
        
        return new Response<>(List.of("Удалено элементов: " + removedCount));
    }
}


