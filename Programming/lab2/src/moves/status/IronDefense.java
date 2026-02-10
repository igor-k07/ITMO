package moves.status;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class IronDefense extends StatusMove {
    public IronDefense(){
        super(Type.NORMAL, 20, 100);
    }

    @Override
    protected void applySelfEffects(Pokemon attacker){
        attacker.setMod(Stat.DEFENSE, 2); // повышает защиту цели на 2 ступени
    }

    @Override
    protected String describe() {
        return "использует Iron Defense";
    }
}
