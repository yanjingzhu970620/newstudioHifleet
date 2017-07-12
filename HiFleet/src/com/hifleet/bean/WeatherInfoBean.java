package com.hifleet.bean;

import com.e.common.utility.CommonUtility;


/**
 * @{# ShipsBean.java Create on 2015年3月25日 下午2:40:02
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WeatherInfoBean {

	public String lat;
	/**
	 * @return the lat
	 */
	public double getLat() {
		if(CommonUtility.Utility.isNull(lat)) {
			return 0;
		}
		return Double.parseDouble(lat);
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(String lat) {
		this.lat = lat;
	}
	/**
	 * @return the lon
	 */
	public double getLon() {
		if(CommonUtility.Utility.isNull(lon)) {
			return 0;
		}
		return Double.parseDouble(lon);
	}
	/**
	 * @param lon the lon to set
	 */
	public void setLon(String lon) {
		this.lon = lon;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the updatetime
	 */
	public String getUpdatetime() {
		return updatetime;
	}
	/**
	 * @param updatetime the updatetime to set
	 */
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	/**
	 * @return the maxwave
	 */
	public String getMaxwave() {
		return maxwave;
	}
	/**
	 * @param maxwave the maxwave to set
	 */
	public void setMaxwave(String maxwave) {
		this.maxwave = maxwave;
	}
	/**
	 * @return the wave
	 */
	public String getWave() {
		if(wave.equals("N/A")){
			
		}
		return wave;
	}
	/**
	 * @param wave the wave to set
	 */
	public void setWave(String wave) {
		this.wave = wave;
	}
	/**
	 * @return the winddirection
	 */
	public String getWinddirection() {
		return winddirection;
	}
	/**
	 * @param winddirection the winddirection to set
	 */
	public void setWinddirection(String winddirection) {
		this.winddirection = winddirection;
	}
	/**
	 * @return the windspeed
	 */
	public String getWindspeed() {
		return windspeed;
	}
	/**
	 * @param windspeed the windspeed to set
	 */
	public void setWindspeed(String windspeed) {
		this.windspeed = windspeed;
	}
	public String lon;
	public String name;
	public String updatetime;
	public String maxwave;
	public String wave;
	public String winddirection;
	public String windspeed;
	public String compasswinddirection;
	/**
	 * @return the compasswinddirection
	 */
	public String getCompasswinddirection() {
		return compasswinddirection;
	}
	/**
	 * @param compasswinddirection the compasswinddirection to set
	 */
	public void setCompasswinddirection(String compasswinddirection) {
		this.compasswinddirection = compasswinddirection;
	}
	/**
	 * @return the windforce
	 */
	public String getWindforce() {
		return windforce;
	}
	/**
	 * @param windforce the windforce to set
	 */
	public void setWindforce(String windforce) {
		this.windforce = windforce;
	}
	public String windforce;
//	public String dn;
//	public String t;//type
//	public String subt;
	
}
