package com.itmo.util.transfer.request;

import com.itmo.models.abstracts.Element;

import java.util.List;


// Запрос с элементом

public class ElementRequest extends StandartRequest {
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


