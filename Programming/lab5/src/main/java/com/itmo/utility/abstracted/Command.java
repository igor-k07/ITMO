package com.itmo.utility.abstracted;

import com.itmo.utility.abstracted.interfaces.Describable;
import com.itmo.utility.abstracted.interfaces.Executable;

public abstract class Command implements Describable, Executable {
    private final String name;
    public final String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Command command = (Command) object;
        return name.equals(command.name) && description.equals(command.description);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return "Command{" + "name='" + name + '\'' +
                ", description='" + description + '\'' + '}';
    }
}
