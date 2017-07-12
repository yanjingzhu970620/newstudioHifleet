package com.hifleet.utility;

import java.util.HashMap;

import com.hifleet.bean.ShipsBean;

public class Cell {

	private double _x;
	public double get_x() {
		return _x;
	}

	public void set_x(double _x) {
		this._x = _x;
	}

	public double get_y() {
		return _y;
	}

	public void set_y(double _y) {
		this._y = _y;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	private double _y;
	private String _id;
	
	private HashMap<String,ShipsBean> _mmsiMapShipInfo = new HashMap<String,ShipsBean>();
	private HashMap<String,ShipsBean> _mmsiTeamShipInfo = new HashMap<String,ShipsBean>();
	
	public HashMap mmsiHashMapShipInfo(){
		return _mmsiMapShipInfo;
	}
	
	public void clearMMSIShipInfoMap(){
		_mmsiMapShipInfo.clear();
	}
	public HashMap mmsiTeamShipInfo(){
		return _mmsiTeamShipInfo;
	}
	
	public void clearMMSITeamShipInfoMap(){
		_mmsiTeamShipInfo.clear();
	}
	
	public Cell(double x,double y){
		this._x = x;
		this._y = y;
		this._id = "x"+x+"y"+y;
	}
	
	
}
