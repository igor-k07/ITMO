package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.managers.CommandManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

import java.util.stream.Collectors;

public class Help extends Command {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        super("help", "вывести справку по коммандам");
        this.commandManager = commandManager;
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        return new ExecutionResponse(commandManager.getCommands()
                .values()
                .stream()
                .map(command -> String.format(" %35s - %s%n",
                        command.getName(),
                        command.getDescription()))
                .collect(Collectors.joining("\n")));
    }
}