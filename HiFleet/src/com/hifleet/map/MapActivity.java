package com.hifleet.map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.SearchActivity;
import com.hifleet.bean.loginSession;
import com.hifleet.fragment.AreaFragment;
import com.hifleet.fragment.DetailsFragment;
import com.hifleet.fragment.PortShipsFragment;
import com.hifleet.fragment.SettingFragment;
import com.hifleet.fragment.TeamFragment;
import com.hifleet.map.MapTileDownloader.DownloadRequest;
import com.hifleet.map.MapTileDownloader.IMapDownloaderCallback;
import com.hifleet.plus.R;
import com.hifleet.thread.UserLogout;
import com.hifleet.update.Update;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MapActivity extends AccessibleActivity implements
		UncaughtExceptionHandler {
	// �?��比例尺设�?
	private static final int SHOW_POSITION_MSG_ID = OsmAndConstants.UI_HANDLER_MAP_VIEW + 1;
	private static final int LONG_KEYPRESS_MSG_ID = OsmAndConstants.UI_HANDLER_MAP_VIEW + 2;
	private static final int LONG_KEYPRESS_DELAY = 500;
	private static final String FIRST_TIME_APP_RUN = "FIRST_TIME_APP_RUN"; //$NON-NLS-1$
	private static final String VERSION_INSTALLED = "VERSION_INSTALLED"; //$NON-NLS-1$

	/** Called when the activity is first created. */
	private OsmandMapTileView mapView;

	// private MapActivityActions mapActions;
	private MapActivityLayers mapLayers;

	private static MapViewTrackingUtilities mapViewTrackingUtilities;

	// Notification status
	private NotificationManager mNotificationManager;
	private int APP_NOTIFICATION_ID = 1;

	// handler to show/hide trackball position and to link map with delay
	private Handler uiHandler = new Handler();
	// App variables
	private static OsmandApplication app;
	public static String mapUrl;
	public static int mapKey = 3;
	private OsmandSettings settings;

	private Dialog progressDlg = null;

	private ProgressDialog startProgressDialog;

	private FrameLayout lockView, frameMap;

	private RelativeLayout relative;

	private static MapActivity activity;

	private static Boolean isTimeOut = true;

	private ImageButton img_zoom_in_btn, img_zoom_out_btn, img_location,
			img_change_map, img_sea_map, img_all_sea_map, img_land_map,
			img_air, img_wind, img_wave, img_ocean;
	private SeekBar seekBar;
	private TextView seekweathertime;
	String localTime;

	private View mViewChageMap;

	private LinearLayout mLinearLayoutChangeMap;

	private static RadioButton mRadioDetails, mRaidoShips;

	public static Date weathertime = new Date(getUtctime());
	public static Date nowtime = new Date(getUtctime());
	public static String weatherf = "000";

	// public int cday;
	public void onNewIntent() {
		print("MapActivity onNew Intent.");
	}

	private Notification getNotification() {
		Intent notificationIndent = new Intent(this, getMyApplication()
				.getAppCustomization().getMapActivity());
		notificationIndent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Notification notification = new Notification(R.drawable.alert_icon, "", //$NON-NLS-1$
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// notification.setLatestEventInfo(this, Version.getAppName(app),
		// getString(R.string.go_back_to_osmand), PendingIntent
		// .getActivity(this, 0, notificationIndent,
		// PendingIntent.FLAG_UPDATE_CURRENT));
		return notification;
	}

	public static void updateMapLayerImageState(boolean flag) {

	}

	// 在开始阶段提示是否有新版本可供更新下载安装
	private void checkNewVersionAndUpgradeOnInit() {
		String sdcardpath = Environment.getExternalStorageDirectory() + "/";
		String mSavePath = sdcardpath + "hifleet/";
		System.out.println("checkNewVersionpath::" + mSavePath);
		Update updateManager = new Update(this,
				IndexConstants.CHECK_NEW_VERSION_URL, mSavePath);
		updateManager.checkUpdateWithoutNotification();
	}

	private DetailsFragment mDetailsFragment;
	private SettingFragment mSettingFragment;
	private TeamFragment mTeamFragment;
	private PortShipsFragment portshipsFragment;
	private AreaFragment areaFragment;
	public static Boolean isShipDetails = false;
	public static Boolean isTeamShipMove = false;
	Boolean isShowWave, isShowWind, isShowAirPressure, isShowOcean;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		StatService.setDebugOn(true);
		stat();
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Thread.setDefaultUncaughtExceptionHandler(this);
		weathertime.setDate(weathertime.getDate() - 1);// 传入请求的日期。
		// System.err.println("weathertime 初始化" + weathertime);

		app = getMyApplication();
		settings = app.getSettings();

		// boolean uninstall = false;

		if (isAPKInstalled()) {
			int vcode = getOldAppVersionCode();
			System.out.println("老版本安装了。version code: " + vcode);
			if (vcode != -1 && vcode < 6) {
				// uninstall = true;
				// 提示用户要卸载老版本
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle(R.string.uninstall_old_version_dialog_title);
				builder.setMessage(R.string.find_old_version_app_installed);
				builder.setPositiveButton(
						R.string.uninstall_old_version_button,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 通过程序的包名创建URI
								String oldpackageuri = "package:"
										+ oldPackageName;
								Uri packageURI = Uri.parse(oldpackageuri);
								// 创建Intent意图
								Intent intent = new Intent(
										Intent.ACTION_DELETE, packageURI);
								// 执行卸载程序
								startActivity(intent);
								finish();
							}
						});

				builder.setNegativeButton(R.string.default_buttons_cancel,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});
				Dialog noticeDialog = builder.create();
				noticeDialog.show();

			}
		}

		checkNewVersionAndUpgradeOnInit();

		// if(!uninstall){

		app.applyTheme(this);

		app.setMapActivity(this);
		// app.setIslogin(app.myPreferences.getBoolean("IsLogin", false));
		app.setLableadd(app.myPreferences.getBoolean("isShowMyTeamName", true));
		// super.onCreate(savedInstanceState);
		mapLayers = new MapActivityLayers(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		mapView = (OsmandMapTileView) findViewById(R.id.MapView);
		activity = this;

		waveforcasttime = new ArrayList<String>();
		weatherforcasttime = new ArrayList<String>();
		LodingWavetimeListThread lwa = new LodingWavetimeListThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			print("启动 LodingWavetimeListThread");
			lwa.executeOnExecutor(Executors.newCachedThreadPool(),
					new String[0]);
		} else {
			lwa.execute();
		}// 加载海浪图预报时间
		if (app.isIslogin()) {
			if (!app.getMyrole().equals("vvip")
					|| app.getLoginbean().getGchart().equals("1")
					|| app.getLoginbean().getGchartupdate().equals("1")) {
				mapKey = 3;
			} else {
				mapKey = 2;
			}
		} else {
			mapKey = 2;
		}

		final OsmAndAppCustomization appCustomization = getMyApplication()
				.getAppCustomization();

		// mapActions = new MapActivityActions(this);
		// print("启动 mapView.setLatLon");

		if (mapViewTrackingUtilities == null) {
			mapViewTrackingUtilities = new MapViewTrackingUtilities(app);
		}
		mapViewTrackingUtilities.setMapView(mapView);

		app.getResourceManager().getMapTileDownloader()
				.addDownloaderCallback(new IMapDownloaderCallback() {
					@Override
					public void tileDownloaded(DownloadRequest request) {
						// System.out.println("获得通知。");
						if (request != null && !request.error
								&& request.fileToSave != null) {

							// print("Map Activity  map tile download.");
							ResourceManager mgr = app.getResourceManager();
							mgr.tileDownloaded(request);
						}
						if (request == null || !request.error) {
							mapView.tileDownloaded(request);
						}
					}
				});
		// print("启动 createLayerscreateLayers");
		mapLayers.createLayers(mapView, 0);

		if (!settings.isLastKnownMapLocation()) {
			Location location = app.getLocationProvider()
					.getFirstTimeRunDefaultLocation();
			if (location != null) {
				// System.err.println("初始位置不为null！");
				mapView.setLatLon(location.getLatitude(),
						location.getLongitude());
				mapView.setIntZoom(2);// 开始比例尺
			} else {
				// System.err.println("初始位置为null！设到默认位置上。");
				location = app.getLocationProvider().getInitLocation();
				mapView.setLatLon(location.getLatitude(),
						location.getLongitude());
				mapView.setIntZoom(2);// 开始比例尺
			}
		} else {
			// System.err.println("settings.isLastKnownMapLocation() null!!!!");
			// Location location = app.getLocationProvider().getInitLocation();
			LatLon location = settings.getLastKnownMapLocation();
			mapView.setLatLon(location.getLatitude(), location.getLongitude());
			mapView.setIntZoom(2);// 开始比例尺
		}
		if (lockView != null) {
			((FrameLayout) mapView.getParent()).addView(lockView);
		}
		relative = (RelativeLayout) findViewById(R.id.relative);
		img_zoom_in_btn = (ImageButton) findViewById(R.id.mapActivityZoomInImageButton);
		img_zoom_out_btn = (ImageButton) findViewById(R.id.mapActivityZoomOutImageButton);
		img_location = (ImageButton) findViewById(R.id.locateMyLocationImageButton);
		mRadioDetails = (RadioButton) findViewById(R.id.radio_details);
		mRaidoShips = (RadioButton) findViewById(R.id.radio_ships);
		mLinearLayoutChangeMap = (LinearLayout) findViewById(R.id.ll_change_map);
		mViewChageMap = (View) findViewById(R.id.view_black);
		img_change_map = (ImageButton) findViewById(R.id.img_change_map);
		img_sea_map = (ImageButton) findViewById(R.id.img_sea_map);
		img_all_sea_map = (ImageButton) findViewById(R.id.img_all_sea_map);
		img_land_map = (ImageButton) findViewById(R.id.img_land_map);
		img_air = (ImageButton) findViewById(R.id.img_air_pressure_show);
		img_wind = (ImageButton) findViewById(R.id.img_wind_show);
		img_wave = (ImageButton) findViewById(R.id.img_wave_show);
		img_ocean = (ImageButton) findViewById(R.id.img_ocean_show);
		seekweathertime = (TextView) findViewById(R.id.description);
		if (weatherf != null) {
			Date da = new Date(getUtctime());
			localTime = utc2Localtime(da, (nowtime.getDate() - 1),
					Integer.valueOf(weatherf), 0, 0, "yyyy-MM-dd HH:mm:ss");
			seekweathertime.setText("气象时间：" + localTime);
		}
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			/**
			 * 拖动条停止拖动的时候调用
			 */
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				// System.err.println("onStopTrackingTouchonStopTrackingTouch");
				ITileSource newwaveSource = settings
						.getWaveMapTileSource(false);
				mapLayers.waveMapTileLayer.setMap(newwaveSource);
				ITileSource newwindSource = settings
						.getWindMapTileSource(false);
				mapLayers.windMapTileLayer.setMap(newwindSource);
				ITileSource newweatherSource = settings
						.getWeatherMapTileSource(false);
				mapLayers.weatherMapTileLayer.setMap(newweatherSource);
				ITileSource newoceanSource = settings.getOceanMapTileSource(false);
				mapLayers.oceanmaptilelayer.setMap(newoceanSource);
				if (mapView.getZoom() <= 7) {
					mapView.refreshMapForceDraw(false, true);
				}

				seekweathertime.setText("气象时间：" + localTime);
			}

			/**
			 * 拖动条开始拖动的时候调用
			 */
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// seekweathertime.setText("开始拖动");
				// System.err.println("onStartTrackingTouchonStartTrackingTouch");
				String path = app.getResourceManager().getDirWithTiles()
						.getAbsolutePath();
				deleteDirectory(path + "/气压图");
				deleteDirectory(path + "/海浪图");
				deleteDirectory(path + "/风力图");
				deleteDirectory(path + "/洋流图");
				mapView.getApplication().getResourceManager().clearAllTiles();
				// 清空已有的天气图片。
			}

			/**
			 * 拖动条进度改变的时候调用
			 */
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// System.err.println("onProgressChangedonProgressChanged"+fromUser);
				if (weatherforcasttime.size() > 0 && fromUser) {
					int i = weatherforcasttime.size();
					p = (int) ((i - 1) * progress / 100);
					Date da = new Date(getUtctime());
					localTime = utc2Localtime(da, (nowtime.getDate() - 1),
							Integer.valueOf(weatherforcasttime.get(p)), 0, 0,
							"yyyy-MM-dd HH:mm:ss");
					weatherf = weatherforcasttime.get(p);
					seekweathertime.setText("气象时间：" + localTime);
				}
			}
		});

		isShowWave = app.myPreferences.getBoolean("IsShowWave", false);
		isShowWind = app.myPreferences.getBoolean("IsShowWind", false);
		isShowAirPressure = app.myPreferences.getBoolean("IsShowAirPressure",
				false);
		isShowOcean = app.myPreferences.getBoolean("IsShowOcean", false);
		if (!app.isIslogin()) {
			img_land_map.setBackgroundResource(R.drawable.btn_land_map_select);
		} else {
			if (mapKey == 1) {
				img_sea_map
						.setBackgroundResource(R.drawable.btn_sea_map_select);
			} else if (mapKey == 2) {
				img_land_map
						.setBackgroundResource(R.drawable.btn_land_map_select);
			} else if (mapKey == 3) {
				img_all_sea_map
						.setBackgroundResource(R.drawable.btn_all_sea_map_select);
			}
			if (isShowWave) {
				img_wave.setBackgroundResource(R.drawable.btn_wave_select);
			}
			if (isShowWind) {
				img_wind.setBackgroundResource(R.drawable.btn_wind_select);
			}
			if (isShowAirPressure) {
				img_air.setBackgroundResource(R.drawable.btn_pressure_select);
			}
			if (isShowOcean) {
				img_ocean.setBackgroundResource(R.drawable.btn_ocean_select);
			}
		}
		// 放大按钮触发事件
		img_zoom_in_btn.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				zoomInMapView();
			}
		});

		img_zoom_out_btn.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				zoomOutMapView();
			}
		});
		app.getInstance().addActivity(this);
		// }
	}

	private void stat() {
		// 打开调试开关，正式版本请关闭，以免影响性能
//		StatService.setDebugOn(true);

//		// 设置APP_KEY，APP_KEY是从mtj官网获取，建议通过manifest.xml配置
//		StatService.setAppKey("d6d7dad2aa");
//		// 设置渠道，建议通过manifest.xml配置
//		StatService.setAppChannel(this, "Baidu Market", true);
		// 打开异常收集开关，默认收集java层异常，如果有嵌入SDK提供的so库，则可以收集native crash异常
		StatService.setOn(this, StatService.EXCEPTION_LOG);

		// 如果没有页面和事件埋点，此代码必须设置，否则无法完成接入
		// 设置发送策略，建议使用 APP_START
		// 发送策略，取值 取值 APP_START、SET_TIME_INTERVAL、ONCE_A_DAY
		// 备注，SET_TIME_INTERVAL与ONCE_A_DAY，如果APP退出或者进程死亡，则不会发送
		// 建议此代码不要在Application中设置，否则可能因为进程重启等造成启动次数高，具体见web端sdk帮助中心
		StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
	}
	private String oldPackageName = null;

	private boolean isAPKInstalled() {
		PackageInfo packageInfo1 = null;
		try {
			packageInfo1 = getPackageManager().getPackageInfo("com.hifleet", 0);
			oldPackageName = "com.hifleet";
		} catch (NameNotFoundException e) {
			packageInfo1 = null;
		}

		if (packageInfo1 == null) {
			return false;
		} else {
			return true;
		}
	}

	private String getOldVersionPackageName() {
		return oldPackageName;

	}

	private int getOldAppVersionCode() {
		int versionCode = -1;
		try {
			versionCode = getPackageManager().getPackageInfo("com.hifleet", 0).versionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return versionCode;
	}

	class LodingWindtimeListThread extends AsyncTask<String, Void, Void> {
		// long s;
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd");
				String t = sfd.format(weathertime);
				// SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd");
				// s=System.currentTimeMillis();
				// System.err.println("test time mapactivity"+sfd1.format(System.currentTimeMillis())+"sdf"+t);
				String httpPost = IndexConstants.GET_WIND_TIMELIST + t;
				System.out.println(httpPost + "time:" + t);
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				// System.out.println("抵港船舶  请求完成  花费时间：：："+(System.currentTimeMillis()-s));
				parseXMLnew(inStream, windforcasttime);
				System.err.println("windforcasttime" + windforcasttime.size());
				inStream.close();

				if (pressureforcasttime.size() > 0
						&& waveforcasttime.size() > 0
						&& weatherforcasttime.size() == 0) {
					windforcasttime.retainAll(pressureforcasttime);
					windforcasttime.retainAll(waveforcasttime);
					weatherforcasttime.addAll(windforcasttime);
					for (int i = 0; i < windforcasttime.size(); i++) {
						if (Integer.parseInt(windforcasttime.get(i)) % 2 == 1) {
							System.err.println("还有残余？？？");
						}
						Date da = new Date(getUtctime());
						// System.out.println("new da::"+da+"+++++windtime::"+weathertime);
						da.setDate(nowtime.getDate() - 1);
						da.setHours(Integer.parseInt(windforcasttime.get(i)));
						da.setMinutes(0);// 得到此次循环的预报时间
						// System.err.println("new da after setdate::"+da+"+++++windtime::"+weathertime);

						if (nowtime.after(da)) {
							System.err.println("当前时间在da1之后，继续循环"
									+ windforcasttime.get(i) + "+++da：：" + da);
							continue;
						} else {
							Date da1 = new Date(getUtctime());
							// System.err.println("new da1::"+da1+"windtime::"+windtime);
							da1.setDate(nowtime.getDate() - 1);
							da1.setHours(Integer.parseInt(windforcasttime
									.get(i - 1)) + 2);
							da1.setMinutes(0);// 得到2小时的分界点
							if (nowtime.before(da1)) {
								weatherf = windforcasttime.get(i - 1);
								// windtime.setHours(Integer.parseInt(windforcasttime.get(i-1)));
								for (int r = 0; r < i - 1; r++) {
									weatherforcasttime.remove(r);
								}
								// System.err.println("windf取值i-1："+windforcasttime.get(i-1)+"d:"+weathertime.getDate()+weathertime.getHours()+"da1:"+da1);
								break;
							} else {
								weatherf = windforcasttime.get(i);
								// windtime.setHours(Integer.parseInt(windforcasttime.get(i)));
								for (int r = 0; r < i; r++) {
									weatherforcasttime.remove(r);
								}
								// System.err.println("windf取值i："+windforcasttime.get(i)+"d:"+weathertime.getDate()+weathertime.getHours()+"da1:"+da1);
								break;
							}
						}

					}
				}
				// System.out.println("抵港船舶  解析完成  花费时间：：："+(System.currentTimeMillis()-s));
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// System.err.println("utc ffffffffffff::"+"utcday-1:::"+cday);
			if (weatherforcasttime.size() > 0) {
				ITileSource newwaveSource = settings
						.getWaveMapTileSource(false);
				mapLayers.waveMapTileLayer.setMap(newwaveSource);
				ITileSource newwindSource = settings
						.getWindMapTileSource(false);
				mapLayers.windMapTileLayer.setMap(newwindSource);
				ITileSource newweatherSource = settings
						.getWeatherMapTileSource(false);
				mapLayers.weatherMapTileLayer.setMap(newweatherSource);
				ITileSource newoceanSource = settings.getOceanMapTileSource(false);
				mapLayers.oceanmaptilelayer.setMap(newoceanSource);
			}

		}
	}

	class LodingWavetimeListThread extends AsyncTask<String, Void, Void> {
		// long s;
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd");
				String t = sfd.format(weathertime);
				// SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd");
				// s=System.currentTimeMillis();
				// System.err.println("test time mapactivity"+sfd1.format(System.currentTimeMillis())+"sdf"+t);
				String httpPost = IndexConstants.GET_WAVE_TIMELIST + t;
				System.out.println(httpPost + "time" + t);
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				// System.out.println("抵港船舶  请求完成  花费时间：：："+(System.currentTimeMillis()-s));
				parseXMLnew(inStream, waveforcasttime);
				System.err.println("waveforcasttime" + waveforcasttime.size());
				inStream.close();

				// if(pressureforcasttime.size()>0&&windforcasttime.size()>0&&weatherforcasttime.size()==0){
				// waveforcasttime.retainAll(pressureforcasttime);
				// waveforcasttime.retainAll(windforcasttime);
				weatherforcasttime.addAll(waveforcasttime);
				for (int i = 0; i < waveforcasttime.size(); i++) {
					if (Integer.parseInt(waveforcasttime.get(i)) % 2 == 1) {
						System.err.println("还有残余？？？");
					}
					Date da = new Date(getUtctime());
					// System.out.println("new wave da::" + da +
					// "+++++windtime::"
					// + weathertime);
					da.setDate(nowtime.getDate() - 1);
					da.setHours(Integer.parseInt(waveforcasttime.get(i)));
					da.setMinutes(0);// 得到此次循环的预报时间
					// System.err.println("new wave  da after setdate::"+da+"+++++windtime::"+weathertime);

					if (nowtime.after(da)) {
						// System.err.println("wave 当前时间在da1之后，继续循环"+waveforcasttime.get(i)+"+++da：："+da);
						continue;
					} else {
						Date da1 = new Date(getUtctime());
						// System.err.println("new da1::"+da1+"windtime::"+windtime);
						da1.setDate(nowtime.getDate() - 1);
						da1.setHours(Integer.parseInt(waveforcasttime
								.get(i - 1)) + 2);
						da1.setMinutes(0);// 得到2小时的分界点
						System.out
								.println("da1:" + da1 + " nowtime:" + nowtime);
						if (nowtime.before(da1)) {
							weatherf = waveforcasttime.get(i - 1);
							// windtime.setHours(Integer.parseInt(windforcasttime.get(i-1)));
							for (int r = 0; r < i - 1; r++) {
								weatherforcasttime.remove(0);
								// System.out.println("yichu:::"+r+" i_1::"+i);
							}
							// System.err.println("wavef取值i-1："+waveforcasttime.get(i-1)+"d:"+weathertime.getDate()+weathertime.getHours()+"da1:"+da1);
							break;
						} else {
							weatherf = waveforcasttime.get(i);
							// windtime.setHours(Integer.parseInt(windforcasttime.get(i)));
							for (int r = 0; r < i; r++) {
								weatherforcasttime.remove(0);
								// System.out.println("yichu:::"+r+" i::"+i);
							}
							// System.err.println("wavef取值i："+waveforcasttime.get(i)+"d:"+weathertime.getDate()+weathertime.getHours()+"da1:"+da1);
							break;
						}
					}
				}
				// }
				// System.out.println("抵港船舶  解析完成  花费时间：：："+(System.currentTimeMillis()-s));
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (weatherf != null) {
				Date da = new Date(getUtctime());
				localTime = utc2Localtime(da, (nowtime.getDate() - 1),
						Integer.valueOf(weatherf), 0, 0, "yyyy-MM-dd HH:mm:ss");
				seekweathertime.setText("气象时间：" + localTime);
			}
			if (weatherforcasttime.size() > 0) {
				ITileSource newwaveSource = settings
						.getWaveMapTileSource(false);
				mapLayers.waveMapTileLayer.setMap(newwaveSource);
				ITileSource newwindSource = settings
						.getWindMapTileSource(false);
				mapLayers.windMapTileLayer.setMap(newwindSource);
				ITileSource newweatherSource = settings
						.getWeatherMapTileSource(false);
				mapLayers.weatherMapTileLayer.setMap(newweatherSource);
				ITileSource newoceanSource = settings.getOceanMapTileSource(false);
				mapLayers.oceanmaptilelayer.setMap(newoceanSource);
			}
		}
	}

	class LodingpressuretimeListThread extends AsyncTask<String, Void, Void> {
		// long s;
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd");
				String t = sfd.format(weathertime);
				// SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd");
				// s=System.currentTimeMillis();
				// System.err.println("test time mapactivity"+sfd1.format(System.currentTimeMillis())+"sdf"+t);
				String httpPost = IndexConstants.GET_PRESSURE_TIMELIST + t;
				System.out.println(httpPost + "time" + t);
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				// System.out.println("抵港船舶  请求完成  花费时间：：："+(System.currentTimeMillis()-s));
				parseXMLnew(inStream, pressureforcasttime);
				System.err.println("pressureforcasttime"
						+ pressureforcasttime.size());
				inStream.close();

				if (windforcasttime.size() > 0 && waveforcasttime.size() > 0
						&& weatherforcasttime.size() == 0) {
					pressureforcasttime.retainAll(windforcasttime);
					pressureforcasttime.retainAll(waveforcasttime);
					weatherforcasttime.addAll(pressureforcasttime);
					for (int i = 0; i < pressureforcasttime.size(); i++) {
						if (Integer.parseInt(pressureforcasttime.get(i)) % 2 == 1) {
							System.err.println("还有残余？？？");
						}
						Date da = new Date(getUtctime());
						// System.out.println("new pressure da::"+da+"+++++windtime::"+weathertime);
						da.setDate(nowtime.getDate() - 1);
						da.setHours(Integer.parseInt(pressureforcasttime.get(i)));
						da.setMinutes(0);// 得到此次循环的预报时间
						// System.err.println("new pressure da after setdate::"+da+"+++++windtime::"+weathertime);

						if (nowtime.after(da)) {
							// System.err.println(" pressure 当前时间在da1之后，继续循环"+pressureforcasttime.get(i)+"+++da：："+da);
							continue;
						} else {
							Date da1 = new Date(getUtctime());
							// System.err.println("new da1::"+da1+"windtime::"+windtime);
							da1.setDate(nowtime.getDate() - 1);
							da1.setHours(Integer.parseInt(pressureforcasttime
									.get(i - 1)) + 2);
							da1.setMinutes(0);// 得到2小时的分界点
							if (nowtime.before(da1)) {
								weatherf = pressureforcasttime.get(i - 1);
								// windtime.setHours(Integer.parseInt(windforcasttime.get(i-1)));

								for (int r = 0; r < i - 1; r++) {
									weatherforcasttime.remove(r);
								}
								// System.err.println("pf取值i-1："+pressureforcasttime.get(i-1)+"d:"+weathertime.getDate()+weathertime.getHours()+"da1:"+da1);
								break;
							} else {
								weatherf = pressureforcasttime.get(i);
								// windtime.setHours(Integer.parseInt(windforcasttime.get(i)));
								for (int r = 0; r < i; r++) {
									weatherforcasttime.remove(r);
								}
								// System.err.println("pf取值i："+pressureforcasttime.get(i)+"d:"+weathertime.getDate()+weathertime.getHours()+"da1:"+da1);
								break;
							}
						}
					}
				}
				// System.out.println("抵港船舶  解析完成  花费时间：：："+(System.currentTimeMillis()-s));
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (weatherforcasttime.size() > 0) {
				ITileSource newwaveSource = settings
						.getWaveMapTileSource(false);
				mapLayers.waveMapTileLayer.setMap(newwaveSource);
				ITileSource newwindSource = settings
						.getWindMapTileSource(false);
				mapLayers.windMapTileLayer.setMap(newwindSource);
				ITileSource newweatherSource = settings
						.getWeatherMapTileSource(false);
				mapLayers.weatherMapTileLayer.setMap(newweatherSource);
				ITileSource newoceanSource = settings.getOceanMapTileSource(false);
				mapLayers.oceanmaptilelayer.setMap(newoceanSource);
			}
		}
	}

	public static long getUtctime() {
		// SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// System.out.println("foo:"+foo.format(new Date()));

		// Calendar gc = GregorianCalendar.getInstance();
		// System.out.println("gc.getTime():"+gc.getTime());
		// System.out.println("gc.getTimeInMillis():"+new
		// Date(gc.getTimeInMillis()));

		// 当前系统默认时区的时间：
		Calendar calendar = new GregorianCalendar();
		// System.out.println("时区："+calendar.getTimeZone().getID()+"  ");
		// System.err.println("时间："+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
		TimeZone tz = TimeZone.getDefault();
		// 时区转换
		calendar.setTimeZone(tz);
		// System.err.println("时区："+calendar.getTimeZone().getID()+"  ");
		// System.err.println("当地时间："+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
		// Date time = new Date();

		// 1、取得本地时间：
		java.util.Calendar cal = java.util.Calendar.getInstance();

		// 2、取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		// 之后调用cal.get(int x)或cal.getTimeInMillis()方法所取得的时间即是UTC标准时间。
		// System.out.println("UTC:"+new Date(cal.getTimeInMillis()));
		return (new Date(cal.getTimeInMillis()).getTime());
	}// 自己添加

	private String utc2Localtime(Date da, int date, int hours, int minute,
			int seconds, String localFormate) {
		SimpleDateFormat UtcFormater = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		da.setDate(date);
		da.setHours(hours);
		da.setMinutes(minute);
		da.setSeconds(seconds);
		String utct = UtcFormater.format(da);// 得到对应时间点的时间 utcString
		// System.out.println("utc   before"+utct);

		SimpleDateFormat UtcFormater1 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		UtcFormater1.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date utcd = null;
		try {
			utcd = UtcFormater1.parse(utct);// 以utc时区解析成date格式。

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("utcd   before1"+UtcFormater1.format(utcd)+"da1111:"+Integer.parseInt(weatherforcasttime
		// .get(p)));
		// System.out.println("da1   before1"+UtcFormater.format(da1)+"weather:"+Integer.parseInt(weatherforcasttime
		// .get(p)));
		// System.out.println("Date utcd"+utcd);
		SimpleDateFormat localFormater = new SimpleDateFormat(localFormate);
		localFormater.setTimeZone(TimeZone.getDefault());
		// System.out.println("da1   after"+localFormater.format(utcd.getTime()));
		return localFormater.format(utcd);// 转化成本地时间的string。
	}// 自己添加

	private List<String> windforcasttime;
	private List<String> waveforcasttime;
	private List<String> pressureforcasttime;
	private List<String> weatherforcasttime;
	private int p = 0;

	private void parseXMLnew(InputStream inStream, List<String> list)
			throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		if (root.getNodeName().compareTo("forcasttimelist") == 0) {
			// System.err.println("mapactivity time"+windforcasttime.size());
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = (Node) childNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) node;
					if (childElement.getNodeName().equals("forcasttime")) {
						if (Integer.parseInt(childElement.getAttribute("time")) % 2 == 0) {
							list.add(childElement.getAttribute("time"));
							// System.err.println("mapactivity time"+childElement.getAttribute("time")+windforcasttime.size());
						}
						// list.add(childElement.getAttribute("time"));
						// System.err.println("mapactivity time"+childElement.getAttribute("time")+windforcasttime.size());
					}
				}
			}
		}
	}

	public static Handler teamShipsHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 44:
				if (app.isIslogin()) {
					if (isTimeOut) {
						isTimeOut = false;
						Toast.makeText(activity, "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};

	private void alertdialog(String s) {
		new AlertDialog.Builder(activity).setTitle("提示").setCancelable(false)
				.setMessage(s).setNegativeButton("取消", null)
				.setPositiveButton("登录", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(app.getMapActivity(),
								IsLoginActivity.class);
						app.getMapActivity().startActivity(intent);
						(app.getMapActivity()).overridePendingTransition(
								R.drawable.activity_open, 0);
					}
				}).show();
	}

	private void selectPressurelayer() {
		if (isShowAirPressure) {
			img_air.setBackgroundResource(R.drawable.btn_pressure);
			isShowAirPressure = false;
		} else {
			img_air.setBackgroundResource(R.drawable.btn_pressure_select);
			isShowAirPressure = true;
		}
		app.mEditor.putBoolean("IsShowAirPressure", isShowAirPressure).commit();
		mapView.refreshMapForceDraw(false, true);
	}

	private void selectWindlayer() {
		if (isShowWind) {
			img_wind.setBackgroundResource(R.drawable.btn_wind);
			isShowWind = false;
		} else {
			img_wind.setBackgroundResource(R.drawable.btn_wind_select);
			isShowWind = true;
		}
		app.mEditor.putBoolean("IsShowWind", isShowWind).commit();
		mapView.refreshMapForceDraw(false, true);
	}

	private void selectWavelayer() {
		if (isShowWave) {
			img_wave.setBackgroundResource(R.drawable.btn_wave);
			isShowWave = false;
		} else {
			img_wave.setBackgroundResource(R.drawable.btn_wave_select);
			isShowWave = true;
		}
		app.mEditor.putBoolean("IsShowWave", isShowWave).commit();
		mapView.refreshMapForceDraw(false, true);
	}

	private void selectOceanlayer() {
		if (isShowOcean) {
			img_ocean.setBackgroundResource(R.drawable.btn_ocean);
			isShowOcean = false;
		} else {
			img_ocean.setBackgroundResource(R.drawable.btn_ocean_select);
			isShowOcean = true;
		}
		app.mEditor.putBoolean("IsShowOcean", isShowOcean).commit();
		mapView.refreshMapForceDraw(false, true);
	}

	private void changetoChinachart() {
		mapKey = 1;
		img_sea_map.setBackgroundResource(R.drawable.btn_sea_map_select);
		img_all_sea_map.setBackgroundResource(R.drawable.btn_all_sea_map);
		img_land_map.setBackgroundResource(R.drawable.btn_land_map);

		ITileSource newSource1 = settings.getMapTileSource(false);
		mapLayers.mapTileLayer.setMap(newSource1);
		mapView.refreshMapForceDraw(false, true);
	}

	private void changetoWorldmap() {
		mapKey = 3;
		img_sea_map.setBackgroundResource(R.drawable.btn_sea_map);
		img_all_sea_map
				.setBackgroundResource(R.drawable.btn_all_sea_map_select);
		img_land_map.setBackgroundResource(R.drawable.btn_land_map);

		ITileSource newSource2 = settings.getMapTileSource(false);
		mapLayers.mapTileLayer.setMap(newSource2);
		mapView.refreshMapForceDraw(false, true);
	}

	// 界面跳转
	public void onClick(View view) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (view.getId()) {
		case R.id.effectRelativeLayout_search:
			Intent intent = new Intent(activity, SearchActivity.class);
			startActivity(intent);
			break;
		case R.id.locateMyLocationImageButton:
			activity.getMapViewTrackingUtilities().backToLocationImpl();
			break;
		case R.id.img_change_map:
			mViewChageMap.setVisibility(View.VISIBLE);
			mLinearLayoutChangeMap.setVisibility(View.VISIBLE);
			img_change_map.setBackgroundResource(R.drawable.bg_map_change);
			break;
		case R.id.view_black:
			mViewChageMap.setVisibility(View.GONE);
			mLinearLayoutChangeMap.setVisibility(View.GONE);
			img_change_map.setBackgroundResource(R.drawable.bg_zoom_control);
			break;
		case R.id.img_air_pressure_show:
			if (app.isIslogin()) {
				if (!app.getMyrole().equals("vvip")
						|| app.getLoginbean().getWeather().equals("1")) {
					selectPressurelayer();
					// }else if(!app.getMyrole().equals("vvip")){
					// selectPressurelayer();
				} else {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("气象权限不足请升级用户权限！")
							.setNegativeButton("确定", null).show();
				}

			} else {
				alertdialog("未登录用户不能使用气象，请登录");
			}
			break;
		case R.id.img_wind_show:
			if (app.isIslogin()) {
				if (!app.getMyrole().equals("vvip")
						|| app.getLoginbean().getWeather().equals("1")) {
					selectWindlayer();
					// }else if(!app.getMyrole().equals("vvip")){
					// selectWindlayer();
				} else {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("气象权限不足请升级用户权限！")
							.setNegativeButton("确定", null).show();
				}

			} else {
				alertdialog("未登录用户不能使用气象，请登录");
			}
			break;
		case R.id.img_wave_show:
			if (app.isIslogin()) {
				if (!app.getMyrole().equals("vvip")
						|| app.getLoginbean().getWeather().equals("1")) {
					selectWavelayer();
					// }else if(!app.getMyrole().equals("vvip")){
					// selectWavelayer();
				} else {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("气象权限不足请升级用户权限！")
							.setNegativeButton("确定", null).show();
				}

			} else {
				alertdialog("未登录用户不能使用气象，请登录");
			}
			break;
		case R.id.img_ocean_show:
			if (app.isIslogin()) {
				if (!app.getMyrole().equals("vvip")
						|| app.getLoginbean().getWeather().equals("1")) {
					selectOceanlayer();
					// }else if(!app.getMyrole().equals("vvip")){
					// selectWavelayer();
				} else {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("气象权限不足请升级用户权限！")
							.setNegativeButton("确定", null).show();
				}

			} else {
				alertdialog("未登录用户不能使用气象，请登录");
			}
			break;
		case R.id.img_sea_map:
			if (app.isIslogin()) {
				if (!app.getMyrole().equals("vvip")
						|| app.getLoginbean().getChinachart().equals("1")) {
					changetoChinachart();
					// }else if(!app.getMyrole().equals("vvip")){
					// changetoChinachart();
				} else {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("权限不足请升级用户权限！")
							.setNegativeButton("确定", null).show();
				}
			} else {
				alertdialog("未登录用户不能使用海图，请登录");
			}
			break;
		case R.id.img_all_sea_map:
			if (app.isIslogin()) {
				// System.err.println("app.getLoginbean().getGchart()"+app.getLoginbean().getGchart());
				if (!app.getMyrole().equals("vvip")
						|| app.getLoginbean().getGchart().equals("1")
						|| app.getLoginbean().getGchartupdate().equals("1")) {
					changetoWorldmap();
					// }else if(!app.getMyrole().equals("vvip")){
					// changetoWorldmap();
				} else {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("权限不足请升级用户权限！")
							.setNegativeButton("确定", null).show();
				}

			} else {
				alertdialog("未登录用户不能使用全球海图，请登录");
			}
			break;
		case R.id.img_land_map:
			if (app.isIslogin()) {
				mapKey = 2;
				img_sea_map.setBackgroundResource(R.drawable.btn_sea_map);
				img_all_sea_map
						.setBackgroundResource(R.drawable.btn_all_sea_map);
				img_land_map
						.setBackgroundResource(R.drawable.btn_land_map_select);
				ITileSource newSource3 = settings.getMapTileSource(false);
				mapLayers.mapTileLayer.setMap(newSource3);
				mapView.refreshMapForceDraw(false, true);
			} else {
				// new AlertDialog.Builder(activity).setTitle("提示")
				// .setCancelable(false).setMessage("未登录用户不能使用海图，请登录")
				// .setNegativeButton("取消", null)
				// .setPositiveButton("登录", new OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// // TODO Auto-generated method stub
				// Intent intent = new Intent(
				// app.getMapActivity(),
				// IsLoginActivity.class);
				// app.getMapActivity().startActivity(intent);
				// (app.getMapActivity())
				// .overridePendingTransition(
				// R.drawable.activity_open, 0);
				// }
				// }).show();//所有用户均可查看。
			}
			break;
		case R.id.weather_time_before:
			if (p - 1 >= 0) {
				p = p - 1;
			} else {
				p = 0;
				break;
			}
			String path = app.getResourceManager().getDirWithTiles()
					.getAbsolutePath();
			deleteDirectory(path + "/气压图");
			deleteDirectory(path + "/海浪图");
			deleteDirectory(path + "/风力图");
			deleteDirectory(path + "/洋流图");

			mapView.getApplication().getResourceManager().clearAllTiles();
			// 清空已有的天气图片。

			Date dab = new Date(getUtctime());
			localTime = utc2Localtime(dab, (nowtime.getDate() - 1),
					Integer.valueOf(weatherforcasttime.get(p)), 0, 0,
					"yyyy-MM-dd HH:mm:ss");
			weatherf = weatherforcasttime.get(p);
			
			ITileSource newwaveSource = settings.getWaveMapTileSource(false);
			mapLayers.waveMapTileLayer.setMap(newwaveSource);
			ITileSource newwindSource = settings.getWindMapTileSource(false);
			mapLayers.windMapTileLayer.setMap(newwindSource);
			ITileSource newweatherSource = settings
					.getWeatherMapTileSource(false);
			mapLayers.weatherMapTileLayer.setMap(newweatherSource);
			ITileSource newoceanSource = settings.getOceanMapTileSource(false);
			mapLayers.oceanmaptilelayer.setMap(newoceanSource);
			if (mapView.getZoom() <= 7) {
				mapView.refreshMapForceDraw(false, true);
			}

			
			seekBar.setProgress(p * 100 / (weatherforcasttime.size() - 1));
			seekweathertime.setText("气象时间：" + localTime);
			break;
		case R.id.weather_time_after:
			if (p + 1 < weatherforcasttime.size()) {
				p = p + 1;
			} else {
				p = weatherforcasttime.size() - 1;
				break;
			}
			String path1 = app.getResourceManager().getDirWithTiles()
					.getAbsolutePath();
			deleteDirectory(path1 + "/气压图");
			deleteDirectory(path1 + "/海浪图");
			deleteDirectory(path1 + "/风力图");
			deleteDirectory(path1 + "/洋流图");
			mapView.getApplication().getResourceManager().clearAllTiles();
			// 清空已有的天气图片。

			Date daa = new Date(getUtctime());
			localTime = utc2Localtime(daa, (nowtime.getDate() - 1),
					Integer.valueOf(weatherforcasttime.get(p)), 0, 0,
					"yyyy-MM-dd HH:mm:ss");
			weatherf = weatherforcasttime.get(p);
			
			ITileSource newwaveSource1 = settings.getWaveMapTileSource(false);
			mapLayers.waveMapTileLayer.setMap(newwaveSource1);
			ITileSource newwindSource1 = settings.getWindMapTileSource(false);
			mapLayers.windMapTileLayer.setMap(newwindSource1);
			ITileSource newweatherSource1 = settings
					.getWeatherMapTileSource(false);
			mapLayers.weatherMapTileLayer.setMap(newweatherSource1);
			ITileSource newoceanSource1 = settings.getOceanMapTileSource(false);
			mapLayers.oceanmaptilelayer.setMap(newoceanSource1);
			if (mapView.getZoom() <= 7) {
				mapView.refreshMapForceDraw(false, true);
			}

			seekBar.setProgress(p * 100 / (weatherforcasttime.size() - 1));
			seekweathertime.setText("气象时间：" + localTime);
			break;
		case R.id.radio_details:
			if (!app.getMyrole().equals("vvip")
					|| app.getLoginbean().getShipdetail().equals("1")) {
				if (mDetailsFragment == null) {
					mDetailsFragment = new DetailsFragment();
				}
				transaction.replace(R.id.fragment_content, mDetailsFragment);
				// }else if(!app.getMyrole().equals("vvip")){
				// if (mDetailsFragment == null) {
				// mDetailsFragment = new DetailsFragment();
				// }
				// transaction.replace(R.id.fragment_content, mDetailsFragment);
			} else {
				new AlertDialog.Builder(activity).setTitle("提示")
						.setCancelable(false).setMessage("权限不足请升级用户权限！")
						.setNegativeButton("确定", null).show();
			}

			break;
		case R.id.radio_setting:
			if (mSettingFragment == null) {
				mSettingFragment = new SettingFragment(MapActivity.this);
			}
			transaction.replace(R.id.fragment_content, mSettingFragment);
			break;
		case R.id.radio_team:

			if (mTeamFragment == null) {
				mTeamFragment = new TeamFragment(MapActivity.this);
			}
			transaction.replace(R.id.fragment_content, mTeamFragment);
			break;
		case R.id.radio_myarea:

			if (areaFragment == null) {
				areaFragment = new AreaFragment(MapActivity.this);
			}
			transaction.replace(R.id.fragment_content, areaFragment);
			break;
		case R.id.radio_ships:
			if (mSettingFragment != null) {
				transaction.remove(mSettingFragment);
			}
			if (mDetailsFragment != null) {
				transaction.remove(mDetailsFragment);
			}
			if (mTeamFragment != null) {
				transaction.remove(mTeamFragment);
			}
			if (portshipsFragment != null) {
				transaction.remove(portshipsFragment);
			}
			if (areaFragment != null) {
				transaction.remove(areaFragment);
			}
			// if (app.myPreferences.getBoolean("IsLogin", false)
			// && !app.myPreferences.getString("role", null).equals(
			// "antenna")) {
			// mapKey = 1;
			// img_sea_map
			// .setBackgroundResource(R.drawable.btn_sea_map_select);
			// img_all_sea_map
			// .setBackgroundResource(R.drawable.btn_all_sea_map);
			// img_land_map.setBackgroundResource(R.drawable.btn_land_map);
			// }
			break;
		case R.id.radio_myportships:
			if (portshipsFragment == null) {
				portshipsFragment = new PortShipsFragment(MapActivity.this);
			}
			transaction.replace(R.id.fragment_content, portshipsFragment);
			break;
		}
		transaction.commit();
	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	private void setDefaultFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		mDetailsFragment = new DetailsFragment();
		transaction.replace(R.id.fragment_content, mDetailsFragment);
		transaction.commit();
	}

	// 放大显示海图
	private void zoomInMapView() {
		activity.changeZoom(1);
	}

	// 缩小显示海图
	private void zoomOutMapView() {
		activity.changeZoom(-1);
	}

	private class LocationBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(IndexConstants.LOCATION_ACTION))
				return;
			Double la = intent.getDoubleExtra("Lat", 1.1);
			Double lo = intent.getDoubleExtra("Lon", 2.2);
			MapActivity.this.unregisterReceiver(this);// 不需要时注销
			if (la == 1.1) {
				Toast.makeText(activity, "请检查网络设置", Toast.LENGTH_LONG).show();
			} else {
				AnimateDraggingMapThread thread = mapView
						.getAnimatedDraggingThread();
				thread.startMoving(la, lo, mapView.getZoom(), false);
			}
		}
	}

	private static final String VECTOR_INDEXES_CHECK = "VECTOR_INDEXES_CHECK"; //$NON-NLS-1$

	public void addLockView(FrameLayout lockView) {
		this.lockView = lockView;
	}

	private void createProgressBarForRouting() {
		FrameLayout parent = (FrameLayout) mapView.getParent();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		params.topMargin = (int) (60 * dm.density);
		final ProgressBar pb = new ProgressBar(this, null,
				android.R.attr.progressBarStyleHorizontal);
		pb.setIndeterminate(false);
		pb.setMax(100);
		pb.setLayoutParams(params);
		pb.setVisibility(View.GONE);
		parent.addView(pb);
	}

	@SuppressWarnings("rawtypes")
	public Object getLastNonConfigurationInstanceByKey(String key) {
		Object k = super.getLastNonConfigurationInstance();
		if (k instanceof Map) {
			return ((Map) k).get(key);
		}
		return null;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		LinkedHashMap<String, Object> l = new LinkedHashMap<String, Object>();
		// for (OsmandMapLayer ml : mapView.getLayers()) {
		// ml.onRetainNonConfigurationInstance(l);
		// }
		return l;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (app.myPreferences.getBoolean("IsLogin", false)
		// && !app.myPreferences.getString("role", null).equals("antenna")) {
		// mapKey = 1;
		// img_sea_map.setBackgroundResource(R.drawable.btn_sea_map_select);
		// img_all_sea_map.setBackgroundResource(R.drawable.btn_all_sea_map);
		// img_land_map.setBackgroundResource(R.drawable.btn_land_map);
		// } else {
		// img_sea_map.setBackgroundResource(R.drawable.btn_sea_map);
		// img_all_sea_map.setBackgroundResource(R.drawable.btn_all_sea_map);
		// img_land_map.setBackgroundResource(R.drawable.btn_land_map_select);
		// mapKey = 2;
		// }
		// ITileSource newSource3 = settings.getMapTileSource(false);
		// mapLayers.mapTileLayer.setMap(newSource3);
		// mapView.refreshMapForceDraw(false, true);
		cancelNotification();
		// print("onResum in MapActivity !!!!");
		if (settings.AUDIO_STREAM_GUIDANCE.get() != null) {
			setVolumeControlStream(settings.AUDIO_STREAM_GUIDANCE.get());
		} else {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
		}
		updateApplicationModeSettings();
		String filterId = settings.getPoiFilterForMap();
		// print("MapActivity调用添加位置侦听器。");
		app.getLocationProvider().resumeAllUpdates();

		app.getLocationProvider().checkIfLastKnownLocationIsValid();

		if (settings != null && settings.isLastKnownMapLocation()) {
			LatLon l = settings.getLastKnownMapLocation();
			// print("MapActivity 341.");
			mapView.setLatLon(l.getLatitude(), l.getLongitude());
			mapView.setIntZoom(2);
		}

		settings.MAP_ACTIVITY_ENABLED.set(true);
		checkExternalStorage();
		showAndHideMapPosition();

		// LatLon cur = new LatLon(mapView.getLatitude(),
		// mapView.getLongitude());
		LatLon latLonToShow = settings.getAndClearMapLocationToShow();

		if (latLonToShow != null) {
			mapViewTrackingUtilities.setMapLinkedToLocation(false);
		}

		// String mapLabelToShow = settings.getAndClearMapLabelToShow();
		// Object toShow = settings.getAndClearObjectToShow();
		// mapView.refreshMap(true);

		mapLayers.shipsInfoLayer.showSearchships();
	}

	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}

	// public void addDialogProvider(DialogProvider dp) {
	// dialogProviders.add(dp);
	// }

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		// for (DialogProvider dp : dialogProviders) {
		// dialog = dp.onCreateDialog(id);
		// if (dialog != null) {
		// return dialog;
		// }
		// }
		if (id == OsmandApplication.PROGRESS_DIALOG) {
			return startProgressDialog;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		// for (DialogProvider dp : dialogProviders) {
		// dp.onPrepareDialog(id, dialog);
		// }
	}

	// 更新 放大缩小图标
	public void updateZoomButtonStatus(boolean zoomInEnabled,
			boolean zoomOutEnabled) {
		if (zoomInEnabled) {
			img_zoom_in_btn.setImageDrawable(getResources().getDrawable(
					R.drawable.stickers_add));
		} else {
			img_zoom_in_btn.setImageDrawable(getResources().getDrawable(
					R.drawable.stickers_add_cancel));
		}

		if (zoomOutEnabled) {
			img_zoom_out_btn.setImageDrawable(getResources().getDrawable(
					R.drawable.stickers_sub));
		} else {
			img_zoom_out_btn.setImageDrawable(getResources().getDrawable(
					R.drawable.stickers_sub_cancel));
		}
	}

	public void changeZoom(int stp) {
		boolean changeLocation = false;
		final int newZoom = mapView.getZoom() + stp;
		// System.err.println("newZoom: "+newZoom+", changeLocation: "+changeLocation);
		print("这是设置的最小zoom"
				+ activity.getMapView().getMainLayer().getMinimumShownMapZoom()
				+ "最大"
				+ activity.getMapView().getMainLayer().getMaximumShownMapZoom());
		if (stp > 0) {
			if (newZoom > activity.getMapView().getMainLayer()
					.getMaximumShownMapZoom()) {
				img_zoom_in_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.stickers_add_cancel));
				return;
			} else {
				img_zoom_in_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.stickers_add));
				img_zoom_out_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.stickers_sub));
			}
		} else {
			if (newZoom < activity.getMapView().getMainLayer()
					.getMinimumShownMapZoom()) {
				img_zoom_out_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.stickers_sub_cancel));
				return;
			} else {
				img_zoom_out_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.stickers_sub));
				img_zoom_in_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.stickers_add));
			}
		}
		mapView.getAnimatedDraggingThread().startZooming(newZoom,
				changeLocation);

		// if (app.accessibilityEnabled())
		// AccessibleToast
		// .makeText(
		// this,
		//							getString(R.string.zoomIs) + " " + newZoom, Toast.LENGTH_SHORT).show(); //$NON-NLS-1$
		showAndHideMapPosition();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
				&& app.accessibilityEnabled()) {
			if (!uiHandler.hasMessages(LONG_KEYPRESS_MSG_ID)) {
				Message msg = Message.obtain(uiHandler, new Runnable() {
					@Override
					public void run() {
						// app.getLocationProvider().emitNavigationHint();
					}
				});
				msg.what = LONG_KEYPRESS_MSG_ID;
				uiHandler.sendMessageDelayed(msg, LONG_KEYPRESS_DELAY);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU
				&& event.getRepeatCount() == 0) {

			// 点击菜单按钮，弹出菜单界�?
			// mapActions.openOptionsMenuAsList();// yang chun

			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			System.out.println("Map activity 按两次退出。");
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), /* "再按�?���?��程序" */
				getResources().getString(R.string.tap_twice_to_exit),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {

				String path = app.getResourceManager().getDirWithTiles()
						.getAbsolutePath();
				System.out.println("path______:" + path);
				deleteDirectory(path + "/green");// 退出时清空绿点图，下次重新加载
				deleteDirectory(path + "/气压图");
				deleteDirectory(path + "/海浪图");
				deleteDirectory(path + "/风力图");
				deleteDirectory(path + "/洋流图");

				if (app.getLoginbean() != null
						&& app.getLoginbean().getGchartupdate() != null
						&& app.getLoginbean().getGchartupdate().equals("1")) {
					File f = new File(path + "/全球海图");
					Date d = new Date();
					if (d.getTime() - f.lastModified() > 7 * 24 * 60 * 60
							* 1000) {
						// deleteDirectory(path + "/海图");
						// deleteDirectory(path + "/地图");
						deleteDirectory(path + "/全球海图");
					}// 大于7天清除海图文件夹 更新海图。 如果权限要更新则判断日期进行删除缓存
				}
				// File f=new File(path);
				// File[] fs=f.listFiles();
				// for(int i=0;i<fs.length;i++){
				// System.out.println("删除后剩余的"+fs[i].getName());
				// }//检测删除是否成功

				UserLogout thread = null;
				thread = new UserLogout(app);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					thread.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					thread.execute();
				}
				finish();
				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void deleteallMaps() {
		String path = app.getResourceManager().getDirWithTiles()
				.getAbsolutePath();
		System.out.println("path______:" + path);
		deleteDirectory(path + "/green");// 清空绿点图，下次重新加载
		deleteDirectory(path + "/气压图");
		deleteDirectory(path + "/海浪图");
		deleteDirectory(path + "/风力图");
		deleteDirectory(path + "/洋流图");

		deleteDirectory(path + "/地图");
		deleteDirectory(path + "/全球海图");
		deleteDirectory(path + "/海图");
		AccessibleToast.makeText(this, R.string.clear_cache_success,
				Toast.LENGTH_LONG).show();
	}// 清空本地图片

	private static void print(String msg) {

		android.util.Log.i(TAG, msg);

	}

	private static final String TAG = "FileDownloader";

	private long exitTime = 0;

	public void setMapLocation(double lat, double lon) {
		print("MapActivity 505.");
		mapView.setLatLon(lat, lon);
		mapViewTrackingUtilities.locationChanged(lat, lon, this);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE
				&& settings.USE_TRACKBALL_FOR_MOVEMENTS.get()) {
			float x = event.getX();
			float y = event.getY();
			final RotatedTileBox tb = mapView.getCurrentRotatedTileBox();
			final QuadPoint cp = tb.getCenterPixelPoint();
			final LatLon l = tb
					.getLatLonFromPixel(cp.x + x * 15, cp.y + y * 15);
			setMapLocation(l.getLatitude(), l.getLongitude());
			return true;
		}
		return super.onTrackballEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (isShipDetails) {
			mRadioDetails.performClick();
			isShipDetails = false;
		}
		if (isTeamShipMove) {
			mRaidoShips.performClick();
			isTeamShipMove = false;
		}
	}

	protected void setProgressDlg(Dialog progressDlg) {
		this.progressDlg = progressDlg;
	}

	protected Dialog getProgressDlg() {
		return progressDlg;
	}

	@Override
	protected void onStop() {
		// System.out.println("onStoponStoponStoponStoponStop");
		if (progressDlg != null) {
			progressDlg.dismiss();
			progressDlg = null;
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// FailSafeFuntions.quitRouteRestoreDialog();
		// OsmandPlugin.onMapActivityDestroy(this);
		// System.out.println("onDestroy:onDestroyonDestroyonDestroy");
		// String
		// path=app.getResourceManager().getDirWithTiles().getAbsolutePath();
		// System.out.println("path______:"+path);
		// deleteDirectory(path+"/green");
		if (app.isIslogin()) {
			UserLogout thread = null;
			thread = new UserLogout(app);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				thread.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				thread.execute();
			}
//			app.setIslogin(false);
//			app.setLoginbean(null);
//
//			// app.mEditor.putString("role", "");
//			// // System.err.println("islogin rolerole " + l.getRole());
//			// app.mEditor.putBoolean("IsLogin", false);
//			app.mEditor.commit();
		}

		mapViewTrackingUtilities.setMapView(null);
		cancelNotification();
		app.getResourceManager().getMapTileDownloader()
				.removeDownloaderCallback(mapView);
	}

	public boolean deleteDirectory(String filePath) {
		boolean flag = false;
		// 如果filePath不以文件分隔符结尾，自动添加文件分隔符
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		// 遍历删除文件夹下的所有文件(包括子目录)
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				// 删除子文件
				flag = files[i].delete();
				if (!flag)
					break;
			} else {
				// 删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前空目录
		dirFile.delete();
		return !dirFile.exists();
	}

	private void cancelNotification() {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		if (mNotificationManager != null) {
			mNotificationManager.cancel(APP_NOTIFICATION_ID);
		}
	}

	public LatLon getMapLocation() {
		return new LatLon(mapView.getLatitude(), mapView.getLongitude());
	}

	// Duplicate methods to OsmAndApplication
	// public LatLon getPointToNavigate() {
	// return app.getTargetPointsHelper().getPointToNavigate();
	// }
	//
	// public RoutingHelper getRoutingHelper() {
	// return app.getRoutingHelper();
	// }

	@Override
	protected void onPause() {
		// System.err.println("onPause");
		super.onPause();
		// app.getLocationProvider().pauseAllUpdates();
		// app.getDaynightHelper().stopSensorIfNeeded();
		// settings.APPLICATION_MODE.removeListener(applicationModeListener);
		// System.out.println("onPauseonPauseonPauseonPauseonPause");
		settings.setLastKnownMapLocation((float) mapView.getLatitude(),
				(float) mapView.getLongitude());
		AnimateDraggingMapThread animatedThread = mapView
				.getAnimatedDraggingThread();
		if (animatedThread.isAnimating()
				&& animatedThread.getTargetIntZoom() != 0) {
			settings.setMapLocationToShow(animatedThread.getTargetLatitude(),
					animatedThread.getTargetLongitude(),
					animatedThread.getTargetIntZoom());
		}

		settings.setLastKnownMapZoom(mapView.getZoom());
		settings.MAP_ACTIVITY_ENABLED.set(false);
		// app.getResourceManager().interruptRendering();
		// app.getResourceManager().setBusyIndicator(null);

		// OsmandPlugin.onMapActivityPause(this);
	}

	public void updateApplicationModeSettings() {
		// update vector renderer
		// print("map activity zoom scale: "+mapView.getSettingsZoomScale());
		mapLayers.updateLayers(mapView, 0);
		mapView.setComplexZoom(mapView.getZoom(),
				mapView.getSettingsZoomScale());

		// mapViewTrackingUtilities.updateSettings();

		getMapView().refreshMap(true);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (!app.accessibilityEnabled()) {
				// mapActions.contextMenuPoint(mapView.getLatitude(),
				// mapView.getLongitude());
			} else if (uiHandler.hasMessages(LONG_KEYPRESS_MSG_ID)) {
				uiHandler.removeMessages(LONG_KEYPRESS_MSG_ID);
				// mapActions.contextMenuPoint(mapView.getLatitude(),
				// mapView.getLongitude());
			}
			return true;
		} else if (settings.ZOOM_BY_TRACKBALL.get()) {
			// Parrot device has only dpad left and right
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				changeZoom(-1);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				changeZoom(1);
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				|| keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			int dx = keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 15
					: (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ? -15 : 0);
			int dy = keyCode == KeyEvent.KEYCODE_DPAD_DOWN ? 15
					: (keyCode == KeyEvent.KEYCODE_DPAD_UP ? -15 : 0);
			final RotatedTileBox tb = mapView.getCurrentRotatedTileBox();
			final QuadPoint cp = tb.getCenterPixelPoint();
			final LatLon l = tb.getLatLonFromPixel(cp.x + dx, cp.y + dy);
			setMapLocation(l.getLatitude(), l.getLongitude());
			return true;
		}
		// else if (OsmandPlugin.onMapActivityKeyUp(this, keyCode)) {
		// return true;
		// }
		return super.onKeyUp(keyCode, event);
	}

	public void checkExternalStorage() {
		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_MOUNTED.equals(state)) {
//			// ok
//		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//			AccessibleToast.makeText(this, R.string.sd_mounted_ro,
//					Toast.LENGTH_LONG).show();
//		} else {
//			AccessibleToast.makeText(this, R.string.sd_unmounted,
//					Toast.LENGTH_LONG).show();
//		}
	}

	public void showAndHideMapPosition() {
		mapView.setShowMapPosition(true);
		app.runMessageInUIThreadAndCancelPrevious(SHOW_POSITION_MSG_ID,
				new Runnable() {
					@Override
					public void run() {
						if (mapView.isShowMapPosition()) {
							mapView.setShowMapPosition(false);
							mapView.refreshMap();
						}
					}
				}, 2500);
	}

	public OsmandMapTileView getMapView() {
		return mapView;
	}

	public MapViewTrackingUtilities getMapViewTrackingUtilities() {
		return mapViewTrackingUtilities;
	}

	protected void parseLaunchIntentLocation() {

	}

	// public MapActivityActions getMapActions() {
	// return mapActions;
	// }

	public MapActivityLayers getMapLayers() {
		return mapLayers;
	}

	public static void launchMapActivityMoveToTop(Context activity) {
		System.err.println("launchMapActivityMoveToTop");
		Intent newIntent = new Intent(activity,
				((OsmandApplication) activity.getApplicationContext())
						.getAppCustomization().getMapActivity());
		newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(newIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// OsmandPlugin.onMapActivityResult(requestCode, resultCode, data);
	}

	public void refreshMap() {
		getMapView().refreshMap();
	}

	private String formatLonString(double value) {
		boolean sign;
		if (value >= 0)
			sign = true;
		else {
			value = Math.abs(value);
			sign = false;
		}

		// if (value >= 0)
		// sign = true;
		// else {
		// value = Math.abs(value);
		// sign = false;
		// }

		double d_value = value * 3600;
		// print("d_value (*3600) : "+d_value);

		String str = "";
		String deg_str = "";
		int deg = (int) Math.floor(d_value / 3600);
		if (deg < 10) {
			deg_str = "00" + deg;
		} else if (deg > 10 && deg < 100) {
			deg_str = "0" + deg;
		} else {
			deg_str = "" + deg;
		}

		// print("整个度： "+deg_str);

		String min_str = "";

		int min = (int) Math.floor((d_value - deg * 3600) / 60);
		if (min < 10) {
			min_str = "0" + min;
		} else {
			min_str = "" + min;
		}

		// print("整分�?+min_str);

		String sec_str = "";
		int sec = (int) Math.floor((d_value - deg * 3600 - min * 60));

		// print("sec: "+sec);

		double l = d_value - deg * 3600 - min * 60 - sec;// 秒的小数

		java.text.DecimalFormat dft = new java.text.DecimalFormat(".##");
		// double ll = l / 60.0;

		String ll_str = dft.format(l);
		float ll_f = Float.parseFloat(ll_str);
		sec_str = "" + (sec + ll_f);

		// if(sec_decimal<10){
		// sec_str ="0"+sec_decimal;
		// }else{
		// sec_str = ""+sec_decimal;
		// }
		// print("sec_str: "+sec_str);

		str = deg_str + "°" + min_str + "'" + sec_str + "''"/* + sec_decimal */
				+ (sign ? "E" : "W");

		return str;
	}

	private String formatLatString(double value) {

		boolean sign;
		if (value >= 0)
			sign = true;
		else {
			value = Math.abs(value);
			sign = false;
		}

		// if (value >= 0)
		// sign = true;
		// else {
		// value = Math.abs(value);
		// sign = false;
		// }

		String str;
		String deg_str, min_str, sec_str;

		double d_value = value * 3600;

		int deg = (int) Math.floor(d_value / 3600);
		if (deg < 10) {
			deg_str = "0" + deg;
		} else {
			deg_str = "" + deg;
		}

		int min = (int) Math.floor((d_value - deg * 3600) / 60);
		if (min < 10) {
			min_str = "0" + min;
		} else {
			min_str = "" + min;
		}

		int sec = (int) Math.floor((d_value - deg * 3600 - min * 60));

		double l = d_value - deg * 3600 - min * 60 - sec;
		// double ll = l/60.0;

		// double sec_d = sec+ll;

		java.text.DecimalFormat dft = new java.text.DecimalFormat(".##");

		String sec_d_str = dft.format(l);

		float sec_d_f = Float.parseFloat(sec_d_str);

		sec_str = "" + (sec + sec_d_f);

		// sec_d_f = sec_d_f * 60;

		// java.text.DecimalFormat dft2 = new java.text.DecimalFormat("##.##");
		// String sss = dft2.format(sec_d_f);

		// if(sec_d_f<10){
		// sec_str ="0"+sec_d_f;
		// }else{
		// sec_str = ""+sec_d_f;
		// }

		str = deg_str + "°" + min_str + "'" + sec_str + "''"/* ." + sec_decimal */
				+ (sign ? "N" : "S");
		return str;
	}

	private String absoluteDirectionString(float bearing, OsmandApplication app) {
		int cs = (int) Math.round((float) Math.rint(bearing));
		if (cs < 10) {
			return "00" + cs;
		} else if (cs > 10 && cs < 100) {
			return "0" + cs;
		} else
			return "" + cs;

	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		System.err.println("mapactivity Thread.setDefaultUncaughtExceptionHandler(this); "
				+ arg0 + " " + arg1);
	}

}