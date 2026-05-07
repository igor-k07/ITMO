package com.itmo.managers.interfaces;

import com.itmo.commands.Command;
import com.itmo.util.transfer.request.Request;

import java.util.List;
import java.util.Map;

// Абстрактный класс для управления списком комманд и историей выполнения

public interface CommandManager {
    void register(String commandName, Command<?> command);
    Map<String, Command<?>> getCommands();
    List<String> getCommandHistory(int number);
    Map<String, Class<? extends Request>> getCommandAttributes();
    void addToHistory(String command);
}


