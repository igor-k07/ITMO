package com.itmo.util.transfer.request;

import java.util.List;

// Запрос с одной строкой в качестве аргумента

public class StringRequest extends StandartRequest {
    private final String row;

    public StringRequest(String name, String row) {
        super(name);
        this.row = row;
    }

    public String getRow() {
        return row;
    }

    public static boolean validate(List<?> args) {
        return (args.size() == 1 && args.get(0) instanceof String && args.get(0) != "");
    }
}


