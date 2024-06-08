package tcpserver.Server;

import tcpserver.Helpers.Helpers; // Importing helper class for utility functions

import java.io.*;
import java.net.Socket;

// Declaring ClientHandler class which implements the Runnable interface for concurrent execution
public class ClientHandler implements Runnable {
    private Socket socket;
    public Server server;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private ProtocolHandler ph;
    private ClientWriter cw;
    private String imei = "";

    public String isn = "0000";

    private boolean clientActive = true;

    public int byteSize = Helpers.getByteSize(); // Byte size is 2

    private long shutdownTime = 0; // Timestamp for when the client should be shut down

    // Constructor for initializing ClientHandler
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        resetShutdownTime();
    }

    // Run method for concurrent execution
    public void run() {
        int nRead = 0;
        byte[] dataT = new byte[1024];
        String dataString = "";

        try {
            bis = new BufferedInputStream(socket.getInputStream()); // Initializing input stream
            bos = new BufferedOutputStream(socket.getOutputStream()); // Initializing output stream

            this.ph = new ProtocolHandler(this); // Initializing ProtocolHandler for message processing
            
            this.cw = new ClientWriter(this, bos); // Initializing ClientWriter for writing responses
        } catch (IOException e) {
            System.out.println("Error occurred while setting up client connection");
            e.printStackTrace();
        }

        try {
            // Loop for reading data from the client
            while (clientActive) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new IOException();
                }
                if (System.currentTimeMillis() >= shutdownTime) {
                    System.out.println("Time Is Up For: " + this.imei);
                    System.out.println();
                    throw new IOException();
                }
                while (bis.available() > 0) { // Loop while data is available for reading
                    nRead = bis.read(dataT); // Read data into the temporary byte array
                    if (nRead != -1) {
                        byte[] data = Helpers.byteCutoff(dataT, nRead);
                        dataString = Helpers.byteToHex(data);

                        dataString = Helpers.removeWhiteSpace(dataString);
                        dataString = Helpers.toLowerCase(dataString);

                        System.out.println("Input: " + dataString + "   " + Helpers.ts()); // Log the received data and current timestamp
                        System.out.println();

                        ph.handleMessage(dataString); // Handle the incoming message using ProtocolHandler
                    }
                    else {
                        break; // Break out of the loop if no more data is available
                    }
                }
            }
        } 
        catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }
        finally {
            server.removeClient(this);

            try {
                bis.close();
                bos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Method to reset the shutdown timer
    public void resetShutdownTime() {
        System.out.println("Timer Reset");
        System.out.println();
        this.shutdownTime = System.currentTimeMillis() + 120 * 1000; // Set shutdown time 120 seconds from the current time
    }

    // Method to publish client location
    public void publish(float lat, float lon) {
        server.mqttClient.publish(imei, lat, lon);
    }

    // Method to respond to client status
    public void respondToStatus(String isn) throws IOException {
        cw.respondStandard("13", isn);
    }

    // Method to respond to client login
    public void respondToLogin(String isn) throws IOException {
        cw.respondStandard("01", isn);
    }

    // Method to respond to client alarm
    public void respondToAlarm(String isn) throws IOException {
        cw.respondStandard("26", isn);
    }

    // Method to send command to client
    public void sendCommand(String command) throws IOException {
        cw.sendCommand(command);
    }

    // Method to handle command response
    public void commandResponse(String response) throws IOException {
        server.commandResponse(response);
    }

    // Method to send a message to the client
    public void sendMessage(String mes) throws IOException {
        Helpers.sendMessage(mes, bos);
    }

    // Method to set client name
    public void setName(String name) {
        this.imei = name;
        System.out.println("Name Set: " + name);
        System.out.println();
    }

    // Method to get client IMEI
    public String getImei() {
        return this.imei;
    }

    // Method to check for duplicate clients
    public void checkDups() {
        server.removeDups(this.imei, this);
    }

    // Method to set client end flag
    public void setEndFlag(boolean flag) {
        this.clientActive = flag;
    }

    // Method to get client socket
    public Socket getSocket() {
        return this.socket;
    }
}
