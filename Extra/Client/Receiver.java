package Client;

import java.io.BufferedInputStream;
import java.io.IOException;

// Receiver class responsible for reading data from the server
public class Receiver implements Runnable {
    private ClientDummy cd;
    private BufferedInputStream bis;

    private boolean running = true;

    // Constructor to initialize the Receiver with ClientDummy and BufferedInputStream
    public Receiver(ClientDummy cd, BufferedInputStream bis) {
        this.cd = cd;
        this.bis = bis;
    }

    // The run method is executed when the thread starts
    @Override
    public void run() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new IOException();
                }
                // Read data while available
                while (bis.available() > 0) {
                    nRead = bis.read(dataT); // Read data into buffer

                    byte[] data = byteCutoff(dataT, nRead);
                    dataString = byteToHex(data);
                    dataString = removeWhiteSpace(dataString);
                    dataString = hexToString(dataString);

                    System.out.println("Input: " + dataString);
                    System.out.println();
                    System.out.println("Write data:"); // Prompt for new data
                }
            }
        } catch (IOException e) {
            System.out.println("Failed To Read the Bis");
            System.out.println();
            e.printStackTrace();
        } finally {
            cd.retry = true;
        }
    }

    // Method to cutoff the buffer to the actual number of bytes read
    public byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead]; // Create a new array of size nRead

        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i]; // Copy the data from the original buffer
        }
        return d;
    }

    // Method to remove whitespace from a string
    public String removeWhiteSpace(String in) {
        String out = "";
 
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
 
            // Check if the character is not whitespace
            if (!Character.isWhitespace(ch)) {
                out += ch; // Append the non-whitespace character to the output string
            }
        }
        return out;
    }

    // Method to convert a byte array to a hex string
    public String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b)); // Format each byte as a hex string and append to the StringBuilder
        }
        return sb.toString();
    }

    // Method to convert a hex string to a plain text string
    public static String hexToString(String str) {
        String txt = "";
        for (int i = 0; i <= str.length() - 2; i += 2) {
            String s = str.substring(i, i + 2); // Get each hex pair
            txt = txt + (char) Integer.parseInt(s, 16); // Convert hex pair to character and append to the result string
        }

        return txt;
    }
}
