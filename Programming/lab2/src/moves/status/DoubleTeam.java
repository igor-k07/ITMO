package moves.status;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class DoubleTeam extends StatusMove {
    public DoubleTeam(){
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon attacker){
        attacker.setMod(Stat.EVASION, 1); // повышает уклончивость на 1 ступень
    }

    @Override
    protected String describe() {
        return "использует Double Team";
    }
}
