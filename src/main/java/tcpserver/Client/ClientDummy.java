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

public class ClientDummy implements Runnable {
    public Socket client = null;
    public String ip = "159.65.118.39";
    public int port = 30001;
    public BufferedInputStream bis = null;
    public BufferedOutputStream bos = null;
    public Receiver r = null;
    
    public String msg = "";

    public ClientDummy() {
        try {
            client = new Socket(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            r = new Receiver(bis);
            new Thread(r).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) throws IOException {
        System.out.println("Message: " + msg);
        System.out.println();

        byte[] b = hexStrToByteArr(msg);

        bos.write(b);
        bos.flush();
    }

    public void run() {
        try {
            runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runClient() throws IOException {
        BufferedReader reader;

        boolean active = true;
        sendMessage(this.msg);


        // reader = new BufferedReader(new FileReader("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\Client\\randImeis.txt"));
        reader = new BufferedReader(new FileReader("D:\\Development\\Bachelor\\TCP-Server_With-MQTT\\src\\main\\java\\tcpserver\\Client\\status_message.txt"));

        StatusSender ss = new StatusSender(this, reader.readLine());

        reader.close();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(ss, 0, (int) (Math.random() * (20 - 10) + 10), TimeUnit.SECONDS);

        while (active) {

        }
        bos.close();
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }

    // public void runClient() throws IOException {
    //     boolean active = true;

    //     while (active) {
    //         System.out.println("Write data");

    //         String data = scan.nextLine();

    //         System.out.println("Your input: " + data);

    //         // byte[] b = hexStrToByteArr(data);
    //         byte[] b = data.getBytes();

    //         bos.write(b);
    //         bos.flush();
    //     }
    //     bos.close();
    // }
}


class StatusSender implements Runnable {
    public ClientDummy cd;
    public String mes = "";

    public StatusSender(ClientDummy cd, String mes) {
        this.cd = cd;
        this.mes = mes;
    }

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
