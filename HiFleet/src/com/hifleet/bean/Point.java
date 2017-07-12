package com.hifleet.bean;

import java.util.ArrayList;
import java.util.Map;

public class Point {

	//private double lon,lat;
	private Center center;
	private String colorType;
	
	public String getColorType() {
		return colorType;
	}

	public void setColorType(String colorType) {
		this.colorType = colorType;
	}

	public void setCenter(Center c){
		center =c;
	}
	
	public Center getCenter(){
		return center;
	}
	private String name;
	
	public void setName(String n){
		name = n;
	}
	
	public String getName(){
		return name;
	}
	
private ArrayList<ArrayList<MetaPolygon>> alertareaes;
	
	public ArrayList<ArrayList<MetaPolygon>> getAlertareaes() {
		return alertareaes;
	}
	public void setAlertareaes(
			ArrayList<ArrayList<MetaPolygon>> alertareaes) {
		this.alertareaes = alertareaes;
	}

//	public double getLon() {
//		return lon;
//	}
//
//	public void setLon(double lon) {
//		this.lon = lon;
//	}
//
//	public double getLat() {
//		return lat;
//	}
//
//	public void setLat(double lat) {
//		this.lat = lat;
//	}
	
	
}
