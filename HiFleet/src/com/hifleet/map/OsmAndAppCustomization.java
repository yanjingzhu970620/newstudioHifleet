package com.hifleet.map;



import android.app.Activity;
import android.view.Window;

public class OsmAndAppCustomization {
	
	protected OsmandApplication app;

	public void setup(OsmandApplication app) {
		this.app = app;
	}
	
	public OsmandSettings createSettings(SettingsAPI api) {
		return new OsmandSettings(app, api);
	}

	public boolean checkExceptionsOnStart() {
		return true;
	}

	public boolean showFirstTimeRunAndTips(boolean firstTime, boolean appVersionChanged) {
		return true;
	}

	public boolean checkBasemapDownloadedOnStart() {
		return true;
	}

	public void customizeMainMenu(Window window, Activity activity) {
	}
	
	public Class<MapActivity> getMapActivity(){
		return MapActivity.class;
	}
	
//	public Class<MainMenuActivity> getMainMenuActivity() {
//		return MainMenuActivity.class;
//	}
	
	
	

}
