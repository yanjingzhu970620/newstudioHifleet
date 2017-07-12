/*package com.hifleet.map;

public class IndexConstants {

	// Important : Every time you change schema of db upgrade version!!!
	// If you want that new application support old index : put upgrade code in
	// android app ResourceManager
	
	
	
	public final static int POI_TABLE_VERSION = 1;
	public final static int BINARY_MAP_VERSION = 2; // starts with 1
	public final static int VOICE_VERSION = 0; // supported download versions
	public final static int TTSVOICE_VERSION = 1; // supported download versions

	public static final String POI_INDEX_EXT = ".poi.odb"; //$NON-NLS-1$
	public static final String BINARY_MAP_INDEX_EXT = ".obf"; //$NON-NLS-1$
	public static final String BINARY_SRTM_MAP_INDEX_EXT = ".srtm.obf"; //$NON-NLS-1$
	public static final String BINARY_SRTM_MAP_INDEX_EXT_ZIP = ".srtm.obf.zip"; //$NON-NLS-1$
	public static final String TOUR_INDEX_EXT = ".tour"; //$NON-NLS-1$

	public static final String GEN_LOG_EXT = ".gen.log"; //$NON-NLS-1$

	public static final String POI_INDEX_EXT_ZIP = ".poi.zip"; //$NON-NLS-1$
	public static final String VOICE_INDEX_EXT_ZIP = ".voice.zip"; //$NON-NLS-1$
	public static final String TTSVOICE_INDEX_EXT_ZIP = ".ttsvoice.zip"; //$NON-NLS-1$
	public static final String ANYVOICE_INDEX_EXT_ZIP = "voice.zip"; //$NON-NLS-1$ //to cactch both voices, .voice.zip and .ttsvoice.zip
	public static final String BINARY_MAP_INDEX_EXT_ZIP = ".obf.zip"; //$NON-NLS-1$
	public static final String TOUR_INDEX_EXT_ZIP = ".tour.zip"; //$NON-NLS-1$

	public static final String EXTRA_ZIP_EXT = ".extra.zip";
	public static final String EXTRA_EXT = ".extra";

	public static final String RENDERER_INDEX_EXT = ".render.xml"; //$NON-NLS-1$

	public final static String POI_TABLE = "poi"; //$NON-NLS-1$

	// index_download_domain前面不要有http�?/
	// 测试离线包下载地�?��182.92.229.132/eseanavtest
	// 原版离线包下载地�?��182.92.229.132/GProxy
	// public static final String INDEX_DOWNLOAD_DOMAIN =
	// "182.92.229.132/GProxy";//"182.92.229.132/GProxy";//"www.hifleet.com/GProxy";//"download.osmand.net";//原版
	// 正式版：eseanav
	// 測試版：eseanav_test
	public static final String APP_DIR = "hifleet/";//"manyshipsand/"; //$NON-NLS-1$
	public static final String MAPS_PATH = "";
	public static final String BACKUP_INDEX_DIR = "backup/";
	public static final String GPX_INDEX_DIR = "tracks/";
	public static final String ROUTE_INDEX_DIR = "routes/";
	public static final String TILES_INDEX_DIR = "tiles/";
	public static final String SRTM_INDEX_DIR = "srtm/"; //$NON-NLS-1$
	public static final String AV_INDEX_DIR = "avnotes/"; //$NON-NLS-1$
	public static final String VOICE_INDEX_DIR = "voice/"; //$NON-NLS-1$
	public static final String RENDERERS_DIR = "rendering/"; //$NON-NLS-1$
	public static final String ROUTING_XML_FILE = "routing.xml";

	public static final String SQLITE_EXT = ".sqlitedb"; //$NON-NLS-1$
	public static final String DOWNLOAD_SQLITE_EXT = ".download"; //$NON-NLS-1$
	public static final String TEMP_SOURCE_TO_LOAD = "temp";

	// :8080/cnooc/ 测试
	// public static final String
	// CHECK_NEW_VERSION_URL="http://182.92.229.132/GProxy/EasyNavigationUpgradeVersion.xml";//"http://www.hifleet.com/GProxy/EasyNavigationUpgradeVersion.xml";
	public static final String GET_SEAREAWX_ACTION_URL = "http://202.108.65.45:8080/cnooc/GetSeareaWxAction.do";

	public static final String LOGIN_URL = "http://202.108.65.45:8080/cnooc/UserLogin.do?UserID=";

	public static final String GET_BBOX_SHIPS_URL = "http://202.108.65.45:8080/cnooc/MobileBBoxSearchVessel.do?bbox=";

	public static final String FUZZY_SEARCH_URL = "http://202.108.65.45:8080/cnooc/GetShipSuggest.do?q=";

	public static final String GET_WANNING_URL = "http://202.108.65.45:8080/cnooc/openAlertRsList.do?UserId=";

	public static final String GET_PLOT_URL = "http://202.108.65.45:8080/cnooc/openLayerShow.do?ScreenRange=polygon";

	public static final String GET_CHOOSED_SHIP_URL = "http://202.108.65.45:8080/cnooc/SearchVessel.do?keyword=";

	public static final String GET_MY_TEAM_URL = "http://202.108.65.45:8080/cnooc/GetAllFleetVessels.do?";

	public static final String GET_MY_TEAM_NAME_URL = "http://202.108.65.45:8080/cnooc/ListFleets.do";

	public static final String GET_LAYER_LIST_URL = "http://202.108.65.45:8080/cnooc/openLayerList.do?UserId=";

	public static final String GET_KEEP_HEARTBEAT = "http://202.108.65.45:8080/cnooc/KeepHeartBeatInBackground.do?userid=";

	public static final String GET_WEATHER_STATIONS = "http://202.108.65.45:8080/cnooc/GetWeatherStations.do";

	public static final String GET_VESSEL_TRAJECTORY_DATA = "http://202.108.65.45:8080/cnooc/NewMobileGetVesselTrajectoryData.do?bbox=Polygon";

	public static final String GET_QUERY_MY_FLEET="http://202.108.65.45:8080/cnooc/QueryMyFleet.do?group=";
	
	public static final String GET_BBOX_SEARCH_VESSEL_URL = "http://202.108.65.45:8080/cnooc/MobileBBoxSearchVessel.do?bbox=Polygon((121.826%2031.230,121.826%2030.666,122.196%2030.666,122.196%2031.230,121.826%2031.230))";

}*/
package com.hifleet.map;

public class IndexConstants {
	public static final String INDEX_DOWNLOAD_DOMAIN ="www.hifleet.com/offlinemap";// "182.92.229.132/GProxy";//离线包地址
	public static final String LOCATION = "location";
	public static final String LOCATION_ACTION = "locationAction";
	// Important : Every time you change schema of db upgrade version!!!
	// If you want that new application support old index : put upgrade code in
	// android app ResourceManager
	public final static int POI_TABLE_VERSION = 1;
	public final static int BINARY_MAP_VERSION = 2; // starts with 1
	public final static int VOICE_VERSION = 0; // supported download versions
	public final static int TTSVOICE_VERSION = 1; // supported download versions
	public final static int START_TO_SHOW_SHIPS_ZOOM = 10;
	public final static int START_TO_SHOW_TYPHOON_ZOOM = 1;
	public static final String POI_INDEX_EXT = ".poi.odb"; //$NON-NLS-1$
	public static final String BINARY_MAP_INDEX_EXT = ".obf"; //$NON-NLS-1$
	public static final String BINARY_SRTM_MAP_INDEX_EXT = ".srtm.obf"; //$NON-NLS-1$
	public static final String BINARY_SRTM_MAP_INDEX_EXT_ZIP = ".srtm.obf.zip"; //$NON-NLS-1$
	public static final String TOUR_INDEX_EXT = ".tour"; //$NON-NLS-1$

	public static final String GEN_LOG_EXT = ".gen.log"; //$NON-NLS-1$

	public static final String POI_INDEX_EXT_ZIP = ".poi.zip"; //$NON-NLS-1$
	public static final String VOICE_INDEX_EXT_ZIP = ".voice.zip"; //$NON-NLS-1$
	public static final String TTSVOICE_INDEX_EXT_ZIP = ".ttsvoice.zip"; //$NON-NLS-1$
	public static final String ANYVOICE_INDEX_EXT_ZIP = "voice.zip"; //$NON-NLS-1$ //to cactch both voices, .voice.zip and .ttsvoice.zip
	public static final String BINARY_MAP_INDEX_EXT_ZIP = ".obf.zip"; //$NON-NLS-1$
	public static final String TOUR_INDEX_EXT_ZIP = ".tour.zip"; //$NON-NLS-1$

	public static final String EXTRA_ZIP_EXT = ".extra.zip";
	public static final String EXTRA_EXT = ".extra";

	public static final String RENDERER_INDEX_EXT = ".render.xml"; //$NON-NLS-1$

	public final static String POI_TABLE = "poi"; //$NON-NLS-1$

	// index_download_domain前面不要有http�?/
	// 测试离线包下载地�?��182.92.229.132/eseanavtest
	// 原版离线包下载地�?��182.92.229.132/GProxy
	// public static final String INDEX_DOWNLOAD_DOMAIN =
	// "182.92.229.132/GProxy";//"182.92.229.132/GProxy";//"www.hifleet.com/GProxy";//"download.osmand.net";//原版
	// 正式版：eseanav
	// 測試版：eseanav_test
	public static final String APP_DIR = "hifleet/";//"manyshipsand/"; //$NON-NLS-1$
	public static final String MAPS_PATH = "";
	public static final String BACKUP_INDEX_DIR = "backup/";
	public static final String GPX_INDEX_DIR = "tracks/";
	public static final String ROUTE_INDEX_DIR = "routes/";
	public static final String TILES_INDEX_DIR = "tiles/";
	public static final String SRTM_INDEX_DIR = "srtm/"; //$NON-NLS-1$
	public static final String AV_INDEX_DIR = "avnotes/"; //$NON-NLS-1$
	public static final String VOICE_INDEX_DIR = "voice/"; //$NON-NLS-1$
	public static final String RENDERERS_DIR = "rendering/"; //$NON-NLS-1$
	public static final String ROUTING_XML_FILE = "routing.xml";

	public static final String SQLITE_EXT = ".sqlitedb"; //$NON-NLS-1$
	public static final String DOWNLOAD_SQLITE_EXT = ".download"; //$NON-NLS-1$
	public static final String TEMP_SOURCE_TO_LOAD = "temp";

	// :8080/cnooc/ 测试

	public static final String CHECK_NEW_VERSION_URL = "http://www.hifleet.com/hiFleetUpgradeVersion.xml";// "http://www.hifleet.com/GProxy/EasyNavigationUpgradeVersion.xml";

	public static String outer_net = "http://58.40.126.151";
	public static String inter_net = "http://202.108.65.45";

	public static String GET_WIND_TIMELIST = "http://58.40.126.151:8080/HiFleetBackEndSupport/getwindforcasttimelist.do?time=";
	public static String GET_WAVE_TIMELIST = "http://58.40.126.151:8080/HiFleetBackEndSupport/getwaveforcasttimelist.do?time=";
	public static String GET_PRESSURE_TIMELIST = "http://58.40.126.151:8080/HiFleetBackEndSupport/getpressureforcasttimelist.do?time=";

	public static final String GET_SEAREAWX_ACTION_URL = "getSeareaWx.do?mode=0";
	public static final String GET_WANNING_URL = ":8080/cnooc/openAlertRsList.do?UserId=";
	public static final String GET_PLOT_URL = ":8080/cnooc/openLayerShow.do?ScreenRange=polygon";
	public static final String GET_LAYER_LIST_URL = ":8080/cnooc/openLayerList.do?UserId=";
	public static final String GET_WEATHER_STATIONS = ":8080/cnooc/GetWeatherStations.do";
	public static final String GET_WEATHER_INFO = ":8080/cnooc/GetWeatherInfo.do?stationId=";

	public static final String SAVE_MYFLEET_SHIPS = "mobileSaveMyShip.do?";
	public static final String DELETE_MYFLEET_SHIP = "mobileDelMyShip.do?";
	public static final String GET_VESSEL_TRAJECTORY_DATA = "mobileGetTrajectory.do?bbox=Polygon";
	public static final String GET_QUERY_MY_FLEET = "mobileListMyFleetVessels.do?group=";
	public static final String GET_KEEP_HEARTBEAT = "mobileHeartBeat.do?";
	public static final String LOGIN_URL = "mobileUserLogin.do?email=";
	public static final String GET_CHOOSED_SHIP_URL = "mobileSearchVessel.do?keyword=";
	public static final String MOBILE_SEARCH_VESSEL_FREE = "mobileSearchVesselFree.do?keyword=";
	public static final String GET_MY_TEAM_URL = "mobileGetAllMyFleetVessels.do?";
	public static final String GET_MY_TEAM_NAME_URL = "mobileGetMyFleetsList.do?";
	public static final String GET_MY_TEAM_ALERT_URL = "mobileSelectAlertRsTitleByFleet.do?StartTime=";
	public static final String GET_MY_SHIPS_ALERT_URL = "mobileSelectAlertRsListByMmsi.do?StartTime=";
	public static final String GET_SHIPS_ALERT_MESSAGE_URL = "mobileSelectAlertRsOneByMmsi.do?Mmsi=";
	public static final String SET_TEAM_COLOR_URL = "mobileChangeMyGroupColor.do?";
	public static final String DELET_MYGROUP_URL = "deleteMyGroupByGroupName.do?";
	public static final String GET_BBOX_SHIPS_URL = "mobileBBoxSearch.do?bbox=";
	public static final String GET_WEATHERINFO_URL = "http://58.40.126.151:8080/HiFleetBackEndSupport/getmeteoro.do?";
	public static final String GET_TYPHOON_URL = "getCurrentTyphoon.do?";
	public static final String GET_TYPHOONINFO_URL = "gettyphooninfo.do?xuhao=";
	public static final String GET_HIGHlIGHTED_SHIPS_URL = "mobileGetHighlightedVessels.do?mmsi=";
	public static final String FUZZY_SEARCH_URL = "mobileGetShipSuggest.do?q=";
	public static final String PORT_SEARCH_URL = "mobileGetPortsData.do?tags=";
	public static final String GET_MY_AREA = "mobileListMyMultizones.do";
	public static final String GET_MY_AREA_ALERT = "mobileSelectAlertRsTitleByAlertTypeAndAreaId.do?StartTime=";
	public static final String GET_MY_AREA_ALERT_MESSAGE_URL = "mobileSelectAlertRsListByAlertTypeAndAreaId.do?AreaId=";
	public static final String GET_MY_AREA_ALERT_ETA_MESSAGE_URL = "mobileSelecETAShipsList.do?";
	public static final String GET_MY_AREASHIPS = "getAreaShipsPolygonAction.do?dwt=";
	public static final String INPORT_SHIPS_URL = "getInPortShips.do?dwt=";
	public static final String WILLARRIVEPORT_SHIPS_URL = "getWillArrivePortShips.do?dwt=";
	public static final String ARRIVEPORT_SHIPS_URL = "getArrivePortShips.do?dwt=";
	public static final String LINE_SHIPS_URL = "getLineOperatingShip.do?dwt=";
	public static final String GET_USER_LOGOUT = "mobileUserLogout.do";
	public static final String GET_SHIPS_DETAIL_XML = "getShipsDetailXmlNew.do?keyword=";
	public static final String GET_SHIPS_DETAIL_XML_FREE = "getShipsDetailXmlNewFree.do?mmsi=";
	public static final String GET_NOTE_CODE = "getnotecode.do?phone=";
	public static final String CREATE_MOBILE_USER = "createmobileuser.do?phone=";
	public static final String GET_LOGIN_CODE = "getlogincode.do?phone=";
	public static final String MOBILE_USER_LOGIN = "mobileUserLogin.do?code=";
	public static final String UPLOAD_DATA_ACTION = "uploadDataAction.do?mmsi=";
	public static final String GET_WXUSERINFO = "mobileloginByWeChat.do?code=";
}
