package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.DatabaseManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.ElementRequest;
import com.itmo.util.response.Response;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.itmo.util.Status;

// Добавляет новый элемент в коллекцию, если он больше максимального

public class AddIfMax extends Command<ElementRequest> {
    private final CollectionManager<Element> collectionManager;
    private final DatabaseManager databaseManager;

    public AddIfMax(CollectionManager<Element> collectionManager, DatabaseManager databaseManager) {
        super(new CommandAttribute("add_if_max {элемент}", "добавить новый элемент, если он больше максимального", ElementRequest.class));
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public Response<?> execute(ElementRequest request) {
        MusicBand band = (MusicBand) request.getElement();
        Optional<MusicBand> max = collectionManager.getCollectionCopy().stream()
            .map(e -> (MusicBand) e)
            .max(Comparator.naturalOrder());

        if (max.isEmpty() || band.compareTo(max.get()) > 0) {
            if (request.getUserId() == null) {
                return new Response<>(List.of("Не удалось определить пользователя"), Status.ERROR);
            }
            try {
                int id = databaseManager.insertMusicBand(band, request.getUserId().intValue());
                band.setId(id);
                band.setOwnerId(request.getUserId());
                collectionManager.addToCollection(band);
                return new Response<>(List.of("Элемент добавлен"));
            } catch (Exception e) {
                return new Response<>(List.of("Не удалось добавить элемент: " + e.getMessage()), Status.ERROR);
            }
        }
        return new Response<>(List.of("Элемент не добавлен"));
    }
}
