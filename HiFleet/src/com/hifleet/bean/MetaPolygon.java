package com.hifleet.bean;
import java.util.*;
public class MetaPolygon {

	private ArrayList<HashMap<String,Double>> points = new ArrayList<HashMap<String,Double>>();
	
	public void addPoint(HashMap<String,Double> m){
		points.add(m);
	}
	
	public ArrayList<HashMap<String,Double>> getPoints(){
		return points;
	}
	
	
	
}
