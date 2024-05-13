package tcpserver.Backend.Options;

public class ConnectOptions {
    private boolean username = true;
    private boolean password = true;
    private boolean willRetain = false;
    private int willQos = 0;
    private boolean willFlag = false;
    private boolean clean = true;
    private int keepAlive = 180;

    public boolean isUsername() {
        return username;
    }
    public void setUsername(boolean username) {
        this.username = username;
    }
    public boolean isPassword() {
        return password;
    }
    public void setPassword(boolean password) {
        this.password = password;
    }
    public boolean isWillRetain() {
        return willRetain;
    }
    public void setWillRetain(boolean willRetain) {
        this.willRetain = willRetain;
    }
    public int getWillQos() {
        return willQos;
    }
    public void setWillQos(int willQos) {
        this.willQos = willQos;
    }
    public boolean isWillFlag() {
        return willFlag;
    }
    public void setWillFlag(boolean willFlag) {
        this.willFlag = willFlag;
    }
    public boolean isClean() {
        return clean;
    }
    public void setClean(boolean clean) {
        this.clean = clean;
    }
    public int getKeepAlive() {
        return keepAlive;
    }
    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }
}
