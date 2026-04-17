package commands;

import java.util.List;

import util.transfer.request.standart.StandartRequest;
import util.transfer.response.Response;
import managers.CommandManager;


/**
 * Команда 'help'. Выводит справку по доступным командам.
 * @author Septyq
 */
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
    commandManager.getCommands().values().forEach(command -> {
      infoText.append(command.getAttribute().getName() + ": " + command.getAttribute().getDescription() + "\n\n");
    });
    return new Response<String>(List.of(infoText.toString()));
  }
}