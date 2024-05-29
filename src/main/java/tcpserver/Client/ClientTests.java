package tcpserver.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClientTests {
    public String firstH = "78781101";
    public String secondH = "010032000000E9940D0A";

    public void test() {
        BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\myfile.txt"));
			String line = reader.readLine();

			while (line != null) {
				System.out.println(line);
                ClientDummy cd = new ClientDummy();
                cd.setMsg(firstH + line + secondH);

                Thread t = new Thread(cd);
                t.start();

				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        while(true) {}
    }
}
