package com.itmo.managers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

public class UserManager {
    private static final String REGISTER_USER = """
        INSERT INTO users (login, password_md5)
        VALUES (?, ?)
        RETURNING id
        """;

    private static final String FIND_USER = """
        SELECT id
        FROM users
        WHERE login = ? AND password_md5 = ?
        """;

    public long register(String login, String password) throws SQLException {
        validateCredentials(login, password);

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(REGISTER_USER)) {
            statement.setString(1, login);
            statement.setString(2, md5(password));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("База данных не вернула id пользователя");
                }
                return resultSet.getLong("id");
            }
        }
    }

    public Optional<Long> authenticate(String login, String password) throws SQLException {
        validateCredentials(login, password);

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(FIND_USER)) {
            statement.setString(1, login);
            statement.setString(2, md5(password));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(resultSet.getLong("id"));
            }
        }
    }

    public static String md5(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(hash.length * 2);
            for (byte item : hash) {
                result.append(String.format("%02x", item & 0xff));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Алгоритм MD5 недоступен", exception);
        }
    }

    private void validateCredentials(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
    }
}