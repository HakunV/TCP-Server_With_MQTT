package tcpserver.Backend;

import tcpserver.Backend.CommunicationFlow.ComFlow;
import tcpserver.Backend.HandlePackets.*;

public class MQTT_ProtocolHandler {
    private Receiver r = null;
    private int byteSize = 2;

    public MQTT_ProtocolHandler(Receiver r) {
        this.r = r;
    }

    public void handleMessage(String str) {
        String protocol = str.substring(0, 1*byteSize/2);

        switch (protocol) {
            case "2":
                System.out.println("Connack Message Received:");
                System.out.println();
                if (Connack.connack(str)) {
                    r.setConAcc(true);
                    r.wakeUp();
                }
                else {
                    r.setRetryMQTT(true);
                }
                break;
            case "3":
                System.out.println("Publish Message Received:");
                System.out.println();
                int[] pubRes = Publish.publish(str);
                if (pubRes[1] == 1) {
                    r.getComFlow().createFlow(false, pubRes[0], pubRes[1], "");
                }
                else if (pubRes[1] == 2) {
                    r.getComFlow().createFlow(false, pubRes[0], pubRes[1], "");
                }
                break;
            case "4":
                System.out.println("Puback Message Received:");
                System.out.println();
                r.getComFlow().update(Pubacks.pubacks(str), 4);
                break;
            case "5":
                System.out.println("Pubrec Message Received:");
                System.out.println();
                r.getComFlow().update(Pubacks.pubacks(str), 5);
                break;
            case "6":
                System.out.println("Pubrel Message Received:");
                System.out.println();
                r.getComFlow().update(Pubacks.pubacks(str), 6);
                break;
            case "7":
                System.out.println("Pubcomp Message Received:");
                System.out.println();
                r.getComFlow().update(Pubacks.pubacks(str), 7);
                break;
            case "9":
                System.out.println("Suback Message Received:");
                System.out.println();
                String mes = Suback.suback(str);
                if (mes.length() > 0) {
                    handleMessage(mes);
                }
                break;
            case "b":
                System.out.println("Unsuback Message Received:");
                System.out.println();
                Unsuback.unsuback(str);
                break;
            case "d":
                System.out.println("Pingresp Message Received:");
                System.out.println();
                Pingresp.pingresp();
                break;
            default:
                System.out.println("Packet Type Not Recognized");
                System.out.println();
                break;
        }
    }
}
