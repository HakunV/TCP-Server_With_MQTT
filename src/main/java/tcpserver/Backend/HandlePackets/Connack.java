package tcpserver.Backend.HandlePackets;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Connack {
    private static int byteSize = Helpers.getByteSize();
    
    public static boolean connack(String str) {
        int pointer = 1*byteSize;

        boolean accepted = false;

        int[] remLenRes = MQTT.recRemLen(str.substring(pointer));
        System.out.println("Length: " + remLenRes[0]);
        System.out.println();

        pointer = pointer + remLenRes[1]*byteSize;

        boolean session = Integer.parseInt(str.substring(pointer, pointer+1*byteSize), 16) == 1 ? true : false;
        System.out.println("    Session Present: " + session);
        System.out.println();

        pointer = pointer+1*byteSize;

        String returnCode = str.substring(pointer, pointer+1*byteSize);
        System.out.println("    Return Code: ");
        switch (returnCode) {
            case "00":
                accepted = true;
                System.out.print("    Connection Accepted");
                System.out.println();
                break;
            case "81":
                System.out.print("      Malformed Packet");
                System.out.println();
                break;
            case "82":
                System.out.print("      Protocol Error");
                System.out.println();
                break;
            case "84":
                System.out.print("      Unsupported Protocol Version");
                System.out.println();
                break;
            case "85":
                System.out.print("      Client Identifier Not Valid");
                System.out.println();
                break;
            case "86":
                System.out.print("      Bad Username Or Password");
                System.out.println();
                break;
            case "95":
                System.out.print("      Packet Too Large");
                System.out.println();
                break;
            case "8a":
                System.out.print("      Banned");
                System.out.println();
                break;
            default:
                System.out.print("      Could Not Recognize Reason Code");
                System.out.println();
                break;
        }

        pointer = pointer+1*byteSize;

        return accepted;
    }
}
