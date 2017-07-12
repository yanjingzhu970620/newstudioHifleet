package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hifleet.plus.R;
import com.hifleet.adapter.MyTeamListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.MyTeamBean;
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
public class MyTeamActivity extends HBaseActivity {

	ListView mMyTeam;

	OsmandApplication app;

	ProgressBar progress;

	MyTeamListAdapter mMyTeamListAdapter;

	private List<MyTeamBean> mMyTeamBeans = new ArrayList<MyTeamBean>();
	private List<MyTeamBean> mMyTeamBeans1 = new ArrayList<MyTeamBean>();
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
		setContentView(R.layout.activity_my_team);

		initNav();
		setNavTitle("team");

		progress = (ProgressBar) findViewById(R.id.progress);

		mMyTeam = (ListView) findViewById(R.id.list_my_team);

		MyTeamThread mt=new MyTeamThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			mt.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);}
			else{
				mt.execute();
			}

		app.getInstance().addActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MyTeamThread mt=new MyTeamThread();
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			//print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//			mt.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);}
//			else{
//				mt.execute();
//			}
	}
	class MyTeamThread extends AsyncTask<String, Void, Void> {
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
						null) + IndexConstants.GET_MY_TEAM_NAME_URL;
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

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					Toast.makeText(getBaseContext(), "会话超时，未能获取网络数据", Toast.LENGTH_LONG).show();
					return;
				}
			}

			for (MyTeamBean team : mMyTeamBeans) {
				Boolean isShow = app.myPreferences.getBoolean(team.name, true);
				if (isShow) {
					mMyTeamBeans1.add(team);
				}
			}
//			mMyTeamListAdapter = new MyTeamListAdapter(activity, mMyTeamBeans1);
//			mMyTeam.setAdapter(mMyTeamListAdapter);
//			progress.setVisibility(View.GONE);
		}

	}

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
				if (childElement.getNodeName().compareTo("fleet") == 0) {
					mMyTeamBeans.add(XmlParseUtility.parse(childElement,
							MyTeamBean.class));
				}
			}
		}
	}

}
