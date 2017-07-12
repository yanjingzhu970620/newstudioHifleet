/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hifleet.aisparser;
//import com.manyships.parser.*;
//import com.manyships.parser.classB.*;

/**  
 * This is the interface for ais observers.  
 *   
 * @author Patrick Gotthard <mailto:patrick.gotthard@bader-jene.de>  
 *   
 */
public interface AISObserver {

    /**      
     * Receive notifications for new position reports.      
     *       
     * @param report Position report      
     */
    void update(Class123PositionReportDTO report);

    void update(VesselDataDTO data);

    void update(ClassB18PositionReportDTO report);

    void update(ClassB19PositionReportDTO report);

    void update(ClassB24ReportDTO data);
    
    void update(Message21Report report);
}
