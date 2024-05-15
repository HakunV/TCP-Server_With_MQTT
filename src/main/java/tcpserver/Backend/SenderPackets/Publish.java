package tcpserver.Backend.SenderPackets;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tcpserver.Backend.MQTT_PubPayload;
import tcpserver.Backend.Options.PublishOptions;
import tcpserver.Helpers.Helpers;
import tcpserver.Helpers.MQTT;

public class Publish {
    private static MQTT_PubPayload mpp = new MQTT_PubPayload();

    private static String nothingImportant = "6a2b0454-7bcb-46eb-8e77-37005d22d72c";
    
    public static String[] publish(String device, float lat, float lon, PublishOptions po) {
        String message = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", 3);
        message += packetType;

        String dup = po.isDup() ? "1" : "0";

        String qos = "";
        if (po.getQos() == 0) {
            qos = "00";
        }
        else if (po.getQos() == 1) {
            qos = "01";
        }
        else {
            qos = "10";
        }

        String retain = po.isRetain() ? "1" : "0";
        message += String.format("%01X", Integer.parseInt(dup+qos+retain, 2));

        // Variable Header

        String topic = "DTU-IWP-DeviceData";
        String topicHex = Helpers.textToHex(topic);

        int topicLength = topicHex.length()/2;
        tempMes += String.format("%04X", topicLength);
        tempMes += topicHex;

        String packetID = "";
        if (Integer.parseInt(qos) > 0) {
            packetID = MQTT.generatePacketID();
            tempMes += String.format("%04X", Integer.parseInt(packetID));
        }

        // Payload

        String payload = getJSON(device, lat, lon);
        String payloadHex = Helpers.textToHex(payload);

        tempMes += payloadHex;

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += MQTT.calcRemLen(mesLength);
        message += tempMes;

        return new String[] {packetID, message};
    }

    /*
     * The wifi_config elements are as follows:
     * [0]: IPv4 address
     * [1]: Gateway IPv4 address
     * [2]: SSID
     * [3]: BSSID
     * [4]: Channel
     * [5]: Signal aka RSSI
     */
    private static String getJSON(String device, float lat, float lon) {
        // String[] wifi_config = wc.configure();

        String mac = imeiToDeviceID(device);

        mpp.setName("");
        mpp.setMAC(mac);
        mpp.setTechnology("wifi");
        // mpp.setIP(wifi_config[0]);
        mpp.setIP("10.209.216.197");
        // mpp.setRssi(Integer.parseInt(wifi_config[5]));
        mpp.setRssi(-40);
        // mpp.setSsid(wifi_config[2]);
        mpp.setSsid("eduroam");
        mpp.setHost("192.38.81.6");
        // mpp.setGwIP(wifi_config[1]);
        mpp.setGwIP("10.209.128.1");
        // mpp.setBSSID(wifi_config[3]);
        mpp.setBSSID("10:a8:29:a4:e9:4e");
        // mpp.setChannel(Integer.parseInt(wifi_config[4]));
        mpp.setChannel(44);
        mpp.setSeq(5);
        mpp.setData(lat, lon, device);
        mpp.setAuthToken(nothingImportant);

        Gson gson = new Gson();
        String json = gson.toJson(mpp);

        System.out.println(json);
        return json;
    }

    private static String imeiToDeviceID(String device) {
        String res = "";

        // JsonArray ja = convertFileToJSON("C:/Users/mariu/Development/Bachelor/tcpserver/src/main/java/tcpserver/Devices.json");
        JsonArray ja = convertFileToJSON("./Devices.json");

        System.out.println("Array: " + ja);

        for (JsonElement je : ja) {
            System.out.println("Element: " + je);

            JsonObject jo = je.getAsJsonObject();

            if (jo.get("IMEI").getAsString().equals(device)) {
                System.out.println("Yes, it equals");
                System.out.println();
                res = jo.get("DeviceID").getAsString();
                break;
            }
        }

        return res;
    }

    private static JsonArray convertFileToJSON (String fileName){
        System.out.println("File: " + fileName);

        // Read from File to String
        JsonArray jsonArray = new JsonArray();
        
        try {
            JsonParser parser = new JsonParser();
            System.out.println("Parser: " + parser);
            JsonElement jsonElement = parser.parse(new FileReader(fileName));
            jsonArray = jsonElement.getAsJsonArray();
        } catch (FileNotFoundException e) {
           System.out.println("Could not parse file");
           e.printStackTrace();
        }
        return jsonArray;
    }
}
