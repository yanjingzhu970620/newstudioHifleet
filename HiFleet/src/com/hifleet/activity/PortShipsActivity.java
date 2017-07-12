package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hifleet.plus.R;
import com.hifleet.activity.GetInportShipsActivity.LodingInportShipsThread;
import com.hifleet.adapter.ChooseShipAdapter;
import com.hifleet.adapter.PortListAdapter;
import com.hifleet.adapter.PortShipsAdapter;
import com.hifleet.adapter.ShipsListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.InportShipsBean;
import com.hifleet.bean.PortBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.MyTeamShipsThread;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;
import com.hifleet.widget.DateTimePickDialogUtil;
import com.hifleet.xlistview.XListView;
import com.hifleet.xlistview.XListView.IXListViewListener;

/**
 * @{# ShipSimpleDetailActivity.java Create on 2015年7月15日 下午4:22:41
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class PortShipsActivity extends Activity  implements IXListViewListener {
	
	
	private XListView mSimpleDetailList;
	private ListView mList;
	TextView pagecount;
	TextView mtitle;
	private PortShipsAdapter mAdapter;
	public static RelativeLayout progressLoading;
	
	OsmandApplication app;
	
	public static List<InportShipsBean> inportships = new ArrayList<InportShipsBean>();
	public static List<InportShipsBean> currentpageinportships = new ArrayList<InportShipsBean>();//此次请求的数据
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	private int limit1=0;
	private int limit2=10;
	private int pcount=1;
	public static int total=0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		System.out.println("portShipsActivity oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_ship);
		mtitle=(TextView) findViewById(R.id.activity_chooseship_title);
       limit1=0;
       limit2=10;
       mList = (ListView) findViewById(R.id.list_ship_simple_detail);
       mList.setVisibility(View.GONE);
       mSimpleDetailList = (XListView) findViewById(R.id.xlist_ship_simple_detail);
       mSimpleDetailList.setVisibility(View.VISIBLE);
		pagecount=(TextView) findViewById(R.id.portships_pagecount);
//		progress = (ProgressBar) findViewById(R.id.progress);
		progressLoading = (RelativeLayout) findViewById(R.id.progress_ships_loading);
		if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
		inportships=GetInportShipsActivity.inportships;
		total=GetInportShipsActivity.total;
		mAdapter = new PortShipsAdapter(this, inportships);
		mtitle.setText("在港船舶");
		}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
			inportships=GetArriveShipsActivity.inportships;
			total=GetArriveShipsActivity.total;
			mAdapter = new PortShipsAdapter(this,inportships);
			mtitle.setText("最近抵港");
		}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
			inportships=GetWillArriveShipsActivity.inportships;
			total=GetWillArriveShipsActivity.total;
			mAdapter = new PortShipsAdapter(this, inportships);
			mtitle.setText("预抵船舶");
		}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
			inportships=LineShipsActivity.inportships;
			total=LineShipsActivity.total;
			mAdapter = new PortShipsAdapter(this, inportships);
			mtitle.setText("航线船舶");
		}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
			inportships= GetAreaShipsActivity.inportships;
			total=GetAreaShipsActivity.total;
			mAdapter = new PortShipsAdapter(this,inportships);
			mtitle.setText("区域船舶");
		}
		mSimpleDetailList.setPullLoadEnable(true);
		mSimpleDetailList.setAdapter(mAdapter);
		mSimpleDetailList.setXListViewListener(this);
	}
	private void onLoad() {
		mSimpleDetailList.stopRefresh();
		mSimpleDetailList.stopLoadMore();
		mSimpleDetailList.setRefreshTime("刚刚");
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
//		case R.id.last_page:
//			if(limit1==0){
//				Toast.makeText(getBaseContext(), "已经是第一页", Toast.LENGTH_LONG).show();
//			}else{
//				limit1=limit1-10;
////				limit2=limit2-10;
//				pcount--;
//			}
//			if(inportships!=null){
//				inportships.clear();
//				}
//			closeReqest1();
//			String uuid = UUID.randomUUID().toString();
//			if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
//			LodingInportShipsThread LodingShips=new LodingInportShipsThread();
//			taskmap.put(uuid, LodingShips);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//				LodingShips.executeOnExecutor(
//						Executors.newCachedThreadPool(), new String[0]);
//			} else {
//				LodingShips.execute();
//			}
//			}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
//				LodingArrivePortShipsThread LodingShips=new LodingArrivePortShipsThread();
//				ataskmap.put(uuid, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
//				LodingWillArriveShipsThread LodingShips=new LodingWillArriveShipsThread();
//				wataskmap.put(uuid, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
//				LodingLineShipsThread LodingShips=new LodingLineShipsThread();
//				ltaskmap.put(uuid, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
////				mAdapter = new PortShipsAdapter(this, GetAreaShipsActivity.inportships);
////				mtitle.setText("区域船舶");
//				LodingAreaShipsThread LodingShips=new LodingAreaShipsThread();
//				areataskmap.put(uuid, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}
//			progressLoading.setVisibility(View.VISIBLE);
//			break;
//		case R.id.next_page:
////			if(limit2==0){
////				Toast.makeText(getBaseContext(), "已经是第一页", Toast.LENGTH_LONG).show();
////			}else{
//				limit1=limit1+10;
////				limit2=limit2+10;
//				pcount++;
////				pagecount.setText("第"+pcount+"页");
////			}
//			if(inportships!=null){
//				inportships.clear();
//				}
//			closeReqest1();
//			String uuid1 = UUID.randomUUID().toString();
//			if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
//			LodingInportShipsThread LodingShips1=new LodingInportShipsThread();
//			taskmap.put(uuid1, LodingShips1);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//				LodingShips1.executeOnExecutor(
//						Executors.newCachedThreadPool(), new String[0]);
//			} else {
//				LodingShips1.execute();
//			}
//			}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
//				LodingArrivePortShipsThread LodingShips1=new LodingArrivePortShipsThread();
//				ataskmap.put(uuid1, LodingShips1);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips1.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips1.execute();
//				}
//			}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
//				LodingWillArriveShipsThread LodingShips=new LodingWillArriveShipsThread();
//				wataskmap.put(uuid1, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
//				LodingLineShipsThread LodingShips=new LodingLineShipsThread();
//				ltaskmap.put(uuid1, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
//				LodingAreaShipsThread LodingShips=new LodingAreaShipsThread();
//				areataskmap.put(uuid1, LodingShips);
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//					LodingShips.executeOnExecutor(
//							Executors.newCachedThreadPool(), new String[0]);
//				} else {
//					LodingShips.execute();
//				}
//			}
//			progressLoading.setVisibility(View.VISIBLE);
//			break;
		}
	}
	
	
	private HashMap<String, LodingInportShipsThread> taskmap = new HashMap<String, LodingInportShipsThread>();//获取在港船舶的线程
	private HashMap<String, LodingArrivePortShipsThread> ataskmap = new HashMap<String, LodingArrivePortShipsThread>();//获取抵港船舶的线程
	private HashMap<String, LodingWillArriveShipsThread> wataskmap = new HashMap<String, LodingWillArriveShipsThread>();//获取预抵港船舶的线程
	private HashMap<String, LodingLineShipsThread> ltaskmap = new HashMap<String, LodingLineShipsThread>();//获取航线船舶的线程
	private HashMap<String, LodingAreaShipsThread> areataskmap = new HashMap<String, LodingAreaShipsThread>();//获取在港船舶的线程
	
	private HashMap<String, LodingInportShipsThread> choosetaskmap = new HashMap<String, LodingInportShipsThread>();
	private HashMap<String, LodingInportShipsThread> choosetaskmap2 = new HashMap<String, LodingInportShipsThread>();
	private void closeReqest1() {
		if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
		if (taskmap.isEmpty())
			return;
		Iterator<String> it = taskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			LodingInportShipsThread task = taskmap.get(uuid);
			task.cancel(true);
		}
		}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
			if (ataskmap.isEmpty())
				return;
			Iterator<String> it = ataskmap.keySet().iterator();
			while (it.hasNext()) {
				String uuid = it.next();
				LodingArrivePortShipsThread task = ataskmap.get(uuid);
				task.cancel(true);
			}
		}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
			if (wataskmap.isEmpty())
				return;
			Iterator<String> it = wataskmap.keySet().iterator();
			while (it.hasNext()) {
				String uuid = it.next();
				LodingWillArriveShipsThread task = wataskmap.get(uuid);
				task.cancel(true);
			}
		}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
			if (ltaskmap.isEmpty())
				return;
			Iterator<String> it = ltaskmap.keySet().iterator();
			while (it.hasNext()) {
				String uuid = it.next();
				LodingLineShipsThread task = ltaskmap.get(uuid);
				task.cancel(true);
			}
		}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
//			mAdapter = new PortShipsAdapter(this, GetAreaShipsActivity.inportships);
//			mtitle.setText("区域船舶");
			if (areataskmap.isEmpty())
				return;
			Iterator<String> it = areataskmap.keySet().iterator();
			while (it.hasNext()) {
				String uuid = it.next();
				LodingAreaShipsThread task = areataskmap.get(uuid);
				task.cancel(true);
			}
		}
	}
	
	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		heartBeatBean.clear();
		currentpageinportships = new ArrayList<InportShipsBean>();
		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
//				if (childElement.getNodeName().compareTo("port") == 0) {
//					allsearchPortBean.add(XmlParseUtility.parse(childElement,
//							PortBean.class));
//				}
				if (childElement.getNodeName().compareTo("InPortShips") == 0) {
					InportShipsBean is=new InportShipsBean();
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						if (childElement2.getNodeName().compareTo("mmsi") == 0) {
							is.mmsi=childElement2.getTextContent();
//							System.out.println("解析的inportships mmsi"+is.mmsi);
						}
						if (childElement2.getNodeName().compareTo("shipname") == 0) {
							is.shipname=childElement2.getTextContent();
//							System.out.println("解析的inportships shipname"+is.shipname);
						}
						if (childElement2.getNodeName().compareTo("updatetime") == 0) {
							is.updatetime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("ansub") == 0) {
							is.ansub=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dwt") == 0) {
							is.dwt=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("type") == 0) {
							is.type=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dataflag") == 0) {
							is.dataflag=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("year_of_built") == 0) {
							is.year_of_built=childElement2.getTextContent();
						}
					}
					currentpageinportships.add(is);
//					System.out.println("解析的inportships"+is.mmsi);
				}
				if (childElement.getNodeName().compareTo("ArrivePortShips") == 0) {
					InportShipsBean is=new InportShipsBean();
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						if (childElement2.getNodeName().compareTo("mmsi") == 0) {
							is.mmsi=childElement2.getTextContent();
//							System.out.println("解析的inportships mmsi"+is.mmsi);
						}
						if (childElement2.getNodeName().compareTo("shipname") == 0) {
							is.shipname=childElement2.getTextContent();
//							System.out.println("解析的inportships shipname"+is.shipname);
						}
						if (childElement2.getNodeName().compareTo("updatetime") == 0) {
							is.updatetime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("ansub") == 0) {
							is.ansub=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dwt") == 0) {
							is.dwt=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("type") == 0) {
							is.type=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dataflag") == 0) {
							is.dataflag=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("triggertime") == 0) {
							is.triggertime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("year_of_built") == 0) {
							is.year_of_built=childElement2.getTextContent();
						}
					}
					currentpageinportships.add(is);
//					System.out.println("解析的inportships"+is.mmsi);
				}
				if (childElement.getNodeName().compareTo("WillArrivePortShips") == 0) {
					InportShipsBean is=new InportShipsBean();
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						if (childElement2.getNodeName().compareTo("mmsi") == 0) {
							is.mmsi=childElement2.getTextContent();
//							System.out.println("解析的inportships mmsi"+is.mmsi);
						}
						if (childElement2.getNodeName().compareTo("shipname") == 0) {
							is.shipname=childElement2.getTextContent();
//							System.out.println("解析的inportships shipname"+is.shipname);
						}
						if (childElement2.getNodeName().compareTo("updatetime") == 0) {
							is.updatetime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("ansub") == 0) {
							is.ansub=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dwt") == 0) {
							is.dwt=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("type") == 0) {
							is.type=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dataflag") == 0) {
							is.dataflag=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("eta") == 0) {
							is.eta=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("year_of_built") == 0) {
							is.year_of_built=childElement2.getTextContent();
						}
					}
					currentpageinportships.add(is);
//					System.out.println("解析的inportships"+is.mmsi);
				}
				if (childElement.getNodeName().compareTo("LineOperatingShips") == 0) {
					InportShipsBean is=new InportShipsBean();
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						if (childElement2.getNodeName().compareTo("mmsi") == 0) {
							is.mmsi=childElement2.getTextContent();
//							System.out.println("解析的inportships mmsi"+is.mmsi);
						}
						if (childElement2.getNodeName().compareTo("shipname") == 0) {
							is.shipname=childElement2.getTextContent();
//							System.out.println("解析的inportships shipname"+is.shipname);
						}
						if (childElement2.getNodeName().compareTo("leavetime") == 0) {
							is.leavetime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("arrivetime") == 0) {
							is.arrivetime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("costtime") == 0) {
							is.costtime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dwt") == 0) {
							is.dwt=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("type") == 0) {
							is.type=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dataflag") == 0) {
							is.dataflag=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("shipflag") == 0) {
							is.shipflag=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("year_of_built") == 0) {
							is.year_of_built=childElement2.getTextContent();
						}
					}
					currentpageinportships.add(is);
//					System.out.println("解析的inportships"+is.mmsi);
				}
				if (childElement.getNodeName().compareTo("AreaShips") == 0) {
					InportShipsBean is=new InportShipsBean();
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						if (childElement2.getNodeName().compareTo("mmsi") == 0) {
							is.mmsi=childElement2.getTextContent();
//							System.out.println("解析的inportships mmsi"+is.mmsi);
						}
						if (childElement2.getNodeName().compareTo("shipname") == 0) {
							is.shipname=childElement2.getTextContent();
//							System.out.println("解析的inportships shipname"+is.shipname);
						}
						if (childElement2.getNodeName().compareTo("updatetime") == 0) {
							is.updatetime=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("ansub") == 0) {
							is.ansub=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dwt") == 0) {
							is.dwt=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("type") == 0) {
							is.type=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("dataflag") == 0) {
							is.dataflag=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("year_of_built") == 0) {
							is.year_of_built=childElement2.getTextContent();
						}
					}
					currentpageinportships.add(is);
			}
				if (childElement.getNodeName().compareTo("total") == 0) {
					total=Integer.parseInt(childElement.getTextContent());
				}
			// System.out.println("allsearchPortBean"+allsearchPortBean.size());
//			Message message = new Message();
//			message.what = REFRESH;
//			handler.sendMessage(message);
		}
	}
	}
	class LodingInportShipsThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.INPORT_SHIPS_URL + GetInportShipsActivity.sdwt+"&limit1="+limit1+"&limit2="+limit2+"&portcode="+
						GetInportShipsActivity.mPort.getPortCode()+"&type="+GetInportShipsActivity.stype;
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
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			if (inportships.size() > 0) {
				onLoad();
//				mAdapter = new PortShipsAdapter(PortShipsActivity.this,inportships);
//				mSimpleDetailList.setAdapter(mAdapter);
				inportships.addAll(currentpageinportships);
				mAdapter.notifyDataSetChanged();
				pagecount.setText("第"+pcount+"页");
//				progress.setVisibility(View.GONE);
//				System.out.println("获得了："+inportships.size() +"进行刷新");
//				Intent intent = new Intent(GetInportShipsActivity.this, PortShipsActivity.class);
//				System.out.println("点击了："+"进行跳转");
//				progress.setVisibility(View.GONE);
				progressLoading.setVisibility(View.GONE);
//				GetInportShipsActivity.this.startActivity(intent);
				// ((SearchActivity) context).finish();
			} else {
				progressLoading.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "暂无在港船舶", Toast.LENGTH_LONG).show();
			}
			
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
//			progress.setVisibility(View.GONE);
		}
	}
	
	class LodingArrivePortShipsThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.ARRIVEPORT_SHIPS_URL + GetArriveShipsActivity.sdwt+"&limit1="+limit1+"&limit2="+limit2+"&portcode="+
						GetArriveShipsActivity.mPort.getPortCode()+"&type="+GetArriveShipsActivity.stype;
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
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			if (inportships.size() > 0) {
				onLoad();
				inportships.addAll(currentpageinportships);
				mAdapter.notifyDataSetChanged();
				pagecount.setText("第"+pcount+"页");
				progressLoading.setVisibility(View.GONE);
			} else {
				progressLoading.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "暂无抵港船舶", Toast.LENGTH_LONG).show();
			}
			
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
//			progress.setVisibility(View.GONE);
		}
	}

	class LodingWillArriveShipsThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.WILLARRIVEPORT_SHIPS_URL + GetWillArriveShipsActivity.sdwt+"&starttime="+
						GetWillArriveShipsActivity.stime+"&endtime="+GetWillArriveShipsActivity.etime+"&limit1="+"0"+"&limit2="+"10"+"&portcode="+
						GetWillArriveShipsActivity.mPort.getPortCode()+"&type="+GetWillArriveShipsActivity.stype;
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
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			if (inportships.size() > 0) {
				onLoad();
				inportships.addAll(currentpageinportships);
				mAdapter.notifyDataSetChanged();
				pagecount.setText("第"+pcount+"页");
				progressLoading.setVisibility(View.GONE);
			} else {
				progressLoading.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "暂无将要抵港船舶", Toast.LENGTH_LONG).show();
			}
			
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
//			progress.setVisibility(View.GONE);
		}
	}
	
	class LodingLineShipsThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.LINE_SHIPS_URL + LineShipsActivity.sdwt+"&limit1="+limit1+"&limit2="+limit2+"&startportcode="+
						LineShipsActivity.startport.getPortCode()+"&endportcode="+LineShipsActivity.endport.getPortCode()+"&type="+LineShipsActivity.stype;
				System.out.println("lineships:::"+httpPost);
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
				conn.setConnectTimeout(30000);
				InputStream inStream = conn.getInputStream();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			if (inportships.size() > 0) {
				onLoad();
				inportships.addAll(currentpageinportships);
				mAdapter.notifyDataSetChanged();
				pagecount.setText("第"+pcount+"页");
				progressLoading.setVisibility(View.GONE);
			} else {
				progressLoading.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "暂无此航线船舶", Toast.LENGTH_LONG).show();
			}
			
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
//			progress.setVisibility(View.GONE);
		}
	}
	class LodingAreaShipsThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_AREASHIPS + GetAreaShipsActivity.sdwt+"&limit1="+limit1+"&limit2="+limit2+"&shape="+
						GetAreaShipsActivity.sarea+"&type="+GetAreaShipsActivity.stype+"&stop="+GetAreaShipsActivity.sstop;
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
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			if (inportships.size() > 0) {
				onLoad();
				inportships.addAll(currentpageinportships);
				mAdapter.notifyDataSetChanged();
				pagecount.setText("第"+pcount+"页");
				progressLoading.setVisibility(View.GONE);
			} else {
				progressLoading.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "暂无此区域船舶", Toast.LENGTH_LONG).show();
			}
			
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
//			progress.setVisibility(View.GONE);
		}
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		onLoad();
		System.out.println("刷新界面");
	}//刷新界面
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
//			System.out.println("加载更多");
//			limit2=limit2+10;
			if(limit1+10>=total){
				onLoad();
				Toast.makeText(getBaseContext(), "没有更多数据了", Toast.LENGTH_LONG).show();
				return;
			}
			limit1=limit1+10;
			pcount++;
//			pagecount.setText("第"+pcount+"页");
//		if(inportships!=null){
//			inportships.clear();
//			}
		closeReqest1();
		String uuid1 = UUID.randomUUID().toString();
		if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
		LodingInportShipsThread LodingShips1=new LodingInportShipsThread();
		taskmap.put(uuid1, LodingShips1);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			LodingShips1.executeOnExecutor(
					Executors.newCachedThreadPool(), new String[0]);
		} else {
			LodingShips1.execute();
		}
		}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
			LodingArrivePortShipsThread LodingShips1=new LodingArrivePortShipsThread();
			ataskmap.put(uuid1, LodingShips1);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				LodingShips1.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				LodingShips1.execute();
			}
		}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
			LodingWillArriveShipsThread LodingShips=new LodingWillArriveShipsThread();
			wataskmap.put(uuid1, LodingShips);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				LodingShips.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				LodingShips.execute();
			}
		}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
			LodingLineShipsThread LodingShips=new LodingLineShipsThread();
			ltaskmap.put(uuid1, LodingShips);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				LodingShips.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				LodingShips.execute();
			}
		}else if(app.myPreferences.getString("portships", "").equals("GetAreaShipsActivity")){
			LodingAreaShipsThread LodingShips=new LodingAreaShipsThread();
			areataskmap.put(uuid1, LodingShips);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				LodingShips.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				LodingShips.execute();
			}
		}
//		progressLoading.setVisibility(View.VISIBLE);
//		onLoad();
	}	//加载更多
	
	public static Handler newHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 000:
				progressLoading.setVisibility(View.VISIBLE);
				break;
			case 111:
				progressLoading.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
	};
}
