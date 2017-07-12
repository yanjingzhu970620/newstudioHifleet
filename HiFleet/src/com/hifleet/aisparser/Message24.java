package com.hifleet.aisparser;

/**
 * AIS Message 24 class
 * Class B"CS" Static Data Report
 * 
 */
public class Message24 extends Messages {
    
    int part_number;       // 2 bits   : Part Number
    //!< Message 24A 
    String name;              // 120 bits : Ship Name in ASCII
    //!< Message 24B 
    int ship_type;         // 8 bits   : Type of Ship and Cargo
    String vendor_id;         // 42 bits  : Vendor ID in ASCII
    String callsign;          // 42 bits  : Callsign in ASCII
    int dim_bow;           // 9 bits   : GPS Ant. Distance from Bow
    int dim_stern;         // 9 bits   : GPS Ant. Distance from Stern
    int dim_port;          // 6 bits   : GPS Ant. Distance from Port
    int dim_starboard;     // 6 bits   : GPS Ant. Distance from Starboard
//    int spare;             // 6 bits   : Spare
    int flags;             // A/B flags - A = 1  B = 2  Both = 3

    public int getPartNumber() {
        return this.part_number;
    }
    
    public String getShipName() {
        return this.name;
    }
    
    public int getShipType() {
        return this.ship_type;
    }

//    public String vendor_id() {
//        return this.vendor_id;
//    }
    public String getCallSign() {
        return this.callsign;
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

//    public int spare() {
//        return this.spare;
//    }
//
    private int flags() {
        return this.flags;
    }
    
    public Message24() {
        super();
    }
    
    public ClassB24ReportDTO parse(Sixbit six_state)
            throws SixbitsExhaustedException, AISMessageException {
        final ClassB24ReportDTO report = new ClassB24ReportDTO();
        super.parse(24, six_state);
        report.setMmsi(super.getMmsi());
        this.part_number = (int) six_state.get(2);
        report.setPart(part_number);
        if (this.part_number == 0) {
            /* Parse 24A */
            /* Get the Ship Name, convert to ASCII */
            //this.name = ;
            report.setName(six_state.get_string(18));
            /* Indicate reception of part A */
            this.flags |= 0x01;
        } else if (this.part_number == 1) {
            /* Parse 24B */
            
            report.setShipType((int) six_state.get(8));
            //this.vendor_id = six_state.get_string(7);
            six_state.get_string(7);
            report.setCallsign(six_state.get_string(7));
            report.setDimensionToBow((int) six_state.get(9));
            report.setDimensionToStern((int) six_state.get(9));
            report.setDimensionToPort((int) six_state.get(6));
            report.setDimensionToStarboard((int) six_state.get(6));          
            //this.spare = (int) six_state.get(6);

            /* Indicate reception of part A */
            this.flags |= 0x02;
        } else {
            throw new AISMessageException("Unknown Message 24 Part #");            
        }
        return report;
    }
}
