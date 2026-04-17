package commands;

import util.transfer.request.standart.StringRequest;
import util.transfer.response.Response;

public class ExecuteScript extends Command<StringRequest> {
    public ExecuteScript() {
        super(new CommandAttribute("execute_script file_name", "исполнить скрипт из файла", StringRequest.class));
    }

    public Response<?> execute(StringRequest request) {
        // execution of scripts is handled locally in LocalRuntime; remote returns OK
        return new Response<>();
    }
}
