package tcpserver.Backend.Options;

public class PublishOptions {
    private boolean dup = false;
    private int qos = 0;
    private boolean retain = false;


    public boolean isDup() {
        return dup;
    }
    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getQos() {
        return qos;
    }
    public void setQos(int qos) {
        this.qos = qos;
    }
    
    public boolean isRetain() {
        return retain;
    }
    public void setRetain(boolean retain) {
        this.retain = retain;
    }
}
