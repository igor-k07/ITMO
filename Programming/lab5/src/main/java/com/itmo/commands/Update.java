package commands;

import java.util.List;

import managers.CollectionManager;
import models.Entity;
import util.Status;
import util.transfer.request.standart.CombinedRequest;
import util.transfer.response.Response;


/**
 * Команда 'update'. Обновляет значение элемента коллекции по ID.
 * @author Septyq
 */
public class Update extends Command<CombinedRequest> {
    private final CollectionManager<Entity> collectionManager;

    public Update(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute(
            "update <ID> {element}", 
            "обновить значение элемента коллекции по ID",
            CombinedRequest.class
            ));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(CombinedRequest request) {
        try {
            Integer id = request.getId();
            Entity entity = request.getEntity();
            
            if (collectionManager.getById(id) == null) {
                return new Response<>(List.of("Item not found"), Status.ERROR);
            }   

            Status result = collectionManager.updateById(id, entity);
            
            if (result == Status.OK) {
                return new Response<>(List.of("element updated"));
            } else {
                return new Response<>(List.of("Item not found"), result);
            }
        } catch (NumberFormatException e) {
            return new Response<>(List.of("Invalid id"), Status.ERROR);
        }
    }
}
