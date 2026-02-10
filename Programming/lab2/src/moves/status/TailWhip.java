package moves.status;


import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.StatusMove;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class TailWhip extends StatusMove {
    public TailWhip(){
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon target) {
        target.setMod(Stat.DEFENSE, -1); // снижает защиту цели на 1 ступень
    }


    @Override
    protected String describe() {
        return "использует Tail Whip";
    }

}
