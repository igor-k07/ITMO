package com.itmo.util.asks;

import com.itmo.models.Coordinates;
import com.itmo.util.console.StandardConsole;
import com.itmo.util.exceptions.InvalidAskException;
import com.itmo.util.exceptions.ScriptSyntaxException;

import java.util.NoSuchElementException;

// Запрос на ввод данных для создания объекта Coordinates

public class AskCoordinates extends Ask<Coordinates> {
    private final StandardConsole console;
    private final boolean fileMode;

    public AskCoordinates(StandardConsole console) {
        this.console = console;
        this.fileMode = console.fileMode();
    }

    public Coordinates build() throws InvalidAskException {
        try {
            Coordinates coordinates = new Coordinates(askX(), askY());
            if (!coordinates.validate()) throw new InvalidAskException("Валидация координат не пройдена");
            return coordinates;   
        } catch (Exception e) {
            throw new InvalidAskException(e.getMessage());
        }
    }

    private long askX() throws ScriptSyntaxException {
        long x = 0L;
        boolean asked = false;
        do {
            try {
                console.print("Введите координату X (long): ");
                String strX = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strX);
                x = Long.parseLong(strX);
                asked = true;
                break;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                } else {
                    console.printError("Координата X не распознана, введите еще раз");
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
                console.print("Введите координату Y (Long): ");
                String strY = console.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strY);
                if (strY == "") {return null;}
                y = Long.parseLong(strY);
                asked = true;
                break;
            } catch (NoSuchElementException | NumberFormatException e) {
                if (fileMode) {
                    asked = true;
                    throw new ScriptSyntaxException("Некорректные входные данные в скрипте -> выполнение остановлено");
                } else {
                    console.printError("Координата Y не распознана, введите еще раз");
                }
            }
        } while (!asked);
    
        return y;
    }
    
}


