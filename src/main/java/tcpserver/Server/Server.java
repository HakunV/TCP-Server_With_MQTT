package tcpserver.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.ServerSocket;
import java.lang.Thread;

import tcpserver.Backend.BackendClient;
import tcpserver.Downlink.CommandLink;

// W15L: 353994711603560, 4IT7Sm
// R56L: 355688700322392, ULEukW
// R58L: 351969561190977, 0xmzJf
// S11: 359510085385131, W1xQND
// S13: 863014538187053, CnO34B
// R11: 359510087387234, RfJSdb
// W15: 359510087300096, Je8YEe

public class Server {
    // Define the port on which the server will listen
    public int port = 30001;

    // Declare server socket and client socket variables
    public ServerSocket mss = null;
    public Socket clientSocket;
    public BufferedInputStream bis = null;
    public BufferedOutputStream bos = null;

    // Declare instances for MQTT client and command link
    public BackendClient mqttClient = null;
    public CommandLink cl = null;

    // Lists to hold connected clients and their corresponding threads
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private HashMap<ClientHandler, Thread> clientThreads = new HashMap<ClientHandler, Thread>();

    // Constructor to initialize the server socket and necessary components
    public Server() {
        try {
            // Initialize the server socket on the specified port
            mss = new ServerSocket(port);
            System.out.println("Server Running");
        } catch (IOException e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }

        // Initialize the MQTT client and command link
        mqttClient = new BackendClient();
        cl = new CommandLink(this); 
    }

    // Main server loop to accept and handle client connections
    public void runServer() throws IOException {
        boolean serverActive = true;
        
        // Start the MQTT connection and command link threads
        mqttConnect();
        startCommandLink();

        // Schedule periodic printing of server load
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new PrintLoad(this), 0, 10, TimeUnit.SECONDS);

        while (serverActive) {
            try {
                // Accept a new client connection
                clientSocket = mss.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new ClientHandler for the connected client and start its thread
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                Thread cThread = new Thread(client);
                clientThreads.put(client, cThread);
                cThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }          
        }
        // Close the server socket when done
        mss.close();
    }

    // Method to start the MQTT connection in a new thread
    public void mqttConnect() {
        Thread mqttThread = new Thread(mqttClient);
        mqttThread.start();
    }

    // Method to start the command link in a new thread
    public void startCommandLink() {
        Thread comThread = new Thread(cl);
        comThread.start();
    }

    // Method to remove a client and its corresponding thread from the lists
    public void removeClient(ClientHandler client) {
        if (clients.contains(client)) {
            clients.remove(client);
            System.out.println("ClientHandler Removed");
            System.out.println();
        } else {
            System.out.println("Could Not Find ClientHandler");
            System.out.println();
        }

        if (clientThreads.containsKey(client)) {
            clientThreads.remove(client);
            System.out.println("Thread Removed");
            System.out.println(); 
        } else {
            System.out.println("Could Not Find Thread");
            System.out.println();
        }
    }

    // Method to send a command response through the command link
    public void commandResponse(String str) throws IOException {
        cl.sendResponse(str);
    }

    // Method to remove duplicate clients with the same IMEI
    public void removeDups(String imei, ClientHandler client) {
        if (clients.size() <= 1) {
            System.out.println("No Other Clients With This IMEI");
            System.out.println();
        } else {
            if (clients.contains(client)) {
                int i = clients.indexOf(client);
                for (int j = 0; j < i; j++) {
                    if (clients.get(j).getImei().equals(imei)) {
                        getClientThread(clients.get(j)).interrupt();
                        System.out.println("Removed A Client");
                        System.out.println();
                    }
                }
            } else {
                System.out.println("This Client Does Not Exist");
                System.out.println();
            }
        }
    }

    // Method to get a client handler by IMEI
    public ClientHandler getClient(String imei) {
        ClientHandler res = null;
        for (ClientHandler c : this.clients) {
            if (c.getImei().equals(imei)) {
                res = c;
                break;
            }
        }
        return res;
    }

    // Method to get the thread associated with a client handler
    public Thread getClientThread(ClientHandler ch) {
        Thread t = null;
        if (clientThreads.containsKey(ch)) {
            t = clientThreads.get(ch);
        }
        return t;
    }

    // Method to get the list of clients
    public ArrayList<ClientHandler> getClients() {
        return this.clients;
    }

    // Method to get the map of client handlers and their threads
    public HashMap<ClientHandler, Thread> getClientThreads() {
        return this.clientThreads;
    }
}

// Class to periodically print the load of the server
class PrintLoad implements Runnable {
    private Server s;

    // Constructor to initialize with the server instance
    public PrintLoad(Server s) {
        this.s = s;
    }

    // Method to run periodically to print the number of clients and threads
    public void run() {
        System.out.println("Clients Connected: " + s.getClients().size());
        System.out.println("Client Threads Active: " + s.getClientThreads().size());
        System.out.println("Total Threads Active: " + Thread.activeCount());
    }
}
