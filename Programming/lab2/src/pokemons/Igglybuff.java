package pokemons;

import moves.special.DreamEater;
import moves.special.Flamethrower;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Igglybuff extends Pokemon {
    public Igglybuff(String name, int level){
        super(name, level);
        super.setType(Type.NORMAL, Type.FAIRY);
        super.setStats(90, 30, 15, 40, 20, 15);

        Flamethrower flamethrower = new Flamethrower();
        DreamEater dreamEater = new DreamEater();
        super.setMove(flamethrower, dreamEater);
    }
}
