package com.itmo.util.transfer.request.standart;

import com.itmo.models.Element;

import java.util.List;

// Комбинированный запрос с элементом и id

public class CombinedRequest extends IdRequest {
    private final Element element;

    public CombinedRequest(String name, Element element, int id) {
        super(name, id);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public static boolean validate(List<?> args) {
        return (args.size() == 2 
            && args.get(0) instanceof Element && args.get(0) != null
            && isNumeric(args.get(1))) && args.get(1) != null;
    }

}
