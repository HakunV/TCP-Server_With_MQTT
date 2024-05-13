package tcpserver.Backend.CommunicationFlow;

import java.util.ArrayList;

import tcpserver.Backend.Sender;

public class ComFlow {
    private Sender s = null;

    private ArrayList<Flow> flows = new ArrayList<Flow>();

    public ComFlow(Sender s) {
        this.s = s;
    }

    public void createFlow(boolean sender, int packetID, int qos, String message) {
        if (getFlow(packetID) != null) {
            if (sender) {
                flows.add(new Flow(sender, packetID, qos, message));
            }
            else {
                if (qos == 1) {
                    s.pubacks(packetID, 4);
                }
                else {
                    String newMes = s.pubacks(packetID, 5);
                    flows.add(new Flow(sender, packetID, qos, newMes));
                }
            }
        }
    }

    public void update(int packetID, int ackType) {
        Flow f = getFlow(packetID);

        if (f != null) {
            State s = f.getState();

            switch (s) {
                case AWAIT_ACK:
                    if (ackType == 4) {
                        finishFlow(f);
                    }
                    else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                case AWAIT_REC:
                    if (ackType == 5) {
                        sendRel_qos2(f);
                    }
                    else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                case AWAIT_REL:
                    if (ackType == 6) {
                        sendComp_qos2(f);
                        finishFlow(f);
                    }
                    else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                case AWAIT_COMP:
                    if (ackType == 7) {
                        finishFlow(f);
                    }
                    else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                default:
                    System.out.println("Did Not Recognize The State");
                    System.out.println();
            }
        }
    }

    private void finishFlow(Flow f) {
        flows.remove(f);
    }

    private void sendRel_qos2(Flow f) {
        f.changeMessage(s.pubacks(f.getPacketID(), 6));

        f.changeState(State.AWAIT_COMP);
    }

    private void sendComp_qos2(Flow f) {
        s.pubacks(f.getPacketID(), 7);
    }

    public Flow getFlow(int packetID) {
        Flow res = null;

        for (Flow f : flows) {
            if (f.getPacketID() == packetID) {
                res = f;
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

    public State getState() {
        return this.state;
    }

    public void changeState(State s) {
        this.state = s;
    }

    public String getMessage() {
        return this.message;
    }

    public void changeMessage(String message) {
        this.message = message;
    }
}

enum State {
    AWAIT_ACK, AWAIT_REC, AWAIT_REL, AWAIT_COMP
}
