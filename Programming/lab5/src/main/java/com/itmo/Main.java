package com.itmo;

import com.itmo.managers.CollectionManager;
import com.itmo.managers.CommandManager;
import com.itmo.managers.DumpManager;
import com.itmo.models.MusicBand;
import com.itmo.utility.Ask;
import com.itmo.utility.Runner;
import com.itmo.utility.commands.*;
import com.itmo.utility.consoles.StandartConsole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Ask.AskBreak {
        String dataFilePath = System.getenv("DATA_FILE");
        System.out.println(dataFilePath);

        var console = new StandartConsole();
        if (dataFilePath == null) {
            console.println("Укажите путь до считываемого файла в переменной окружения DATA_FILE");
            System.exit(1);
        }

        var dumpManager = new DumpManager(dataFilePath, console);
        var collectionManager = new CollectionManager(dumpManager);
        if (!collectionManager.init()) {
            System.exit(1);
        }

        var commandManager = new CommandManager() {{
            register("add", new Add(console, collectionManager));
            register("add_if_max", new AddIfMax(console, collectionManager));
            register("clear", new Clear(collectionManager));
            register("execute_script", new ExecuteScript());
            register("exit", new Exit());
            register("filter_by_number_of_participants",
                    new FilterByNumberOfParticipants(console, collectionManager));
            register("help", new Help(this));
            register("info", new Info(collectionManager));
            register("max_by_number_of_participants",
                    new MaxByNumberOfParticipants(collectionManager));
            register("min_by_best_album", new MinByBestAlbum(collectionManager));
            register("remove_by_id", new RemoveById(collectionManager));
            register("remove_greater", new RemoveGreater(console, collectionManager));
            register("remove_lower", new RemoveLower(console, collectionManager));
            register("save", new Save(collectionManager));
            register("show", new Show(collectionManager));
            register("update", new Update(console, collectionManager));
        }};

        new Runner(console, commandManager).interactiveMode();

//        Album abbeyRoad = new Album("Abbey Road", 17L, 2820, 31000000.0);
//        Album nightVisions = new Album("Night Visions", 11L, 2580, 8000000.0);
//        MusicBand theBeatles = new MusicBand(1, "The Beatles",
//                new Coordinates(6L, 4L), ZonedDateTime.now(),
//                4L, 12L, MusicGenre.HIP_HOP, abbeyRoad);
//        MusicBand imagineDragons = new MusicBand(2, "Imagine Dragons",
//                new Coordinates(10L, 2L), ZonedDateTime.now(),
//                4L, 7L, MusicGenre.POST_ROCK, nightVisions);
    }
}