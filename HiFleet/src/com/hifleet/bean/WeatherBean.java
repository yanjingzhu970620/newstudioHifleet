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
public class WeatherBean {

	public String lon;
	public String lat;
	public String weatherid;
	public String areaname;
	public String vis;
	public String windd;
	public String windf;
	public String sea;
	public String weather;
	public String startime;
	public String endtime;
	public String temperature;
	public String pressure;
	public String humidity;
	public String waterlevel;
	public String current;
	public String currentdirection;
	public String seadir;
	public String seatemp;
	public String mudtemp;
	public String salinity;
	public String updatetime;
	
	public double getLa() {
		if(CommonUtility.Utility.isNull(lat)) {
			return 0;
		}
		return Double.parseDouble(lat);
	}

	public double getLo() {
		if(CommonUtility.Utility.isNull(lon)) {
			return 0;
		}
		return Double.parseDouble(lon);
	}

}
