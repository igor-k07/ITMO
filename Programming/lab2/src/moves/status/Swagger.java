package moves.status;


import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.StatusMove;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Swagger extends StatusMove {
    public Swagger(){
        super(Type.NORMAL, 0, 85);
    }

    @Override
    protected void applyOppEffects(Pokemon target) {
        target.confuse(); // сбивает цель с толку
        target.setMod(Stat.ATTACK, 2); // увеличивает ее урон на 2 ступени
    }


    @Override
    protected String describe() {
        return "использует Swagger";
    }

}
