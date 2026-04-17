package commands;

import java.util.ArrayList;

import managers.CollectionManager;
import models.Entity;
import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;


/**
 * Команда 'show'. Выводит все элементы коллекции.
 * @author Septyq
 */
public class Show extends Command<StandartRequest> {
    private final CollectionManager<Entity>collectionManager;

    public Show(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute(
            "show", 
            "вывести все элементы коллекции",
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        return new Response<>(new ArrayList<>(collectionManager.getCollection()));
    }
}
