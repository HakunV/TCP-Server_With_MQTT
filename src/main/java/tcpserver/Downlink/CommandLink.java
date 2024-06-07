package tcpserver.Downlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

    public boolean loggedIn = false;

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
        System.out.println();
        boolean serverActive = true;
        while(serverActive) {
            try {
                clientSocket = mss.accept();

                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                bis = new BufferedInputStream(clientSocket.getInputStream());
                bos = new BufferedOutputStream(clientSocket.getOutputStream());

                r = new Receiver(this, bis);
                r.runReceiver();
            } catch (Exception e) {
                e.printStackTrace();
            }          
        }
        try {
            mss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleData(String str) throws IOException {
        if (!loggedIn) {
            handleLogin(str);
        }
        else {
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
    }

    public void handleLogin(String str) throws SocketException, IOException {
        String p = "";

        BufferedReader br = new BufferedReader(new FileReader("/home/student/p.txt"));
        p = br.readLine();
        br.close();

        if (str.equals(p)) {
            clientSocket.setSoTimeout(0);
            this.loggedIn = true;
            sendResponse(Helpers.textToHex("Accepted"));
        }
        else {
            r.setRunning(false);
            sendResponse(Helpers.textToHex("Rejected"));
        }
    }

    public void sendResponse(String str) throws IOException {
        Helpers.sendMessage(str, bos);
    }
}

class Receiver {
    public CommandLink cl = null;
    public BufferedInputStream bis = null;

    private boolean running = true;

    private long timeUp = 0;

    public Receiver(CommandLink cl, BufferedInputStream bis) {
        this.cl = cl;
        this.bis = bis;
    }

    public void runReceiver() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            setTimeUp();
            while (running) {
                if (!cl.loggedIn && System.currentTimeMillis() >= timeUp) {
                    throw new IOException();
                }
                while (bis.available() > 0) {
                    nRead = bis.read(dataT);
                    if (nRead != -1) {
                        byte[] data = Helpers.byteCutoff(dataT, nRead);

                        dataString = new String(data, StandardCharsets.UTF_8);

                        System.out.println("Input: " + dataString);
                        System.out.println();

                        cl.handleData(dataString);
                    }
                    else {
                        break;
                    }
                }
            }
            throw new IOException();
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            System.out.println();
            e.printStackTrace();
        }
        finally {
            try {
                bis.close();
                cl.bos.close();
                cl.clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTimeUp() {
        this.timeUp = System.currentTimeMillis() + 10*1000;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }
}