package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class Exit extends Command {

    public Exit() {
        super("exit", "завершить программу (без сохранений в файл)");
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        return new ExecutionResponse("Завершение выполнения...");
    }
}