package commands;

import java.util.List;

import managers.CollectionManager;
import models.Entity;
import models.MusicBand;
import util.Status;
import util.transfer.request.standart.IdRequest;
import util.transfer.response.Response;


/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по ID.
 * @author Septyq
 */
public class RemoveById extends Command<IdRequest> {
    private final CollectionManager<Entity> collectionManager;

    public RemoveById(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute(
            "remove_by_id <ID>", 
            "удалить элемент из коллекции по ID",
            IdRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(IdRequest request) {
        MusicBand bandToRemove = (MusicBand) collectionManager.getById(request.getId());
        if (bandToRemove == null) {
            return new Response<>(List.of("Item not found"), Status.ERROR);
        }
        collectionManager.removeFromCollection(bandToRemove);
        return new Response<>(List.of("element removed"));
    }
}
