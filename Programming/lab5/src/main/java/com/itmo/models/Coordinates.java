package com.itmo.models;

import com.itmo.utility.abstracted.interfaces.Validatable;

import java.util.Objects;

public class Coordinates implements Validatable {
    private long x;
    private Long y; //Максимальное значение поля: 996, Поле не может быть null

    public Coordinates(long x, Long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    @Override
    public boolean validate(){
        if (y == null || y > 996) return false;
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Coordinates that = (Coordinates) object;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
