package tcpserver.Backend;

import tcpserver.Backend.CommunicationFlow.ComFlow;
import tcpserver.Helpers.Helpers;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Receiver implements Runnable {
    private ComFlow cf = null;
    private BufferedInputStream bis = null;
    private MQTT_ProtocolHandler mph = null;
    public Object waiter = null;

    private boolean running = true;

    private boolean conAcc = false;

    public Receiver(ComFlow cf, BufferedInputStream bis, Object waiter) {
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
                while ((nRead = bis.read(dataT)) != -1) {
                    byte[] data = Helpers.byteCutoff(dataT, nRead);

                    dataString = Helpers.byteToHex(data);
                    dataString = Helpers.removeWhiteSpace(dataString);
                    dataString = Helpers.toLowerCase(dataString);

                    System.out.println("Input: " + dataString);
                    System.out.println();

                    mph.handleMessage(dataString);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed To Read the Bis MQTT");
            e.printStackTrace();
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

    public ComFlow getComFlow() {
        return this.cf;
    }
}
