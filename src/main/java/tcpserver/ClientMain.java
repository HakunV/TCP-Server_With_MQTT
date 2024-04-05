package tcpserver;

import java.io.IOException;

import tcpserver.Client.ClientDummy;

public class ClientMain {
    public static void main(String[] args) {
        ClientDummy cd = new ClientDummy();

        try {
            cd.runClient();
        } catch (IOException e) {
            System.out.println("Failed to Make Client");
            e.printStackTrace();
        }
    }
}
