import pokemons.*;
import ru.ifmo.se.pokemon.*;

public class TestBattle {
    public static void main(String[] args) {
        Battle b = new Battle();

        Forretress p1 = new Forretress("форетрес", 1);
        Igglybuff p2 = new Igglybuff("иглибаф", 1);
        Jigglypuff p3 = new Jigglypuff("джиглипаф", 1);
        Kangaskhan p4 = new Kangaskhan("кангасхан", 1);
        Pineco p5 = new Pineco("пинеко", 1);
        Wigglytuff p6 = new Wigglytuff("виглитаф", 1);

        b.addAlly(p1);
        b.addAlly(p2);
        b.addAlly(p3);

        b.addFoe(p4);
        b.addFoe(p5);
        b.addFoe(p6);

        b.go();
    }
}
