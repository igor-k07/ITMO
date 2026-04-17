package commands;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import managers.CollectionManager;
import models.Entity;
import models.MusicBand;
import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;

public class MaxByNumberOfParticipants extends Command<StandartRequest> {
    private final CollectionManager<Entity> collectionManager;

    public MaxByNumberOfParticipants(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute("max_by_number_of_participants", "вывести объект с максимальным numberOfParticipants", StandartRequest.class));
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
            return new Response<>(List.of("No elements with numberOfParticipants"));
        }
    }
}
