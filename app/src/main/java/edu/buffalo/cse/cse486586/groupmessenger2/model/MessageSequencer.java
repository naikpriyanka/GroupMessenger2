package edu.buffalo.cse.cse486586.groupmessenger2.model;

import java.util.Comparator;

/**
 * Created by priyankanaik on 05/03/2018.
 *
 * Referred from : https://docs.oracle.com/javase/7/docs/api/java/util/Comparator.html
 *
 * Used to sequence the message using sequence number and the sender port number
 */
public class MessageSequencer implements Comparator<Message> {

    //Compare two message m1 and m2
    public int compare(Message m1, Message m2) {
        if (m1.getSeqNum() < m2.getSeqNum()) {
            return -1;
        } else if (m1.getSeqNum() > m2.getSeqNum()) {
            return 1;
        } else {
            //If the sequence number is same then break the tie using the sender port number
            if (Integer.parseInt(m1.getSender()) < Integer.parseInt(m2.getSender())) {
                return -1;
            } else
                return 1;
        }
    }
}