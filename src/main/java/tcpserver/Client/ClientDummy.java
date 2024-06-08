package tcpserver.Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// ClientDummy class implements Runnable to handle client operations
public class ClientDummy implements Runnable {
    public Socket client = null;
    public String ip = "159.65.118.39";
    public int port = 30001;
    public BufferedInputStream bis = null;
    public BufferedOutputStream bos = null;
    public Receiver r = null;
    
    // Message to be sent
    public String msg = "";

    // Constructor to initialize client and start the receiver thread
    public ClientDummy() {
        try {
            // Establish connection to the server
            client = new Socket(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            // Initialize and start the receiver thread
            r = new Receiver(bis);
            new Thread(r).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to the server
    public void sendMessage(String msg) throws IOException {
        System.out.println("Message: " + msg);
        System.out.println();

        byte[] b = hexStrToByteArr(msg);

        bos.write(b);
        bos.flush();
    }

    // Method to run the client logic in a thread
    public void run() {
        try {
            runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to run the main client logic
    public void runClient() throws IOException {
        BufferedReader reader;
        boolean active = true;

        // Send the initial message
        sendMessage(this.msg);

        // Read status message from a file
        reader = new BufferedReader(new FileReader("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\Client\\status_message.txt"));
        // reader = new BufferedReader(new FileReader("D:\\Development\\Bachelor\\TCP-Server_With-MQTT\\src\\main\\java\\tcpserver\\Client\\status_message.txt"));

        // Create a StatusSender object with the read status message
        StatusSender ss = new StatusSender(this, reader.readLine());

        reader.close();

        // Schedule periodic sending of the status message
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(ss, 0, 5, TimeUnit.MINUTES);

        // Keep the client running
        while (active) {

        }
        bos.close();
    }

    // Method to set the message to be sent
    public void setMsg(String msg) {
        this.msg = msg;
    }

    // Helper method to convert hex string to byte array
    public static byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }
}

// StatusSender class implements Runnable to periodically send status messages
class StatusSender implements Runnable {
    public ClientDummy cd;
    public String mes = "";

    // Constructor to initialize StatusSender with a ClientDummy and a message
    public StatusSender(ClientDummy cd, String mes) {
        this.cd = cd;
        this.mes = mes;
    }

    // Method to send the status message
    public void run() {
        try {
            cd.sendMessage(mes);
        } catch (IOException e) {
            System.out.println("Could Not Send Status Message");
            System.out.println();
            e.printStackTrace();
        }
    }
}
