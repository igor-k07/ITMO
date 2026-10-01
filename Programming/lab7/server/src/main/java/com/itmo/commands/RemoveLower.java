package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DatabaseManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.itmo.util.Status;

// Удаляет из коллекции все элементы, меньшие, чем заданный

public class RemoveLower extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DatabaseManager databaseManager;

    public RemoveLower(CollectionManager<Element> collectionManager, DatabaseManager databaseManager) {
        super(new CommandAttribute(
            "remove_lower {элемент}", 
            "удалить из коллекции все элементы, меньшие, чем заданный",
            ElementRequest.class
            ));
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand target = (MusicBand) request.getElement();
        Set<Element> toRemove = collectionManager.getCollectionCopy().stream()
            .map(e -> (MusicBand) e)
            .filter(b -> request.getUserId().equals(b.getOwnerId()) && b.compareTo(target) < 0)
            .collect(Collectors.toSet());
        try {
            Set<Integer> deletedIds = databaseManager.deleteMusicBands(
                toRemove.stream().map(Element::getId).collect(Collectors.toSet()),
                request.getUserId().intValue());
            toRemove.removeIf(element -> !deletedIds.contains(element.getId()));
            collectionManager.getCollection().removeAll(toRemove);
            return new Response<>(List.of("Удалено элементов: " + deletedIds.size()));
        } catch (Exception e) {
            return new Response<>(List.of("Не удалось удалить элементы: " + e.getMessage()), Status.ERROR);
        }
    }
}


