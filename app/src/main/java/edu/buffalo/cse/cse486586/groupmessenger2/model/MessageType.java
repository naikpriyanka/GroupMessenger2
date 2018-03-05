package edu.buffalo.cse.cse486586.groupmessenger2.model;

/**
 * Created by priyankanaik on 04/03/2018.
 */

public enum MessageType {

    MESSAGE("MESSAGE"),
    PROPOSED("PROPOSED"),
    AGREED("AGREED");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public static MessageType getEnumBy(String type){
        for(MessageType m : MessageType.values()){
            if(m.type.equals(type)) return m;
        }
        return null;
    }

}
