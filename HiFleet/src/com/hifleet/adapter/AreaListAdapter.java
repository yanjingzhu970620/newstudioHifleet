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
import com.hifleet.activity.AreaAlertTypeActivity;
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
public class AreaListAdapter extends BaseAdapter {
	private Context context;
//	private boolean longclick=false;
	public final static int ADAPTER=10000;
//	private List<ZoneBean> teamShipsBean;
	public List<ZoneBean> zoneBeans = new ArrayList<ZoneBean>();
	ArrayList<String> alertzones=new ArrayList<String>();
	Map<String,String> alerttypebyid=new HashMap<String,String>();
	public String mShipName;
	OsmandApplication app;
//	public static Boolean isMove = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public AreaListAdapter(Context context, List<ZoneBean> zoneBean,ArrayList<String> alertzones,Map<String,String> alerttypebyid) {
		this.context = context;
		this.zoneBeans = zoneBean;
		this.alertzones=alertzones;
		this.alerttypebyid=alerttypebyid;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return zoneBeans.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return zoneBeans.get(position);
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
		try{
		if(zoneBeans!=null&&zoneBeans.size()>position-1&&alertzones.contains(zoneBeans.get(position).getZoneid())){
			alerticon.setVisibility(View.VISIBLE);
			alerticon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, AreaAlertTypeActivity.class);
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					if (zoneBeans.get(position).getName()!=null) {
						bundle.putString("zone", zoneBeans.get(position).getName());
					} 
					bundle.putString("zoneid", zoneBeans.get(position).getZoneid());
					bundle.putString("alerttype", alerttypebyid.get(zoneBeans.get(position).getZoneid()));
					intent.putExtras(bundle);
					context.startActivity(intent);
//					System.err.println("ship have alert message!!!");
				}
			});
		}
		if (zoneBeans.get(position).getName()!=null) {
			shipsText.setText(zoneBeans.get(position).getName());
		}else{
			shipsText.setText(zoneBeans.get(position).getZoneid());
		}
		EffectColorRelativeLayout shipsEffectColorRelativeLayout = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_ships);
		shipsEffectColorRelativeLayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context,
								MapActivity1.class);
						Bundle bundle = new Bundle();
						
						String p=zoneBeans.get(position).getBound();
					    if(p!=null&&!p.equals("")){
						bundle.putString("latlon", p);
						bundle.putString("needlayer", "area");
						bundle.putString("areaname", zoneBeans.get(position).getName());
						intent.putExtras(bundle);
						context.startActivity(intent);
					}
					}
				});
		}catch (java.lang.IndexOutOfBoundsException iobexp){
			System.err.println("IndexOutOfBoundsException arealistadapter::"+iobexp);
		}
		return convertView;
	}

}
