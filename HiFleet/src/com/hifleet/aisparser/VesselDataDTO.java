/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;



/**
 * This class holds information about a vessel. This is the information of ais message type 5.<br />
 * Refer to http://gpsd.berlios.de/AIVDM.html#_type_5_static_and_voyage_related_data for more information.
 * 
 * @author Patrick Gotthard <mailto:patrick.gotthard@bader-jene.de>
 * 
 */
public class VesselDataDTO {

    private int aisMessageType;
    private int repeatIndicator;
    private long mmsi;
    private int aisVersion;
    private long imoNumber;
    private String callSign;
    private String vesselName;
    private int shipType;
    private int dimensionToBow;
    private int dimensionToStern;
    private int dimensionToPort;
    private int dimensionToStarboard;
    private int positionFixType;
    private int etaMonth;
    private int etaDay;
    private int etaHour;
    private int etaMinute;
    private String Eta;
    private double draught;
    private String destination;
    private boolean terminalReady;

    
   public void setEta(String eta){
       this.Eta=eta;
   }
   
   public String getEta(){
       return this.Eta;
   }
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
     * @return the repeat indicator
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
     * Get the used AIS version. Possible values:
     * <ul>
     * <li>0: ITU1371</li>
     * <li>1-3: future editions</li>
     * </ul>
     * 
     * @return the used AIS version
     */
    private int getAisVersion() {
        return aisVersion;
    }

    /**
     * Set the used AIS version.
     * 
     * @param aisVersion the AIS version
     */
    private void setAisVersion(final int aisVersion) {
        this.aisVersion = aisVersion;
    }

    /**
     * Get the IMO number.
     * 
     * @return the IMO number
     */
    public long getImoNumber() {
        return imoNumber;
    }

    /**
     * Set the IMO number.
     * 
     * @param imoNumber the IMO number
     */
    public void setImoNumber(final long imoNumber) {
        this.imoNumber = imoNumber;
    }

    /**
     * Get the callsign.
     * 
     * @return the callsign
     */
    public String getCallSign() {
        return callSign;
    }

    /**
     * Set the callsign.
     * 
     * @param callSign the callsign
     */
    public void setCallSign(final String callSign) {
        this.callSign = callSign;
    }

    /**
     * Get the vesselname .
     * 
     * @return the vesselname
     */
    public String getVesselName() {
        return vesselName;
    }

    /**
     * Set the vesselname.
     * 
     * @param vesselName the vesselname
     */
    public void setVesselName(final String vesselName) {
        this.vesselName = vesselName;
    }

    /**
     * Get the shiptype. Possible values are:
     * <ul>
     * <li>0: Not available (default)</li>
     * <li>1-19: Reserved for future use</li>
     * <li>20: Wing in ground (WIG), all ships of this type</li>
     * <li>21: Wing in ground (WIG), Hazardous category A</li>
     * <li>22: Wing in ground (WIG), Hazardous category B</li>
     * <li>23: Wing in ground (WIG), Hazardous category C</li>
     * <li>24: Wing in ground (WIG), Hazardous category D</li>
     * <li>25: Wing in ground (WIG), Reserved for future use</li>
     * <li>26: Wing in ground (WIG), Reserved for future use</li>
     * <li>27: Wing in ground (WIG), Reserved for future use</li>
     * <li>28: Wing in ground (WIG), Reserved for future use</li>
     * <li>29: Wing in ground (WIG), Reserved for future use</li>
     * <li>30: Fishing</li>
     * <li>31: Towing</li>
     * <li>32: Towing: length exceeds 200m or breadth exceeds 25m</li>
     * <li>33: Dredging or underwater ops</li>
     * <li>34: Diving ops</li>
     * <li>35: Military ops</li>
     * <li>36: Sailing</li>
     * <li>37: Pleasure Craft</li>
     * <li>38: Reserved</li>
     * <li>39: Reserved</li>
     * <li>40: High speed craft (HSC), all ships of this type</li>
     * <li>41: High speed craft (HSC), Hazardous category A</li>
     * <li>42: High speed craft (HSC), Hazardous category B</li>
     * <li>43: High speed craft (HSC), Hazardous category C</li>
     * <li>44: High speed craft (HSC), Hazardous category D</li>
     * <li>45: High speed craft (HSC), Reserved for future use</li>
     * <li>46: High speed craft (HSC), Reserved for future use</li>
     * <li>47: High speed craft (HSC), Reserved for future use</li>
     * <li>48: High speed craft (HSC), Reserved for future use</li>
     * <li>49: High speed craft (HSC), No additional information</li>
     * <li>50: Pilot Vessel</li>
     * <li>51: Search and Rescue vessel</li>
     * <li>52: Tug</li>
     * <li>53: Port Tender</li>
     * <li>54: Anti-pollution equipment</li>
     * <li>55: Law Enforcement</li>
     * <li>56: Spare - Local Vessel</li>
     * <li>57: Spare - Local Vessel</li>
     * <li>58: Medical Transport</li>
     * <li>59: Noncombatant ship according to RR Resolution No. 18</li>
     * <li>60: Passenger, all ships of this type</li>
     * <li>61: Passenger, Hazardous category A</li>
     * <li>62: Passenger, Hazardous category B</li>
     * <li>63: Passenger, Hazardous category C</li>
     * <li>64: Passenger, Hazardous category D</li>
     * <li>65: Passenger, Reserved for future use</li>
     * <li>66: Passenger, Reserved for future use</li>
     * <li>67: Passenger, Reserved for future use</li>
     * <li>68: Passenger, Reserved for future use</li>
     * <li>69: Passenger, No additional information</li>
     * <li>70: Cargo, all ships of this type</li>
     * <li>71: Cargo, Hazardous category A</li>
     * <li>72: Cargo, Hazardous category B</li>
     * <li>73: Cargo, Hazardous category C</li>
     * <li>74: Cargo, Hazardous category D</li>
     * <li>75: Cargo, Reserved for future use</li>
     * <li>76: Cargo, Reserved for future use</li>
     * <li>77: Cargo, Reserved for future use</li>
     * <li>78: Cargo, Reserved for future use</li>
     * <li>79: Cargo, No additional information</li>
     * <li>80: Tanker, all ships of this type</li>
     * <li>81: Tanker, Hazardous category A</li>
     * <li>82: Tanker, Hazardous category B</li>
     * <li>83: Tanker, Hazardous category C</li>
     * <li>84: Tanker, Hazardous category D</li>
     * <li>85: Tanker, Reserved for future use</li>
     * <li>86: Tanker, Reserved for future use</li>
     * <li>87: Tanker, Reserved for future use</li>
     * <li>88: Tanker, Reserved for future use</li>
     * <li>89: Tanker, No additional information</li>
     * <li>90: Other Type, all ships of this type</li>
     * <li>91: Other Type, Hazardous category A</li>
     * <li>92: Other Type, Hazardous category B</li>
     * <li>93: Other Type, Hazardous category C</li>
     * <li>94: Other Type, Hazardous category D</li>
     * <li>95: Other Type, Reserved for future use</li>
     * <li>96: Other Type, Reserved for future use</li>
     * <li>97: Other Type, Reserved for future use</li>
     * <li>98: Other Type, Reserved for future use</li>
     * <li>99: Other Type, no additional information</li>
     * <li>Higher than 99: this values should be treated like 0</li>
     * </ul>
     * 
     * @return the shiptype
     */
    public int getShipType() {
        return shipType;
    }

    /**
     * Set the shiptype.
     * 
     * @param shipType the shiptype
     */
    public void setShipType(final int shipType) {
        this.shipType = shipType;
    }

    /**
     * Get the dimension to bow.<br />
     * <ul>
     * <li>0: not available</li>
     * <li>1-510: (value) meters</li>
     * <li>511: 511 meters or greater</li>
     * </ul>
     * 
     * @return the dimension to bow
     */
    public int getDimensionToBow() {
        return dimensionToBow;
    }

    /**
     * Set the dimension to bow.
     * 
     * @param dimensionToBow the dimension to bow
     */
    public void setDimensionToBow(final int dimensionToBow) {
        this.dimensionToBow = dimensionToBow;
    }

    /**
     * Get the dimension to stern.
     * <ul>
     * <li>0: not available</li>
     * <li>1-510: (value) meters</li>
     * <li>511: 511 meters or greater</li>
     * </ul>
     * 
     * @return the dimension to stern
     */
    public int getDimensionToStern() {
        return dimensionToStern;
    }

    /**
     * Set the dimension to stern.
     * 
     * @param dimensionToStern the dimension to stern
     */
    public void setDimensionToStern(final int dimensionToStern) {
        this.dimensionToStern = dimensionToStern;
    }

    /**
     * Get the dimension to port.
     * <ul>
     * <li>0: not available</li>
     * <li>0-62: (value) meters</li>
     * <li>63: 63 meters or greater</li>
     * </ul>
     * 
     * @return the dimension to port
     */
    public int getDimensionToPort() {
        return dimensionToPort;
    }

    /**
     * Set the dimension to port.
     * 
     * @param dimensionToPort the dimension to port
     */
    public void setDimensionToPort(final int dimensionToPort) {
        this.dimensionToPort = dimensionToPort;
    }

    /**
     * Get the dimension to starboard.
     * <ul>
     * <li>0: not available</li>
     * <li>1-62: (value) meters</li>
     * <li>63: 63 meters or greater</li>
     * </ul>
     * 
     * @return the dimension to starboard
     */
    public int getDimensionToStarboard() {
        return dimensionToStarboard;
    }

    /**
     * Set the dimension to starboard.
     * 
     * @param dimensionToStarboard the dimension to starboard
     */
    public void setDimensionToStarboard(final int dimensionToStarboard) {
        this.dimensionToStarboard = dimensionToStarboard;
    }

    /**
     * Get the type of the position fix. Possible values:
     * <ul>
     * <li>0: Undefined (default)</li>
     * <li>1: GPS</li>
     * <li>2: GLONASS</li>
     * <li>3: Combined GPS/GLONASS</li>
     * <li>4: Loran-C</li>
     * <li>5: Chayka</li>
     * <li>6: Integrated navigation system</li>
     * <li>7: Surveyed</li>
     * <li>8: Galileo</li>
     * </ul>
     * 
     * @return the type of the position fix
     */
    private int getPositionFixType() {
        return positionFixType;
    }

    /**
     * Set the type of the position fix.
     * 
     * @param positionFixType the type of the position fix
     */
    private void setPositionFixType(final int positionFixType) {
        this.positionFixType = positionFixType;
    }

    /**
     * Get the estimated months to arrival. Possible values:
     * <ul>
     * <li>0: not available (default)</li>
     * <li>1-12</li>
     * </ul>
     * 
     * @return the estimated months to arrival
     */
    private int getEtaMonth() {
        return etaMonth;
    }

    /**
     * Set the estimated months to arrival
     * 
     * @param etaMonth the estimated months to arrival
     */
    private void setEtaMonth(final int etaMonth) {
        this.etaMonth = etaMonth;
    }

    /**
     * Get the estimated days to arrival. Possible values:
     * <ul>
     * <li>0: not available (default)</li>
     * <li>1-31</li>
     * </ul>
     * 
     * @return the the estimated days to arrival
     */
    private int getEtaDay() {
        return etaDay;
    }

    /**
     * Set the estimated days to arrival.
     * 
     * @param etaDay the ETA in days
     */
    private void setEtaDay(final int etaDay) {
        this.etaDay = etaDay;
    }

    /**
     * Get the the estimated hours to arrival. Possible values:
     * <ul>
     * <li>0-23</li>
     * <li>24: not available (default)</li>
     * </ul>
     * 
     * @return the estimated hours to arrival
     */
    private int getEtaHour() {
        return etaHour;
    }

    /**
     * Set the estimated hours to arrival.
     * 
     * @param etaHour the estimated hours to arrival
     */
    private void setEtaHour(final int etaHour) {
        this.etaHour = etaHour;
    }

    /**
     * Get the estimated minutes to arrival. Possible values:
     * <ul>
     * <li>0-59</li>
     * <li>60: not available (default</li>
     * </ul>
     * 
     * @return the estimates minutes to arrival
     */
    private int getEtaMinute() {
        return etaMinute;
    }

    /**
     * Set the estimated minutes to arrival.
     * 
     * @param etaMinute the estimated time to arrival
     */
    private void setEtaMinute(final int etaMinute) {
        this.etaMinute = etaMinute;
    }

    /**
     * Get the draught.
     * 
     * @return the draught
     */
    public double getDraught() {
        return draught;
    }

    /**
     * Set the draught.
     * 
     * @param draught the draught
     */
    public void setDraught(final double draught) {
        this.draught = draught;
    }

    /**
     * Get the destination.
     * 
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Set the destination.
     * 
     * @param destination the destinatiob
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * Check whether the terminal is ready.
     * 
     * @return whether the terminal is ready.
     */
    private boolean isTerminalReady() {
        return terminalReady;
    }

    /**
     * Set whether the terminal is ready.
     * 
     * @param terminalReady whether the terminal is ready
     */
    private void setTerminalReady(final boolean terminalReady) {
        this.terminalReady = terminalReady;
    }
}
