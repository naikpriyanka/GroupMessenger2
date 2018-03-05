package edu.buffalo.cse.cse486586.groupmessenger2.model;

/**
 * Created by priyankanaik on 03/03/2018.
 */

public class Message {

    public static final String DELIMITER = ":";
    private MessageType type;
    private String sender;
    private String dest;
    private String msg;
    private int seqNum;


    public Message(MessageType type, String sender, String msg, int seqNum) {
        this.type = type;
        this.sender = sender;
        this.msg = msg;
        this.seqNum = seqNum;
    }

    @Override
    public String toString() {
        return type + DELIMITER + sender + DELIMITER + msg + DELIMITER + seqNum;
    }
}
