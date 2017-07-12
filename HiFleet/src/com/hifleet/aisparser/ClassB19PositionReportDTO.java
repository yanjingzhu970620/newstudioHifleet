/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;

/**
 *
 * @author yangchun
 */
public class ClassB19PositionReportDTO {

    private int aisMessageType;
    private int repeatIndicator;
    private long mmsi;
    private double speedOverGround;
    private double longitude;
    private double latitude;
    private double courseOverGround;
    private int trueHeading;
    private String timestamp;
    private String vesselName;
    private int shipType;
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
     * @return the speedOverGround
     */
    public double getSpeedOverGround() {
        return speedOverGround;
    }

    /**
     * @param speedOverGround the speedOverGround to set
     */
    public void setSpeedOverGround(double speedOverGround) {
        this.speedOverGround = speedOverGround;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double raw_longitude) {
       if( raw_longitude >= 0x8000000 )
	    {
	        this.longitude = 0x10000000 - raw_longitude;
	        this.longitude *= -1;
	    } else {
	    	this.longitude = raw_longitude;
	    }
        //this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double raw_latitude) {
        if( raw_latitude >= 0x4000000 )
	    {
	        this.latitude = 0x8000000 - raw_latitude;
	        this.latitude *= -1;
	    } else {
	    	this.latitude = raw_latitude;
	    }
        //this.latitude = latitude;
    }

    /**
     * @return the courseOverGround
     */
    public double getCourseOverGround() {
        return courseOverGround;
    }

    /**
     * @param courseOverGround the courseOverGround to set
     */
    public void setCourseOverGround(double courseOverGround) {
        this.courseOverGround = courseOverGround;
    }

    /**
     * @return the trueHeading
     */
    public int getTrueHeading() {
        return trueHeading;
    }

    /**
     * @param trueHeading the trueHeading to set
     */
    public void setTrueHeading(int trueHeading) {
        this.trueHeading = trueHeading;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the vesselName
     */
    public String getVesselName() {
        return vesselName;
    }

    /**
     * @param vesselName the vesselName to set
     */
    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
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
