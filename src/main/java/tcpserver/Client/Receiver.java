package tcpserver.Client;

import java.io.BufferedInputStream;
import java.io.IOException;

// Receiver class that implements Runnable to handle reading data from a BufferedInputStream
public class Receiver implements Runnable {
    private BufferedInputStream bis;
    
    private boolean running = true;

    // Constructor to initialize the BufferedInputStream
    public Receiver(BufferedInputStream bis) {
        this.bis = bis;
    }

    // Main method to run the receiver
    public void run() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            // Continue reading data while the receiver is running
            while (running) {
                // Read data from the input stream
                while ((nRead = bis.read(dataT)) != -1) {
                    // Process the read data
                    byte[] data = byteCutoff(dataT, nRead);
                    dataString = byteToHex(data);
                    dataString = removeWhiteSpace(dataString);

                    System.out.println("Input: " + dataString);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed To Read the Bis");
            e.printStackTrace();
        }
    }

    // Helper method to trim the byte array to the actual number of read bytes
    public byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead];
        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i];
        }
        return d;
    }

    // Helper method to remove whitespace from a string
    public String removeWhiteSpace(String in) {
        String out = "";
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
            // Checking whether the character is white space or not
            if (!Character.isWhitespace(ch)) {
                out += ch;
            }
        }
        return out;
    }

    // Helper method to convert a byte array to a hex string
    public String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    // Helper method to convert a hex string to a normal string
    public static String hexToString(String str) {
        String txt = "";
        for (int i = 0; i <= str.length() - 2; i += 2) {
            String s = str.substring(i, i + 2);
            txt = txt + (char) Integer.parseInt(s, 16);
        }
        return txt;
    }
}
