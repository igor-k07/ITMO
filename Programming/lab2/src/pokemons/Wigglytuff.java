package pokemons;

import moves.status.Sing;

public class Wigglytuff extends Jigglypuff{
    public Wigglytuff(String name, int level){
        super(name, level);
        super.setStats(140, 70, 45, 85, 50, 45);

        Sing sing = new Sing();
        super.addMove(sing);
    }
}
