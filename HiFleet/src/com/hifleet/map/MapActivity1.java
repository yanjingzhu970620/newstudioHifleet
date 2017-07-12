package com.hifleet.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hifleet.lnfo.layer.TraceInfoLayer;
import com.hifleet.lnfo.layer.timeLableLayer;
import com.hifleet.map.MapTileDownloader.DownloadRequest;
import com.hifleet.map.MapTileDownloader.IMapDownloaderCallback;
import com.hifleet.plus.R;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapActivity1 extends AccessibleActivity implements UncaughtExceptionHandler {
	// �?��比例尺设�?
	private static final int SHOW_POSITION_MSG_ID = OsmAndConstants.UI_HANDLER_MAP_VIEW + 1;
	private static final int LONG_KEYPRESS_MSG_ID = OsmAndConstants.UI_HANDLER_MAP_VIEW + 2;
	private static final int LONG_KEYPRESS_DELAY = 500;
	private static final String FIRST_TIME_APP_RUN = "FIRST_TIME_APP_RUN"; //$NON-NLS-1$
	private static final String VERSION_INSTALLED = "VERSION_INSTALLED"; //$NON-NLS-1$
	public static Double la;
	public static Double lo;
	public static float co;
	public static Double sp;
	public static String fromac;
	public static String startTime;
	public static String endTime;
	public static String mymmsi;
	public static String needlayer;
	public static String latlon;
	public static String areaname;
	/** Called when the activity is first created. */
	private OsmandMapTileView mapView;

	// private MapActivityActions mapActions;
	private MapActivityLayers mapLayers;

	// Notification status
	private NotificationManager mNotificationManager;
	private int APP_NOTIFICATION_ID = 1;

	// handler to show/hide trackball position and to link map with delay
	private Handler uiHandler = new Handler();
	// App variables
	private static OsmandApplication app;
	private OsmandSettings settings;

	private Dialog progressDlg = null;

	private ProgressDialog startProgressDialog;

	private FrameLayout lockView, frameMap;

	private RelativeLayout relative;

	public static RelativeLayout relativeLayoutBackground;

	private static MapActivity1 activity;

	private TraceInfoLayer traceInfoLayer;
	private ImageButton img_zoom_in_btn, img_zoom_out_btn;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		app = getMyApplication();
		settings = app.getSettings();

		app.applyTheme(this);

		app.setMapActivity1(this);

		super.onCreate(savedInstanceState);
		activity = this;
		Thread.setDefaultUncaughtExceptionHandler(this);
		final OsmAndAppCustomization appCustomization = getMyApplication()
				.getAppCustomization();

		// mapActions = new MapActivityActions(this);
		mapLayers = new MapActivityLayers(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        System.out.println("map1 oncreate!!!");
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		String k = bundle.getString("key");
		startTime = bundle.getString("starttime");
		endTime = bundle.getString("endtime");
		mymmsi = bundle.getString("mmsi");
		needlayer = bundle.getString("needlayer");
		latlon=bundle.getString("latlon");
		areaname=bundle.getString("areaname");
		
		// AnimateDraggingMapThread thread =
		// mapView.getAnimatedDraggingThread();
		if (bundle.getString("la") != null && bundle.getString("lo") != null) {
			la = Double.parseDouble(bundle.getString("la"));
			lo = Double.parseDouble(bundle.getString("lo"));
		}
		if (bundle.getString("co") != null && bundle.getString("sp") != null) {
			co = Float.parseFloat(bundle.getString("co"));
			sp = Double.parseDouble(bundle.getString("sp"));
		}
		fromac = bundle.getString("from");
		setContentView(R.layout.activity_map1);
		mapView = (OsmandMapTileView) findViewById(R.id.MapView);
		if (la != null && !la.equals("") && lo != null && !lo.equals("")) {
			mapView.setLatLon(la, lo);
		} else {
			mapView.setLatLon(31, 122);
		}

		
		if (needlayer.equals("ship")||needlayer.equals("alert")||needlayer.equals("area")) {
			mapView.setIntZoom(10);
			}else{
				mapView.setIntZoom(2);
			}//判断是否是轨迹
		app.getResourceManager().getMapTileDownloader()
				.addDownloaderCallback(new IMapDownloaderCallback() {
					@Override
					public void tileDownloaded(DownloadRequest request) {
						if (request != null && !request.error
								&& request.fileToSave != null) {
							ResourceManager mgr = app.getResourceManager();
							print("map activity 1 map tile download.");
							mgr.tileDownloaded(request);
						}
						if (request == null || !request.error) {
							mapView.tileDownloaded(request);
						}
					}
				});
		// Intent intent = getIntent();
		// Bundle bundle = new Bundle();
		// bundle = intent.getExtras();
		// String k = bundle.getString("key");
		// AnimateDraggingMapThread thread =
		// mapView.getAnimatedDraggingThread();
		// Double la = Double.parseDouble(bundle.getString("la"));
		// Double lo = Double.parseDouble(bundle.getString("lo"));
		//
		// //System.out.println("lalala===" + la + lo);
		// thread.startMoving(la, lo, 9, false);
		print("map activity 1 createLayers.");
		mapLayers.createLayers(mapView, 1);
		TraceInfoLayer.listp.clear();
		timeLableLayer.addedlable.clear();
		// AnimateDraggingMapThread thread =
		// mapView.getAnimatedDraggingThread();
		// thread.startMoving(la, lo, 9, false);
		if (lockView != null) {
			((FrameLayout) mapView.getParent()).addView(lockView);
		}
		relativeLayoutBackground = (RelativeLayout) findViewById(R.id.rl_background);
		if (needlayer.equals("ship")||needlayer.equals("alert")||needlayer.equals("area")) {
			relativeLayoutBackground.setVisibility(View.GONE);
		}
		relative = (RelativeLayout) findViewById(R.id.relative);
		img_zoom_in_btn = (ImageButton) findViewById(R.id.mapActivityZoomInImageButton);
		img_zoom_out_btn = (ImageButton) findViewById(R.id.mapActivityZoomOutImageButton);

		// BubbleView b1 = new BubbleView(this);
		// b1.getText1().setText("我是气泡图");
		// relative.addView(b1);

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
		// AnimateDraggingMapThread thread =
		// mapView.getAnimatedDraggingThread();
		// thread.startMoving(la, lo, 9, false);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_background:
			break;
		}
	}

	public static Handler tracHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TraceInfoLayer.ALERTIMEOUT:
				new AlertDialog.Builder(activity).setTitle("提示")
						.setCancelable(false).setMessage("网络请求超时，请重新查看")
						.setNegativeButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								activity.finish();
							}
						}).show();
				break;
			case TraceInfoLayer.CANCLEBACKGROUND:
				relativeLayoutBackground.setVisibility(View.GONE);
				break;
			case TraceInfoLayer.FINISHACTIVITY:
				new AlertDialog.Builder(activity).setTitle("提示")
						.setCancelable(false).setMessage("暂无轨迹数据")
						.setNegativeButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								activity.finish();
							}
						}).show();
				break;
			case TraceInfoLayer.SESSIONOUTTIME:
//				UserLogout thread = null;
//				thread = new UserLogout(app);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					thread.executeOnExecutor(Executors.newCachedThreadPool(),
//							new String[0]);
//				} else {
//					thread.execute();
//				}
//				new AlertDialog.Builder(activity).setTitle("提示")
//						.setCancelable(false).setMessage("会话超时或账号在其他计算机登录！")
//						.setNegativeButton("确定", new OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//								app.mEditor.putBoolean("IsLogin", false)
//										.commit();
//								Intent intent = new Intent(activity,
//										LoginActivity.class);
//								activity.startActivity(intent);
//								app.getInstance().exit();
//							}
//						}).show();
				Toast.makeText(activity, "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	// 放大显示海图
	private void zoomInMapView() {
		activity.changeZoom(1);
	}

	// 缩小显示海图
	private void zoomOutMapView() {
		activity.changeZoom(-1);
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
		// print("onResum in MapActivity 1/");
		cancelNotification();
		// if (settings.MAP_SCREEN_ORIENTATION.get() !=
		// getRequestedOrientation()) {
		// setRequestedOrientation(settings.MAP_SCREEN_ORIENTATION.get());
		// // can't return from this method we are not sure if activity will be
		// // recreated or not
		// }
		// for voice navigation
		if (settings.AUDIO_STREAM_GUIDANCE.get() != null) {
			setVolumeControlStream(settings.AUDIO_STREAM_GUIDANCE.get());
		} else {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
		}
		updateApplicationModeSettings();
		String filterId = settings.getPoiFilterForMap();

		if (settings != null && settings.isLastKnownMapLocation()) {
			LatLon l = settings.getLastKnownMapLocation();
			mapView.setLatLon(l.getLatitude(), l.getLongitude());
			mapView.setIntZoom(settings.getLastKnownMapZoom());
		}

		settings.MAP_ACTIVITY_ENABLED.set(true);
		checkExternalStorage();
		showAndHideMapPosition();

		LatLon cur = new LatLon(mapView.getLatitude(), mapView.getLongitude());
		LatLon latLonToShow = settings.getAndClearMapLocationToShow();
		String mapLabelToShow = settings.getAndClearMapLabelToShow();
		Object toShow = settings.getAndClearObjectToShow();
		// AnimateDraggingMapThread thread =
		// mapView.getAnimatedDraggingThread();
		// thread.startMoving(la, lo, 9, false);
		mapView.refreshMap(true);
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

		if (app.accessibilityEnabled())
//			AccessibleToast
//					.makeText(
//							this,
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
		}
		return super.onKeyDown(keyCode, event);
	}

	private static void print(String msg) {

		android.util.Log.i(TAG, msg);

	}

	private static final String TAG = "FileDownloader";

	private long exitTime = 0;

	public void setMapLocation(double lat, double lon) {
		mapView.setLatLon(lat, lon);
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

	protected void setProgressDlg(Dialog progressDlg) {
		this.progressDlg = progressDlg;
	}

	protected Dialog getProgressDlg() {
		return progressDlg;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OsmandApplication.nflag = true;
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
	}

	public void updateApplicationModeSettings() {
		// update vector renderer
		// print("map activity 1: zoom scale: "+mapView.getSettingsZoomScale());
		mapLayers.updateLayers(mapView, 1);
		mapView.setComplexZoom(mapView.getZoom(),
				mapView.getSettingsZoomScale());
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
		System.err.println("Thread.setDefaultUncaughtExceptionHandler(this); "+arg0+" "+arg1);
	}

}