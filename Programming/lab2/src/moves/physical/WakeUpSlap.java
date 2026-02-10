package moves.physical;

import ru.ifmo.se.pokemon.*;

public class WakeUpSlap extends PhysicalMove {
    public WakeUpSlap(){
        super(Type.FIGHTING, 70, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon target, double damage){
        if (target.getCondition() == Status.SLEEP){
            damage = damage * 2;
            target.setMod(Stat.HP, (int)Math.round(damage));
        }
    }

    @Override
    protected void applyOppEffects(Pokemon target){
        target.setCondition(new Effect().condition(Status.NORMAL));
    }

    @Override
    protected String describe() {
        return "использует Wake-Up Slap";
    }
}
