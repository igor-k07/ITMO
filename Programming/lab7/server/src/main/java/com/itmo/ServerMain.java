package com.itmo;

import com.itmo.network.Server;
import com.itmo.runtime.RemoteRuntime;
import com.itmo.util.exceptions.RuntimeInitException;

import java.io.IOException;

public class ServerMain {
    private static final int DEFAULT_PORT = 5555;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            RemoteRuntime runtime = new RemoteRuntime();
            runtime.registerCommands();
            Server server = new Server(port, runtime);
            server.start();
        } catch (RuntimeInitException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
            System.exit(1);
        }
    }
}
