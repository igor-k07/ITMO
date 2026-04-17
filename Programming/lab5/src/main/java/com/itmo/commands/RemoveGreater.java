package commands;

import java.util.Iterator;
import java.util.List;

import managers.CollectionManager;
import models.Entity;
import models.MusicBand;
import util.transfer.request.standart.EntityRequest;
import util.transfer.response.Response;

public class RemoveGreater extends Command<EntityRequest> {
    private final CollectionManager<Entity> collectionManager;

    public RemoveGreater(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный", EntityRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(EntityRequest request) {
        MusicBand target = (MusicBand) request.getEntity();
        Iterator<Entity> iterator = collectionManager.getCollection().iterator();
        int removed = 0;
        while (iterator.hasNext()) {
            MusicBand b = (MusicBand) iterator.next();
            if (b.compareTo(target) > 0) {
                iterator.remove();
                removed++;
            }
        }
        return new Response<>(List.of("Removed " + removed + " elements"));
    }
}
