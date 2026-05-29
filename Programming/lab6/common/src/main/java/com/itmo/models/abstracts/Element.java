package com.itmo.models.abstracts;

import com.itmo.util.Validatable;

import java.io.Serializable;
import java.util.Comparator;

// Абстрактный класс элементов коллекции

public abstract class Element implements Validatable, Comparable<Element>, Serializable {
    abstract public int getId();

    public void update(Element newElement) {}

    public abstract void setId(Integer id);
    
    protected Comparator<Element> getComparator() {
        return Comparator.comparingInt(Element::getId);
    }
    public int compareTo(Element other) {
        return getComparator().compare(this, other);
    }
}


