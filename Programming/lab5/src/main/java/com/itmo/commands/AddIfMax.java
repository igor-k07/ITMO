package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.List;

// Добавляет новый элемент в коллекцию, если он больше максимального

public class AddIfMax extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;

    public AddIfMax(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("add_if_max {элемент}", "добавить новый элемент, если он больше максимального", ElementRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand band = (MusicBand) request.getElement();
        // find current max
        MusicBand max = null;
        for (Element e : collectionManager.getCollection()) {
            MusicBand b = (MusicBand) e;
            if (max == null || b.compareTo(max) > 0) max = b;
        }

        if (max == null || band.compareTo(max) > 0) {
            collectionManager.addToCollection(band);
            return new Response<>(List.of("Элемент добавлен"));
        }
        return new Response<>(List.of("Элемент не добавлен"));
    }
}
