package com.hifleet.bean;
import java.util.*;
public class Line {

	//private String polygon;
	
	private String name;
	
	private Center center;
	private String colorType;
	
//	public String getPolygon() {
//		return polygon;
//	}
//
//	public void setPolygon(String polygon) {
//		this.polygon = polygon;
//	}

	public String getColorType() {
		return colorType;
	}

	public void setColorType(String colorType) {
		this.colorType = colorType;
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

	private List<Map<String,Double>> points;
	
	private ArrayList<ArrayList<MetaPolygon>> alertareaes;
	
	public ArrayList<ArrayList<MetaPolygon>> getAlertareaes() {
		return alertareaes;
	}
	public void setAlertareaes(
			ArrayList<ArrayList<MetaPolygon>> alertareaes) {
		this.alertareaes = alertareaes;
	}
	
	
}
