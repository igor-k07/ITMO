package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.Iterator;
import java.util.List;

// Удаляет из коллекции все элементы, превышающие заданный

public class RemoveGreater extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;

    public RemoveGreater(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный", ElementRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand target = (MusicBand) request.getElement();
        Iterator<Element> iterator = collectionManager.getCollection().iterator();
        int removed = 0;
        while (iterator.hasNext()) {
            MusicBand b = (MusicBand) iterator.next();
            if (b.compareTo(target) > 0) {
                iterator.remove();
                removed++;
            }
        }
        return new Response<>(List.of("Удалено элементов: " + removed));
    }
}
