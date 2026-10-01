package com.itmo.commands;

import com.itmo.managers.UserManager;
import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;

import java.sql.SQLException;
import java.util.List;

public class Register extends Command<StandartRequest> {
    private final UserManager userManager;

    public Register(UserManager userManager) {
        super(new CommandAttribute(
            "register",
            "зарегистрировать пользователя",
            StandartRequest.class
        ));
        this.userManager = userManager;
    }

    @Override
    public Response<?> execute(StandartRequest request) {
        try {
            userManager.register(request.getLogin(), request.getPassword());
            return new Response<>(List.of("Пользователь зарегистрирован"));
        } catch (IllegalArgumentException e) {
            return new Response<>(List.of(e.getMessage()), com.itmo.util.Status.ERROR);
        } catch (SQLException e) {
            return new Response<>(List.of("Не удалось зарегистрировать пользователя: " + e.getMessage()),
                com.itmo.util.Status.ERROR);
        }
    }
}