package objects;
import absracted.enums.Sound;

import java.util.Objects;

public class Engine {
    private final Sound sound = Sound.ENGINE_SOUND;
    public Sound getSound(){
        return sound;
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Engine;
    }

    @Override
    public int hashCode(){
        return Objects.hash("Engine");
    }

    @Override
    public String toString(){
        return "двигатель";
    }
}
