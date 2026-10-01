package com.itmo.util;

// Обработчик переменной окружения для получения пути к файлу коллекции

public class SetEnviroment {
    public static String getCollectionPath() {
        String fileValue = System.getenv("DATA_FILE");
        if (fileValue == null) {
            return null;
        }
        if (fileValue.contains("/") || fileValue.contains("\\")) {
            return fileValue;
        }
        return "data/" + fileValue;
    }
}


