package tcpserver;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tcpserver.Server.*;

import tcpserver.Backend.*;
import tcpserver.Helpers.Helpers;

public class Main {
    public static void main(String[] args) throws IOException {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new DeviceList(), 0, 10, TimeUnit.MINUTES);

        Server server = new Server();

        server.runServer();

        // BackendClient backendClient = new BackendClient();

        // backendClient.publish("355688700322392", (float) 30.2324, (float) 13.2390);

        // Thread th = new Thread(backendClient);

        // th.start();
        // backendClient.publish("355688700322392", (float) 30.2324, (float) 13.2390);
        // backendClient.publish("353994711603560", (float) 47.5432, (float) 9.2342);
    }
}