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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
public class PortShipsAdapter extends BaseAdapter {
	private Context context;
	private List<InportShipsBean> mShipBean;
	public static List<ShipsBean> shipsBeans = new ArrayList<ShipsBean>();
	OsmandApplication app;
	String mPortName;
	String leavetime;
	String arrivetime;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public PortShipsAdapter(Context context, List<InportShipsBean> mShipBean) {
		this.context = context;
		this.mShipBean = mShipBean;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mShipBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mShipBean.get(position);
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
		final InportShipsBean m = mShipBean.get(position);
		nameText.setText("船名：" + m.shipname);
		mmsiText.setText("建造年份：" + m.year_of_built+"年");
//		mmsiText.setText("MMSI：" + m.mmsi);
//		imoText.setText("船舶类型：" + m.type);
		imoText.setText("DWT：" + m.dwt+"  t(吨)");
		if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
		callNumberText.setText("更新时间：" + m.updatetime);
		}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
			callNumberText.setText("抵港时间：" + m.triggertime);
		}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
			callNumberText.setText("ETA：" + m.eta);
		}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
			callNumberText.setText("航行时间：" + m.costtime);
		}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
			callNumberText.setText("更新时间：" + m.updatetime);
		}
		weatherStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				System.out.println("点击了portships"+m.shipname+m.ansub+m.mmsi+mShipBean.size());
//				((ChooseShipActivity) context).finish();
				mPortName=m.mmsi;
				shipsBeans.clear();
				if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					SimpleDateFormat sdf1 = new SimpleDateFormat(
							"yyyy-MM-dd-HH-mm-ss");
					try {
						leavetime="0";
						arrivetime="0";
						leavetime=sdf1.format(sdf.parse(m.leavetime));
						arrivetime=sdf1.format(sdf.parse(m.arrivetime));
						if(!leavetime.equals("0")&&!arrivetime.equals("0")){
//							try {
//								Date startDate = sdf1.parse(leavetime);
//								Date endDate = sdf1.parse(arrivetime);
//								Long checkLong = endDate.getTime()
//										- startDate.getTime();
//								if (checkLong < 432000000) {	
//									Intent intent = new Intent(context,
//											MapActivity1.class);
//									Bundle b = new Bundle();
//									b.putString("mmsi", m.mmsi);
////									b.putString("la", shipsBeans.get(0).getLa()+"");
////									b.putString("lo", shipsBeans.get(0).getLo()+"");
//									b.putString("starttime", leavetime);
//									b.putString("endtime", arrivetime);
//									b.putString("from", "LineShipsActivity");
//									b.putString("key", "1");
//									intent.putExtras(b);
//									context.startActivity(intent);
////									closeReqest();
////									String uuid = UUID.randomUUID().toString();
////									ShipChoosedThread sc = new ShipChoosedThread();
////									choosetaskmap.put(uuid, sc);
////									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////										// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
////										sc.executeOnExecutor(
////												Executors.newCachedThreadPool(),
////												new String[0]);
////									} else {
////										sc.execute();
////									}//搜索对应船舶
//								} else {
//									Toast.makeText(context, "时间跨度大于5天，不能查看轨迹", Toast.LENGTH_LONG).show();
//						}
//					  } catch (ParseException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
//					  }
					}else{
						Toast.makeText(context, "获取的时间数据错误，请刷新", Toast.LENGTH_LONG).show();
						return;
					}
//						System.err.println("leavetime::"+leavetime+"arrivetime::"+arrivetime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					Message message = new Message();
					message.what = 000;
					PortShipsActivity.newHandler.sendMessage(message);
				closeReqest();
				String uuid = UUID.randomUUID().toString();
				ShipChoosedThread sc = new ShipChoosedThread();
				choosetaskmap.put(uuid, sc);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					sc.executeOnExecutor(
							Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					sc.execute();
				}//搜索对应船舶
				
				
			}
			
		});
		return convertView;
	}
	
	private HashMap<String, ShipChoosedThread> choosetaskmap = new HashMap<String, ShipChoosedThread>();
//	private HashMap<String, ShipChoosedThread> choosetaskmap2 = new HashMap<String, ShipChoosedThread>();
	private void closeReqest() {
		if (choosetaskmap.isEmpty())
			return;
		Iterator<String> it = choosetaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			ShipChoosedThread task = choosetaskmap.get(uuid);
			task.cancel(true);
		}
		}
	
	class ShipChoosedThread extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				mPortName = mPortName.replace(" ", "%20");

				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_CHOOSED_SHIP_URL + mPortName;

				// print("1: "+httpPost);

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
				conn.setReadTimeout(10000);

				InputStream inStream = conn.getInputStream();

				parseXMLnew(inStream);
				inStream.close();

			} catch (Exception e) {
				// TODO: handle exception
				print("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (shipsBeans.size() > 0) {
				Intent intent = new Intent(context,
					ShipInfoActivity.class);
			Bundle data = new Bundle();
//			data.putString("mmsi", shipsBeans.get(0).getM());
//			data.putString("la", shipsBeans.get(0).getLa()+"");
//			data.putString("lo", shipsBeans.get(0).getLo()+"");
			if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
				data.putString("from", "GetInportShipsActivity");
				}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
					data.putString("from", "GetArriveShipsActivity");
				}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
					data.putString("from", "GetWillArriveShipsActivity");
				}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
					data.putString("starttime", leavetime);
					data.putString("endtime", arrivetime);
					data.putString("from", "LineShipsActivity");
				}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
					data.putString("from", "GetAreaShipsActivity");
				}
			data.putString("key", "1");
			boolean myfleet = false;
			data.putString("myfleet", myfleet + "");
			data.putString("shipn", shipsBeans.get(0).n);
			data.putString("shipcs", shipsBeans.get(0).c);
			data.putString("shipcname", shipsBeans.get(0).cname);
			data.putString("shipm", shipsBeans.get(0).m);
			data.putString("shipi", shipsBeans.get(0).i);
			data.putString("shiplo", shipsBeans.get(0).lo);
			data.putString("shipla", shipsBeans.get(0).la);
			data.putString("shipco", shipsBeans.get(0).co);
			data.putString("shipsp", shipsBeans.get(0).sp);
			data.putString("shipsubt", shipsBeans.get(0).getSubt());
			data.putString("ships", shipsBeans.get(0).s);
			data.putString("shipl", shipsBeans.get(0).l);
			data.putString("shipb", shipsBeans.get(0).b);
			data.putString("shipdr", shipsBeans.get(0).dr);
			data.putString("shipd", shipsBeans.get(0).d);
			data.putString("shipe", shipsBeans.get(0).e);
			data.putString("shipti", shipsBeans.get(0).ti);
			data.putString("shipan", shipsBeans.get(0).an);
			data.putString("shipc", shipsBeans.get(0).c);
			data.putString("shipdn", shipsBeans.get(0).dn);
			data.putString("shipflag", shipsBeans.get(0).flag);
			data.putString("shipfle", shipsBeans.get(0).fle);
			data.putString("shiph", shipsBeans.get(0).h);
			data.putString("shipmes", shipsBeans.get(0).message);
			data.putString("shiprot", shipsBeans.get(0).rot);
			data.putString("shiprs", shipsBeans.get(0).rs);
			intent.putExtras(data);
			context.startActivity(intent);
			
			Message message = new Message();
			message.what = 111;
			PortShipsActivity.newHandler.sendMessage(message);
			return;
			}
		}
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			
//			if (shipsBeans.size() > 0) {
////				if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
////					
////					Intent intent = new Intent(context,
////							MapActivity1.class);
////					Bundle b = new Bundle();
////					b.putString("mmsi", shipsBeans.get(0).getM());
////					b.putString("la", shipsBeans.get(0).getLa()+"");
////					b.putString("lo", shipsBeans.get(0).getLo()+"");
////					b.putString("starttime", leavetime);
////					b.putString("endtime", arrivetime);
////					b.putString("from", "LineShipsActivity");
////					b.putString("key", "1");
////					intent.putExtras(b);
////					context.startActivity(intent);
////					return;
////		}
////				Message message = new Message();
////				message.what = 111;
//////				print("点击了："+mPortName);
////				if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
////				GetInportShipsActivity.newHandler.sendMessage(message);
////				}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
////					GetArriveShipsActivity.newHandler.sendMessage(message);
////				}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
////					GetWillArriveShipsActivity.newHandler.sendMessage(message);
////				}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
////					LineShipsActivity.newHandler.sendMessage(message);
////				}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
////					GetAreaShipsActivity.newHandler.sendMessage(message);
////				}
//				Message message = new Message();
//				message.what = 111;
//				PortShipsActivity.newHandler.sendMessage(message);
//				ShipsListAdapter.isShow = true;
//				ShipsListAdapter.isMove = true;
//				if (ShipsInfoLayer.tap_ships.size() >= 1) {
//					int have = 0;
//					for (int at = 0; at < ShipsInfoLayer.alltap_ships.size(); at++) {
//						if (ShipsInfoLayer.alltap_ships.get(at).m
//								.equals(ShipsInfoLayer.tap_ships.get(0).m)) {
//							ShipsInfoLayer.alltap_ships.remove(at);
//							ShipsInfoLayer.alltap_ships
//									.add(ShipsInfoLayer.tap_ships.get(0));
//							ShipsInfoLayer.tap_ships.clear();
//							have++;
//							break;
//						}
//					}
//					if (have == 0) {
//						ShipsInfoLayer.alltap_ships
//								.add(ShipsInfoLayer.tap_ships.get(0));
//						ShipsInfoLayer.tap_ships.clear();
//					}
//				}
//				if (ShipsInfoLayer.teamship.size() > 0) {
//					for (int i1 = 0; i1 < ShipsInfoLayer.teamship.size(); i1++) {
//						boolean have = false;
//						for (int i = 0; i < ShipsInfoLayer.allteamship.size(); i++) {
//
//							if (ShipsInfoLayer.teamship.get(i1).m
//									.equals(ShipsInfoLayer.allteamship.get(i).m)) {
//								ShipsInfoLayer.allteamship.remove(i);
//								ShipsInfoLayer.allteamship
//										.add(ShipsInfoLayer.teamship.get(i1));
//								have = true;
//								break;
//							}
//						}
//						if (!have) {
//							ShipsInfoLayer.allteamship
//									.add(ShipsInfoLayer.teamship.get(i1));
//						}
//					}
//					ShipsInfoLayer.teamship.clear();
//				}
//				if (ShipsInfoLayer.searchshipsBeans.size() > 0) {
//					for (int i1 = 0; i1 < ShipsInfoLayer.searchshipsBeans
//							.size(); i1++) {
//						boolean have = false;
//						for (int i = 0; i < ShipsInfoLayer.allsearchshipsBeans
//								.size(); i++) {
//
//							if (ShipsInfoLayer.searchshipsBeans.get(i1).m
//									.equals(ShipsInfoLayer.allsearchshipsBeans
//											.get(i).m)) {
//								ShipsInfoLayer.allsearchshipsBeans.remove(i);
//								ShipsInfoLayer.allsearchshipsBeans
//										.add(ShipsInfoLayer.searchshipsBeans
//												.get(i1));
//								have = true;
//								break;
//							}
//						}
//						if (!have) {
//							ShipsInfoLayer.allsearchshipsBeans
//									.add(ShipsInfoLayer.searchshipsBeans
//											.get(i1));
//						}
//					}
//				}
//				ShipsInfoLayer.searchshipsBeans.clear();
//				ShipsInfoLayer.searchshipsBeans.addAll(shipsBeans);
//				MapActivity.isTeamShipMove = true;
//				if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
//				GetInportShipsActivity.searchActivity.finish();
//				}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
//					GetArriveShipsActivity.searchActivity.finish();
//				}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
//					GetWillArriveShipsActivity.searchActivity.finish();
//				}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
//					LineShipsActivity.searchActivity.finish();
//				}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
//					GetAreaShipsActivity.searchActivity.finish();
//				}
//				((PortShipsActivity) context).finish();
////				Intent intent = new Intent(context, ChooseShipActivity.class);
////				print("点击了："+mPortName+"进行跳转");
////				context.startActivity(intent);
//				// ((SearchActivity) context).finish();
//			} else {
//				Toast.makeText(context, "数据过期", Toast.LENGTH_LONG).show();
//			}
//		}
	}
	
	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					shipsBeans.add(XmlParseUtility.parse(childElement,
							ShipsBean.class));
				}
			}
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
	
}
