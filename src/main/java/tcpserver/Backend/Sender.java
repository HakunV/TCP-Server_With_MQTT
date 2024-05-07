package tcpserver.Backend;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Sender {
    private BackendClient bc = null;
    private BufferedOutputStream bos = null;
    private MQTT_PubPayload mpp = null;
    private WiFi_Config wc = null;

    private String nothingImportant = "6a2b0454-7bcb-46eb-8e77-37005d22d72c";

    private boolean pubDup = false;

    private int upscaleFactor = 10000;

    public Sender(BackendClient bc, BufferedOutputStream bos) {
        this.bc = bc;
        this.bos = bos;
        this.mpp = new MQTT_PubPayload();
        this.wc = new WiFi_Config();
    }

    public void sendConnect() {
        String tempMes = "";
        String message = "";
        
        int fixed = Integer.parseInt("00010000", 2);
        message += String.format("%02X", fixed);

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

        int flags = Integer.parseInt("11000010", 2);
        tempMes += String.format("%02X", flags);

        int aliveMSB = Integer.parseInt("00000011", 2);
        tempMes += String.format("%02X", aliveMSB);

        int aliveLSB = Integer.parseInt("11111111", 2);
        tempMes += String.format("%02X", aliveLSB);

        
        // Payload

        String client_ID = "43235";
        String id_hex = textToHex(client_ID);

        String lengthID = String.format("%04X", client_ID.length());

        tempMes += lengthID;
        tempMes += id_hex;

        String username = "dtuadmin";
        String userHex = textToHex(username);
        
        String lengthUser = String.format("%04X", username.length());

        tempMes += lengthUser;
        tempMes += userHex;

        String password = "$admiN@DTU#8024";
        String passHex = textToHex(password);

        String lengthPass = String.format("%04X", password.length());

        tempMes += lengthPass;
        tempMes += passHex;

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += String.format("%02X", mesLength);
        message += tempMes;

        try {
            sendMessage(message, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Connect Packet");
            e.printStackTrace();
        }
    }

    public void sendPublish(String device, float lat, float lon) {
        String message = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", 3);
        message += packetType;

        String dup = pubDup ? "1" : "0";
        String qos = "01";
        String retain = "0";
        message += String.format("%01X", Integer.parseInt(dup+qos+retain, 2));

        // Variable Header

        String topic = "DTU-IWP-DeviceData";
        String topicHex = textToHex(topic);

        int topicLength = topicHex.length()/2;
        tempMes += String.format("%04X", topicLength);
        tempMes += topicHex;

        if (Integer.parseInt(qos) > 0) {
            String packetID = generatePacketID();
            tempMes += String.format("%04X", Integer.parseInt(packetID));
        }

        // Payload

        String payload = getJSON(device , lat, lon);
        String payloadHex = textToHex(payload);

        tempMes += payloadHex;

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += calcRemLen(mesLength);
        message += tempMes;

        try {
            sendMessage(message, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Publish Packet");
            e.printStackTrace();
        }
    }

    private String generatePacketID() {
        int min = 10000;
        int max = 99999;

        int id = (int) (Math.random()*(max-min+1)+min);
        return Integer.toString(id);
    }

    public void sendPubacks(int packetInt, int ackType) {
        String message = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", ackType);
        message += packetType;

        if (ackType == 6) {
            String reserved = String.format("%01X", 2);
            message += reserved;
        } 
        else {
            String reserved = String.format("%01X", 0);
            message += reserved;
        }

        // Variable Header

        String packetID = String.format("%04X", packetInt);
        tempMes += packetID;

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        message += calcRemLen(mesLength);
        message += tempMes;

        try {
            sendMessage(message, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Publish Packet");
            e.printStackTrace();
        }
    }

    public void sendSubscribe() {
        String mes = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", 8);
        mes += packetType;

        String reserved = String.format("%01X", 2);
        mes += reserved;

        // Variable Header

        String packetID = String.format("%04X", 5832);
        tempMes += packetID;

        // int propLength = 0;
        // tempMes += String.format("%02X", propLength);

        // payload

        String topic = textToHex("DTU-IWP-DeviceData");
        int topicLength = topic.length()/2;
        tempMes += String.format("%04X", topicLength);
        tempMes += topic;

        int flags = Integer.parseInt("00000010", 2);
        tempMes += String.format("%02X", flags);

        // Calculate Remaining Length

        int mesLength = tempMes.length()/2;
        mes += calcRemLen(mesLength);
        mes += tempMes;

        try {
            sendMessage(mes, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Publish Packet");
            e.printStackTrace();
        }
    }

    public void sendUnsubscribe() {

    }

    public void sendPing() {
        String mes = "";

        String packetType = String.format("%01X", 12);
        mes += packetType;

        String reserved = String.format("%01X", 0);
        mes += reserved;

        String remLength = String.format("%02X", 0);
        mes += remLength;

        try {
            sendMessage(mes, bos);
        }
        catch(IOException e) {
            System.out.println("Could Not Send Ping Packet");
        }
    }

    public void sendDisconnect() {
        String mes = "";

        String packetType = String.format("%01X", 14);
        mes += packetType;

        String reserved = String.format("%01X", 0);
        mes += reserved;

        String remLength = String.format("%02X", 0);
        mes += remLength;

        try {
            sendMessage(mes, bos);
        }
        catch(IOException e) {
            System.out.println("Could Not Send Disconnect Packet");
        }
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
    public String getJSON(String device, float lat, float lon) {
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

    private String imeiToDeviceID(String device) {
        String res = "";

        JsonArray ja = convertFileToJSON("C:/Users/mariu/Development/Bachelor/tcpserver/src/main/java/tcpserver/Devices.json");

        for (JsonElement je : ja) {
            JsonObject jo = je.getAsJsonObject();

            if (jo.get("IMEI").getAsString().equals(device)) {
                res = jo.get("DeviceID").getAsString();
                break;
            }
            else {
                System.out.println("Did not find IMEI");
                System.out.println();
            }
        }

        return res;
    }

    private static JsonArray convertFileToJSON (String fileName){

        // Read from File to String
        JsonArray jsonArray = new JsonArray();
        
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(fileName));
            jsonArray = jsonElement.getAsJsonArray();
        } catch (FileNotFoundException e) {
           
        }
        return jsonArray;
    }

    public void sendMessage(String mes, BufferedOutputStream bos) throws IOException {
        System.out.println("Message: " + mes);
        System.out.println();

        byte[] mesBytes = hexStrToByteArr(mes);
        // byte[] mesBytes = mes.getBytes();

        bos.write(mesBytes);
        bos.flush();

        System.out.println("Sent packet");
        System.out.println();
    }

    public String textToHex(String s) {
        char[] userArr = s.toCharArray();
        String userHex = "";
        for (int i = 0; i < userArr.length; i++) {
            userHex += String.format("%02X", (int) userArr[i]);
        }
        return userHex;
    }

    // public String gpsPayloadToHex(String s) {
    //     int lat = 0;
    //     int lon = 0;

    //     int posLat = s.lastIndexOf("\"lat\":")+"\"lat\":".length();
    //     int posLon = s.lastIndexOf("\"lon\":")+"\"lon\":".length();

    //     int lastLat = s.lastIndexOf(",\"lon\":");
    //     int lastLon = s.lastIndexOf("},\"AuthToken");

    //     char[] userArr = s.toCharArray();
    //     String userHex = "";
    //     for (int i = 0; i < userArr.length; i++) {
    //         if (i == posLat) {
    //             String latStr = s.substring(i, lastLat);
    //             lat = Integer.parseInt(latStr);

    //             userHex += String.format("%06x", lat);
    //             i = i+latStr.length()-1;
    //         }
    //         else if (i == posLon) {
    //             String lonStr = s.substring(i, lastLon);
    //             lon = Integer.parseInt(lonStr);

    //             userHex += String.format("%06x", lon);
    //             i = i+lonStr.length()-1;
    //         }
    //         else {
    //             userHex += String.format("%02X", (int) userArr[i]);
    //         }
    //     }
    //     return userHex;
    // }

    public byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }

    public String calcRemLen(int x) {
        int xn = x;
        String res = "";

        while (xn > 0) {
            int digit = xn % 128;
            xn = xn / 128;
            if (xn > 0) {
                digit = digit | 128;
            }
            res += String.format("%02X", digit);
        }
        return res;
    }

    public static <T> T concatenate(T a, T b) {
        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            throw new IllegalArgumentException();
        }
    
        Class<?> resCompType;
        Class<?> aCompType = a.getClass().getComponentType();
        Class<?> bCompType = b.getClass().getComponentType();
    
        if (aCompType.isAssignableFrom(bCompType)) {
            resCompType = aCompType;
        } else if (bCompType.isAssignableFrom(aCompType)) {
            resCompType = bCompType;
        } else {
            throw new IllegalArgumentException();
        }
    
        int aLen = Array.getLength(a);
        int bLen = Array.getLength(b);
    
        @SuppressWarnings("unchecked")
        T result = (T) Array.newInstance(resCompType, aLen + bLen);
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);        
    
        return result;
    }
}
