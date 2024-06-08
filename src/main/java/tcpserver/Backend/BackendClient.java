package tcpserver.Backend;

import java.io.*;
import java.net.Socket;

import tcpserver.Backend.CommunicationFlow.ComFlow;
import tcpserver.Backend.Options.PublishOptions;

// Class representing the backend client
public class BackendClient implements Runnable {
    private Socket client = null;
    private String ip = "thingsofinter.net"; // IP address of the backend server
    private int port = 1883; // Port number of the backend server
    public Object waiter = null;

    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Receiver r = null;
    private Thread tr = null;
    private Sender s = null;

    private ComFlow cf = null; // Communication flow for managing message exchanges

    private boolean active = true;
    private int keepAliveInterval = 120; // Default keep-alive interval for sending ping messages
    private long keepAliveLimit = 0;

    private boolean retry = false;

    // Constructor to initialize the backend client
    public BackendClient() {
        this.waiter = new Object();
        connectTCP(); // Establish TCP connection to the server
    }

    // Method to establish TCP connection to the server
    public void connectTCP() {
        try {
            // Create socket connection to the server
            client = new Socket(ip, port);
            System.out.println("Connected to DTU");

            // Initialize input and output streams for communication
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            // Initialize sender and communication flow
            s = new Sender(this, bos);
            cf = new ComFlow(s);

            // Initialize receiver and start receiver thread
            r = new Receiver(this, cf, bis, this.waiter);
            tr = new Thread(r);
            tr.start();
        } catch (IOException e) {
            System.out.println("Could not connect to DTU");
            e.printStackTrace();
        }
    }

    // Method to run the backend client
    public void run() {
        this.keepAliveInterval = connect(); // Connect to the server and get keep-alive interval
        System.out.println("keepAliveInterval: " + keepAliveInterval);
        System.out.println();
        setDownTime(); // Set the time for the next ping message

        // Main loop for the client
        while (active) {
            if (retry) {
                connectTCP(); // Retry connecting to the server if necessary
                this.keepAliveInterval = connect(); // Reconnect and get keep-alive interval
                setDownTime(); // Set the time for the next ping message
                retry = false;
            }

            // Check if time to send ping
            if (System.currentTimeMillis() >= keepAliveLimit) {
                s.ping(); // Send ping message
                setDownTime(); // Set the time for the next ping message
            }
        }
    }

    // Method to establish MQTT connection to the server
    public int connect() {
        int keepAliveTime = s.connect(); // Connect to the server and get keep-alive time

        // Wait for connection acknowledgment from the receiver
        while (!r.getConAcc()) {
            synchronized (waiter) {
                try {
                    waiter.wait(); // Wait for acception of connect packet
                } catch (InterruptedException e) {
                    System.out.println("Could not wait");
                }
            }
        }
        System.out.println("Granted Access");
        System.out.println();

        return keepAliveTime;
    }

    // Method to publish device data to the server
    public void publish(String device, float lat, float lon) {
        PublishOptions po = new PublishOptions(); // Create publish options
        po.setQos(2);
        s.publish(device, lat, lon, po);
    }

    // Method to subscribe to server topics
    public void subscribe() {
        s.subscribe(new String[]{"DTU-IWP-DeviceData"});
    }

    // Method to unsubscribe from server topics
    public void unsubscribe() {
        s.sendUnsubscribe();
    }

    // Method to set the time for the next ping message
    public void setDownTime() {
        this.keepAliveLimit = System.currentTimeMillis() + (keepAliveInterval - 5) * 1000; // Set timer to 5 seconds before interval
    }

    // Getter method to retrieve the communication flow
    public ComFlow getComFlow() {
        return cf;
    }

    // Getter method to retrieve the sender object
    public Sender getSender() {
        return s;
    }

    // Getter method to retrieve the receiver object
    public Receiver getReceiver() {
        return r;
    }

    // Getter method to retrieve the receiver thread
    public Thread getReceiverThread() {
        return tr;
    }

    // Method to set the retry flag
    public void setRetry(boolean b) {
        this.retry = b;
    }
}
