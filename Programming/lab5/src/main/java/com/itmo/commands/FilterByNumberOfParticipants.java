package com.itmo.commands;

import com.itmo.managers.CollectionManager;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.request.IdRequest;
import com.itmo.util.response.Response;

import java.util.ArrayList;
import java.util.List;

// Выводит элементы с заданным количеством участников

public class FilterByNumberOfParticipants extends Command<IdRequest> {
    private final CollectionManager<Element> collectionManager;

    public FilterByNumberOfParticipants(CollectionManager<Element> collectionManager) {
        super(new CommandAttribute("filter_by_number_of_participants количество_участников", "вывести элементы с заданным количеством участников", IdRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(IdRequest request) {
        long target = request.getId();
        List<MusicBand> out = new ArrayList<>();
        for (Element e : collectionManager.getCollection()) {
            MusicBand b = (MusicBand) e;
            if (b.getNumberOfParticipants() != null && b.getNumberOfParticipants() == target) {
                out.add(b);
            }
        }
        return new Response<>(List.of(out));
    }
}
