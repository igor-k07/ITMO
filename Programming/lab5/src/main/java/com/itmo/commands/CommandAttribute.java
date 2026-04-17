package commands;

import commands.interfaces.Describable;
import util.transfer.request.Request;


/**
 * Класс для хранения атрибутов команды
 * @author Septyq
 */
public class CommandAttribute implements Describable {
    private final String name;
    private final String description;
    private final Class<? extends Request> argsType;

    public CommandAttribute(String name, String description, Class<? extends Request> argsType) {
        this.name = name;
        this.description = description;
        this.argsType = argsType;
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Request> getArgsType() {
        return argsType;
    }
}
