package com.hifleet.activity;

import android.os.Bundle;

import com.hifleet.plus.R;

/**
 * @{# SettingActivity.java Create on 2015年3月4日 上午10:44:29
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WeatherInformationActivity extends HBaseActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_information);

		initNav();
		setNavTitle("气象信息");
	}
}
