package com.hifleet.thread;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * @{# UserLogout.java Create on 2015年6月26日 下午3:42:41
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class UserLogout extends AsyncTask< String, Void, Void> {

	OsmandApplication app;

	public UserLogout(OsmandApplication app){
          this.app=app;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			System.out.println("llllllllllllll");
			String httpPost = app.myPreferences.getString("loginserver", null)
					+ IndexConstants.GET_USER_LOGOUT;
			URL shipsUrl = new URL(httpPost);
			HttpURLConnection conn = (HttpURLConnection) shipsUrl
					.openConnection();
			try {
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
			} catch (java.lang.RuntimeException re) {
				re.printStackTrace();
				return null;
			}
			conn.setConnectTimeout(10000);
			InputStream inStream = conn.getInputStream();
			inStream.close();
//			app.setIslogin(false);
//			app.setLoginbean(null);
		} catch (Exception e) {
			// TODO: handle exception
			// System.out.println("未能获取网络数据");
			e.printStackTrace();
		}

		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if(app.isIslogin()){
			app.setIslogin(false);
			app.setLoginbean(null);
			app.setMyrole("1");
			System.out.println("lougout setmyrole 1");
		}else{

			if (app.myPreferences.getBoolean("IsLogin", false)) {
				String mPassWord = app.myPreferences.getString("PassWord", null);
				String mUserName = app.myPreferences.getString("User", null);
				EmailLoginThread emailloginthread = new EmailLoginThread(app,mUserName,mPassWord,app.getLoginActivity());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					emailloginthread.executeOnExecutor(
							Executors.newCachedThreadPool(), new String[0]);
				} else {
					emailloginthread.execute();
				}
			} else {
				Intent intent = new Intent(app.getLoginActivity(), MapActivity.class);
				app.getLoginActivity().startActivity(intent);
				app.getLoginActivity().finish();
//				new Thread(new LoginActivity.MyThread()).start();
			}

		}
	}
}
