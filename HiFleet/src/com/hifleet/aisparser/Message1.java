package com.hifleet.aisparser;

/**
 * AIS Message 1 class
 * Position Report
 * 
 */
public class Message1 extends Messages {

    //private long mmsi;
    private int nav_status;        // 4 bits  : Navigational Status
    private int rot;               // 8 bits  : Rate of Turn
    private int sog;               // 10 bits : Speed Over Ground
    private int pos_acc;           // 1 bit   : Position Accuracy
    private Position pos;               //         : Lat/Long 1/10000 minute
    private int cog;               // 12 bits : Course over Ground
    private int true_heading;      // 9 bits  : True heading
    // int utc_sec;           // 6 bits  : UTC Seconds
    // int regional;          // 4 bits  : Regional bits
    //  int spare;             // 1 bit   : Spare
    //  int raim;              // 1 bit   : RAIM flag
    //  int sync_state;        // 2 bits  : SOTDMA sync state
    //  int slot_timeout;      // 3 bits  : SOTDMA Slot Timeout
    //   int sub_message;       // 14 bits : SOTDMA sub-message

    public Message1() {
        super();
    }

    public Class123PositionReportDTO parse(Sixbit six_state) {
        try {
            final Class123PositionReportDTO report = new Class123PositionReportDTO();
           
            super.parse(1, six_state);
           
            // super.getMmsi();
        /* Parse the Message 1 */
            report.setMmsi(super.getMmsi());
           // System.out.println("six_state:"+report.getMmsi());
            // this.setNav_status((int) six_state.get(4));
            report.setNavigationStatus((int) six_state.get(4));
           // System.out.println("111:"+six_state.get(4));
            // this.setRot((int) six_state.get(8));
            report.setRateOfTurn((int) six_state.get(8));
          //  System.out.println("222:"+report.getMmsi());
            report.setSpeedOverGround((int) six_state.get(10) / 10.0);
            six_state.get(1);
           // System.out.println("333:"+report.getMmsi());
            //this.pos = new Position();
            report.setLongitude((double) (six_state.get(28)));
            report.setLatitude((double) (six_state.get(27)));

            report.setCourseOverGround((int) six_state.get(12) / 10.0);
            report.setTrueHeading((int) six_state.get(9));
           
            return report;
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }

    /**
     * @return the mmsi
     */
//    public long getMmsi() {
//        return mmsi;
//    }
    /**
     * @param mmsi the mmsi to set
     */
//    private void setMmsi(long mmsi) {
//        this.mmsi = mmsi;
//    }
    /**
     * @return the nav_status
     */
    public int getNavigationStatus() {
        return nav_status;
    }

    /**
     * @param nav_status the nav_status to set
     */
    private void setNav_status(int nav_status) {
        this.nav_status = nav_status;
    }

    /**
     * @return the rot
     */
    public int getRateOfTurn() {
        return rot;
    }

    /**
     * @param rot the rot to set
     */
    private void setRot(int rot) {
        this.rot = rot;
    }

    /**
     * @return the sog
     */
    public int getSpeedOverGround() {
        return sog;
    }

    /**
     * @param sog the sog to set
     */
    private void setSog(int sog) {
        this.sog = sog;
    }

    /**
     * @return the pos_acc
     */
//    public int getPos_acc() {
//        return pos_acc;
//    }
    /**
     * @param pos_acc the pos_acc to set
     */
    /**
     * @return the pos
     */
//    public Position getPosition() {
//        return pos;
//    }
    public double getLongitude() {
        return this.pos.longitude();
    }

    public double getLatitude() {
        return this.pos.latitude();
    }

    /**
     * @param pos the pos to set
     */
    /**
     * @return the cog
     */
    public int getCourseOverGround() {
        return cog;
    }

    /**
     * @param cog the cog to set
     */
    private void setCog(int cog) {
        this.cog = cog;
    }

    /**
     * @return the true_heading
     */
    public int getTrueHeading() {
        return true_heading;
    }

    /**
     * @param true_heading the true_heading to set
     */
    private void setTrue_heading(int true_heading) {
        this.true_heading = true_heading;
    }

    /**
     * @return the pos_acc
     */
    private int getPos_acc() {
        return pos_acc;
    }

    /**
     * @param pos_acc the pos_acc to set
     */
    private void setPos_acc(int pos_acc) {
        this.pos_acc = pos_acc;
    }

    /**
     * @param pos the pos to set
     */
    private void setPos(Position pos) {
        this.pos = pos;
    }
}
