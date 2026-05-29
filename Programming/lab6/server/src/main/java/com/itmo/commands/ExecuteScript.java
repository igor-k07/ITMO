package com.itmo.commands;

import com.itmo.util.request.StringRequest;
import com.itmo.util.response.Response;

// Исполняет скрипт из файла

public class ExecuteScript extends Command<StringRequest> {
    public ExecuteScript() {
        super(new CommandAttribute("execute_script имя_файла", "исполнить скрипт из файла", StringRequest.class));
    }

    public Response<?> execute(StringRequest request) {
        return new Response<>();
    }
}
