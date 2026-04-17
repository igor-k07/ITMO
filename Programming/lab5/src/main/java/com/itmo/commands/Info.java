package commands;

import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;

import java.time.LocalDateTime;

import managers.CollectionManager;
import models.Entity;


/**
 * Команда 'info'. Выводит информацию о коллекции.
 * @author Septyq
 */
public class Info extends Command<StandartRequest> {
    private final CollectionManager<Entity> collectionManager;

    public Info(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute(
            "info",
            "вывести информацию о коллекции",
            StandartRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? "no collection init (load) occured in current session" :
            lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();

        LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();
        String lastSaveTimeString = (lastSaveTime == null) ? "no save occured in current session" :
            lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();

        Response<String> response = new Response<>();
  
        response.put("Collection info:");
        response.put(String.format("Collection Type (Class): %s", collectionManager.getCollectionType()));
        response.put(String.format("Collection size: %d", collectionManager.getCollectionSize()));
        response.put(String.format("Last saved: %s", lastSaveTimeString));
        response.put(String.format("Last initialized: %s", lastInitTimeString));

        return response;
        
    }
}
