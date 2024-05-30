package tcpserver.Server;

import tcpserver.Helpers.GT06;
import tcpserver.Helpers.Helpers;

import java.io.IOException;

public class ProtocolHandler {
    ClientHandler client;
    private GT06_Handler gh;

    private int byteSize = 2;

    public ProtocolHandler(ClientHandler client) {
        this.client = client;
        this.gh = new GT06_Handler(this);
    }

    public void handleMessage(String dataString) {
        String protocol = determineProtocol(dataString);

        switch (protocol) {
            case "GT06":
                System.out.println("GT06 Message");
                System.out.println();
                handleGT06Message(dataString);
                break;
            case "JT808":
                System.out.println("JT808 Message");
                System.out.println();
                handleJT808Message(dataString);
                break;
            default:
                System.out.println("Unknown protocol");
                break;
        }
    }

    private void handleJT808Message(String dataString) {
        String messageId = dataString.substring(2, 6);
        String msgProps = dataString.substring(6, 10);
        String phoneNumber = dataString.substring(10, 22);
        String messageSequence = dataString.substring(22, 26);
        
        System.out.println("Message ID: " + messageId);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Message Props: " + msgProps);
        System.out.println("Message Sequence: " + messageSequence);
        System.out.println();

        if (messageId.equals("0100")) {
            String phoneStr = Helpers.textToHex(phoneNumber);
            String hexString = "8100000f" + phoneNumber +"1a61"+ messageSequence +"00"+phoneStr;
        
            byte[] data2 = Helpers.hexStrToByteArr(hexString);
            String checksum = String.format("%02X", Helpers.calculateChecksum(data2));
            System.out.println("XOR Checksum: " + checksum + "\n");
            String response = "7e8100000f" + phoneNumber +"1a61"+ messageSequence +"00"+phoneStr+checksum+"7e";
            
            try {
                client.sendMessage(response);
            } catch (IOException e) {
                System.out.println("Could Not Send JT808");
                e.printStackTrace();
            }
            // bos.write(Helpers.hexStrToByteArr(response));
            // bos.flush();
            System.out.println("Sent registration response: " + response);
        }
        else if (messageId.equals("0102")) {
            String hexString = "80010005" + phoneNumber + "2f82" + messageSequence + "0102" + "00";

            byte[] data2 = Helpers.hexStrToByteArr(hexString);
            String checksum = String.format("%02X", Helpers.calculateChecksum(data2));
            System.out.println("XOR Checksum: " + checksum + "\n");

            String response = "7e80010005" + phoneNumber + "2f82" + messageSequence + "0102" + "00" + checksum + "7e";

            try {
                client.sendMessage(response);
            } catch (IOException e) {
                System.out.println("Could Not Send JT808");
                e.printStackTrace();
            }

            System.out.println("Sent registration response: " + response);
        }
        else if (messageId.equals("0200")) {
            float lat = getGPS(dataString.substring(42, 50));

            System.out.println("Latitude: " + lat);
            System.out.println();

            float lon = getGPS(dataString.substring(50, 58));

            System.out.println("Longitude: " + lon);
            System.out.println();

            publish(lat, lon);
        }
        else if (messageId.equals("0002")) {

        }
    }

    public float getGPS(String lat) {
        int l = Integer.parseInt(lat, 16);

        float lf = (float) (l/Math.pow(10, 6));

        return lf;
    }

    private void handleGT06Message(String dataString) {
        // gt06 logikken
        int packetLength = 0;
        String protocolNum = "";
        String errCheck = "";

        int len = dataString.length();

        packetLength = Integer.parseInt(dataString.substring(2*byteSize, 3*byteSize), 16);
        System.out.println("Length of the packet: " + packetLength);
        System.out.println();

        protocolNum = dataString.substring(3*byteSize, 4*byteSize);

        // isn = Integer.parseInt(dataString.substring(len-6*byteSize, len-4*byteSize), 16);   // When isn is of type int
        client.isn = dataString.substring(len-6*byteSize, len-4*byteSize);
        System.out.println("Information Serial Number: " + client.isn);
        System.out.println();

        errCheck = dataString.substring(len-4*byteSize, len-2*byteSize);

        System.out.println(GT06.errorCheck(dataString.substring(4, len-4*byteSize), errCheck)); // Checks the error-check with CRC-ITU
        System.out.println();

        gh.handleProtocol(protocolNum, dataString);
    }

    // Method to determine message protocol
    public String determineProtocol(String dataString) {
        // GT06 message starts with "7878"
        if (dataString.startsWith("7878")) {
            return "GT06";
        }

        if (dataString.startsWith("7979")) {
            return "GT06";
        }
        // JT808 message starts with specified message type identifier
        else if (dataString.startsWith("7e")) {
            return "JT808";
        } else {
            return "Unknown";
        }
    }

    public String getIsn() {
        return client.isn;
    }

    public void commandResponse(String com) throws IOException {
        client.commandResponse(com);
    }

    public void setName(String name) {
        client.setName(name);
    }

    public void checkDups() {
        client.checkDups();
    }

    public void respondToLogin(String r) throws IOException {
        client.respondToLogin(r);
    }

    public void publish(float lat, float lon) {
        client.publish(lat, lon);
    }

    public void respondToStatus(String r) throws IOException {
        client.respondToStatus(r);
    }

    public void respondToAlarm(String r) throws IOException {
        client.respondToAlarm(r);
    }
}
