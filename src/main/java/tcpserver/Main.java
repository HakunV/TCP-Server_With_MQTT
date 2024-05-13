package tcpserver;

import java.io.IOException;

import tcpserver.Server.*;

import tcpserver.Backend.*;

public class Main {
    public static void main(String[] args) throws IOException {
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