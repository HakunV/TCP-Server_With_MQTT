package tcpserver.Backend;

import tcpserver.Backend.Options.ConnectOptions;
import tcpserver.Backend.Options.PublishOptions;
import tcpserver.Backend.Options.SubscribeOptions;
import tcpserver.Helpers.Helpers;
import tcpserver.Backend.SenderPackets.*;

import java.io.*;

public class Sender {
    private BackendClient bc = null;
    private BufferedOutputStream bos = null;
    private WiFi_Config wc = null;

    // Constructor to initialize Sender
    public Sender(BackendClient bc, BufferedOutputStream bos) {
        this.bc = bc;
        this.bos = bos;
        this.wc = new WiFi_Config();
    }

    // Method to connect to the server
    public int connect() {
        String[] res = Connect.connect(new ConnectOptions()); // Connect to server with default options
        send(res[0]);
        return Integer.parseInt(res[1]); // Return connection status
    }

    // Method to connect to the server with custom options
    public int connect(ConnectOptions co) {
        String[] res = Connect.connect(co); // Connect to server with custom options
        send(res[0]);
        return Integer.parseInt(res[1]); // Return connection status
    }

    // Method to publish data
    public void publish(String device, float lat, float lon) {
        PublishOptions po = new PublishOptions(); // Default publish options
        String[] pubArr = Publish.publish(device, lat, lon, po, bc.getComFlow().getUsed()); // Generate publish packet
        bc.getComFlow().createFlow(true, Integer.parseInt(pubArr[0]), po.getQos(), pubArr[1]); // Create communication flow
        send(pubArr[1]);
    }

    // Method to publish data with custom options
    public void publish(String device, float lat, float lon, PublishOptions po) {
        String[] pubArr = Publish.publish(device, lat, lon, po, bc.getComFlow().getUsed()); // Generate publish packet
        bc.getComFlow().createFlow(true, Integer.parseInt(pubArr[0]), po.getQos(), pubArr[1]); // Create communication flow
        send(pubArr[1]);
    }

    // Method to send publish acknowledgment
    public String pubacks(int packetID, int ackType) {
        String message = Pubacks.pubacks(packetID, ackType); // Generate publish acknowledgment
        send(message);
        return message;
    }

    // Method to subscribe to topics
    public void subscribe(String[] topics) {
        String[] subArr = Subscribe.subscribe(topics, new SubscribeOptions(topics.length), bc.getComFlow().getUsed()); // Subscribe to topics
        bc.getComFlow().createFlow(true, Integer.parseInt(subArr[0]), 1, subArr[1]); // Create communication flow
        send(subArr[1]); // Send subscription request
    }

    // Method to subscribe to topics with custom options
    public void subscribe(String[] topics, SubscribeOptions so) {
        String[] subArr = Subscribe.subscribe(topics, new SubscribeOptions(topics.length), bc.getComFlow().getUsed()); // Subscribe to topics
        bc.getComFlow().createFlow(true, Integer.parseInt(subArr[0]), 1, subArr[1]); // Create communication flow
        send(subArr[1]);
    }

    // Method to send unsubscribe request
    public void sendUnsubscribe() {
        // Not Implemented
    }

    // Method to send ping message
    public void ping() {
        send(Ping.ping());
    }

    // Method to disconnect from the server
    public void disconnect() {
        send(Disconnect.disconnect());
    }

    // Method to send a message
    private void send(String message) {
        try {
            Helpers.sendMessage(message, bos);
        } catch (IOException e) {
            System.out.println("Could Not Send Message");
            System.out.println();
            e.printStackTrace();
            
            Thread rt = bc.getReceiverThread();
            rt.interrupt(); // Interrupt receiver thread to stop listening for messages

            try {
                rt.join(); // Wait for receiver thread to terminate
            } catch (InterruptedException ie) {
                System.out.println("Could Not Wait For Thread");
                System.out.println();
                ie.printStackTrace();
            }
        }
    }
}
