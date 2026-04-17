package commands;

import java.util.ArrayList;
import java.util.List;

import managers.CollectionManager;
import models.Entity;
import models.MusicBand;
import util.transfer.request.standart.IdRequest;
import util.transfer.response.Response;

public class FilterByNumberOfParticipants extends Command<IdRequest> {
    private final CollectionManager<Entity> collectionManager;

    public FilterByNumberOfParticipants(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute("filter_by_number_of_participants numberOfParticipants", "вывести элементы с заданным numberOfParticipants", IdRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(IdRequest request) {
        long target = request.getId();
        List<MusicBand> out = new ArrayList<>();
        for (Entity e : collectionManager.getCollection()) {
            MusicBand b = (MusicBand) e;
            if (b.getNumberOfParticipants() != null && b.getNumberOfParticipants() == target) {
                out.add(b);
            }
        }
        return new Response<>(List.of(out));
    }
}
