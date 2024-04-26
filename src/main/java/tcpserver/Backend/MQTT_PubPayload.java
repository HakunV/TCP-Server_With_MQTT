package tcpserver.Backend;

public class MQTT_PubPayload {
    private String name = "";
    private String MAC = "";
    private String technology = "";
    private String IP = "";
    private int rssi = 0;
    private String ssid = "";
    private String host = "";
    private String gwIP = "";
    private String BSSID = "";
    private int channel = 0;
    private int seq = 1;
    private GPS_data data = new GPS_data();
    private String AuthToken = "";

    public void setName(String name) {
        this.name = name;
    }

    public void setMAC(String mAC) {
        MAC = mAC;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setGwIP(String gwIP) {
        this.gwIP = gwIP;
    }

    public void setBSSID(String bSSID) {
        BSSID = bSSID;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setData(float lat, float lon) {
        data.setData(lat, lon);
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }
}

class GPS_data {
    private float lat = 0;
    private float lon = 0;

    public GPS_data() {}

    public void setData(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
