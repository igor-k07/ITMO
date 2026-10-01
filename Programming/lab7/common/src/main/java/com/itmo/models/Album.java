package com.itmo.models;

import com.itmo.util.Validatable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

// Модель для альбома музыкальной группы

public class Album implements Validatable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Long tracks; // Поле может быть null, Значение поля должно быть больше 0
    private Integer length; // Поле может быть null, Значение поля должно быть больше 0
    private Double sales; // Поле не может быть null, Значение поля должно быть больше 0

    public Album() {}

    public Album(String name, Long tracks, Integer length, Double sales) {
        this.name = name;
        this.tracks = tracks;
        this.length = length;
        this.sales = sales;
    }

    public String getName() { return name; }
    public Long getTracks() { return tracks; }
    public Integer getLength() { return length; }
    public Double getSales() { return sales; }

    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (sales == null || sales <= 0) return false;
        if (tracks != null && tracks <= 0) return false;
        if (length != null && length <= 0) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(name, album.name) && Objects.equals(tracks, album.tracks) && Objects.equals(length, album.length) && Objects.equals(sales, album.sales);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tracks, length, sales);
    }

    @Override
    public String toString() {
        return String.format("Альбом{название='%s', треки=%s, длительность=%s, продажи=%s}", name, tracks, length, sales);
    }
}


