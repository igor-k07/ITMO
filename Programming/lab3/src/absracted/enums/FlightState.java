package absracted.enums;

public enum FlightState {
    ON_EARTH("на Земле"),
    IN_FLIGHT("в полете");

    private final String text;

    FlightState(String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }
}
