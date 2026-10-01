package com.itmo.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:postgresql://pg:5432/studs";

    // запрещаем создать объект
    private DatabaseConnection() { 
    }

    public static Connection open() throws SQLException {
        String url = getRequiredOrDefault("DB_URL", DEFAULT_URL);
        String user = getRequired("DB_USER");
        String password = getRequired("DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

    private static String getRequired(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Не задана переменная окружения " + name);
        }
        return value;
    }

    private static String getRequiredOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
