package commands;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import managers.CollectionManager;
import models.Entity;
import models.MusicBand;
import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;

public class MinByBestAlbum extends Command<StandartRequest> {
    private final CollectionManager<Entity> collectionManager;

    public MinByBestAlbum(CollectionManager<Entity> collectionManager) {
        super(new CommandAttribute("min_by_best_album", "вывести любой объект с минимальным bestAlbum", StandartRequest.class));
        this.collectionManager = collectionManager;
    }

    public Response<?> execute(StandartRequest request) {
        Optional<MusicBand> opt = collectionManager.getCollection().stream()
            .map(e -> (MusicBand)e)
            .filter(b -> b.getBestAlbum() != null && b.getBestAlbum().getSales() != null)
            .min(Comparator.comparingDouble(b -> b.getBestAlbum().getSales()));

        if (opt.isPresent()) {
            return new Response<>(List.of(opt.get()));
        } else {
            return new Response<>(List.of("Collection is empty or no album data"));
        }
    }
}
