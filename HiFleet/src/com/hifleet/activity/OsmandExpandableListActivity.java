package com.hifleet.activity;

import android.app.ActionBar;
import android.content.IntentFilter;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.hifleet.map.OsmandApplication;
public abstract class OsmandExpandableListActivity extends
		SherlockExpandableListActivity {
	
	public static void print(String msg){

        android.util.Log.i(TAG, msg);

 }
 
private static final String TAG = "FileDownloader";
	
//	private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
//	    @Override
//	    public void onReceive(Context context, Intent intent) {
//	    	print("收到广播信息: "+intent.getAction());
//	           if("finish".equals(intent.getAction())) {
//	             print("I am " + getLocalClassName()
//	                     + ",now finishing myself...");
//	              finish();
//	       }
//	    }
//	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		((OsmandApplication) getApplication()).applyTheme(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		IntentFilter filter = new IntentFilter();
	    filter.addAction("finish");
	   // registerReceiver(mFinishReceiver, filter);
		
//		IntentFilter filter = new IntentFilter();
//	    filter.addAction("finish");
//	    registerReceiver(mFinishReceiver, filter);
		
	}


	public OsmandApplication getMyApplication() {
		return (OsmandApplication)getApplication();
	}
	

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			finish();
			return true;

		}
		return false;
	}
	
	public MenuItem createMenuItem(Menu m, int id, int titleRes, int iconLight, int iconDark, int menuItemType) {
		int r = isLightActionBar() ? iconLight : iconDark;
		MenuItem menuItem = m.add(0, id, 0, titleRes);
		if (r != 0) {
			menuItem.setIcon(r);
		}
		menuItem.setShowAsActionFlags(menuItemType).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});
		return menuItem;
	}
	
	public void fixBackgroundRepeat(View view) {
		Drawable bg = view.getBackground();
		if (bg != null) {
			if (bg instanceof BitmapDrawable) {
				BitmapDrawable bmp = (BitmapDrawable) bg;
				// bmp.mutate(); // make sure that we aren't sharing state anymore
				bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			}
		}
	}
	
	
	public boolean isLightActionBar() {
		return ((OsmandApplication) getApplication()).getSettings().isLightActionBar();
	}
}
