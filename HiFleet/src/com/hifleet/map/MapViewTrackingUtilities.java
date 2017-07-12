package com.hifleet.map;

import java.util.List;

import com.hifleet.map.OsmAndLocationProvider.OsmAndCompassListener;
import com.hifleet.map.OsmAndLocationProvider.OsmAndLocationListener;
//import com.hifleet.map.OsmandSettings.AutoZoomMap;

import android.content.Context;
import android.view.WindowManager;

public class MapViewTrackingUtilities implements OsmAndLocationListener, IMapLocationListener, OsmAndCompassListener {
	private static final int AUTO_FOLLOW_MSG_ID = OsmAndConstants.UI_HANDLER_LOCATION_SERVICE + 4; 
	
	private long lastTimeAutoZooming = 0;
	private long lastTimeSensorMapRotation = 0;
	private boolean sensorRegistered = false;
	private OsmandMapTileView mapView;
	private OsmandSettings settings;
	private OsmandApplication app;
	// by default turn off causing unexpected movements due to network establishing
	private boolean isMapLinkedToLocation = false;
	private boolean followingMode;
	private boolean routePlanningMode;
	
	public MapViewTrackingUtilities(OsmandApplication app){
		this.app = app;
		settings = app.getSettings();
		app.getLocationProvider().addLocationListener(this);
		app.getLocationProvider().addCompassListener(this);
	}

	
	
	
	public void setMapView(OsmandMapTileView mapView) {
		this.mapView = mapView;
		if(mapView != null) {
			WindowManager wm = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
			int orientation = wm.getDefaultDisplay().getOrientation();
			app.getLocationProvider().updateScreenOrientation(orientation);
			mapView.setMapLocationListener(this);
		}
	}
	
	@Override
	public void updateCompassValue(float val) {
		
		if (mapView != null) {
			if (settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_COMPASS && !routePlanningMode) {
				if (Math.abs(MapUtils.degreesDiff(mapView.getRotate(), -val)) > 1) {
					//System.err.println("updateing CompassValue. mapview set rotate.");
					mapView.setRotate(-val);
				}
			} else if (settings.SHOW_VIEW_ANGLE.get()) {
				//mapView.refreshMap();
				//System.err.println("updateCompassValue.");
				mapView.refreshMapForceDraw(true,true);
			}
		}
	}
	
	private static void print(String msg) {

		android.util.Log.i(TAG, msg);

	}
	private static final String TAG = "FileDownloader";
	
	@Override
	public void updateLocation(Location location) {
//		print("track view upatelocation.");
//		if (mapView != null) {
//			if (isMapLinkedToLocation() && location != null) {
//				print("");
//				//registerUnregisterSensor(location, enableCompass);
//				print("trackView 95.");
//				mapView.setLatLon(location.getLatitude(), location.getLongitude());
//				mapView.refreshMap();
//				
//			}
//			
//			//System.err.println(" When location is changed we need to refresh map in order to show movement! 调用 refreshMap");
//			
//		}
	}
	

	public void updateSettings(){
		if (mapView != null) {
			if (settings.ROTATE_MAP.get() != OsmandSettings.ROTATE_MAP_COMPASS || routePlanningMode) {
				mapView.setRotate(0);
			}
			mapView.setMapPosition(settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_BEARING && !routePlanningMode ? OsmandSettings.BOTTOM_CONSTANT
					: OsmandSettings.CENTER_CONSTANT);
		}
		registerUnregisterSensor(app.getLocationProvider().getLastKnownLocation(), false);
	}
	
	private void registerUnregisterSensor(Location location, boolean overruleRegister) {
		boolean currentShowingAngle = settings.SHOW_VIEW_ANGLE.get();
		int currentMapRotation = settings.ROTATE_MAP.get();
		boolean registerCompassListener = overruleRegister || (currentShowingAngle && location != null)
				|| (currentMapRotation == OsmandSettings.ROTATE_MAP_COMPASS && !routePlanningMode);
		// show point view only if gps enabled
		if(sensorRegistered != registerCompassListener) {
			app.getLocationProvider().registerOrUnregisterCompassListener(registerCompassListener);
		}
	}

	private float defineZoomFromSpeed(RotatedTileBox tb, float speed) {
		if (speed < 7f / 3.6) {
			return 0;
		}
		double visibleDist = tb.getDistance(tb.getCenterPixelX(), 0, tb.getCenterPixelX(), tb.getCenterPixelY());
		float time = 75f; // > 83 km/h show 75 seconds 
		if (speed < 83f / 3.6) {
			time = 60f;
		}
//		time /= settings.AUTO_ZOOM_MAP.get().coefficient;
		double distToSee = speed * time;
		float zoomDelta = (float) (Math.log(visibleDist / distToSee) / Math.log(2.0f));
		// check if 17, 18 is correct?
		return zoomDelta;
	}
	
	public void autozoom(Location location) {
		if (location.hasSpeed()) {
			long now = System.currentTimeMillis();
			final RotatedTileBox tb = mapView.getCurrentRotatedTileBox();
			float zdelta = defineZoomFromSpeed(tb, location.getSpeed());
			if (Math.abs(zdelta) >= 0.5/*?Math.sqrt(0.5)*/) {
				// prevent ui hysteresis (check time interval for autozoom)
				if (zdelta >= 2) {
					// decrease a bit
					zdelta -= 1;
				} else if (zdelta <= -2) {
					// decrease a bit
					zdelta += 1;
				}
				if (now - lastTimeAutoZooming > 4500) {
					lastTimeAutoZooming = now;
					float settingsZoomScale = mapView.getSettingsZoomScale();
					float complexZoom = tb.getZoom() + tb.getZoomScale() + zdelta;
					// round to 0.33
					float newZoom = Math.round((complexZoom - settingsZoomScale) * 3) / 3f;
					int nz = (int)Math.round(newZoom);
					float nzscale = newZoom - nz + settingsZoomScale;
					mapView.setComplexZoom(nz, nzscale);
					// mapView.getAnimatedDraggingThread().startZooming(mapView.getFloatZoom() + zdelta, false);
				}
			}
		}
	}
	
	public void locateToInitLocationImpl(){
		if (mapView != null) {
			OsmAndLocationProvider locationProvider = app.getLocationProvider();
			if (!isMapLinkedToLocation()) {
				setMapLinkedToLocation(true);
				if (locationProvider.getLastKnownLocation() != null) {
					com.hifleet.map.Location lastKnownLocation = locationProvider.getLastKnownLocation();
					AnimateDraggingMapThread thread = mapView.getAnimatedDraggingThread();
					//int fZoom = mapView.getZoom() < 15 ? 15 : mapView.getZoom();//定位到最大比例尺上去。
					int fZoom = mapView.getZoom();//定位到当前的比例尺上去。
					thread.startMoving(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), fZoom, false);
				}
				mapView.refreshMap();
			}
			if (locationProvider.getLastKnownLocation() == null) {
				//app.showToastMessage(R.string.unknown_location);
				com.hifleet.map.Location initLocation = locationProvider.getInitLocation();
				AnimateDraggingMapThread thread = mapView.getAnimatedDraggingThread();
				int initZoom = 5;//mapView.getZoom();//定位到当前的比例尺上去。
				thread.startMoving(initLocation.getLatitude(), initLocation.getLongitude(), initZoom, false);
				mapView.refreshMap();
			}else{
				System.err.println("locationProvider.getLastKnownLocation() != null");
			}
		}else{
			System.err.println("mapView is null!!!!!");
		}
	}
	
	
	//定位到指定的经纬度上。
	public void locateToDesignatedLocation(double lon,double lat){
		if(mapView!=null){
			AnimateDraggingMapThread thread = mapView.getAnimatedDraggingThread();
			int fZoom = mapView.getZoom();
			thread.startMoving(lat, lon, fZoom, false);
			mapView.refreshMap();
		}
		else{
			System.err.println("mapView is null!");
		}
	}
	
	public void backToLocationImpl() {
		//print("backToLocationImpl");
		if (mapView != null) {
			OsmAndLocationProvider locationProvider = app.getLocationProvider();
			//if (!isMapLinkedToLocation()) {
				//print("isMapLinkedToLocation false!");
				//setMapLinkedToLocation(true);
				if (locationProvider.getLastKnownLocation() != null) {
					com.hifleet.map.Location lastKnownLocation = locationProvider.getLastKnownLocation();
					AnimateDraggingMapThread thread = mapView.getAnimatedDraggingThread();
					//int fZoom = mapView.getZoom() < 15 ? 15 : mapView.getZoom();//定位到最大比例尺上去。
					int fZoom = mapView.getZoom();//定位到当前的比例尺上去。
					//print("lastKnownLocation: "+lastKnownLocation.getLatitude()+" "+lastKnownLocation.getLongitude());
					thread.startMoving(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), fZoom, false);				
				}
				mapView.refreshMap();
			//}
			if (locationProvider.getLastKnownLocation() == null) {
				app.showToastMessage("位置未知。");
			}
		}
	}
	
	private void backToLocationWithDelay(int delay) {
		app.runMessageInUIThreadAndCancelPrevious(AUTO_FOLLOW_MSG_ID, new Runnable() {
			@Override
			public void run() {
				if (mapView != null && !isMapLinkedToLocation()) {
					//app.showToastMessage(R.string.auto_follow_location_enabled);
					backToLocationImpl();
				}
			}
		}, delay * 1000);
	}
	
	public boolean isMapLinkedToLocation(){
		return isMapLinkedToLocation;
	}
	
	public void setMapLinkedToLocation(boolean isMapLinkedToLocation) {
		this.isMapLinkedToLocation = isMapLinkedToLocation;
	}
	
	@Override
	public void locationChanged(double newLatitude, double newLongitude, Object source) {
		// when user start dragging 
		if(app.getLocationProvider().getLastKnownLocation() != null){
			setMapLinkedToLocation(false);
		}
	}
	
	

	

}
