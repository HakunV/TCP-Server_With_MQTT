package tcpserver.Backend;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Receiver implements Runnable {
    private Sender s = null;
    private BufferedInputStream bis = null;
    private MQTT_ProtocolHandler mph = null;
    public Object waiter = null;

    private boolean running = true;

    private boolean conAcc = false;

    public Receiver(Sender s, BufferedInputStream bis, Object waiter) {
        this.s = s;
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
                    byte[] data = byteCutoff(dataT, nRead);

                    dataString = byteToHex(data);
                    dataString = removeWhiteSpace(dataString);
                    dataString = toLowerCase(dataString);

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

    public void sendPubacks(int packetID, int ackType) {
        s.sendPubacks(packetID, ackType);
    }

    public byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead];

        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i];
        }
        return d;
    }

    public String removeWhiteSpace(String in) {
        String out = "";
 
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
 
            // Checking whether is white space or not
            if (!Character.isWhitespace(ch)) {
                out += ch;
            }
        }
        return out;
    }

    public String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public String toLowerCase(String str) {
        String res = "";

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (!Character.isDigit(c)) {
                char cLower = c;
                if (Character.isUpperCase(c)) {
                    cLower = (char) (c ^ 0x20);
                }
                res += cLower;
            }
            else {
                res += c;
            } 
        }
        return res;
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
}
