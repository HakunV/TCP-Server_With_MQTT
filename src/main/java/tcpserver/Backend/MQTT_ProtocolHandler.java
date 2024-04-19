package tcpserver.Backend;

import java.io.IOException;

public class MQTT_ProtocolHandler {
    private Receiver r = null;
    private int byteSize = 2;

    private int remLenBytes = 1;

    public MQTT_ProtocolHandler(Receiver r) {
        this.r = r;
    }

    public void handleMessage(String str) {
        String protocol = str.substring(0, 1*byteSize/2);

        switch (protocol) {
            case "2":
                System.out.println("Connack Message Received:");
                System.out.println();
                handleConnack(str);
                break;
            case "3":
                System.out.println("Publish Message Received:");
                System.out.println();
                handlePublish(str);
                break;
            case "4":
                System.out.println("Puback Message Received:");
                System.out.println();
                handlePuback(str);
                break;
            case "5":
                System.out.println("Pubrec Message Received:");
                System.out.println();
                handlePubrec(str);
                break;
            case "6":
                System.out.println("Pubrel Message Received:");
                System.out.println();
                handlePubrel(str);
                break;
            case "7":
                System.out.println("Pubcomp Message Received:");
                System.out.println();
                handlePubcomp(str);
                break;
            case "9":
                System.out.println("Suback Message Received:");
                System.out.println();
                handleSuback(str);
                break;
            case "b":
                System.out.println("Unsuback Message Received:");
                System.out.println();
                handleUnSuback(str);
                break;
            case "d":
                System.out.println("Pingresp Message Received:");
                System.out.println();
                handlePingresp(str);
                break;
            default:
                System.out.println("Packet Type Not Recognized");
                System.out.println();
                break;
        }
    }

    private void handlePingresp(String str) {
        System.out.println("    The server responded");
        System.out.println();
    }

    private void handleUnSuback(String str) {
        int pointer = 0;

        // Fixed Header

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("    Length: " + length);
        System.out.println();

        pointer = pointer + (1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        pointer = pointer+2*byteSize;
    }

    private void handleSuback(String str) {
        int pointer = 0;

        // Fixed Header

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("    Length: " + length);
        System.out.println();

        pointer = pointer + (1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        pointer = pointer+2*byteSize;

        // Payload

        while (pointer/2 < 2+length) {
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
                case "83":
                    System.out.println("      Implementation Specific Error");
                    System.out.println();
                    break;
                case "87":
                    System.out.println("      Not Authorized");
                    System.out.println();
                    break;
                case "8f":
                    System.out.println("      Topic Filter Invalid");
                    System.out.println();
                    break;
                case "91":
                    System.out.println("      Packet Identifier In Use");
                    System.out.println();
                    break;
                case "97":
                    System.out.println("      Quota Exceeded");
                    System.out.println();
                    break;
                case "9e":
                    System.out.println("        Shared Subscriptions Not Supported");
                    System.out.println();
                case "a1":
                    System.out.println("        Subscription Identifiers Not Supported");
                    System.out.println();
                case "a2":
                    System.out.println("        Wildcard Subscriptions Not Supported");
                    System.out.println();
                default:
                    System.out.println("      Could Not Recognize Reason Code");
                    System.out.println();
                    break;
            }

            pointer = pointer+1*byteSize;
        }

        if(pointer < str.length()-1) {
            handleMessage(str.substring(pointer, str.length()-1));
        }
    }

    private void handlePubcomp(String str) {
        int pointer = 0;

        // Fixed Header

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("Length: " + length);
        System.out.println();

        pointer = pointer+(1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize));
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();
    }

    private void handlePubrel(String str) {
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

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("Length: " + length);
        System.out.println();

        pointer = pointer+(1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize));
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();
    }

    private void handlePubrec(String str) {
        int pointer = 0;

        // Fixed Header

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("Length: " + length);
        System.out.println();

        pointer = pointer+(1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize));
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();
    }

    private void handlePuback(String str) {
        int pointer = 0;

        // Fixed Header

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("Length: " + length);
        System.out.println();

        pointer = pointer+(1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int packetID = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        pointer = pointer+2*byteSize;

        if (pointer < length) {
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

            if (pointer < length) {
                int propLength = Integer.parseInt(str.substring(pointer, pointer+1*byteSize), 16);
                pointer = pointer+1*byteSize;

                if (propLength != 0) {
                    System.out.println("    Property: ");
    
                    String property = str.substring(pointer, pointer+1*byteSize) == "1f" ? "Reason String" : "User Property";
                    System.out.print(property);
                    System.out.println();
                }
            }
        }
    }

    private void handlePublish(String str) {
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

        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("Length: " + length);
        System.out.println();

        // Pointer gets incremented with 2 because of Remaining Length which is 1 byte
        pointer = pointer+(1+remLenBytes)*byteSize;
        remLenBytes = 1;

        // Variable Header

        int topicLength = Integer.parseInt(str.substring(pointer, pointer+2*byteSize), 16);
        pointer = pointer+2*byteSize;

        String topic = hexToString(str.substring(pointer, pointer+topicLength*byteSize));
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

        String payload = hexToString(str.substring(pointer, str.length()));
        System.out.println(payload);
        System.out.println();

        if (qos == 1) {
            r.sendPubacks(packetID, 4);
        }
        else if (qos == 2) {
            r.sendPubacks(packetID, 5);
        }
    }

    private void handleConnack(String str) {
        int length = recRemLen(str.substring(1*byteSize));
        System.out.println("Length: " + length);
        System.out.println();

        boolean session = Integer.parseInt(str.substring(2*byteSize, 3*byteSize), 16) == 1 ? true : false;
        System.out.println("    Session Present: " + session);
        System.out.println();

        String returnCode = str.substring(3*byteSize, 4*byteSize);
        System.out.println("    Return Code: ");
        switch (returnCode) {
            case "00":
                r.setConAcc(true);
                r.wakeUp();
                System.out.println("        Connection Accepted");
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
    }

    public String hexToString(String str) {
        String txt = "";
        for (int i = 0; i <= str.length()-2; i += 2) {
            String s = str.substring(i, i + 2);
            txt = txt + (char) Integer.parseInt(s, 16);
        }

        return txt;
    }

    public int recRemLen(String inLen) {
        int amountBytes = 0;

        int start = 0;
        int end = 2;

        int multiplier = 1;
        int value = 0;

        while (true) {
            int digit = Integer.parseInt(inLen.substring(start, end), 16);

            value += (digit & 127) * multiplier;
            multiplier *= 128;

            amountBytes++;

            if ((digit & 128) == 0) {
                break;
            }

            start += 2;
            end += 2;
        }

        remLenBytes = amountBytes;
        return value;
    }
}
