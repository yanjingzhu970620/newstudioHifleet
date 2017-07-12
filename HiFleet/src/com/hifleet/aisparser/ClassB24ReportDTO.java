/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;

/**
 *
 * @author yangchun
 */
public class ClassB24ReportDTO {
    private int aisMessageType;
    private int repeatIndicator;
    private long mmsi;
    private int part;
    private String name;
    private int shipType;
    private String callsign;    
    private int dimensionToBow;
    private int dimensionToStern;
    private int dimensionToPort;
    private int dimensionToStarboard;

    /**
     * @return the aisMessageType
     */
    private int getAisMessageType() {
        return aisMessageType;
    }

    /**
     * @param aisMessageType the aisMessageType to set
     */
    private void setAisMessageType(int aisMessageType) {
        this.aisMessageType = aisMessageType;
    }

    /**
     * @return the repeatIndicator
     */
    private int getRepeatIndicator() {
        return repeatIndicator;
    }

    /**
     * @param repeatIndicator the repeatIndicator to set
     */
    private void setRepeatIndicator(int repeatIndicator) {
        this.repeatIndicator = repeatIndicator;
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

    /**
     * @return the part
     */
    public int getPart() {
        return part;
    }

    /**
     * @param part the part to set
     */
    public void setPart(int part) {
        this.part = part;
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
     * @return the shipType
     */
    public int getShipType() {
        return shipType;
    }

    /**
     * @param shipType the shipType to set
     */
    public void setShipType(int shipType) {
        this.shipType = shipType;
    }

    /**
     * @return the callsign
     */
    public String getCallsign() {
        return callsign;
    }

    /**
     * @param callsign the callsign to set
     */
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    /**
     * @return the dimensionToBow
     */
    public int getDimensionToBow() {
        return dimensionToBow;
    }

    /**
     * @param dimensionToBow the dimensionToBow to set
     */
    public void setDimensionToBow(int dimensionToBow) {
        this.dimensionToBow = dimensionToBow;
    }

    /**
     * @return the dimensionToStern
     */
    public int getDimensionToStern() {
        return dimensionToStern;
    }

    /**
     * @param dimensionToStern the dimensionToStern to set
     */
    public void setDimensionToStern(int dimensionToStern) {
        this.dimensionToStern = dimensionToStern;
    }

    /**
     * @return the dimensionToPort
     */
    public int getDimensionToPort() {
        return dimensionToPort;
    }

    /**
     * @param dimensionToPort the dimensionToPort to set
     */
    public void setDimensionToPort(int dimensionToPort) {
        this.dimensionToPort = dimensionToPort;
    }

    /**
     * @return the dimensionToStarboard
     */
    public int getDimensionToStarboard() {
        return dimensionToStarboard;
    }

    /**
     * @param dimensionToStarboard the dimensionToStarboard to set
     */
    public void setDimensionToStarboard(int dimensionToStarboard) {
        this.dimensionToStarboard = dimensionToStarboard;
    }
}
