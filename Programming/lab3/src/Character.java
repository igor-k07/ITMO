import absracted.Entity;
import absracted.Environment;
import absracted.Location;
import absracted.enums.Emotion;
import absracted.enums.EntityState;
import absracted.enums.Sound;
import absracted.interfaces.Hearing;
import absracted.interfaces.Memory;
import absracted.interfaces.Feeling;
import exceptions.NotInFlightException;
import exceptions.PanicException;
import objects.Rocket;

import java.util.Objects;
import java.util.Random;

public class Character extends Entity implements Memory, Feeling, Hearing {
    private static final Random RANDOM = new Random();

    private final String name;
    private Emotion emotion = Emotion.CALM;
    private boolean sleeping = true;
    private Location location;

    public Character(String name, Location location){
        super.state = EntityState.GRAVITY;
        this.name = name;
        this.location = location;
    }

    public void sleep(){
        System.out.println(name + " безмятежно спал в " + location.name() + ".");
    }


    public void wakeUp(){
        sleeping = false;
        if (RANDOM.nextInt(10) < 6) {
            emotion = Emotion.CONFUSION;
        }
        System.out.println("Среди ночи " + name + " проснулся.");
    }

    @Override
    public void act(Environment environment) throws NotInFlightException{
        Rocket rocket = environment.getRocket();
        sleep();
        dontFeel(rocket.toString());
        wakeUp();
        if (emotion == Emotion.CONFUSION) {
            dontUnderstand("где находится и ");
            think("Почему я не дома?");
        }

        remember("сам забрался в " + environment.getName());
        if (!rocket.isInFlight()){
            throw new NotInFlightException();
        }
        feel(state);
        hear(environment.getRocket().getEngine().getSound());
        understand(", что " + rocket.toString());
        if (RANDOM.nextInt(10) < 3){
            emotion = Emotion.PANIC;
            throw new PanicException(name + " запаниковал!");
        }
        emotion = Emotion.JOY;
        System.out.println(name + " ощущал " + emotion + ": все получилось точно, как он рассчитал.");
        System.out.println(name + " решил подождать, пока ракета отлетит от Земли подальше.");
    }

    @Override
    public void think(String thought){
        System.out.println(" думает: \"" + thought + "\"");
    }

    @Override
    public void remember(String event){
        System.out.println(name + " вспомнил, что " + event + ".");
    }

    @Override
    public void understand(String event){
        System.out.println("Тогда " + name + " понял" + event + ".");
    }

    @Override
    public void dontUnderstand(String event){
        System.out.print(name + " не понял " + event);
    }

    @Override
    public void dontFeel(String action){
        System.out.println("Даже не чувствовал, что " + action + ".");
    }

    @Override
    public void feel(String action){
        System.out.println("чувствовал, что " + action);
    }

    @Override
    public void dontFeel(EntityState state){
        System.out.println("не чувствовал" + state.toString() + ".");
    }

    @Override
    public void feel(EntityState state){
        System.out.print(name + " почувствовал " + state.toString());
    }

    @Override
    public void hear(Sound sound){
        System.out.println(" и услышал " + sound.toString() + ".");
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Character character && Objects.equals(name, character.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    @Override
    public String toString(){
        return name + "[" + emotion + "]";
    }
}
