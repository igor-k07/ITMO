package com.itmo.models;

import com.itmo.util.Validatable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


// Модель для координат

public class Coordinates implements Validatable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long x;
    private final Long y; // Максимальное значение поля: 996, Поле не может быть null

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
        return String.format("(координата X=%d, координата Y=%d)", x, y);
    }
}


