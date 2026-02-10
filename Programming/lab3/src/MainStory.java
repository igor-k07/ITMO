import absracted.Environment;
import absracted.Location;
import absracted.enums.EntityState;
import exceptions.NotInFlightException;
import exceptions.PanicException;
import objects.Rocket;

import java.util.ArrayList;
import java.util.List;


public class MainStory {
    public static void main(String[] args) {
        Rocket rocket = new Rocket();
        Environment environment = new Environment(rocket);
        List<Character> characters = new ArrayList<>();
        Character neznaika = new Character("Незнайка",
                new Location("пищевой отсек"));

        characters.add(neznaika);

        rocket.startFlight();
        neznaika.state = EntityState.ZERO_GRAVITY;

        for (Character character : characters) {
            try {
                character.act(environment);
            } catch (NotInFlightException e) {
                System.out.println(e.getMessage());
            } catch (PanicException e) {
                System.out.println("Непредвиденная ситуация: " + e.getMessage());
            }

        }
    }
}