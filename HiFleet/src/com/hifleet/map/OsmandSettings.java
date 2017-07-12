package com.hifleet.map;


import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import com.hifleet.map.SettingsAPI.SettingsEditor;
import com.hifleet.plus.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

@TargetApi(19)
public class OsmandSettings {
	
	public interface OsmandPreference<T>  {
		T get();
		
		boolean set(T obj);
		String getId();
		
		void resetToDefault();
		
		void overrideDefaultValue(T newDefaultValue);
	}
	
	private abstract class PreferenceWithListener<T> implements OsmandPreference<T> {
		
	}
	
	// These settings are stored in SharedPreferences
	private static final String SHARED_PREFERENCES_NAME = "com.manyshipsand.settings"; //$NON-NLS-1$
	
	/// Settings variables
	private final OsmandApplication ctx;
	private SettingsAPI settingsAPI;
	private Object globalPreferences;
	private Object defaultProfilePreferences;
	private Object profilePreferences;
	private Map<String, OsmandPreference<?>> registeredPreferences = 
			new LinkedHashMap<String, OsmandSettings.OsmandPreference<?>>();
	
	// cache variables
	private long lastTimeInternetConnectionChecked = 0;
	private boolean internetConnectionAvailable = true;
	
	
	protected OsmandSettings(OsmandApplication clientContext, SettingsAPI settinsAPI) {
		ctx = clientContext;
		this.settingsAPI = settinsAPI;
		globalPreferences = settingsAPI.getPreferenceObject(SHARED_PREFERENCES_NAME);
		profilePreferences = defaultProfilePreferences;
	}
	
	public OsmandApplication getContext() {
		return ctx;
	}
	
	public void setSettingsAPI(SettingsAPI settingsAPI) {
		this.settingsAPI = settingsAPI;
	}
	
	public SettingsAPI getSettingsAPI() {
		return settingsAPI;
	}
	
	// Check internet connection available every 15 seconds
	public boolean isInternetConnectionAvailable(){
		//System.err.println("call is Internet Connection Available.");
		return isInternetConnectionAvailable(false);
	}
	public boolean isInternetConnectionAvailable(boolean update){
		long delta = System.currentTimeMillis() - lastTimeInternetConnectionChecked;
		if(delta < 0 || delta > 15000 || update){
			internetConnectionAvailable = isInternetConnected();
		}
		return internetConnectionAvailable;
	}
	
	public boolean isWifiConnected() {
		ConnectivityManager mgr =  (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = mgr.getActiveNetworkInfo();
		return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	private boolean isInternetConnected() {
		ConnectivityManager mgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = mgr.getActiveNetworkInfo();
		if(active == null){
			return false;
		} else {
			NetworkInfo.State state = active.getState();
			//System.err.println("net work state: "+active.isAvailable());
			return state != NetworkInfo.State.DISCONNECTED && state != NetworkInfo.State.DISCONNECTING;
		}
	}


	/////////////// PREFERENCES classes ////////////////
	
	public abstract class CommonPreference<T> extends PreferenceWithListener<T> {
		private final String id;
		private boolean global;
		private T cachedValue;
		private Object cachedPreference;
		private boolean cache;		
		private T defaultValue;
		
		
		public CommonPreference(String id, T defaultValue){
			this.id = id;
			this.defaultValue = defaultValue; 
		}
		
		public CommonPreference<T> makeGlobal(){
			global = true;
			return this;
		}
		
		public CommonPreference<T> cache(){
			cache = true;
			return this;
		}
		
		public CommonPreference<T> makeProfile(){
			global = false;
			return this;
		}
		
		protected Object getPreferences(){
			return global ? globalPreferences : profilePreferences;
		}
		
		protected T getDefaultValue(){
			if(global){
				return defaultValue;
			}
			
			if(settingsAPI.contains(defaultProfilePreferences, getId())) {
				return getValue(defaultProfilePreferences, defaultValue);
			} else {
				return defaultValue;
			}
		}
		
		@Override
		public void overrideDefaultValue(T newDefaultValue) {
			this.defaultValue = newDefaultValue;
		}
		
		protected abstract T getValue(Object prefs, T defaultValue);
		
		protected abstract boolean setValue(Object prefs, T val);
				

		@Override
		public T get() {
			if(cache && cachedValue != null && cachedPreference == getPreferences()){
				return cachedValue;
			}
			cachedPreference = getPreferences();
			cachedValue = getValue(cachedPreference, getDefaultValue());
			return cachedValue;
		}

		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public void resetToDefault(){
			set(getDefaultValue());
		}

		@Override
		public boolean set(T obj) {
			Object prefs = getPreferences();
			if (setValue(prefs, obj)) {
				cachedValue = obj;
				cachedPreference = prefs;				
				return true;
			}
			return false;
		}
		
	}
	
	private class BooleanPreference extends CommonPreference<Boolean> {

		
		private BooleanPreference(String id, boolean defaultValue) {
			super(id, defaultValue);
		}
		
		@Override
		protected Boolean getValue(Object prefs, Boolean defaultValue) {
			return settingsAPI.getBoolean(prefs, getId(), defaultValue);
		}

		@Override
		protected boolean setValue(Object prefs, Boolean val) {
			return settingsAPI.edit( prefs).putBoolean(getId(), val).commit();
		}
	}
	
	private class BooleanAccessibilityPreference extends BooleanPreference {

		private BooleanAccessibilityPreference(String id, boolean defaultValue) {
			super(id, defaultValue);
		}
		
		@Override
		protected Boolean getValue(Object prefs, Boolean defaultValue) {
			return ctx.accessibilityEnabled() ?
				super.getValue(prefs, defaultValue) :
				defaultValue;
		}

		@Override
		protected boolean setValue(Object prefs, Boolean val) {
			return ctx.accessibilityEnabled() ?
				super.setValue(prefs, val) :
				false;
		}
	}

	private class IntPreference extends CommonPreference<Integer> {


		private IntPreference(String id, int defaultValue) {
			super(id, defaultValue);
		}
		
		@Override
		protected Integer getValue(Object prefs, Integer defaultValue) {
			return settingsAPI.getInt(prefs, getId(), defaultValue);
		}

		@Override
		protected boolean setValue(Object prefs, Integer val) {
			return settingsAPI.edit( prefs).putInt(getId(), val).commit();
		}

	}
	
	private class LongPreference extends CommonPreference<Long> {


		private LongPreference(String id, long defaultValue) {
			super(id, defaultValue);
		}
		
		@Override
		protected Long getValue(Object prefs, Long defaultValue) {
			return settingsAPI.getLong(prefs, getId(), defaultValue);
		}

		@Override
		protected boolean setValue(Object prefs, Long val) {
			return settingsAPI.edit( prefs).putLong(getId(), val).commit();
		}

	}
	
	private class FloatPreference extends CommonPreference<Float> {


		private FloatPreference(String id, float defaultValue) {
			super(id, defaultValue);
		}
		
		@Override
		protected Float getValue(Object prefs, Float defaultValue) {
			return settingsAPI.getFloat(prefs,getId(), defaultValue);
		}

		@Override
		protected boolean setValue(Object prefs, Float val) {
			return settingsAPI.edit( prefs).putFloat(getId(), val).commit();
		}

	}
	
	private class StringPreference extends CommonPreference<String> {

		private StringPreference(String id, String defaultValue) {
			super(id, defaultValue);
		}

		@Override
		protected String getValue(Object prefs, String defaultValue) {
			return settingsAPI.getString(prefs, getId(), defaultValue);
		}

		@Override
		protected boolean setValue(Object prefs, String val) {
			return settingsAPI.edit( prefs).putString(getId(), (val != null) ? val.trim() : val).commit();
		}

	}
	
	private class EnumIntPreference<E extends Enum<E>> extends CommonPreference<E> {

		private final E[] values;

		private EnumIntPreference(String id, E defaultValue, E[] values) {
			super(id, defaultValue);
			this.values = values;
		}
		

		@Override
		protected E getValue(Object prefs, E defaultValue) {
			try {
				int i = settingsAPI.getInt(prefs, getId(), -1);
				if(i >= 0 && i < values.length){
					return values[i];
				}
			} catch (ClassCastException ex) {
				setValue(prefs, defaultValue);
			}
			return defaultValue;
		}
		
		@Override
		protected boolean setValue(Object prefs,E val) {
			return settingsAPI.edit( prefs).putInt(getId(), val.ordinal()).commit();
		}

	}
	///////////// PREFERENCES classes ////////////////
	
	// this value string is synchronized with settings_pref.xml preference name
	private final OsmandPreference<String> PLUGINS = new StringPreference("enabled_plugins", "").makeGlobal();
	
	public Set<String> getEnabledPlugins(){
		String plugs = PLUGINS.get();
		StringTokenizer toks = new StringTokenizer(plugs, ",");
		Set<String> res = new LinkedHashSet<String>();
		while(toks.hasMoreTokens()) {
			String tok = toks.nextToken();
			if(!tok.startsWith("-")) {
				res.add(tok);
			}
		}
		return res;
	}
	
	public Set<String> getPlugins(){
		String plugs = PLUGINS.get();
		StringTokenizer toks = new StringTokenizer(plugs, ",");
		Set<String> res = new LinkedHashSet<String>();
		while(toks.hasMoreTokens()) {
			res.add(toks.nextToken());
		}
		return res;
	}
	
	public void enablePlugin(String pluginId, boolean enable){
		//System.out.println("enablePlugin.");
		Set<String> set = getPlugins();
		if (enable) {
			set.remove("-" + pluginId);
			set.add(pluginId);
		} else {
			set.remove(pluginId);
			set.add("-" + pluginId);
		}
		StringBuilder serialization = new StringBuilder();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			serialization.append(it.next());
			if(it.hasNext()) {
				serialization.append(",");
			}
		}
		if(!serialization.toString().equals(PLUGINS.get())) {
			PLUGINS.set(serialization.toString());
		}
	}
	

	@SuppressWarnings("unchecked")
	public CommonPreference<Boolean> registerBooleanPreference(String id, boolean defValue) {
		if(registeredPreferences.containsKey(id)) {
			return (CommonPreference<Boolean>) registeredPreferences.get(id);
		}
		BooleanPreference p = new BooleanPreference(id, defValue);
		registeredPreferences.put(id, p);
		return p;
	}
	
	@SuppressWarnings("unchecked")
	public CommonPreference<String> registerBooleanPreference(String id, String defValue) {
		if(registeredPreferences.containsKey(id)) {
			return (CommonPreference<String>) registeredPreferences.get(id);
		}
		StringPreference p = new StringPreference(id, defValue);
		registeredPreferences.put(id, p);
		return p;
	}
	
	@SuppressWarnings("unchecked")
	public CommonPreference<Integer> registerIntPreference(String id, int defValue) {
		if(registeredPreferences.containsKey(id)) {
			return (CommonPreference<Integer>) registeredPreferences.get(id);
		}
		IntPreference p = new IntPreference(id, defValue);
		registeredPreferences.put(id, p);
		return p;
	}
	
	@SuppressWarnings("unchecked")
	public CommonPreference<Long> registerLongPreference(String id, long defValue) {
		if(registeredPreferences.containsKey(id)) {
			return (CommonPreference<Long>) registeredPreferences.get(id);
		}
		LongPreference p = new LongPreference(id, defValue);
		registeredPreferences.put(id, p);
		return p;
	}
	
	@SuppressWarnings("unchecked")
	public CommonPreference<Float> registerFloatPreference(String id, float defValue) {
		if(registeredPreferences.containsKey(id)) {
			return (CommonPreference<Float>) registeredPreferences.get(id);
		}
		FloatPreference p = new FloatPreference(id, defValue);
		registeredPreferences.put(id, p);
		return p;
	}
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Boolean> USE_INTERNET_TO_DOWNLOAD_TILES = new BooleanPreference("use_internet_to_download_tiles", true).makeGlobal().cache();
	
	public final OsmandPreference<String> AVAILABLE_APP_MODES =  new StringPreference("available_application_modes", "car,bicycle,pedestrian,").makeGlobal().cache();
	
//	public final OsmandPreference<ApplicationMode> DEFAULT_APPLICATION_MODE = new CommonPreference<ApplicationMode>("default_application_mode_string", ApplicationMode.DEFAULT) {
//		{
//			makeGlobal();
//		}
//
//		@Override
//		protected ApplicationMode getValue(Object prefs, ApplicationMode defaultValue) {
//			String key = settingsAPI.getString(prefs, getId(), defaultValue.getStringKey());
//			return ApplicationMode.valueOfStringKey(key, defaultValue);
//		}
//		
//		@Override
//		protected boolean setValue(Object prefs, ApplicationMode val) {
//			return settingsAPI.edit( prefs).putString(getId(), val.getStringKey()).commit();
//		}
//	}; 
//

//	public final OsmandPreference<DrivingRegion> DRIVING_REGION = new EnumIntPreference<DrivingRegion>(
//			"default_driving_region", DrivingRegion.EUROPE_ASIA, DrivingRegion.values()) {
//		protected boolean setValue(Object prefs, DrivingRegion val) {
//			((CommonPreference<MetricsConstants>)METRIC_SYSTEM).cachedValue = null;
//			return super.setValue(prefs, val);
//		};
//	}.makeGlobal().cache();
	
	// this value string is synchronized with settings_pref.xml preference name
	// cache of metrics constants as they are used very often
	public final OsmandPreference<MetricsConstants> METRIC_SYSTEM = new EnumIntPreference<MetricsConstants>(
			"default_metric_system", MetricsConstants.KILOMETERS_AND_METERS, MetricsConstants.values()){
		protected MetricsConstants getDefaultValue() {
			return  MetricsConstants.KILOMETERS_AND_METERS;
		};
	}.makeGlobal().cache();
	
	
	
	// this value string is synchronized with settings_pref.xml preference name
	// cache of metrics constants as they are used very often
//	public final OsmandPreference<RelativeDirectionStyle> DIRECTION_STYLE = new EnumIntPreference<RelativeDirectionStyle>(
//			"direction_style", RelativeDirectionStyle.SIDEWISE, RelativeDirectionStyle.values()).makeGlobal().cache();
//	
	// this value string is synchronized with settings_pref.xml preference name
	// cache of metrics constants as they are used very often
	public final OsmandPreference<AccessibilityMode> ACCESSIBILITY_MODE = new EnumIntPreference<AccessibilityMode>(
			"accessibility_mode", AccessibilityMode.DEFAULT, AccessibilityMode.values()).makeGlobal().cache();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Float> SPEECH_RATE =
		new FloatPreference("speech_rate", 1f).makeGlobal();
	
	public final OsmandPreference<Boolean> SELECTED_OFFLINE_MAP_SAVE_DIR =
			new BooleanPreference("selected_offline_map_dir",false).makeGlobal();
	
	
	//显示经纬�?	
	public final OsmandPreference<Boolean> SHOW_LOCATION =
			new BooleanPreference("show_location",true).makeGlobal();
	
	//显示速度
		public final OsmandPreference<Boolean> SHOW_SPEED =
				new BooleanPreference("show_speed",true).makeGlobal();
		
		//显示航向
				public final OsmandPreference<Boolean> SHOW_COURSE =
						new BooleanPreference("show_course",true).makeGlobal();
				
//				//显示速度航向
//				public final OsmandPreference<Boolean> SHOW_SPEED_COURSE =
//						new BooleanPreference("show_speed_course",true).makeGlobal();
	
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> USE_TRACKBALL_FOR_MOVEMENTS =
		new BooleanPreference("use_trackball_for_movements", true).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> ZOOM_BY_TRACKBALL =
		new BooleanAccessibilityPreference("zoom_by_trackball", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> SCROLL_MAP_BY_GESTURES =
		new BooleanAccessibilityPreference("scroll_map_by_gestures", true).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> USE_SHORT_OBJECT_NAMES =
		new BooleanAccessibilityPreference("use_short_object_names", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> ACCESSIBILITY_EXTENSIONS =
		new BooleanAccessibilityPreference("accessibility_extensions", false).makeGlobal();
		
	
	// magnetic field doesn'torkmost of the time on some phones
	public final OsmandPreference<Boolean> USE_MAGNETIC_FIELD_SENSOR_COMPASS = new BooleanPreference("use_magnetic_field_sensor_compass", false).makeGlobal().cache();
	public final OsmandPreference<Boolean> USE_KALMAN_FILTER_FOR_COMPASS = new BooleanPreference("use_kalman_filter_compass", true).makeGlobal().cache();
	
	public final CommonPreference<Float> MAP_ZOOM_SCALE_BY_DENSITY = new FloatPreference("map_zoom_scale_wo_density", 0f).makeProfile().cache();
//	{
//		//MAP_ZOOM_SCALE_BY_DENSITY.setModeDefaultValue(ApplicationMode.CAR, 0.5f);//yang chun
//		MAP_ZOOM_SCALE_BY_DENSITY.setModeDefaultValue(ApplicationMode.LANDMAP, 0.5f);
//		
//		//MAP_ZOOM_SCALE_BY_DENSITY.setModeDefaultValue("land", 0.5f);
//	}
	
	public float getSettingsZoomScale(float density){
		// by default scale between [0, 1[ density (because of lots map complains)
		//return MAP_ZOOM_SCALE_BY_DENSITY.get() + (float)Math.sqrt(Math.max(0, density - 1));
		return (0.0f+(float)Math.sqrt(Math.max(0, density - 1)));
	}
	

	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> SHOW_POI_OVER_MAP = new BooleanPreference("show_poi_over_map", false).makeGlobal();
	
	public final OsmandPreference<Boolean> SHOW_POI_LABEL = new BooleanPreference("show_poi_label", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> SHOW_TRANSPORT_OVER_MAP = new BooleanPreference("show_transport_over_map", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<String> PREFERRED_LOCALE =  new StringPreference("preferred_locale", "").makeGlobal();

	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<String> USER_NAME = new StringPreference("user_name", "NoName").makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<String> USER_OSM_BUG_NAME = 
		new StringPreference("user_osm_bug_name", "NoName/OsmAnd").makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<String> USER_PASSWORD = 
		new StringPreference("user_password", "").makeGlobal();

	// this value boolean is synchronized with settings_pref.xml preference offline POI/Bugs edition
	public final OsmandPreference<Boolean> OFFLINE_EDITION = new BooleanPreference("offline_edition", false).makeGlobal();

	
	// this value string is synchronized with settings_pref.xml preference name
//	public final CommonPreference<DayNightMode> DAYNIGHT_MODE =
//		new EnumIntPreference<DayNightMode>("daynight_mode", DayNightMode.DAY, DayNightMode.values());
//	{
//		DAYNIGHT_MODE.makeProfile().cache();
//		//yang chun
////		DAYNIGHT_MODE.setModeDefaultValue(ApplicationMode.CAR, DayNightMode.AUTO);
////		DAYNIGHT_MODE.setModeDefaultValue(ApplicationMode.BICYCLE, DayNightMode.AUTO);
////		DAYNIGHT_MODE.setModeDefaultValue(ApplicationMode.PEDESTRIAN, DayNightMode.DAY);
//
//	//	DAYNIGHT_MODE.setModeDefaultValue(ApplicationMode.LANDMAP, DayNightMode.DAY);
//	}
	
	// this value string is synchronized with settings_pref.xml preference name
//	public final OsmandPreference<RouteService> ROUTER_SERVICE = 
//			new EnumIntPreference<RouteService>("router_service", RouteService.OSMAND, 
//					RouteService.values()).makeProfile();	
//	
	// this value string is synchronized with settings_pref.xml preference name
//	public final CommonPreference<AutoZoomMap> AUTO_ZOOM_MAP =
//		new EnumIntPreference<AutoZoomMap>("auto_zoom_map_new", AutoZoomMap.NONE,
//				AutoZoomMap.values()).makeProfile().cache();
//	{
//		//yang chun
////		AUTO_ZOOM_MAP.setModeDefaultValue(ApplicationMode.CAR, AutoZoomMap.FARTHEST);
////		AUTO_ZOOM_MAP.setModeDefaultValue(ApplicationMode.BICYCLE, AutoZoomMap.NONE);
////		AUTO_ZOOM_MAP.setModeDefaultValue(ApplicationMode.PEDESTRIAN, AutoZoomMap.NONE);
////		AUTO_ZOOM_MAP.setModeDefaultValue(ApplicationMode.LANDMAP, AutoZoomMap.FARTHEST);
//	}
	
	public final CommonPreference<Boolean> SNAP_TO_ROAD = new BooleanPreference("snap_to_road", false).makeProfile().cache();
	{
		//yang chun
//		SNAP_TO_ROAD.setModeDefaultValue(ApplicationMode.CAR, true);
//		SNAP_TO_ROAD.setModeDefaultValue(ApplicationMode.BICYCLE, true);
	//	SNAP_TO_ROAD.setModeDefaultValue(ApplicationMode.LANDMAP, true);
	}
	

	// this value string is synchronized with settings_pref.xml preference name
	public static final String SAVE_CURRENT_TRACK = "save_current_track"; //$NON-NLS-1$

	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Boolean> SAVE_TRACK_TO_GPX = new BooleanPreference("save_track_to_gpx", false).makeProfile().cache();
	{
		//yang chun
		//SAVE_TRACK_TO_GPX.setModeDefaultValue(ApplicationMode.CAR, true);
		//SAVE_TRACK_TO_GPX.setModeDefaultValue(ApplicationMode.BICYCLE, true);
		//SAVE_TRACK_TO_GPX.setModeDefaultValue(ApplicationMode.PEDESTRIAN, true);
	//	SAVE_TRACK_TO_GPX.setModeDefaultValue(ApplicationMode.LANDMAP, true);
	}
	
	public final CommonPreference<Boolean> SAVE_GLOBAL_TRACK_TO_GPX = new BooleanPreference("save_global_track_to_gpx", false).makeGlobal().cache();
	public final CommonPreference<Integer> SAVE_GLOBAL_TRACK_INTERVAL  = new IntPreference("save_global_track_interval", 5000).makeGlobal().cache();
	// Json 
	public final OsmandPreference<String> SELECTED_GPX = new StringPreference("selected_gpx", "").makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> FAST_ROUTE_MODE = new BooleanPreference("fast_route_mode", true).makeProfile();
	// temporarily for new version
	public final CommonPreference<Boolean> DISABLE_COMPLEX_ROUTING = new BooleanPreference("disable_complex_routing", false).makeGlobal();
	
	public final OsmandPreference<Boolean> SHOW_TRAFFIC_WARNINGS = new BooleanPreference("show_traffic_warnings", true).makeProfile().cache();
	public final OsmandPreference<Boolean> SHOW_CAMERAS = new BooleanPreference("show_cameras", true).makeProfile().cache();
	public final OsmandPreference<Boolean> SHOW_LANES = new BooleanPreference("show_lanes", true).makeProfile().cache();
	
	public final OsmandPreference<Boolean> SPEAK_TRAFFIC_WARNINGS = new BooleanPreference("speak_traffic_warnings", true).makeProfile().cache();
	public final OsmandPreference<Boolean> SPEAK_STREET_NAMES = new BooleanPreference("speak_street_names", true).makeProfile().cache();
	public final OsmandPreference<Boolean> SPEAK_SPEED_CAMERA = new BooleanPreference("speak_cameras", true).makeProfile().cache();
	public final OsmandPreference<Boolean> SPEAK_SPEED_LIMIT = new BooleanPreference("speak_speed_limit", true).makeProfile().cache();
	
	public final OsmandPreference<Boolean> ROUTE_CALC_OSMAND_PARTS = new BooleanPreference("gpx_routing_calculate_osmand_route", true).makeGlobal().cache();
	public final OsmandPreference<Boolean> SPEAK_GPX_WPT = new BooleanPreference("speak_gpx_wpt", true).makeGlobal().cache();
	public final OsmandPreference<Boolean> CALC_GPX_ROUTE = new BooleanPreference("calc_gpx_route", false).makeGlobal().cache();
	
	

	public final OsmandPreference<Boolean> AVOID_TOLL_ROADS = new BooleanPreference("avoid_toll_roads", false).makeProfile().cache();
	public final OsmandPreference<Boolean> AVOID_MOTORWAY = new BooleanPreference("avoid_motorway", false).makeProfile().cache();
	public final OsmandPreference<Boolean> AVOID_UNPAVED_ROADS = new BooleanPreference("avoid_unpaved_roads", false).makeProfile().cache();
	public final OsmandPreference<Boolean> AVOID_FERRIES = new BooleanPreference("avoid_ferries", false).makeProfile().cache();
	
	public final OsmandPreference<Boolean> PREFER_MOTORWAYS = new BooleanPreference("prefer_motorways", false).makeProfile().cache();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Integer> SAVE_TRACK_INTERVAL = new IntPreference("save_track_interval", 1000).makeProfile();
	{
//		SAVE_TRACK_INTERVAL.setModeDefaultValue(ApplicationMode.CAR, 3000);
//		SAVE_TRACK_INTERVAL.setModeDefaultValue(ApplicationMode.BICYCLE, 7000);
//		SAVE_TRACK_INTERVAL.setModeDefaultValue(ApplicationMode.PEDESTRIAN, 10000);
		//yang chun
	//	SAVE_TRACK_INTERVAL.setModeDefaultValue(ApplicationMode.LANDMAP, 3000);
	}


	public final OsmandPreference<Boolean> ALERT_SWITCH = new BooleanPreference(
			"alert_switch", false).makeGlobal();
	public final CommonPreference<Integer> ALERT_RADIUS = new IntPreference(
			"alert_radius", 3*100).makeProfile();//存的数值放大100倍。
	public final CommonPreference<Integer> SHOW_RADIUS = new IntPreference(
			"show_radius", 30*100).makeProfile();//存的数值放大100倍。

	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Boolean> LIVE_MONITORING = new BooleanPreference("live_monitoring", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Integer> LIVE_MONITORING_INTERVAL = new IntPreference("live_monitoring_interval", 5000).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<String> LIVE_MONITORING_URL = new StringPreference("live_monitoring_url", 
			"http://example.com?lat={0}&lon={1}&timestamp={2}&hdop={3}&altitude={4}&speed={5}").makeGlobal();
	
	public final CommonPreference<String> GPS_STATUS_APP = new StringPreference("gps_status_app", "").makeGlobal();

	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> SHOW_OSM_BUGS = new BooleanPreference("show_osm_bugs", false).makeGlobal();
	
	public final OsmandPreference<String> MAP_INFO_CONTROLS = new StringPreference("map_info_controls", "").makeProfile();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> DEBUG_RENDERING_INFO = new BooleanPreference("debug_rendering", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> SHOW_FAVORITES = new BooleanPreference("show_favorites", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Integer> MAP_SCREEN_ORIENTATION = 
		new IntPreference("map_screen_orientation", -1/*ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED*/).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Boolean> SHOW_VIEW_ANGLE = new BooleanPreference("show_view_angle", false).makeProfile().cache();
	{
//		SHOW_VIEW_ANGLE.setModeDefaultValue(ApplicationMode.CAR, false);
//		SHOW_VIEW_ANGLE.setModeDefaultValue(ApplicationMode.BICYCLE, true);
//		SHOW_VIEW_ANGLE.setModeDefaultValue(ApplicationMode.PEDESTRIAN, true);
		//yang chun
//		SHOW_VIEW_ANGLE.setModeDefaultValue(ApplicationMode.LANDMAP, false);
	}
	
	// this value string is synchronized with settings_pref.xml preference name
	// seconds to auto_follow 
	public final CommonPreference<Integer> AUTO_FOLLOW_ROUTE = new IntPreference("auto_follow_route", 0).makeProfile();
	{
//		AUTO_FOLLOW_ROUTE.setModeDefaultValue(ApplicationMode.CAR, 15);
//		AUTO_FOLLOW_ROUTE.setModeDefaultValue(ApplicationMode.BICYCLE, 15);
//		AUTO_FOLLOW_ROUTE.setModeDefaultValue(ApplicationMode.PEDESTRIAN, 0);
		//yang chun
	}
	
	// this value string is synchronized with settings_pref.xml preference name
	// try without AUTO_FOLLOW_ROUTE_NAV (see forum discussion 'Simplify our navigation preference menu')
	//public final CommonPreference<Boolean> AUTO_FOLLOW_ROUTE_NAV = new BooleanPreference("auto_follow_route_navigation", true, false);

	// this value string is synchronized with settings_pref.xml preference name
	public static final int ROTATE_MAP_NONE = 0;
	public static final int ROTATE_MAP_BEARING = 1;
	public static final int ROTATE_MAP_COMPASS = 2;
	public final CommonPreference<Integer> ROTATE_MAP = 
			new IntPreference("rotate_map", ROTATE_MAP_NONE).makeProfile().cache();
	{
//		ROTATE_MAP.setModeDefaultValue(ApplicationMode.CAR, ROTATE_MAP_BEARING);
//		ROTATE_MAP.setModeDefaultValue(ApplicationMode.BICYCLE, ROTATE_MAP_BEARING);
//		ROTATE_MAP.setModeDefaultValue(ApplicationMode.PEDESTRIAN, ROTATE_MAP_COMPASS);
		//yang chun
	}

	// this value string is synchronized with settings_pref.xml preference name
	public static final int CENTER_CONSTANT = 0;
	public static final int BOTTOM_CONSTANT = 1;
	public final CommonPreference<Integer> POSITION_ON_MAP = new IntPreference("position_on_map", CENTER_CONSTANT).makeProfile();
	{
//		POSITION_ON_MAP.setModeDefaultValue(ApplicationMode.CAR, BOTTOM_CONSTANT);
//		POSITION_ON_MAP.setModeDefaultValue(ApplicationMode.BICYCLE, BOTTOM_CONSTANT);
//		POSITION_ON_MAP.setModeDefaultValue(ApplicationMode.PEDESTRIAN, CENTER_CONSTANT);
		//yang chun
	}
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Integer> MAX_LEVEL_TO_DOWNLOAD_TILE = new IntPreference("max_level_download_tile", 15).makeProfile().cache();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Integer> LEVEL_TO_SWITCH_VECTOR_RASTER = new IntPreference("level_to_switch_vector_raster", 1).makeGlobal().cache();

	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Integer> AUDIO_STREAM_GUIDANCE = new IntPreference("audio_stream",
			3/*AudioManager.STREAM_MUSIC*/).makeGlobal();

	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> USE_ENGLISH_NAMES = new BooleanPreference("use_english_names", false).makeGlobal();
	
	public boolean usingEnglishNames(){
		return USE_ENGLISH_NAMES.get();
	}
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Boolean> MAP_ONLINE_DATA = new BooleanPreference("map_online_data", true).makeGlobal();

	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Boolean> SHOW_DESTINATION_ARROW = new BooleanPreference("show_destination_arrow", true).makeProfile();
	{
		//SHOW_DESTINATION_ARROW.setModeDefaultValue(ApplicationMode.CAR, false);	
		//yang chun
	}
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<String> MAP_OVERLAY = new StringPreference("map_overlay", null).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<String> MAP_UNDERLAY = new StringPreference("map_underlay", null).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Integer> MAP_OVERLAY_TRANSPARENCY = new IntPreference("overlay_transparency",
			100).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<Integer> MAP_TRANSPARENCY = new IntPreference("map_transparency",
			255).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final CommonPreference<String> MAP_TILE_SOURCES = new StringPreference("map_tile_sources",
			//TileSourceManager.getMapnikSource().getName()).makeGlobal();//原版
			TileSourceManager.getManyShipsMapSource(MapActivity.mapKey).getName()).makeGlobal();
	
	
	public CommonPreference<String> PREVIOUS_INSTALLED_VERSION = new StringPreference("previous_installed_version", "").makeGlobal();


	public ITileSource getMapTileSource(boolean warnWhenSelected) {
//		String tileName = MAP_TILE_SOURCES.get();
//		if (tileName != null) {
//			ITileSource ts = getTileSourceByName(tileName, warnWhenSelected);
//			if(ts != null){
//				return ts;
//			}
//		}
		//return TileSourceManager.getMapnikSource();//原版
		return TileSourceManager.getManyShipsMapSource(MapActivity.mapKey);
	}
	public ITileSource getGreenMapTileSource(boolean warnWhenSelected) {
//		String tileName = MAP_TILE_SOURCES.get();
//		if (tileName != null) {
//			ITileSource ts = getTileSourceByName(tileName, warnWhenSelected);
//			if(ts != null){
//				return ts;
//			}
//		}
		//return TileSourceManager.getMapnikSource();//原版
//		System.err.println("getGreenMapTileSource");
		return TileSourceManager.getManyShipsMapSource(4);
	}
	public ITileSource getWeatherMapTileSource(boolean warnWhenSelected) {
		return TileSourceManager.getManyShipsMapSource(5);
	}
	public ITileSource getWaveMapTileSource(boolean warnWhenSelected) {
		return TileSourceManager.getManyShipsMapSource(6);
	}
	public ITileSource getWindMapTileSource(boolean warnWhenSelected) {
		return TileSourceManager.getManyShipsMapSource(7);
	}
	public ITileSource getOceanMapTileSource(boolean warnWhenSelected) {
		return TileSourceManager.getManyShipsMapSource(8);
	}
//	private TileSourceTemplate checkAmongAvailableTileSources(File dir, List<TileSourceTemplate> list){
//		if (list != null) {
//			for (TileSourceTemplate l : list) {
//				if (dir.getName().equals(l.getName())) {
//					try {
//						dir.mkdirs();
//						TileSourceManager.createMetaInfoFile(dir, l, true);
//					} catch (IOException e) {
//					}
//					return l;
//				}
//				
//			}
//		}
//		return null;
//	}
	

//	public ITileSource getTileSourceByName(String tileName, boolean warnWhenSelected) {
//		if(tileName == null || tileName.length() == 0){
//			return null;
//		}
//		List<TileSourceTemplate> knownTemplates = TileSourceManager.getKnownSourceTemplates();
//		File tPath = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
//		File dir = new File(tPath, tileName);
//		if (!dir.exists()) {
//			return checkAmongAvailableTileSources(dir, knownTemplates);
//		} else if (tileName.endsWith(IndexConstants.SQLITE_EXT)) {
//			return new SQLiteTileSource(ctx, dir, knownTemplates);
//		} else if (dir.isDirectory() && !dir.getName().startsWith(".")) {
//			TileSourceTemplate t = TileSourceManager.createTileSourceTemplate(dir);
//			if (warnWhenSelected && !t.isRuleAcceptable()) {
//				ctx.showToastMessage(R.string.warning_tile_layer_not_downloadable, dir.getName());
//			}
//			if (!TileSourceManager.isTileSourceMetaInfoExist(dir)) {
//				TileSourceTemplate ret = checkAmongAvailableTileSources(dir, knownTemplates);
//				if (ret != null) {
//					t = ret;
//				}
//			}
//			return t;
//		}
//		return null;
//	}
	
//	public boolean installTileSource(TileSourceTemplate toInstall){
//		File tPath = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
//		File dir = new File(tPath, toInstall.getName());
//		dir.mkdirs();
//		if(dir.exists() && dir.isDirectory()){
//			try {
//				TileSourceManager.createMetaInfoFile(dir, toInstall, true);
//			} catch (IOException e) {
//				return false;
//			}
//		}
//		return true;
//	}
	
	public Map<String, String> getTileSourceEntries(){
		return getTileSourceEntries(true);
		
    }
	
	//当�?择�?海图来源...”这个菜单时，应该只显示海图的来�?	
	public Map<String,String> getChartTileSourceEntries(){
		return getChartTileSourceEntries(true);
	}
	
	String nameForSqliteDbContainsThisRegionTiles;
	public String getNameForSqliteDbContainsThisRegion(){
		return nameForSqliteDbContainsThisRegionTiles.substring(0,nameForSqliteDbContainsThisRegionTiles.indexOf(".")); 
	}
	
	public Map<String,File> getChartTileSourceNameByTilexy(int tilex,int tiley,int zoom,HashMap<String,OfflineDataRegion> mapping){
		
		Iterator it = mapping.keySet().iterator();
		
		double maxlon,maxlat,minlon,minlat;
		double lon,lat;
		PixelXY pixel = BingMapTileAlgorithms.TileXYToPixelXY(tilex, tiley);
		LatLon lonlat = BingMapTileAlgorithms.PixelXYToLatLon(pixel.getPx(), pixel.getPy(), zoom);
		lon = lonlat.getLongitude();
		lat = lonlat.getLatitude();
		
		File dir = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
		Map<String, File> map = new LinkedHashMap<String, File>();
		//File[] files = dir.listFiles();
		while(it.hasNext()){
			String name = (String) it.next();
			OfflineDataRegion  region = mapping.get(name);
			maxlat = region.getMaxlat();
			maxlon = region.getMaxlon();
			minlat = region.getMinlat();
			minlon = region.getMinlon();
			//System.err.println(minlon+" "+lon+" "+maxlon+", "+minlat+" "+lat+" "+maxlat);
			if(lon>=minlon && lon<=maxlon && lat>=minlat && lat<=maxlat){
				File sqllitedb = new File(dir,name);
				nameForSqliteDbContainsThisRegionTiles = name;
				if(sqllitedb.exists()){
					//System.err.println("找到了对应区域的离线包："+name);
					map.put(name, sqllitedb);
				}

			}
			
		}		
		return map;
	}
	
	public Map<String,File> getChartTileSourceEntriesFile(boolean sqlite){
		Map<String, File> map = new LinkedHashMap<String, File>();
		File dir = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
		if (dir != null && dir.canRead()) {
			File[] files = dir.listFiles();
			Arrays.sort(files, new Comparator<File>(){
				@Override
				public int compare(File object1, File object2) {
					if(object1.lastModified() > object2.lastModified()){
						return -1;
					} else if(object1.lastModified() == object2.lastModified()){
						return 0;
					}
					return 1;
				}
				
			});
			
			if (files != null ) {
				for (File f : files) {
					if (f.getName().endsWith(IndexConstants.SQLITE_EXT)) {
						if(sqlite) {
							String n = f.getName();
							map.put(f.getName(), f);
							//System.out.println("sqlite_ext true: "+f.getName());
						}
					} else if (f.isDirectory() && !f.getName().equals(IndexConstants.TEMP_SOURCE_TO_LOAD)
							&& !f.getName().startsWith(".")) {
						//System.out.println("sqlite_ext false: "+f.getName());
						map.put(f.getName(), f);
					}else{
						map.put(f.getName(), f);
					}
				}
			}
			
			
		}
		
//		for(TileSourceTemplate l : TileSourceManager.getKnownChartSourceTemplates()){
//			map.put(l.getName(), l.getName());
//		}
		return map;
	}
	
	public Map<String,String> getChartTileSourceEntries(boolean sqlite){
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		/*
		File dir = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);		
		if (dir != null && dir.canRead()) {
			File[] files = dir.listFiles();
			Arrays.sort(files, new Comparator<File>(){
				@Override
				public int compare(File object1, File object2) {
					if(object1.lastModified() > object2.lastModified()){
						return -1;
					} else if(object1.lastModified() == object2.lastModified()){
						return 0;
					}
					return 1;
				}
				
			});
			
			if (files != null ) {
				for (File f : files) {
					if (f.getName().endsWith(IndexConstants.SQLITE_EXT)) {
						if(sqlite) {
							String n = f.getName();
							map.put(f.getName(), n.substring(0, n.lastIndexOf('.')));
							//System.out.println("sqlite_ext true: "+f.getName());
						}
					} else if (f.isDirectory() && !f.getName().equals(IndexConstants.TEMP_SOURCE_TO_LOAD)
							&& !f.getName().startsWith(".")) {
						//System.out.println("sqlite_ext false: "+f.getName());
						map.put(f.getName(), f.getName());
					}
				}
			}		
			
		}*/
		
//		for(TileSourceTemplate l : TileSourceManager.getKnownChartSourceTemplates()){
//			map.put(l.getName(), l.getName());
//		}
		return map;
	}
	
	public Map<String, String> getTileSourceEntries(boolean sqlite){
		Map<String, String> map = new LinkedHashMap<String, String>();
		File dir = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
		if (dir != null && dir.canRead()) {
			File[] files = dir.listFiles();
			Arrays.sort(files, new Comparator<File>(){
				@Override
				public int compare(File object1, File object2) {
					if(object1.lastModified() > object2.lastModified()){
						return -1;
					} else if(object1.lastModified() == object2.lastModified()){
						return 0;
					}
					return 1;
				}
				
			});
			if (files != null ) {
				for (File f : files) {
					if (f.getName().endsWith(IndexConstants.SQLITE_EXT)) {
						if(sqlite) {
							String n = f.getName();
							map.put(f.getName(), n.substring(0, n.lastIndexOf('.')));
						}
					} else if (f.isDirectory() && !f.getName().equals(IndexConstants.TEMP_SOURCE_TO_LOAD)
							&& !f.getName().startsWith(".")) {
						map.put(f.getName(), f.getName());
					}
				}
			}
		}
//		for(TileSourceTemplate l : TileSourceManager.getKnownSourceTemplates()){
//			map.put(l.getName(), l.getName());
//		}
		return map;
		
    }

	public static final String EXTERNAL_STORAGE_DIR = "external_storage_dir"; 
	public static final String EXTERNAL_STORAGE_DIR_BEFORE_CHANGE = "external_storage_dir_before_change"; 
	public static final String EXTERNAL_STORAGE_DIR_ALIAS="external_storage_dir_alias";
	
	public File getExternalStorageDirectory() {
		String defaultLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
		return new File(settingsAPI.getString(globalPreferences, EXTERNAL_STORAGE_DIR, 
				defaultLocation));
	}
	
	public String getExternalStorageDirectoryPath(){
		String defaultLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
		return (settingsAPI.getString(globalPreferences, EXTERNAL_STORAGE_DIR, 
				defaultLocation));
	}
	
	public String getExternalStorageDirectoryAlias(){
		String defaultLocation="手机内存";
		return settingsAPI.getString(globalPreferences, EXTERNAL_STORAGE_DIR_ALIAS, 
				defaultLocation);
	}
	
	public static final int VERSION_DEFAULTLOCATION_CHANGED = 19;

	public String getDefaultExternalStorageLocation() {
		String defaultLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
		return defaultLocation;
	}
	
	private static List<String> getWritableSecondaryStorages() {
	 	List<String> writableSecondaryStorage = new ArrayList<String>();
		try {
			String rawSecondaryStorage = System.getenv("SECONDARY_STORAGE");
			if (rawSecondaryStorage != null && rawSecondaryStorage.trim().length() > 0) {
				for (String secondaryPath : rawSecondaryStorage.split(":")) {
					File testFile = new File(secondaryPath);
					if (isWritable(testFile)) {
						writableSecondaryStorage.add(secondaryPath);
					}
				}
			}
		} catch (RuntimeException e) {
 			e.printStackTrace();
 		}
		return writableSecondaryStorage;
	}
	
	public String getMatchingExternalFilesDir(String dir) {
		// only API 19 !!
		try {
			File[] externalFilesDirs = ctx.getExternalFilesDirs(null);
			String rawSecondaryStorage = System.getenv("SECONDARY_STORAGE");
			if (rawSecondaryStorage != null && rawSecondaryStorage.trim().length() > 0 && externalFilesDirs != null) {
				for (String secondaryPath : rawSecondaryStorage.split(":")) {
					if (dir.startsWith(secondaryPath)) {
						for (File externFileDir : externalFilesDirs) {
							if (externFileDir != null && externFileDir.getAbsolutePath().startsWith(secondaryPath)) {
								return externFileDir.getAbsolutePath();
							}
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

	public List<String> getWritableSecondaryStorageDirectorys() {		
		// only API 19 !!
		// primary external storage directory
		String primaryExternalStorageDirectory = getDefaultExternalStorageLocation();
		// also creates directories, if they don't exist until now
		File[] externalFilesDirs = ctx.getExternalFilesDirs(null);
		List<String> writableSecondaryStorages = getWritableSecondaryStorages();
		List<String> writableSecondaryStorageDirectory = new ArrayList<String>();
		try {
			boolean primaryExternalStorageFound = false;
			if(externalFilesDirs != null) {
				for (File externFileDir : externalFilesDirs) {
					if (externFileDir != null) {
						final String externalFilePath = externFileDir.getAbsolutePath();
						//System.err.println("externalFilePath: "+externalFilePath);
						if (externalFilePath.startsWith(primaryExternalStorageDirectory) && !primaryExternalStorageFound) {
							// exclude primary external storage
							// no special location is required
							primaryExternalStorageFound = true;
						} else {
							// secondary storage
							// check if special location is required
							boolean specialPathRequired = true;
							for (String writableSecondaryStorage : writableSecondaryStorages) {
								if (externalFilePath.startsWith(writableSecondaryStorage)) {
									// no special location required
									//System.err.println("for loop: "+writableSecondaryStorage);
									writableSecondaryStorageDirectory.add(writableSecondaryStorage);
									specialPathRequired = false;
									break;
								}
							}
							if (specialPathRequired == true) {
								//System.err.println("if loop: "+externalFilePath);
								// special location required
								writableSecondaryStorageDirectory.add(externalFilePath);
							}
						}
					}
				}	
			}
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return writableSecondaryStorageDirectory;
	}
	
	public static boolean isWritable(File dirToTest) {
		boolean isWriteable = false;
		try {
			File writeTestFile = File.createTempFile("osmand_", ".tmp", dirToTest);
			isWriteable = writeTestFile.exists();
			writeTestFile.delete();
		} catch (IOException e) {
			isWriteable = false;
		}
		return isWriteable;
	}
	
	public boolean setExternalStorageDirectory(String externalStorageDir) {
		return settingsAPI.edit(globalPreferences).putString(EXTERNAL_STORAGE_DIR, externalStorageDir).commit();
	}
	
	public boolean setExternalStorageDirectoryAlias(String externalStorageDirAlias){
		return settingsAPI.edit(globalPreferences).putString(EXTERNAL_STORAGE_DIR_ALIAS, externalStorageDirAlias).commit();
	}
	
	public boolean setExternalStorageDirectoryBeforeChangeStoragePlace(String externalStorageDirBeforeChange){
		return settingsAPI.edit(globalPreferences).putString(EXTERNAL_STORAGE_DIR_BEFORE_CHANGE, externalStorageDirBeforeChange).commit();
	}
	
	public String getBeforeChangeExternalStorageDirectory() {
		String defaultLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
		return settingsAPI.getString(globalPreferences, EXTERNAL_STORAGE_DIR_BEFORE_CHANGE,defaultLocation);
	}
	

	
	// This value is a key for saving last known location shown on the map
	public static final String LAST_KNOWN_MAP_LAT = "last_known_map_lat"; //$NON-NLS-1$
	public static final String LAST_KNOWN_MAP_LON = "last_known_map_lon"; //$NON-NLS-1$
	public static final String LAST_KNOWN_MAP_ZOOM = "last_known_map_zoom"; //$NON-NLS-1$
	
	public static final String PHONE_IMEI_NUMBER="phone_imei_number";

	public static final String MAP_LABEL_TO_SHOW = "map_label_to_show"; //$NON-NLS-1$
	public static final String MAP_LAT_TO_SHOW = "map_lat_to_show"; //$NON-NLS-1$
	public static final String MAP_LON_TO_SHOW = "map_lon_to_show"; //$NON-NLS-1$
	public static final String MAP_ZOOM_TO_SHOW = "map_zoom_to_show"; //$NON-NLS-1$	
	public static final String SHOW_ALL_FAVOURITE_POINTS="show_all_favourite_points";

	public LatLon getLastKnownMapLocation() {
		float lat = settingsAPI.getFloat(globalPreferences,LAST_KNOWN_MAP_LAT, 31);
		float lon = settingsAPI.getFloat(globalPreferences,LAST_KNOWN_MAP_LON, 122);
		return new LatLon(lat, lon);
	}
	
	public boolean isLastKnownMapLocation(){
		return settingsAPI.contains(globalPreferences,LAST_KNOWN_MAP_LAT);
	}

		
	public LatLon getAndClearMapLocationToShow(){
		if(!settingsAPI.contains(globalPreferences,MAP_LAT_TO_SHOW)){
			return null;
		}
		float lat = settingsAPI.getFloat(globalPreferences,MAP_LAT_TO_SHOW, 0);
		float lon = settingsAPI.getFloat(globalPreferences,MAP_LON_TO_SHOW, 0);
		settingsAPI.edit(globalPreferences).remove(MAP_LAT_TO_SHOW).commit();
		return new LatLon(lat, lon);
	}
	
	public String getAndClearMapLabelToShow(){
		String label = settingsAPI.getString(globalPreferences,MAP_LABEL_TO_SHOW, null);
		settingsAPI.edit(globalPreferences).remove(MAP_LABEL_TO_SHOW).commit();
		return label;
	}
	
	private Object objectToShow;
	public Object getAndClearObjectToShow(){
		Object objectToShow = this.objectToShow;
		this.objectToShow = null;
		return objectToShow;
	}
	
	public int getMapZoomToShow() {
		return settingsAPI.getInt(globalPreferences,MAP_ZOOM_TO_SHOW, 8);
	}
	
	public void setShowAllFavouritePointsFlag(boolean flag){
//		SettingsEditor edit = settingsAPI.edit(globalPreferences);
//		edit.putBoolean(SHOW_ALL_FAVOURITE_POINTS, flag);
//		edit.commit();
	}
	
	
	
	public void setMapLocationToShow(double latitude, double longitude, int zoom, String historyDescription,
			String labelToShow, Object toShow) {
//		SettingsEditor edit = settingsAPI.edit(globalPreferences);
//		edit.putFloat(MAP_LAT_TO_SHOW, (float) latitude);
//		edit.putFloat(MAP_LON_TO_SHOW, (float) longitude);
//		
//		if (labelToShow != null) {
//			edit.putString(MAP_LABEL_TO_SHOW, labelToShow);
//		} else {
//			edit.remove(MAP_LABEL_TO_SHOW);
//		}
//		edit.putInt(MAP_ZOOM_TO_SHOW, zoom);
//		edit.commit();
//		objectToShow = toShow;
//		if(historyDescription != null){
//			SearchHistoryHelper.getInstance(ctx).addNewItemToHistory(latitude, longitude, historyDescription);
//		}
	}
	
	public Object getObjectToShow(){
		return objectToShow;
	}
	
	public void setPlanedRouteToShow(int zoom,String labelToShow,Object toShow){
		
	}
	
	public void setMapLocationToShow(double latitude, double longitude, int zoom) {
		setMapLocationToShow(latitude, longitude, zoom,  null, null, null);
	}

	public void setMapLocationToShow(double latitude, double longitude, int zoom, String historyDescription){
		setMapLocationToShow(latitude, longitude, zoom, historyDescription, historyDescription, null);
	}

	// Do not use that method if you want to show point on map. Use setMapLocationToShow
	public void setLastKnownMapLocation(double latitude, double longitude) {
//		SettingsEditor edit = settingsAPI.edit(globalPreferences);
//		edit.putFloat(LAST_KNOWN_MAP_LAT, (float) latitude);
//		edit.putFloat(LAST_KNOWN_MAP_LON, (float) longitude);
//		edit.commit();
	}

	public int getLastKnownMapZoom() {
		return settingsAPI.getInt(globalPreferences,LAST_KNOWN_MAP_ZOOM, 8);
	}

	public void setLastKnownMapZoom(int zoom) {
		settingsAPI.edit(globalPreferences).putInt(LAST_KNOWN_MAP_ZOOM, zoom).commit();
	}

	//
	public void setPhoneIMEINumber(String imei){
		settingsAPI.edit(globalPreferences).putString(PHONE_IMEI_NUMBER, imei).commit();
	}
	
	//获得存储的手机串�?	
	public String getPhoneIMEINumber(){
		return settingsAPI.getString(globalPreferences, PHONE_IMEI_NUMBER, "NULL");
	}
	

	public final static String POINT_NAVIGATE_LAT = "point_navigate_lat"; //$NON-NLS-1$
	public final static String POINT_NAVIGATE_LON = "point_navigate_lon"; //$NON-NLS-1$
	public final static String POINT_NAVIGATE_ROUTE = "point_navigate_route"; //$NON-NLS-1$
	public final static String POINT_NAVIGATE_DESCRIPTION = "point_navigate_description"; //$NON-NLS-1$
	public final static String START_POINT_LAT = "start_point_lat"; //$NON-NLS-1$
	public final static String START_POINT_LON = "start_point_lon"; //$NON-NLS-1$
	public final static String START_POINT_DESCRIPTION = "start_point_description"; //$NON-NLS-1$
	public final static String INTERMEDIATE_POINTS = "intermediate_points"; //$NON-NLS-1$
	public final static String INTERMEDIATE_POINTS_DESCRIPTION = "intermediate_points_description"; //$NON-NLS-1$

	public LatLon getPointToNavigate() {
		float lat = settingsAPI.getFloat(globalPreferences,POINT_NAVIGATE_LAT, 0);
		float lon = settingsAPI.getFloat(globalPreferences,POINT_NAVIGATE_LON, 0);
		if (lat == 0 && lon == 0) {
			return null;
		}
		return new LatLon(lat, lon);
	}
	
	
	public LatLon getPointToStart() {
		float lat = settingsAPI.getFloat(globalPreferences,START_POINT_LAT, 0);
		float lon = settingsAPI.getFloat(globalPreferences,START_POINT_LON, 0);
		if (lat == 0 && lon == 0) {
			return null;
		}
		return new LatLon(lat, lon);
	}
	
	public String getStartPointDescription() {
		return settingsAPI.getString(globalPreferences, START_POINT_DESCRIPTION, "");
	}
	
	public String getPointNavigateDescription() {
		return settingsAPI.getString(globalPreferences, POINT_NAVIGATE_DESCRIPTION, "");
	}
	
	
	public boolean isRouteToPointNavigateAndClear(){
		boolean t = settingsAPI.contains(globalPreferences,POINT_NAVIGATE_ROUTE);
		settingsAPI.edit(globalPreferences).remove(POINT_NAVIGATE_ROUTE).commit();
		return t;
	}
	
	public boolean clearIntermediatePoints() {
		return settingsAPI.edit(globalPreferences).remove(INTERMEDIATE_POINTS).remove(INTERMEDIATE_POINTS_DESCRIPTION).commit();
	}
	
	public List<String> getIntermediatePointDescriptions(int sz) {
		List<String> list = new ArrayList<String>();
		String ip = settingsAPI.getString(globalPreferences,INTERMEDIATE_POINTS_DESCRIPTION, "");
		if (ip.trim().length() > 0) {
			list.addAll(Arrays.asList(ip.split("--")));
		}
		while(list.size() > sz) {
			list.remove(list.size() - 1);
		}
		while(list.size() < sz) {
			list.add("");
		}
		return list;
	}
	
	public List<LatLon> getIntermediatePoints() {
		List<LatLon> list = new ArrayList<LatLon>();
		String ip = settingsAPI.getString(globalPreferences,INTERMEDIATE_POINTS, "");
		if (ip.trim().length() > 0) {
			StringTokenizer tok = new StringTokenizer(ip, ",");
			while (tok.hasMoreTokens()) {
				String lat = tok.nextToken();
				if (!tok.hasMoreTokens()) {
					break;
				}
				String lon = tok.nextToken();
				list.add(new LatLon(Float.parseFloat(lat), Float.parseFloat(lon)));
			}
		}
		return list;
	}
	
	public boolean insertIntermediatePoint(double latitude, double longitude, String historyDescription, int index) {
		List<LatLon> ps = getIntermediatePoints();
		List<String> ds = getIntermediatePointDescriptions(ps.size());
		ps.add(index, new LatLon(latitude, longitude));
		ds.add(index, historyDescription);
//		if (historyDescription != null) {
//			SearchHistoryHelper.getInstance(ctx).addNewItemToHistory(latitude, longitude, historyDescription);
//		}
		return saveIntermediatePoints(ps,ds);
	}
	public boolean deleteIntermediatePoint( int index) {
		List<LatLon> ps = getIntermediatePoints();
		List<String> ds = getIntermediatePointDescriptions(ps.size());
		ps.remove(index);
		ds.remove(index);
		return saveIntermediatePoints(ps,ds);
	}

	public boolean saveIntermediatePoints(List<LatLon> ps, List<String> ds) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<ps.size(); i++) {
			if(i > 0){
				sb.append(",");
			}
			sb.append(((float)ps.get(i).getLatitude()+"")).append(",").append(((float)ps.get(i).getLongitude()+""));
		}
		StringBuilder tb = new StringBuilder();
		for (int i = 0; i < ds.size(); i++) {
			if (i > 0) {
				tb.append("--");
			}
			if (ds.get(i) == null) {
				tb.append("");
			} else {
				tb.append(ds.get(i));
			}
		}
		return settingsAPI.edit(globalPreferences).putString(INTERMEDIATE_POINTS, sb.toString()).
				putString(INTERMEDIATE_POINTS_DESCRIPTION, tb.toString()).
				commit();
	}
	
	public boolean clearPointToNavigate() {
		return settingsAPI.edit(globalPreferences).remove(POINT_NAVIGATE_LAT).remove(POINT_NAVIGATE_LON).
				remove(POINT_NAVIGATE_DESCRIPTION).commit();
	}
	
	public boolean clearPointToStart() {
		return settingsAPI.edit(globalPreferences).remove(START_POINT_LAT).remove(START_POINT_LON).
				remove(START_POINT_DESCRIPTION).commit();
	}
	
	public boolean setPointToNavigate(double latitude, double longitude, String historyDescription) {
		boolean add = settingsAPI.edit(globalPreferences).putFloat(POINT_NAVIGATE_LAT, (float) latitude).putFloat(POINT_NAVIGATE_LON, (float) longitude).commit();
		settingsAPI.edit(globalPreferences).putString(POINT_NAVIGATE_DESCRIPTION, historyDescription).commit();
		if(add){
//			if(historyDescription != null){
//				SearchHistoryHelper.getInstance(ctx).addNewItemToHistory(latitude, longitude, historyDescription);
//			}
		}
		return add;
	}
	
	public boolean setPointToStart(double latitude, double longitude, String description) {
		boolean add = settingsAPI.edit(globalPreferences).putFloat(START_POINT_LAT, (float) latitude).putFloat(START_POINT_LON, (float) longitude).commit();
		if (description == null) {
			description = "";
		}
		settingsAPI.edit(globalPreferences).putString(START_POINT_DESCRIPTION, description).commit();
		return add;
	}
	
	public boolean navigateDialog() {
		return settingsAPI.edit(globalPreferences).putString(POINT_NAVIGATE_ROUTE, "true").commit();
	}


	/**
	 * the location of a parked car
	 */
		
	public static final String LAST_SEARCHED_REGION = "last_searched_region"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_CITY = "last_searched_city"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_CITY_NAME = "last_searched_city_name"; //$NON-NLS-1$
	public static final String lAST_SEARCHED_POSTCODE= "last_searched_postcode"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_STREET = "last_searched_street"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_BUILDING = "last_searched_building"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_INTERSECTED_STREET = "last_searched_intersected_street"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_LAT = "last_searched_lat"; //$NON-NLS-1$
	public static final String LAST_SEARCHED_LON = "last_searched_lon"; //$NON-NLS-1$
	
	public LatLon getLastSearchedPoint(){
		if(settingsAPI.contains(globalPreferences,LAST_SEARCHED_LAT) && settingsAPI.contains(globalPreferences,LAST_SEARCHED_LON)){
			return new LatLon(settingsAPI.getFloat(globalPreferences,LAST_SEARCHED_LAT, 0), 
					settingsAPI.getFloat(globalPreferences,LAST_SEARCHED_LON, 0));
		}
		return null;
	}
	
	public boolean setLastSearchedPoint(LatLon l){
		if(l == null){
			return settingsAPI.edit(globalPreferences).remove(LAST_SEARCHED_LAT).remove(LAST_SEARCHED_LON).commit();
		} else {
			return setLastSearchedPoint(l.getLatitude(), l.getLongitude());
		}
	}
	
	public boolean setLastSearchedPoint(double lat, double lon){
		return settingsAPI.edit(globalPreferences).putFloat(LAST_SEARCHED_LAT, (float) lat).
			putFloat(LAST_SEARCHED_LON, (float) lon).commit();
	}

	public String getLastSearchedRegion() {
		return settingsAPI.getString(globalPreferences, LAST_SEARCHED_REGION, ""); //$NON-NLS-1$
	}

	public boolean setLastSearchedRegion(String region, LatLon l) {
		SettingsEditor edit = settingsAPI.edit(globalPreferences).putString(LAST_SEARCHED_REGION, region).putLong(LAST_SEARCHED_CITY, -1).
			putString(LAST_SEARCHED_CITY_NAME, "").putString(lAST_SEARCHED_POSTCODE, "").
			putString(LAST_SEARCHED_STREET,"").putString(LAST_SEARCHED_BUILDING, ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (settingsAPI.contains(globalPreferences,LAST_SEARCHED_INTERSECTED_STREET)) {
			edit.putString(LAST_SEARCHED_INTERSECTED_STREET, ""); //$NON-NLS-1$
		}
		boolean res = edit.commit();
		setLastSearchedPoint(l);
		return res;
	}
	
	public String getLastSearchedPostcode(){
		return settingsAPI.getString(globalPreferences, lAST_SEARCHED_POSTCODE, null);	
	}
	
	public boolean setLastSearchedPostcode(String postcode, LatLon point){
		SettingsEditor edit = settingsAPI.edit(globalPreferences).putLong(LAST_SEARCHED_CITY, -1).putString(LAST_SEARCHED_STREET, "").putString( //$NON-NLS-1$
				LAST_SEARCHED_BUILDING, "").putString(lAST_SEARCHED_POSTCODE, postcode); //$NON-NLS-1$
		if(settingsAPI.contains(globalPreferences,LAST_SEARCHED_INTERSECTED_STREET)){
			edit.putString(LAST_SEARCHED_INTERSECTED_STREET, ""); //$NON-NLS-1$
		}
		boolean res = edit.commit();
		setLastSearchedPoint(point);
		return res;
	}

	public Long getLastSearchedCity() {
		return settingsAPI.getLong(globalPreferences,LAST_SEARCHED_CITY, -1);
	}
	
	public String getLastSearchedCityName() {
		return settingsAPI.getString(globalPreferences,LAST_SEARCHED_CITY_NAME, "");
	}

	public boolean setLastSearchedCity(Long cityId, String name, LatLon point) {
		SettingsEditor edit = settingsAPI.edit(globalPreferences).putLong(LAST_SEARCHED_CITY, cityId).putString(LAST_SEARCHED_CITY_NAME, name).
				putString(LAST_SEARCHED_STREET, "").putString(LAST_SEARCHED_BUILDING, ""); //$NON-NLS-1$
		edit.remove(lAST_SEARCHED_POSTCODE);
		if(settingsAPI.contains(globalPreferences,LAST_SEARCHED_INTERSECTED_STREET)){
			edit.putString(LAST_SEARCHED_INTERSECTED_STREET, ""); //$NON-NLS-1$
		}
		boolean res = edit.commit();
		setLastSearchedPoint(point);
		return res;
	}

	public String getLastSearchedStreet() {
		return settingsAPI.getString(globalPreferences,LAST_SEARCHED_STREET, ""); //$NON-NLS-1$
	}

	public boolean setLastSearchedStreet(String street, LatLon point) {
		SettingsEditor edit = settingsAPI.edit(globalPreferences).putString(LAST_SEARCHED_STREET, street).putString(LAST_SEARCHED_BUILDING, ""); //$NON-NLS-1$
		if (settingsAPI.contains(globalPreferences,LAST_SEARCHED_INTERSECTED_STREET)) {
			edit.putString(LAST_SEARCHED_INTERSECTED_STREET, ""); //$NON-NLS-1$
		}
		boolean res = edit.commit();
		setLastSearchedPoint(point);
		return res;
	}

	public String getLastSearchedBuilding() {
		return settingsAPI.getString(globalPreferences,LAST_SEARCHED_BUILDING, ""); //$NON-NLS-1$
	}

	public boolean setLastSearchedBuilding(String building, LatLon point) {
		boolean res = settingsAPI.edit(globalPreferences).putString(LAST_SEARCHED_BUILDING, building).remove(LAST_SEARCHED_INTERSECTED_STREET).commit();
		setLastSearchedPoint(point);
		return res;
	}

	public String getLastSearchedIntersectedStreet() {
		if (!settingsAPI.contains(globalPreferences,LAST_SEARCHED_INTERSECTED_STREET)) {
			return null;
		}
		return settingsAPI.getString(globalPreferences,LAST_SEARCHED_INTERSECTED_STREET, ""); //$NON-NLS-1$
	}

	public boolean setLastSearchedIntersectedStreet(String street, LatLon l) {
		setLastSearchedPoint(l);
		return settingsAPI.edit(globalPreferences).putString(LAST_SEARCHED_INTERSECTED_STREET, street).commit();
	}

	public static final String SELECTED_POI_FILTER_FOR_MAP = "selected_poi_filter_for_map"; //$NON-NLS-1$

	public boolean setPoiFilterForMap(String filterId) {
		return settingsAPI.edit(globalPreferences).putString(SELECTED_POI_FILTER_FOR_MAP, filterId).commit();
	}
	
	public String getPoiFilterForMap(){
		return settingsAPI.getString(globalPreferences,SELECTED_POI_FILTER_FOR_MAP, null);
	}

	public static final String VOICE_PROVIDER_NOT_USE = "VOICE_PROVIDER_NOT_USE"; 
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<String> VOICE_PROVIDER = new StringPreference("voice_provider", null).makeProfile();
	
	// this value string is synchronized with settings_pref.xml preference name
	//public final OsmandPreference<Boolean> USE_COMPASS_IN_NAVIGATION = new BooleanPreference("use_compass_navigation", true).makeProfile().cache();
	public final CommonPreference<Boolean> USE_COMPASS_IN_NAVIGATION = new BooleanPreference("use_compass_navigation", true).makeProfile().cache();
	{
		//USE_COMPASS_IN_NAVIGATION.setModeDefaultValue(ApplicationMode.CAR, false);
		//yang chun
	}

	// this value string is synchronized with settings_pref.xml preference name
//	public final CommonPreference<String> RENDERER = new StringPreference("renderer", RendererRegistry.DEFAULT_RENDER) {
//		{
//			makeProfile();
//		}
//		@Override
//		protected boolean setValue(Object prefs, String val) {
//			if(val == null){
//				val = RendererRegistry.DEFAULT_RENDER;
//			}
//			RenderingRulesStorage loaded = ctx.getRendererRegistry().getRenderer(val);
//			if (loaded != null) {
//				super.setValue(prefs, val);
//				return true;
//			}
//			return false;
//		};
//	};
	
	
	Map<String, CommonPreference<String>> customRendersProps = new LinkedHashMap<String, OsmandSettings.CommonPreference<String>>();
	public CommonPreference<String> getCustomRenderProperty(String attrName){
		if(!customRendersProps.containsKey(attrName)){
			customRendersProps.put(attrName, new StringPreference("nrenderer_"+attrName, "").makeProfile());
		}
		return customRendersProps.get(attrName);
	}
	{
		CommonPreference<String> pref = getCustomRenderProperty("appMode");
//		pref.setModeDefaultValue(ApplicationMode.CAR, "car");
//		pref.setModeDefaultValue(ApplicationMode.PEDESTRIAN, "pedestrian");
//		pref.setModeDefaultValue(ApplicationMode.BICYCLE, "bicycle");
		//yang chun
		
//		pref.setModeDefaultValue(ApplicationMode.LANDMAP, "landmap");
//		pref.setModeDefaultValue(ApplicationMode.SATMAP, "satmap");
//		pref.setModeDefaultValue(ApplicationMode.CHARTPLUSSAT, "chartplussat");
//		pref.setModeDefaultValue(ApplicationMode.CHARTPLULANDMAP, "chartpluslandmap");
	}
	
	Map<String, CommonPreference<Boolean>> customBooleanRendersProps = new LinkedHashMap<String, OsmandSettings.CommonPreference<Boolean>>();
	public CommonPreference<Boolean> getCustomRenderBooleanProperty(String attrName){
		if(!customBooleanRendersProps.containsKey(attrName)){
			customBooleanRendersProps.put(attrName, new BooleanPreference("nrenderer_"+attrName, false).makeProfile());
		}
		return customBooleanRendersProps.get(attrName);
	}
	
	Map<String, CommonPreference<String>> customRoutingProps = new LinkedHashMap<String, OsmandSettings.CommonPreference<String>>();
	public CommonPreference<String> getCustomRoutingProperty(String attrName){
		if(!customRoutingProps.containsKey(attrName)){
			customRoutingProps.put(attrName, new StringPreference("prouting_"+attrName, "").makeProfile());
		}
		return customRoutingProps.get(attrName);
	}
	{
//		CommonPreference<String> pref = getCustomRoutingProperty("appMode");
//		pref.setModeDefaultValue(ApplicationMode.CAR, "car");
	}
	
	Map<String, CommonPreference<Boolean>> customBooleanRoutingProps = new LinkedHashMap<String, OsmandSettings.CommonPreference<Boolean>>();
	public CommonPreference<Boolean> getCustomRoutingBooleanProperty(String attrName){
		if(!customBooleanRoutingProps.containsKey(attrName)){
			customBooleanRoutingProps.put(attrName, new BooleanPreference("prouting_"+attrName, false).makeProfile());
		}
		return customBooleanRoutingProps.get(attrName);
	}
	
	public final OsmandPreference<Boolean> VOICE_MUTE = new BooleanPreference("voice_mute", false).makeGlobal();
	
	// for background service
	public final OsmandPreference<Boolean> MAP_ACTIVITY_ENABLED = new BooleanPreference("map_activity_enabled", false).makeGlobal();
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Boolean> SAFE_MODE = new BooleanPreference("safe_mode", false).makeGlobal();
	
	public final OsmandPreference<Boolean> NATIVE_RENDERING_FAILED = new BooleanPreference("native_rendering_failed_init", false).makeGlobal();
	
	
	// this value string is synchronized with settings_pref.xml preference name
	public static final String SERVICE_OFF_ENABLED = "service_off_enabled"; //$NON-NLS-1$
	
	// this value string is synchronized with settings_pref.xml preference name
	public final OsmandPreference<Integer> SERVICE_OFF_INTERVAL = new IntPreference("service_off_interval", 
			1 * 10 * 1000).makeGlobal();
	
	public final CommonPreference<Boolean> SHOW_CURRENT_GPX_TRACK = 
			new BooleanPreference("show_current_gpx_track", true).makeGlobal().cache();
	
	public final OsmandPreference<String> CONTRIBUTION_INSTALL_APP_DATE = new StringPreference("CONTRIBUTION_INSTALL_APP_DATE", null).makeGlobal();
	
	
	public final OsmandPreference<Boolean> FOLLOW_THE_ROUTE = new BooleanPreference("follow_to_route", false).makeGlobal();
	public final OsmandPreference<String> FOLLOW_THE_GPX_ROUTE = new StringPreference("follow_gpx", null).makeGlobal();
	
	public final OsmandPreference<Boolean> SHOW_ARRIVAL_TIME_OTHERWISE_EXPECTED_TIME = 
		new BooleanPreference("show_arrival_time", true).makeGlobal();
	
	
	// UI boxes
	public final CommonPreference<Boolean> TRANSPARENT_MAP_THEME = 
			new BooleanPreference("transparent_map_theme", true).makeProfile();
	{
//		TRANSPARENT_MAP_THEME.setModeDefaultValue(ApplicationMode.CAR, false);
//		TRANSPARENT_MAP_THEME.setModeDefaultValue(ApplicationMode.BICYCLE, false);
//		TRANSPARENT_MAP_THEME.setModeDefaultValue(ApplicationMode.PEDESTRIAN, true);
		//yang chun
//		TRANSPARENT_MAP_THEME.setModeDefaultValue(ApplicationMode.LANDMAP, true);
	}
	
	public static final int OSMAND_DARK_THEME = 0;
	public static final int OSMAND_LIGHT_THEME = 1;
	public static final int OSMAND_LIGHT_DARK_ACTIONBAR_THEME = 2;
	public final CommonPreference<Integer> OSMAND_THEME = 
			new IntPreference("osmand_theme", OSMAND_DARK_THEME).makeGlobal().cache();
	
	public boolean isLightActionBar(){
		return OSMAND_THEME.get() == OSMAND_LIGHT_THEME;
	}
	
	public boolean isLightContentMenu(){
		return OSMAND_THEME.get() != OSMAND_DARK_THEME  || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
	}
	
	public boolean isLightContent(){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			return false;
		}
		return OSMAND_THEME.get() != OSMAND_DARK_THEME ;
	}
	
	
	
	public final CommonPreference<Boolean> FLUORESCENT_OVERLAYS = 
			new BooleanPreference("fluorescent_overlays", false).makeGlobal().cache();

	
	public final CommonPreference<Boolean> SHOW_RULER = 
			new BooleanPreference("show_ruler", true).makeProfile().cache();
	
	

	public final OsmandPreference<Integer> NUMBER_OF_FREE_DOWNLOADS = new IntPreference("free_downloads_v2", 0).makeGlobal();
	
	public boolean checkFreeDownloadsNumberZero(){
		if(!settingsAPI.contains(globalPreferences,NUMBER_OF_FREE_DOWNLOADS.getId())){
			NUMBER_OF_FREE_DOWNLOADS.set(0);
			return true;
		}
		return false;
	}
	
	public enum DayNightMode {
//		AUTO(R.string.daynight_mode_auto),
//		DAY(R.string.daynight_mode_day),
//		NIGHT(R.string.daynight_mode_night),
//		SENSOR(R.string.daynight_mode_sensor);
//
//		private final int key;
//
//		DayNightMode(int key) {
//			this.key = key;
//		}
//
//		public  String toHumanString(Context ctx){
//			return ctx.getString(key);
//		}
//
//		public boolean isSensor() {
//			return this == SENSOR;
//		}
//
//		public boolean isAuto() {
//			return this == AUTO;
//		}
//
//		public boolean isDay() {
//			return this == DAY;
//		}
//
//		public boolean isNight() {
//			return this == NIGHT;
//		}
//
//		public static DayNightMode[] possibleValues(Context context) {
//			SensorManager mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
//		    Sensor mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//			boolean isLightSensorEnabled = mLight != null;
//	         if (isLightSensorEnabled) {
//	        	 return DayNightMode.values();
//	         } else {
//	        	 return new DayNightMode[] { AUTO, DAY, NIGHT };
//	         }
//		}

	}
	
	public enum MetricsConstants {
	    KILOMETERS_AND_METERS(R.string.si_km_m,"km-m"),
		MILES_AND_FOOTS(R.string.si_mi_foots,"mi-f"),
		MILES_AND_YARDS(R.string.si_mi_yard,"mi-y");
		
		private final int key;
		private final String ttsString;

		MetricsConstants(int key, String ttsString) {
			this.key = key;
			this.ttsString = ttsString;
		}
		
		public String toHumanString(Context ctx){
			return ctx.getString(key);
		}
		
		public String toTTSString(){
			return ttsString;
		}
		
	}
	
//	public enum AutoZoomMap {
//		NONE(R.string.auto_zoom_none, 0f),
//		FARTHEST(R.string.auto_zoom_farthest, 1f),
//		FAR(R.string.auto_zoom_far, 1.4f),
//		CLOSE(R.string.auto_zoom_close, 2f)
//		;
//		public final float coefficient;
//		public final int name;
//		AutoZoomMap(int name, float coefficient) {
//			this.name = name;
//			this.coefficient = coefficient;
//
//		}
//	}
	
	/**
	 * Class represents specific for driving region
	 * Signs, leftHandDriving
	 */
//	public enum DrivingRegion {
//
//		EUROPE_ASIA(R.string.driving_region_europe_asia, MetricsConstants.KILOMETERS_AND_METERS, false, false),
//		US(R.string.driving_region_us, MetricsConstants.MILES_AND_FOOTS, false, true),
//		CANADA(R.string.driving_region_canada, MetricsConstants.KILOMETERS_AND_METERS, false, true),
//		UK_AND_OTHERS(R.string.driving_region_uk, MetricsConstants.MILES_AND_FOOTS, true, false),
//		JAPAN(R.string.driving_region_japan, MetricsConstants.KILOMETERS_AND_METERS, true, false)
//		;
//
//		public final boolean leftHandDriving;
//		public final boolean americanSigns;
//		public final MetricsConstants defMetrics;
//		public final int name;
//
//		DrivingRegion(int name, MetricsConstants def, boolean leftHandDriving, boolean americanSigns) {
//			this.name = name;
//			defMetrics = def;
//			this.leftHandDriving = leftHandDriving;
//			this.americanSigns = americanSigns;
//		}
//
//	}

	
}