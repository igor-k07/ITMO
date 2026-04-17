package commands;

import java.util.List;

import managers.CollectionManager;
import models.Entity;
import models.MusicBand;
import util.transfer.request.standart.EntityRequest;
import util.transfer.response.Response;


public class AddIfMax extends Command<EntityRequest> {
    private final CollectionManager<Entity> collectionManager;

    public AddIfMax(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute("add_if_max {element}", "добавить новый элемент, если он больше максимального", EntityRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(EntityRequest request) {
        MusicBand band = (MusicBand) request.getEntity();
        // find current max
        MusicBand max = null;
        for (Entity e : collectionManager.getCollection()) {
            MusicBand b = (MusicBand) e;
            if (max == null || b.compareTo(max) > 0) max = b;
        }

        if (max == null || band.compareTo(max) > 0) {
            collectionManager.addToCollection(band);
            return new Response<>(List.of("element added"));
        }
        return new Response<>(List.of("element not added"));
    }
}
