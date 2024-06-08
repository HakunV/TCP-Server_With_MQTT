package tcpserver;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tcpserver.Server.*;

public class Main {
    public static void main(String[] args) throws IOException {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Update device list every 3 minutes
        scheduler.scheduleAtFixedRate(new DeviceList(), 0, 3, TimeUnit.MINUTES);

        Server server = new Server();

        server.runServer();
    }
}