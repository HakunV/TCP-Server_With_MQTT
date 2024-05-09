package tcpserver.Backend.SenderPackets;

import tcpserver.Backend.Options.ConnectOptions;
import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Connect {

    public static String connect(ConnectOptions co) {
        String tempMes = "";
        String message = "";

        // Fixed Header
        
        int fixed = Integer.parseInt("00010000", 2);
        message += String.format("%02X", fixed);

        // Variable Header

        int protLen = 4;
        tempMes += String.format("%04X", protLen);

        int m = 'M';
        tempMes += String.format("%02X", m);

        int q = 'Q';
        tempMes += String.format("%02X", q);

        int t = 'T';
        tempMes += String.format("%02X", t);
        tempMes += String.format("%02X", t);

        int level = Integer.parseInt("00000100", 2);
        tempMes += String.format("%02X", level);

        tempMes += flags(co);

        tempMes += String.format("%04X", co.getKeepAlive());

        
        // Payload

        String client_ID = "43235";
        String id_hex = Helpers.textToHex(client_ID);

        String lengthID = String.format("%04X", client_ID.length());

        tempMes += lengthID;
        tempMes += id_hex;

        String username = "dtuadmin";
        String userHex = Helpers.textToHex(username);
        
        String lengthUser = String.format("%04X", username.length());

        tempMes += lengthUser;
        tempMes += userHex;

        String password = "$admiN@DTU#8024";
        String passHex = Helpers.textToHex(password);

        String lengthPass = String.format("%04X", password.length());

        tempMes += lengthPass;
        tempMes += passHex;

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += MQTT.calcRemLen(mesLength);
        message += tempMes;

        return message;
    }

    private static String flags(ConnectOptions co) {
        String str = "";

        String username = co.isUsername() ? "1" : "0";

        String password = co.isPassword() ? "1" : "0";

        String willRetain = co.isWillRetain() ? "1" : "0";

        String willQos = "";
        if (co.getWillQos() == 0) {
            willQos = "00";
        }
        else if (co.getWillQos() == 1) {
            willQos = "01";
        }
        else {
            willQos = "10";
        }

        String willFlag = co.isWillFlag() ? "1" : "0";

        String clean = co.isClean() ? "1" : "0";

        String flagsStr = username+password+willRetain+willQos+willFlag+clean+"0";

        int flags = Integer.parseInt(flagsStr, 2);
        str += String.format("%02X", flags);

        return str;
    }
}
