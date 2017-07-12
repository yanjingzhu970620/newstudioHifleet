package com.hifleet.bean;

public class Plot {

	String id,name,type,shape,colorType;
	
	public String getColorType() {
		return colorType;
	}
	public void setColorType(String colorType) {
		this.colorType = colorType;
	}
	public void setShape(String s){
		shape=s;
	}
	public String getShape(){
		return shape;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public AlertAreaShape[] getAlertshapes() {
		return alertshapes;
	}

	public void setAlertshapes(AlertAreaShape[] alertshapes) {
		this.alertshapes = alertshapes;
	}

	public String getIntesect() {
		return intesect;
	}

	public void setIntesect(String intesect) {
		this.intesect = intesect;
	}

	String center;
	
	AlertAreaShape[] alertshapes= new AlertAreaShape[3];
	
	String intesect;
	
}
