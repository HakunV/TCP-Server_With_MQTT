package tcpserver.Backend;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class BackendClient implements Runnable {
    private Socket client = null;
    private String ip = "thingsofinter.net";
    private int port = 1883;
    public Object waiter = null;

    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Receiver r = null;
    private Sender s = null;

    private boolean active = true;
    private int keepAlive = 120;

    public BackendClient() {
        this.waiter = new Object();
        try {
            client = new Socket(ip, port);
            System.out.println("Connected to DTU");

            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            s = new Sender(this, bos);

            r = new Receiver(s, bis, this.waiter);
            new Thread(r).start();
        }
        catch(IOException e) {
            System.out.println("Could not connect to DTU");
            e.printStackTrace();
        }
    }

    public void run() {
        connect();

        while (active) {
            
        }
    }

    public void connect() {
        s.connect();

        while(!r.getConAcc()) {
            synchronized(waiter) {
                try {
                    waiter.wait();
                } catch (InterruptedException e) {
                    System.out.println("Could not wait");
                }
            }
        }
        System.out.println("Granted Access");
        System.out.println();
    }

    public void publish(String device, float lat, float lon) {
        s.publish(device, lat, lon);
    }

    public void subscribe() {
        s.subscribe(new String[] {"DTU-IWP-DeviceData"});
    }

    public void unsubscribe() {
        s.sendUnsubscribe();
    }

    public void setKeepAlive(int x) {
        keepAlive = x;
    }
}
