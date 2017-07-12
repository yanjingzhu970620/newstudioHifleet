package com.hifleet.bean;

public class AlertBeans {
	public String AlertRsId;
	public String Mmsi;
	public String AlertConditionId;
	public String AlertAreaId;
	public String AlertAreaName;
	public String Position;
	public String TriggerTime;
	public String EndTime;
	public String AlertType;
	public String ShipName;
	public String Dn;
	
	private String shipname;
	/**
	 * @return the shipname
	 */
	public String getShipname() {
		return shipname;
	}
	/**
	 * @param shipname the shipname to set
	 */
	public void setShipname(String shipname) {
		this.shipname = shipname;
	}
	/**
	 * @return the mmsi
	 */
	public String getMmsi() {
		return mmsi;
	}
	/**
	 * @param mmsi the mmsi to set
	 */
	public void setMmsi(String mmsi) {
		this.mmsi = mmsi;
	}
	/**
	 * @return the alertconditionid
	 */
	public String getAlertconditionid() {
		return alertconditionid;
	}
	/**
	 * @param alertconditionid the alertconditionid to set
	 */
	public void setAlertconditionid(String alertconditionid) {
		this.alertconditionid = alertconditionid;
	}
	/**
	 * @return the caleta
	 */
	public String getCaleta() {
		return caleta;
	}
	/**
	 * @param caleta the caleta to set
	 */
	public void setCaleta(String caleta) {
		this.caleta = caleta;
	}
	/**
	 * @return the reporteta
	 */
	public String getReporteta() {
		return reporteta;
	}
	/**
	 * @param reporteta the reporteta to set
	 */
	public void setReporteta(String reporteta) {
		this.reporteta = reporteta;
	}
	/**
	 * @return the dn
	 */
	public String getDn() {
		return dn;
	}
	/**
	 * @param dn the dn to set
	 */
	public void setDn(String dn) {
		this.dn = dn;
	}
	private String mmsi;
	private String alertconditionid;
	private String caleta;
	private String reporteta;
	private String dn;//eta beans
	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		if(AlertType!=null){
			String type;
			if(AlertType.equals("A")){
				type="超速";
			}else if(AlertType.equals("B")){
				type="进入";
			}else if(AlertType.equals("C")){
				type="反航向";
			}else if(AlertType.equals("D")){
				type="走锚";
			}else if(AlertType.equals("E")){
				type="靠泊";
			}else if(AlertType.equals("F")){
				type="穿过";
			}else if(AlertType.equals("G")){
				type="低硫区";
			}else if(AlertType.equals("H")){
				type="超速";
			}else if(AlertType.equals("I")){
				type="海浪";
			}else if(AlertType.equals("J")){
				type="走锚";
			}else{
				type=AlertType;
			}
			return type;
		}else{
		return AlertType;
		}
	}
	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		AlertType = alertType;
	}
	public String An;
	public String ShipSpeed;
}
