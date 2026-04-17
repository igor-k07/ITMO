package util.exceptions;


/**
 * Исключение для ошибок запуска основных модулей
 * @author Septyq
 */
public class RuntimeInitException extends Exception {
    public RuntimeInitException(String message) {
        super(message);
    }
}
