package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.app.AlertDialog;
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

import com.hifleet.plus.R;
import com.hifleet.adapter.WanningListAdapter;
import com.hifleet.bean.WanningBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;

/**
 * @{# MoreActivity.java Create on 2015年3月10日 上午9:34:56
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class MoreActivity extends HBaseActivity {

	private List<WanningBean> wanningBean = new ArrayList<WanningBean>();
	private List<WanningBean> wanningadapterBean = new ArrayList<WanningBean>();
	public static List<String> alertplots = new ArrayList<String>();
	alertareaThread areatask = new alertareaThread();
	WarningThread warntask = new WarningThread();
	ListView mListWanning;

	WanningListAdapter mAdapter;
	private final static int REFRESH=70001;
	OsmandApplication app;

	String wanning;
	long d;

	ProgressBar progress;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		d = System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);

		initNav();
		setNavTitle("报警信息");
		setTextNavLeft("返回");

		progress = (ProgressBar) findViewById(R.id.progress);
		mListWanning = (ListView) findViewById(R.id.list_wanning);
		mAdapter = new WanningListAdapter(activity, wanningadapterBean);
		mListWanning.setAdapter(mAdapter);
//		print("MoreActivity 》");
		callareaInfoAction();

	}

	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.effectButton_delete:
			mListWanning.setVisibility(View.GONE);
			break;
		case R.id.text_nav_left:
			if (warntask != null) {
				warntask.cancel(true);
			}
			if (areatask != null) {
				areatask.cancel(true);
			}
			finish();
			break;
		}
	}

	private HashMap<String, alertareaThread> asyntaskmap = new HashMap<String, alertareaThread>();

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

	public void callareaInfoAction() {
		closeReqest();
		print("MoreActivity 》callareaInfoAction 报警数据");
		areatask = new alertareaThread();
		String uuid = UUID.randomUUID().toString();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			areatask.executeOnExecutor(Executors.newCachedThreadPool(),
					new String[0]);
		} else {
			areatask.execute();
		}
		asyntaskmap.put(uuid, areatask);

	}

	class alertareaThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				print("MoreActivity 》 第一个异步线程，目标：获得所有报警信息的plotid 与选定的报警区域的plotid进行匹配，获得alertplotlist。");
				long s = System.currentTimeMillis();
				String userId = app.myPreferences.getString("User", null);
				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.GET_LAYER_LIST_URL
						+ userId
						+ "&userDomain=qq.com";
				// print("MoreActivity 》 获得所有报警的plotid " + httpPost);
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
				if (this.isCancelled())
					return null;
				InputStream inStream = conn.getInputStream();
				// print("MoreActivity 》 解析报警区域列表数据");
				if (this.isCancelled())
					return null;
				parsealertXML(inStream);
				if (this.isCancelled())
					return null;
				inStream.close();
				long e = System.currentTimeMillis();
				print("MoreActivity 》 第一个线程doinbackgroundbufen完成，耗时： "
						+ (e - s) / 1000.0);
			} catch (Exception e) {
				System.out.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// print("MoreActivity 》 alertareaThread 》 onPostExecute");
			// new WarningThread().execute();
			closewarnReqest();
			String uuid = UUID.randomUUID().toString();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				warntask.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
				// warntask.execute();
			} else {
				warntask.execute();
			}

			// warntask.execute();
			warntasks.put(uuid, warntask);
		}
	}

	private void parsealertXML(InputStream inStream) throws Exception {
		long s = System.currentTimeMillis();
		String myFleet = LoginActivity.myFleet;
		alertplots.clear();
		SAXReader reader = new SAXReader();
		org.dom4j.Document document = reader.read(inStream);
		// print("document: "+document.asXML());
		org.dom4j.Node root = document.selectSingleNode("/root");

		String xpath = "/root/model/layer[@Name='" + myFleet + "']//layer";
		List<Element> layers = root.selectNodes(xpath);

		for (Element layer : layers) {
			String layerid = layer.attributeValue("LayerId");
			if (app.myPreferences.getBoolean(layerid, false)) {
				xpath = "/root/model/layer[@Name='" + myFleet
						+ "']//layer[@LayerId='" + layerid + "']//plot";
				List<Element> plots = root.selectNodes(xpath);
				for (Element plot : plots) {
					String plotid = plot.attributeValue("PlotId");
					if (alertplots.contains(plotid)) {
						// alertplots.remove(plotid);
						continue;
					}
					alertplots.add(plotid);
				}
			}
		}
		long e = System.currentTimeMillis();
		print("MoreActivity 》 解析第一个xml: 耗时： " + (e - s) / 1000.0);

		/*
		 * 
		 * 
		 * DocumentBuilderFactory factory =
		 * DocumentBuilderFactory.newInstance(); DocumentBuilder builder =
		 * factory.newDocumentBuilder(); Document document =
		 * builder.parse(inStream); XPath xpath =
		 * XPathFactory.newInstance().newXPath();
		 * 
		 * String myFleet = LoginActivity.myFleet; NodeList layers = (NodeList)
		 * xpath.evaluate("/root/model/layer[@Name='" + myFleet + "']/layer",
		 * document, XPathConstants.NODESET); for (int i = 0; i <
		 * layers.getLength(); i++) { if (areatask.isCancelled()) return; Node
		 * layer = layers.item(i); WanningBean wbb = new WanningBean(); String
		 * layerid = xpath.evaluate("@LayerId", layer,
		 * XPathConstants.STRING).toString(); wbb.LayerId = layerid; NodeList
		 * plots = (NodeList) xpath.evaluate( "/root/model/layer[@Name='" +
		 * myFleet + "']/layer[@LayerId='" + layerid + "']//plot", document,
		 * XPathConstants.NODESET);
		 * 
		 * if (app.myPreferences.getBoolean(layerid, false)) { for (int i1 = 0;
		 * i1 < plots.getLength(); i1++) { if (areatask.isCancelled()) return;
		 * Node plot = plots.item(i1); String plotid = xpath.evaluate("@PlotId",
		 * plot, XPathConstants.STRING).toString(); if
		 * (alertplots.contains(plotid)) { alertplots.remove(plotid); }
		 * alertplots.add(plotid); }
		 * 
		 * } }
		 */

	}

	private HashMap<String, WarningThread> warntasks = new HashMap<String, WarningThread>();

	private void closewarnReqest() {
		if (warntasks.isEmpty())
			return;

		Iterator<String> it = warntasks.keySet().iterator();

		while (it.hasNext()) {
			String uuid = it.next();
			WarningThread task = warntasks.get(uuid);
			task.cancel(true);
		}
	}

	class WarningThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				print("MoreActivity 》 第二个线程，目标：获得报警信息与alertplotlist 匹配，拿到应该显示的报警信息。 ");
				long s = System.currentTimeMillis();
				Date date = new Date();
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd-HH-mm-ss");
				String time = sDateFormat.format(date);
				// System.out.println("time====" + time);
				String mEndTime = time;
				date.setHours(date.getHours() - 24);
				time = sDateFormat.format(date);
				String mStartTime = time;
				String userId = app.myPreferences.getString("User", null);
				String mUrl = app.myPreferences.getString("loginserver", null)
						+ IndexConstants.GET_WANNING_URL + userId
						+ "&userDomain=qq.com&starttime=" + mStartTime
						+ "&endtime=" + mEndTime + "&AlertType=All";
				// print("MoreActivity 》 WarningThread: " + mUrl);
				URL shipsUrl = new URL(mUrl);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
					// System.out.println("用的loginSession.getSessionid哦");
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				}
				conn.setConnectTimeout(5000);
				if (this.isCancelled())
					return null;
				InputStream inStream = conn.getInputStream();
				// print("MoreActivity 》 WarningThread: 解析报警数据");
				if (this.isCancelled())
					return null;
				parseXMLnew(inStream);
				if (this.isCancelled())
					return null;
				inStream.close();
				long e = System.currentTimeMillis();
				print("MoreActivity 》 第二个异步线程doinbackground部分结束，耗时：" + (e - s)
						/ 1000.0);
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
			try {
				progress.setVisibility(View.GONE);
				// wanningBean.clear();
				// print("MoreActivity 》 对报警进行判断了" + wanningBean.size());
				if (wanningBean.size() == 0) {
					new AlertDialog.Builder(activity).setTitle("提示")
							.setCancelable(false).setMessage("暂无报警信息或无勾选报警区域！")
							.setPositiveButton("确定", null).show();
				}
				OfflineUserComparator offlineUserComparator = new OfflineUserComparator();
				Collections.sort(wanningBean, offlineUserComparator);
//				mAdapter = new WanningListAdapter(activity, wanningBean);
//				mListWanning.setAdapter(mAdapter);
				Message message = new Message();
				message.what = REFRESH;
				handler.sendMessage(message);
				mListWanning.setVisibility(View.VISIBLE);

				long end = System.currentTimeMillis();

				print("总共耗时: " + (end - d) / 1000.0);

			} catch (java.util.ConcurrentModificationException cme) {
				// System.out.println("报警 Exception");
			}
		}
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		wanningBean.clear();

		SAXReader reader = new SAXReader();
		org.dom4j.Document document = reader.read(inStream);

		Map<String, Element> map = new HashMap<String, Element>();
		List<Element> root = document.getRootElement().elements();
		print("MoreActivity 》 报警结果中 Plot 个数: " + root.size());
		print("MoreActivity 》 筛选目标 个数： " + alertplots.size());
		long s = System.currentTimeMillis();
		for (Element e : root) {
			map.put(e.attribute("PlotId").getText(), e);
		}

		for (String p : alertplots) {
			Element rse = map.get(p);
			if (rse != null) {
				String plotname = rse.attributeValue("PlotName");
				// print("MoreActivity 》 筛选目标name： "+plotname);
				List<Element> rslist = rse.elements();
				if (rslist != null) {
					for (Element e : rslist) {
						if (warntask.isCancelled())
							return;
						String mmsi = e.attributeValue("Mmsi");// .getText();
						String shipname = e.attributeValue("Shipname");// .getText();
						String TriggerTime = e.attributeValue("TriggerTime");
						String speedtype = e.attributeValue("SpeedType");
						String speed = e.attributeValue("Speed");
						WanningBean wb = new WanningBean();
						wb.PlotName = plotname;
						wb.Mmsi = mmsi;
						wb.Shipname = shipname;
						wb.TriggerTime = TriggerTime;
						wb.SpeedType = speedtype;
						wb.Speed = speed;
						wanningBean.add(wb);
						// System.out.println(mmsi+", "+shipname+", "+TriggerTime);
					}
				}
			}
		}
		long e = System.currentTimeMillis();
		print("MoreActivity 解析第二个xml 》 耗时：" + (e - s) / 1000.0);
		/*
		 * DocumentBuilderFactory factory =
		 * DocumentBuilderFactory.newInstance(); DocumentBuilder builder =
		 * factory.newDocumentBuilder(); Document document =
		 * builder.parse(inStream); XPath xpath =
		 * XPathFactory.newInstance().newXPath(); NodeList plots = (NodeList)
		 * xpath.evaluate("/root/Plot", document, XPathConstants.NODESET);
		 * 
		 * <<<<<<< .mine
		 * 
		 * int count=0;
		 * 
		 * 
		 * for(String p:alertplots){
		 * 
		 * print("MoreActivity 》 筛选目标: "+p); NodeList plotlist = (NodeList)
		 * xpath.evaluate("/root/Plot[@PlotId='" + p + "']//AlertRs", document,
		 * XPathConstants.NODESET);
		 * 
		 * for(int i1=0;i1<plotlist.getLength();i1++){
		 * 
		 * Node alertrs = plotlist.item(i1); WanningBean wb = new WanningBean();
		 * 
		 * wb.Mmsi = xpath.evaluate("@Mmsi", alertrs,
		 * XPathConstants.STRING).toString();
		 * //print("MoreActivity 》 报警船mmsi: "+wb.Mmsi); wb.Shipname =
		 * xpath.evaluate("@Shipname", alertrs,
		 * XPathConstants.STRING).toString(); wb.TriggerTime =
		 * xpath.evaluate("@TriggerTime", alertrs,
		 * XPathConstants.STRING).toString(); wb.SpeedType =
		 * xpath.evaluate("@SpeedType", alertrs,
		 * XPathConstants.STRING).toString(); wb.Speed =
		 * xpath.evaluate("@Speed", alertrs, XPathConstants.STRING).toString();
		 * wanningBean.add(wb); }
		 * 
		 * }
		 */

		// long g = System.currentTimeMillis() - d;
		// print("MoreActivity 》 耗时： " + g/1000.0+", 消息数： "+wanningBean.size());

	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (warntask != null) {
				warntask.cancel(true);
			}
			if (areatask != null) {
				areatask.cancel(true);
			}
			finish();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public class OfflineUserComparator implements Comparator {

		@Override
		public int compare(Object obj1, Object obj2) {
			WanningBean user1 = (WanningBean) obj1;
			WanningBean user2 = (WanningBean) obj2;
			
			int flag = user2.TriggerTime.compareTo(user1.TriggerTime);
			return flag;
		}
	}
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case REFRESH:
				
				wanningadapterBean.clear();
				wanningadapterBean.addAll(wanningBean);
				mAdapter.notifyDataSetChanged();
			}
			super.handleMessage(msg);
		}
	};
}
