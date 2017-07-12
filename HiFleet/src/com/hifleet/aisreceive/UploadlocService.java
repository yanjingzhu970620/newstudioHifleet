//package com.hifleet.aisreceive;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.Thread.UncaughtExceptionHandler;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.Executors;
//
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.media.RingtoneManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Binder;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
//import android.os.SystemClock;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.hifleet.aisreceive.TcpUpload;
//import com.hifleet.app.OsmAndLocationProvider;
//import com.hifleet.app.OsmandApplication;
//import com.hifleet.app.OsmandSettings;
//import com.hifleet.app.OsmandSettings.MetricsConstants;
//import com.hifleet.base.AccessibleToast;
//import com.hifleet.base.OnNavigationServiceAlarmReceiver;
//import com.hifleet.helper.SavingTrackHelper;
//import com.hifleet.util.PlatformUtil;
//import com.hifleeteasynavigetion.ais.R;
//
//public class UploadlocService extends Service implements LocationListener,UncaughtExceptionHandler {
//
//	public static class NavigationServiceBinder extends Binder {
//
//	}
//	com.hifleet.data.Location loc;
//	// global id don't conflict with others
//	private final static int NOTIFICATION_SERVICE_ID = 5;
//	public final static String OSMAND_STOP_SERVICE_ACTION = "OSMAND_STOP_SERVICE_ACTION"; //$NON-NLS-1$
//	public final static String NAVIGATION_START_SERVICE_PARAM = "NAVIGATION_START_SERVICE_PARAM";
//
//	public static int USED_BY_GPX = 2;
//	public final static String USAGE_INTENT = "SERVICE_USED_BY";
//
//	private NavigationServiceBinder binder = new NavigationServiceBinder();
//
//	private int serviceOffInterval;
//	private String serviceOffProvider;
//	private int serviceError;
//
//	private OsmandSettings settings;
//	SavingTrackHelper savingtrackhelper;
//	private Handler handler;
//
//	private static WakeLock lockStatic;
//	private PendingIntent pendingIntent;
//	private BroadcastReceiver broadcastReceiver;
//	private boolean startedForNavigation;
//
//	private static Method mStartForeground;
//	private static Method mStopForeground;
//	private static Method mSetForeground;
//	private OsmAndLocationProvider locationProvider;
//	private int usedBy = 0;
//	public void stopIfNeeded(Context ctx, int usageIntent) {
//		usedBy -= usageIntent;
//		if (usedBy == 0) {
//			final Intent serviceIntent = new Intent(ctx,
//					UploadlocService.class);
//			ctx.stopService(serviceIntent);
//		}
//	}
//
//	public void addUsageIntent(int usageIntent) {
//		usedBy |= usageIntent;
//	}
//
//	private void checkForegroundAPI() {
//		System.out.println("开始checkForegroundAPI");
//		// check new API
//		try {
//			mStartForeground = getClass().getMethod("startForeground",
//					new Class[] { int.class, Notification.class });
//			mStopForeground = getClass().getMethod("stopForeground",
//					new Class[] { boolean.class });
//			Log.d(PlatformUtil.TAG,
//					"startForeground and stopForeground available");
//		} catch (NoSuchMethodException e) {
//			mStartForeground = null;
//			mStopForeground = null;
//			Log.d(PlatformUtil.TAG,
//					"startForeground and stopForeground not available");
//		}
//
//		// check old API
//		try {
//			mSetForeground = getClass().getMethod("setForeground",
//					new Class[] { boolean.class });
//			Log.d(PlatformUtil.TAG, "setForeground available");
//		} catch (NoSuchMethodException e) {
//			mSetForeground = null;
//			Log.d(PlatformUtil.TAG, "setForeground not available");
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		System.out.println("开始onBind");
//		return binder;
//	}
//
//	protected synchronized static WakeLock getLock(Context context) {
//		System.out.println("开始getLock");
//		if (lockStatic == null) {
//			PowerManager mgr = (PowerManager) context
//					.getSystemService(Context.POWER_SERVICE);
//			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//					"OsmandServiceLock");
//		}
//		return lockStatic;
//	}
//
//	protected Handler getHandler() {
//		System.out.println("开始getHandler");
//		return handler;
//	}
//
//	public int getServiceError() {
//		System.out.println("开始getServiceError");
//		return serviceError;
//	}
//
//	public int getServiceOffInterval() {
//		System.out.println("开始getServiceOffInterval");
//		return serviceOffInterval;
//	}
//
//	public String getServiceOffProvider() {
//		System.out.println("开始getServiceOffProvider");
//		return serviceOffProvider;
//	}
//
//	public boolean startedForNavigation() {
//		System.out.println("开始startedForNavigation");
//		return startedForNavigation;
//	}
//
//
//
//		          public BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
//		              @Override
//		              public void onReceive(Context context, Intent intent) {
//		            	  System.err.println("熄灭屏幕???"+intent.getAction());
//		                  if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//		                      return;
//		                  }
//		                  System.err.println("熄灭屏幕");
////		                  if (mSensorManager != null) {//取消监听后重写，以保持后台运行
////		                	  if (timerupload != null) {
////		                		  timerupload.cancel();
////		                		  timerupload = null;
////		              		}
////		                	  if (timeruploadtask != null) {
////		                		  timeruploadtask.cancel();
////		                		  timeruploadtask = null;
////		              		}
////
////		                	  setTimerTask();
//
////		                  }
////		                	  System.err.println("setTimerTask finish");
//		              }
//
//		          };
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		System.out.println("开始onStartCommand");
//		handler = new Handler();
////		OsmandApplication app = (OsmandApplication) getApplication();
//		settings = app.getSettings();
////		savingtrackhelper=app.getSavingTrackHelper();
//
////		startedForNavigation = intent.getBooleanExtra(
////				NAVIGATION_START_SERVICE_PARAM, false);
////		if (startedForNavigation) {
//			serviceOffInterval = 0;
////		} else {
////			serviceOffInterval = settings.SERVICE_OFF_INTERVAL.get();// 应该是设置的记录时间间隔。
////		}
////		System.err.println("serviceOffInterval: " + serviceOffInterval);
//		// use only gps provider
//		serviceOffProvider = LocationManager.GPS_PROVIDER;
//		serviceError = serviceOffInterval / 5;
//		// 1. not more than 12 mins
//		serviceError = Math.min(serviceError, 12 * 60 * 1000);
//		// 2. not less than 30 seconds
//		serviceError = Math.max(serviceError, 30 * 1000);
//		// 3. not more than serviceOffInterval
//		serviceError = Math.min(serviceError, serviceOffInterval);
//
//		System.err.println("Service   serviceError: " + serviceError);
//
//		locationProvider = app.getLocationProvider();
//		app.setuplodlocService(this);
//
//		// requesting
////		if (isContinuous()) {
//			System.err.println("连续记录。。。");
//			// request location updates
//			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//			try {
//				locationManager.requestLocationUpdates(serviceOffProvider, 0,
//						0, UploadlocService.this);
//				locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0,
//						0, UploadlocService.this);
//
//				IntentFilter mFilter = new IntentFilter();
//		        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//		        registerReceiver(mReceiver, mFilter);
//			} catch (IllegalArgumentException e) {
//				Toast.makeText(this, R.string.gps_not_available,
//						Toast.LENGTH_LONG).show();
//				Log.d(PlatformUtil.TAG, "GPS location provider not available"); //$NON-NLS-1$
//			}
////		} else {
////			System.err.println("不是连续记录。。。");
////			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
////			pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
////					this, OnNavigationServiceAlarmReceiver.class),
////					PendingIntent.FLAG_UPDATE_CURRENT);
////			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
////					SystemClock.elapsedRealtime() + 500, serviceOffInterval,
////					pendingIntent);
////
////			IntentFilter mFilter = new IntentFilter();
////	        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
////	        registerReceiver(mReceiver, mFilter);
////		}
//
//		// registering icon at top level
//		// Leave icon visible even for navigation for proper display
//		// if (!startedForNavigation) {
//		showNotificationInStatusBar(app);
//		// }
//		return START_REDELIVER_INTENT;
//	}
//
//
//	public void initNotification(String mmsi,boolean sound){
////		System.err.println("initNotification "+mmsi);
//		sound=false;
//		Context context=this;
//		String ns = context.NOTIFICATION_SERVICE;
//		// 获取NotificationManager的引用
//		NotificationManager mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		 NotificationCompat.Builder m_builder = new NotificationCompat.Builder(context);
//		// 创建一个Notification对象
////		int icon = R.drawable.ais_mark;
//
//		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_safe_area);
//		CharSequence tickerText = "Hello alert";
//		long when = System.currentTimeMillis();
////		Notification notification = new Notification(icon, tickerText, when);
//		m_builder.setContentTitle("警告信息");     // 主标题
//        m_builder.setLargeIcon(icon);        //设置大图标
//        m_builder.setContentText(mmsi+"靠近"); //设置主要内容
//        m_builder.setContentInfo("!!!");   //设置内容附加消息
//        m_builder.setSmallIcon(R.drawable.icon_safe_message);        //设置小图标
//        m_builder.setWhen(when);
////        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//设置提示音
//        if(sound){
//        Uri uri = null;
//        File f=new File(app.getAppPath("SOUNDS/")+"alert.mp3");
//        if(!f.exists()){
////        	System.err.println("!!!f.exists"+f.getName());
//        	try {
//				f.createNewFile();
//				 InputStream ins = null;
//					try {
//						ins = context.getAssets().open("alarm.mp3");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if(ins!=null){
//					inputstreamtofile(ins, f);
//			        uri = Uri.fromFile(f);
////			        uri = Uri.parse(view.getApplication().getAppPath("SOUNDS/")+"alert.mp3");
//					}else{
//						uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//					}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }else{
////        	System.err.println("f.exists"+f.getName());
//        	uri=Uri.parse(app.getAppPath("SOUNDS/")+"alert.mp3");
//        }
//
//        m_builder.setSound(uri);
//        }//设置提示音
////        m_builder.setDefaults(Notification.DEFAULT_SOUND);//.flags |= Notification.DEFAULT_ALL;
//        m_builder.setDefaults(Notification.DEFAULT_VIBRATE);
////		// 定义Notification的title、message、和pendingIntent
////		CharSequence contentTitle = "My notification";
////		CharSequence contentText = "Hello World!";
//		Intent notificationIntent = new Intent();
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 1000, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		 m_builder.setContentIntent(contentIntent);
////		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
////		notification.flags |= Notification.FLAG_AUTO_CANCEL;
////		notification.flags |= Notification.FLAG_NO_CLEAR;
////		notification.defaults |= Notification.DEFAULT_SOUND;//声音
////		notification.defaults |= Notification.DEFAULT_VIBRATE;//震动
////		notification.defaults |= Notification.DEFAULT_LIGHTS;//指示灯
//		// 通知状态栏显示Notification
//		final int HELLO_ID = 1000;
//		mNM.notify(HELLO_ID, m_builder.build());
//
//	}
//	public static void inputstreamtofile(InputStream ins,File file) {
//		  try {
//
//
//			   OutputStream os = new FileOutputStream(file);
//			   int bytesWritten = 0;
//			   int byteCount = 0;
//
//			   byte[] b = new byte[1024];
//			   while ((byteCount=ins.read(b)) != -1) {
////				   System.err.println("FileOutputStream write"+b);
//			    os.write(b, bytesWritten, byteCount);
//			   }
//			   os.close();
//			   ins.close();
//			  } catch (Exception e) {
//			   e.printStackTrace();
//			  }
//			 }
//
//	private ConnectivityManager connectivityManager;
//    private NetworkInfo info;
//
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
////                System.out.println( "网络状态已经改变");
//                connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                info = connectivityManager.getActiveNetworkInfo();
//                if(info != null && info.isAvailable()) {
//                    String name = info.getTypeName();
//                    if (settings.SAVE_TRACK_TO_GPX.get()) {
////                    savingtrackhelper.saveDataToGpx();
//                    }
////                    System.out.println(  "当前网络名称：" + name);//联网保存  发送
//
////                    Toast.makeText(context,  "当前网络：" + name, Toast.LENGTH_LONG).show();
//                } else {
//                	 if (settings.SAVE_TRACK_TO_GPX.get()) {
////                         savingtrackhelper.saveDataToGpx();
//                         }
//                	 Toast.makeText(context,  "没有可用网络", Toast.LENGTH_LONG).show();
////                	 System.out.println( "没有可用网络");//断网保存
//                }
//            }
//        }
//    };  //监听网络改变
//	private OsmandApplication app;
//
//	@SuppressWarnings("deprecation")
//	private void showNotificationInStatusBar(OsmandApplication cl) {
//		broadcastReceiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				System.err.println("UploadlocService stop");
////				UploadlocService.this.stopSelf();//不让用户取消
//			}
//
//		};
//		registerReceiver(broadcastReceiver, new IntentFilter());
//		Intent notificationIntent = new Intent(OSMAND_STOP_SERVICE_ACTION);
////		Intent notificationIntent = new Intent(this,app.getMapActivity().getClass());
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//		Notification notification = new Notification(R.drawable.msaicon, "", //$NON-NLS-1$
//				System.currentTimeMillis());
//		notification.flags = Notification.FLAG_ONGOING_EVENT;
//		notification.setLatestEventInfo(this, R.string.app_name+"",
//				getString(R.string.service_stop_background_service),
//				pendingIntent);
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		if (mStartForeground != null) {
//			Log.d(PlatformUtil.TAG, "invoke startForeground");
//			try {
//				mStartForeground.invoke(this, NOTIFICATION_SERVICE_ID,
//						notification);
//			} catch (InvocationTargetException e) {
//				Log.d(PlatformUtil.TAG, "invoke startForeground failed");
//			} catch (IllegalAccessException e) {
//				Log.d(PlatformUtil.TAG, "invoke startForeground failed");
//			}
//		} else {
//			Log.d(PlatformUtil.TAG, "invoke setForeground");
//			mNotificationManager.notify(NOTIFICATION_SERVICE_ID, notification);
//			try {
//				mSetForeground.invoke(this, Boolean.TRUE);
//			} catch (InvocationTargetException e) {
//				Log.d(PlatformUtil.TAG, "invoke setForeground failed");
//			} catch (IllegalAccessException e) {
//				Log.d(PlatformUtil.TAG, "invoke setForeground failed");
//			}
//		}
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		// initializing variables
//		System.out.println("开始onCreate uploadlocservice");
//		app = (OsmandApplication) getApplication();
//		settings = app.getSettings();
//		Thread.setDefaultUncaughtExceptionHandler(this);
//		setTimerTask();
//
//		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);// 屏幕熄掉后依然运行
//		filter.addAction(Intent.ACTION_SCREEN_OFF);
//		registerReceiver(mScreenReceiver, filter);
//		checkForegroundAPI();
//	}
//
//	 public  Timer timerupload = new Timer();
//	 public TimerTask timeruploadtask;
//	 Handler uploadhandler = new Handler() {
//	     @Override
//	     public void handleMessage(Message msg) {
//	         // TODO Auto-generated method stub
//	         // 要做的事情
//	         super.handleMessage(msg);
////	         System.err.println("service handleMessage");
//	         AisUploadconnThread uploc = new AisUploadconnThread();
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					uploc.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					uploc.execute();
//				}
//	     }
//	 };
//
//	 private void setTimerTask(){
//
//	 timeruploadtask = new TimerTask() {
//		    @Override
//		    public void run() {
//		        // TODO Auto-generated method stub
//		        Message message = new Message();
//		        message.what = 1;
//		        uploadhandler.sendMessage(message);
//		    }
//		};
//	 timerupload.schedule(timeruploadtask, 1000, 30*1000);
//	 }
//
//	 class AisUploadconnThread extends AsyncTask<String, Void, Void> {
//			/*
//			 * (non-Javadoc)
//			 *
//			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
//			 */
////			String ip;
////			int port;
////			public AisconnThread(String ip,int port){
////				this.ip=ip;
////				this.port=port;
////			}
//			@Override
//			protected Void doInBackground(String... params) {
//				// TODO Auto-generated method stub
//				String mmsi=settings.SHIP_MMSI.get();
//	 			String name=settings.SHIP_NAME.get();
//	 			String length=settings.SHIP_LENGTH.get();
//	 			String width=settings.SHIP_WIDTH.get();
//	 			String type=settings.SHIP_TYPE.get();
//
////	 			if(app!=null&&app.getMapActivity()!=null&&app.getMapActivity().lon!=null&&app.getMapActivity().lat!=null
////	 					&&app.getMapActivity().speed!=null&&app.getMapActivity().course!=null){
//	 			if(app!=null&&loc==null){
//                loc=app.getLocationProvider().getLastKnownLocation();
//	 			}
//	 			if(loc!=null){
//	 			String lon=String.valueOf(loc.getLongitude());//app.getMapActivity().lon;
//	 			String lat=String.valueOf(loc.getLatitude());//app.getMapActivity().lat;
//	 			String speed=//app.getMapActivity().speed;
//	 		 			getFormattedSpeed(loc.getSpeed(),app);
//	 			String co=String.valueOf(loc.getBearing());//app.getMapActivity().course;
//
//	 			String con="&NSRVAIS,"+mmsi+","+name+","+lon+","+lat+","+speed+","+co+","+length+","+width+","+type
////	 					+sdf.format(new Date(System.currentTimeMillis()))
//	 					+"*END";
////	 			initNotification( "999999",true);
//	 			System.err.println("upload loc con"+con+" type "+type);
//	 			if(lon!=null&&lat!=null&&speed!=null&&co!=null){
//	 				String murl="http://www.hifleet.com/uploadpos.do?imei="+settings.getPhoneIMEINumber() +
//	 						"&lon="+lon+"&lat="+lat +
//	 						"&speed="+speed +
//	 						"&course="+co;
//
//	 				URL url = null;
//					try {
//						url = new URL(murl);
//					} catch (MalformedURLException e2) {
//						// TODO Auto-generated catch block
//						e2.printStackTrace();
//					}
//
//					if (this.isCancelled()) {
//						// print("线程取消 2 .");
//						return null;
//					}
//
//					HttpURLConnection conn = null;
//					try {
//						conn = (HttpURLConnection) url.openConnection();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					conn.setConnectTimeout(10000);
//
//					try {
//						InputStream inStream = conn.getInputStream();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
////	 			System.err.println("uploadlocservice TcpUpload");
//	 			}
//	 			}else{System.err.println("lat lon null");}
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(Void result) {
//				// TODO Auto-generated method stub
//				super.onPostExecute(result);
//			}
//		}
//	 public static String getFormattedSpeed(float metersperseconds, OsmandApplication ctx) {
//			OsmandSettings settings = ctx.getSettings();
//			MetricsConstants mc = MetricsConstants.KILOMETERS_AND_METERS;//settings.METRIC_SYSTEM.get();
//			//ApplicationMode am = settings.getApplicationMode();
//			float kmh = metersperseconds * 3.6f;
//			if (mc == MetricsConstants.KILOMETERS_AND_METERS) {
//				if (kmh >= 10/* || am.hasFastSpeed()*/) {
//					// case of car 以下一行是原版	 yang chun
//					//return ((int) Math.round(kmh)) + " " + ctx.getString(R.string.km_h);
//					double rmk = kmh/1.852;
//					return ""+((int) Math.round(rmk));// + " " + ctx.getString(R.string.knots);
//				}
//
//				//以下三行是原版 yang chun
//				//int kmh10 = (int) (kmh * 10f);
//				//// calculate 2.0 km/h instead of 2 km/h in order to not stress UI text lengh
//				//return (kmh10 / 10f) + " " + ctx.getString(R.string.km_h);
//
//				int kmh10 = (int)(kmh*10f);
//				double rmk = kmh10/1.852;
//				rmk = rmk/10f;
//				String s = String.format("%.1f",rmk);
//				return s;//+ " " + ctx.getString(R.string.knots);
//			}
//			return metersperseconds+"";
//		}
//
//	private boolean isContinuous() {
//		return serviceOffInterval == 0;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		System.out.println("开始onDestory service");
//		((OsmandApplication) getApplication()).setuplodlocService(null);
//
//		// remove updates
//		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		locationManager.removeUpdates(this);
//
//		if (!isContinuous()) {
//			WakeLock lock = getLock(this);
//			if (lock.isHeld()) {
//				lock.release();
//			}
//		}
//		// remove alarm
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		alarmManager.cancel(pendingIntent);
//		// remove notification
//		removeNotification();
//	}
//
//	private void removeNotification() {
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		mNotificationManager.cancel(NOTIFICATION_SERVICE_ID);
//		if (broadcastReceiver != null) {
//			unregisterReceiver(broadcastReceiver);
//			broadcastReceiver = null;
//		}
//		if (mReceiver != null) {
//			unregisterReceiver(mReceiver);
//			mReceiver = null;
//		}
//
//		if (mStopForeground != null) {
//			Log.d(PlatformUtil.TAG, "invoke stopForeground");
//			try {
//				mStopForeground.invoke(this, Boolean.TRUE);
//			} catch (InvocationTargetException e) {
//				Log.d(PlatformUtil.TAG, "invoke stopForeground failed");
//			} catch (IllegalAccessException e) {
//				Log.d(PlatformUtil.TAG, "invoke stopForeground failed");
//			}
//		} else {
//			Log.d(PlatformUtil.TAG, "invoke setForeground");
//			try {
//				mSetForeground.invoke(this, Boolean.FALSE);
//			} catch (InvocationTargetException e) {
//				Log.d(PlatformUtil.TAG, "invoke setForeground failed");
//			} catch (IllegalAccessException e) {
//				Log.d(PlatformUtil.TAG, "invoke setForeground failed");
//			}
//		}
//	}
//
//	@Override
//	public void onLocationChanged(Location l) {
////		System.out.println("update loc 开始onLocationChanged");
////		 String s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(l.getTime()));
////		 System.err.println("on Location Change in Navigation service. "+l.getLongitude()+", "+l.getLatitude()+"time"+s);
//		if (l != null /* && !settings.MAP_ACTIVITY_ENABLED.get() */) {
//			// System.err
//			// .println("on Location Change in Navigation service jump to if block.");
//			com.hifleet.data.Location location = OsmAndLocationProvider
//					.convertLocation(l, (OsmandApplication) getApplication());
//			// if (!isContinuous()) {
//			// // unregister listener and wait next time
//			// LocationManager locationManager = (LocationManager)
//			// getSystemService(LOCATION_SERVICE);
//			// locationManager.removeUpdates(this);
//			// WakeLock lock = getLock(this);
//			// if (lock.isHeld()) {
//			// lock.release();
//			// }
//			// }
//			//
//			// System.err
//			// .println("locationProvider.setLocationFromService(location, isContinuous()); "
//			// + isContinuous());
//			locationProvider.setLocationFromService(location, isContinuous());//记录轨迹  本项目不需要；
//			loc=location;
////			System.err.println("nav service onLocationChanged 更新时间 ：" + location.getTime());
//		}else{System.err.println("nav service onLocationChanged 更新null");}
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		System.out.println("upload onProviderDisabled");
////		AccessibleToast.makeText(this,
////				getString(R.string.off_router_service_no_gps_available),
////				Toast.LENGTH_LONG).show();
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		System.err.println("upload  status"+status);
//	}
//
//	@Override
//	public void uncaughtException(Thread arg0, Throwable arg1) {
//		// TODO Auto-generated method stub
//		System.err.println("uploadservice Thread.setDefaultUncaughtExceptionHandler(this); "+arg0+" "+arg1);
//	}
//
//}
