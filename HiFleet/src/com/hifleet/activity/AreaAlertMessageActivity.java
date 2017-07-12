package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
import com.hifleet.adapter.AlertMessageAdapter;
import com.hifleet.adapter.ChooseShipAdapter;
import com.hifleet.adapter.PortListAdapter;
import com.hifleet.adapter.PortShipsAdapter;
import com.hifleet.adapter.ShipsListAdapter;
import com.hifleet.bean.AlertBeans;
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
public class AreaAlertMessageActivity extends Activity  implements IXListViewListener {
	
	
	private XListView mSimpleDetailList;
	private ListView mList;
	TextView pagecount;
	TextView mtitle;
	private AlertMessageAdapter mAdapter;
	public static RelativeLayout progressLoading;
	
	OsmandApplication app;
	
	public List<AlertBeans> inportships = new ArrayList<AlertBeans>();
	public List<AlertBeans> currentpagealertmsgs = new ArrayList<AlertBeans>();//此次请求的数据
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	private int limit1=0;
	private int limit2=10;
	private int pcount=1;
	public static int total=0;
	String ship;
	String mmsi;
	String alerttype;
	String AlertConditionId;
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
		Bundle b=this.getIntent().getExtras();
		ship=b.getString("ship");
		mmsi=b.getString("mmsi");
		alerttype=b.getString("alerttype");
		AlertConditionId=b.getString("conditonid");
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
		progressLoading.setVisibility(View.VISIBLE);
		mAdapter = new AlertMessageAdapter(this,"area",alerttype, inportships);
		if(ship==null||ship.equals("N/A")){
			mtitle.setText(mmsi+"-报警记录");
		}else{
		mtitle.setText(ship+"-报警记录");
		}
		
		String uuid1 = UUID.randomUUID().toString();
		LodingAreaAlertMsgThread LodingShips=new LodingAreaAlertMsgThread();
		areataskmap.put(uuid1, LodingShips);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			LodingShips.executeOnExecutor(
					Executors.newCachedThreadPool(), new String[0]);
		} else {
			LodingShips.execute();
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
		}
	}
	
	
//	private HashMap<String, LodingInportShipsThread> taskmap = new HashMap<String, LodingInportShipsThread>();//获取在港船舶的线程
//	private HashMap<String, LodingArrivePortShipsThread> ataskmap = new HashMap<String, LodingArrivePortShipsThread>();//获取抵港船舶的线程
//	private HashMap<String, LodingWillArriveShipsThread> wataskmap = new HashMap<String, LodingWillArriveShipsThread>();//获取预抵港船舶的线程
//	private HashMap<String, LodingLineShipsThread> ltaskmap = new HashMap<String, LodingLineShipsThread>();//获取航线船舶的线程
	private HashMap<String, LodingAreaAlertMsgThread> areataskmap = new HashMap<String, LodingAreaAlertMsgThread>();//获取在港船舶的线程
	
//	private HashMap<String, LodingInportShipsThread> choosetaskmap = new HashMap<String, LodingInportShipsThread>();
//	private HashMap<String, LodingInportShipsThread> choosetaskmap2 = new HashMap<String, LodingInportShipsThread>();
	private void closeReqest1() {
//			mAdapter = new PortShipsAdapter(this, GetAreaShipsActivity.inportships);
//			mtitle.setText("区域船舶");
			if (areataskmap.isEmpty())
				return;
			Iterator<String> it = areataskmap.keySet().iterator();
			while (it.hasNext()) {
				String uuid = it.next();
				LodingAreaAlertMsgThread task = areataskmap.get(uuid);
				task.cancel(true);
			}
	}
	
	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		heartBeatBean.clear();
		currentpagealertmsgs = new ArrayList<AlertBeans>();
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
				if (childElement.getNodeName().compareTo("AlertRsType") == 0) {

					
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						AlertBeans is=new AlertBeans();
						is.AlertAreaId=childElement2.getAttribute("AlertRsId");
						is.Mmsi=childElement2.getAttribute("Mmsi");
						is.AlertConditionId=childElement2.getAttribute("AlertConditionId");
						is.AlertAreaId=childElement2.getAttribute("AlertAreaId");
						is.AlertAreaName=childElement2.getAttribute("AlertAreaName");
						is.Position=childElement2.getAttribute("Position");
						is.TriggerTime=childElement2.getAttribute("TriggerTime");
						is.EndTime=childElement2.getAttribute("EndTime");
						is.AlertType=childElement2.getAttribute("AlertType");
						is.An=childElement2.getAttribute("An");
						is.ShipSpeed=childElement2.getAttribute("ShipSpeed");
						is.ShipName=childElement2.getAttribute("ShipName");
						is.Dn=childElement2.getAttribute("Dn");
//							System.out.println("解析的inportships mmsi"+is.Mmsi);
							
							currentpagealertmsgs.add(is);
					}
					
//					System.out.println("解析的currentpagealertmsgs SIZE"+currentpagealertmsgs.size());
			}
          if (childElement.getNodeName().compareTo("ETAShips") == 0) {

					
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						Element childElement2 = (Element) childNode2;
						AlertBeans is=new AlertBeans();
						is.setDn(childElement2.getAttribute("dn"));
						is.setCaleta(childElement2.getAttribute("caleta"));
						is.setMmsi(childElement2.getAttribute("mmsi"));
						is.setReporteta(childElement2.getAttribute("reporteta"));
						is.setShipname(childElement2.getAttribute("shipname"));
//							System.out.println("解析的inportships mmsi"+is.Mmsi);
							
							currentpagealertmsgs.add(is);
					}
					
//					System.out.println("解析的currentpagealertmsgs SIZE"+currentpagealertmsgs.size());
			}	
          if (childElement.getNodeName().compareTo("Total") == 0) {
        	  if(childElement.getAttribute("num")!=null&&!childElement.getAttribute("num").equals("")){
				total=Integer.parseInt(childElement.getAttribute("num"));
        	  }else if(childElement.getAttribute("TotalNum")!=null&&!childElement.getAttribute("TotalNum").equals("")){
        		  total=Integer.parseInt(childElement.getAttribute("TotalNum"));
        	  }
			}
//				if (childElement.getNodeName().compareTo("Total") == 0) {
//					total=Integer.parseInt(childElement.getAttribute("TotalNum"));
//				}
//			 System.out.println("total"+total);
//			Message message = new Message();
//			message.what = REFRESH;
//			handler.sendMessage(message);
		}
	}
	}
	
	class LodingAreaAlertMsgThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost;
//				System.err.println("alerttype"+alerttype+"!alerttype.equals"+!alerttype.equals("M"));
			if(!alerttype.equals("M")){
				SimpleDateFormat  dfm=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d=new Date();
				Date ldate=(Date) d.clone();
				ldate.setDate(d.getDate()-7);
				
				String st=URLEncoder.encode(new String(dfm.format(ldate).getBytes("UTF-8")), "UTF-8");;
				String et=URLEncoder.encode(new String(dfm.format(d).getBytes("UTF-8")), "UTF-8");;
				
				httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_AREA_ALERT_MESSAGE_URL +mmsi+"&AlertType="+alerttype+"&StartTime="+st+"&EndTime="+et+"&limit1="+limit1+"&limit2="+limit2;
				System.out.println("GET_area_ALERT_MESSAGE_URL::"+httpPost);
			}else{
				httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_AREA_ALERT_ETA_MESSAGE_URL+"AlertConditionId="+AlertConditionId+"&limit1="+limit1+"&limit2="+limit2;
				System.out.println("GET_area_ALERT_MESSAGE_URL MMM::"+httpPost);
			}
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
			inportships.addAll(currentpagealertmsgs);
			if (inportships.size() > 0) {
				onLoad();
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
			LodingAreaAlertMsgThread LodingShips=new LodingAreaAlertMsgThread();
			areataskmap.put(uuid1, LodingShips);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				LodingShips.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				LodingShips.execute();
			}
//		progressLoading.setVisibility(View.VISIBLE);
//		onLoad();
	}	//加载更多
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// FailSafeFuntions.quitRouteRestoreDialog();
	}
	
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
