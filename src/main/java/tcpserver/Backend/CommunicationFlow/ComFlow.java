package tcpserver.Backend.CommunicationFlow;

import java.util.ArrayList;

import tcpserver.Backend.Sender;

// Class representing the communication flow manager
public class ComFlow {
    private Sender s = null;
    private ArrayList<Flow> flows = new ArrayList<Flow>();

    // Constructor to initialize the communication flow manager
    public ComFlow(Sender s) {
        this.s = s;
    }

    // Method to create a new communication flow
    public void createFlow(boolean sender, int packetID, int qos, String message) {
        if (getFlow(packetID) == null) {
            if (sender) {
                flows.add(new Flow(sender, packetID, qos, message));
            } else {
                if (qos == 1) {
                    s.pubacks(packetID, 4); // Send PUBACK message for QoS 1
                } else {
                    String newMes = s.pubacks(packetID, 5); // Send PUBREC message for QoS 2
                    flows.add(new Flow(sender, packetID, qos, newMes));
                }
            }
        }
    }

    // Method to update a communication flow based on acknowledgment type
    public void update(int packetID, int ackType) {
        Flow f = getFlow(packetID); // Get the flow with the given packet ID

        if (f != null) {
            State s = f.getState(); // Get the current state of the flow

            switch (s) {
                case AWAIT_ACK:
                    if (ackType == 4) {
                        finishFlow(f); // Finish the flow if PUBACK received
                    } else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                case AWAIT_REC:
                    if (ackType == 5) {
                        sendRel_qos2(f); // Send PUBREL message for QoS 2 if PUBREC received
                    } else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                case AWAIT_REL:
                    if (ackType == 6) {
                        sendComp_qos2(f); // Send PUBCOMP message for QoS 2 if PUBREL received
                        finishFlow(f); // Finish the flow
                    } else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                case AWAIT_COMP:
                    if (ackType == 7) {
                        finishFlow(f); // Finish the flow if PUBCOMP received
                    } else {
                        System.out.println("Wrong Acknowledgement");
                    }
                    break;
                default:
                    System.out.println("Did Not Recognize The State");
                    System.out.println();
            }
        }
    }

    // Method to finish a communication flow
    private void finishFlow(Flow f) {
        flows.remove(f);
    }

    // Method to send PUBREL message for QoS 2
    private void sendRel_qos2(Flow f) {
        f.changeMessage(s.pubacks(f.getPacketID(), 6)); // Change the message to PUBREL
        f.changeState(State.AWAIT_COMP); // Change the state to AWAIT_COMP
    }

    // Method to send PUBCOMP message for QoS 2
    private void sendComp_qos2(Flow f) {
        s.pubacks(f.getPacketID(), 7); // Send PUBCOMP message
    }

    // Method to get the communication flow with a given packet ID
    public Flow getFlow(int packetID) {
        Flow res = null;

        for (Flow f : flows) { // Iterate through the list of flows
            if (f.getPacketID() == packetID) {
                res = f;
                break; // Break the loop since the flow is found
            }
        }
        return res;
    }

    // Method to get the packet IDs of all active flows
    public int[] getUsed() {
        int[] res = new int[flows.size()];

        for (int i = 0; i < flows.size(); i++) {
            res[i] = flows.get(i).getPacketID(); // Store currently used packet IDs
        }

        return res;
    }
}


// Class representing a communication flow
class Flow {
    private boolean sender;
    private int packetID;
    private int qos;
    private String message;

    private State state;

    // Constructor to initialize a flow
    public Flow(boolean sender, int packetID, int qos, String message) {
        this.sender = sender;
        this.packetID = packetID;
        this.qos = qos;
        this.message = message;

        // Initialize the state based on whether it's a sender or receiver flow and the QoS level
        if (sender) {
            if (qos == 2) {
                this.state = State.AWAIT_REC; // Sender flow with QoS 2 awaits PUBREC
            } else {
                this.state = State.AWAIT_ACK; // Sender flow with QoS 1 awaits PUBACK
            }
        } else {
            if (qos == 2) {
                this.state = State.AWAIT_REL; // Receiver flow with QoS 2 awaits PUBREL
            }
        }
    }

    // Get the packet ID of the flow
    public int getPacketID() {
        return this.packetID;
    }

    // Get the current state of the flow
    public State getState() {
        return this.state;
    }

    // Change the state of the flow
    public void changeState(State s) {
        this.state = s;
    }

    // Get the message content of the flow
    public String getMessage() {
        return this.message;
    }

    // Change the message content of the flow
    public void changeMessage(String message) {
        this.message = message;
    }
}

// Enum representing the states of a communication flow
enum State {
    AWAIT_ACK,
    AWAIT_REC,
    AWAIT_REL,
    AWAIT_COMP
}

