package tcpserver.Downlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import tcpserver.Helpers.Helpers;
import tcpserver.Server.ClientHandler;
import tcpserver.Server.Server;

public class CommandLink implements Runnable {
    public int port = 30000; // Port number for command link

    public ServerSocket mss = null; // Server socket for command link
    public Socket clientSocket;

    public Server s = null;

    public BufferedInputStream bis = null;
    public BufferedOutputStream bos = null;
    
    public Receiver r = null; // Receiver object for handling incoming data

    public boolean loggedIn = false; // Flag indicating if a client is logged in

    // Constructor for initializing CommandLink
    public CommandLink(Server s) {
        this.s = s;
        try {
            mss = new ServerSocket(port); // Creating server socket
            System.out.println("Command Link Running");
        } catch (IOException e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }

    // Run method for concurrent execution
    public void run() {
        System.out.println("ComLink Started");
        System.out.println();
        boolean serverActive = true;
        while (serverActive) {
            try {
                clientSocket = mss.accept(); // Accept incoming client connection

                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                bis = new BufferedInputStream(clientSocket.getInputStream());
                bos = new BufferedOutputStream(clientSocket.getOutputStream());

                r = new Receiver(this, bis);
                r.runReceiver();
            } catch (Exception e) {
                e.printStackTrace();
            }          
        }
        try {
            mss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle incoming data
    public void handleData(String str) throws IOException {
        if (!loggedIn) {
            handleLogin(str);
        } else if (str.equals("exit")) {
            r.setRunning(false); // Set receiver thread to stop running
        } else {
            if (str.length() < 15 || !str.contains(";")) {
                System.out.println("Too Short");
                System.out.println();
                sendResponse(Helpers.textToHex("Too Short"));
            } else {
                String[] mes = str.split(";"); // Split message. Even indexes are IMEI number, uneven are commands

                try {
                    for (int i = 0; i < mes.length; i += 2) {
                        System.out.println("Imei: " + mes[i]);
                        System.out.println("Command: " + mes[i + 1]);
                        System.out.println();

                        ClientHandler ch = s.getClient(mes[i]); // Get client handler based on IMEI

                        if (ch == null) {
                            System.out.println("No Such Clients");
                            System.out.println();
                            sendResponse(Helpers.textToHex("No Such Clients"));
                        } else {
                            ch.sendCommand(mes[i + 1]); // Send command to tracker
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ae) {
                    System.out.println("Index out of bounds");
                    System.out.println("Try Again");
                    System.out.println();
                    sendResponse(Helpers.textToHex("Index Out Of Bounds: Try Again"));
                }
                r.setTimeUp(300 * 1000); // Set timer to 5 minutes
            }
        }
    }

    // Method to handle login
    public void handleLogin(String str) throws SocketException, IOException {
        String p = "";

        BufferedReader br = new BufferedReader(new FileReader("/home/student/p.txt")); // Read required password from file
        p = br.readLine(); // Read sent password
        br.close();

        if (str.equals(p)) {
            this.loggedIn = true;
            sendResponse(Helpers.textToHex("Accepted")); // Client accepted
            r.setTimeUp(300 * 1000); // Set timer to 5 minutes
        } else {
            r.setRunning(false);
            sendResponse(Helpers.textToHex("Rejected")); // Client rejected
        }
    }

    // Method to send response
    public void sendResponse(String str) throws IOException {
        Helpers.sendMessage(str, bos);
    }
}

// Receiver class for handling incoming data
class Receiver {
    public CommandLink cl = null;
    public BufferedInputStream bis = null;

    private boolean running = true;

    private long timeUp = 0; // Time until receiver should stop running

    // Constructor for initializing Receiver
    public Receiver(CommandLink cl, BufferedInputStream bis) {
        this.cl = cl;
        this.bis = bis;
    }

    // Method to start the receiver thread
    public void runReceiver() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            setTimeUp(15*1000); // Set timer to 15 seconds
            while (running) {
                // Check if timer has run out
                if (System.currentTimeMillis() >= timeUp) {
                    throw new IOException();
                }
                while (bis.available() > 0) { // Reads while data is available
                    nRead = bis.read(dataT); // Read data into byte array
                    if (nRead != -1) {
                        byte[] data = Helpers.byteCutoff(dataT, nRead);

                        dataString = new String(data, StandardCharsets.UTF_8);

                        System.out.println("Input: " + dataString);
                        System.out.println();

                        cl.handleData(dataString);
                    }
                    else {
                        break;
                    }
                }
            }
            throw new IOException();
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            System.out.println();
            e.printStackTrace();
        }
        finally {
            try {
                bis.close();
                cl.bos.close();
                cl.clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Set timer
    public void setTimeUp(long time) {
        this.timeUp = System.currentTimeMillis() + time;
    }

    // Set state of Receiver
    public void setRunning(boolean b) {
        this.running = b;
    }
}