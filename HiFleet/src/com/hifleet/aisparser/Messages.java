package com.hifleet.aisparser;

/**
 * AIS Parser SDK
 * Base Class for Messages
 * Copyright 2008 by Brian C. Lane <bcl@brianlane.com>
 * All Rights Reserved
 * 
 * @author Brian C. Lane
 */
class AISMessageException extends Exception {

    public AISMessageException() {
    }

    public AISMessageException(String str) {
        super(str);
    }
}

/**
 * AIS Message base class
 * 
 * All the messages are derived from this class which provides the msgid, 
 * repeat value and userid
 *
 */
public class Messages {

    private int msgid;             // 6 bits  : Message ID (1)
    private int repeat;            // 2 bits  : Repeated
    private long mmsi;            // 30 bits : UserID / MMSI

    public Messages() {
    }

    private int getMsgid() {
        return this.msgid;
    }

//    public int repeat() {
//        return this.repeat;
//    }

    public long  getMmsi() {
        return this.mmsi;
    }

    // Subclasses need to override with their own parsing method
    public void parse(int msgid, Sixbit six_state)
            throws SixbitsExhaustedException {
        
         six_state.get(6);
        //this.msgid = msgid;
        
        //this.repeat = (int) six_state.get(2);
        six_state.get(2);
        // System.out.println("repeat: "+repeat);
       
        this.mmsi = (long) six_state.get(30);
        // System.out.println("userid: "+userid);
    }
}
