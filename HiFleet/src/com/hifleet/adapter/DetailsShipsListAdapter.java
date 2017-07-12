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
import android.content.Context;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hifleet.plus.R;
import com.hifleet.activity.IsLoginActivity;
import com.hifleet.activity.ShipSimpleDetailActivity;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.SearchShipsBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.fragment.DetailsFragment;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
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
public class DetailsShipsListAdapter extends BaseAdapter {
	private Context context;
	private List<SearchShipsBean> searchShipsBean;
	private List<SearchShipsBean> searchHistory;
	public static List<ShipsBean> shipsBeans = new ArrayList<ShipsBean>();
	private List<HeartBeatBean> outBeans = new ArrayList<HeartBeatBean>();
	String mShipName;
	OsmandApplication app;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public DetailsShipsListAdapter(Context context,
			List<SearchShipsBean> searchShipsBean) {
		this.context = context;
		this.searchShipsBean = searchShipsBean;
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
		convertView = LayoutInflater.from(context).inflate(
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
						Gson gson = new Gson();
						List<SearchShipsBean> historyBean = new ArrayList<SearchShipsBean>();
						// TODO Auto-generated method stub
						try {
							String history = app.myPreferences.getString(
									"history", null);
							if (!history.equals(null)) {
								historyBean = gson.fromJson(history,
										new TypeToken<List<SearchShipsBean>>() {
										}.getType());
								for (int i = 0; i < historyBean.size(); i++) {
									if (m.getKey().equals(
											historyBean.get(i).getKey())) {
										historyBean.remove(i);
									}
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						historyBean.add(m);
						String history = gson.toJson(historyBean);
						app.mEditor.putString("history", history).commit();
						historyBean.clear();
						Message message = new Message();
						message.what = 000;
						DetailsFragment.newHandler.sendMessage(message);
						if (shipsBeans != null) {
							shipsBeans.clear();
						}
						InputMethodManager imm = (InputMethodManager) context
								.getSystemService(context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						mShipName = m.getKey();
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

				if (app.myPreferences.getBoolean("IsLogin", false)) {
					httpPost = app.myPreferences.getString("loginserver", null)
							+ IndexConstants.GET_CHOOSED_SHIP_URL + mShipName;
				} else {
					httpPost = app.myPreferences.getString("loginserver", null)
							+ IndexConstants.MOBILE_SEARCH_VESSEL_FREE
							+ mShipName + "&deviceid="
							+ app.myPreferences.getString("DEVICE_ID", null);
				}
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
				conn.setReadTimeout(20000);

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
				DetailsFragment.newHandler.sendMessage(message);
				for (HeartBeatBean h : outBeans) {
					new AlertDialog.Builder(context)
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
											Intent intent = new Intent(context,
													IsLoginActivity.class);
											context.startActivity(intent);
											((MapActivity) context)
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
				DetailsFragment.newHandler.sendMessage(message);
				Intent intent = new Intent(context,
						ShipSimpleDetailActivity.class);
				context.startActivity(intent);
			} else {
				Toast.makeText(context, "船舶过期", Toast.LENGTH_LONG).show();
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
