package com.itmo.util.transfer.request;

import java.util.List;


// Запрос с id

public class IdRequest extends StandartRequest {
    private final Integer id;

    public IdRequest(String name, Integer id) {
        super(name);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static boolean validate(List<?> args) {
        return (args.size() == 1 && isNumeric(args.get(0)));
    }

    public static boolean isNumeric(Object obj) {
        if (obj instanceof Number) {
            return true;
        }
        if (obj instanceof String) {
            try {
                Integer.parseInt(((String) obj).trim());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

}


