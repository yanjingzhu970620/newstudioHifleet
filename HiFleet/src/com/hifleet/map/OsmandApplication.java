package com.hifleet.map;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.e.common.manager.init.InitManager;
import com.e.common.manager.net.INet;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.LoginActivity;
import com.hifleet.activity.ShowWanningActivity;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.LoginBean;
import com.hifleet.bean.loginSession;
import com.hifleet.plus.R;
import com.hifleet.thread.EmailLoginThread;
import com.hifleet.thread.UserLogout;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.jpush.android.api.JPushInterface;


public class OsmandApplication extends Application implements INet {
    public static final String EXCEPTION_PATH = "exception.log"; //$NON-NLS-1$
    private static final org.apache.commons.logging.Log LOG = PlatformUtil
            .getLog(OsmandApplication.class);

    public static SharedPreferences myPreferences;
    public static SharedPreferences.Editor mEditor;

    ResourceManager resourceManager = null;
    public static boolean wflag = false;
    public static boolean pscfirst = true;
    OsmandSettings osmandSettings = null;
    OsmandApplication app;
    OsmAndAppCustomization appCustomization;
    OsmAndLocationProvider locationProvider;

    // start variables
    private Handler uiHandler;
    private MapActivity mapActivity;
    private MapActivity1 mapActivity1;
//    private MapActivity_Nav mapActivity_Nav;

    public IsLoginActivity getIsLoginActivity() {
        return IsLoginActivity;
    }

    public void setIsLoginActivity(IsLoginActivity isLoginActivity) {
        IsLoginActivity = isLoginActivity;
    }

    private IsLoginActivity IsLoginActivity;

    public LoginActivity getLoginActivity() {
        return LoginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        LoginActivity = loginActivity;
    }

    private LoginActivity LoginActivity;
    private boolean applicationInitializing = false;
    private Locale prefferedLocale = null;
    public static boolean nflag = false;
    private long lasttime;

    private boolean lableadd;
    private boolean islogin = false;
    private String myrole = "1";
    public LoginBean loginbean;

    /**
     * @return the loginbean
     */
    public LoginBean getLoginbean() {
        return loginbean;
    }

    /**
     * @param loginbean the loginbean to set
     */
    public void setLoginbean(LoginBean loginbean) {
//		System.err.println("setloginbean ");
        this.loginbean = loginbean;
//		System.err.println("setloginbean finish"+this.loginbean.email+this.loginbean.traffic);
    }

    /**
     * @return the islogin
     */
    public boolean isIslogin() {
        return islogin;
    }

    /**
     * @param islogin the islogin to set
     */
    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    /**
     * @return the lableadd
     */
    public boolean isLabeladd() {
        return lableadd;
    }

    /**
     * @param lableadd the lableadd to set
     */
    public void setLableadd(boolean lableadd) {
        this.lableadd = lableadd;
    }

    /**
     * @return the myrole
     */
    public String getMyrole() {
        if (myrole.equals("1")) {
            myrole = app.myPreferences.getString("role", "1");
        }
        return myrole;
    }

    /**
     * @param myrole the myrole to set
     */
    public void setMyrole(String myrole) {
//		System.err.println("setmyrole setmyrole");
        this.myrole = myrole;
    }

    public final String[] colorarray = {"00b6ef", "1b0051", "003a79",
            "4eb693", "005e66", "006eb5",
            "97be0b", "504f54", "000527",
            "807f84", "808c9a", "00842b",
            "917dba", "6275b7", "751086",
            "943807", "ad0073", "afaeb3",
            "b58636", "e969a4", "ea4e01",
            "ec6863", "eeeeee", "f59701", "ffef3a"};
    //	public static List<ShipsBean> SearchshipRecord = new ArrayList<ShipsBean>();
//	public static HashMap<String,ShipsBean> SearchshipRecord = new HashMap<String,ShipsBean>();
    public static ArrayList<String> Searchshipkey = new ArrayList<String>();

    //	public void getSearchString(ArrayList<String> s){
//	if(app.SearchshipRecord.size()>0){
//		Set<String> keSet=app.SearchshipRecord.keySet();  
//		for(Iterator<String> iterator = keSet.iterator(); iterator.hasNext();){
//			 String string = iterator.next();  
//			if(app.SearchshipRecord.get(string).cname!=null
//					&&!app.SearchshipRecord.get(string).cname.equals("")
//					&&!app.SearchshipRecord.get(string).cname.equals("null")){
//				
//				s.add(app.SearchshipRecord.get(string).cname);
//			}else if(app.SearchshipRecord.get(string).n!=null
//					&&!app.SearchshipRecord.get(string).n.equals("")
//					&&!app.SearchshipRecord.get(string).n.equals("null")){
//				s.add(app.SearchshipRecord.get(string).n);
//			}else if(app.SearchshipRecord.get(string).getM()!=null
//					&&!app.SearchshipRecord.get(string).getM().equals("")
//					&&!app.SearchshipRecord.get(string).getM().equals("null")){
//				s.add(app.SearchshipRecord.get(string).getM());
//			}
//		}
//	}else{
//	}
//	}
    public boolean saveSearchArray() {
        SharedPreferences sp = getSharedPreferences("hifleet", Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Search_size", Searchshipkey.size()); /*sKey is an array*/

        for (int i = 0; i < Searchshipkey.size(); i++) {
            mEdit1.remove("Search_" + i);
            mEdit1.putString("Search_" + i, Searchshipkey.get(i));
        }

        return mEdit1.commit();
    }

    public void loadSearchArray() {
        SharedPreferences mSharedPreference1 = getSharedPreferences("hifleet", Activity.MODE_PRIVATE);
        Searchshipkey.clear();
        int size = mSharedPreference1.getInt("Search_size", 0);

        for (int i = 0; i < size; i++) {
            Searchshipkey.add(mSharedPreference1.getString("Search_" + i, null));

        }
    }

    public void wxLogin(String code) {
        WxgetUserinfoThread wxlogin = new WxgetUserinfoThread(code);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
            wxlogin.executeOnExecutor(
                    Executors.newCachedThreadPool(), new String[0]);
        } else {
            wxlogin.execute();
        }
    }

    class WxgetUserinfoThread extends AsyncTask<String, Void, Void> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        String code;

        public WxgetUserinfoThread(String code) {
            this.code = code;
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String url = app.myPreferences.getString("loginserver", null)
                        + IndexConstants.GET_WXUSERINFO + code;
                URL heartBeatUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) heartBeatUrl
                        .openConnection();
                if (loginSession.getSessionid() != null) {
                    conn.setRequestProperty("cookie",
                            loginSession.getSessionid());
                } else {
                    conn.setRequestProperty("cookie",
                            app.myPreferences.getString("sessionid", ""));
                }
                conn.setConnectTimeout(5000);
                InputStream inStream = conn.getInputStream();
                parseWxLoginXML(inStream);
                inStream.close();
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("未能获取网络数据");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//			getIsLoginActivity().finish();
        }
    }

    private void parseWxLoginXML(InputStream inStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inStream);
        Element root = document.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        if (root.getNodeName().compareTo("result") == 0) {
            String email = root.getAttribute("email");
            String password = root.getAttribute("password");

            if (getIsLoginActivity() != null) {
                EmailLoginThread emailloginthread = new EmailLoginThread(app, email, password, getIsLoginActivity());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    emailloginthread.executeOnExecutor(
                            Executors.newCachedThreadPool(), new String[0]);
                } else {
                    emailloginthread.execute();
                }
            }
        }
    }

    SQLiteAPI sqliteAPI;

    String mUrl, mStartTime, mEndTime;

    private static Stack<Activity> activityStack;

    private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

    public void setMapActivity(MapActivity activity) {
        mapActivity = activity;
    }

    public void setMapActivity1(MapActivity1 activity1) {
        mapActivity1 = activity1;
    }

//    public void setMapActivity_Nav(MapActivity_Nav activity) {
//        mapActivity_Nav = activity;
//    }
//    public MapActivity_Nav getMapActivity_Nav() {
//        return mapActivity_Nav;
//    }
    public MapActivity getMapActivity() {
        return mapActivity;
    }

    private List<Activity> activityList = new LinkedList<Activity>();
    private static OsmandApplication instance;
    ShowWanningActivity sa = new ShowWanningActivity();
    private IWXAPI api;

    @Override
    public void onCreate() {
        long timeToStart = System.currentTimeMillis();

        super.onCreate();

        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        app = this;
        api = WXAPIFactory.createWXAPI(this, "wxc5bec7b9a9fd4d7a", true);
        api.registerApp("wxc5bec7b9a9fd4d7a");

        InitManager.getInstance().initImageLoaderConfig(this);

        appCustomization = new OsmAndAppCustomization();
        appCustomization.setup(this);

        sqliteAPI = new SQLiteAPIImpl(this);
        // settings used everywhere so they need to be created first
        osmandSettings = appCustomization.createSettings(new SettingsAPIImpl(
                this));
        // always update application mode to default

        applyTheme(this);

        resourceManager = new ResourceManager(this);
        uiHandler = new Handler();

        checkPrefferedLocale();

        locationProvider = new OsmAndLocationProvider(this);

        startApplication();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Time to start application "
                    + (System.currentTimeMillis() - timeToStart)
                    + " ms. Should be less < 800 ms");
        }
        timeToStart = System.currentTimeMillis();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Time to init plugins "
                    + (System.currentTimeMillis() - timeToStart)
                    + " ms. Should be less < 800 ms");
        }
        // 获取DEVICE_ID
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        System.out.println("看看设备识别号====" + DEVICE_ID);
        myPreferences = getSharedPreferences("hifleet", Activity.MODE_PRIVATE);
        mEditor = myPreferences.edit();
        if (myPreferences.getBoolean("IsLogin", false)) {
            new Thread(new MyThread()).start();
        }
        mEditor.putString("DEVICE_ID", DEVICE_ID);
        mEditor.putBoolean("isAppOnForeground1", true);
        mEditor.putBoolean("isAppOnForeground", true);
        mEditor.commit();

        loadSearchArray();//读取搜索记录。
    }

    public static OsmandApplication getInstance() {
        if (null == instance) {
            instance = new OsmandApplication();
        }
        return instance;
    }

    public OsmAndLocationProvider getLocationProvider() {
        return locationProvider;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            new HeartBeatThread().execute();
            super.handleMessage(msg);
        }
    };

    public class MyThread implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(300000);// 线程暂停10秒，单位毫秒
                    if (nflag) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);// 发送消息
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        /**
         * method desc：
         */
        public void start() {
            // TODO Auto-generated method stub

        }
    }

    class HeartBeatThread extends AsyncTask<String, Void, Void> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String url = app.myPreferences.getString("loginserver", null)
                        + IndexConstants.GET_KEEP_HEARTBEAT;
                URL heartBeatUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) heartBeatUrl
                        .openConnection();
                if (loginSession.getSessionid() != null) {
                    conn.setRequestProperty("cookie",
                            loginSession.getSessionid());
                } else {
                    conn.setRequestProperty("cookie",
                            app.myPreferences.getString("sessionid", ""));
                }
                conn.setConnectTimeout(5000);
                InputStream inStream = conn.getInputStream();
                inStream.close();
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("未能获取网络数据");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        UserLogout sc = new UserLogout(this.app);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sc.executeOnExecutor(Executors
                            .newCachedThreadPool(),
                    new String[0]);
        } else {
            sc.execute();
        }
    }

    public OsmAndAppCustomization getAppCustomization() {
        return appCustomization;
    }

    public void setAppCustomization(OsmAndAppCustomization appCustomization) {
        this.appCustomization = appCustomization;
        this.appCustomization.setup(this);
    }

    /**
     * Application settings
     *
     * @return Reference to instance of OsmandSettings
     */
    public OsmandSettings getSettings() {
        if (osmandSettings == null) {
            LOG.error("Trying to access settings before they were created");
        }
        return osmandSettings;
    }

    //
    // public OfflineMapIndexesHelper getOfflinaMapIndexesHelper(){
    // if(offlineMapIndexesHelper==null){
    // offlineMapIndexesHelper = new OfflineMapIndexesHelper(this);
    // }
    // return offlineMapIndexesHelper;
    // }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        resourceManager.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (prefferedLocale != null
                && !newConfig.locale.getLanguage().equals(
                prefferedLocale.getLanguage())) {
            super.onConfigurationChanged(newConfig);
            // ugly fix ! On devices after 4.0 screen is blinking when you
            // rotate device!
            if (Build.VERSION.SDK_INT < 14) {
                newConfig.locale = prefferedLocale;
            }
            getBaseContext().getResources().updateConfiguration(newConfig,
                    getBaseContext().getResources().getDisplayMetrics());
            Locale.setDefault(prefferedLocale);
        } else {
            super.onConfigurationChanged(newConfig);
        }
    }

    public void checkPrefferedLocale() {
        Configuration config = getBaseContext().getResources()
                .getConfiguration();
        String lang = osmandSettings.PREFERRED_LOCALE.get();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            prefferedLocale = new Locale(lang);
            Locale.setDefault(prefferedLocale);
            config.locale = prefferedLocale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        String clang = "".equals(lang) ? config.locale.getLanguage() : lang;
        // resourceManager.getOsmandRegions().setLocale(clang);

    }

    public static final int PROGRESS_DIALOG = 5;

    /**
     * @param activity       that supports onCreateDialog({@link #PROGRESS_DIALOG}) and
     *                       returns @param progressdialog
     * @param progressDialog - it should be exactly the same as onCreateDialog
     * @return
     */
    public void checkApplicationIsBeingInitialized(Activity activity,
                                                   ProgressDialog progressDialog) {
        // start application if it was previously closed
        startApplication();
        // synchronized (OsmandApplication.this) {
        // if (startDialog != null) {
        // try {
        // SpecialPhrases.setLanguage(this, osmandSettings);
        // } catch (IOException e) {
        // LOG.error("I/O exception", e);
        // //Toast error = Toast.makeText(this, "未能读取默认配置文件，可能需要重启本程序�?,
        // // Toast.LENGTH_LONG);
        // //error.show();
        // }
        //
        // progressDialog.setTitle(getString(R.string.loading_data));
        // progressDialog.setMessage(getString(R.string.reading_indexes));
        // activity.showDialog(PROGRESS_DIALOG);
        // startDialog.setDialog(progressDialog);
        // } else if (startingWarnings != null) {
        // //System.out.println("show warnings.");
        // showWarnings(startingWarnings, activity);
        // }
        // }
    }

    // public boolean isApplicationInitializing() {
    // //return startDialog != null;
    // }

    public void initVoiceCommandPlayer(final Activity uiContext) {
        // showDialogInitializingCommandPlayer(uiContext, true, null, false);
    }

    public void showDialogInitializingCommandPlayer(final Activity uiContext,
                                                    boolean warningNoneProvider) {
        // showDialogInitializingCommandPlayer(uiContext, warningNoneProvider,
        // null, true);
    }

    public void showDialogInitializingCommandPlayer(final Activity uiContext,
                                                    boolean warningNoneProvider, Runnable run, boolean showDialog) {
        // String voiceProvider = osmandSettings.VOICE_PROVIDER.get();
        // if (voiceProvider == null ||
        // OsmandSettings.VOICE_PROVIDER_NOT_USE.equals(voiceProvider)) {
        // if (warningNoneProvider && voiceProvider == null) {
        // Builder builder = new AccessibleAlertBuilder(uiContext);
        // LinearLayout ll = new LinearLayout(uiContext);
        // ll.setOrientation(LinearLayout.VERTICAL);
        // final TextView tv = new TextView(uiContext);
        // tv.setPadding(7, 3, 7, 0);
        // tv.setText(R.string.voice_is_not_available_msg);
        // tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
        // ll.addView(tv);
        //
        // final CheckBox cb = new CheckBox(uiContext);
        // cb.setText(R.string.remember_choice);
        // LinearLayout.LayoutParams lp = new
        // LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
        // LayoutParams.WRAP_CONTENT);
        // lp.setMargins(7, 10, 7, 0);
        // cb.setLayoutParams(lp);
        // ll.addView(cb);
        //
        // builder.setCancelable(true);
        // builder.setNegativeButton(R.string.default_buttons_cancel, new
        // DialogInterface.OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // if(cb.isChecked()) {
        // osmandSettings.VOICE_PROVIDER.set(OsmandSettings.VOICE_PROVIDER_NOT_USE);
        // }
        // }
        // });
        // builder.setPositiveButton(R.string.default_buttons_ok, new
        // DialogInterface.OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // Intent intent = new Intent(uiContext, SettingsActivity.class);
        // intent.putExtra(SettingsActivity.INTENT_KEY_SETTINGS_SCREEN,
        // SettingsActivity.SCREEN_NAVIGATION_SETTINGS);
        // uiContext.startActivity(intent);
        // }
        // });
        //
        //
        // builder.setTitle(R.string.voice_is_not_available_title);
        // builder.setView(ll);
        // //builder.setMessage(R.string.voice_is_not_available_msg);
        // builder.show();
        // }
        //
        // } else {
        // if (player == null || !Algorithms.objectEquals(voiceProvider,
        // player.getCurrentVoice())) {
        // initVoiceDataInDifferentThread(uiContext, voiceProvider, run,
        // showDialog);
        // }
        // }

    }

    private void initVoiceDataInDifferentThread(final Activity uiContext,
                                                final String voiceProvider, final Runnable run, boolean showDialog) {
        // final ProgressDialog dlg = showDialog ?
        // ProgressDialog.show(uiContext, getString(R.string.loading_data),
        // getString(R.string.voice_data_initializing)) : null;
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // try {
        // if (player != null) {
        // player.clear();
        // }
        // player = CommandPlayerFactory.createCommandPlayer(voiceProvider,
        // OsmandApplication.this, uiContext);
        // routingHelper.getVoiceRouter().setPlayer(player);
        // if(dlg != null) {
        // dlg.dismiss();
        // }
        // if (run != null && uiContext != null) {
        // uiContext.runOnUiThread(run);
        // }
        // } catch (CommandPlayerException e) {
        // if(dlg != null) {
        // dlg.dismiss();
        // }
        // showWarning(uiContext, e.getError());
        // }
        // }
        // }).start();
    }

    private void fullExit() {
        // http://stackoverflow.com/questions/2092951/how-to-close-android-application
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    public synchronized void closeApplication(final Activity activity) {
        // if (getNavigationService() != null) {
        // Builder bld = new AlertDialog.Builder(activity);
        //
        // bld.setMessage(R.string.background_service_is_enabled_question);
        //
        // bld.setPositiveButton(R.string.default_buttons_yes, new
        // DialogInterface.OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // closeApplicationAnyway(activity, true);
        // }
        // });
        // bld.setNegativeButton(R.string.default_buttons_no, new
        // DialogInterface.OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // closeApplicationAnyway(activity, false);
        // }
        // });
        // bld.show();
        // } else {
        closeApplicationAnyway(activity, true);
        // }
    }

    private void closeApplicationAnyway(final Activity activity,
                                        boolean disableService) {
        if (applicationInitializing) {
            resourceManager.close();
        }
        applicationInitializing = false;

        activity.finish();

        // if (getNavigationService() == null) {
        fullExit();
        // }
        // else if (disableService) {
        // final Intent serviceIntent = new Intent(this,
        // NavigationService.class);
        // stopService(serviceIntent);
        //
        // new Thread(new Runnable() {
        // public void run() {
        // //wait until the service has fully stopped
        // while (getNavigationService() != null) {
        // try {
        // Thread.sleep(100);
        // }
        // catch (InterruptedException e) {
        // }
        // }
        //
        // fullExit();
        // }
        // }).start();
        // }
    }

    public synchronized void startApplication() {
        if (applicationInitializing) {
            return;
        }
        applicationInitializing = true;
        // startDialog = new ProgressDialogImplementation(this, null, false);
        //
        //		startDialog.setRunnable("Initializing app", new Runnable() { //$NON-NLS-1$
        // @Override
        // public void run() {
        // startApplicationBackground();
        // }
        // });
        // startDialog.run();

        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

    }

    private void startApplicationBackground() {
        List<String> warnings = new ArrayList<String>();
        try {
            // if (!Version.isBlackberry(this)) {
            // if (osmandSettings.NATIVE_RENDERING_FAILED.get()) {
            // osmandSettings.SAFE_MODE.set(true);
            // osmandSettings.NATIVE_RENDERING_FAILED.set(false);
            // warnings.add(getString(R.string.native_library_not_supported));
            // } else {
            // osmandSettings.SAFE_MODE.set(false);
            // osmandSettings.NATIVE_RENDERING_FAILED.set(true);
            // startDialog.startTask(getString(R.string.init_native_library),
            // -1);
            // RenderingRulesStorage storage =
            // rendererRegistry.getCurrentSelectedRenderer();
            // boolean initialized = NativeOsmandLibrary.getLibrary(storage,
            // this) != null;
            // osmandSettings.NATIVE_RENDERING_FAILED.set(false);
            // if (!initialized) {
            // LOG.info("Native library could not be loaded!");
            // }
            // }
            // }
            // warnings.addAll(resourceManager.reloadIndexes(startDialog));
            // player = null;
            // if (savingTrackHelper.hasDataToSave()) {
            // startDialog.startTask(getString(R.string.saving_gpx_tracks), -1);
            // try {
            // warnings.addAll(savingTrackHelper.saveDataToGpx());
            // } catch (RuntimeException e) {
            // warnings.add(e.getMessage());
            // }
            // }

            // restore backuped favorites to normal file
            final File appDir = getAppPath(null);
            // File save = new File(appDir, FavouritesDbHelper.FILE_TO_SAVE);
            // File bak = new File(appDir, FavouritesDbHelper.FILE_TO_BACKUP);
            // if (bak.exists() && (!save.exists() || bak.lastModified() >
            // save.lastModified())) {
            // if (save.exists()) {
            // save.delete();
            // }
            // bak.renameTo(save);
            // }
        } catch (RuntimeException e) {
            // System.out.println(e.getMessage());
            // e.printStackTrace();
            warnings.add(e.getMessage());
        } finally {
            synchronized (OsmandApplication.this) {
                final ProgressDialog toDismiss;
                // if (startDialog != null) {
                // toDismiss = startDialog.getDialog();
                // } else {
                // toDismiss = null;
                // }
                // startDialog = null;

                // if (toDismiss != null) {
                // uiHandler.post(new Runnable() {
                // @Override
                // public void run() {
                // if (toDismiss != null) {
                // // TODO handling this dialog is bad, we need a better
                // standard way
                // toDismiss.dismiss();
                // //
                // toDismiss.getOwnerActivity().dismissDialog(PROGRESS_DIALOG);
                // }
                // }
                // });
                // showWarnings(warnings, toDismiss.getContext());
                // } else {
                // startingWarnings = warnings;
                // }
            }
        }
    }

    protected void showWarnings(List<String> warnings, final Context uiContext) {
        if (warnings != null && !warnings.isEmpty()) {
            final StringBuilder b = new StringBuilder();
            boolean f = true;
            for (String w : warnings) {
                // System.out.println("warnings: "+warnings);
                if (f) {
                    f = false;
                } else {
                    b.append('\n');
                }
                b.append(w);
            }
            if (b != null && b.toString().compareTo("null") != 0)
                showWarning(uiContext, b.toString());

        }
    }

    private void showWarning(final Context uiContext, final String b) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                // if(b
                // ==null){System.out.println(" b is null. ");}else{System.out.println("b has content.");}
                // System.out.println("warning: "+b);
                AccessibleToast.makeText(uiContext, b, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private class DefaultExceptionHandler implements UncaughtExceptionHandler {

        private UncaughtExceptionHandler defaultHandler;
        private PendingIntent intent;

        public DefaultExceptionHandler() {
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            intent = PendingIntent.getActivity(OsmandApplication.this
                            .getBaseContext(), 0,
                    new Intent(OsmandApplication.this.getBaseContext(),
                            getAppCustomization().getMapActivity()), 0);
        }

        @Override
        public void uncaughtException(final Thread thread, final Throwable ex) {
            File file = getAppPath(EXCEPTION_PATH);
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(out);
                ex.printStackTrace(printStream);
                StringBuilder msg = new StringBuilder();
                //msg.append("Version  " + Version.getFullVersion(OsmandApplication.this) + "\n"). //$NON-NLS-1$
                // append(DateFormat.format("dd.MM.yyyy h:mm:ss",
                // System.currentTimeMillis()));
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(
                            getPackageName(), 0);
                    if (info != null) {
                        msg.append("\nApk Version : ").append(info.versionName).append(" ").append(info.versionCode); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } catch (Throwable e) {
                }
                msg.append("\n"). //$NON-NLS-1$//$NON-NLS-2$
                        append("Exception occured in thread " + thread.toString() + " : \n"). //$NON-NLS-1$ //$NON-NLS-2$
                        append(new String(out.toByteArray()));

                if (file.getParentFile().canWrite()) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(
                            file, true));
                    writer.write(msg.toString());
                    writer.close();
                }
                // if (routingHelper.isFollowingMode()) {
                // AlarmManager mgr = (AlarmManager)
                // getSystemService(Context.ALARM_SERVICE);
                // mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
                // intent);
                // System.exit(2);
                // }
                defaultHandler.uncaughtException(thread, ex);
            } catch (Exception e) {
                // swallow all exceptions
                android.util.Log.e(PlatformUtil.TAG,
                        "Exception while handle other exception", e); //$NON-NLS-1$
            }

        }
    }

    public void showShortToastMessage(int msgId, Object... args) {
        AccessibleToast.makeText(this, getString(msgId, args),
                Toast.LENGTH_SHORT).show();
    }

    public void showToastMessage(int msgId, Object... args) {
        AccessibleToast.makeText(this, getString(msgId, args),
                Toast.LENGTH_LONG).show();
    }

    public void showToastMessage(String msg) {
        AccessibleToast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void showShortToastMessage(String msg) {
        AccessibleToast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public SQLiteAPI getSQLiteAPI() {
        return sqliteAPI;
    }

    public void runInUIThread(Runnable run) {
        uiHandler.post(run);
    }

    public void runInUIThread(Runnable run, long delay) {
        uiHandler.postDelayed(run, delay);
    }

    public void runMessageInUIThreadAndCancelPrevious(final int messageId,
                                                      final Runnable run, long delay) {
        Message msg = Message.obtain(uiHandler, new Runnable() {

            @Override
            public void run() {
                if (!uiHandler.hasMessages(messageId)) {
                    run.run();
                }
            }
        });
        msg.what = messageId;
        uiHandler.removeMessages(messageId);
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public File getAppPath(String path) {
        if (path == null) {
            path = "";
        }
        return new File(getSettings().getExternalStorageDirectory(),
                IndexConstants.APP_DIR + path);
    }

    public IWXAPI getwxApi() {
        return this.api;
    }

    public String getNewPath(String path) {
        return getSettings().getExternalStorageDirectoryPath();
    }

    public static void print(String msg) {
        android.util.Log.i(TAG, msg);
    }

    private static final String TAG = "FileDownloader";

    public String getOldAppPath(String path) {
        // if(path==null) path="";
        // print("老的路径�?"+getSettings().getBeforeChangeExternalStorageDirectory());
        return (getSettings().getBeforeChangeExternalStorageDirectory());
    }

    public void applyTheme(Context c) {
        int t = R.style.OsmandLightDarkActionBarTheme;

        // if (osmandSettings.OSMAND_THEME.get() ==
        // OsmandSettings.OSMAND_DARK_THEME) {
        // System.err.println("OsmandDarkTheme");
        // t = R.style.OsmandDarkTheme;
        // } else if (osmandSettings.OSMAND_THEME.get() ==
        // OsmandSettings.OSMAND_LIGHT_THEME) {
        // System.err.println("OsmandLightTheme");
        // t = R.style.OsmandLightTheme;
        // } else if (osmandSettings.OSMAND_THEME.get() ==
        // OsmandSettings.OSMAND_LIGHT_DARK_ACTIONBAR_THEME) {
        // System.err.println("OsmandLightDarkActionBarTheme");
        // t = R.style.OsmandLightDarkActionBarTheme;
        // }

        // System.err.println("OsmandLightTheme");
        t = R.style.OsmandLightTheme;

        setLanguage(c);
        c.setTheme(t);

        if (osmandSettings.OSMAND_THEME.get() == OsmandSettings.OSMAND_LIGHT_DARK_ACTIONBAR_THEME
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // ActionBar ab = null;
            // if (c instanceof SherlockActivity) {
            // ab = ((SherlockActivity) c).getSupportActionBar();
            // } else if (c instanceof SherlockListActivity) {
            // ab = ((SherlockListActivity) c).getSupportActionBar();
            // } else if (c instanceof SherlockExpandableListActivity) {
            // ab = ((SherlockExpandableListActivity) c).getSupportActionBar();
            // }
            // if (ab != null) {
            // BitmapDrawable bg = (BitmapDrawable)
            // getResources().getDrawable(R.drawable.bg_striped);
            // bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            // ab.setBackgroundDrawable(bg);
            // }
        }
    }

    public void setLanguage(Context context) {
        if (prefferedLocale != null) {
            Configuration config = context.getResources().getConfiguration();
            String lang = prefferedLocale.getLanguage();
            if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
                prefferedLocale = new Locale(lang);
                Locale.setDefault(prefferedLocale);
                config.locale = prefferedLocale;
                context.getResources().updateConfiguration(config,
                        context.getResources().getDisplayMetrics());
            }
        }
    }

    public boolean accessibilityExtensions() {
        return (Build.VERSION.SDK_INT < 14) ? getSettings().ACCESSIBILITY_EXTENSIONS
                .get() : false;
    }

    public boolean accessibilityEnabled() {
        final AccessibilityMode mode = getSettings().ACCESSIBILITY_MODE.get();
        // if(OsmandPlugin.getEnabledPlugin(AccessibilityPlugin.class) == null)
        // {
        // return false;
        // }
        if (mode == AccessibilityMode.ON) {
            return true;
        } else if (mode == AccessibilityMode.OFF) {
            return false;
        }
        return ((AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE))
                .isEnabled();
    }

    public String getVersionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public int getVersionCode() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.e.common.i.IJsonOperate#isRespSuccess(java.lang.Object)
     */
    @Override
    public boolean isRespSuccess(Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.e.common.i.IJsonOperate#getRespMsg(java.lang.Object)
     */
    @Override
    public String getRespMsg(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.e.common.manager.net.INet#getBaseRequestPath()
     */
    @Override
    public String getBaseRequestPath() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.e.common.manager.net.INet#getBasePicturePath()
     */
    @Override
    public String getBasePicturePath() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.e.common.manager.net.INet#getHeaderInfo()
     */
    @Override
    public HashMap<String, String> getHeaderInfo() {
        // TODO Auto-generated method stub
        return null;
    }
    // public static final void openGPS(Context context) {
    // Intent GPSIntent = new Intent();
    // GPSIntent.setClassName("com.android.settings",
    // "com.android.settings.widget.SettingsAppWidgetProvider");
    // GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
    // GPSIntent.setData(Uri.parse("custom:3"));
    // try {
    // PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
    // } catch (CanceledException e) {
    // e.printStackTrace();
    // }
    // }


}
