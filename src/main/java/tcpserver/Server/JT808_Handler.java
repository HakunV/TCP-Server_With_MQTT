package tcpserver.Server;

import tcpserver.Helpers.Helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JT808_Handler {
    ProtocolHandler ph;

    private int byteSize = 2;

    public JT808_Handler(ProtocolHandler ph) {
        this.ph = ph;
    }

    public void handleProtocol(String ds) {
        String dataString = ds.replace("7d02", "7e");
        dataString = dataString.replace("7d01", "7d");

        String messageId = dataString.substring(2, 6);
        String msgProps = dataString.substring(6, 10);
        String phoneNumber = dataString.substring(10, 22);
        String messageSequence = dataString.substring(22, 26);
        
        System.out.println("Message ID: " + messageId);
        System.out.println("Message Props: " + msgProps);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Message Sequence: " + messageSequence);
        System.out.println();

        String mesBody = dataString.substring(26, dataString.length()-4);

        switch (messageId) {
            case "0100":
                System.out.println("Registration Message");
                System.out.println();
                handleReg(mesBody, phoneNumber, messageSequence);
                break;
            case "0102":
                System.out.println("Authentication Message");
                System.out.println();
                handleAuth(mesBody, phoneNumber, messageSequence);
                break;
            case "0200":
                System.out.println("Location Message");
                System.out.println();
                handleLoc(mesBody);
                break;
            case "0704":
                System.out.println("Data Batch Message");
                System.out.println();
                handleBatch(mesBody);
                break;
            case "0002":
                System.out.println("Heartbeat Message");
                System.out.println();
                break;
            default:
                System.out.println("Message ID Not Known");
                System.out.println();
                break;
        }
    }

    // private String revEscape(String ds) {
    //     for (int i = 2; i < ds.length()-2; i += 2) {
    //         if (ds.substring(i, i+2).equals("02") && ds.substring(i-2, i).equals("7d")) {
    //             StringBuilder 
    //         }
    //         else if (ds.substring(i, i+2).equals("01") && ds.substring(i-2, i).equals("7d")) {

    //         }
    //     }
    // }

    private void handleBatch(String dataString) {
        int nLocItem = Integer.parseInt(dataString.substring(0, 4), 16);
        int type = Integer.parseInt(dataString.substring(4, 6), 16);

        int pointer = 6;
        for (int i = 0; i < nLocItem; i++) {
            int len = Integer.parseInt(dataString.substring(pointer, pointer+4), 16);
            pointer = pointer+4;

            String locData = dataString.substring(pointer, pointer+len*byteSize);
            handleLoc(locData);

            pointer = pointer+len*byteSize;
        }
    }

    private void handleLoc(String dataString) {
        float lat = getGPS(dataString.substring(16, 24));

        System.out.println("Latitude: " + lat);
        System.out.println();

        float lon = getGPS(dataString.substring(24, 32));

        System.out.println("Longitude: " + lon);
        System.out.println();

        ph.publish(lat, lon);

        String time = dataString.substring(44, 56);
        System.out.println("Date: " + time);
    }

    public float getGPS(String lat) {
        int l = Integer.parseInt(lat, 16);

        float lf = (float) (l/Math.pow(10, 6));

        return lf;
    }

    private void handleAuth(String dataString, String phoneNumber, String messageSequence) {
        String auth = dataString;
        System.out.println("Authentication Code: " + auth);
        System.out.println();

        String res = "";

        if (authRegistered(auth)) {
            ph.setName(getImeiByAuth(auth));
            ph.checkDups();
            res = "00";
        }
        else {
            res = "00";
        }

        String hexString = "80010005" + phoneNumber + "2f82" + messageSequence + "0102" + res;

        byte[] data2 = Helpers.hexStrToByteArr(hexString);
        String checksum = String.format("%02X", Helpers.calculateChecksum(data2));
        System.out.println("XOR Checksum: " + checksum + "\n");

        String response = "7e80010005" + phoneNumber + "2f82" + messageSequence + "0102" + res + checksum + "7e";

        try {
            ph.sendMessage(response);
        } catch (IOException e) {
            System.out.println("Could Not Send JT808");
            e.printStackTrace();
        }

        System.out.println("Sent registration response: " + response);
    }

    public String getImeiByAuth(String a) {
        BufferedReader reader;
        String res = "";

		try {
            // For Local Computer
			reader = new BufferedReader(new FileReader("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\Server\\jtTrackers.txt"));

            // For VM
            // reader = new BufferedReader(new FileReader("/home/student/TCP-Server_With_MQTT/src/main/java/tcpserver/Server/jtTrackers.txt"));

			String line = reader.readLine();

			while (line != null) {
				String[] pair = line.split(",");

                if (a.equals(pair[0])) {
                    System.out.println("Auth Exists");
                    System.out.println();

                    res = pair[1];
                    break;
                }

				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return res;
    }

    private void handleReg(String dataString, String phoneNumber, String messageSequence) {
        String imei = Helpers.hexToString(getManID(dataString)) + phoneNumber.substring(2);

        String auth = Helpers.textToHex(phoneNumber);

        String res = register(auth, imei);

        String hexString = "8100000f" + phoneNumber +"1a61"+ messageSequence +res+auth;
    
        byte[] data2 = Helpers.hexStrToByteArr(hexString);
        String checksum = String.format("%02X", Helpers.calculateChecksum(data2));
        System.out.println("XOR Checksum: " + checksum + "\n");
        String response = "7e8100000f" + phoneNumber +"1a61"+ messageSequence +res+auth+checksum+"7e";
        
        try {
            ph.sendMessage(response);
        } catch (IOException e) {
            System.out.println("Could Not Send JT808");
            e.printStackTrace();
        }
        System.out.println("Sent registration response: " + response);
    }

    public String register(String auth, String imei) {
        
        if (authRegistered(auth)) {
            return "03";
        }

        try(FileWriter fw = new FileWriter("jtTrackers.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(auth + "," + imei);

            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ph.setName(imei);
        ph.checkDups();

        return "00";
    }

    public boolean authRegistered(String auth) {
        BufferedReader reader;
        boolean result = false;

		try {
			// For Local Computer
			reader = new BufferedReader(new FileReader("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\Server\\jtTrackers.txt"));

            // For VM
            // reader = new BufferedReader(new FileReader("/home/student/TCP-Server_With_MQTT/src/main/java/tcpserver/Server/jtTrackers.txt"));
			String line = reader.readLine();

			while (line != null) {
				String[] pair = line.split(",");

                if (auth.equals(pair[0])) {
                    System.out.println("Auth Exists");
                    System.out.println();

                    result = true;
                    break;
                }

				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return result;
    }

    public String getManID(String str) {
        return str.substring(8, 18);
    }
}
