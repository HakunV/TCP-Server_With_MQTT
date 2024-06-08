import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPMain {
    
    public static void main(String[] args) throws SocketException, UnknownHostException {
        udp es = new udp();
        es.start();
    }
}

class udp extends Thread {

    private DatagramSocket socket;
    private InetAddress address = InetAddress.getByName("thingsofinter.net");
    private int port = 5507;
    private boolean running;
    private byte[] buf = new byte[256];

    public udp() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
    }

    public void run() {
        running = true;

        while (running) {
            // Data in JSON format
            String data = "{\"name\": \"359510087300096\",\"MAC\": \"Je8YEe\",\"technology\": \"wifi\",\"IP\": \"10.209.216.197\",\"rssi\": -40,\"ssid\": \"eduroam\",\"host\": \"192.38.81.6\",\"gwIP\": \"10.209.128.1\",\"BSSID\": \"10:a8:29:a4:e9:4e\",\"channel\": 44,\"seq\": 10,\"data\": { \"lat\": 55.1233, \"lon\": 13.2345, \"imei\": 359510087300096 },\"AuthToken\": \"6a2b0454-7bcb-46eb-8e77-37005d22d72c\"}";

            buf = data.getBytes();

            // Create UDP packet
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            running = false;
        }
        socket.close();
    }
}
