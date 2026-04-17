package com.itmo.util.forms;

import com.itmo.models.Album;
import com.itmo.models.Coordinates;
import com.itmo.models.MusicBand;
import com.itmo.models.MusicGenre;
import com.itmo.util.console.IOConsole;
import com.itmo.util.exceptions.InvalidFormException;
import com.itmo.util.exceptions.ScriptSyntaxException;

import java.util.NoSuchElementException;

/**
 * Форма для создания MusicBand через консоль.
 */
public class MusicBandForm extends Form<MusicBand> {
    private final IOConsole console;
    private final boolean fileMode;

    public MusicBandForm(IOConsole console) {
        this.console = console;
        this.fileMode = console.fileMode();
    }

    @Override
    public MusicBand build() {
        try {
            String name = askName();
            Coordinates coordinates = new AskCoordinates(console).build();
            Long numberOfParticipants = askNumberOfParticipants();
            Long albumsCount = askAlbumsCount();
            MusicGenre genre = askGenre();
            Album bestAlbum = new AskAlbum(console).build();

            MusicBand band = new MusicBand(name, coordinates, numberOfParticipants, albumsCount, genre, bestAlbum);
            if (!band.validate()) throw new InvalidFormException("Final MusicBand validation failed");
            return band;
        } catch (InvalidFormException | ScriptSyntaxException e) {
            console.printError(e.getMessage());
            return null;
        }
    }

    private String askName() throws ScriptSyntaxException {
        String name = "";
        boolean asked = false;
        do {
            try {
                console.print("Enter band name: ");
                name = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(name);
                if (name.equals("")) throw new ScriptSyntaxException("Invalid name");
                asked = true;
                break;
            } catch (NoSuchElementException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                } else {
                    console.printError("Name not recognized, enter it again");
                }
            }
        } while (!asked);

        return name;
    }

    private Long askNumberOfParticipants() throws ScriptSyntaxException {
        do {
            try {
                console.print("Enter numberOfParticipants (Long) or empty for null: ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                if (s.isEmpty()) return null;
                return Long.parseLong(s);
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                console.printError("Value not recognized, enter it again");
            }
        } while (true);
    }

    private Long askAlbumsCount() throws ScriptSyntaxException {
        do {
            try {
                console.print("Enter albumsCount (Long): ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                Long v = Long.parseLong(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                console.printError("albumsCount not recognized, enter it again");
            }
        } while (true);
    }

    private MusicGenre askGenre() throws ScriptSyntaxException {
        do {
            try {
                console.print("Enter genre (one of: ");
                for (MusicGenre g : MusicGenre.values()) {
                    console.print(g.name() + " ");
                }
                console.println("");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                return MusicGenre.valueOf(s);
            } catch (IllegalArgumentException | NoSuchElementException e) {
                if (fileMode) throw new ScriptSyntaxException("Invalid input data in script -> operation stopped");
                console.printError("Genre not recognized, enter it again");
            }
        } while (true);
    }
}
