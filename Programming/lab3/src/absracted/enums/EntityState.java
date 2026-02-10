package absracted.enums;

public enum EntityState {
    GRAVITY("гравитация"),
    ZERO_GRAVITY("невесомость");

    private final String text;

    EntityState(String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }
}
