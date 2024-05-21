package tcpserver.Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClientDummy {
    public Socket client = null;
    public String ip = "159.65.118.39";
    public int port = 30000;
    public BufferedInputStream bis = null;
    public BufferedOutputStream bos = null;
    public Scanner scan = null;

    public Receiver r = null;

    public ClientDummy() {
        try {
            client = new Socket(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());
            scan = new Scanner(System.in);

            r = new Receiver(bis);
            new Thread(r).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runClient() throws IOException {
        boolean active = true;

        while (active) {
            System.out.println("Write data");

            String data = scan.nextLine();

            System.out.println("Your input: " + data);

            // byte[] b = hexStrToByteArr(data);
            byte[] b = data.getBytes();

            bos.write(b);
            bos.flush();
        }
        bos.close();
    }

    private byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }
}
