package pokemons;

import moves.status.DoubleTeam;
import moves.status.IronDefense;
import moves.status.Swagger;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Pineco extends Pokemon{
    public Pineco(String name, int level){
        super(name, level);
        super.setType(Type.BUG);
        super.setStats(50, 65, 90, 35, 35, 15);

        Swagger swagger = new Swagger();
        IronDefense ironDefense = new IronDefense();
        DoubleTeam doubleTeam = new DoubleTeam();
        super.setMove(swagger, ironDefense, doubleTeam);
    }
}
