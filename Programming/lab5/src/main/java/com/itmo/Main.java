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

        var console = new StandartConsole();

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
    }
}