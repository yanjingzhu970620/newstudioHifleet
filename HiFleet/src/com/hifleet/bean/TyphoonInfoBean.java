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
public class TyphoonInfoBean {

	public String lon;
	public String lat;
	public String xuhao;
	public String name;
	public String level;
	public String windspeed;
	public String pressure;
	public String movespeed;
	public String movedirection;
	public String updatetime;
	public String circle7quad1;
	public String circle7quad2;
	public String circle7quad3;
	public String circle7quad4;
	public String circle10quad1;
	public String circle10quad2;
	public String circle10quad3;
	public String circle10quad4;
	public String circle12quad1;
	public String circle12quad2;
	public String circle12quad3;
	public String circle12quad4;
	public String fore6lon;
	public String fore6lat;
	public String fore6level;
	public String fore6windspeed;
	public String fore6pressure;
	public String fore12lon;
	public String fore12lat;
	public String fore12level;
	public String fore12windspeed;
	public String fore12pressure;
	public String fore18lon;
	public String fore18lat;
	public String fore18level;
	public String fore18windspeed;
	public String fore18pressure;
	public String fore24lon;
	public String fore24lat;
	public String fore24level;
	public String fore24windspeed;
	public String fore24pressure;
	public String fore36lon;
	public String fore36lat;
	public String fore36level;
	public String fore36windspeed;
	public String fore36pressure;
	public String fore48lon;
	public String fore48lat;
	public String fore48level;
	public String fore48windspeed;
	public String fore48pressure;
	public String fore60lon;
	public String fore60lat;
	public String fore60level;
	public String fore60windspeed;
	public String fore60pressure;
	public String fore72lon;
	public String fore72lat;
	public String fore72level;
	public String fore72windspeed;
	public String fore72pressure;
	
	
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
