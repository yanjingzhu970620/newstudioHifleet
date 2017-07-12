/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;


public class Class123PositionReportDTO {

    private int aisMessageType;
    private int repeatIndicator;
    private long mmsi;
    private int navigationStatus;
    private int rateOfTurn;
    private double speedOverGround;
    private boolean positionAccurate;
    private double longitude;
    private double latitude;
    private double courseOverGround;
    private int trueHeading;
    //private int timestamp;
    private String timestamp;
    private int maneuverIndicator;
    private boolean raimUsed;
    private int radioStatus;

    /**
     * Get the AIS message type.
     * 
     * @return the AIS message type
     */
    private int getAisMessageType() {
        return aisMessageType;
    }

    /**
     * Set the AIS message type.
     * 
     * @param aisMessageType the AIS message type
     */
    private void setAisMessageType(final int aisMessageType) {
        this.aisMessageType = aisMessageType;
    }

    /**
     * Get the repeat indicator.
     * 
     * @return the repeat indicator.
     */
    private int getRepeatIndicator() {
        return repeatIndicator;
    }

    /**
     * Set the repeat indicator.
     * 
     * @param repeatIndicator the repeat indicator
     */
    private void setRepeatIndicator(final int repeatIndicator) {
        this.repeatIndicator = repeatIndicator;
    }

    /**
     * Get the MMSI.
     * 
     * @return the MMSI
     */
    public long getMmsi() {
        return mmsi;
    }

    /**
     * Set the MMSI.
     * 
     * @param mmsi the MMSI
     */
    public void setMmsi(final long mmsi) {
        this.mmsi = mmsi;
    }

    /**
     * Get the navigation status. Possible values are:
     * <ul>
     * <li>0: Under way using engine</li>
     * <li>1: At anchor</li>
     * <li>2: Not under command</li>
     * <li>3: Restricted maneuverability</li>
     * <li>4: Constrained by her draught</li>
     * <li>5: Moored</li>
     * <li>6: Aground</li>
     * <li>7: Engaged in fishing</li>
     * <li>8: Under way sailing</li>
     * <li>9: Reserved for future amendment of Navigational Status for HSC</li>
     * <li>10: Reserved for future amendment of Navigational Status for WIG</li>
     * <li>11: Reserved for future use</li>
     * <li>12: Reserved for future use</li>
     * <li>13: Reserved for future use</li>
     * <li>14: Reserved for future use</li>
     * <li>15: Not defined (default)</li>
     * </ul>
     * 
     * @return the navigation status
     */
    public int getNavigationStatus() {
        return navigationStatus;
    }

    /**
     * Set the navigation status.
     * 
     * @param navigationStatus the navigation status
     */
    public void setNavigationStatus(final int navigationStatus) {
        this.navigationStatus = navigationStatus;
    }

    /**
     * Get the rate of turn. Possible values are:
     * <ul>
     * <li>0: not turning</li>
     * <li>1 to 126: turning right at up to 708 degrees per minute or higher</li>
     * <li>-1 to - 126: turning left at up to 708 degrees per minute or higher</li>
     * <li>127: turning right at more than 5 degrees per 30 seconds (no turn information available)</li>
     * <li>-127: turning left at more than 5 degrees per 30seconds (no turn information available)</li>
     * <li>128: indicates no turn information available (default)</li>
     * </ul>
     * Decode values between 0 and 708 degrees/min by dividing the value through 4.733 and then squaring it. Preserve the sign field,
     * otherwise the left/right indication will be lost.
     * 
     * @return the rate of turn
     */
    public double getRateOfTurn() {
        return rateOfTurn;
    }

    /**
     * Set the rate of turn.
     * 
     * @param rateOfTurn the rate of turn
     */
    public void setRateOfTurn(final int rateOfTurn) {
        if (rateOfTurn == 0x80) {
            //System.out.println("defult");
            this.rateOfTurn = 0;
        } else if (rateOfTurn > 0x80) {
           // System.out.println("negtive");
            this.rateOfTurn = 128 - rateOfTurn;
        } else {
            this.rateOfTurn = rateOfTurn;
        }
        //this.rateOfTurn = rateOfTurn;
    }

    /**
     * Get the speed over ground. Possible values:
     * <ul>
     * <li>0.0-102.1: speed in knots</li>
     * <li>102.2: speed is 102.2 knots or higher</li>
     * <li>102.3: speed is not available</li>
     * </ul>
     * 
     * @return the speed over ground
     */
    public double getSpeedOverGround() {
        return speedOverGround;
    }

    /**
     * Set the speed over ground.
     * 
     * @param speedOverGround the speed over ground
     */
    public void setSpeedOverGround(final double speedOverGround) {
        this.speedOverGround = speedOverGround;
    }

    /**
     * Check whether the position is accurate. Possible values:
     * <ul>
     * <li>true: position is precise up to 10 meters</li>
     * <li>false: precision is lower than 10 meters (default)</li>
     * </ul>
     * 
     * @return whether the position is accurate
     */
    private boolean isPositionAccurate() {
        return positionAccurate;
    }

    /**
     * Set whether the position is accurate.
     * 
     * @param positionAccurate whether the position is accurate
     */
    private void setPositionAccurate(final boolean positionAccurate) {
        this.positionAccurate = positionAccurate;
    }

    /**
     * Get the longitude.<br />
     * Values up to plus or minus 180 degrees, East = positive, West = negative. A value of 181 degrees indicates that longitude is not
     * available and is the default.
     * 
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude.
     * 
     * @param longitude the longitude
     */
    public void setLongitude(final double raw_longitude) {
         if( raw_longitude >= 0x8000000 )
	    {
	        this.longitude = 0x10000000 - raw_longitude;
	        this.longitude *= -1;
	    } else {
	    	this.longitude = raw_longitude;
	    }
        //this.longitude = raw_longitude;
    }

    /**
     * Get the latitude.<br />
     * Values up to plus or minus 90 degrees, North = positive, South = negative. A value of 91 degrees indicates latitude is not available
     * and is the default.
     * 
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude.
     * 
     * @param latitude the latitude
     */
    public void setLatitude(final double raw_latitude) {
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
     * Get the course over ground. Possible values:
     * <ul>
     * <li>0.0-359.9: course over ground</li>
     * <li>360.0: data is not available</li>
     * </ul>
     * 
     * @return the course over ground
     */
    public double getCourseOverGround() {
        return courseOverGround;
    }

    /**
     * Set the course over ground.
     * 
     * @param courseOverGround the course over ground
     */
    public void setCourseOverGround(final double courseOverGround) {
        this.courseOverGround = courseOverGround;
    }

    /**
     * Get the true heading.
     * 
     * @return the true heading
     */
    public int getTrueHeading() {
        return trueHeading;
    }

    /**
     * Set the true heading.
     * 
     * @param trueHeading the true heading
     */
    public void setTrueHeading(final int trueHeading) {
        this.trueHeading = trueHeading;
    }

    /**
     * Get the timestamp. Possible values:
     * <ul>
     * <li>0-59: seconds in utc timestamp</li>
     * <li>60: time stamp is not available (default)</li>
     * <li>61: positioning system is in manual input mode</li>
     * <li>62: operates in estimated (dead reckoning) mode</li>
     * <li>63: inoperative</li>
     * </ul>
     * 
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp.
     * 
     * @param timestamp the timestamp
     */
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the maneuver indicator. Possible values:
     * <ul>
     * <li>0: Not available (default)</li>
     * <li>1: No special maneuver</li>
     * <li>2: Special maneuver (such as regional passing arrangement)</li>
     * </ul>
     * 
     * @return the maneuver indicator
     */
    private int getManeuverIndicator() {
        return maneuverIndicator;
    }

    /**
     * Set the maneuver indicator.
     * 
     * @param maneuverIndicator the maneuver indicator
     */
    private void setManeuverIndicator(final int maneuverIndicator) {
        this.maneuverIndicator = maneuverIndicator;
    }

    /**
     * Check whether the RAIM system is used. The RAIM flag indicates whether Receiver Autonomous Integrity Monitoring is being used to
     * check the performance of the EPFD. See http://en.wikipedia.org/wiki/Receiver_Autonomous_Integrity_Monitoring for more information.
     * 
     * @return whether RAIM is used
     */
    private boolean isRaimUsed() {
        return raimUsed;
    }

    /**
     * Set whether RAIM is used.
     * 
     * @param raimUsed whether RAIM is used
     */
    private void setRaimUsed(final boolean raimUsed) {
        this.raimUsed = raimUsed;
    }

    /**
     * Get the radio status.
     * 
     * @return the radio status
     */
    private int getRadioStatus() {
        return radioStatus;
    }

    /**
     * Set the radio status.
     * 
     * @param radioStatus the radio status
     */
    private void setRadioStatus(final int radioStatus) {
        this.radioStatus = radioStatus;
    }
}
