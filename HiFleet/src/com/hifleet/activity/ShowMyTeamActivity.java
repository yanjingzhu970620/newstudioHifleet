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
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hifleet.plus.R;
import com.hifleet.adapter.ShowTeamListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# ShowMyTeamActivity.java Create on 2015年5月28日 下午3:35:45
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShowMyTeamActivity extends HBaseActivity {

	ListView mMyTeam;

	OsmandApplication app;

	ShowTeamListAdapter mMyTeamListAdapter;
	
	ProgressBar progress;

	private List<MyTeamBean> mMyTeamBeans = new ArrayList<MyTeamBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

	private String isTeamChoose;

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
		setNavTitle("我的船队");
		
		progress = (ProgressBar)findViewById(R.id.progress);
		mMyTeam = (ListView) findViewById(R.id.list_my_team);

		MyTeamThread myteam=new MyTeamThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			myteam.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);}
			else{
				myteam.execute();
			}
	}

	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.ll_nav_right:
			System.out.println(isTeamChoose.length() + "  ");
			if (isTeamChoose == "全选") {
				setTextNavRight("全不选");
				isTeamChoose = "全不选";
				for (MyTeamBean team : mMyTeamBeans) {
					app.mEditor.putBoolean(team.name, true);
				}
			} else {
				setTextNavRight("全选");
				isTeamChoose = "全选";
				for (MyTeamBean team : mMyTeamBeans) {
					app.mEditor.putBoolean(team.name, false);
				}
			}
			app.mEditor.putString("isTeamChoose", isTeamChoose);
			app.mEditor.commit();
			mMyTeamListAdapter.notifyDataSetChanged();
			break;
		}
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
				System.out.println("MyTeamThread");
				String httpPost = app.myPreferences.getString("loginserver", null)+IndexConstants.GET_MY_TEAM_NAME_URL;
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
			isTeamChoose = app.myPreferences.getString("isTeamChoose", null);
			if (isTeamChoose == null) {
				setTextNavRight("全不选");
				isTeamChoose = "全不选";
			} else {
				setTextNavRight(isTeamChoose);
			}
			mMyTeamListAdapter = new ShowTeamListAdapter(activity, mMyTeamBeans);
			mMyTeam.setAdapter(mMyTeamListAdapter);
			progress.setVisibility(View.GONE);
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
				if (childElement.getNodeName().compareTo("fleet") == 0) {
					mMyTeamBeans.add(XmlParseUtility.parse(childElement,
							MyTeamBean.class));
				}
			}
		}
	}

}
