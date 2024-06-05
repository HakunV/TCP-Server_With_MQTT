package tcpserver.Server;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.GT06;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    public Server server;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private ProtocolHandler ph;
    private ClientWriter cw;
    private String imei = "";

    public String isn = "0000";  // Might change to int

    private boolean clientActive = true;

    public int byteSize = Helpers.getByteSize();

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        int nRead = 0;
        byte[] dataT = new byte[1024];
        String dataString = "";

        try {
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());

            this.ph = new ProtocolHandler(this);
            
            this.cw = new ClientWriter(this, bos);
        } catch (IOException e) {
            System.out.println("Wrong when setting up");
            e.printStackTrace();
        }

        try {
            while (clientActive) {
                socket.setSoTimeout(600*1000);
                while ((nRead = bis.read(dataT)) != -1) {
                    byte[] data = Helpers.byteCutoff(dataT, nRead); // Makes a new array with the size of nRead instead of 1024
                    dataString = Helpers.byteToHex(data);

                    dataString = Helpers.removeWhiteSpace(dataString); // If there are whitespaces between bytes

                    dataString = Helpers.toLowerCase(dataString); // If the hexadecimal is uppercase

                    System.out.println("Input: " + dataString + "   " + Helpers.ts());
                    System.out.println();

                    ph.handleMessage(dataString);
                }
            }
        } catch (IOException e) {
            System.out.println("Reading from bis is failing");

            try {
                bis.close();
                bos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            cw.closeWriter();
            server.removeClient(this);

            e.printStackTrace();
        }
    }

    public void publish(float lat, float lon) {
        server.mqttClient.publish(imei, lat, lon);
    }

    public void respondToStatus(String isn) throws IOException {
        cw.respondStandard("13", isn);
    }

    public void respondToLogin(String isn) throws IOException {
        cw.respondStandard("01", isn);
    }

    public void respondToAlarm(String isn) throws IOException {
        cw.respondStandard("26", isn);
    }

    public void sendCommand(String command) throws IOException {
        cw.sendCommand(command);
    }

    public void commandResponse(String response) throws IOException {
        server.commandResponse(response);
    }

    public void sendMessage(String mes) throws IOException {
        Helpers.sendMessage(mes, bos);
    }

    public void setName(String name) {
        this.imei = name;
        System.out.println("Name Set: " + name);
        System.out.println();
        // cw.setWindowName(name);
    }

    public String getImei() {
        return this.imei;
    }

    public void checkDups() {
        server.removeDups(this.imei, this);;
    }

    public void setEndFlag(boolean flag) {
        this.clientActive = flag;
    }

    public Socket getSocket() {
        return this.socket;
    }

    
}
