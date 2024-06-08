package tcpserver;

import tcpserver.Helpers.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Class to handle retrieving a list of devices from a server
public class DeviceList implements Runnable {
    // URL to get all devices for a user
    public URL url = new URL("http://www.thingsofinter.net:5001/device/getAllDevicesForUser");

    // Constructor that can throw a MalformedURLException
    public DeviceList() throws MalformedURLException {}

    // Method to run when the thread is started
    public void run() {
        try {
            // Open a connection to the URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET"); // Set the request method to GET

            // Set request properties for the connection
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("AuthToken", "6a2b0454-7bcb-46eb-8e77-37005d22d72c");

            // Create a BufferedReader to read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            StringBuilder content = new StringBuilder(); // Use StringBuilder to collect response content

            // Read the response line by line
            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            // Update the device list using a helper method
            Helpers.updateDeviceList(Helpers.stringToJsonArray(content.toString()));

            br.close();

            // Disconnect the HTTP connection
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
