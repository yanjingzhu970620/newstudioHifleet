package com.hifleet.fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;

import com.hifleet.plus.R;
import com.hifleet.activity.GetAreaShipsActivity;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.MyTeamShipsActivity;
import com.hifleet.adapter.AreaListAdapter;
import com.hifleet.adapter.ColorOptionsAdapter;
import com.hifleet.adapter.MyTeamListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.ZoneBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.MyTeamShipsThread;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# DetailsFragment.java Create on 2015年7月8日 下午2:04:06
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
@SuppressLint("ValidFragment")
public class AreaFragment extends Fragment {

	Context context;

	ListView mTeamList;

	ListView mMyTeam;

	OsmandApplication app;

	ProgressBar progress;

	AreaListAdapter areaListAdapter;

	ArrayList<String> alertgroup=new ArrayList<String>();
	Map<String,String> alerttypebyid=new HashMap<String,String>();
	List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
    public String groupclick=null;

	private boolean oncr;

	private MapActivity mapactivity;

	private DisplayMetrics dm;
	public AreaFragment(MapActivity mapactivity){
		this.mapactivity=mapactivity;
	}
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
		oncr = true;
		View view = inflater.inflate(R.layout.fragment_team, null);
		TextView t=(TextView) view.findViewById(R.id.team_title);
		t.setText("我的区域");
		app=mapactivity.getMyApplication();
		dm= mapactivity.getMapView().getResources().getDisplayMetrics();
		context = getActivity();

		mMyTeam = (ListView) view.findViewById(R.id.list_my_team);
		progress = (ProgressBar) view.findViewById(R.id.progress);
		super.registerForContextMenu(this.mMyTeam);
		if (!app.isIslogin()) {
			progress.setVisibility(View.GONE);
			Intent intent = new Intent(context, IsLoginActivity.class);
			context.startActivity(intent);
			((MapActivity) context).overridePendingTransition(
					R.drawable.activity_open, 0);
		} else {
			LodingAreaThread mt = new LodingAreaThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				mt.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				mt.execute();
			}
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (oncr) {
			oncr = false;
			return;
		} else {
			if (app.isIslogin()) {
				LodingAreaThread mt = new LodingAreaThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					mt.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					mt.execute();
				}
			}else {
				progress.setVisibility(View.GONE);
			}
		}
	}
	public static List<ZoneBean> allsearchZoneBean = new ArrayList<ZoneBean>();
	class LodingAreaThread extends AsyncTask<String, Void, Void> {
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
						null) + IndexConstants.GET_MY_AREA;
				System.out.println("area url"+httpPost);
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
				allsearchZoneBean.clear();
				parseXMLnew(inStream);
				inStream.close();
				AreaAlertThread mt = new AreaAlertThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					mt.executeOnExecutor(Executors.newCachedThreadPool(),
							new String[0]);
				} else {
					mt.execute();
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println("未能获取网络数据");
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
//			System.out.println("search area finish"+allsearchZoneBean.size());
			if(allsearchZoneBean.size()>0){
				
				areaListAdapter = new AreaListAdapter(context, allsearchZoneBean,alertgroup,alerttypebyid);
				mMyTeam.setAdapter(areaListAdapter);
			} 
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(context, "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
			progress.setVisibility(View.GONE);
		}
	}

	class AreaAlertThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				SimpleDateFormat  dfm=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d=new Date();
				Date ldate=(Date) d.clone();
				ldate.setDate(d.getDate()-7);
				
				String st=URLEncoder.encode(new String(dfm.format(ldate).getBytes("UTF-8")), "UTF-8");;
				String et=URLEncoder.encode(new String(dfm.format(d).getBytes("UTF-8")), "UTF-8");;
//				alertgroup.clear();
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_AREA_ALERT+st+"&EndTime="+et;
				System.out.println("AREA alert: " + httpPost);
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
				alerttypebyid.clear();
				alertgroup.clear();
				parseXMLnew(inStream);
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
//			System.err.println("MyTeamAlertThread alertgroup"+alertgroup.size());
			areaListAdapter = new AreaListAdapter(context, allsearchZoneBean,alertgroup,alerttypebyid);
			mMyTeam.setAdapter(areaListAdapter);
		}

	}
	private void parseXMLnew(InputStream inStream) throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);

		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("zone") == 0) {
					ZoneBean z=new ZoneBean();
					z.name=childElement.getAttribute("name");
					z.bound=childElement.getAttribute("bound");
//					System.out.println("area bound"+z.bound);
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement2 = (Element) childNode2;
						if (childElement2.getNodeName().compareTo("zoneid") == 0) {
//							System.out.println("解析的inportships mmsi"+is.mmsi);
							z.zoneid=childElement2.getTextContent();
						}
						if (childElement2.getNodeName().compareTo("updateTime") == 0) {
//							System.out.println("解析的inportships mmsi"+is.mmsi);
							z.updateTime=childElement2.getTextContent();
						}
						}
					}
					allsearchZoneBean.add(z);
				}
				if (childElement.getNodeName().compareTo("AlertRsTitle") == 0) {
					String id=childElement.getAttribute("AlertAreaId");
					String alerttype=childElement.getAttribute("AlertType");
					String AlertConditionId=childElement.getAttribute("AlertConditionId");
					if(!alertgroup.contains(id)){
					alertgroup.add(id);
					}
					if(alerttypebyid.containsKey(id)){
						String mytype=alerttypebyid.get(id);
						mytype=mytype+","+alerttype;
						mytype=	mytype+":"+AlertConditionId;
						alerttypebyid.put(id, mytype);
					}else{
					alerttypebyid.put(id, alerttype+":"+AlertConditionId);
					}
//					System.err.println("area alert::"+childElement.getAttribute("AlertAreaId"));
				}
			}
		}
		
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
//		System.err.println("team context menu ");
		// if(true){
//		MyTeamBean s = areaListAdapter.lteamBeans.get(0);
//		menu.setHeaderTitle(s.getName());
//		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "删除船队");
//		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "编辑颜色");
		// menu.add(Menu.NONE, Menu.FIRST + 2, 2, "");
		// }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
//			DeletGroupDialog();
			break;
		}
		return false;
	}
	
	
	  public static int dip2px(float density, float dpValue) {  
	        final float scale = density;  
	        return (int) (dpValue * scale + 0.5f);  
	  } 

	  
}
