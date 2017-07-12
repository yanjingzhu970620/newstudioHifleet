package com.hifleet.map;

public class OfflineDataRegion {
	private double maxlon,maxlat,minlon,minlat;
	private int id;
	
	public OfflineDataRegion(double minlon,double minlat,double maxlon,double maxlat,int id){
		this.maxlon = maxlon;
		this.maxlat = maxlat;
		this.minlon=minlon;
		this.minlat = minlat;
		this.id=id;
	}
	
	public OfflineDataRegion(){
		
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public int getId(){
		return this.id;
	}
	
	
	/**
	 * @param maxlon the maxlon to set
	 */
	public void setMaxlon(double maxlon) {
		this.maxlon = maxlon;
	}

	/**
	 * @param maxlat the maxlat to set
	 */
	public void setMaxlat(double maxlat) {
		this.maxlat = maxlat;
	}

	/**
	 * @param minlon the minlon to set
	 */
	public void setMinlon(double minlon) {
		this.minlon = minlon;
	}

	/**
	 * @param minlat the minlat to set
	 */
	public void setMinlat(double minlat) {
		this.minlat = minlat;
	}

	public double getMaxlat(){
		return maxlat;
	}
	public double getMaxlon(){
		return maxlon;
	}
	public double getMinlat(){
		return minlat;
	}
	public double getMinlon(){
		return minlon;
	}
	
	
}
