package moves.special;

import ru.ifmo.se.pokemon.*;

public class DreamEater extends SpecialMove {
    public DreamEater(){
        super(Type.PSYCHIC, 100, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon target, double damage){
        if (target.getCondition() == Status.SLEEP){
            target.setMod(Stat.HP, (int)Math.round(damage));
        }
    }

    @Override
    protected String describe() {
        return "использует Dream Eater";
    }
}

