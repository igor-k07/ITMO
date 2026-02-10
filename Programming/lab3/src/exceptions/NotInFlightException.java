package exceptions;

public class NotInFlightException extends Exception{
    @Override
    public String getMessage(){
        return "Невозможно определить полет: ракета еще не стартовала";
    }
}
