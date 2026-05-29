package com.itmo.util.asks;

import com.itmo.models.Album;
import com.itmo.util.console.StandardConsole;
import com.itmo.util.exceptions.InvalidAskException;
import com.itmo.util.exceptions.ScriptSyntaxException;

import java.util.NoSuchElementException;

// Запрос на ввод данных для создания объекта Album

public class AskAlbum extends Ask<Album> {
    private final StandardConsole console;
    private final boolean fileMode;

    public AskAlbum(StandardConsole console) {
        this.console = console;
        this.fileMode = console.fileMode();
    }

    @Override
    public Album build() throws InvalidAskException {
        try {
            Album album = new Album(askName(), askTracks(), askLength(), askSales());
            if (!album.validate()) throw new InvalidAskException("Валидация альбома не пройдена");
            return album;
        } catch (ScriptSyntaxException e) {
            throw new InvalidAskException(e.getMessage());
        }
    }

    private String askName() throws ScriptSyntaxException {
        String name = "";
        boolean asked = false;
        do {
            try {
                console.print("Введите название альбома: ");
                name = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(name);
                if (name.equals("")) throw new ScriptSyntaxException("Некорректное название альбома");
                asked = true;
                break;
            } catch (NoSuchElementException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                } else {
                    console.printError("Название альбома не распознано, введите еще раз");
                }
            }
        } while (!asked);
        return name;
    }

    private Long askTracks() throws ScriptSyntaxException {
        boolean asked = false;
        do {
            try {
                console.print("Введите количество треков (Long) или оставьте пустым: ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                if (s.isEmpty()) return null;
                Long v = Long.parseLong(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                console.printError("Количество треков не распознано, введите еще раз");
            }
        } while (true);
    }

    private Integer askLength() throws ScriptSyntaxException {
        do {
            try {
                console.print("Введите длительность (Integer) или оставьте пустым: ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                if (s.isEmpty()) return null;
                Integer v = Integer.parseInt(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                console.printError("Длительность не распознана, введите еще раз");
            }
        } while (true);
    }

    private Double askSales() throws ScriptSyntaxException {
        do {
            try {
                console.print("Введите продажи (Double): ");
                String s = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(s);
                Double v = Double.parseDouble(s);
                return v;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                console.printError("Продажи не распознаны, введите еще раз");
            }
        } while (true);
    }
}


