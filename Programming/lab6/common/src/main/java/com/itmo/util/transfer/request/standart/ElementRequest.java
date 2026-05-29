package com.itmo.util.transfer.request.standart;

import com.itmo.models.Element;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

// Запрос с элементом

public class ElementRequest extends StandartRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Element element;

    public ElementRequest(String name, Element element) {
        super(name);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public static boolean validate(List<?> args) {
        return (args.size() == 1 && args.get(0) instanceof Element && args.get(0) != null);
    }
}


