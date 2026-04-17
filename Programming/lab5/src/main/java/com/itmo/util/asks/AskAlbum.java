package com.itmo.util.forms;

import com.itmo.models.Album;
import com.itmo.util.console.IOConsole;
import com.itmo.util.exceptions.InvalidFormException;
import com.itmo.util.exceptions.ScriptSyntaxException;

import java.util.NoSuchElementException;

/**
 * Форма для Album
 */
public class AskAlbum extends Ask<Album> {
    private final IOConsole console;
    private final boolean fileMode;

    public AskAlbum(IOConsole console) {
        this.console = console;
        this.fileMode = console.fileMode();
    }

    @Override
    public Album build() throws InvalidFormException {
        try {
            Album album = new Album(askName(), askTracks(), askLength(), askSales());
            if (!album.validate()) throw new InvalidFormException("Album validation failed");
            return album;
        } catch (ScriptSyntaxException e) {
            throw new InvalidFormException(e.getMessage());
        }
    }

    private String askName() throws ScriptSyntaxException {
        String name = "";
        boolean asked = false;
        do {
            try {
                console.print("Enter album name: ");
                name = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(name);
                if (name.equals("")) throw new ScriptSyntaxException("Invalid album name");
                asked = true;
                break;
            } catch (NoSuchElementException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                } else {
                    console.printError("Album name not recognized, enter it again");
                }
            }
        } while (!asked);
        return name;
    }

    private Long askTracks() throws ScriptSyntaxException {
        boolean asked = false;
        do {
            try {
                console.print("Enter tracks (Long) or empty for null: ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                if (s.isEmpty()) return null;
                Long v = Long.parseLong(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                console.printError("Tracks not recognized, enter it again");
            }
        } while (true);
    }

    private Integer askLength() throws ScriptSyntaxException {
        do {
            try {
                console.print("Enter length (Integer) or empty for null: ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                if (s.isEmpty()) return null;
                Integer v = Integer.parseInt(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                console.printError("Length not recognized, enter it again");
            }
        } while (true);
    }

    private Double askSales() throws ScriptSyntaxException {
        do {
            try {
                console.print("Enter sales (Double): ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                Double v = Double.parseDouble(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                console.printError("Sales not recognized, enter it again");
            }
        } while (true);
    }
}
