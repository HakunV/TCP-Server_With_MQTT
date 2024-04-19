package tcpserver;

import java.io.IOException;

import tcpserver.Server.*;

import tcpserver.Backend.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Server server = new Server();

        // server.runServer();

        BackendClient backendClient = new BackendClient();

        backendClient.run();
    }
}