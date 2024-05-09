package tcpserver.Backend.HandlePackets;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Publish {
    private static int byteSize = Helpers.getByteSize();
    
    public static int publish(String str) {
        int pointer = 0;

        // Fixed Header

        int flagsInt = Integer.parseInt(str.substring(pointer+1*byteSize/2, pointer+1*byteSize), 16);
        String flags = String.format("%4s", Integer.toBinaryString(flagsInt)).replace(" ", "0");

        boolean dup = flags.substring(0, 1).equals("0") ? false : true;
        System.out.println("    Duplicate: " + dup);
        System.out.println();

        int qos = Integer.parseInt(flags.substring(1, 3), 2);
        System.out.println("    QoS: " + qos);
        System.out.println();

        boolean retain = flags.substring(3, 4).equals("0") ? false : true;
        System.out.println("    Retain: " + retain);
        System.out.println();

        pointer = pointer+1*byteSize;

        int[] remLenRes = MQTT.recRemLen(str.substring(pointer));
        System.out.println("Length: " + remLenRes[0]);
        System.out.println();

        // Pointer gets incremented with 2 because of Remaining Length which is 1 byte
        pointer = pointer+remLenRes[1]*byteSize;

        // Variable Header

        int topicLength = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        pointer = pointer+2*byteSize;

        String topic = Helpers.hexToString(str.substring(pointer, pointer+topicLength*byteSize));
        System.out.println("    Topic: " + topic);
        System.out.println();
        pointer = pointer+topicLength*byteSize;

        int packetID = 0;
        if (qos > 0) {
            packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
            System.out.println("    Packet identifier: " + packetID);
            System.out.println();
            pointer = pointer+2*byteSize;
        }

        // Payload

        String payload = Helpers.hexToString(str.substring(pointer, str.length()));
        System.out.println(payload);
        System.out.println();

        return qos;
    }
}
