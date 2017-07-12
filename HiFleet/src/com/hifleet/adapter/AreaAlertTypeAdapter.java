package com.hifleet.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.plus.R;
import com.hifleet.activity.AreaAlertMessageActivity;
import com.hifleet.activity.MyTeamShipsActivity;
import com.hifleet.activity.TeamShipsAlertMessageActivity;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.ZoneBean;
import com.hifleet.bean.loginSession;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.MapActivity1;
import com.hifleet.map.OsmandApplication;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# ShipsListAdapter.java Create on 2015年4月14日 上午11:14:11
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class AreaAlertTypeAdapter extends BaseAdapter {
	private Context context;
//	private boolean longclick=false;
	public final static int ADAPTER=10000;
//	private List<ZoneBean> teamShipsBean;
//	public List<ZoneBean> zoneBeans = new ArrayList<ZoneBean>();
//	ArrayList<String> alertzones=new ArrayList<String>();
	private String zone;
	private String zoneid;
	private String alerttype;
//	Map<String,String> alerttypebyid=new HashMap<String,String>();
	public String mShipName;
	OsmandApplication app;
//	public static Boolean isMove = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public AreaAlertTypeAdapter(Context context,String alerttype,String zoneid,String zone) {
		this.context = context;
		this.zone = zone;
		this.zoneid=zoneid;
		this.alerttype=alerttype;
	}

	public String getAlertType(String AlertType) {
		if(AlertType!=null){
			String type;
			if(AlertType.equals("A")){
				type="超速";
			}else if(AlertType.equals("B")){
				type="进入";
			}else if(AlertType.equals("C")){
				type="反航向";
			}else if(AlertType.equals("D")){
				type="走锚";
			}else if(AlertType.equals("E")){
				type="靠泊";
			}else if(AlertType.equals("F")){
				type="穿过";
			}else if(AlertType.equals("G")){
				type="低硫区";
			}else if(AlertType.equals("H")){
				type="超速";
			}else if(AlertType.equals("I")){
				type="海浪";
			}else if(AlertType.equals("J")){
				type="走锚";
			}else if(AlertType.equals("M")){
				type="ETA";
			}else{
				type=AlertType;
			}
			return type;
		}else{
		return AlertType;
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return alerttype.split(",").length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
//		System.err.println("adapter alerttype"+alerttype);
		return alerttype.split(",")[position];
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_ships_search, null);
		TextView shipsText = (TextView) convertView
				.findViewById(R.id.text_ships);
		LinearLayout alerticon=(LinearLayout)convertView
				.findViewById(R.id.teamship_alert_png);
//		System.err.println("alertgroup size::"+alertgroup.size());
		if(alerttype.split(",")[position]!=null){
			alerticon.setVisibility(View.VISIBLE);
			alerticon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, AreaAlertMessageActivity.class);
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					if (zone!=null) {
						bundle.putString("ship", zone);
					}
					bundle.putString("mmsi", zoneid);
					bundle.putString("conditonid", alerttype.split(",")[position].split(":")[1]);
//					if(alerttype.split(",")[position].contains("M")){
						bundle.putString("alerttype",alerttype.split(",")[position].split(":")[0]);
//					}else{
//					bundle.putString("alerttype", alerttype.split(",")[position]);
//					}
					intent.putExtras(bundle);
					context.startActivity(intent);
//					System.err.println("ship have alert message!!!");
				}
			});
		}
		if (alerttype.split(",")[position]!=null) {
//			if(alerttype.split(",")[position].contains("M")){
				shipsText.setText(getAlertType(alerttype.split(",")[position].split(":")[0]));
//			}else{
//			shipsText.setText(getAlertType(alerttype.split(",")[position]));
//			}
		}else{
			shipsText.setText("报警信息错误");
		}
		EffectColorRelativeLayout shipsEffectColorRelativeLayout = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_ships);
		shipsEffectColorRelativeLayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, AreaAlertMessageActivity.class);
						Bundle bundle = new Bundle();
						// 传递name参数为tinyphp
						if (zone!=null) {
							bundle.putString("ship", zone);
						}
						bundle.putString("mmsi", zoneid);
						bundle.putString("conditonid", alerttype.split(",")[position].split(":")[1]);
//						if(alerttype.split(",")[position].contains("M")){
							bundle.putString("alerttype",alerttype.split(",")[position].split(":")[0]);
//						}else{
//						bundle.putString("alerttype", alerttype.split(",")[position]);
//						}
						intent.putExtras(bundle);
						context.startActivity(intent);
//						System.err.println("ship have alert message!!!");
					}
				});
		return convertView;
	}

}
