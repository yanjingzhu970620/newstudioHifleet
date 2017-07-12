package com.hifleet.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.hifleet.map.OsmandSettings.OsmandPreference;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class OsmAndLocationProvider implements SensorEventListener{
	
	public interface OsmAndLocationListener {
		void updateLocation(com.hifleet.map.Location location);
	}

	public interface OsmAndCompassListener {
		void updateCompassValue(float value);
	}

	private static final int INTERVAL_TO_CLEAR_SET_LOCATION = 30 * 1000;
	private static final int LOST_LOCATION_MSG_ID = OsmAndConstants.UI_HANDLER_LOCATION_SERVICE + 1;
	private static final int START_SIMULATE_LOCATION_MSG_ID = OsmAndConstants.UI_HANDLER_LOCATION_SERVICE + 2;
	private static final int RUN_SIMULATE_LOCATION_MSG_ID = OsmAndConstants.UI_HANDLER_LOCATION_SERVICE + 3;
	private static final long LOST_LOCATION_CHECK_DELAY = 18000;
	private static final long START_LOCATION_SIMULATION_DELAY = 2000;

	private static final float ACCURACY_FOR_GPX_AND_ROUTING = 50;

	private static final int GPS_TIMEOUT_REQUEST = 1;
	private static final int GPS_DIST_REQUEST = 0;
	
	private static final int NETWORK_TIMEOUT_REQUEST=0;
	private static final int NETWORK_DIST_REQUEST=0;
	
	private static final int NOT_SWITCH_TO_NETWORK_WHEN_GPS_LOST_MS = 120;

	private long lastTimeGPSLocationFixed = 0;
	private boolean gpsSignalLost;

	private boolean sensorRegistered = false;
	private float[] mGravs = new float[3];
	private float[] mGeoMags = new float[3];
	private float previousCorrectionValue = 360;
	
	
	
	private static final boolean USE_KALMAN_FILTER = true;
	private static final float KALMAN_COEFFICIENT = 0.04f;
	
	float avgValSin = 0;
	float avgValCos = 0;
	float lastValSin = 0;
	float lastValCos = 0;
	private float[] previousCompassValuesA = new float[50];
	private float[] previousCompassValuesB = new float[50];
	private int previousCompassIndA = 0;
	private int previousCompassIndB = 0;
	private boolean inUpdateValue = false;
	
	private Float heading = null;

	// Current screen orientation
	private int currentScreenOrientation;

	private OsmandApplication app;
	private OsmandSettings settings;
	
	//private CurrentPositionHelper currentPositionHelper;

	private com.hifleet.map.Location location = null;
	
	private MyCurrentLocationInfo myCurrentLocationInfo = new MyCurrentLocationInfo();
	

	private List<OsmAndLocationListener> locationListeners = new ArrayList<OsmAndLocationProvider.OsmAndLocationListener>();
	private List<OsmAndCompassListener> compassListeners = new ArrayList<OsmAndLocationProvider.OsmAndCompassListener>();
	private Listener gpsStatusListener;
	private float[] mRotationM =  new float[9];
	private OsmandPreference<Boolean> USE_MAGNETIC_FIELD_SENSOR_COMPASS;
	private OsmandPreference<Boolean> USE_FILTER_FOR_COMPASS;
	
	
	public OsmAndLocationProvider(OsmandApplication app) {
		this.app = app;		
		settings = app.getSettings();
		USE_MAGNETIC_FIELD_SENSOR_COMPASS = settings.USE_MAGNETIC_FIELD_SENSOR_COMPASS;
		USE_FILTER_FOR_COMPASS = settings.USE_KALMAN_FILTER_FOR_COMPASS;
	}

	public void resumeAllUpdates() {
		final LocationManager service = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		
		service.addGpsStatusListener(getGpsStatusListener(service));		
	    
		try {
			
			service.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIMEOUT_REQUEST, GPS_DIST_REQUEST, gpsListener);
			
			networkListeners.add(gpsListener);
			//print("添加gps位置监听器。");
			
		} catch (IllegalArgumentException e) {
		}
		
		
		// try to always ask for network provide : it is faster way to find location
		List<String> providers = service.getProviders(true);
		for (String provider : providers) {
			if (provider == null || provider.equals(LocationManager.GPS_PROVIDER)) {
				continue;
			}
			try {
				NetworkListener networkListener = new NetworkListener();
				service.requestLocationUpdates(provider, NETWORK_TIMEOUT_REQUEST, NETWORK_DIST_REQUEST, networkListener);
				networkListeners.add(networkListener);
				//print("添加网络位置监听器。");
			} catch (IllegalArgumentException e) {
			}
		}
	}

	private Listener getGpsStatusListener(final LocationManager service) {
		
		gpsStatusListener = new Listener() {
			private GpsStatus gpsStatus;
			@Override
			public void onGpsStatusChanged(int event) {				
				gpsStatus = service.getGpsStatus(gpsStatus);				
				updateLocation(location);
			}
		};
		return gpsStatusListener;
	}
	
	
	
	public MyCurrentLocationInfo getMyCurrentLocationInfo(){
		return myCurrentLocationInfo;
	}
	
	public void updateScreenOrientation(int orientation) {
		currentScreenOrientation = orientation;
	}
	
	public void addLocationListener(OsmAndLocationListener listener){
		if(!locationListeners.contains(listener)) {
			locationListeners.add(listener);
		}
	}
	
	public void removeLocationListener(OsmAndLocationListener listener){
		locationListeners.remove(listener);
	}
	
	public void addCompassListener(OsmAndCompassListener listener){
		if(!compassListeners.contains(listener)) {
			compassListeners.add(listener);
		}
	}
	
	public void removeCompassListener(OsmAndCompassListener listener){
		compassListeners.remove(listener);
	}

	public com.hifleet.map.Location getFirstTimeRunDefaultLocation() {
		LocationManager service = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		List<String> ps = service.getProviders(true);
		if(ps == null) {
			return null;
		}
		List<String> providers = new ArrayList<String>(ps);
		// note, passive provider is from API_LEVEL 8 but it is a constant, we can check for it.
		// constant should not be changed in future
		int passiveFirst = providers.indexOf("passive"); // LocationManager.PASSIVE_PROVIDER
		// put passive provider to first place
		if (passiveFirst > -1) {
			providers.add(0, providers.remove(passiveFirst));
		}
		// find location
		for (String provider : providers) {
			com.hifleet.map.Location location = convertLocation(service.getLastKnownLocation(provider), app);
			if (location != null) {
				return location;
			}
		}
		return null;
	}

	public void registerOrUnregisterCompassListener(boolean register) {
		if (sensorRegistered && !register) {
			Log.d(PlatformUtil.TAG, "Disable sensor"); //$NON-NLS-1$
			((SensorManager) app.getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
			sensorRegistered = false;
			heading = null;
		} else if (!sensorRegistered && register) {
			Log.d(PlatformUtil.TAG, "Enable sensor"); //$NON-NLS-1$
			SensorManager sensorMgr = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
			if (USE_MAGNETIC_FIELD_SENSOR_COMPASS.get()) {
				Sensor s = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				if (s == null || !sensorMgr.registerListener(this, s, SensorManager.SENSOR_DELAY_UI)) {
					Log.e(PlatformUtil.TAG, "Sensor accelerometer could not be enabled");
				}
				s = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				if (s == null || !sensorMgr.registerListener(this, s, SensorManager.SENSOR_DELAY_UI)) {
					Log.e(PlatformUtil.TAG, "Sensor magnetic field could not be enabled");
				}
			} else {
				Sensor s = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
				if (s == null || !sensorMgr.registerListener(this, s, SensorManager.SENSOR_DELAY_UI)) {
					Log.e(PlatformUtil.TAG, "Sensor orientation could not be enabled");
				}
			}
			sensorRegistered = true;
		}
	}
	public static boolean isPointAccurateForRouting(Location loc) {
		
		return loc != null && (!loc.hasAccuracy() || loc.getAccuracy() < ACCURACY_FOR_GPX_AND_ROUTING * 3 / 2);
	}

	private boolean isRunningOnEmulator() {
		if (Build.DEVICE.equals("generic")) { //$NON-NLS-1$ 
			return true;
		}
		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Attention : sensor produces a lot of events & can hang the system
		if(inUpdateValue) {
			return;
		}
		synchronized (this) {
			inUpdateValue = true;
			try {
				float val = 0;
				switch (event.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					System.arraycopy(event.values, 0, mGravs, 0, 3);
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					System.arraycopy(event.values, 0, mGeoMags, 0, 3);
					break;
				case Sensor.TYPE_ORIENTATION:
					val = event.values[0];
					break;
				default:
					return;
				}
				if (USE_MAGNETIC_FIELD_SENSOR_COMPASS.get()) {
					if (mGravs != null && mGeoMags != null) {
						boolean success = SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags);
						if (!success) {
							return;
						}
						float[] orientation = SensorManager.getOrientation(mRotationM, new float[3]);
						val = (float) Math.toDegrees(orientation[0]);
					} else {
						return;
					}
				}
				val = calcScreenOrientationCorrection(val);
				val = calcGeoMagneticCorrection(val);

				float valRad = (float) (val / 180f * Math.PI);
				lastValSin = (float) Math.sin(valRad);
				lastValCos = (float) Math.cos(valRad);
				// lastHeadingCalcTime = System.currentTimeMillis();
				boolean filter = USE_FILTER_FOR_COMPASS.get(); //USE_MAGNETIC_FIELD_SENSOR_COMPASS.get();
				if (filter) {
					filterCompassValue();
				} else {
					avgValSin = lastValSin;
					avgValCos = lastValCos;
				}

				updateCompassVal();
			} finally {
				inUpdateValue = false;
			}
		}
	}

	private float calcGeoMagneticCorrection(float val) {
		if (previousCorrectionValue == 360 && getLastKnownLocation() != null) {
			com.hifleet.map.Location l = getLastKnownLocation();
			GeomagneticField gf = new GeomagneticField((float) l.getLatitude(), (float) l.getLongitude(), (float) l.getAltitude(),
					System.currentTimeMillis());
			previousCorrectionValue = gf.getDeclination();
		}
		if (previousCorrectionValue != 360) {
			val += previousCorrectionValue;
		}
		return val;
	}

	private float calcScreenOrientationCorrection(float val) {
		if (currentScreenOrientation == 1) {
			val += 90;
		} else if (currentScreenOrientation == 2) {
			val += 180;
		} else if (currentScreenOrientation == 3) {
			val -= 90;
		}
		return val;
	}

	private void filterCompassValue() {
		if(heading == null && previousCompassIndA == 0) {
			Arrays.fill(previousCompassValuesA, lastValSin);
			Arrays.fill(previousCompassValuesB, lastValCos);
			avgValSin = lastValSin;
			avgValCos = lastValCos;
		} else {
			if (USE_KALMAN_FILTER) {
				avgValSin = KALMAN_COEFFICIENT * lastValSin + avgValSin * (1 - KALMAN_COEFFICIENT);
				avgValCos = KALMAN_COEFFICIENT * lastValCos + avgValCos * (1 - KALMAN_COEFFICIENT);
			} else {
				int l = previousCompassValuesA.length;
				previousCompassIndA = (previousCompassIndA + 1) % l;
				previousCompassIndB = (previousCompassIndB + 1) % l;
				// update average
				avgValSin = avgValSin + (-previousCompassValuesA[previousCompassIndA] + lastValSin) / l;
				previousCompassValuesA[previousCompassIndA] = lastValSin;
				avgValCos = avgValCos + (-previousCompassValuesB[previousCompassIndB] + lastValCos) / l;
				previousCompassValuesB[previousCompassIndB] = lastValCos;
			}
		}
	}	

	private void updateCompassVal() {
		heading = (float) getAngle(avgValSin, avgValCos);
		for(OsmAndCompassListener c : compassListeners){
			c.updateCompassValue(heading);
		}
	}
	
	public Float getHeading() {
		return heading;
	}
	
	private float getAngle(float sinA, float cosA) {
		return MapUtils.unifyRotationTo360((float) (Math.atan2(sinA, cosA) * 180 / Math.PI));
	}

	
	private void updateLocation(com.hifleet.map.Location loc ) {
		
		if(loc==null) {return;}
		//定位数据位空，避免在图上画不了位置，就设置返回。
		//有可能不对。
		myCurrentLocationInfo.lat = loc.getLatitude();
		myCurrentLocationInfo.lon = loc.getLongitude();
		myCurrentLocationInfo.speed = loc.getSpeed();
		myCurrentLocationInfo.course = loc.getBearing();
		
		this.location = loc;
		//updateMyLastKnonwLocation(myCurrentLocationInfo);
		
		//print("更新位置信息： "+settings.getPhoneIMEINumber()+", "+myCurrentLocationInfo.lon+" "+myCurrentLocationInfo.lat);
		
		for(OsmAndLocationListener l : locationListeners){
			l.updateLocation(loc);
		}
	}
	
	

	
	private Location getBestLocation(LocationManager locationManager) {
		 Location gpsLoc = locationManager
                 .getLastKnownLocation(LocationManager.GPS_PROVIDER);
		 Location networkLoc = locationManager
                 .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		 Location bestLoc = null;
		 if (gpsLoc != null && networkLoc != null) {
             // if gps loc is more than 5 min older than network loc
             bestLoc = networkLoc.getTime() - 5 * 60 * 1000 > gpsLoc
                     .getTime() ? networkLoc : gpsLoc;
         }
		 else if(gpsLoc != null){
			 bestLoc = gpsLoc;
		 }else if(networkLoc!=null){
			 bestLoc = networkLoc;
		 }
		 return bestLoc;
	}
	
	private LocationListener gpsListener = new LocationListener() {
		
		boolean isRemoved=false;//判断网络监听是否移除。
		
		@Override
		public void onLocationChanged(Location location) {
						
			//print("获得GPS位置： "+location.getLatitude()+", "+location.getLongitude());
			
			if (location != null) {
				lastTimeGPSLocationFixed = location.getTime();
			}
			
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
			if(LocationProvider.OUT_OF_SERVICE == status){
				print("GPS 定位失败。需要切换到网络定位。");
			}
			
		}
	};
	
	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
	
	private LinkedList<LocationListener> networkListeners = new LinkedList<LocationListener>();

	private boolean useOnlyGPS() {
		
		if((System.currentTimeMillis() - lastTimeGPSLocationFixed) < NOT_SWITCH_TO_NETWORK_WHEN_GPS_LOST_MS) {
			return true;
		}
		if(isRunningOnEmulator()) {
			return true;
		}
		return false;
	}

	// Working with location listeners
	private class NetworkListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			
			
			// double check about use only gps
			// that strange situation but it could happen?
			if (!useOnlyGPS() ) {
				//System.err.println("网络定位数据 set location ");
				//System.err.println("设定网络定位数据： "+location.getLatitude()+", "+location.getLongitude());
				setLocation(convertLocation(location, app));
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(LocationProvider.OUT_OF_SERVICE == status){
				//System.err.println("网络定位失败。需要切换到GPS定位。");
			}
		}

	};

	private void stopLocationRequests() {
		LocationManager service = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		service.removeGpsStatusListener(gpsStatusListener);
		service.removeUpdates(gpsListener);
		while(!networkListeners.isEmpty()) {
			service.removeUpdates(networkListeners.poll());
		}
	}

	public void pauseAllUpdates() {
		stopLocationRequests();
		SensorManager sensorMgr = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
		sensorMgr.unregisterListener(this);
		sensorRegistered = false;
	}

	public static com.hifleet.map.Location convertLocation(Location l, OsmandApplication app) {
		if (l == null) {
			return null;
		}
		com.hifleet.map.Location r = new com.hifleet.map.Location(l.getProvider());
		r.setLatitude(l.getLatitude());
		r.setLongitude(l.getLongitude());
		r.setTime(l.getTime());
		if (l.hasAccuracy()) {
			//System.err.println("定位精度有： "+l.getAccuracy());
			r.setAccuracy(l.getAccuracy());
		}
		if (l.hasSpeed()) {
			r.setSpeed(l.getSpeed());
		}
		if (l.hasAltitude()) {
			r.setAltitude(l.getAltitude());
		}
		if (l.hasBearing()) {
			r.setBearing(l.getBearing());
		}		
		return r;
	}
	
	
	private void scheduleCheckIfGpsLost(final com.hifleet.map.Location location) {
		//final RoutingHelper routingHelper = app.getRoutingHelper();
		if (location != null) {
			final long fixTime = location.getTime();
			app.runMessageInUIThreadAndCancelPrevious(LOST_LOCATION_MSG_ID, new Runnable() {

				@Override
				public void run() {
					com.hifleet.map.Location lastKnown = getLastKnownLocation();
					if (lastKnown != null && lastKnown.getTime() > fixTime) {
						// false positive case, still strange how we got here with removeMessages
						return;
					}
					gpsSignalLost = true;
					
					setLocation(null);
				}
			}, LOST_LOCATION_CHECK_DELAY);			
		}
	}
	
	
	public void setLocationFromService(com.hifleet.map.Location location, boolean continuous) {
		// if continuous notify about lost location
		if (continuous) {			
			scheduleCheckIfGpsLost(location);
		}
		
	}
	
	public void setLocationFromSimulation(com.hifleet.map.Location location) {
		setLocation(location);
	}

	private void setLocation(com.hifleet.map.Location location) {
		
		if(location != null) {
			
			if(gpsSignalLost) {
				gpsSignalLost = false;
			}
		}
		enhanceLocation(location);
		scheduleCheckIfGpsLost(location);
		updateLocation(location);
	}

	private void enhanceLocation(com.hifleet.map.Location location) {
		if (location != null && isRunningOnEmulator()) {
			// only for emulator
			//updateSpeedEmulator(location);
		}
	}

	public void checkIfLastKnownLocationIsValid() {
		com.hifleet.map.Location loc = getLastKnownLocation();
		if (loc != null && (System.currentTimeMillis() - loc.getTime()) > INTERVAL_TO_CLEAR_SET_LOCATION) {
			setLocation(null);
		}
	}
	
	public com.hifleet.map.Location getLastKnownLocation() {
		return location;
	}
	
	//有一个初始的位置（122,31）
	public com.hifleet.map.Location getInitLocation(){
		com.hifleet.map.Location loc = new com.hifleet.map.Location("init");
		loc.setLongitude(122);
		loc.setLatitude(31);
		return loc;
	}


	public static class MyCurrentLocationInfo{
		public double lon=-1,lat=-1;
		public float speed=0,course=-1;
	}

}
