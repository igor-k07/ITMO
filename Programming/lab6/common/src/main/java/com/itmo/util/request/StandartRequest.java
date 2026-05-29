package com.itmo.util.request;

import java.io.Serial;
import java.io.Serializable;

// Стандартный запрос без аргументов

public class StandartRequest implements Request, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;

    public StandartRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    };
}


