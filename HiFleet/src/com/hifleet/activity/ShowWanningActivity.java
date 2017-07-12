package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hifleet.plus.R;
import com.hifleet.adapter.ShowWanningListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.WanningBean;
import com.hifleet.bean.loginSession;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# ShowWanningActivity.java Create on 2015年5月28日 下午3:36:03
 * 在设置中打开报警的区域列表，然后勾选要
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShowWanningActivity extends HBaseActivity {
	public final int ONCREATEREFRESH = 70000;
	public final int REFRESH = 70001;
	private boolean buttonflag = true;
	ListView mMyTeam;
	List<WanningBean> msgs = null;
	String wanningString;
	OsmandApplication app;

	ShowWanningListAdapter mMyTeamListAdapter;
	static boolean inactivity;
	public static List<String> alertplots = new ArrayList<String>();
//	public static List<WanningBean> ltmMyTeamBeans1 = new ArrayList<WanningBean>();//解析得到的列表
	public static List<WanningBean> warnList = new ArrayList<WanningBean>();//缓存的列表
	public static List<WanningBean> warnadapterList = new ArrayList<WanningBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

	ProgressBar progress;

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
	long s;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//print("初始化" + inactivity + buttonflag);
		// TODO Auto-generated method stub
		s=System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_team);

		initNav();
		setNavTitle("报警区域");
		inactivity = true;
		progress = (ProgressBar) findViewById(R.id.progress);
		mMyTeam = (ListView) findViewById(R.id.list_my_team);	
		mMyTeamListAdapter = new ShowWanningListAdapter(activity,
				warnadapterList);
		// System.out.println("activity刷新报警这儿执行了吗"+ShowWanningListAdapter.lastbean.size());
		mMyTeam.setAdapter(mMyTeamListAdapter);
		//print("Show warning activity. ");
		callwanAction();
	}

	class alertareaThread extends AsyncTask<String, Void, Void> {
		String uuid;
		public alertareaThread(String uuid) {
            this.uuid=uuid;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
			//	print("准备请求全部报警数据");
				long s=System.currentTimeMillis();
				String userId = app.myPreferences.getString("User", null);
				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.GET_LAYER_LIST_URL
						+ userId
						+ "&userDomain=qq.com";
				// System.out.println("httppost报警" + httpPost);
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
//				System.out.println("解析报警列表数据");
				parseXMLnew(inStream);
				inStream.close();
				long e=System.currentTimeMillis();
			//	print("解析报警数据  完成"+(e-s));
			} catch (Exception e) {
				// TODO: handle exception
			//	System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			clearMapByUUID(uuid);
			super.onPostExecute(result);
		}
	}

	class wanningThread extends AsyncTask<String, Void, Void> {

		public wanningThread() {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				long h=System.currentTimeMillis();
				System.out.println("准备请求报警列表数据"+(h-s));
//				long s=System.currentTimeMillis();
				String userId = app.myPreferences.getString("User", null);
				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.GET_LAYER_LIST_URL
						+ userId
						+ "&userDomain=qq.com";
				//print("httppost报警区域： " + httpPost);
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
				conn.setConnectTimeout(5000);
				InputStream inStream = conn.getInputStream();
				long t=System.currentTimeMillis();
				print("准备解析报警列表数据"+(t-s));
				parseXMLnew1(inStream);
				inStream.close();
//				long e=System.currentTimeMillis();
				//print("解析报警列表数据  完成： ");
			} catch (Exception e) {
				// TODO: handle exception
				//System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
//			ShipsInfoLayer.justflag++;
			super.onPostExecute(result);
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}

					Message message = new Message();
					message.what = ONCREATEREFRESH;
					handler.sendMessage(message);
//				}
//			}
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ONCREATEREFRESH:
				if (warnList.size() > 1) {
					for (int i = 0; i < warnList.size(); i++) {
						if (!app.myPreferences.getBoolean(warnList.get(i).getLayerId(), false)) {
							buttonflag = false;
							System.out.println("刷新报警得到未勾选名称"+buttonflag+warnList.get(i).Name);
							setTextNavRight("全选");
							break;
						}
					}
					if (buttonflag) {
						setTextNavRight("全不选");
						// }else {
						// System.out.println("刷新报警这儿执行了吗"+buttonflag);
						// setTextNavRight("全选");
					}
				}
				// ShowWanningListAdapter.lastbean.addAll(mMyTeamBeans1);
				// mMyTeamListAdapter.notifyDataSetChanged();
//				mMyTeamListAdapter = new ShowWanningListAdapter(activity,
//						warnList);
//				// System.out.println("activity刷新报警这儿执行了吗"+ShowWanningListAdapter.lastbean.size());
//				mMyTeam.setAdapter(mMyTeamListAdapter);
				warnadapterList.clear();
				warnadapterList.addAll(warnList);
				mMyTeamListAdapter.notifyDataSetChanged();
				progress.setVisibility(View.GONE);
				long e=System.currentTimeMillis();
				print("刷新报警ye完成了"+(e-s));
				break;
			case REFRESH:
				warnadapterList.clear();
				warnadapterList.addAll(warnList);
				mMyTeamListAdapter.notifyDataSetChanged();
			}
			super.handleMessage(msg);
		}
	};

	public void callInfoAction() {
		closeReqest();
		
		String uuid = UUID.randomUUID().toString();
		
		alertareaThread task = new alertareaThread(uuid);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			task.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);}
			else{
				task.execute();
			}
		asyntaskmap.put(uuid, task);
			
	}

	private HashMap<String, alertareaThread> asyntaskmap = new HashMap<String, alertareaThread>();

	private void clearMapByUUID(String uuid) {
		asyntaskmap.remove(uuid);
	}

	private void closeReqest() {
		if (asyntaskmap.isEmpty())
			return;

		Iterator<String> it = asyntaskmap.keySet().iterator();

		while (it.hasNext()) {
			String uuid = it.next();
			alertareaThread task = asyntaskmap.get(uuid);
			task.cancel(true);
		}
	}
	public void callwanAction() {
		wanningThread wan = new wanningThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			wan.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);}
			else{
				wan.execute();
			}
	}

	public void onClick(View view) {
		super.onClick(view);
		if (warnList.size() > 0) {
			switch (view.getId()) {
			case R.id.ll_nav_right:
				buttonflag = !buttonflag;
				for (int j = 0; j < warnList.size(); j++) {
					if (buttonflag) {
						setTextNavRight("全不选");
					} else {
						setTextNavRight("全选");
					}
					app.mEditor.putBoolean(warnList.get(j).getLayerId(),
							buttonflag);
					app.mEditor.commit();
                   System.out.println(""+warnList.get(j).Name+app.myPreferences.getBoolean(warnList.get(j).getLayerId(), false));
				}

				Message message = new Message();
				message.what = REFRESH;
				handler.sendMessage(message);
				break;
			}
		}
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		// tmMyTeamBeans1.clear();
		//print("解析警报区域.");
		String myFleet = LoginActivity.myFleet;
		alertplots.clear();
		SAXReader reader = new SAXReader();
		org.dom4j.Document document = reader.read(inStream);
		//print("document: "+document.asXML());
		org.dom4j.Node root = document.selectSingleNode("/root");
		
		String xpath ="/root/model/layer[@Name='"+myFleet+"']//layer";
		List<org.dom4j.Element> layers = root.selectNodes(xpath);
		
		for(org.dom4j.Element layer:layers){
			String layerid = layer.attributeValue("LayerId");
			if (app.myPreferences.getBoolean(layerid, false)) {				
				xpath  = "/root/model/layer[@Name='"+myFleet+"']//layer[@LayerId='"+layerid+"']//plot";
				List<org.dom4j.Element> plots = root.selectNodes(xpath);
				for(org.dom4j.Element plot:plots){
					String plotid = plot.attributeValue("PlotId");
					if(alertplots.contains(plotid)){
						//alertplots.remove(plotid);	
						continue;
					}
					alertplots.add(plotid);
				}
			}
		}
	}

	private void parseXMLnew1(InputStream inStream) throws Exception {
		//print("解析activity内报警");
		long s=System.currentTimeMillis();
		warnList.clear();
		SAXReader reader = new SAXReader();
		org.dom4j.Document document = reader.read(inStream);
		String myFleet= LoginActivity.myFleet;
Map<String,org.dom4j.Element> map = new HashMap<String,org.dom4j.Element>();
List<org.dom4j.Element> root = document.getRootElement().elements();


//		if (root.getNodeName().compareTo("session__timeout") == 0) {
//			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			
//		Map<String, org.dom4j.Element> map = new HashMap<String, org.dom4j.Element>();
//		List<org.dom4j.Element> root = document.getRootElement().elements();
		
		//print("MoreActivity 》 报警结果中 Plot 个数: "+root.size());
		
		for (org.dom4j.Element  e : root) {
			 map.put(e.attribute("Name").getText(), e);
		}
		
		 for(String p:map.keySet()){
			 org.dom4j.Element rse = map.get(p);
			if(rse!=null){
				List<org.dom4j.Element>  rslist = rse.elements();

				// print("MoreActivity 》 筛选目标 个数： "+rslist.size());

				 if(rslist!=null){
				for(org.dom4j.Element e:rslist){
				String fleetname=e.attributeValue("Name");

				 //print("MoreActivity 》 筛选fleetlayer 目标 name： "+fleetname);

				if(myFleet.equals(fleetname)){
					List<org.dom4j.Element> layers= e.elements();
					for(org.dom4j.Element e2:layers){
            			String layerid = e2.attributeValue("LayerId");//.getText();
            			String layername = e2.attributeValue("Name");//.getText();
            			
            			WanningBean wb = new WanningBean();
            			
            			wb.Name = layername;
            			wb.LayerId = layerid;
            			warnList.add(wb);    
            			//System.out.println(mmsi+", "+shipname+", "+TriggerTime);
            		}
				}
				}
			}
			}
		 }
//		 long e=System.currentTimeMillis();
//		print("报警列表解析完成 "+warnList.size()+"time"+(e-s));
//
//		for (int i = 0; i < layers.getLength(); i++) {
//			Node layer = layers.item(i);
//			WanningBean wbb = new WanningBean();
//			String layerid = xpath.evaluate("@LayerId", layer,
//					XPathConstants.STRING).toString();
//			String layername = xpath.evaluate("@Name", layer,
//					XPathConstants.STRING).toString();
//			wbb.Name = layername;
//			wbb.LayerId = layerid;
//			//print("layername: "+layername);
//			lmMyTeamBeans1.add(wbb);
//			//ltmMyTeamBeans1.add(wbb);
//
//		}
	//	print("报警列表解析完成 "+lmMyTeamBeans1.size());
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			
			finish();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
