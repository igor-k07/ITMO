package util.exceptions;


/**
 * Исключение для ошибок вызова команд
 * @author Septyq
 */
public class IncorrectRequestException extends Exception {
    public IncorrectRequestException(String message) {
        super(message);
    }
}
