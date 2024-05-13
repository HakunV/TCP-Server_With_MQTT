package tcpserver.Backend.SenderPackets;

import tcpserver.Helpers.MQTT;

public class Pubacks {
    
    public static String pubacks(int packetInt, int ackType) {
        String message = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", ackType);
        message += packetType;

        if (ackType == 6) {
            String reserved = String.format("%01X", 2);
            message += reserved;
        } 
        else {
            String reserved = String.format("%01X", 0);
            message += reserved;
        }

        // Variable Header

        String packetID = String.format("%04X", packetInt);
        tempMes += packetID;

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += MQTT.calcRemLen(mesLength);
        message += tempMes;

        return message;
    }
}
