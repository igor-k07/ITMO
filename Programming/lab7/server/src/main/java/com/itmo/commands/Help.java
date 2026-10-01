package com.itmo.commands;

import com.itmo.managers.CommandManager;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.util.List;


// Выводит справку по доступным командам

public class Help extends Command<StandartRequest> {
  private final CommandManager commandManager;

  public Help(CommandManager commandManager) {
    super(new CommandAttribute(
      "help", 
      "вывести справку по доступным командам",
      StandartRequest.class
      ));
    this.commandManager = commandManager;
  }

  public Response<?> execute(StandartRequest request) {
    StringBuilder infoText = new StringBuilder();
    commandManager.getCommands().values().stream()
      .filter(command -> !command.getAttribute().getName().equals("save"))
      .forEach(command -> {
        infoText.append(command.getAttribute().getName() + ": " + command.getAttribute().getDescription() + "\n\n");
      });
    // throw new RuntimeException();
    return new Response<String>(List.of(infoText.toString()));
  }
}
