package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Добавляет новый элемент в коллекцию, если он больше максимального

public class AddIfMax extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;

    public AddIfMax(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("add_if_max {элемент}", "добавить новый элемент, если он больше максимального", ElementRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand band = (MusicBand) request.getElement();
        Optional<MusicBand> max = collectionManager.getCollection().stream()
            .map(e -> (MusicBand) e)
            .max(Comparator.naturalOrder());

        if (max.isEmpty() || band.compareTo(max.get()) > 0) {
            collectionManager.addToCollection(band);
            return new Response<>(List.of("Элемент добавлен"));
        }
        return new Response<>(List.of("Элемент не добавлен"));
    }
}
