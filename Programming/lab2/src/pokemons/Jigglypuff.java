package pokemons;

import moves.physical.WakeUpSlap;

public class Jigglypuff extends Igglybuff{
    public Jigglypuff(String name, int level){
        super(name, level);
        super.setStats(115, 45, 20, 45, 25, 20);

        WakeUpSlap wakeUpSlap = new WakeUpSlap();
        super.addMove(wakeUpSlap);
    }

}
