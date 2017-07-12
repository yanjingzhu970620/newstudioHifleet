package com.hifleet.aisparser;

/**
 * AIS Message 5 class
 * Static and Voyage Related Data
 * 
 */
public class Message5 extends Messages {

    long mmsi;
    int version;           // 2 bits          : AIS Version
    long imo;               // 30 bits         : IMO Number
    String callsign;          // 7x6 (42) bits   : Callsign
    String name;              // 20x6 (120) bits : Ship Name
    int ship_type;         // 8 bits          : Type of Ship and Cargo
    int dim_bow;           // 9 bits          : GPS Ant. Distance from Bow
    int dim_stern;         // 9 bits          : GPS Ant. Distance from stern
    int dim_port;          // 6 bits          : GPS Ant. Distance from port
    int dim_starboard;     // 6 bits          : GPS Ant. Distance from starboard
    int pos_type;          // 4 bits          : Type of position fixing device
    //long eta;               // 20 bits         : Estimated Time of Arrival MMDDHHMM
    ETA eta;
    int draught;           // 8 bits          : Maximum present static draught
    String dest;              // 6x20 (120) bits : Ship Destination
//    int dte;               // 1 bit           : DTE flag
//    int spare;             // 1 bit           : spare

//    private int version() {
//        return this.version;
//    }
    public long getImo() {
        return this.imo;
    }

    public String getCallsign() {
        return this.callsign;
    }

    public String getShipName() {
        return this.name;
    }

    public int getShipType() {
        return this.ship_type;
    }

    public int getDimToBow() {
        return this.dim_bow;
    }

    public int getDimToStern() {
        return this.dim_stern;
    }

    public int getDimToPort() {
        return this.dim_port;
    }

    public int getDimToStarboard() {
        return this.dim_starboard;
    }

//    private int pos_type() {
//        return this.pos_type;
//    }
    public ETA getETA() {
        return this.eta;
    }

    public int getDraught() {
        return this.draught;
    }

    public String getDestination() {
        return this.dest;
    }

    public Message5() {
        super();
    }

    public VesselDataDTO parse(Sixbit six_state) {
        try {
            final VesselDataDTO data = new VesselDataDTO();
            super.parse(5, six_state);
            data.setMmsi(super.getMmsi());
            // this.version = (int) six_state.get(2);
            six_state.get(2);
            data.setImoNumber((long) six_state.get(30));
            data.setCallSign(six_state.get_string(7));
            data.setVesselName(six_state.get_string(20));
            data.setShipType((int) six_state.get(8));
            data.setDimensionToBow((int) six_state.get(9));
            data.setDimensionToStern((int) six_state.get(9));
            data.setDimensionToPort((int) six_state.get(6));
            data.setDimensionToStarboard((int) six_state.get(6));
            //this.pos_type = (int) six_state.get(4);
            six_state.get(4);
            this.eta = new ETA();
            eta.setMonth((int) six_state.get(4));
            eta.setDay((int) six_state.get(5));
            eta.setHour((int) six_state.get(5));
            eta.setMinute((int) six_state.get(6));
            data.setEta(eta.getETA());
            //this.eta = (long) six_state.get(20);
            data.setDraught((int) six_state.get(8) / 10.0);
            data.setDestination(six_state.get_string(20));
            return data;
        } catch (Exception ex) {
            return null;
        }
    }
}
