package com.hifleet.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.plus.R;
import com.hifleet.activity.ChooseShipActivity;
import com.hifleet.activity.GetAreaShipsActivity;
import com.hifleet.activity.GetArriveShipsActivity;
import com.hifleet.activity.GetInportShipsActivity;
import com.hifleet.activity.GetWillArriveShipsActivity;
import com.hifleet.activity.LineShipsActivity;
import com.hifleet.activity.PortShipsActivity;
import com.hifleet.activity.SearchActivity;
import com.hifleet.activity.ShipInfoActivity;
import com.hifleet.bean.AlertBeans;
import com.hifleet.bean.InportShipsBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.MapActivity1;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.MyTeamShipsThread;
import com.hifleet.utility.XmlParseUtility;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * @{# WeatherStationsAdapter.java Create on 2015年5月26日 下午6:50:23
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class AlertMessageAdapter extends BaseAdapter {
	private Context context;
//	private List<InportShipsBean> mShipBean;
	public List<AlertBeans> alertBeans = new ArrayList<AlertBeans>();
	OsmandApplication app;
	String mPortName;
	String leavetime;
	String arrivetime;
	String from;
	String alerttype;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public AlertMessageAdapter(Context context,String from,String alerttype, List<AlertBeans> alertbean) {
		this.context = context;
		this.alertBeans = alertbean;
		this.from=from;
		this.alerttype=alerttype;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return alertBeans.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return alertBeans.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_simple_detail, null);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.text_ship_name);
		TextView mmsiText = (TextView) convertView
				.findViewById(R.id.text_ship_mmsi);
		TextView imoText = (TextView) convertView
				.findViewById(R.id.text_ship_imo);
		TextView callNumberText = (TextView) convertView
				.findViewById(R.id.text_ship_call_number);
		EffectColorRelativeLayout weatherStation = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_details);
		final AlertBeans m = alertBeans.get(position);
//		System.err.println("alertmsg adapter getview"+m.getReporteta());
		String reporteta = "",reportetaport = "";
		
		if(from.equals("area")){
			if(alerttype.equals("M")){
				if(m.getReporteta().contains("(")){
				String port=m.getReporteta().replace("(", ",");
			    port=port.replace(")", "");
//				System.err.println("alertmsg adapter getview"+port);
				String etaport[]=port.split(",");
//				System.err.println("alertmsg adapter getview"+etaport.length);
				if(etaport!=null&&etaport.length>1){
				reporteta=etaport[0];
//				System.err.println("alertmsg adapter getview"+reporteta);
				reportetaport=etaport[1];
//				System.err.println("alertmsg adapter getview"+reportetaport);
				}
				}
				nameText.setText("船旗：" + m.getDn()+"\n船名：" + m.getShipname()+"\n船报目的港:"+reportetaport);
			}else{
			nameText.setText("船旗：" + m.Dn+"    航速:"+m.ShipSpeed);
			}
		}else{
		nameText.setText("类型：" + m.getAlertType()+"    航速:"+m.ShipSpeed);
		}
		if(from.equals("area")){
			if(alerttype.equals("M")){
				mmsiText.setText("船报ETA：" + reporteta);
			}else{
			mmsiText.setText("船名：" + m.ShipName);
			}
		}else{
		mmsiText.setText("区域：" + m.AlertAreaName);
		}
//		mmsiText.setText("MMSI：" + m.mmsi);
//		imoText.setText("船舶类型：" + m.type);
		if(alerttype.equals("M")){
			imoText.setText("本港计算ETA：" + m.getCaleta());
		}else{
		imoText.setText("时间：" + m.TriggerTime);
		}
		
		if(alerttype.equals("M")){
			SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat s1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date ctime = null ;
			try {
				ctime = s.parse(m.getCaleta());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			Date rtime = null ;
			try {
				rtime = s1.parse(reporteta);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				
			}
			if(ctime==null||rtime==null){
				if(ctime==null){
					imoText.setText("本港计算ETA：-");
				}
				callNumberText.setText("时间差：-");
			}else{
			long dtime=ctime.getTime()-rtime.getTime();
			if(dtime>15){
				callNumberText.setTextColor(Color.RED);
			}else if(dtime<-15){
				callNumberText.setTextColor(Color.BLUE);
			}else{
				callNumberText.setTextColor(Color.GREEN);
			}
			dtime=Math.abs(dtime);
//			if(Math.abs(ctime.getDay()-rtime.getDay())>1){
//				t=t+Math.abs(ctime.getDay()-rtime.getDay())+"天";
//				if(Math.abs(ctime.getHours()-rtime.getHours())>1){
//					t=t+Math.abs(ctime.getHours()-rtime.getHours())+"小时";
//					if(Math.abs(ctime.getMinutes()-rtime.getMinutes())>1){
//						t=t+Math.abs(ctime.getMinutes()-rtime.getMinutes())+"分钟";
//					}
//				}
//			}else if(Math.abs(ctime.getHours()-rtime.getHours())>1){
//				t=t+Math.abs(ctime.getHours()-rtime.getHours())+"小时";
//				if(Math.abs(ctime.getMinutes()-rtime.getMinutes())>1){
//					t=t+Math.abs(ctime.getMinutes()-rtime.getMinutes())+"分钟";
//				}
//			}else{
//				if(Math.abs(ctime.getMinutes()-rtime.getMinutes())>1){
//					t=t+Math.abs(ctime.getMinutes()-rtime.getMinutes())+"分钟";
//				}
//			}
			String leave = "";
			// 计算相差的月份
			long month = (long) Math.floor(dtime
					/(24 * 3600 * 1000)/30);
			long leave0 = dtime % (30 * 24 * 3600 * 1000); // 计算月数后剩余的毫秒数
			// 计算出相差天数
			long days = (long) Math.floor(leave0
					/ (24 * 3600 * 1000));
			// 计算出小时数
			long leave1 = dtime % (24 * 3600 * 1000); // 计算天数后剩余的毫秒数
			long hours = (long) Math.floor(leave1
					/ (3600 * 1000));
			// 计算相差分钟数
			long leave2 = leave1 % (3600 * 1000); // 计算小时数后剩余的毫秒数
			long minutes = (long) Math.floor(leave2
					/ (60 * 1000));
			// 计算相差秒数
			long leave3 = leave2 % (60 * 1000); // 计算分钟数后剩余的毫秒数
			long seconds = Math.round(leave3 / 1000);
			if (month != 0) {
				long days1 = Math.round(leave0
						/ (24 * 3600 * 1000));
				leave = month + "月" + days1 + "日";
			} else {
				if (days != 0) {
					long hours1 = Math.round(leave1
							/ (3600 * 1000));
					leave = days + "日" + hours1 + "时";
				} else {
					if (hours != 0) {
						long minutes1 = Math.round(leave2
								/ (60 * 1000));
						leave = hours + "时" + minutes1 + "分";
					} else {
						if (minutes != 0) {
							leave = minutes + "分" + seconds
									+ "秒";
						} else {
							leave = seconds + "秒";
						}
					}
				}
			}
//				if(dtime/1000/60/60>1){
//					if(dtime/1000/60/60/24>1){
//						t=Math.floor(dtime/1000/60/60/24)+"天"+(dtime-Math.floor(dtime/1000/60/60/24)*24*60*60*1000)/1000/60/60+"小时";	
//					}else{
//					t=Math.floor(dtime/1000/60/60)+"小时"+(dtime-Math.floor(dtime/1000/60/60)*60*60*1000)/1000/60+"分钟";
//					}
//				}else{
//					t=dtime/1000/60+"分钟";	
//				}
				
				

			callNumberText.setText("时间差："+ leave.replace("日", "天"));
			}
			
		}else{
		if(m.Position!=null&&m.Position.contains("POINT(")){
			String p=m.Position.replace("POINT(", "");
		    p=p.replace(")", "");
		    String[] l=p.split(" ");
		    double lon=Double.valueOf(l[0]);
		    double lat=Double.valueOf(l[1]);
		callNumberText.setText("位置：" + getStringLon(lon)+","+getStringLat(lat));
		}else{
			callNumberText.setText("位置：" + m.Position);
		}
		}
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(alerttype.equals("M")){return;}
				Intent intent = new Intent(context,
						MapActivity1.class);
				Bundle bundle = new Bundle();
				
				String p=m.Position.replace("POINT(", "");
			    p=p.replace(")", "");
			    String[] l=p.split(" ");
			    String slat=l[1];
			    String slon=l[0];
			    if(slat!=null&&slon!=null&&!slat.equals("")&&!slon.equals("")){
				bundle.putString("la", slat);
				bundle.putString("lo", slon);
				bundle.putString("needlayer", "alert");
				intent.putExtras(bundle);
				context.startActivity(intent);
			    }
			}
			
		});
		return convertView;
	}
	
	
	public String getStringLon(double lon){
		String getlon="";
		String sign="";
		if(lon<0){
//			getlat= Math.abs(lat);
			sign="W";
		}else{
//			getlat=lat;
			sign="E";
		}
		double l=Math.abs(lon);
		int d=(int)Math.floor(l);
		if (d < 10) {
			getlon ="00" + d +"°";
		} else if (d > 10 && d < 100) {
			getlon ="0" + d +"°";
		} else {
			getlon ="" + d +"°";
		}
		double m=(l-d)*60;
		int m1=(int)Math.floor(m);
		if(m<10){
			getlon=getlon +"0"+ m1+ "'";
		}else{
			getlon=getlon + m1 + "'";
		}
		double s=m-m1;
		java.text.DecimalFormat dft = new java.text.DecimalFormat(".###");
		// double ll = l / 60.0;
		getlon=getlon + dft.format(s);
//		System.err.println("lon："+lon+" String lon:"+getlon);
		return getlon+sign;
		
	}
	
public String getStringLat(double lat){
		
		String getlat="";
		String sign="";
		if(lat<0){
//			getlat= Math.abs(lat);
			sign="S";
		}else{
//			getlat=lat;
			sign="N";
		}
		double l=Math.abs(lat);
		int d=(int)Math.floor(l);
//		System.err.println("l："+l+" d:"+d);
		if (d < 10) {
			getlat ="0" + d +"°";
		} else if (d > 10 && d < 100) {
			getlat ="" + d +"°";
		} else {
			getlat ="" + d +"°";
		}
		double m=(l-d)*60;
		int m1=(int)Math.floor(m);
//		System.err.println("m："+m+" m1:"+m1);
		if(m1<10){
			getlat=getlat +"0"+ m1+ "'";
		}else{
			getlat=getlat + m1 + "'";
		}
		double s=m-m1;
		java.text.DecimalFormat dft = new java.text.DecimalFormat(".###");
		// double ll = l / 60.0;
		getlat=getlat + dft.format(s);
//		System.err.println("lat："+lat+" String lat:"+getlat);
		return getlat+sign;
		
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
	
}
