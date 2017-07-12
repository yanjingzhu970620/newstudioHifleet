/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;

/**
 *
 * @author yangchun
 */
public class Message21 extends Messages {

    int aton_type;         // 5 bits    : Type of AtoN    
    String name;              // 120 bits  : Name of AtoN in ASCII    
    int pos_acc;           // 1 bit     : Position Accuracy   
    Position       pos;                //           : Lat/Long 1/100000 minute    
    int dim_bow;           // 9 bits    : GPS Ant. Distance from Bow   
    int dim_stern;         // 9 bits    : GPS Ant. Distance from Stern    
    int dim_port;          // 6 bits    : GPS Ant. Distance from Port    
    int dim_starboard;     // 6 bits    : GPS Ant. Distance from Starboard    
    int pos_type;          // 4 bits    : Type of Position Fixing Device    
    int utc_sec;           // 6 bits    : UTC Seconds    
    int off_position;      // 1 bit     : Off Position Flag    
    int regional;          // 8 bits    : Regional Bits    
    int raim;              // 1 bit     : RAIM Flag    
    int virtual;           // 1 bit     : Virtual/Pseudo AtoN Flag    
    int assigned;          // 1 bit     : Assigned Mode Flag   
    int spare1;            // 1 bit     : Spare    
    String name_ext;          // 0-84 bits : Extended name in ASCII   
    int spare2;            // 0-6 bits  : Spare        

    public int aton_type() {
        return this.aton_type;
    }

    public String name() {
        return this.name;
    }

    public int pos_acc() {
        return this.pos_acc;
    }

    public double longitude() {
        return this.pos.longitude();
    }

    public double latitude() {
        return this.pos.latitude();
    }

    public int dim_bow() {
        return this.dim_bow;
    }

    public int dim_stern() {
        return this.dim_stern;
    }

    public int dim_port() {
        return this.dim_port;
    }

    public int dim_starboard() {
        return this.dim_starboard;
    }

    public int pos_type() {
        return this.pos_type;
    }

    public int utc_sec() {
        return this.utc_sec;
    }

    public int off_position() {
        return this.off_position;
    }

    public int regional() {
        return this.regional;
    }

    public int raim() {
        return this.raim;
    }

    public int virtual() {
        return this.virtual;
    }

    public int assigned() {
        return this.assigned;
    }

    public int spare1() {
        return this.spare1;
    }

    public String name_ext() {
        return this.name_ext;
    }

    public int spare2() {
        return this.spare2;
    }

    public Message21() {
        super();
    }

    private long mmsi;
    private void setMMSI(long mmsi){
        this.mmsi = mmsi;
    }
    public long getMMSI(){
        return this.mmsi;
    }
    public Message21Report parse(Sixbit six_state) throws SixbitsExhaustedException, AISMessageException {
        int length = six_state.bit_length();
        if ((length < 272) || (length > 360)) {
            throw new AISMessageException("Message 21 wrong length");
        }
        final Message21Report report = new Message21Report();
        super.parse(21, six_state);
        
        report.setMmsi(super.getMmsi());
        setMMSI(super.getMmsi());
        this.aton_type = (int) six_state.get(5);
        report.setAton_type(this.aton_type);
        this.name = six_state.get_string(20);
        report.setName(this.name);
        this.pos_acc = (int) six_state.get(1);
        report.setPos_acc(this.pos_acc);
        this.pos = new Position();
        this.pos.setLongitude((long) six_state.get(28));
        this.pos.setLatitude((long) six_state.get(27));
        report.setPos(this.pos);
        this.dim_bow = (int) six_state.get(9);
        report.setDim_bow(this.dim_bow);
        this.dim_stern = (int) six_state.get(9);
        report.setDim_port(this.dim_stern);
        this.dim_port = (int) six_state.get(6);
        report.setDim_port(this.dim_port);
        this.dim_starboard = (int) six_state.get(6);
        report.setDim_starboard(this.dim_starboard);
        this.pos_type = (int) six_state.get(4);
        report.setPos_type(this.pos_type);
        this.utc_sec = (int) six_state.get(6);
        report.setUtc_sec(this.utc_sec);
        this.off_position = (int) six_state.get(1);
        report.setOff_position(this.off_position);
        this.regional = (int) six_state.get(8);
        report.setRegional(this.regional);
        this.raim = (int) six_state.get(1);
        report.setRaim(this.raim);
        this.virtual = (int) six_state.get(1);
        report.setVirtual(this.virtual);
        this.assigned = (int) six_state.get(1);
        report.setAssigned(this.assigned);
        this.spare1 = (int) six_state.get(1);
        report.setSpare1(this.spare1);
        if (length > 272) {
            this.name_ext = six_state.get_string((length - 272) / 6);
            report.setName_ext(this.name_ext);
        }
        return report;
    }
}
