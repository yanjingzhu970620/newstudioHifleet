package com.hifleet.bean;

import com.e.common.utility.CommonUtility;
import com.ibm.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * @{# ShipsBean.java Create on 2015年3月25日 下午2:40:02
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipsBean {

	public String m;//mmsi
	public String n;//name
	public String c;//callsign
	public String lo;
	public String la;
	public String sp;
	public String co;
	public String i;//imo
	public String dn;
	public String t;//type
	public String subt;
	public String rs;
	/**
	 * @return the subt
	 */
	public String getSubt() {
		return subt;
	}

	/**
	 * @param subt the subt to set
	 */
	public void setSubt(String subt) {
		this.subt = subt;
	}

	public String l;
	public String b;
	public String ti;//更新时间

	public String getSatti() {
		return satti;
	}

	public void setSatti(String satti) {
		this.satti = satti;
	}

	public String satti="";
	public String s;
	public String d;
	public String e;
	public String dr;
	public String h;
	public String rot;
	public String an;
	public String fle;
	public String cname;
	public String message;
	public String oa;
	public String ob;
	public String oc;
	public String od;
	public String flag;
	private String teamgroup;

	/**
	 * @return the teamgroup
	 */
	public String getTeamgroup() {
		return teamgroup;
	}

	/**
	 * @param teamgroup the teamgroup to set
	 */
	public void setTeamgroup(String teamgroup) {
		this.teamgroup = teamgroup;
	}

	public double getSp(){
		if(CommonUtility.Utility.isNull(sp)){
			return 0;
		}
		return Double.parseDouble(sp);
	}
	
	public double getLa() {
		if(CommonUtility.Utility.isNull(la)) {
			return 0;
		}
		return Double.parseDouble(la);
	}

	public double getLo() {
		if(CommonUtility.Utility.isNull(lo)) {
			return 0;
		}
		return Double.parseDouble(lo);
	}
	
	public Float getCo(){
		if(CommonUtility.Utility.isNull(co)) {
//			System.out.println("course: nulllllll！！！！" );
			return null;
		}
		//System.out.println("course: " + Float.parseFloat(co));
//		System.out.println("course: " + Float.parseFloat(co));
		return Float.parseFloat(co);
	}

	public String getM(){
		return m;
	}
	
	public Date getDateupdatetime(){
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date upt = null;
		try {
			upt= sdf.parse(ti);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return upt;
	}

	public Date getDatesatti(){
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date upt = null;
		try {
			upt= sdf.parse(getSatti());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return upt;
	}
	public String getformatti(Date d){
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String stringtime=sdf.format(d);

		return stringtime;
	}
}
