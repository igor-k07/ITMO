package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.IdRequest;
import com.itmo.util.response.Response;

import java.util.List;
import java.util.stream.Collectors;

// Выводит элементы с заданным количеством участников

public class FilterByNumberOfParticipants extends Command<IdRequest> {
    private final CollectionManager<Element> collectionManager;

    public FilterByNumberOfParticipants(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("filter_by_number_of_participants количество_участников", "вывести элементы с заданным количеством участников", IdRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(IdRequest request) {
        long target = request.getId();
        List<MusicBand> out = collectionManager.getCollection().stream()
            .map(e -> (MusicBand) e)
            .filter(b -> b.getNumberOfParticipants() != null && b.getNumberOfParticipants() == target)
            .sorted()
            .collect(Collectors.toList());
        return new Response<>(out);
    }
}
