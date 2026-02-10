package pokemons;

import moves.physical.DizzyPunch;
import moves.physical.ShadowClaw;

import moves.status.Swagger;

import moves.status.TailWhip;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Kangaskhan extends Pokemon{
    public Kangaskhan(String name, int level){
        super(name, level);
        super.setType(Type.NORMAL);
        super.setStats(105, 95, 80, 40, 80, 90);

        DizzyPunch dizzyPunch = new DizzyPunch();
        ShadowClaw shadowClaw = new ShadowClaw();
        Swagger swagger = new Swagger();
        TailWhip thailWhip = new TailWhip();
        super.setMove(dizzyPunch, shadowClaw, swagger, thailWhip);
    }
}
