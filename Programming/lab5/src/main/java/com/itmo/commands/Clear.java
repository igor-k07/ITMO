package commands;

import managers.CollectionManager;
import models.Entity;

import java.util.List;

import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;

/**
 * Команда 'clear'. Очищает коллекцию.
 * @author Septyq
 */
public class Clear extends Command<StandartRequest> {
    private final CollectionManager<Entity> collectionManager;

    public Clear(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute(
            "clear", 
            "очистить коллекцию", 
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        collectionManager.clearCollection();
        return new Response<>(List.of("collection cleared"));
    }
}
