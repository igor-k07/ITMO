package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Выводит элемент с минимальными продажами лучшего альбома

public class MinByBestAlbum extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;

    public MinByBestAlbum(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("min_by_best_album", "вывести любой объект с минимальными продажами лучшего альбома", StandartRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        Optional<MusicBand> opt = collectionManager.getCollection().stream()
            .map(e -> (MusicBand)e)
            .filter(b -> b.getBestAlbum() != null && b.getBestAlbum().getSales() != null)
            .min(Comparator.comparingDouble(b -> b.getBestAlbum().getSales()));

        if (opt.isPresent()) {
            return new Response<>(List.of(opt.get()));
        } else {
            return new Response<>(List.of("Коллекция пуста или отсутствуют данные об альбоме"));
        }
    }
}
