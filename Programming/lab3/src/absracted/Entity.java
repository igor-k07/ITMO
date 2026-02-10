package absracted;

import absracted.enums.EntityState;
import exceptions.NotInFlightException;

public abstract class Entity {
    public EntityState state;

    public abstract void act(Environment environment)
            throws NotInFlightException;

}
