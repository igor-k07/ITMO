package com.itmo.models;

import com.itmo.utility.abstracted.interfaces.Validatable;

import java.util.Objects;

public class Album implements Validatable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Long tracks; //Поле может быть null, Значение поля должно быть больше 0
    private Integer length; //Поле может быть null, Значение поля должно быть больше 0
    private Double sales; //Поле не может быть null, Значение поля должно быть больше 0

    public Album(String name, Long tracks, Integer length, Double sales){
        this.name = name;
        this.tracks = tracks;
        this.length = length;
        this.sales = sales;
    }

    public String getName() {
        return name;
    }

    public Long getTracks() {
        return tracks;
    }

    public Integer getLength() {
        return length;
    }

    public Double getSales() {
        return sales;
    }

    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (tracks == null || tracks <= 0) return false;
        if (length == null || length <= 0) return false;
        if (sales == null || sales <= 0) return false;
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Album that = (Album) object;
        return Objects.equals(name, that.name) && Objects.equals(length, that.length);
    }

    @Override
    public String toString() {
        return name;
    }
}
