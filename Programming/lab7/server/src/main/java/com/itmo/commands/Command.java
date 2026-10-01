package com.itmo.commands;

import com.itmo.commands.interfaces.Executable;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.Objects;

// Абстрактный класс для реализации выполнения команды

public abstract class Command<T extends StandartRequest> implements Executable<T> {
    private final CommandAttribute commandAttribute;

    public Command(CommandAttribute commandAttribute) {
        this.commandAttribute = commandAttribute;
    }

    public CommandAttribute getAttribute() {
        return commandAttribute;
    }

//    public Response<?> execute(T request) {
////        isValid(request);
//        return executeInternal(request);
//    }

//    protected abstract Response<?> isValid(T request);

//    protected abstract Response<?> executeInternal(T request);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command<?> command = (Command<?>) o;
        return Objects.equals(commandAttribute.getName(), command.getAttribute().getName()) 
            && Objects.equals(commandAttribute.getDescription(), command.getAttribute().getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandAttribute.getName(), commandAttribute.getDescription());
    }

    @Override
    public String toString() {
        return "Команда{" +
        "имя='" + commandAttribute.getName() + '\'' +
        ", описание='" + commandAttribute.getDescription() + '\'' +
        '}';
    }

}


