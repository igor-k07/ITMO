package objects;

import absracted.enums.FlightState;
import exceptions.NoPassangersException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rocket {
    private FlightState state = FlightState.ON_EARTH;
    private final Engine engine = new Engine();
    public List<Character> characters = new ArrayList<>;

    public void startFlight(){
        state = FlightState.IN_FLIGHT;
    }
    public boolean isInFlight(){
        return state == FlightState.IN_FLIGHT;
    }
    public Engine getEngine(){
        return engine;
    }

    public int countPassangers(){
        return characters.size();
    }

    public void addPassanger(Character character){
        characters.add(character);
    }

    public void exitPassanger(Character character) throws NoPassangersException {
        if (characters.size() == 0){
            throw new NoPassangersException();
        }
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Rocket rocket && state == rocket.state;
    }

    @Override
    public int hashCode(){
        return Objects.hash(state);
    }

    @Override
    public String toString(){
        return "ракета " + state;
    }
}
