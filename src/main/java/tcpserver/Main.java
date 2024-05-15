package tcpserver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tcpserver.Server.*;

import tcpserver.Backend.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server();

        server.runServer();

        // System.out.println(imeiToDeviceID("353994711603560"));

        // BackendClient backendClient = new BackendClient();

        // backendClient.publish("355688700322392", (float) 30.2324, (float) 13.2390);

        // Thread th = new Thread(backendClient);

        // th.start();
        // backendClient.publish("355688700322392", (float) 30.2324, (float) 13.2390);
        // backendClient.publish("353994711603560", (float) 47.5432, (float) 9.2342);
    }

    private static String imeiToDeviceID(String device) {
        String res = "";

        JsonArray ja = convertFileToJSON("/Users/mariu/Development/Bachelor/tcpserver/src/main/java/tcpserver/Devices.json");

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