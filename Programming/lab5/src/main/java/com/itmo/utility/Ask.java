package com.itmo.utility;

import com.itmo.managers.CollectionManager;
import com.itmo.utility.abstracted.interfaces.Console;
import com.itmo.models.Album;
import com.itmo.models.Coordinates;
import com.itmo.models.MusicBand;
import com.itmo.models.enums.MusicGenre;

import java.util.NoSuchElementException;

public class Ask {
    public static class AskBreak extends Exception{}

    public static MusicBand askMusicBand(Console console, int id) throws AskBreak{
        try {
            String name;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("band name: ");
                }
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) break;
            }

            var coordinates = askCoordinates(console);

            Long numberOfParticipants;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("number of prticipants: ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                try {
                    if (line.isEmpty()) {
                        numberOfParticipants = null;
                        break;
                    }
                    numberOfParticipants = Long.parseLong(line);
                    if (numberOfParticipants > 0) break;
                } catch (NumberFormatException e) {}
            }

            Long albumsCount;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("albums count: ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        albumsCount = Long.parseLong(line);
                        if (albumsCount > 0) break;
                    } catch (NumberFormatException e) {}
                }
            }

            var genre = askGenre(console);
            var bestAlbum = askAlbum(console);
            return new MusicBand(id, name, coordinates, numberOfParticipants, albumsCount, genre, bestAlbum);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static MusicBand askId(Console console,
                                  CollectionManager collectionManager) throws AskBreak {
        try {
            int id;
            MusicBand band;
            while (true) {
                if (!console.isFileScanner()) {
                    console.print("Введите id группы c нужным значением: ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        id = Integer.parseInt(line);
                        if (id > 0 && collectionManager.byId(id) != null) {
                            break;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            band = collectionManager.byId(id);
            return band;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Coordinates askCoordinates(Console console) throws AskBreak {
        try {
            long x;
            while (true) {
                if (!console.isFileScanner()) {
                    console.print("coordinates.x: ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        x = Long.parseLong(line);
                        break;
                    } catch (NumberFormatException e) {}
                }
            }
            long y;
            while (true) {
                if (!console.isFileScanner()) {
                    console.print("coordinates.y: ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        y = Long.parseLong(line);
                        if (y <= 996) break;
                    } catch (NumberFormatException e) {}
                }
            }
            return new Coordinates(x, y);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static MusicGenre askGenre(Console console) throws AskBreak {
        try {
            MusicGenre genre;
            while (true) {
                if (!console.isFileScanner()) {
                    console.print("music genre (" + MusicGenre.names() + "): ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        genre = MusicGenre.valueOf(line);
                        break;
                    } catch (NullPointerException | IllegalArgumentException e) { }
                } else return null;
            }
            return genre;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Album askAlbum(Console console) throws AskBreak {
        try {
            String name;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("The best album name: ");
                }
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) break;
            }

            long tracks;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("number of tracks in album: ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        tracks = Long.parseLong(line);
                        if (tracks > 0) break;
                    } catch (NumberFormatException e) {}
                }
            }

            int lenght;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("album lenght(sec.): ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        lenght = Integer.parseInt(line);
                        if (lenght > 0) break;
                    } catch (NumberFormatException e) {}
                }
            }

            double sales;
            while (true){
                if (!console.isFileScanner()) {
                    console.print("album sales(copies): ");
                }
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        sales = Double.parseDouble(line);
                        if (sales > 0) break;
                    } catch (NumberFormatException e) {}
                }
            }
            return new Album(name, tracks, lenght, sales);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }
}
