package tcpserver;

import tcpserver.Helpers.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Task implements Runnable {
    public URL url = new URL("http://www.thingsofinter.net:5001/device/getAllDevicesForUser");

    public Task() throws MalformedURLException {}

    public void run() {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("AuthToken", "6a2b0454-7bcb-46eb-8e77-37005d22d72c");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            StringBuilder content = new StringBuilder();

            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            Helpers.updateDeviceList(Helpers.stringToJsonArray(content.toString()));

            br.close();

            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
