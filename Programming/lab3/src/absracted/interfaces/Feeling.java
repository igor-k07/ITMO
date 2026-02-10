package absracted.interfaces;

import absracted.enums.EntityState;
import absracted.enums.FlightState;

public interface Feeling {
    void feel(EntityState state);
    void feel(String action);

    void dontFeel(EntityState state);
    void dontFeel(String action);
}
