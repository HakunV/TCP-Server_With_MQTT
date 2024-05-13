package tcpserver.Backend.SenderPackets;

import tcpserver.Backend.Options.SubscribeOptions;
import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Subscribe {
    
    public static String[] subscribe(String[] topics, SubscribeOptions so) {
        String message = "";
        String tempMes = "";


        // Fixed Header

        String packetType = String.format("%01X", 8);
        message += packetType;

        String reserved = String.format("%01X", 2);
        message += reserved;


        // Variable Header

        String packetID = MQTT.generatePacketID();
        tempMes += String.format("%04X", Integer.parseInt(packetID));


        // payload

        for (int i = 0; i < topics.length; i++) {
            String topic = Helpers.textToHex(topics[i]);
            int topicLength = topic.length()/2;
            tempMes += String.format("%04X", topicLength);
            tempMes += topic;

            String qos = "";
            if (so.getQos()[i] == 0) {
                qos = "00";
            }
            else if (so.getQos()[i] == 1) {
                qos = "01";
            }
            else {
                qos = "10";
            }

            int flags = Integer.parseInt("000000"+qos, 2);
            tempMes += String.format("%02X", flags);
        }

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += MQTT.calcRemLen(mesLength);
        message += tempMes;

        return new String[] {packetID, message};
    }
}
