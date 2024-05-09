package tcpserver.Backend.Options;

public class SubscribeOptions {
    private int[] qos;

    public SubscribeOptions(int length) {
        this.qos = new int[length];

        for (int i = 0; i < length; i++) {
            this.qos[i] = 0;
        }
    }

    public SubscribeOptions(int length, int[] qos) {
        this.qos = new int[length];

        for (int i = 0; i < length; i++) {
            this.qos[i] = qos[i];
        }
    }

    public int[] getQos() {
        return qos;
    }
}
