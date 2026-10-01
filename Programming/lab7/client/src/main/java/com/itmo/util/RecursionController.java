package com.itmo.util;

import java.util.HashSet;
import java.util.Set;

// Стандартный класс для обработки рекурсии при исполнении скриптов

public class RecursionController {
    private Set<String> scriptStack = new HashSet<>();

    public void pushScript(String script) {
        scriptStack.add(script);
    }

    public void popScript(String script) {
        scriptStack.remove(script);
    }

    public boolean checkRecursion(String script) {
        if (scriptStack.contains(script)) {
            scriptStack.clear();
            return true;
        }
        return false;
    }
}
