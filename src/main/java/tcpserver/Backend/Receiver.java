package tcpserver.Backend;

import tcpserver.Backend.CommunicationFlow.ComFlow;
import tcpserver.Helpers.Helpers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Receiver implements Runnable {
    private BackendClient bc = null;
    private ComFlow cf = null;
    private BufferedInputStream bis = null;
    private MQTT_ProtocolHandler mph = null;
    public Object waiter = null; // Waiter object for synchronization

    private boolean running = true;

    private boolean conAcc = false; // Flag indicating if connection is accepted
    private boolean retryMQTT = false; // Flag indicating if MQTT connection needs to be retried

    // Constructor to initialize Receiver
    public Receiver(BackendClient bc, ComFlow cf, BufferedInputStream bis, Object waiter) {
        this.bc = bc;
        this.cf = cf;
        this.bis = bis;
        this.mph = new MQTT_ProtocolHandler(this);
        this.waiter = waiter;
    }

    // Run method to start the receiver
    public void run() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new IOException();
                }
                if (retryMQTT) {
                    try {
                        retryConnect(); // Send another connect packet
                    }
                    catch (InterruptedException e) {
                        System.out.println("Could Not Reconnect");
                        System.out.println();
                        e.printStackTrace();
                    }
                }
                while (bis.available() > 0) { // Read while data is available
                    nRead = bis.read(dataT);

                    if (nRead != -1) {
                        byte[] data = Helpers.byteCutoff(dataT, nRead);

                        dataString = Helpers.byteToHex(data);
                        dataString = Helpers.removeWhiteSpace(dataString);
                        dataString = Helpers.toLowerCase(dataString);

                        System.out.println("Input: " + dataString);
                        System.out.println();

                        mph.handleMessage(dataString); // Handle received message with MQTT protocol handler
                    }
                    else {
                        break;
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed To Read the Bis MQTT");
            System.out.println();
            e.printStackTrace();
        }
        finally {
            bc.setRetry(true); // Create new TCP connection
        }
    }

    // Method to set the connection accepted flag
    public void setConAcc(boolean b) {
        this.conAcc = b;
    }

    // Method to get the connection accepted flag
    public boolean getConAcc() {
        return this.conAcc;
    }

    // Method to wake up the receiver
    public void wakeUp() {
        synchronized(waiter) {
            waiter.notify(); // Notify the waiter object to proceed with connection
        }
    }

    // Method to set the retry MQTT flag
    public void setRetryMQTT(boolean b) {
        retryMQTT = b;
    }

    // Method to get the communication flow
    public ComFlow getComFlow() {
        return this.cf;
    }

    // Method to retry MQTT connection
    public void retryConnect() throws InterruptedException {
        System.out.println("Wait 5 Seconds");
        System.out.println();

        TimeUnit.SECONDS.sleep(5); // Wait for 5 seconds
        bc.getSender().connect(); // Reconnect to the server
    }
}
