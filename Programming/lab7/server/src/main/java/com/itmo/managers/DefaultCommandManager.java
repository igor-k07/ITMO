package com.itmo.managers;

import com.itmo.commands.Command;
import com.itmo.managers.interfaces.CommandManager;
import com.itmo.util.request.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Менеджер для работы со списком комманд и историей выполнения

public class DefaultCommandManager implements CommandManager {
    private final Map<String, Command<?>> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    public void register(String commandName, Command<?> command) {
        commands.put(commandName, command);
    }

    public Map<String, Command<?>> getCommands() {
        return commands;
    }

    public List<String> getCommandHistory(int number) {
        return commandHistory.subList(0, Math.min(number, commandHistory.size()));
    }

    public Map<String, Class<? extends Request>> getCommandAttributes() {
        Map<String, Class<? extends Request>> commandAttributes = new HashMap<>();
        commands.forEach((name, command) -> {
            commandAttributes.put(name, command.getAttribute().getArgsType());
        });
        return commandAttributes;
    };

    public void addToHistory(String command) {
        commandHistory.add(command);
    }
}


