package com.itmo;

import com.itmo.network.ClientTransport;
import com.itmo.network.RequestPersistenceProxy;
import com.itmo.network.RequestTransport;
import com.itmo.runtime.LocalRuntime;
import com.itmo.util.RecursionController;

public class ClientMain {
    private static final int DEFAULT_PORT = 5555;
    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length > 0 && !args[0].isBlank()) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        RequestTransport transport = new RequestPersistenceProxy(new ClientTransport(host, port));
        LocalRuntime runtime = new LocalRuntime(transport, new RecursionController());
        runtime.run("interactive");
    }
}
