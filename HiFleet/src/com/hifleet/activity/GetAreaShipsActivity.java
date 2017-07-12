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
import android.widget.LinearLayout;
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
import com.hifleet.bean.ZoneBean;
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
public class GetAreaShipsActivity extends HBaseActivity {
	public static final int REFRESH = 20001;
	protected static final int SETEDITTEXT=20002;
	public static GetAreaShipsActivity searchActivity;
//	public static CleanableEditText mEditPort;
	ListView mListShips;
	OsmandApplication app;

	static PortListAdapter mAdapter;

	ProgressBar progress;

	public static RelativeLayout progressLoading;
	LinearLayout portedit,cArea,shipstop;
//	String portName = null;
//	public static PortBean mPort;
	public static List<ZoneBean> searchZoneBean = new ArrayList<ZoneBean>();
	public static List<ZoneBean> allsearchZoneBean = new ArrayList<ZoneBean>();
	public static List<InportShipsBean> inportships = new ArrayList<InportShipsBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	
	private List<HashMap<String,String>> dwtid=new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> typeid=new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> stopid=new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,ZoneBean>> areaid=new ArrayList<HashMap<String,ZoneBean>>();
	
	private Spinner spArea ;
	private Spinner spDwt ;
	private Spinner spType ;
	private Spinner spStop;
	private ArrayAdapter<CharSequence> spDwtadapter = null;
	private ArrayAdapter<CharSequence> spTypeadapter = null;
	private ArrayAdapter<CharSequence> spAreaadapter = null;
	private ArrayAdapter<CharSequence> spStopadapter = null;
	String[] rDwt;
	String[] rType;
	String[] rArea;
	String[] rStop;
	private TextView mtitle;
	public static String stype="0";
	public static String sdwt="0";
	public static String sarea="0";
	public static String sstop="0";
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
		
//		mListShips = (ListView) findViewById(R.id.list_ports);
//		mEditPort = (CleanableEditText) findViewById(R.id.edit_port_start);
//		mEditPort.addTextChangedListener(portsWatcher);
		mtitle=(TextView) findViewById(R.id.portships_activity_title);
		mtitle.setText("区域船舶");
		progress = (ProgressBar) findViewById(R.id.progress);
		progressLoading = (RelativeLayout) findViewById(R.id.progress_loading);
		portedit = (LinearLayout) findViewById(R.id.start_portedit);
		portedit.setVisibility(View.GONE);
		cArea = (LinearLayout) findViewById(R.id.inportships_area);
		cArea.setVisibility(View.VISIBLE);
		shipstop = (LinearLayout) findViewById(R.id.inportships_stop);
		shipstop.setVisibility(View.VISIBLE);
		
		spArea=(Spinner) findViewById(R.id.Spinnerarea);
		spDwt=(Spinner) findViewById(R.id.Spinner02);
		spType=(Spinner) findViewById(R.id.Spinner01);
		spStop=(Spinner) findViewById(R.id.Spinnerstop);
		
		rDwt = new String[]{"不限","<=500","500-1000","1000-3000","3000-10000","10000-50000",">50000"};
		rArea = new String[]{"请选择区域"};
		rType = new String[]{"全部","集装箱船","散货船","石油化学品船","杂货船","气槽船","滚装船","客船","工程及服务船",
				"近海作业船","拖轮","渔船","专用船","游艇","其他类型液货船","其他","未知类型干货船","未知类型液货船"};
		rStop = new String[]{"不限定",">=1天",">=5天",">=10天",">=15天",">=30天"};
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
		for(int i=0;i<rStop.length;i++){
			HashMap m=new HashMap();
			m.put(rStop[i], i+"");
			stopid.add(m);
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
    					System.out.println("spDwt选择了：：："+sdwt);
    				}
    			}
//    			System.out.println("spDwt选择了：：："+sdwt);
    		}

    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {
    			// TODO Auto-generated method stub
    			
    		}
           });
        
        spAreaadapter=new ArrayAdapter<CharSequence>(this,
    			android.R.layout.simple_dropdown_item_1line,rArea);
        spArea.setAdapter(spAreaadapter);
        
        spStopadapter=new ArrayAdapter<CharSequence>(this,
    			android.R.layout.simple_dropdown_item_1line,rStop);
        spStop.setAdapter(spStopadapter);
        spStop.setOnItemSelectedListener(new OnItemSelectedListener(){

        		@Override
        		public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
        				long arg3) {
        			// TODO Auto-generated method stub
//        			 System.out.println("spArea.getSelectedItem().toString()选择了：：："+spArea.getSelectedItem().toString());
        			TextView v1 = (TextView)v;
       			    v1.setTextColor(Color.BLACK);
//       			 sarea=spArea.getSelectedItem().toString();
       			
        			for(int i=0;i<stopid.size();i++){
        				if(stopid.get(i).containsKey(spStop.getSelectedItem().toString())){
        					sstop=stopid.get(i).get(spStop.getSelectedItem().toString());
        					System.out.println("spstop选择了：：："+
        					stopid.get(i).get(spStop.getSelectedItem().toString()));
        				}
        			}
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
//        			System.out.println("spType for循环前选择了：：："+spType.getSelectedItem().toString());
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
  
//		mAdapter = new PortListAdapter(searchActivity,searchPortBean,mEditPort);
//		mListShips.setAdapter(mAdapter);
		LodingAreaThread searchship = new LodingAreaThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			searchship.executeOnExecutor(
					Executors.newCachedThreadPool(), new String[0]);
		} else {
			searchship.execute();
		}
		progress.setVisibility(View.VISIBLE);//LodingAreaThread
		
//		rArea = new String[]{"选择区域","id<=500","id500-1000","id1000-3000","3000-10000","10000-50000",">50000"};
//		 spAreaadapter.notifyDataSetChanged();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.progress_loading:
			break;
		case R.id.effectButton_search_inportships:
			if(!sarea.equals("请选择区域")){
//				System.out.println("查询港口船舶"+sdwt+stype+portName);
				if(inportships!=null){
				inportships.clear();
				}
				LodingAreaShipsThread LodingShips=new LodingAreaShipsThread();
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

//	private TextWatcher portsWatcher = new TextWatcher() {
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before,
//				int count) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//			// TODO Auto-generated method stub
//			String nameString = mEditPort.getText().toString();
////			mAdapter.mPort;
//			portName=null;
//			portName = nameString.replace(" ", "%20");
//			if (allsearchPortBean.size() > 0) {
//				allsearchPortBean.clear();
//			}
////			if (!nameString.equals("")) {
////				LodingportsNameThread searchship = new LodingportsNameThread();
////				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
////					searchship.executeOnExecutor(
////							Executors.newCachedThreadPool(), new String[0]);
////				} else {
////					searchship.execute();
////				}
////				progress.setVisibility(View.VISIBLE);//LodingAreaThread
////			}
//		}
//	};

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		heartBeatBean.clear();
		allsearchZoneBean = new ArrayList<ZoneBean>();
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
//					allsearchZoneBean.add(XmlParseUtility.parse(childElement,
//							ZoneBean.class));
	
//					System.out.println("allsearchZoneBean.size():::"+allsearchZoneBean.size());
				}
				
				if (childElement.getNodeName().compareTo("AreaShips") == 0) {
					InportShipsBean is=new InportShipsBean();
					NodeList childNodes2 =childElement.getChildNodes();
					for(int b=0;b<childNodes2.getLength();b++){
						Node childNode2 = (Node) childNodes2.item(b);
						if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
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
					}
					inportships.add(is);
//					System.out.println("解析的inportships"+is.mmsi);
				}
				if (childElement.getNodeName().compareTo("total") == 0) {
					total=Integer.parseInt(childElement.getTextContent());
				}
			}
			// System.out.println("allsearchPortBean"+allsearchPortBean.size());
//			Message message = new Message();
//			message.what = REFRESH;
//			handler.sendMessage(message);
		}
	}
	
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
//				rArea = new String[]{"选择区域","id<=500","id500-1000","id1000-3000","3000-10000","10000-50000",">50000"};
//				 spAreaadapter.notifyDataSetChanged();
//				System.out.println("tigongde选择区域"+rArea);
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
//				System.out.println("search area finish areaid clear"+allsearchZoneBean.size());
			areaid.clear();
			rArea=new String[allsearchZoneBean.size()];
			for(int i=0;i<allsearchZoneBean.size();i++){
				String n=allsearchZoneBean.get(i).getName();
				rArea[i]=n;
				HashMap hm=new HashMap();
				hm.put(n, allsearchZoneBean.get(i));
//				System.out.println("allsearchZoneBean.get(i)name:::"+allsearchZoneBean.get(i).getName()+"id:::"+allsearchZoneBean.get(i).getZoneid());
				areaid.add(hm);
			}
//			for(int j=0;j<areaid.size();j++){
//				System.out.println("areaid.get(j).keySet()"+areaid.get(j).keySet());
//			}
			  spAreaadapter=new ArrayAdapter<CharSequence>(GetAreaShipsActivity.this,
		    			android.R.layout.simple_dropdown_item_1line,rArea);
//			  System.out.println("search area finish spAreaadapter setadapter");
		        spArea.setAdapter(spAreaadapter);
		        
		        spArea.setOnItemSelectedListener(new OnItemSelectedListener(){

	        		@Override
	        		public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
	        				long arg3) {
	        			// TODO Auto-generated method stub
//	        			 System.out.println("spArea.getSelectedItem().toString()选择了：：："+spArea.getSelectedItem().toString());
	        			TextView v1 = (TextView)v;
	       			    v1.setTextColor(Color.BLACK);
//	       			 sarea=spArea.getSelectedItem().toString();
	       			
	        			for(int i=0;i<areaid.size();i++){
	        				if(areaid.get(i).containsKey(spArea.getSelectedItem().toString())){
	        					sarea=areaid.get(i).get(spArea.getSelectedItem().toString()).getBound().replace(" ", "%20");
//	        					System.out.println("spArea选择了：：："+
//	        					areaid.get(i).get(spArea.getSelectedItem().toString()).getBound());
	        				}
	        			}
//	        			System.out.println("spArea选择了：：："+sarea+"areaid:::"+areaid.size());
	        		}

	        		@Override
	        		public void onNothingSelected(AdapterView<?> arg0) {
	        			// TODO Auto-generated method stub
	        			
	        		}
	               });
			} 
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
						null) + IndexConstants.GET_MY_AREASHIPS + sdwt+"&limit1="+"0"+"&limit2="+"10"+"&shape="+
						sarea+"&type="+stype+"&stop="+sstop;
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
				progress.setVisibility(View.GONE);
//				System.out.println("点击了："+inportships.size() +"进行跳转");
				Intent intent = new Intent(GetAreaShipsActivity.this, PortShipsActivity.class);
//				System.out.println("点击了："+"进行跳转");
//				progress.setVisibility(View.GONE);
				progressLoading.setVisibility(View.GONE);
				GetAreaShipsActivity.this.startActivity(intent);
				// ((SearchActivity) context).finish();
			} else {
				progressLoading.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "暂无此区域船舶", Toast.LENGTH_LONG).show();
			}
			
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					System.out.println(h.message);
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
//					UserLogout thread = null;
//					thread = new UserLogout();
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//						// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//						thread.executeOnExecutor(
//								Executors.newCachedThreadPool(), new String[0]);
//					} else {
//						thread.execute();
//					}
//					new AlertDialog.Builder(activity).setTitle("提示")
//							.setCancelable(false)
//							.setMessage("会话超时或账号在其他计算机登录！")
//							.setNegativeButton("确定", new OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//									app.mEditor.putInt("isAutoLogin", 2)
//											.commit();
//									Intent intent = new Intent(activity,
//											LoginActivity.class);
//									startActivity(intent);
//									app.getInstance().exit();
//								}
//							}).show();
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
				searchZoneBean.clear();
				mAdapter.notifyDataSetChanged();
				searchZoneBean.addAll(allsearchZoneBean);
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
