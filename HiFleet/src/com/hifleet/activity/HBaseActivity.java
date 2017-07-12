package com.hifleet.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;

import com.e.common.activity.BaseActivity;
import com.e.common.task.net.RequestInterceptor;
import com.hifleet.map.OsmandApplication;
//import com.e.common.task.net.RequestInterceptor_;
//import com.e.common.task.net.RequestInterceptor_;

/**
 * @{# MBaseActivity.java Create on 2014年12月8日 下午9:40:03
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description 所有activity父类
 *
 */
public class HBaseActivity extends BaseActivity {

	public RequestInterceptor mRequestInterceptor;
	OsmandApplication app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// mRequestInterceptor = RequestInterceptor_.getInstance_(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public String getRespMsg(Object object) {
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
	};

	@Override
	protected void onStop() {
		super.onStop();
	}

	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
}
