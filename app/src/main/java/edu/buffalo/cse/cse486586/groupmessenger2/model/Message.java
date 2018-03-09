package edu.buffalo.cse.cse486586.groupmessenger2.model;

import static edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType.*;

/**
 * Created by priyankanaik on 03/03/2018.
 *
 *
 * This class is used to store the messages using the Message type and other fields
 */
public class Message {

    //Used to separate fields of the message
    public static final String DELIMITER = ":";

    //Used to store the message type
    private MessageType type;

    //Used to store the message ID
    private int msgID;

    //Used to store the message text
    private String msg;

    //Used to store the sender of the message
    private String sender;

    //Used to store the receiver of the message
    private String receiver;

    //Used to store the sequence number of the message
    private int seqNum;

    //Used to mark the message as can be delivered or not
    private boolean isToBeDelivered;

    public Message() {

    }

    //This constructor is used for MESSAGE type message
    public Message(MessageType type, String msg, int msgID, String sender, String receiver) {
        this.type = type;
        this.sender = sender;
        this.msg = msg;
        this.receiver = receiver;
        this.msgID = msgID;
    }

    //This constructor is used for AGREED type message
    public Message(MessageType type, int msgID, String sender, int agreedSeq, String receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.msgID = msgID;
        this.seqNum = agreedSeq;
    }

    //This constructor is used for PROPOSED type message
    public Message(MessageType type, int msgID, int proposedSeq, String receiver) {
        this.type = type;
        this.receiver = receiver;
        this.msgID = msgID;
        this.seqNum = proposedSeq;
    }

    ////This constructor is used for putting the messages on the queue
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

    /*
     * Overrided this method by separating required field by DELIMITER
     * The message string is decided by the type of the message
     */
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
