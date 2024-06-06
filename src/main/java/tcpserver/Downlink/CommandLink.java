package tcpserver.Downlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import tcpserver.Helpers.Helpers;
import tcpserver.Server.ClientHandler;
import tcpserver.Server.Server;

public class CommandLink implements Runnable {
    public int port = 30000;

	public ServerSocket mss = null;
    public Socket clientSocket;

    public Server s = null;

    public BufferedInputStream bis = null;
	public BufferedOutputStream bos = null;
    
    public Receiver r = null;

    public CommandLink(Server s) {
        this.s = s;
        try {
            mss = new ServerSocket(port);
            System.out.println("Command Link Running");
        }
        catch (IOException e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("ComLink Started");
        boolean serverActive = true;
        while(serverActive) {
            try {
                clientSocket = mss.accept();

                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                bis = new BufferedInputStream(clientSocket.getInputStream());
                bos = new BufferedOutputStream(clientSocket.getOutputStream());

                r = new Receiver(this, bis);
                new Thread(r).start();
            } catch (Exception e) {
            }          
        }
        try {
            mss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleData(String str) throws IOException {
        if (str.length() < 15 || !str.contains(";")) {
            System.out.println("Too Short");
            System.out.println();
            sendResponse(Helpers.textToHex("Too Short"));
        }
        else {
            String[] mes = str.split(";");

            try {
                for (int i = 0; i < mes.length; i += 2) {
                    System.out.println("Imei: " + mes[i]);
                    System.out.println("Command: " + mes[i+1]);
                    System.out.println();

                    ClientHandler ch = s.getClient(mes[i]);

                    if (ch == null) {
                        System.out.println("No Such Clients");
                        System.out.println();
                        sendResponse(Helpers.textToHex("No Such Clients"));
                    }
                    else {
                        ch.sendCommand(mes[i+1]);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException ae) {
                System.out.println("Index out of bounds");
                System.out.println("Try Again");
                System.out.println();
                sendResponse(Helpers.textToHex("Index Out Of Bounds: Try Again"));
            }
        }
    }

    public void sendResponse(String str) throws IOException {
        Helpers.sendMessage(str, bos);
    }
}

class Receiver implements Runnable {
    public CommandLink cl = null;
    public BufferedInputStream bis = null;

    public boolean running = true;

    public Receiver(CommandLink cl, BufferedInputStream bis) {
        this.cl = cl;
        this.bis = bis;
    }

    public void run() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            while (running) {
                while ((nRead = bis.read(dataT)) != -1) {
                    byte[] data = Helpers.byteCutoff(dataT, nRead);

                    dataString = new String(data, StandardCharsets.UTF_8);

                    System.out.println("Input: " + dataString);
                    System.out.println();

                    cl.handleData(dataString);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed To Read the Bis CommandLink");
            e.printStackTrace();
        }
    }
}