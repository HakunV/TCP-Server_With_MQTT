package tcpserver.Server;

import tcpserver.Helpers.GT06;

import java.io.IOException;

public class ProtocolHandler {
    // Reference to the associated client handler
    ClientHandler client;
    // Instances of protocol handlers for GT06 and JT808 protocols
    private GT06_Handler gh;
    private JT808_Handler jh;

    // Defines the size of byte segments used in parsing the data
    private int byteSize = 2;

    // Constructor to initialize the protocol handler with a client handler
    public ProtocolHandler(ClientHandler client) {
        this.client = client;
        this.gh = new GT06_Handler(this);
        this.jh = new JT808_Handler(this);
    }

    // Method to handle incoming messages and route them to the appropriate protocol handler
    public void handleMessage(String dataString) {
        // Determine the protocol based on the data string
        String protocol = determineProtocol(dataString);

        switch (protocol) {
            case "GT06":
                // Handle GT06 protocol messages
                System.out.println("GT06 Message");
                System.out.println();
                client.resetShutdownTime();
                handleGT06Message(dataString);
                break;
            case "JT808":
                // Handle JT808 protocol messages
                System.out.println("JT808 Message");
                System.out.println();
                client.resetShutdownTime();
                jh.handleProtocol(dataString);
                break;
            default:
                // Handle unknown protocol messages
                System.out.println("Unknown protocol");
                break;
        }
    }

    // Method to handle GT06 specific messages
    private void handleGT06Message(String dataString) {
        // Parsing and handling GT06 message logic
        int packetLength = 0;
        String protocolNum = "";
        String errCheck = "";

        int len = dataString.length();

        // Extract and parse the packet length
        packetLength = Integer.parseInt(dataString.substring(2 * byteSize, 3 * byteSize), 16);
        System.out.println("Length of the packet: " + packetLength);
        System.out.println();

        // Extract the protocol number
        protocolNum = dataString.substring(3 * byteSize, 4 * byteSize);

        // Extract the information serial number and assign it to the client
        client.isn = dataString.substring(len - 6 * byteSize, len - 4 * byteSize);
        System.out.println("Information Serial Number: " + client.isn);
        System.out.println();

        // Extract and check for errors
        errCheck = dataString.substring(len - 4 * byteSize, len - 2 * byteSize);
        System.out.println(GT06.errorCheck(dataString.substring(4, len - 4 * byteSize), errCheck)); // Checks the error-check with CRC-ITU
        System.out.println();

        // Handle the protocol using the GT06 handler
        gh.handleProtocol(protocolNum, dataString);
    }

    // Method to determine the protocol type based on the message content
    public String determineProtocol(String dataString) {
        // GT06 messages start with "7878" or "7979"
        if (dataString.startsWith("7878")) {
            return "GT06";
        } else if (dataString.startsWith("7979")) {
            return "GT06";
        }
        // JT808 messages start with "7e"
        else if (dataString.startsWith("7e")) {
            return "JT808";
        } else {
            return "Unknown";
        }
    }

    // Various methods to interact with the client handler and perform actions
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

    public void sendMessage(String mes) throws IOException {
        client.sendMessage(mes);
    }
}
