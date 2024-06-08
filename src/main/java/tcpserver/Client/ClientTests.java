package tcpserver.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// ClientTests class to test client functionality by creating multiple ClientDummy instances
public class ClientTests {
    // Hexadecimal strings used as headers for messages
    public String firstH = "78781101";
    public String secondH = "010032000000E9940D0A";

    // Method to run the test
    public void test() {
        BufferedReader reader;

        try {
            // Initialize BufferedReader to read IMEI numbers from a file
            reader = new BufferedReader(new FileReader("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\Client\\randImeis2.txt"));
            // reader = new BufferedReader(new FileReader("D:\\Development\\Bachelor\\TCP-Server_With-MQTT\\src\\main\\java\\tcpserver\\Client\\randImeis.txt"));

            // Read the first line from the file
            String line = reader.readLine();
            int i = 0;

            // Loop through each line in the file
            while (line != null) {
                // Create a new ClientDummy instance
                ClientDummy cd = new ClientDummy();
				
                // Set the message for the client using the headers and the current line (IMEI number)
                cd.setMsg(firstH + line + secondH);

                // Start a new thread for the client
                Thread t = new Thread(cd);
                t.start();

                i += 1;
                System.out.println("Clients Created: " + i);
                System.out.println();

                // Read the next line from the file
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Keep the main thread running indefinitely
        while(true) {}
    }
}
