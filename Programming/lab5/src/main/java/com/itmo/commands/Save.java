package commands;

import managers.CollectionManager;
import managers.FileManager;
import models.Entity;
import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;

import java.util.List;


/**
 * Команда 'save'. Сохраняет коллекцию в файл.
 * @author Septyq
 */
public class Save extends Command<StandartRequest> {
    private final CollectionManager<Entity> collectionManager;
    private final FileManager fileManager;

    public Save(CollectionManager<Entity> collectionManager, FileManager fileManager) {
        super(new CommandAttribute(
            "save", 
            "сохранить коллекцию в файл",
            StandartRequest.class
            ));
        this.collectionManager = collectionManager; 
        this.fileManager = fileManager;
    }
    
    public Response<?> execute(StandartRequest request) {
        collectionManager.saveCollection(fileManager);
        return new Response<>(List.of("collection saved"));
    }
}
