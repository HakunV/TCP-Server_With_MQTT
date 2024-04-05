package tcpserver.Server;

import java.io.*;

public class ClientWriter {
    private ClientHandler client;
    private BufferedOutputStream output;
    // private Window window;
    

    public ClientWriter(ClientHandler client, BufferedOutputStream bos) {
        this.client = client;
        this.output = bos;

        // window = new Window(this);
        // Thread tw = new Thread(window);

        // tw.start();
    }

    public void closeWriter() {
        // window.closeWindow();
    }

    public void respondStandard(String prot, String isn) throws IOException{
        String respond = "";

        String protNum = prot;
        String serialNum = isn;
        int packLenInt = (protNum.length() + serialNum.length())/2 + 2;
        String packLenStr = String.format("%02X", packLenInt);
        
        respond = packLenStr + protNum + serialNum;
        String crc = client.crcCalc(respond);
        respond += crc;

        respond = client.addStartEnd(respond);
        System.out.println("Respond: " + respond);
        System.out.println();

        byte[] bArr = client.hexStrToByteArr(respond);

        output.write(bArr);
        output.flush();
    }

    public void sendCommand(String str) throws IOException {
        String respond = "";

        String protNum = "80";
        String serverFlags = "00000001";
        String command = getCommand(str);

        String language = "0002";

        int isnInt = Integer.parseInt(client.isn, 16);
        String serNum = String.format("%04X", isnInt+1);

        int commandLen = (serverFlags.length()+command.length())/2;
        String comLenStr = String.format("%02X", commandLen);

        respond = protNum + comLenStr + serverFlags + command + language + serNum;

        int packLenInt = respond.length()/2+2;
        String packLenStr = String.format("%02X", packLenInt);

        respond = packLenStr + respond;
        String crc = client.crcCalc(respond);
        respond += crc;

        respond = client.addStartEnd(respond);

        System.out.println("Final Command Message: " + respond);
        System.out.println();

        byte[] bArr = client.hexStrToByteArr(respond);

        output.write(bArr);
        output.flush();
    }

    public String getCommand(String str) {
        String hexStr = "";
        for (char c : str.toCharArray()) {
            hexStr += String.format("%H", c);
        }

        hexStr.replace(" ", "");

        System.out.println("Hex String: " + hexStr);
        System.out.println();
        return hexStr;
    }

    // public void setWindowName(String name) {
    //     window.setTitle("Terminal: " + name);
    // }
}
