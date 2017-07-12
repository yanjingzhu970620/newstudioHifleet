package com.hifleet.aisparser;

/**
 * AIS Message 19 class
 * Extended Class B Equipment Position Report
 * 
 */
public class Message19 extends Messages {

    int regional1;         // 8 bits   : Regional Bits
    int sog;               // 10 bits  : Speed Over Ground
    int pos_acc;           // 1 bit    : Position Accuracy
    Position pos;               //          : Lat/Long 1/100000 minute
    int cog;               // 12 bits  : Course Over Ground
    int true_heading;      // 9 bits   : True Heading
    int utc_sec;           // 6 bits   : UTC Seconds
    int regional2;         // 4 bits   : Regional Bits
    String name;              // 120 bits : Ship Name in ASCII
    int ship_type;         // 8 bits   : Type of Ship and Cargo
    int dim_bow;           // 9 bits   : GPS Ant. Distance from Bow
    int dim_stern;         // 9 bits   : GPS Ant. Distance from Stern
    int dim_port;          // 6 bits   : GPS Ant. Distance from Port
    int dim_starboard;     // 6 bits   : GPS Ant. Distance from Starboard
//    int pos_type;          // 4 bits   : Type of Position Fixing Device
//    int raim;              // 1 bit    : RAIM Flag
//    int dte;               // 1 bit    : DTE Flag
//    int spare;             // 5 bits   : Spare

//    public int regional1() {
//        return this.regional1;
//    }
    public int getSpeedOverGround() {
        return this.sog;
    }

//    public int pos_acc() {
//        return this.pos_acc;
//    }
    public double getLongitude() {
        return this.pos.longitude();
    }

    public double getLatitude() {
        return this.pos.latitude();
    }

    public int getCoureseOverGround() {
        return this.cog;
    }

    public int getTrueHeading() {
        return this.true_heading;
    }

//    public int utc_sec() {
//        return this.utc_sec;
//    }
//    public int regional2() {
//        return this.regional2;
//    }
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

//    public int pos_type() {
//        return this.pos_type;
//    }
//
//    public int raim() {
//        return this.raim;
//    }
//
//    public int dte() {
//        return this.dte;
//    }
//
//    public int spare() {
//        return this.spare;
//    }
    public Message19() {
        super();
    }

    public ClassB19PositionReportDTO parse(Sixbit six_state) {
        try {
            final ClassB19PositionReportDTO report = new ClassB19PositionReportDTO();
            super.parse(19, six_state);
            report.setMmsi(super.getMmsi());
            six_state.get(8);
            report.setSpeedOverGround((int) six_state.get(10) / 10.0);

            six_state.get(1);

            report.setLongitude((double) (six_state.get(28)));
            report.setLatitude((double) (six_state.get(27)));

            report.setCourseOverGround((int) six_state.get(12) / 10.0);

            report.setTrueHeading((int) six_state.get(9));
            six_state.get(6);
            six_state.get(4);
            report.setVesselName(six_state.get_string(20));
            report.setShipType((int) six_state.get(8));
            report.setDimensionToBow((int) six_state.get(9));
            report.setDimensionToStern((int) six_state.get(9));
            report.setDimensionToPort((int) six_state.get(6));
            report.setDimensionToStarboard((int) six_state.get(6));
            return report;
        } catch (Exception ex) {
            return null;
        }
    }
}
