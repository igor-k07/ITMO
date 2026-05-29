package com.itmo.util.transfer.request.standart;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

// Запрос с одной строкой в качестве аргумента

public class StringRequest extends StandartRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String row;

    public StringRequest(String name, String row) {
        super(name);
        this.row = row;
    }

    public String getRow() {
        return row;
    }

    public static boolean validate(List<?> args) {
        return (args.size() == 1
            && args.get(0) instanceof String
            && !((String) args.get(0)).isEmpty());
    }
}


