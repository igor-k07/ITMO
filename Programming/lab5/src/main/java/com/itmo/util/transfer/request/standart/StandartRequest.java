package com.itmo.util.transfer.request.standart;

import com.itmo.util.transfer.request.Request;

// Стандартный запрос без аргументов

public class StandartRequest implements Request {
    private final String name;

    public StandartRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    };
}


