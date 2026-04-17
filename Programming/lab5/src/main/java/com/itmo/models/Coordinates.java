package models;

import java.util.Objects;
import util.Validatable;


/**
 * Класс координат.
 * x: long
 * y: Long, не может быть null, максимальное значение 996
 */
public class Coordinates implements Validatable {
    private final long x;
    private final Long y;

    public Coordinates(long x, Long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() { return x; }
    public Long getY() { return y; }

    @Override
    public boolean validate() {
        return y != null && y <= 996;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(x=%d, y=%d)", x, y);
    }
}
