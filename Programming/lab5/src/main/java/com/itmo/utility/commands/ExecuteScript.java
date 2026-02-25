package com.itmo.utility.commands;


import com.itmo.managers.CollectionManager;
import com.itmo.utility.ExecutionResponse;
import com.itmo.utility.abstracted.Command;
import com.itmo.utility.abstracted.interfaces.Console;

public class ExecuteScript extends Command {

    public ExecuteScript() {
        super("execute_script <file_name>", "выполнить скрипт из указанного файла");
    }

    @Override
    public ExecutionResponse apply(String[] arguments) {
        if (arguments[1].isEmpty() | arguments.length > 2) {
            return new ExecutionResponse(false,
                    "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        return new ExecutionResponse("Выполнение скрипта '" + arguments[1] + "'...");
    }
}