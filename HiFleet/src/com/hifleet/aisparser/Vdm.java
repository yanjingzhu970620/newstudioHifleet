package com.hifleet.aisparser;

/**
 * AIS Parser SDK
 * VDM Parser Class
 * Copyright 2008 by Brian C. Lane <bcl@brianlane.com>
 * All Rights Reserved
 * 
 * @author Brian C. Lane
 */
class ChecksumFailedException extends Exception {

    public ChecksumFailedException() {
    }

    public ChecksumFailedException(String str) {
        super(str);
    }
}

class VDMSentenceException extends Exception {

    public VDMSentenceException() {
    }

    public VDMSentenceException(String str) {
        super(str);
    }
}

/**
 * VDM Class
 *
 * This keeps track partial messages until a complete message has been
 * received and it holds the sixbit state for exteacting bits from the
 * message.
 */
public class Vdm {

    int msgid;               //!< Message ID 0-31
    int sequence;            //!< VDM message sequence number
    int total;               //!< Total # of parts for the message
    int num;                 //!< Number of the last part stored
    char channel;             //!< AIS Channel character
    Sixbit six_state;           //!< sixbit parser state

    /*
     * Constructor, initialize the state
     */
    public Vdm() {
        this.total = 0;
        this.sequence = 0;
        this.num = 0;
    }

    /**
     * Return the 6-bit state
     */
    public Sixbit sixbit() {
        return this.six_state;
    }

    /**
     * Get the message id
     */
    public int msgid() {
        return this.msgid;
    }

    /**
     *  Assemble AIVDM/VDO sentences
     *
     * This function handles re-assembly and extraction of the 6-bit data
     * from AIVDM/AIVDO sentences.
     *
     * Because the NMEA standard limits the length of a line to 80 characters
     * some AIS messages, such as message 5, are output as a multipart VDM 
     * messages. 
     * This routine collects the 6-bit encoded data from these parts and 
     * returns a 1 when all pieces have been reassembled.
     * 
     * It expects the sentences to:
     * - Be in order, part 1, part 2, etc.
     * - Be from a single sequence
     * 
     * It will return an error if it receives a piece out of order or from
     * a new sequence before the previous one is finished.
     * 
     * Returns
     *   - 0 Complete packet
     *   - 1 Incomplete packet
     *   - 2 NMEA 0183 checksum failed
     *   - 3 Not an AIS message
     *   - 4 Error with nmea_next_field
     *   - 5 Out of sequence packet
     *
     */

    public void add(String str)
            {
            this.six_state = new Sixbit();
            this.six_state.init("");
            six_state.add(str);
//            try {
//                this.msgid = (int) six_state.get(6);
//            } catch (SixbitsExhaustedException e) {
//               e.printStackTrace();
//            }
    }
    java.util.Hashtable buf = new java.util.Hashtable();
    int previous_serial_no;
    String packedMsg;
}
