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

    public int connect() {
        String[] res = Connect.connect(new ConnectOptions());
        send(res[0]);
        return Integer.parseInt(res[1]);
    }

    public int connect(ConnectOptions co) {
        String[] res = Connect.connect(new ConnectOptions());
        send(res[0]);
        return Integer.parseInt(res[1]);
    }

    public void publish(String device, float lat, float lon) {
        PublishOptions po = new PublishOptions();
        String[] pubArr = Publish.publish(device, lat, lon, po);
        bc.getComFlow().createFlow(true, Integer.parseInt(pubArr[0]), po.getQos(), pubArr[1]);
        send(pubArr[1]);
    }

    public void publish(String device, float lat, float lon, PublishOptions po) {
        String[] pubArr = Publish.publish(device, lat, lon, po);
        bc.getComFlow().createFlow(true, Integer.parseInt(pubArr[0]), po.getQos(), pubArr[1]);
        send(pubArr[1]);
    }

    public String pubacks(int packetID, int ackType) {
        String message = Pubacks.pubacks(packetID, ackType);
        send(message);
        return message;
    }

    public void subscribe(String[] topics) {
        String[] subArr = Subscribe.subscribe(topics, new SubscribeOptions(topics.length));
        bc.getComFlow().createFlow(true, Integer.parseInt(subArr[0]), 1, subArr[1]);
        send(subArr[1]);
    }

    public void subscribe(String[] topics, SubscribeOptions so) {
        String[] subArr = Subscribe.subscribe(topics, new SubscribeOptions(topics.length));
        bc.getComFlow().createFlow(true, Integer.parseInt(subArr[0]), 1, subArr[1]);
        send(subArr[1]);
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
