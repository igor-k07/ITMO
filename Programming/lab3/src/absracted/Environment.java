package absracted;

import objects.Rocket;

public class Environment {
    private final Rocket rocket;
    private String name;

    public Environment(Rocket rocket){
        this.rocket = rocket;
        this.name = "ракета";
    }

    public Rocket getRocket(){
        return rocket;
    }

    public String getName(){
        return name;
    }
}
