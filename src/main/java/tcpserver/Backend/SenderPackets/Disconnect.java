package tcpserver.Backend.SenderPackets;

public class Disconnect {
    
    public static String disconnect() {
        String message = "";

        String packetType = String.format("%01X", 14);
        message += packetType;

        String reserved = String.format("%01X", 0);
        message += reserved;

        String remLength = String.format("%02X", 0);
        message += remLength;

        return message;
    }
}
