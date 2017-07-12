package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.e.common.widget.CleanableEditText;
import com.hifleet.plus.R;
import com.hifleet.adapter.PortListAdapter;
import com.hifleet.adapter.ShipsListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.InportShipsBean;
import com.hifleet.bean.PortBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# SearchActivity.java Create on 2015年3月3日 上午10:53:28
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class GetArriveShipsActivity extends HBaseActivity {
	public static final int REFRESH = 20001;
	public static GetArriveShipsActivity searchActivity;
	public static CleanableEditText mEditPort;
	ListView mListShips;
	OsmandApplication app;

	static PortListAdapter mAdapter;

	ProgressBar progress;

	public static RelativeLayout progressLoading;

	String portName = null;
	public static PortBean mPort;
	public static List<PortBean> searchPortBean = new ArrayList<PortBean>();
	public static List<PortBean> allsearchPortBean = new ArrayList<PortBean>();
	public static List<InportShipsBean> inportships = new ArrayList<InportShipsBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	
	private List<HashMap<String,String>> dwtid=new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> typeid=new ArrayList<HashMap<String,String>>();
	
	TextView mtitle;
	private Spinner spDwt ;
	private Spinner spType ;
	private ArrayAdapter<CharSequence> spDwtadapter = null;
	private ArrayAdapter<CharSequence> spTypeadapter = null;
	String[] rDwt;
	String[] rType;
	public static String stype="0";
	public static String sdwt="0";
	public static int total=0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inport);

		searchActivity = this;
		
		mListShips = (ListView) findViewById(R.id.list_ports);
		mEditPort = (CleanableEditText) findViewById(R.id.edit_port_start);
		mEditPort.addTextChangedListener(portsWatcher);
		progress = (ProgressBar) findViewById(R.id.progress);
		progressLoading = (RelativeLayout) findViewById(R.id.progress_loading);
		
		mtitle=(TextView) findViewById(R.id.portships_activity_title);
		mtitle.setText("最近抵港");
		spDwt=(Spinner) findViewById(R.id.Spinner02);
		spType=(Spinner) findViewById(R.id.Spinner01);
		rDwt = new String[]{"不限","<=500","500-1000","1000-3000","3000-10000","10000-50000",">50000"};
		rType = new String[]{"全部","集装箱船","散货船","石油化学品船","杂货船","气槽船","滚装船","客船","工程及服务船",
				"近海作业船","拖轮","渔船","专用船","游艇","其他类型液货船","其他","未知类型干货船","未知类型液货船"};
		for(int i=0;i<rDwt.length;i++){
		HashMap m=new HashMap();
		m.put(rDwt[i], i+"");
		dwtid.add(m);
		}
		for(int i=0;i<rType.length;i++){
			HashMap m=new HashMap();
			m.put(rType[i], i+"");
			typeid.add(m);
		}
		spDwtadapter=new ArrayAdapter<CharSequence>(this,
			android.R.layout.simple_dropdown_item_1line,rDwt);
        spDwt.setAdapter(spDwtadapter);
        spDwt.setOnItemSelectedListener(new OnItemSelectedListener(){

    		@Override
    		public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
    				long arg3) {
    			// TODO Auto-generated method stub
    			TextView v1 = (TextView)v;
   			    v1.setTextColor(Color.BLACK);
//    			sdwt=spDwt.getSelectedItem().toString();
    			for(int i=0;i<dwtid.size();i++){
    				if(dwtid.get(i).containsKey(spDwt.getSelectedItem().toString())){
    					sdwt=dwtid.get(i).get(spDwt.getSelectedItem().toString());
//    					System.out.println("spDwt选择了：：："+sdwt);
    				}
    			}
//    			System.out.println("spDwt选择了：：："+sdwt);
    		}

    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {
    			// TODO Auto-generated method stub
    			
    		}
           });
        spTypeadapter=new ArrayAdapter<CharSequence>(this,
    			android.R.layout.simple_dropdown_item_1line,rType);
            spType.setAdapter(spTypeadapter);
            spType.setOnItemSelectedListener(new OnItemSelectedListener(){

        		@Override
        		public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
        				long arg3) {
        			// TODO Auto-generated method stub
        			TextView v1 = (TextView)v;
        			 v1.setTextColor(Color.BLACK);
//        			stype=spType.getSelectedItem().toString();
//        			System.out.println("spType选择了：：："+stype);
        			for(int i=0;i<typeid.size();i++){
        				if(typeid.get(i).containsKey(spType.getSelectedItem().toString())){
        					stype=typeid.get(i).get(spType.getSelectedItem().toString());
//        					System.out.println("spType选择了：：："+stype);
        				}
        			}
        		}

        		@Override
        		public void onNothingSelected(AdapterView<?> arg0) {
        			// TODO Auto-generated method stub
        			
        		}
               });
  
		mAdapter = new PortListAdapter(activity, searchPortBean,mEditPort);
		mListShips.setAdapter(mAdapter);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.progress_loading:
			break;
		case R.id.effectButton_search_inportships:
			if(portName!=null){
//				System.out.println("查询港口船舶"+sdwt+stype+portName);
				if(inportships!=null){
				inportships.clear();
				}
				
				LodingArrivePortShipsThread LodingShips=new LodingArrivePortShipsThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					LodingShips.executeOnExecutor(
							Executors.newCachedThreadPool(), new String[0]);
				} else {
					LodingShips.execute();
				}
				progressLoading.setVisibility(View.VISIBLE);
			}else{
				Toast.makeText(getBaseContext(), "港口名称未填写", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	private TextWatcher portsWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String nameString = mEditPort.getText().toString();
//			mAdapter.mPort;
			portName=null;
			portName = nameString.replace(" ", "%20");
			if (allsearchPortBean.size() > 0) {
				allsearchPortBean.clear();
			}
			if (!nameString.equals("")) {
				LodingportsNameThread searchship = new LodingportsNameThread();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					searchship.executeOnExecutor(
							Executors.newCachedThreadPool(), new String[0]);
				} else {
					searchship.execute();
				}
				progress.setVisibility(View.VISIBLE);
			}
		}
	};

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		heartBeatBean.clear();
		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("port") == 0) {
					allsearchPortBean.add(XmlParseUtility.parse(childElement,
							PortBean.class));
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
					inportships.add(is);
//					System.out.println("解析的inportships"+is.mmsi);
				}
				if (childElement.getNodeName().compareTo("total") == 0) {
					total=Integer.parseInt(childElement.getTextContent());
				}
			}
			// System.out.println("allsearchPortBean"+allsearchPortBean.size());
			Message message = new Message();
			message.what = REFRESH;
			handler.sendMessage(message);
		}
	}
	
	class LodingportsNameThread extends AsyncTask<String, Void, Void> {
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
						null) + IndexConstants.PORT_SEARCH_URL + portName;
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
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}
			progress.setVisibility(View.GONE);
		}
	}

	class LodingArrivePortShipsThread extends AsyncTask<String, Void, Void> {
//		long s;
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
//				s=System.currentTimeMillis();
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.ARRIVEPORT_SHIPS_URL + sdwt+"&limit1="+"0"+"&limit2="+"10"+"&portcode="+
						mPort.getPortCode()+"&type="+stype;
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
//				System.out.println("抵港船舶  请求完成  花费时间：：："+(System.currentTimeMillis()-s));
				parseXMLnew(inStream);
//				System.out.println("抵港船舶  解析完成  花费时间：：："+(System.currentTimeMillis()-s));
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
//			System.out.println("抵港船舶  线程完成花费时间：：："+(System.currentTimeMillis()-s));
			if (inportships.size() > 0) {
				progress.setVisibility(View.GONE);
//				System.out.println("点击了："+inportships.size() +"进行跳转");
				Intent intent = new Intent(GetArriveShipsActivity.this, PortShipsActivity.class);
//				System.out.println("点击了："+"进行跳转");
//				progress.setVisibility(View.GONE);
				progressLoading.setVisibility(View.GONE);
				GetArriveShipsActivity.this.startActivity(intent);
				// ((SearchActivity) context).finish();
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
	
	public static Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH:
				searchPortBean.clear();
				mAdapter.notifyDataSetChanged();
				searchPortBean.addAll(allsearchPortBean);
				// System.out.println("searchPortBean"+searchPortBean.size());
				mAdapter.notifyDataSetChanged();
			}
			super.handleMessage(msg);
		}
	};

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
//	@Override
//	protected void onResume(){
//		super.onResume();
//		progress.setVisibility(View.GONE);
//	}
	
}
