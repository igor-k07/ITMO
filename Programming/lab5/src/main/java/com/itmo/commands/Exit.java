package commands;

import util.Status;
import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;


/**
 * Команда 'exit'. Завершиет программу (без сохранения в файл).
 * @author Septyq
 */
public class Exit extends Command<StandartRequest> {
    public Exit() {
        super(new CommandAttribute(
            "exit", 
            "завершить программу (без сохранения в файл)", 
            StandartRequest.class
            ));
    }

    public Response<?> execute(StandartRequest request) {
        return new Response<>(Status.EXIT);
    }
}
