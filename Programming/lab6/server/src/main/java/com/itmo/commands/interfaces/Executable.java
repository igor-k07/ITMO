package com.itmo.commands.interfaces;

import com.itmo.util.request.StandartRequest;
import com.itmo.util.response.Response;


/**
 * Определяет классы с возможностью запуска.
 * @author Septyq
 */
public interface Executable<T extends StandartRequest> {
    Response<?> execute(T request);
}


