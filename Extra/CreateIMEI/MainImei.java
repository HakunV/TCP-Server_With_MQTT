import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

public class MainImei {

    public static void main(String[] args) {
        BigInteger imei = new BigInteger("353994711603560");

        try(
            // FileWriter fw = new FileWriter("C:\\Users\\mariu\\Development\\Bachelor\\Developing\\TCP-Server_With_MQTT\\src\\main\\java\\tcpserver\\Client\\randImeis.txt", true);
            FileWriter fw = new FileWriter("randImeis.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (int i = 0; i < 1000; i++) { // Create 1000 incremented IMEI numbers
                imei = imei.add(BigInteger.ONE);
                out.println("0" + imei.toString());
            }   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
