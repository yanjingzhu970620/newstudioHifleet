package com.hifleet.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.common.activity.BaseActivity;
import com.e.common.widget.effect.button.EffectColorButton;
import com.hifleet.bean.LoginBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.plus.R;
import com.hifleet.thread.UserLogout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @{# LoginActivity.java Create on 2015年3月24日 下午3:35:31
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class LoginActivity extends BaseActivity {

	protected OsmandMapTileView mapTileView;
	OsmandApplication app;
	EditText mEditUserName;

	EditText mEditPassWord;

	TextView text_login_server;

	RelativeLayout ll;

	EffectColorButton login;

	RelativeLayout progressLoading;

	private String mUserName, mPassWord, mFlag, mRole;
	private Boolean isJump;

	String mUrl = null;
	private long exitTime = 0;

	int isPass;

	private ArrayList<LoginBean> mLoginList;

	private List<LoginBean> loginBeans = new ArrayList<LoginBean>();
	private List<String> server = new ArrayList<String>();

	public static String sessionid;
	public static String myFleet;
	private String address = "http://www.hifleet.com/";
	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mToast.cancel();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hifleet.activity.HBaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		progressLoading = (RelativeLayout) findViewById(R.id.progress_loading);

		isJump = true;
		app=getMyApplication();

		app.setLoginActivity(this);
		app.mEditor.putString("loginserver", "http://www.hifleet.com/");
//		app.mEditor.putString("loginserver", "http://192.168.10.25:8081/HiFleetWeb/");
		
		app.mEditor.commit();

		if (app.myPreferences.getBoolean("IsLogin", false)) {
			mPassWord = app.myPreferences.getString("PassWord", null);
			mUserName = app.myPreferences.getString("User", null);
			LoginThread loginthread = new LoginThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				loginthread.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				loginthread.execute();
			}
		} else {
			new Thread(new MyThread()).start();
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Builder d=new AlertDialog.Builder(activity);
				d.setTitle("提示")
						.setMessage("账号异常或服务中断！").setNegativeButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								app.setIslogin(false);
								new Thread(new MyThread()).start();
							}
							});
				d.show();		
				
				break;
			}
		}
	};

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();

		if (root.getNodeName().compareTo("result") == 0) {
			
			LoginBean l=new LoginBean();
			l.setEmail(root.getAttribute("email"));
			l.setFlag(root.getAttribute("flag"));
			l.setType(root.getAttribute("type"));
//			l.setFleet(fleet);
			l.setRole(root.getAttribute("role"));
			l.setMsg(root.getAttribute("msg"));
			l.setName(root.getAttribute("name"));
			if(l.getRole().equals("vvip")){
			l.setMap(root.getAttribute("map"));
			l.setSatellitemap(root.getAttribute("satellitemap"));
			l.setChinachart(root.getAttribute("chinachart"));
			l.setGchart(root.getAttribute("gchart"));
			l.setGchartupdate(root.getAttribute("gchartupdate"));
			l.setShipdetail(root.getAttribute("shipdetail"));
			l.setWeather(root.getAttribute("weather"));
			l.setSearchship(root.getAttribute("searchship"));
			l.setTraffic(root.getAttribute("traffic"));
			l.setFleets(root.getAttribute("fleets"));
			l.setRegionalert(root.getAttribute("regionalert"));
			l.setPortship(root.getAttribute("portship"));
			l.setRegionship(root.getAttribute("regionship"));
			l.setRoute(root.getAttribute("route"));
			l.setObservatory(root.getAttribute("observatory"));
			}
			loginBeans.add(l);
//			loginBeans.add(XmlParseUtility.parse(root, LoginBean.class));
		}
	}

	class LoginThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = address + IndexConstants.LOGIN_URL
						+ mUserName + "&password=" + mPassWord;
				System.out.println("login url:::" + httpPost);
				URL loginUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) loginUrl
						.openConnection();
				if (sessionid != null) {

					conn.setRequestProperty("cookie", sessionid);
					// response.encodeURL(sessionid);
				}
				// String cookieval = conn.getHeaderField("set-cookie");
				String cookieval = null;
				Map<String, List<String>> headers = conn.getHeaderFields();
				for (String s : headers.keySet()) {
					List<String> headerslist = (headers.get(s));
					for (String ss : headerslist) {
						System.out.println("login value: " + ss);
						if (ss.contains("JSESSIONID")) {
							cookieval = ss;
						}
					}
				}

				if (cookieval != null) {
					sessionid = cookieval.substring(0, cookieval.indexOf(";"));
					// System.out.println("保存sessionid： "+sessionid);
//					loginSession.setSessionid(sessionid);
//					app.mEditor.putString("sessionid", sessionid);
//					app.mEditor.commit();
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				// System.out.println("inStream=" + inStream);
				parseXMLnew(inStream);
//				cookieval = conn.getHeaderField("set-cookie");

//				System.out.println("cookieval 2 : " + cookieval);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mHandler.removeCallbacks(r);
			for (LoginBean l : loginBeans) {
				System.out.println("login::"+l.getMsg() + " "+l.getFlag()+" "+l.getRole());
				if (l.getFlag().equals("1")) {

					loginSession.setSessionid(sessionid);
					app.mEditor.putString("sessionid", sessionid);
					app.mEditor.commit();

					app.mEditor.putString("role", l.getRole());
					app.mEditor.putString("Username", l.getName());
					app.mEditor.putString("type", l.getType());

					app.setMyrole(l.getRole());
					app.setLoginbean(l);
					app.setIslogin(true);
					
					app.mEditor.putString("isTimeout", "1");
//					System.out.println("login 1::"+l.getMsg() + " "+l.getFlag()+" "+l.getRole());
					app.mEditor.putInt("isAutoLogin", 1);
					app.mEditor.putBoolean("IsLogin", true);
//					System.out.println("login 2::"+l.getMsg() + " "+l.getFlag()+" "+l.getRole());
					app.mEditor.commit();
//					System.out.println("login 3::"+l.getMsg() + " "+l.getFlag()+" "+l.getRole());
//					
//					myFleet = l.getFleet();
					OsmandApplication.nflag = true;
					OsmandApplication.wflag = true;
					Intent intent = new Intent(activity, MapActivity.class);
					startActivity(intent);
					activity.finish();
//					System.out.println(l.getMsg() );
					if (mToast != null) {
						mToast.setText(l.getMsg());
					} else {
						mToast = Toast.makeText(activity, l.getMsg(),
								Toast.LENGTH_LONG);
					}
					mHandler.postDelayed(r, 1000);
					mToast.show();
				} else {
					UserLogout thread = null;
					thread = new UserLogout(app);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
						thread.executeOnExecutor(
								Executors.newCachedThreadPool(),
								new String[0]);
					} else {
						thread.execute();
					}
//					Intent intent = new Intent(activity,
//							LoginActivity.class);
//					startActivity(intent);
//					new Thread(new MyThread()).start();
				}

			}
		}

	}

	public class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					if (isJump) {
						Thread.sleep(2000);// 单位毫秒
						isJump = false;
						Intent intent = new Intent(activity, MapActivity.class);
						startActivity(intent);
						finish();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("LoginActivity 按两次退出。");
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), /* "再按退出程序" */
				getResources().getString(R.string.tap_twice_to_exit),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				UserLogout thread = null;
				thread = new UserLogout(app);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					thread.executeOnExecutor(
							Executors.newCachedThreadPool(),
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
	
	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		app.setLoginActivity(null);
	}
}
