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
public class TyphoonBean {

	public String lon;
	public String lat;
	public String name;
	public String xuhao;
	public String chncode;
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
