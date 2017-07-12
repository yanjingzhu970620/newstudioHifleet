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

import android.content.Context;
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

import com.e.common.widget.CleanableEditText;
import com.e.common.widget.effect.layout.EffectColorRelativeLayout;
import com.hifleet.plus.R;
import com.hifleet.activity.ChooseShipActivity;
import com.hifleet.activity.GetArriveShipsActivity;
import com.hifleet.activity.GetInportShipsActivity;
import com.hifleet.activity.GetWillArriveShipsActivity;
import com.hifleet.activity.LineShipsActivity;
import com.hifleet.activity.SearchActivity;
import com.hifleet.bean.PortBean;
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
public class PortListAdapter extends BaseAdapter {
	private Context context;
	private List<PortBean> PortBean;
	public static PortBean mPort;
	public static List<ShipsBean> shipsBeans = new ArrayList<ShipsBean>();
	public static Boolean isShow = false;
	public static Boolean isMove = false;
	String mPortName;
	OsmandApplication app;
	CleanableEditText mEditPort;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	public PortListAdapter(Context context,
			List<PortBean> PortBean,CleanableEditText mEditPort) {
		this.context = context;
		this.PortBean = PortBean;
		this.mEditPort=mEditPort;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PortBean.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return PortBean.get(position);
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
		final PortBean m = PortBean.get(position);
		shipsText.setText(m.getPortName());
		EffectColorRelativeLayout shipsEffectColorRelativeLayout = (EffectColorRelativeLayout) convertView
				.findViewById(R.id.effectRelativeLayout_ships);
		shipsEffectColorRelativeLayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						Message message = new Message();
//						message.what = 000;
//						SearchActivity.newHandler.sendMessage(message);
//						if (shipsBeans != null) {
//							shipsBeans.clear();
//						}
//						InputMethodManager imm = (InputMethodManager) context
//								.getSystemService(context.INPUT_METHOD_SERVICE);
//						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						mPortName = m.getPortName();
						mPort=null;
						mPort=m;
						mEditPort.setText(mPortName);
						if(app.myPreferences.getString("portships", "").equals("GetInportShipsActivity")){
//						mEditPort.setText(mPortName);
						GetInportShipsActivity.allsearchPortBean.clear();
						GetInportShipsActivity.mPort=m;
						Message msg=new Message();
						msg.what=GetInportShipsActivity.REFRESH;
						msg.obj="re";
						GetInportShipsActivity.handler.sendMessage(msg);
						}else if(app.myPreferences.getString("portships", "").equals("GetArriveShipsActivity")){
							GetArriveShipsActivity.allsearchPortBean.clear();
							GetArriveShipsActivity.mPort=m;
							Message msg=new Message();
							msg.what=GetArriveShipsActivity.REFRESH;
							msg.obj="re";
							GetArriveShipsActivity.handler.sendMessage(msg);
						}else if(app.myPreferences.getString("portships", "").equals("GetWillArriveShipsActivity")){
							GetWillArriveShipsActivity.allsearchPortBean.clear();
							GetWillArriveShipsActivity.mPort=m;
							Message msg=new Message();
							msg.what=GetWillArriveShipsActivity.REFRESH;
							msg.obj="re";
							GetWillArriveShipsActivity.handler.sendMessage(msg);
						}else if(app.myPreferences.getString("portships", "").equals("LineShipsActivity")){
							LineShipsActivity.allsearchPortBean.clear();
							if(LineShipsActivity.pt.equals("sp")){
								LineShipsActivity.startport=m;
							}else if(LineShipsActivity.pt.equals("ep")){
								LineShipsActivity.endport=m;
							}
							Message msg=new Message();
							msg.what=LineShipsActivity.REFRESH;
							msg.obj="re";
							LineShipsActivity.handler.sendMessage(msg);
						}
//						System.out.println("点击了port：：：："+mPortName+m.getPortCode());
//						ShipChoosedThread sc = new ShipChoosedThread();
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//							// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
//							sc.executeOnExecutor(
//									Executors.newCachedThreadPool(),
//									new String[0]);
//						} else {
//							sc.execute();
//						}
					}
				});
		return convertView;
	}

	class ShipChoosedThread extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				mPortName = mPortName.replace(" ", "%20");

				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_CHOOSED_SHIP_URL + mPortName;

				// print("1: "+httpPost);

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
			if (shipsBeans.size() > 0) {
				Message message = new Message();
				message.what = 111;
//				print("点击了："+mPortName);
				SearchActivity.newHandler.sendMessage(message);
//				Intent intent = new Intent(context, ChooseShipActivity.class);
//				print("点击了："+mPortName+"进行跳转");
//				context.startActivity(intent);
				// ((SearchActivity) context).finish();
			} else {
				Toast.makeText(context, "数据过期", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

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
