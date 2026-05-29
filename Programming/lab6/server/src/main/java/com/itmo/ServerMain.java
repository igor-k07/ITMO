package com.itmo;

import com.itmo.network.Server;
import com.itmo.runtime.RemoteRuntime;
import com.itmo.util.SetEnviroment;
import com.itmo.util.exceptions.RuntimeInitException;

import java.io.IOException;

public class ServerMain {
    private static final int DEFAULT_PORT = 5555;

    public static void main(String[] args) {
        String filePath = SetEnviroment.getCollectionPath();
        if (filePath == null) {
            System.out.println("Не задана переменная окружения DATA_FILE");
            System.exit(1);
        }

        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            RemoteRuntime runtime = new RemoteRuntime(filePath);
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
