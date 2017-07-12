package com.hifleet.bean;

import com.e.common.utility.CommonUtility;

public class TraceBean {
	public String la;
	public String lo;
	public String z;
	public String ti;
	public String co;
	public String sp;
	public String m;
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
	/**
	 * @param la the la to set
	 */
	public void setLa(String la) {
		this.la = la;
	}
	/**
	 * @param lo the lo to set
	 */
	public void setLo(String lo) {
		this.lo = lo;
	}
	/**
	 * @return the z
	 */
	public String getZ() {
		return z;
	}
	/**
	 * @param z the z to set
	 */
	public void setZ(String z) {
		this.z = z;
	}
	/**
	 * @return the ti
	 */
	public String getTi() {
		return ti;
	}
	/**
	 * @param ti the ti to set
	 */
	public void setTi(String ti) {
		this.ti = ti;
	}
	/**
	 * @return the co
	 */
	public String getCo() {
		return co;
	}
	/**
	 * @param co the co to set
	 */
	public void setCo(String co) {
		this.co = co;
	}
	/**
	 * @return the sp
	 */
	public String getSp() {
		return sp;
	}
	/**
	 * @param sp the sp to set
	 */
	public void setSp(String sp) {
		this.sp = sp;
	}
	/**
	 * @return the m
	 */
	public String getM() {
		return m;
	}
	/**
	 * @param m the m to set
	 */
	public void setM(String m) {
		this.m = m;
	}
}
