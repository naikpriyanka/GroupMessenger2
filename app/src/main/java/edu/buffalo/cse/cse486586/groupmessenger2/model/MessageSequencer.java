package edu.buffalo.cse.cse486586.groupmessenger2.model;

import java.util.Comparator;

/**
 * Created by priyankanaik on 05/03/2018.
 */

public class MessageSequencer implements Comparator<Message> {
    public int compare(Message m1, Message m2) {
        if (m1.getSeqNum() < m2.getSeqNum()) {
            return -1;
        } else if (m1.getSeqNum() > m2.getSeqNum()) {
            return 1;
        } else {
            if (Integer.parseInt(m1.getSender()) < Integer.parseInt(m2.getSender())) {
                return -1;
            } else
                return 1;
        }
    }
}