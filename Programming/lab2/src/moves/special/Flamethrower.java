package moves.special;

import ru.ifmo.se.pokemon.*;

import static ru.ifmo.se.pokemon.Effect.burn;

public class Flamethrower extends SpecialMove {
    public Flamethrower(){
        super(Type.FIRE, 90, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon target){
        if (Math.random() < 0.1) { // 10% шанс
            burn(target); // сжигает цель
        }
    }

    @Override
    protected String describe() {
        return "использует Flamethrower";
    }
}
