package absracted.enums;

public enum Emotion {
    CALM("спокойствие"),
    CONFUSION("растерянность"),
    JOY("радость"),
    PANIC("паника");

    private final String text;

    Emotion(String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }
}
