package tcpserver.Backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;

public class WiFi_Config {
    
    public WiFi_Config() {

    }

    // Method to configure WiFi and retrieve information
    public String[] configure() {
        // getPublicIPAddress();
        String[] gw = getGateway(); // Get gateway information
        String[] wifi = getWiFiInfo(); // Get WiFi information

        String[] res = new String[gw.length + wifi.length]; // Combine gateway and WiFi information into one array

        int j = 0;
        for (int i = 0; i < gw.length; i++) {
            res[i] = gw[i]; // Add gateway information to result array
            j++;
        }
        for (int i = 0; i < wifi.length; i++) {
            res[j] = wifi[i]; // Add WiFi information to result array
            j++;
        }
        return res; // Return combined information
    }

    // Method to retrieve WiFi information
    private String[] getWiFiInfo() {
        String[] elems = new String[4]; // Array to store WiFi information

        try {
            // For Windows
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show interface \"Wi-Fi\"");
            // For Linux
            // ProcessBuilder pb = new ProcessBuilder("bash", "-c", "netsh wlan show interface \"Wi-Fi\"");
            pb.redirectErrorStream(true);
        
            BufferedReader br = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));

            String line = "";
            while ((line = br.readLine()) != null) {
                // System.out.println("Input: " + line);
                // System.out.println();
                
                if (line.contains(" SSID")) {
                    String ssid = line.split(": ")[1];
                    System.out.println("SSID: " + ssid);
                    System.out.println();
                    elems[0] = ssid;
                }
                else if (line.contains("BSSID")) {
                    String bssid = line.split(": ")[1];
                    System.out.println("BSSID: " + bssid);
                    System.out.println();
                    elems[1] = bssid;
                }
                else if (line.contains("Channel")) {
                    int channel = Integer.parseInt(line.split(": ")[1]);
                    System.out.println("Channel: " + channel);
                    System.out.println();
                    elems[2] = Integer.toString(channel);
                }
                else if (line.contains("Signal")) {
                    String sigStrength = line.split(": ")[1].substring(0, 2);
                    // int rssi = -mapRange(Integer.parseInt(sigStrength), 1, 100, -20, -88);

                    int rssi = 0;
                    if (Integer.parseInt(sigStrength) >= 97 || Integer.parseInt(sigStrength) <= 65 && Integer.parseInt(sigStrength) >= 50) {
                        rssi = Integer.parseInt(sigStrength)-123;
                    }
                    else if (Integer.parseInt(sigStrength) <= 96 && Integer.parseInt(sigStrength) >= 66) {
                        rssi = Integer.parseInt(sigStrength)-128;
                    }
                    else if (Integer.parseInt(sigStrength) <= 49 && Integer.parseInt(sigStrength) >= 40) {
                        rssi = Integer.parseInt(sigStrength)-117;
                    }
                    else if (Integer.parseInt(sigStrength) <= 39 && Integer.parseInt(sigStrength) >= 30) {
                        rssi = Integer.parseInt(sigStrength)-112;
                    }
                    else if (Integer.parseInt(sigStrength) <= 29 && Integer.parseInt(sigStrength) >= 20) {
                        rssi = Integer.parseInt(sigStrength)-107;
                    }
                    else if (Integer.parseInt(sigStrength) <= 19 && Integer.parseInt(sigStrength) >= 10) {
                        rssi = Integer.parseInt(sigStrength)-102;
                    }
                    else {
                        rssi = Integer.parseInt(sigStrength)-96;
                    }

                    System.out.println("RSSI: " + rssi);
                    System.out.println();
                    elems[3] = Integer.toString(rssi);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed When Getting WiFi Stuff");
            e.printStackTrace();
        }
        return elems; // Return WiFi information
    }

    // private String getPublicIPAddress() {
    //     String host = "";

    //     try {
    //         ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "curl ifconfig.me");
    //         pb.redirectErrorStream(true);
            
    //         BufferedReader br = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
    //         String line = "";
    //         while ((line = br.readLine()) != null) {
    //             // System.out.println("Input: " + line);
    //             // System.out.println();

    //             if (line.contains(".")) {
    //                 host = line;
    //                 System.out.println("Host: " + host);
    //                 System.out.println();
    //             }
    //         }
    //     }
    //     catch (IOException e) {
    //         System.out.println("Failed When Getting IP-address");
    //         e.printStackTrace();
    //     }
    //     return host;
    // }

    // Method to retrieve gateway information
    private String[] getGateway() {
        boolean correctAdapter = false; // Flag to indicate correct network adapter
        String[] elems = new String[2]; // Array to store gateway information

        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "ipconfig"); // Command to retrieve network information
            pb.redirectErrorStream(true);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                // System.out.println("Input: " + line);
                // System.out.println();

                if (line.contains("Wi-Fi")) {
                    correctAdapter = true; // Set flag to true if Wi-Fi adapter is found
                }

                if (correctAdapter && line.contains("IPv4 Address")) { // Fetch IPv4 address
                    String ipv4 = line.split(": ")[1];
                    System.out.println("IP-address: " + ipv4);
                    System.out.println();
                    elems[0] = ipv4;
                }
                else if (correctAdapter && line.contains("Default Gateway")) { // Fetch address of gateway
                    String gateway = line.split(": ")[1];
                    System.out.println("Gateway: " + gateway);
                    System.out.println();
                    elems[1] = gateway;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed When Getting Gateway");
            e.printStackTrace();
        }
        return elems;
    }
}
