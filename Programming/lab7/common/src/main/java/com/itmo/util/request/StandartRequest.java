package com.itmo.util.request;

import java.io.Serial;
import java.io.Serializable;

// Стандартный запрос без аргументов

public class StandartRequest implements Request, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String login;
    private final String password;
    private Long userId;

    public StandartRequest(String name) {
        this(name, null, null);
    }

    public StandartRequest(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    };

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}


