package moves.status;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

import static ru.ifmo.se.pokemon.Effect.sleep;

public class Sing extends StatusMove {
    public Sing(){
        super(Type.NORMAL,0, 55);
    }

    @Override
    protected void applyOppEffects(Pokemon target){
        sleep(target);
    }

    @Override
    protected String describe() {
        return "использует Sing";
    }
}
