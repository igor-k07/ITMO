package com.itmo.util.asks;

import com.itmo.models.Album;
import com.itmo.models.Coordinates;
import com.itmo.models.MusicBand;
import com.itmo.models.enums.MusicGenre;
import com.itmo.util.console.StandardConsole;
import com.itmo.util.exceptions.InvalidAskException;
import com.itmo.util.exceptions.ScriptSyntaxException;

import java.util.NoSuchElementException;

// Запрос на ввод данных для создания объекта MusicBand

public class AskMusicBand extends Ask<MusicBand> {
    private final StandardConsole console;
    private final boolean fileMode;

    public AskMusicBand(StandardConsole console) {
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
            if (!band.validate()) throw new InvalidAskException("Финальная валидация музыкальной группы не пройдена");
            return band;
        } catch (InvalidAskException | ScriptSyntaxException e) {
            console.printError(e.getMessage());
            return null;
        }
    }

    private String askName() throws ScriptSyntaxException {
        String name = "";
        boolean asked = false;
        do {
            try {
                console.print("Введите название группы: ");
                name = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(name);
                if (name.equals("")) throw new ScriptSyntaxException("Некорректное название");
                asked = true;
                break;
            } catch (NoSuchElementException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                } else {
                    console.printError("Название не распознано, введите еще раз");
                }
            }
        } while (!asked);

        return name;
    }

    private Long askNumberOfParticipants() throws ScriptSyntaxException {
        do {
            try {
                console.print("Введите количество участников (Long) или оставьте пустым: ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                if (s.isEmpty()) return null;
                return Long.parseLong(s);
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                console.printError("Значение не распознано, введите еще раз");
            }
        } while (true);
    }

    private Long askAlbumsCount() throws ScriptSyntaxException {
        do {
            try {
                console.print("Введите количество альбомов (Long): ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                Long v = Long.parseLong(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                console.printError("Количество альбомов не распознано, введите еще раз");
            }
        } while (true);
    }

    private MusicGenre askGenre() throws ScriptSyntaxException {
        do {
            try {
                console.print("Введите жанр (один из: ");
                for (MusicGenre g : MusicGenre.values()) {
                    console.print(g.name() + " ");
                }
                console.println("");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                return MusicGenre.valueOf(s);
            } catch (IllegalArgumentException | NoSuchElementException e) {
                if (fileMode) throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                console.printError("Жанр не распознан, введите еще раз");
            }
        } while (true);
    }
}


