package tcpserver.Backend.HandlePackets;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Suback {
    private static int byteSize = Helpers.getByteSize();

    public static String suback(String str) {
        int pointer = 1*byteSize;

        // Fixed Header

        int[] remLenRes = MQTT.recRemLen(str.substring(pointer));
        System.out.println("    Length: " + remLenRes[0]);
        System.out.println();

        pointer = pointer + remLenRes[1]*byteSize;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        pointer = pointer+2*byteSize;

        // Payload

        while (pointer/2 < 2+remLenRes[0]) {
            String reasonCode = str.substring(pointer, pointer+1*byteSize);
            System.out.println("    Reason Code: ");

            switch (reasonCode) {
                case "00":
                    System.out.println("      QoS 0 Granted");
                    System.out.println();
                    break;
                case "01":
                    System.out.println("      QoS 1 Granted");
                    System.out.println();
                    break;
                case "02":
                    System.out.println("      QoS 2 Granted");
                    System.out.println();
                    break;
                case "80":
                    System.out.println("      Unspecified Error");
                    System.out.println();
                    break;
                default:
                    System.out.println("      Could Not Recognize Reason Code");
                    System.out.println();
                    break;
            }

            pointer = pointer+1*byteSize;
        }

        if(pointer < str.length()-1) {
            return str.substring(pointer, str.length()-1);
        }
        else {
            return "";
        }
    }
}
