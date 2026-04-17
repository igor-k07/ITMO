package com.itmo.util.forms;

import com.itmo.models.Coordinates;
import com.itmo.util.console.IOConsole;
import com.itmo.util.exceptions.InvalidFormException;
import com.itmo.util.exceptions.ScriptSyntaxException;

import java.util.NoSuchElementException;

/**
 * Класс формы для координат.
 * @author Septyq
 */
public class AskCoordinates extends Ask<Coordinates> {
    private final IOConsole console;
    private final boolean fileMode;

    public AskCoordinates(IOConsole console) {
        this.console = console;
        this.fileMode = console.fileMode();
    }

    public Coordinates build() throws InvalidFormException {
        try {
            Coordinates coordinates = new Coordinates(askX(), askY());
            if (!coordinates.validate()) throw new InvalidFormException("Coordinates validation failed");
            return coordinates;   
        } catch (Exception e) {
            throw new InvalidFormException(e.getMessage());
        }
    }

    private long askX() throws ScriptSyntaxException {
        long x = 0L;
        boolean asked = false;
        do {
            try {
                console.print("Enter coordinate X (long): ");
                String strX = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strX);
                x = Long.parseLong(strX);
                asked = true;
                break;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                } else {
                    console.printError("Coordinate X was not recognized, enter it again");
                }
            }
        } while (!asked);

        return x;
    }

    private Long askY() throws ScriptSyntaxException {
        Long y = Long.valueOf(0);
        boolean asked = false;
        do {
            try {
                console.print("Enter coordinate Y (Long): ");
                String strY = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strY);
                if (strY == "") {return null;}
                y = Long.parseLong(strY);
                asked = true;
                break;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                } else {
                    console.printError("Coordinate Y was not recognized, enter it again");
                }
            }
        } while (!asked);
    
        return y;
    }
    
}
