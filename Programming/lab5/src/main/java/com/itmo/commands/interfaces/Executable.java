package commands.interfaces;

import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;


/**
 * Определяет классы с возможностью запуска.
 * @author Septyq
 */
public interface Executable<T extends StandartRequest> {
    Response<?> execute(T request);
}
