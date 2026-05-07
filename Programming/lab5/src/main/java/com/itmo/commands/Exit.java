package com.itmo.commands;

import com.itmo.util.Status;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;


// Завершает программу (без сохранения в файл)

public class Exit extends Command<StandartRequest> {
    public Exit() {
        super(new CommandAttribute(
            "exit", 
            "завершить программу (без сохранения в файл)", 
            StandartRequest.class
            ));
    }

    public Response<?> execute(StandartRequest request) {
        return new Response<>(Status.EXIT);
    }
}


