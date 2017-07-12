/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;

/**
 *
 * @author yangchun
 */
public class Message21Report {
    private int aton_type;         // 5 bits    : Type of AtoN    
    private String name;              // 120 bits  : Name of AtoN in ASCII    
    private int pos_acc;           // 1 bit     : Position Accuracy   
    private Position       pos;                //           : Lat/Long 1/100000 minute    
    private int dim_bow;           // 9 bits    : GPS Ant. Distance from Bow   
    private int dim_stern;         // 9 bits    : GPS Ant. Distance from Stern    
    private int dim_port;          // 6 bits    : GPS Ant. Distance from Port    
    private int dim_starboard;     // 6 bits    : GPS Ant. Distance from Starboard    
    private int pos_type;          // 4 bits    : Type of Position Fixing Device    
    private int utc_sec;           // 6 bits    : UTC Seconds    
    private int off_position;      // 1 bit     : Off Position Flag    
    private int regional;          // 8 bits    : Regional Bits    
    private int raim;              // 1 bit     : RAIM Flag    
    private int virtual;           // 1 bit     : Virtual/Pseudo AtoN Flag    
    private int assigned;          // 1 bit     : Assigned Mode Flag   
    private int spare1;            // 1 bit     : Spare    
    private String name_ext;          // 0-84 bits : Extended name in ASCII   
    private int spare2;            // 0-6 bits  : Spare       
    private long mmsi;

    /**
     * @return the aton_type
     */
    public int getAton_type() {
        return aton_type;
    }

    /**
     * @param aton_type the aton_type to set
     */
    public void setAton_type(int aton_type) {
        this.aton_type = aton_type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the pos_acc
     */
    public int getPos_acc() {
        return pos_acc;
    }

    /**
     * @param pos_acc the pos_acc to set
     */
    public void setPos_acc(int pos_acc) {
        this.pos_acc = pos_acc;
    }

    /**
     * @return the pos
     */
    public Position getPos() {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Position pos) {
        this.pos = pos;
    }

    /**
     * @return the dim_bow
     */
    public int getDim_bow() {
        return dim_bow;
    }

    /**
     * @param dim_bow the dim_bow to set
     */
    public void setDim_bow(int dim_bow) {
        this.dim_bow = dim_bow;
    }

    /**
     * @return the dim_stern
     */
    public int getDim_stern() {
        return dim_stern;
    }

    /**
     * @param dim_stern the dim_stern to set
     */
    public void setDim_stern(int dim_stern) {
        this.dim_stern = dim_stern;
    }

    /**
     * @return the dim_port
     */
    public int getDim_port() {
        return dim_port;
    }

    /**
     * @param dim_port the dim_port to set
     */
    public void setDim_port(int dim_port) {
        this.dim_port = dim_port;
    }

    /**
     * @return the dim_starboard
     */
    public int getDim_starboard() {
        return dim_starboard;
    }

    /**
     * @param dim_starboard the dim_starboard to set
     */
    public void setDim_starboard(int dim_starboard) {
        this.dim_starboard = dim_starboard;
    }

    /**
     * @return the pos_type
     */
    public int getPos_type() {
        return pos_type;
    }

    /**
     * @param pos_type the pos_type to set
     */
    public void setPos_type(int pos_type) {
        this.pos_type = pos_type;
    }

    /**
     * @return the utc_sec
     */
    public int getUtc_sec() {
        return utc_sec;
    }

    /**
     * @param utc_sec the utc_sec to set
     */
    public void setUtc_sec(int utc_sec) {
        this.utc_sec = utc_sec;
    }

    /**
     * @return the off_position
     */
    public int getOff_position() {
        return off_position;
    }

    /**
     * @param off_position the off_position to set
     */
    public void setOff_position(int off_position) {
        this.off_position = off_position;
    }

    /**
     * @return the regional
     */
    public int getRegional() {
        return regional;
    }

    /**
     * @param regional the regional to set
     */
    public void setRegional(int regional) {
        this.regional = regional;
    }

    /**
     * @return the raim
     */
    public int getRaim() {
        return raim;
    }

    /**
     * @param raim the raim to set
     */
    public void setRaim(int raim) {
        this.raim = raim;
    }

    /**
     * @return the virtual
     */
    public int getVirtual() {
        return virtual;
    }

    /**
     * @param virtual the virtual to set
     */
    public void setVirtual(int virtual) {
        this.virtual = virtual;
    }

    /**
     * @return the assigned
     */
    public int getAssigned() {
        return assigned;
    }

    /**
     * @param assigned the assigned to set
     */
    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }

    /**
     * @return the spare1
     */
    public int getSpare1() {
        return spare1;
    }

    /**
     * @param spare1 the spare1 to set
     */
    public void setSpare1(int spare1) {
        this.spare1 = spare1;
    }

    /**
     * @return the name_ext
     */
    public String getName_ext() {
        return name_ext;
    }

    /**
     * @param name_ext the name_ext to set
     */
    public void setName_ext(String name_ext) {
        this.name_ext = name_ext;
    }

    /**
     * @return the spare2
     */
    public int getSpare2() {
        return spare2;
    }

    /**
     * @param spare2 the spare2 to set
     */
    public void setSpare2(int spare2) {
        this.spare2 = spare2;
    }

    /**
     * @return the mmsi
     */
    public long getMmsi() {
        return mmsi;
    }

    /**
     * @param mmsi the mmsi to set
     */
    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }
}
