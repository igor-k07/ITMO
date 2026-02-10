package exceptions;

public class NoPassangersException extends Exception {
    @Override
    public String getMessage(){
        return "На борту находится 0 человек";
    }

}
