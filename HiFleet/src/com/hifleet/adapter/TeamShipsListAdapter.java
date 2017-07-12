package com.hifleet.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import com.hifleet.activity.MyTeamShipsActivity;
import com.hifleet.activity.TeamShipsAlertMessageActivity;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
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
public class TeamShipsListAdapter extends BaseAdapter {
	private Context context;
//	private boolean longclick=false;
	public final static int ADAPTER=10000;
	private List<ShipsBean> teamShipsBean;
	public List<ShipsBean> teamShipsBeans = new ArrayList<ShipsBean>();
	public static List<ShipsBean> lteamShipsBeans = new ArrayList<ShipsBean>();//长按获取的船舶。
	ArrayList<String> alertships=new ArrayList<String>();
	public String mShipName;
	OsmandApplication app;
	public static Boolean isMove = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public TeamShipsListAdapter(Context context, List<ShipsBean> teamShipsBean,ArrayList<String> alertships) {
		this.context = context;
		this.teamShipsBean = teamShipsBean;
		this.alertships=alertships;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return teamShipsBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return teamShipsBean.get(position);
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
		if(alertships.contains(teamShipsBean.get(position).getM())){
			alerticon.setVisibility(View.VISIBLE);
			alerticon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, TeamShipsAlertMessageActivity.class);
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					if (teamShipsBean.get(position).cname==null||teamShipsBean.get(position).cname.equals("")
							|| teamShipsBean.get(position).cname.equals("N/A")) {
						bundle.putString("ship", teamShipsBean.get(position).n);
					} else {
						bundle.putString("ship", teamShipsBean.get(position).cname);
					}
					bundle.putString("mmsi", teamShipsBean.get(position).getM());
					intent.putExtras(bundle);
					context.startActivity(intent);
//					System.err.println("ship have alert message!!!");
				}
			});
		}
		if (teamShipsBean.get(position).cname==null||teamShipsBean.get(position).cname.equals("")
				|| teamShipsBean.get(position).cname.equals("N/A")) {
			shipsText.setText(teamShipsBean.get(position).n);
		} else {
			shipsText.setText(teamShipsBean.get(position).cname);
		}
		EffectColorRelativeLayout shipsEffectColorRelativeLayout = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_ships);
		shipsEffectColorRelativeLayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (ShipsInfoLayer.teamship != null) {
							ShipsInfoLayer.teamship.clear();
						}
						mShipName = teamShipsBean.get(position).m;

						teamShipsBeans.clear();
						teamShipsBeans.add(teamShipsBean.get(position));
						solveList();
						isMove = true;
						MapActivity.isTeamShipMove = true;
						((MyTeamShipsActivity) context).finish();
//						System.out.println("team listadapter到底有没有具体信息"+teamShipsBean.get(position).n+teamShipsBean.get(position).m+teamShipsBean.get(position).fle);
//						ShipChoosedThread shc = new ShipChoosedThread();
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//							// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//							shc.executeOnExecutor(
//									Executors.newCachedThreadPool(),
//									new String[0]);
//						} else {
//							shc.execute();
//						}
					}
				});
		shipsEffectColorRelativeLayout.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
//				longclick=true;
//				System.out.println("longclick =true fuzhi");
				mShipName = teamShipsBean.get(position).m;
				lteamShipsBeans.clear();
				lteamShipsBeans.add(teamShipsBean.get(position));
				// TODO Auto-generated method stub
//				ShipChoosedThread shc = new ShipChoosedThread();
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					shc.executeOnExecutor(
//							Executors.newCachedThreadPool(),
//							new String[0]);
//				} else {
//					shc.execute();
//				}
				
				return false;
			}
		});
		return convertView;
	}



	class ShipChoosedThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String shipName = mShipName.replace(" ", "%20");
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_CHOOSED_SHIP_URL + shipName;
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
				conn.setConnectTimeout(20000);
				InputStream inStream = conn.getInputStream();
//				if(longclick){
//				parse1shipXMLnew(inStream);
//				}else{
					parseXMLnew(inStream);
					System.out.println("获取的数量："+teamShipsBeans.size());
//				}
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
//			System.out.println("team ship 请求完毕"+longclick);
//			if(longclick){
//				System.out.println("GET SHIP  HANDLER!!");
//				Message m = new Message();
//				m.what = ADAPTER;
//				m.obj = "1";
//				if(MyTeamShipsActivity.inedit)
//				MyTeamShipsActivity.handler.sendMessage(m);
//			}else{
			isMove = true;
			MapActivity.isTeamShipMove = true;
			((MyTeamShipsActivity) context).finish();
//			}
//			longclick=false;
		}
	}//好像不需要   暂时不使用了。。
	

	private void solveList(){
		if (ShipsInfoLayer.tap_ships.size() >= 1) {
			int have = 0;
			for (int at = 0; at < ShipsInfoLayer.alltap_ships.size(); at++) {
				if (ShipsInfoLayer.alltap_ships.get(at).m
						.equals(ShipsInfoLayer.tap_ships.get(0).m)) {
					ShipsInfoLayer.alltap_ships.remove(at);
					ShipsInfoLayer.alltap_ships.add(ShipsInfoLayer.tap_ships.get(0));
					ShipsInfoLayer.tap_ships.clear();
					have++;
					break;
				}
			}
			if (have == 0) {
				ShipsInfoLayer.alltap_ships.add(ShipsInfoLayer.tap_ships.get(0));
				ShipsInfoLayer.tap_ships.clear();
			}
		}
		if(ShipsInfoLayer.teamship.size()>0){
			for(int i1=0;i1<ShipsInfoLayer.teamship.size();i1++){
				boolean have=false;
				for(int i=0;i<ShipsInfoLayer.allteamship.size();i++){
					
						if(ShipsInfoLayer.teamship.get(i1).m.equals(ShipsInfoLayer.allteamship.get(i).m)){
							ShipsInfoLayer.allteamship.remove(i);
							ShipsInfoLayer.allteamship.add(ShipsInfoLayer.teamship.get(i1));
							have=true;
							break;
						}
					}
				if(!have){
					ShipsInfoLayer.allteamship.add(ShipsInfoLayer.teamship.get(i1));
				}
				}
			}
		ShipsInfoLayer.teamship.clear();
		ShipsInfoLayer.teamship.addAll(teamShipsBeans);
//		
//		ShipsInfoLayer.tap_shipsPoint.clear();
//		ShipsInfoLayer.tap_shipsPoint.add(teamShipsBeans.get(0));//记录轨迹点
		
		if(ShipsInfoLayer.searchshipsBeans.size()>0){
			for(int i1=0;i1<ShipsInfoLayer.searchshipsBeans.size();i1++){
				boolean have=false;
				for(int i=0;i<ShipsInfoLayer.allsearchshipsBeans.size();i++){
					
						if(ShipsInfoLayer.searchshipsBeans.get(i1).m.equals(ShipsInfoLayer.allsearchshipsBeans.get(i).m)){
							ShipsInfoLayer.allsearchshipsBeans.remove(i);
							ShipsInfoLayer.allsearchshipsBeans.add(ShipsInfoLayer.searchshipsBeans.get(i1));
							have=true;
							break;
						}
					}
				if(!have){
					ShipsInfoLayer.allsearchshipsBeans.add(ShipsInfoLayer.searchshipsBeans.get(i1));
				}
				}
			ShipsInfoLayer.searchshipsBeans.clear();
			}
	}
//	private void parse1shipXMLnew(InputStream inStream) throws Exception {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document document = builder.parse(inStream);
//		Element root = document.getDocumentElement();
//		NodeList childNodes = root.getChildNodes();
//		for (int j = 0; j < childNodes.getLength(); j++) {
//			Node childNode = (Node) childNodes.item(j);
//			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
//				Element childElement = (Element) childNode;
//				if (childElement.getNodeName().compareTo("ship") == 0) {
//					lteamShipsBeans.add(XmlParseUtility.parse(
//							childElement, ShipsBean.class));
//				}
//			}
//		}
//		
//	}
	
	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		teamShipsBeans.clear();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					teamShipsBeans.add(XmlParseUtility.parse(
							childElement, ShipsBean.class));
				}
			}
		}
		
		
	}

}
