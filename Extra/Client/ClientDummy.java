package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// ClientDummy class to simulate a client connecting to a server and sending data
public class ClientDummy {
    public Socket client = null;
    public String ip = "thingsofinter.net"; // Server IP address
    public int port = 30000; // Server port
    public BufferedInputStream bis = null;
    public BufferedOutputStream bos = null;
    public Scanner scan = null;

    public boolean retry = false;

    public Receiver r = null;
    public Thread tr = null;

    // Constructor to establish initial TCP connection
    public ClientDummy() {
        connectTCP();
    }

    // Method to establish a TCP connection
    public void connectTCP() {
        try {
            // Create a new socket connection to the server
            client = new Socket(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());
            scan = new Scanner(System.in);

            // Initialize the receiver thread to handle incoming data
            r = new Receiver(this, bis);
            this.tr = new Thread(r);
            tr.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to run the client
    public void runClient() {
        boolean active = true;

        // Main loop to keep the client running
        while (active) {
            if (retry) {
                // Reconnect
                connectTCP();
                retry = false;
            }
            try {
                // Prompt user for input
                System.out.println("Write data:");
                String data = scan.nextLine();
                System.out.println("Your input: " + data);

                // Convert input data to bytes
                byte[] b = data.getBytes();

                // Send data to server
                bos.write(b);
                bos.flush();
            } catch (IOException e) {
                // Handle disconnection
                System.out.println("Disconnected");
                System.out.println();
                e.printStackTrace();
                tr.interrupt(); // Interrupt the receiver thread

                try {
                    tr.join(); // Wait for receiver thread to finish
                } catch (InterruptedException ie) {
                    System.out.println("Could Not Wait For Thread");
                    System.out.println();
                    ie.printStackTrace();
                }
            }
        }

        try {
            bos.close();
        } catch (IOException e) {
            System.out.println("Could Not Close Bos");
            System.out.println();
            e.printStackTrace();
        }
    }

    // Helper method to convert hex string to byte array
    private byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }
}
