package com.hifleet.map;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.view.MotionEvent;

// Provide some additional accessibility means for activity view elements.
//
// To make use of these capabilities simply derive your activity from this class
// and then add view elements you wish to be accessible
// to the accessibleContent list.
//
public class AccessibleActivity extends Activity implements
		AccessibleContent.Callback {

	OsmandApplication app;

	// List of accessible views. Use accessibleContent.add(element)
	// to add element to it.
	public final AccessibleContent accessibleContent = new AccessibleContent(
			this);

	@Override
	public boolean dispatchNativeTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return accessibleContent.dispatchTouchEvent(event, this);
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
