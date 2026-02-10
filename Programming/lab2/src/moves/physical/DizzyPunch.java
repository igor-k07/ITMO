package moves.physical;


import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class DizzyPunch extends PhysicalMove {
    public DizzyPunch(){
        super(Type.NORMAL, 70, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon target) {
        if (Math.random() < 0.2) { // 20% шанс
            target.confuse(); // сбивает цель с толку
        }
    }

    @Override
    protected String describe() {
        return "использует Dizzy Punch";
    }

}
