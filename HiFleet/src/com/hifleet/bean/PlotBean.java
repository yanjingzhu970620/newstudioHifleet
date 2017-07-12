package com.hifleet.bean;

/**
 * @{# PlotBean.java Create on 2015年5月18日 下午2:23:45
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class PlotBean {
	public String PlotId;
	/**
	 * @param plotId the plotId to set
	 */
	public void setPlotId(String plotId) {
		PlotId = plotId;
	}

	/**
	 * @param plotName the plotName to set
	 */
	public void setPlotName(String plotName) {
		PlotName = plotName;
	}

	/**
	 * @param round the round to set
	 */
	public void setRound(String round) {
		Round = round;
	}

	/**
	 * @param layerId the layerId to set
	 */
	public void setLayerId(String layerId) {
		LayerId = layerId;
	}

	/**
	 * @param plotType the plotType to set
	 */
	public void setPlotType(String plotType) {
		PlotType = plotType;
	}

	/**
	 * @param alertAreaShapes1 the alertAreaShapes1 to set
	 */
	public void setAlertAreaShapes1(String alertAreaShapes1) {
		AlertAreaShapes1 = alertAreaShapes1;
	}

	/**
	 * @param polygon the polygon to set
	 */
	public void setPolygon(String polygon) {
		Polygon = polygon;
	}

	public String PlotName;
	public String Round;
	public String LayerId;
	public String PlotType;
	public String AlertAreaShapes1;
	public String Polygon;
	public String Shape;
	
	/**
	 * @return the shape
	 */
	public String getShape() {
		return Shape;
	}

	/**
	 * @param shape the shape to set
	 */
	public void setShape(String shape) {
		Shape = shape;
	}

	public String getPolygon(){
		return Polygon;
	}

	public String getPlotId() {
		return PlotId;
	}

	public String getPlotName() {
		return PlotName;
	}

	public String getRound() {
		return Round;
	}

	public String getLayerId() {
		return LayerId;
	}

	public String getPlotType() {
		return PlotType;
	}

	public String getAlertAreaShapes1() {
		return AlertAreaShapes1;
	}
}
