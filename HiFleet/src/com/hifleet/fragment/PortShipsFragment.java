package com.hifleet.fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.e.common.widget.UISwitchButton;
import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.plus.R;
import com.hifleet.activity.AboutUsActivity;
import com.hifleet.activity.GetAreaShipsActivity;
import com.hifleet.activity.GetArriveShipsActivity;
import com.hifleet.activity.GetInportShipsActivity;
import com.hifleet.activity.GetWillArriveShipsActivity;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.LineShipsActivity;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;

/**
 * @{# SettingFragment.java Create on 2015年7月8日 下午2:22:04
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
@SuppressLint("ValidFragment")
public class PortShipsFragment extends Fragment {
	MapActivity mapactivity;
	private UISwitchButton mIsShowName;
	private UISwitchButton mIsShowDot;
	private TextView mUsername, mAccessControl, mCloseAccount;
	private EffectColorRelativeLayout mAboutOur, mIsLogin,mports,marriveShips,mwarriveShips,mlineShips,mareaships;

	private String mUser, mRole;
	private Boolean mIsShow;
	private Boolean dIsShow;

	OsmandApplication app;
	Context context;
	View view;

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
		View view = inflater.inflate(R.layout.fragment_myportships, null);
        this.view=view;
        context=getActivity();
		app=mapactivity.getMyApplication();
		mports = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_ports);
		marriveShips = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_arriveports);
		mwarriveShips = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_willarriveports);
		mlineShips = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_lineships);
		mareaships = (EffectColorRelativeLayout) view
				.findViewById(R.id.effect_areaships);
		
		mports.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!app.isIslogin()) {
					Intent intent = new Intent(context, IsLoginActivity.class);
					context.startActivity(intent);
					((MapActivity) context).overridePendingTransition(
							R.drawable.activity_open, 0);
					return;
				}
				if (app.getMyrole().equals("shipping")||
						app.getMyrole().equals("vip")||
						app.getMyrole().equals("admin")||(app.getMyrole().equals("vvip")&&app.getLoginbean().getPortship().equals("1"))) {
					Intent intent = new Intent(context, GetInportShipsActivity.class);
					app.mEditor.putString("portships", "GetInportShipsActivity");
					app.mEditor.commit();
					context.startActivity(intent);	
				}else{
					System.out.println("GetInportShipsActivity权限不足"+app.getMyrole());
				new AlertDialog.Builder(context).setTitle("提示")
				.setCancelable(false).setMessage("请注册登录或升级用户权限！")
				.setNegativeButton("确定", null).show();
				}
				//暂时跳转   测试port搜索。
			}
		});
		marriveShips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!app.isIslogin()) {
					Intent intent = new Intent(context, IsLoginActivity.class);
					context.startActivity(intent);
					((MapActivity) context).overridePendingTransition(
							R.drawable.activity_open, 0);
					return;
				}
				if (app.getMyrole().equals("shipping")||
						app.getMyrole().equals("vip")||
						app.getMyrole().equals("admin")||(app.getMyrole().equals("vvip")&&app.getLoginbean().getPortship().equals("1"))) {
				Intent intent = new Intent(context, GetArriveShipsActivity.class);
				app.mEditor.putString("portships", "GetArriveShipsActivity");
				app.mEditor.commit();
				context.startActivity(intent);
				}else{
					System.out.println("GetArriveShipsActivity权限不足");
					new AlertDialog.Builder(context).setTitle("提示")
					.setCancelable(false).setMessage("请注册登录或升级用户权限！")
					.setNegativeButton("确定", null).show();
					}
				//暂时跳转   测试port搜索。
			}
		});
		mwarriveShips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!app.isIslogin()) {
					Intent intent = new Intent(context, IsLoginActivity.class);
					context.startActivity(intent);
					((MapActivity) context).overridePendingTransition(
							R.drawable.activity_open, 0);
					return;
				}
				if (app.getMyrole().equals("shipping")||
						app.getMyrole().equals("vip")||
						app.getMyrole().equals("admin")||(app.getMyrole().equals("vvip")&&app.getLoginbean().getPortship().equals("1"))) {
				Intent intent = new Intent(context, GetWillArriveShipsActivity.class);
				app.mEditor.putString("portships", "GetWillArriveShipsActivity");
				app.mEditor.commit();
				context.startActivity(intent);
			}else{
				System.out.println("GetWillArriveShipsActivity权限不足");
				new AlertDialog.Builder(context).setTitle("提示")
				.setCancelable(false).setMessage("请注册登录或升级用户权限！")
				.setNegativeButton("确定", null).show();
				}
				//暂时跳转   测试port搜索。
			}
		});
		mlineShips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!app.isIslogin()) {
					Intent intent = new Intent(context, IsLoginActivity.class);
					context.startActivity(intent);
					((MapActivity) context).overridePendingTransition(
							R.drawable.activity_open, 0);
					return;
				}
				if (app.isIslogin()) {
					if(!app.getMyrole().equals("vvip")||app.getLoginbean().getRoute().equals("1")){
					intentToLineships();
//					}else if(!app.getMyrole().equals("vvip")){
//						intentToLineships();
					}else{
						new AlertDialog.Builder(context).setTitle("提示")
						.setCancelable(false).setMessage("请升级用户权限！")
						.setNegativeButton("确定", null).show();
						}
			}else{
				System.out.println("LineShipsActivity权限不足");
				new AlertDialog.Builder(context).setTitle("提示")
				.setCancelable(false).setMessage("请注册登录或升级用户权限！")
				.setNegativeButton("确定", null).show();
				}
				//暂时跳转   测试port搜索。
			}
		});
		mareaships.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!app.isIslogin()) {
					Intent intent = new Intent(context, IsLoginActivity.class);
					context.startActivity(intent);
					((MapActivity) context).overridePendingTransition(
							R.drawable.activity_open, 0);
					return;
				}
				if (app.getMyrole().equals("shipping")||
						app.getMyrole().equals("vip")||
						app.getMyrole().equals("admin")||(app.getMyrole().equals("vvip")&&app.getLoginbean().getRegionship().equals("1"))) {
				Intent intent = new Intent(context, GetAreaShipsActivity.class);
				app.mEditor.putString("portships", "GetAreaShipsActivity");
				app.mEditor.commit();
				context.startActivity(intent);
			}else{
				System.out.println("GetAreaShipsActivity权限不足");
				new AlertDialog.Builder(context).setTitle("提示")
				.setCancelable(false).setMessage("请注册登录或升级用户权限！")
				.setNegativeButton("确定", null).show();
				}
			}
		});
		if (!app.isIslogin()) {
//			progress.setVisibility(View.GONE);
			Intent intent = new Intent(context, IsLoginActivity.class);
			context.startActivity(intent);
			((MapActivity) context).overridePendingTransition(
					R.drawable.activity_open, 0);
		} 
			
		return view;
	}
	private void intentToLineships(){
		Intent intent = new Intent(context, LineShipsActivity.class);
		app.mEditor.putString("portships", "LineShipsActivity");
		app.mEditor.commit();
		context.startActivity(intent);
	}
	public PortShipsFragment(MapActivity mapactivity){
		this.mapactivity=mapactivity;
	}
//	@Override
//	public void onResume() {
//		super.onResume();
//		
//			mports = (EffectColorRelativeLayout) view
//					.findViewById(R.id.effect_ports);
//			marriveShips = (EffectColorRelativeLayout) view
//					.findViewById(R.id.effect_arriveports);
//			mwarriveShips = (EffectColorRelativeLayout) view
//					.findViewById(R.id.effect_willarriveports);
//			mlineShips = (EffectColorRelativeLayout) view
//					.findViewById(R.id.effect_lineships);
//			mareaships = (EffectColorRelativeLayout) view
//					.findViewById(R.id.effect_areaships);
//			
//			mports.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (!app.myPreferences.getBoolean("IsLogin", false)) {
//						Intent intent = new Intent(context, IsLoginActivity.class);
//						context.startActivity(intent);
//						((MapActivity) context).overridePendingTransition(
//								R.drawable.activity_open, 0);
//						return;
//					}
//					if (app.myPreferences.getString("role", "1").equals("professional")||
//							app.myPreferences.getString("role", "1").equals("beidou")) {
//						Intent intent = new Intent(context, GetInportShipsActivity.class);
//						app.mEditor.putString("portships", "GetInportShipsActivity");
//						app.mEditor.commit();
//						context.startActivity(intent);	
//					}else{
//						System.out.println("GetInportShipsActivity权限不足");
//					new AlertDialog.Builder(context).setTitle("提示")
//					.setCancelable(false).setMessage("请注册登录或升级用户权限！")
//					.setNegativeButton("确定", null).show();
//					}
//					//暂时跳转   测试port搜索。
//				}
//			});
//			marriveShips.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (!app.myPreferences.getBoolean("IsLogin", false)) {
//						Intent intent = new Intent(context, IsLoginActivity.class);
//						context.startActivity(intent);
//						((MapActivity) context).overridePendingTransition(
//								R.drawable.activity_open, 0);
//						return;
//					}
//					if (app.myPreferences.getString("role", "1").equals("professional")||
//							app.myPreferences.getString("role", "1").equals("beidou")) {
//					Intent intent = new Intent(context, GetArriveShipsActivity.class);
//					app.mEditor.putString("portships", "GetArriveShipsActivity");
//					app.mEditor.commit();
//					context.startActivity(intent);
//					}else{
//						System.out.println("GetArriveShipsActivity权限不足");
//						new AlertDialog.Builder(context).setTitle("提示")
//						.setCancelable(false).setMessage("请注册登录或升级用户权限！")
//						.setNegativeButton("确定", null).show();
//						}
//					//暂时跳转   测试port搜索。
//				}
//			});
//			mwarriveShips.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (!app.myPreferences.getBoolean("IsLogin", false)) {
//						Intent intent = new Intent(context, IsLoginActivity.class);
//						context.startActivity(intent);
//						((MapActivity) context).overridePendingTransition(
//								R.drawable.activity_open, 0);
//						return;
//					}
//					if (app.myPreferences.getString("role", "1").equals("professional")||
//							app.myPreferences.getString("role", "1").equals("beidou")) {
//					Intent intent = new Intent(context, GetWillArriveShipsActivity.class);
//					app.mEditor.putString("portships", "GetWillArriveShipsActivity");
//					app.mEditor.commit();
//					context.startActivity(intent);
//				}else{
//					System.out.println("GetWillArriveShipsActivity权限不足");
//					new AlertDialog.Builder(context).setTitle("提示")
//					.setCancelable(false).setMessage("请注册登录或升级用户权限！")
//					.setNegativeButton("确定", null).show();
//					}
//					//暂时跳转   测试port搜索。
//				}
//			});
//			mlineShips.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (!app.myPreferences.getBoolean("IsLogin", false)) {
//						Intent intent = new Intent(context, IsLoginActivity.class);
//						context.startActivity(intent);
//						((MapActivity) context).overridePendingTransition(
//								R.drawable.activity_open, 0);
//						return;
//					}
//					if (app.myPreferences.getString("role", "1").equals("professional")||
//							app.myPreferences.getString("role", "1").equals("beidou")) {
//					Intent intent = new Intent(context, LineShipsActivity.class);
//					app.mEditor.putString("portships", "LineShipsActivity");
//					app.mEditor.commit();
//					context.startActivity(intent);
//				}else{
//					System.out.println("LineShipsActivity权限不足");
//					new AlertDialog.Builder(context).setTitle("提示")
//					.setCancelable(false).setMessage("请注册登录或升级用户权限！")
//					.setNegativeButton("确定", null).show();
//					}
//					//暂时跳转   测试port搜索。
//				}
//			});
//			mareaships.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (!app.myPreferences.getBoolean("IsLogin", false)) {
//						Intent intent = new Intent(context, IsLoginActivity.class);
//						context.startActivity(intent);
//						((MapActivity) context).overridePendingTransition(
//								R.drawable.activity_open, 0);
//						return;
//					}
//					if (app.myPreferences.getString("role", "1").equals("professional")||
//							app.myPreferences.getString("role", "1").equals("beidou")) {
//					Intent intent = new Intent(context, GetAreaShipsActivity.class);
//					app.mEditor.putString("portships", "GetAreaShipsActivity");
//					app.mEditor.commit();
//					context.startActivity(intent);
//				}else{
//					System.out.println("GetAreaShipsActivity权限不足");
//					new AlertDialog.Builder(context).setTitle("提示")
//					.setCancelable(false).setMessage("请注册登录或升级用户权限！")
//					.setNegativeButton("确定", null).show();
//					}
//					//暂时跳转   测试port搜索。
//				}
//			});
//		
//	}


}
