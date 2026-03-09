package com.itmo.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.itmo.utility.abstracted.interfaces.Console;
import com.itmo.models.MusicBand;
import com.itmo.utility.adapters.ZonedDateTimeAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;


public class DumpManager {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();

    private final String fileName;
    private final Console console;

    public DumpManager(String fileName, Console console) {
//        if (!(new File(fileName).exists())) {
//            fileName = "../" + fileName;
//        }
        this.fileName = fileName;
        this.console = console;
    }

    public void writeCollection(Collection<MusicBand> collection) {
        try (BufferedOutputStream bos =
                     new BufferedOutputStream(new FileOutputStream(fileName))) {
            String json = gson.toJson(collection);
            bos.write(json.getBytes(StandardCharsets.UTF_8));
            console.println("Коллекция успешно сохранена в файл!");
        } catch (IOException e) {
            console.printError("Загрузочный файл не может быть открыт!");
        }
    }

    public Collection<MusicBand> readCollection() {
        if (fileName != null && !fileName.isEmpty()) {
            try (var fileInputStream = new FileInputStream(fileName);
                 var inputStreamReader = new InputStreamReader(fileInputStream,
                         StandardCharsets.UTF_8);
                 var bufferedReader = new BufferedReader(inputStreamReader)) {

                var collectionType = new TypeToken<HashSet<MusicBand>>(){}.getType();
                var objectType = new TypeToken<MusicBand>(){}.getType();
                var jsonString = new StringBuilder();

                String line;
                ArrayList<String> objects = new ArrayList<>();
                int count = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    if (line.contains("{")) {
                        count += 1;
                    } else if (line.contains("}")) {
                        count -= 1;
                    }
                    if ((!line.equals("") & !line.equals("[") & !line.equals("]"))) {
                        jsonString.append(line);
                        if (count == 0) {
                            if (jsonString.charAt(jsonString.length() - 1) == ',') {
                                jsonString.deleteCharAt(jsonString.length() - 1);
                            }
                            objects.add(jsonString.toString());
                            jsonString.setLength(0);
                        }
                    }
                }


                if (objects.isEmpty()) {
                    jsonString = new StringBuilder("[]");
                }


                HashSet<MusicBand> collection = new HashSet<>();
                for (String band : objects) {
                    try {
                        collection.add(gson.fromJson(band, objectType));
                    } catch (JsonSyntaxException e) {console.printError(
                            "В загруженом файле не все поля валидны, некоторые элементы могут быть утеряны");}
                }

                console.println("Коллекция успешно загружена!");
                return collection;

            } catch (FileNotFoundException e) {
                console.printError("Загрузочный файл не найден!");
            } catch (NoSuchElementException e) {
                console.printError("Загрузочный файл пуст!");
            } catch (JsonParseException e) {
                console.printError("В загрузочном файле не обнаружена необходимая коллекция!");
            } catch (IllegalStateException | IOException e) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        } else {
            console.println("Укажите путь до считываемого файла в переменной окружения DATA_FILE");
            System.exit(1);
        }
        return new HashSet<>();
    }
}
