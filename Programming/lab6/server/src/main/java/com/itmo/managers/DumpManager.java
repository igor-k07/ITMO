package com.itmo.managers;

import com.google.gson.*;
import com.itmo.models.abstracts.Element;
import com.itmo.models.MusicBand;
import com.itmo.util.ZonedDateTimeAdapter;
import com.itmo.util.exceptions.LoadException;
import com.itmo.util.exceptions.WriteException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Менеджер для сохранения и загрузки коллекции из файла

public class DumpManager {
    private final String fileName;

    private final Gson writeGson = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
        .create();

    private final Gson readGson = new GsonBuilder()
        .setLenient()
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
        .create();

    public DumpManager(String fileName) {
        this.fileName = fileName;
    }
    
    public Collection<Element> readCollectionFromFile() throws LoadException {
        if (fileName == null || fileName.isEmpty()) {
            return new ArrayList<>();
        }
        
        File file = new File(fileName);
        if (!file.exists()) {
            throw new LoadException("Файл не существует");
        }
        if (!file.canRead()) {
            throw new SecurityException("Нет прав на чтение: " + file.getAbsolutePath());
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            StringBuilder jsonString = readFileContent(reader);
            if (jsonString.length() == 0) {
                jsonString = new StringBuilder("[]");
            }
            return parseValidBands(jsonString.toString());

        } catch (IOException e) {
            throw new LoadException("Не удалось прочитать файл: " + e.getMessage());
        } catch (SecurityException e) {
            throw new LoadException(e.getMessage());
        }
    }
    private StringBuilder readFileContent(InputStreamReader reader) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            jsonString.append(buffer, 0, read);
        }
        return jsonString;
    }

    private Collection<Element> parseValidBands(String jsonString) {
        try {
            JsonElement jsonArray = JsonParser.parseString(jsonString);
            if (!jsonArray.isJsonArray()) {
                return new ArrayList<>();
            }

            List<Element> valid = new ArrayList<>();
            for (JsonElement element : jsonArray.getAsJsonArray()) {
                try {
                    MusicBand band = readGson.fromJson(element, MusicBand.class);
                    if (band != null && band.validate()) {
                        valid.add(band);
                    }
                } catch (Exception e) {}
            }

            int maxId = valid.stream()
                .map(e -> ((MusicBand)e).getId())
                .mapToInt(Integer::intValue)
                .max().orElse(0);

            for (Element e : valid) {
                MusicBand band = (MusicBand) e;
                if (band.getId() == 0) {
                    maxId++;
                    band.setId(maxId);
                }
            }

            return valid;
        } catch (JsonParseException e) {
            return new ArrayList<>();
        }
    }

    public void writeCollectionToFile(Collection<? extends Element> collection) throws WriteException {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            byte[] bytes = writeGson.toJson(collection).getBytes(StandardCharsets.UTF_8);
            out.write(bytes);
            out.flush();
        } catch (IOException exception) {
            throw new WriteException("Не удалось записать коллекцию в файл");
        }
    }
}


