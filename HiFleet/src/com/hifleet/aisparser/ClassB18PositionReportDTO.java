/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;

/**
 *
 * @author yangchun
 */
public class ClassB18PositionReportDTO {

    private int aisMessageType;
    private int repeatIndicator;
    private long mmsi;   
    private double speedOverGround;
    private double longitude;
    private double latitude;
    private double courseOverGround;
    private int trueHeading;
    private String timestamp;

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
}
