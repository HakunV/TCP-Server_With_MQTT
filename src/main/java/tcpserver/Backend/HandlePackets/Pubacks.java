package tcpserver.Backend.HandlePackets;

import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Pubacks {
    private static int byteSize = Helpers.getByteSize();

    /*
     * Packet Types of Publish Acknowledgements
     * Puback = 4, Pubrec = 5, Pubrel = 6, Pubcomp = 7
     */
    public static int pubacks(String str) {
        int pointer = 1*byteSize;

        // Fixed Header

        int[] remLenRes = MQTT.recRemLen(str.substring(pointer));
        System.out.println("Length: " + remLenRes[0]);
        System.out.println();

        pointer = pointer+remLenRes[1]*byteSize;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        pointer = pointer+2*byteSize;

        if (pointer/2 < 2+remLenRes[0]) {
            String reasonCode = str.substring(pointer, pointer+1*byteSize);
            System.out.println("    Reason Code: ");
            switch (reasonCode) {
                case "00":
                    System.out.print("Success");
                    System.out.println();
                    System.out.println();
                    break;
                case "10":
                    System.out.print("No Matchig Subscribers");
                    System.out.println();
                    System.out.println();
                    break;
                case "80":
                    System.out.print("Unspecified Error");
                    System.out.println();
                    System.out.println();
                    break;
                case "83":
                    System.out.print("Implementation Specific Error");
                    System.out.println();
                    System.out.println();
                    break;
                case "87":
                    System.out.print("Not Authorized");
                    System.out.println();
                    System.out.println();
                    break;
                case "90":
                    System.out.print("Topic Name Invalid");
                    System.out.println();
                    System.out.println();
                    break;
                case "91":
                    System.out.print("Packet Identifier In Use");
                    System.out.println();
                    System.out.println();
                    break;
                case "97":
                    System.out.print("Quota Exceeded");
                    System.out.println();
                    System.out.println();
                    break;
                case "99":
                    System.out.print("Payload Format Invalid");
                    System.out.println();
                    System.out.println();
                    break;
                default:
                    System.out.print("Could Not Recognize Reason Code");
                    System.out.println();
                    System.out.println();
                    break;
            }

            pointer = pointer+1*byteSize;
        }

        return packetID;
    }
}
