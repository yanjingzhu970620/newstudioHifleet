package com.hifleet.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.e.common.widget.UISwitchButton;
import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.activity.AboutUsActivity;
import com.hifleet.activity.DownloadIndexActivity;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.bean.lableBean;
import com.hifleet.bean.loginSession;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.map.AccessibleToast;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;
import com.hifleet.plus.wxapi.WXEntryActivity;
import com.hifleet.thread.UserLogout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * @{# SettingFragment.java Create on 2015年7月8日 下午2:22:04
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
@SuppressLint("ValidFragment")
public class SettingFragment extends Fragment {
	public MapActivity mapactivity;
	private UISwitchButton mIsShowName;
	private UISwitchButton mIsShowDot;
	private TextView mUsername, mAccessControl, mCloseAccount, mLogin;
	private EffectColorRelativeLayout mAboutUs, mIsLogin,mOfflinemap,mclearallmap, mclearhistory,
			marriveShips, mwarriveShips, mlineShips, mareaships,mCustomerqq;

	private String mUser, mRole,mName,mType;
	private Boolean mIsShow;
	private Boolean dIsShow;

	OsmandApplication app;
	Context context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_setting, null);

		context = getActivity();
		app=mapactivity.getMyApplication();
		mIsShowName = (UISwitchButton) view
				.findViewById(R.id.switch_isshow_ship_name);
		mIsShowDot = (UISwitchButton) view.findViewById(R.id.switch_isshow_dot);
		mUsername = (TextView) view.findViewById(R.id.text_user_name);
		mAccessControl = (TextView) view.findViewById(R.id.text_access_control);
		mCloseAccount = (TextView) view.findViewById(R.id.text_close_account);
		mLogin = (TextView) view.findViewById(R.id.text_login);
		mIsLogin = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_register_test);
		mOfflinemap = (EffectColorRelativeLayout) view
				.findViewById(R.id.offlinemap_btn);
		mclearallmap = (EffectColorRelativeLayout) view
				.findViewById(R.id.clearallmap_btn);
		mclearhistory=(EffectColorRelativeLayout) view
				.findViewById(R.id.clearallrecord_btn);
		mIsShowName = (UISwitchButton) view
				.findViewById(R.id.switch_isshow_ship_name);
		mAboutUs = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_about_us);
		mCustomerqq = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_service_qq);
		mUser = app.myPreferences.getString("User", "");
		mName = app.myPreferences.getString("Username", "");
		mType = app.myPreferences.getString("type", "");
		if(mType.equals("0")) {
			mUsername.setText(mUser);
		}else{
			mUsername.setText(mName);
		}
		if (app.isIslogin()) {
//			mUser = app.myPreferences.getString("User", null);
//			mUsername.setText(mUser);
			mLogin.setVisibility(View.GONE);
			mCloseAccount.setVisibility(View.VISIBLE);

			mRole = app.getMyrole();
			if (mRole.equals("antenna")) {
				mAccessControl.setText("基站合作");
			}
			if (mRole.equals("fleet")) {
				mAccessControl.setText("船队监控");
			}
			if (mRole.equals("professional")) {
				mAccessControl.setText("航运管理");
			}
			if (mRole.equals("Beidou")) {
				mAccessControl.setText("全球鹰");
			}
			if (mRole.equals("vvip")) {
				mAccessControl.setText("自选");
			}
			if (mRole.equals("admin")) {
				mAccessControl.setText("管理员");
			}
		} else {
			mUsername.setText("无");
			mAccessControl.setText(" ");
			mLogin.setVisibility(View.VISIBLE);
			mCloseAccount.setVisibility(View.GONE);
		}

		mIsShowName.setChecked(app.isLabeladd());
		mIsShowDot.setChecked(app.myPreferences.getBoolean("isShowdot", false));

		mCloseAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (app.isIslogin()) {
					new AlertDialog.Builder(context)
							.setTitle("提示")
							.setCancelable(false)
							.setMessage("是否确定注销此用户？")
							.setNegativeButton(
									"确定",
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											UserLogout sc = new UserLogout(app);
											if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
												sc.executeOnExecutor(Executors
														.newCachedThreadPool(),
														new String[0]);
											} else {
												sc.execute();
											}
											app.mEditor.putBoolean("IsLogin",
													false);
//											app.mEditor.putString("User", "无");
											app.mEditor.putString("PassWord", "");
											app.mEditor.putString("role", " ");
											app.mEditor.putBoolean(
													"IsShowWave", false);
											app.mEditor.putBoolean(
													"IsShowWind", false);
											app.mEditor.putBoolean(
													"IsShowAirPressure", false);
											app.mEditor.commit();
											mUsername.setText("无");
											mAccessControl.setText(" ");
//											app.setIslogin(false);
//											app.setLoginbean(null);
//											app.setMyrole("1");
//											System.out.println("lougout setmyrole 1");
											mLogin.setVisibility(View.VISIBLE);
											mCloseAccount
													.setVisibility(View.GONE);
										}
									}).setPositiveButton("取消", null).show();
				}
			}
		});

		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, IsLoginActivity.class);
				context.startActivity(intent);
			}
		});

		mIsShowName.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mIsShow = mIsShowName.isChecked();
				app.mEditor.putBoolean("isShowMyTeamName", mIsShow);
				app.mEditor.commit();
				app.setLableadd(mIsShowName.isChecked());
				;
			}
		});

		mIsShowDot.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				dIsShow = mIsShowDot.isChecked();
				app.mEditor.putBoolean("isShowdot", dIsShow);
				app.mEditor.commit();
//				ShipLableLayer.teamlable = true;
				app.getMapActivity().getMapLayers().getShipsInfoLayer().caddedlable = new ArrayList<lableBean>();
				app.getMapActivity().getMapLayers().getShipsInfoLayer().showallships=dIsShow;
				app.getMapActivity().getMapView().refreshMapForceDraw(true, true);
			}
		});

		mAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, AboutUsActivity.class);
				context.startActivity(intent);
			}
		});
		final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=2931491585&version=1";  
		mCustomerqq.setOnClickListener(new View.OnClickListener() {  
		    @Override  
		    public void onClick(View v) {  
		        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));  
		    }  
		});  
		mIsLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, WXEntryActivity.class);
				context.startActivity(intent);
			}
		});

		mOfflinemap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onOfflineMapDataManagerButtonClick(context);
			}
		});
		mclearallmap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				

				new AlertDialog.Builder(context)
						.setTitle("提示")
						.setCancelable(false)
						.setMessage("是否确定清空本地缓存？")
						.setNegativeButton(
								"确定",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										mapactivity.deleteallMaps();
//										ShipsInfoLayer.cleartapships();
									}
									
								}).setPositiveButton("取消", null).show();
			
				
			}
		});
		
		mclearhistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShipsInfoLayer.cleartapships();
				AccessibleToast.makeText(mapactivity, R.string.clear_record_success,
						Toast.LENGTH_LONG).show();
			}
		});
		return view;
	}
	
	
	// 打开下载离线海图包界面�?
		private void onOfflineMapDataManagerButtonClick(Context context) {

			Intent offlineMapDataManageActivity = new Intent(context,DownloadIndexActivity.class);

			offlineMapDataManageActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

//			mapActivity.isUpdateMapResource = false;
//
			context.startActivity(offlineMapDataManageActivity);

		}
		
	class LogoutThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = "http://192.168.1.5:8081/HiFleetWeb/"
						+ IndexConstants.GET_USER_LOGOUT;
				System.out.println(httpPost);
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
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (app.isIslogin()) {
			mUser = app.myPreferences.getString("User", "");
			mName = app.myPreferences.getString("Username", "");
			mType = app.myPreferences.getString("type", "");
			if(mType.equals("0")) {
				mUsername.setText(mUser);
			}else{
				mUsername.setText(mName);
			}
			mLogin.setVisibility(View.GONE);
			mCloseAccount.setVisibility(View.VISIBLE);

			mRole = app.getMyrole();
			if (mRole.equals("ship")) {
				mAccessControl.setText("船舶动态");
			}
			if (mRole.equals("fleet")) {
				mAccessControl.setText("船队监控");
			}
			if (mRole.equals("shipping")) {
				mAccessControl.setText("航运管理");
			}
			if (mRole.equals("vip")) {
				mAccessControl.setText("全球鹰");
			}
			if (mRole.equals("vvip")) {
				mAccessControl.setText("自选");
			}
		} else {
			mUsername.setText("无");
			mAccessControl.setText(" ");
			mLogin.setVisibility(View.VISIBLE);
			mCloseAccount.setVisibility(View.GONE);
		}
	}
	public SettingFragment(MapActivity mapactivity){
		this.mapactivity=mapactivity;
	}
}
