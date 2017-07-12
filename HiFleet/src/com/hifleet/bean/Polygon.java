package com.hifleet.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Polygon {

	private String name;
	private Center center;
	private List<Map<String,Double>> points;
	private String colorType;
	
	
	public String getColorType() {
		return colorType;
	}
	public void setColorType(String colorType) {
		this.colorType = colorType;
	}
	private ArrayList<ArrayList<MetaPolygon>> alertareaes;
	
	public ArrayList<ArrayList<MetaPolygon>> getAlertareaes() {
		return alertareaes;
	}
	public void setAlertareaes(
			ArrayList<ArrayList<MetaPolygon>> alertareaes) {
		this.alertareaes = alertareaes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Center getCenter() {
		return center;
	}
	public void setCenter(Center center) {
		this.center = center;
	}
	public List<Map<String,Double>> getPoints() {
		return points;
	}
	public void setPoints(List<Map<String,Double>> points) {
		this.points = points;
	}
}
