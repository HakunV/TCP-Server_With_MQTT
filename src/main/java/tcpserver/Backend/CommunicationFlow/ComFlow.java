package tcpserver.Backend.CommunicationFlow;

import java.util.ArrayList;

public class ComFlow {
    public ArrayList<Flow> flows = new ArrayList<Flow>();

    public void createFlow(boolean sender, int packetID, int qos, String message) {
        if (!checkPacketID(packetID)) {
            flows.add(new Flow(sender, packetID, qos, message));
        }
        
    }

    public void update(int packetID, int ackType) {
        
    }

    public boolean checkPacketID(int packetID) {
        boolean res = false;

        for (Flow f : flows) {
            if (f.getPacketID() == packetID) {
                res = true;
                break;
            }
        }
        return res;
    }
}

class Flow {
    private boolean sender;
    private int packetID;
    private int qos;
    private String message;

    private State state;

    public Flow(boolean sender, int packetID, int qos, String message) {
        this.sender = sender;
        this.packetID = packetID;
        this.qos = qos;
        this.message = message;

        if (sender) {
            if (qos == 2) {
                this.state = State.AWAIT_REC;
            }
            else {
                this.state = State.AWAIT_ACK;
            }
        }
        else {
            if (qos == 2) {
                this.state = State.AWAIT_REL;
            }
        }
    }

    public int getPacketID() {
        return this.packetID;
    }
}

enum State {
    AWAIT_ACK, AWAIT_REC, AWAIT_REL, AWAIT_COMP
}
