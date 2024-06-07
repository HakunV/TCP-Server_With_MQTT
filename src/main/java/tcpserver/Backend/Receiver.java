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
    public Object waiter = null;

    private boolean running = true;

    private boolean conAcc = false;
    private boolean retryMQTT = false;

    public Receiver(BackendClient bc, ComFlow cf, BufferedInputStream bis, Object waiter) {
        this.bc = bc;
        this.cf = cf;
        this.bis = bis;
        this.mph = new MQTT_ProtocolHandler(this);
        this.waiter = waiter;
    }

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
                        retryConnect();
                    }
                    catch (InterruptedException e) {
                        System.out.println("Could Not Reconnect");
                        System.out.println();
                        e.printStackTrace();
                    }
                }
                while (bis.available() > 0) {
                    nRead = bis.read(dataT);

                    if (nRead != -1) {
                        byte[] data = Helpers.byteCutoff(dataT, nRead);

                        dataString = Helpers.byteToHex(data);
                        dataString = Helpers.removeWhiteSpace(dataString);
                        dataString = Helpers.toLowerCase(dataString);

                        System.out.println("Input: " + dataString);
                        System.out.println();

                        mph.handleMessage(dataString);
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
            bc.setRetry(true);
        }
    }

    public void setConAcc(boolean b) {
        this.conAcc = b;
    }

    public boolean getConAcc() {return this.conAcc;}

    public void wakeUp() {
        synchronized(waiter) {
            waiter.notify();
        }
    }

    public void setRetryMQTT(boolean b) {
        retryMQTT = b;
    }

    public ComFlow getComFlow() {
        return this.cf;
    }

    public void retryConnect() throws InterruptedException {
        System.out.println("Wait 5 Seconds");
        System.out.println();

        TimeUnit.SECONDS.sleep(5);
        bc.getSender().connect();
    }
}
