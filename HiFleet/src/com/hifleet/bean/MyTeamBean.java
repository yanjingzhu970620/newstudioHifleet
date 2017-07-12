package com.hifleet.bean;

import android.graphics.Color;

/**
 * @{# PlotBean.java Create on 2015年5月18日 下午2:23:45
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class MyTeamBean {
	public String name;
	public String id;
	private String groupcolor;

	/**
	 * @return the color
	 */
	public int getColor() {
//		System.err.println("groupcolor"+groupcolor);
		int c=-1;
		if(groupcolor!=null&&!groupcolor.equals("")&&!groupcolor.equals("N/A")&&!groupcolor.equals("-")){
		c=Color.parseColor("#"+groupcolor);
		}
		return c;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.groupcolor = color;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
