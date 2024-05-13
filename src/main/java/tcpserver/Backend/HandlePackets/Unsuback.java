package tcpserver.Backend.HandlePackets;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Unsuback {
    private static int byteSize = Helpers.getByteSize();
    
    public static void unsuback(String str) {
        int pointer = 0;

        // Fixed Header

        int[] remLenRes = MQTT.recRemLen(str.substring(1*byteSize));
        System.out.println("    Length: " + remLenRes[0]);
        System.out.println();

        pointer = pointer + remLenRes[1]*byteSize;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        pointer = pointer+2*byteSize;
    }
}
