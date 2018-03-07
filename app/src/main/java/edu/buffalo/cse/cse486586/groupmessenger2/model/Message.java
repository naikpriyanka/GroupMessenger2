package edu.buffalo.cse.cse486586.groupmessenger2.model;

import static edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType.*;

/**
 * Created by priyankanaik on 03/03/2018.
 */

public class Message {

    public static final String DELIMITER = ":";
    private MessageType type;
    private int msgID;
    private String msg;
    private String sender;
    private String receiver;
    private int seqNum;
    private boolean isToBeDelivered;

    public Message() {

    }

    public Message(MessageType type, String msg, int msgID, String sender, String receiver) {
        this.type = type;
        this.sender = sender;
        this.msg = msg;
        this.receiver = receiver;
        this.msgID = msgID;
    }

    public Message(MessageType type, int msgID, String sender, int agreedSeq, String receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.msgID = msgID;
        this.seqNum = agreedSeq;
    }

    public Message(MessageType type, int msgID, int proposedSeq, String receiver) {
        this.type = type;
        this.receiver = receiver;
        this.msgID = msgID;
        this.seqNum = proposedSeq;
    }

    public Message(String msg, int msgID, String sender, String receiver, boolean isToBeDelivered) {
        this.sender = sender;
        this.msg = msg;
        this.receiver = receiver;
        this.msgID = msgID;
        this.isToBeDelivered = isToBeDelivered;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public boolean isToBeDelivered() {
        return isToBeDelivered;
    }

    public void setToBeDelivered(boolean toBeDelivered) {
        this.isToBeDelivered = toBeDelivered;
    }

    @Override
    public String toString() {
        if(type == MESSAGE) {
            return type + DELIMITER + msg + DELIMITER + msgID + DELIMITER + sender + DELIMITER + receiver;
        } else if(type == AGREED) {
            return type + DELIMITER + msgID + DELIMITER + sender + DELIMITER + seqNum + DELIMITER + receiver;
        } else if(type == PROPOSED) {
            return type + DELIMITER + msgID + DELIMITER + seqNum + DELIMITER + receiver;
        }
        return type + ", " + msgID + ", " + msg + ", " + sender + ", " + receiver + ", " + seqNum + ", " + isToBeDelivered;
    }
}
