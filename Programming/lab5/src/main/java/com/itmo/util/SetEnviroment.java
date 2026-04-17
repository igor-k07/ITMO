package com.itmo.util;

// Обработчик переменной окружения для получения пути к файлу коллекции

public class LocalEnvironment {
    public static String getCollectionPath() {
        String fileValue = System.getenv("DATA_FILE");
        if (fileValue == null) {
            return null;
        }
        // If user provided a path (contains slash or backslash), use as-is.
        if (fileValue.contains("/") || fileValue.contains("\\")) {
            return fileValue;
        }
        // Otherwise treat value as filename inside data/ directory.
        return "data/" + fileValue;
    }
}


