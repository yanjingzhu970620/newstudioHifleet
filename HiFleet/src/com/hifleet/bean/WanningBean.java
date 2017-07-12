package com.hifleet.bean;

/**
 * @{# WanningBean.java Create on 2015年5月15日 下午2:35:51
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WanningBean {

	public String PlotId;
	public String PlotName;
	public String AlertClass;
	public String AlertRsId;
	public String AlertTimeE;
	public String AlertType;
	public String AlertTimeS;
	public String ArriveOrleave;
	public String Fleet;
	public String Shipname;
	public String Speed;
	public String SpeedType;
	public String SpeedValue;
	public String TriggerTime;
	public String Mmsi;
	public String Name;
	public String LayerId;
	public String BelongTo;
	public String booltf="false";
	/**
	 * @return the belongTo
	 */
	public String getBelongTo() {
		return BelongTo;
	}

	/**
	 * @param belongTo the belongTo to set
	 */
	public void setBelongTo(String belongTo) {
		BelongTo = belongTo;
	}

	/**
	 * @return the layerId
	 */
	public String getLayerId() {
		return LayerId;
	}

	/**
	 * @param layerId the layerId to set
	 */
	public void setLayerId(String layerId) {
		LayerId = layerId;
	}

	/**
	 * @return the plotId
	 */
	public String getPlotId() {
		return PlotId;
	}

	/**
	 * @param plotId
	 *            the plotId to set
	 */
	public void setPlotId(String PlotId) {
		this.PlotId = PlotId;
	}

	public String getPlotName() {
		return PlotName;
	}

	public String getAlertClass() {
		return AlertClass;
	}

	public String getAlertRsId() {
		return AlertRsId;
	}

	public String getAlertTimeE() {
		return AlertTimeE;
	}

	public String getAlertType() {
		return AlertType;
	}

	public String getAlertTimeS() {
		return AlertTimeS;
	}

	public String getArriveOrleave() {
		return ArriveOrleave;
	}

	public String getFleet() {
		return Fleet;
	}

	public String getShipname() {
		return Shipname;
	}

	public String getSpeed() {
		return Speed;
	}

	public String getSpeedType() {
		return SpeedType;
	}

	public String getSpeedValue() {
		return SpeedValue;
	}

	public void setTriggerTime(String TriggerTime) {
		this.TriggerTime = TriggerTime;
	}

	public String getTriggerTime() {
		return TriggerTime;
	}

	public String getMmsi() {
		return Mmsi;
	}
	public String getName(){
		return Name;
	}
	
	public void setName(){
		this.Name = Name;
	}
}
