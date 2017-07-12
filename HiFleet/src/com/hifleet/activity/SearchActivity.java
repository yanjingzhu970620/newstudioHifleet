package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.e.common.widget.CleanableEditText;
import com.hifleet.plus.R;
import com.hifleet.adapter.ShipsListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.SearchShipsBean;
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
public class SearchActivity extends HBaseActivity {
	public static final int REFRESH = 20001;
	public static SearchActivity searchActivity;
	CleanableEditText mEditShip;
	ListView mListShips;
	OsmandApplication app;

	ShipsListAdapter mAdapter;

	ProgressBar progress;

	public static RelativeLayout progressLoading;

	String shipName = null;

	private List<SearchShipsBean> searchShipsBean = new ArrayList<SearchShipsBean>();
	private List<SearchShipsBean> allsearchShipsBean = new ArrayList<SearchShipsBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		searchActivity = this;
		app=this.getMyApplication();
		
		mListShips = (ListView) findViewById(R.id.list_ships);
		mEditShip = (CleanableEditText) findViewById(R.id.edit_ship);
		mEditShip.addTextChangedListener(shipsWatcher);
		progress = (ProgressBar) findViewById(R.id.progress);
		progressLoading = (RelativeLayout) findViewById(R.id.progress_loading);

		
		
		for(int i=app.Searchshipkey.size()-1;i>=0;i--){
			SearchShipsBean s=new SearchShipsBean();
			s.key=app.Searchshipkey.get(i);
		    searchShipsBean.add(s);
		}
		mAdapter = new ShipsListAdapter(SearchActivity.this, searchShipsBean);
		mListShips.setAdapter(mAdapter);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.progress_loading:
			break;
		}
	}

	private TextWatcher shipsWatcher = new TextWatcher() {

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
			String nameString = mEditShip.getText().toString();
			shipName = nameString.replace(" ", "%20");
			if (allsearchShipsBean.size() > 0) {
				allsearchShipsBean.clear();
			}
			if (!nameString.equals("")) {
				LodingShipsNameThread searchship = new LodingShipsNameThread();
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
				if (childElement.getNodeName().compareTo("ship") == 0) {
					allsearchShipsBean.add(XmlParseUtility.parse(childElement,
							SearchShipsBean.class));
				}
			}
			// System.out.println("allsearchShipsBean"+allsearchShipsBean.size());
			Message message = new Message();
			message.what = REFRESH;
			handler.sendMessage(message);
		}
	}

	class LodingShipsNameThread extends AsyncTask<String, Void, Void> {
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
						null) + IndexConstants.FUZZY_SEARCH_URL + shipName;
				//print(httpPost);
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

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH:
				searchShipsBean.clear();
				mAdapter.notifyDataSetChanged();
				searchShipsBean.addAll(allsearchShipsBean);
				// System.out.println("searchShipsBean"+searchShipsBean.size());
				mAdapter.notifyDataSetChanged();
			}
			super.handleMessage(msg);
		}
	};

	
	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

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
	
	
	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}
}
