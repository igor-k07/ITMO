package com.itmo.utility.abstracted;

import com.itmo.utility.abstracted.interfaces.Validatable;

public abstract class Element implements Comparable<Element>, Validatable {
    abstract public int getId();
}
