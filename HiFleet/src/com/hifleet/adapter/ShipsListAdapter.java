package com.hifleet.adapter;

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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.plus.R;
import com.hifleet.activity.ChooseShipActivity;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.SearchActivity;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.SearchShipsBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# ShipsListAdapter.java Create on 2015年4月14日 上午11:14:11
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipsListAdapter extends BaseAdapter {
	private SearchActivity searchactivity;
	private List<SearchShipsBean> searchShipsBean;
	public static List<ShipsBean> shipsBeans = new ArrayList<ShipsBean>();
	private List<HeartBeatBean> outBeans = new ArrayList<HeartBeatBean>();
	public static Boolean isShow = false;
	public static Boolean isMove = false;
	String mShipName;
	OsmandApplication app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public ShipsListAdapter(SearchActivity searchactivity,
			List<SearchShipsBean> searchShipsBean) {
		this.searchactivity = searchactivity;
		this.searchShipsBean = searchShipsBean;
		this.app=searchactivity.getMyApplication();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchShipsBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchShipsBean.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(searchactivity).inflate(
				R.layout.item_ships_search, null);
		TextView shipsText = (TextView) convertView
				.findViewById(R.id.text_ships);
		final SearchShipsBean m = searchShipsBean.get(position);
		shipsText.setText(m.getKey());
		EffectColorRelativeLayout shipsEffectColorRelativeLayout = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_ships);
		shipsEffectColorRelativeLayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Message message = new Message();
						message.what = 000;
						SearchActivity.newHandler.sendMessage(message);
						if (shipsBeans != null) {
							shipsBeans.clear();
						}
						InputMethodManager imm = (InputMethodManager) searchactivity
								.getSystemService(searchactivity.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						mShipName = m.getKey();
						
						if(!app.Searchshipkey.contains(mShipName)){
							app.Searchshipkey.add(mShipName);
							}else{
								int i=app.Searchshipkey.indexOf(mShipName);	
								app.Searchshipkey.remove(i);
								app.Searchshipkey.add(mShipName);
							}
						
						if(app.Searchshipkey.size()>20){
							app.Searchshipkey.remove(0);
						}
						app.saveSearchArray();//搜索的船舶字符保存
						
						ShipChoosedThread sc = new ShipChoosedThread();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
							sc.executeOnExecutor(
									Executors.newCachedThreadPool(),
									new String[0]);
						} else {
							sc.execute();
						}
					}
				});
		return convertView;
	}

	class ShipChoosedThread extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost;
				mShipName = mShipName.replace(" ", "%20");

//				System.out.println("DEVICE_ID==="+app.myPreferences.getString("DEVICE_ID", null)
//						+"app.myPreferences.getString(loginserver, null)"+app.myPreferences.getString("loginserver", null)
//						+app.getMyrole());

//				if(app.getMyrole()==null){System.err.println("app.getMyrole()==null");}else{
//					System.err.println("app.getMyrole()!=null");
//				}

				if (app.myPreferences.getBoolean("IsLogin", false)) {
					httpPost = app.myPreferences.getString("loginserver", null)
							+ IndexConstants.GET_CHOOSED_SHIP_URL + mShipName;
				} else {
					httpPost = app.myPreferences.getString("loginserver", null)
							+ IndexConstants.MOBILE_SEARCH_VESSEL_FREE
							+ mShipName + "&deviceid="
							+ app.myPreferences.getString("DEVICE_ID", null);
				}

				System.out.println("搜索地址===" + httpPost);


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
				conn.setReadTimeout(10000);

				InputStream inStream = conn.getInputStream();

				parseXMLnew(inStream);
				inStream.close();

			} catch (Exception e) {
				// TODO: handle exception
				print("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (outBeans.size() > 0) {
				Message message = new Message();
				message.what = 111;
				SearchActivity.newHandler.sendMessage(message);
				for (HeartBeatBean h : outBeans) {
					new AlertDialog.Builder(searchactivity)
							.setTitle("提示")
							.setCancelable(false)
							.setMessage("未登录用户每日只能查询10艘船舶")
							.setNegativeButton("取消", null)
							.setPositiveButton(
									"登录",
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(searchactivity,
													IsLoginActivity.class);
											searchactivity.startActivity(intent);
											((SearchActivity) searchactivity)
													.overridePendingTransition(
															R.drawable.activity_open,
															0);
										}
									}).show();
				}
				outBeans.clear();
				return;
			}
			if (shipsBeans.size() > 0) {
				Message message = new Message();
				message.what = 111;
				// print("点击了："+mShipName);
				SearchActivity.newHandler.sendMessage(message);
				Intent intent = new Intent(searchactivity, ChooseShipActivity.class);
				// print("点击了："+mShipName+"进行跳转");
				searchactivity.startActivity(intent);
				// ((SearchActivity) searchactivity).finish();
			} else {
				Message message = new Message();
				message.what = 111;
				// print("点击了："+mShipName);
				SearchActivity.newHandler.sendMessage(message);
				Toast.makeText(searchactivity, "船舶过期", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("session__timeout") == 0) {
			outBeans.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					shipsBeans.add(XmlParseUtility.parse(childElement,
							ShipsBean.class));
				}
			}
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

}
