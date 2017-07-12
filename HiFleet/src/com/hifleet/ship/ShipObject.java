package com.hifleet.ship;

public class ShipObject extends sShipObject{
	private String callsign,imo,flag,type;
	private double length,width;
	public String getCallsign() {
		return callsign;
	}
	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}
	public String getImo() {
		return imo;
	}
	public void setImo(String imo) {
		this.imo = imo;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public String getNavStatus() {
		return navStatus;
	}
	public void setNavStatus(String navStatus) {
		this.navStatus = navStatus;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getEta() {
		return eta;
	}
	public void setEta(String eta) {
		this.eta = eta;
	}
	public double getDraught() {
		return draught;
	}
	public void setDraught(double draught) {
		this.draught = draught;
	}
	public double getHeading() {
		return heading;
	}
	public void setHeading(double heading) {
		this.heading = heading;
	}
	public double getRot() {
		return rot;
	}
	public void setRot(double rot) {
		this.rot = rot;
	}
	public String getFlagname() {
		return flagname;
	}
	public void setFlagname(String flagname) {
		this.flagname = flagname;
	}
	public boolean isDisplayed() {
		return displayed;
	}
	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
	public boolean isNamedisplayed() {
		return namedisplayed;
	}
	public void setNamedisplayed(boolean namedisplayed) {
		this.namedisplayed = namedisplayed;
	}
	public boolean isIsfleetship() {
		return isfleetship;
	}
	public void setIsfleetship(boolean isfleetship) {
		this.isfleetship = isfleetship;
	}
	private String navStatus,destination,eta;
	private double draught,heading,rot;
	private String flagname;
	private boolean displayed,namedisplayed,isfleetship;
}
