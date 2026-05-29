package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Удаляет из коллекции все элементы, превышающие заданный

public class RemoveGreater extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;

    public RemoveGreater(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный", ElementRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand target = (MusicBand) request.getElement();
        Set<Element> toRemove = collectionManager.getCollection().stream()
            .map(e -> (MusicBand) e)
            .filter(b -> b.compareTo(target) > 0)
            .collect(Collectors.toSet());
        int removed = toRemove.size();
        collectionManager.getCollection().removeAll(toRemove);
        return new Response<>(List.of("Удалено элементов: " + removed));
    }
}
