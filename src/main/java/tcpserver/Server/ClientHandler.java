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
    private GT06_Handler ph;
    private ClientWriter cw;
    private String imei = "";

    private boolean clientActive = true;

    public String isn = "0000";  // Might change to int

    public int byteSize = Helpers.getByteSize();

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        int nRead = 0;
        byte[] dataT = new byte[1024];
        String dataString = "";
        int packetLength = 0;
        String protocolNum = "";
        String errCheck = "";

        try {
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());

            this.ph = new GT06_Handler(this);
            this.cw = new ClientWriter(this, bos);
        } catch (IOException e) {
            System.out.println("Wrong when setting up");
            e.printStackTrace();
        }

        try {
            while (clientActive) {
                while ((nRead = bis.read(dataT)) != -1) {
                    byte[] data = Helpers.byteCutoff(dataT, nRead); // Makes a new array with the size of nRead instead of 1024
                    dataString = Helpers.byteToHex(data);

                    dataString = Helpers.removeWhiteSpace(dataString); // If there are whitespaces between bytes

                    dataString = Helpers.toLowerCase(dataString); // If the hexadecimal is uppercase

                    System.out.println("Input: " + dataString + "   " + Helpers.ts());
                    System.out.println();

                    int len = dataString.length();

                    /*
                     * For GT06, the packet should always start with 7878 or 7979
                     */

                    /*
                    * For JT808 the packet should start with 7E
                    */
                    if (dataString.substring(0, 2*byteSize).equals("7878") || dataString.substring(0, 2*byteSize).equals("7979")) {
                        packetLength = Integer.parseInt(dataString.substring(2*byteSize, 3*byteSize), 16);
                        System.out.println("Length of the packet: " + packetLength);
                        System.out.println();

                        protocolNum = dataString.substring(3*byteSize, 4*byteSize);

                        // isn = Integer.parseInt(dataString.substring(len-6*byteSize, len-4*byteSize), 16);   // When isn is of type int
                        isn = dataString.substring(len-6*byteSize, len-4*byteSize);
                        System.out.println("Information Serial Number: " + isn);
                        System.out.println();

                        errCheck = dataString.substring(len-4*byteSize, len-2*byteSize);

                        System.out.println(GT06.errorCheck(dataString.substring(4, len-4*byteSize), errCheck)); // Checks the error-check with CRC-ITU
                        System.out.println();

                        ph.handleProtocol(protocolNum, dataString);
                    }
                    else {
                        System.out.println("Wrong start");
                    }
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

    public void setName(String name) {
        this.imei = name;
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
