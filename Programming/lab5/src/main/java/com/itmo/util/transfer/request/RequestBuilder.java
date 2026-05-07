package com.itmo.util.transfer.request;

import com.itmo.models.Element;
import com.itmo.util.console.IOConsole;
import com.itmo.util.exceptions.IncorrectRequestException;
import com.itmo.util.exceptions.ScriptSyntaxException;
import com.itmo.util.asks.AskMusicBand;
import com.itmo.util.transfer.request.standart.*;

import java.util.List;
import java.util.Map;


// Создание запроса необходимого формата и валидация аргументов

public class RequestBuilder {
    private final IOConsole console;

    public RequestBuilder(IOConsole console) {
        this.console = console;
    }

    public Request buildRequest(Map<String, Class<? extends Request>> commandsAttributes, String name, List<?> args) 
                                        throws IncorrectRequestException, ScriptSyntaxException {

        Class<? extends Request> requestType = commandsAttributes.get(name);
        if (requestType == null) {
            throw new IncorrectRequestException("Неизвестная команда: " + name);
        }

        if (requestType == StandartRequest.class && args.size() == 0) {
            return new StandartRequest(name);
        } else if (requestType == StringRequest.class && StringRequest.validate(args)) {
            return new StringRequest(name, (String) args.get(0));
        } else if (requestType == IdRequest.class && IdRequest.validate(args)) {
            return new IdRequest(name, Integer.valueOf((String) args.get(0)));
        } else if (requestType == ElementRequest.class) {
            Element result = buildElement();

            if (result == null || !ElementRequest.validate(List.of(result))) {
                if (console.fileMode()) {
                    throw new ScriptSyntaxException("Выполнение скрипта остановлено из-за некорректного синтаксиса");
                }
                throw new IncorrectRequestException("Некорректный запрос");
            }
            return new ElementRequest(name, result);
        } else if (requestType == CombinedRequest.class && args.size() == 1) {
            try {
                Integer id = Integer.valueOf((String) args.get(0));
                Element result = buildElement();
                if (result == null || !CombinedRequest.validate(List.of(result, id))) {
                    throw new IncorrectRequestException("Некорректный запрос");
                }
                return new CombinedRequest(name, result, id);
            } catch (NumberFormatException e) {
                throw new IncorrectRequestException("Некорректный запрос");
            }
        }
        
        throw new IncorrectRequestException("Некорректный запрос");
    }


    private Element buildElement() {
        AskMusicBand form = new AskMusicBand(console);
        return form.build();
    }
}


