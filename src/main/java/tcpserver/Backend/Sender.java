package tcpserver.Backend;

import tcpserver.Backend.Options.ConnectOptions;
import tcpserver.Backend.Options.PublishOptions;
import tcpserver.Backend.Options.SubscribeOptions;
import tcpserver.Helpers.Helpers;
import tcpserver.Backend.SenderPackets.*;

import java.io.*;

public class Sender {
    private BackendClient bc = null;
    private BufferedOutputStream bos = null;
    private WiFi_Config wc = null;

    public Sender(BackendClient bc, BufferedOutputStream bos) {
        this.bc = bc;
        this.bos = bos;
        this.wc = new WiFi_Config();
    }

    public void connect() {
        send(Connect.connect(new ConnectOptions()));;
    }

    public void connect(ConnectOptions co) {
        send(Connect.connect(co));
    }

    public void publish(String device, float lat, float lon) {
        send(Publish.publish(device, lat, lon, new PublishOptions()));
    }

    public void publish(String device, float lat, float lon, PublishOptions po) {
        send(Publish.publish(device, lat, lon, po));
    }

    public void pubacks(int packetInt, int ackType) {
        send(Pubacks.pubacks(packetInt, ackType));
    }

    public void subscribe(String[] topics) {
        send(Subscribe.subscribe(topics, new SubscribeOptions(topics.length)));
    }

    public void subscribe(String[] topics, SubscribeOptions so) {
        send(Subscribe.subscribe(topics, so));
    }

    public void sendUnsubscribe() {

    }

    public void ping() {
        send(Ping.ping());
    }

    public void disconnect() {
        send(Disconnect.disconnect());
    }

    private void send(String message) {
        try {
            Helpers.sendMessage(message, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Message");
            e.printStackTrace();
        }
    }
}
