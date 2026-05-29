package com.itmo.util.request;

import com.itmo.models.abstracts.Element;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


// Комбинированный запрос с элементом и id

public class CombinedRequest extends IdRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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


