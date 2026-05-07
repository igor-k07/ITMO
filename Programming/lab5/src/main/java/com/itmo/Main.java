package com.itmo;

import com.itmo.runtime.LocalRuntime;
import com.itmo.runtime.RemoteRuntime;
import com.itmo.util.SetEnviroment;
import com.itmo.util.RecursionController;
import com.itmo.util.exceptions.RuntimeInitException;

// Главный класс, точка входа в программу

public class Main {
    public static void main(String... args) {
        String filePath = SetEnviroment.getCollectionPath();
        if (filePath == null) {
            System.exit(0);
        }

        try {
            RemoteRuntime remoteRuntime = new RemoteRuntime(filePath); 
            LocalRuntime localRuntime = new LocalRuntime(remoteRuntime, new RecursionController());
            localRuntime.run("interactive");  
        } catch (RuntimeInitException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
