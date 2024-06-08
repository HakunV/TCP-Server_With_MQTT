package tcpserver.Backend;

import tcpserver.Backend.HandlePackets.*;

// Class for handling MQTT protocol messages
public class MQTT_ProtocolHandler {
    private Receiver r = null;
    private int byteSize = 2;

    // Constructor to initialize MQTT_ProtocolHandler with a receiver
    public MQTT_ProtocolHandler(Receiver r) {
        this.r = r;
    }

    // Method to handle incoming MQTT protocol messages
    public void handleMessage(String str) {
        // Extract protocol identifier from the received message
        String protocol = str.substring(0, 1 * byteSize / 2);

        // Switch statement to handle different protocol types
        switch (protocol) {
            case "2": // Connack Message
                System.out.println("Connack Message Received:");
                System.out.println();
                if (Connack.connack(str)) { // Check if connection is accepted
                    r.setConAcc(true); // Connection accepted
                    r.wakeUp(); // Wake up to proceed connection
                } else {
                    r.setRetryMQTT(true); // Retry MQTT connection if connection is not accepted
                }
                break;
            case "3": // Publish Message
                System.out.println("Publish Message Received:");
                System.out.println();
                int[] pubRes = Publish.publish(str); // Process the Publish message
                if (pubRes[1] == 1) { // Check if QoS level is 1
                    r.getComFlow().createFlow(false, pubRes[0], pubRes[1], ""); // Create communication flow for QoS 1
                } else if (pubRes[1] == 2) { // Check if QoS level is 2
                    r.getComFlow().createFlow(false, pubRes[0], pubRes[1], ""); // Create communication flow for QoS 2
                }
                break;
            case "4": // Puback Message
            case "5": // Pubrec Message
            case "6": // Pubrel Message
            case "7": // Pubcomp Message
                System.out.println("Pub Message Received:");
                System.out.println();
                r.getComFlow().update(Pubacks.pubacks(str), Integer.parseInt(protocol, 16)); // Update communication flow
                break;
            case "9": // Suback Message
                System.out.println("Suback Message Received:");
                System.out.println();
                String mes = Suback.suback(str); // Process the Suback message
                if (mes.length() > 0) { // Check if there are additional messages to handle
                    handleMessage(mes); // Handle additional messages
                }
                break;
            case "b": // Unsuback Message
                System.out.println("Unsuback Message Received:");
                System.out.println();
                Unsuback.unsuback(str); // Process the Unsuback message
                break;
            case "d": // Pingresp Message
                System.out.println("Pingresp Message Received:");
                System.out.println();
                Pingresp.pingresp(); // Process the Pingresp message
                break;
            default: // Default case if packet type is not recognized
                System.out.println("Packet Type Not Recognized");
                System.out.println();
                break;
        }
    }
}
