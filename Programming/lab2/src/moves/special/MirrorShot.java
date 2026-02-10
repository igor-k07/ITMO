package moves.special;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.Type;

public class MirrorShot extends SpecialMove {
    public MirrorShot(){
        super(Type.STEEL, 65, 85);
    }

    @Override
    protected void applyOppEffects(Pokemon target){
        if (Math.random() < 0.3) { // 30% шанс
            target.setMod(Stat.ACCURACY, -1); // снижает точность цели на 1 ступень
        }
    }

    @Override
    protected String describe() {
        return "использует Mirror Shot";
    }
}
