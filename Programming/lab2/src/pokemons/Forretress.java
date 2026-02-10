package pokemons;

import moves.special.MirrorShot;
import ru.ifmo.se.pokemon.Type;

public class Forretress extends Pineco{
    public Forretress(String name, int level){
        super(name, level);
        super.setType(Type.BUG, Type.STEEL);
        super.setStats(75, 90, 140, 60, 60, 40);

        MirrorShot mirrorShot = new MirrorShot();
        super.addMove(mirrorShot);
    }
}
