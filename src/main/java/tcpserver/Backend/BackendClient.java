package tcpserver.Backend;

import java.io.*;
import java.net.Socket;
import java.util.*;

import tcpserver.Backend.CommunicationFlow.ComFlow;
import tcpserver.Backend.Options.ConnectOptions;
import tcpserver.Backend.Options.PublishOptions;

public class BackendClient implements Runnable {
    private Socket client = null;
    private String ip = "thingsofinter.net";
    private int port = 1883;
    public Object waiter = null;

    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Receiver r = null;
    private Sender s = null;

    private ComFlow cf = null;

    private boolean active = true;
    private int keepAliveInterval = 120;
    private long keepAliveLimit = 0;

    public BackendClient() {
        this.waiter = new Object();
        try {
            client = new Socket(ip, port);
            System.out.println("Connected to DTU");

            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            s = new Sender(this, bos);

            cf = new ComFlow(s);

            r = new Receiver(cf, bis, this.waiter);
            new Thread(r).start();
        }
        catch(IOException e) {
            System.out.println("Could not connect to DTU");
            e.printStackTrace();
        }
    }

    public void run() {
        this.keepAliveInterval = connect();
        System.out.println("keepAliveInterval: " + keepAliveInterval);
        System.out.println();
        setDownTime();

        publish("355688700322392", (float) 55.43223, (float) 13.12313);
        while (active) {
            

            if (System.currentTimeMillis() >= keepAliveLimit) {
                s.ping();
                setDownTime();
            }
        }
    }

    public int connect() {
        ConnectOptions co = new ConnectOptions();
        co.setKeepAlive(30);

        int keepAliveTime = s.connect(co);

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

        return keepAliveTime;
    }

    public void publish(String device, float lat, float lon) {
        PublishOptions po = new PublishOptions();
        po.setQos(2);
        s.publish(device, lat, lon, po);
    }

    public void subscribe() {
        s.subscribe(new String[] {"DTU-IWP-DeviceData"});
    }

    public void unsubscribe() {
        s.sendUnsubscribe();
    }

    public void setDownTime() {
        this.keepAliveLimit = System.currentTimeMillis() + (keepAliveInterval - 5) * 1000;
    }

    public ComFlow getComFlow() {
        return cf;
    }
}
