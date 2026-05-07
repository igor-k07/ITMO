package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Выводит элемент с максимальным количеством участников

public class MaxByNumberOfParticipants extends Command<StandartRequest> {
    private final CollectionManager<Element> collectionManager;

    public MaxByNumberOfParticipants(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("max_by_number_of_participants", "вывести объект с максимальным количеством участников", StandartRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        Optional<MusicBand> opt = collectionManager.getCollection().stream()
            .map(e -> (MusicBand)e)
            .filter(b -> b.getNumberOfParticipants() != null)
            .max(Comparator.comparingLong(b -> b.getNumberOfParticipants()));

        if (opt.isPresent()) {
            return new Response<>(List.of(opt.get()));
        } else {
            return new Response<>(List.of("Нет элементов с указанным количеством участников"));
        }
    }
}
