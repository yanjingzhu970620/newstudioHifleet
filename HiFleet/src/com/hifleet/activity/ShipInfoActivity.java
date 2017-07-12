package com.hifleet.activity;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.e.common.utility.CommonUtility.ImageUtility;

import com.hifleet.plus.R;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.MyTeamBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity1;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.thread.MyTeamShipsThread;
import com.hifleet.utility.XmlParseUtility;
import com.hifleet.widget.ActionSheetDialog;
import com.hifleet.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.hifleet.widget.ActionSheetDialog.SheetItemColor;
import com.hifleet.widget.DateTimePickerDialog;
import com.hifleet.widget.DateTimePickerDialog.OnDateTimeSetListener;
import com.ibm.icu.text.SimpleDateFormat;

import static android.R.interpolator.linear;
import static com.hifleet.plus.R.id.ll_ship_psc;

/**
 * @{# SettingActivity.java Create on 2015年3月4日 上午10:44:29
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipInfoActivity extends Activity {
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	ShipsBean ship = new ShipsBean();
	private String from;
	private String starttime;
	private String endtime;
	private String temp_file_path;

	private int REQ_CODE_CAMERA = 0001;
	private int REQUEST_CODE_PICK_IMAGE = 0002;

	public static String startTime = null, endTime = null, mmsi = null;

	TextView mTextShipName, mTextStartTime, mTextEndTime, mTextShipEdit;
	LinearLayout mLayoutStartTime, mLayoutEndTime,mLayoutPsc;

	static EditText emmsi;
	static EditText ecallsign;
	static EditText ecname;
	static EditText eaisname;
	static EditText egroup;
	// private Spinner spgroup;
	private ImageView image;
	String[] rgroup;

	String mm = "未填写";
	String cnm = "未填写";
	String an = "未填写";
	String gr = "缺省分组";
	String grs = "未填写";
	String cs = "未填写";

	private OsmandMapTileView mapView;
	private OsmandApplication app;
	private String mRole;
	private String myfleet;

	List<MyTeamBean> mMyTeamBean = new ArrayList<MyTeamBean>();
	private ArrayAdapter<CharSequence> spGroupadapter;

	// private List<HashMap<String,String>> groupid=new
	// ArrayList<HashMap<String,String>>();
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ship_information);
		app=this.getMyApplication();
//		mTextShipName = (TextView) findViewById(R.id.text_ship_name);
		mTextShipEdit = (TextView) findViewById(R.id.ship_info_edit);
		mLayoutPsc = (LinearLayout) findViewById(R.id.ll_ship_psc);

		mRole = app.myPreferences.getString("role", null);
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		myfleet = bundle.getString("myfleet");
		if (myfleet.equals(true + "")) {
			System.out.println("退出船队 myfleet" + myfleet);
			mTextShipEdit.setText("退出船队");
		} else {
			System.out.println(" 加入船队 myfleet" + myfleet);
			mTextShipEdit.setText("加入船队");
		}
		from = null;
		from = bundle.getString("from");
		if (from != null) {
			mTextShipEdit.setText("显示位置");
		}
		ship.n = bundle.getString("shipn");
		ship.c = bundle.getString("shipcs");
		ship.cname = bundle.getString("shipcname");
		ship.m = bundle.getString("shipm");
		ship.i = bundle.getString("shipi");
		ship.lo = bundle.getString("shiplo");
		ship.la = bundle.getString("shipla");
		ship.co = bundle.getString("shipco");
		ship.sp = bundle.getString("shipsp");
		ship.setSubt(bundle.getString("shipsubt"));
		ship.h = bundle.getString("shiph");
		ship.s = bundle.getString("ships");
		ship.l = bundle.getString("shipl");
		ship.b = bundle.getString("shipb");
		ship.dr = bundle.getString("shipdr");
		ship.d = bundle.getString("shipd");
		ship.e = bundle.getString("shipe");
		ship.ti = bundle.getString("shipti");
		ship.rs = bundle.getString("shiprs");//System.out.println("打印了没11 from");
		if(bundle.getString("shipsatti")!=null) {
			ship.setSatti(bundle.getString("shipsatti"));
		}
		if (from != null && from.equals("LineShipsActivity")) {
			starttime = bundle.getString("starttime");
			endtime = bundle.getString("endtime");
		}

		if(ship.i!=null&&(ship.i.equals("-")||ship.i.equals("0"))){
			mLayoutPsc.setVisibility(View.GONE);
		}
		setShipInfo();

		mTextStartTime = (TextView) findViewById(R.id.text_start_time);
		mTextEndTime = (TextView) findViewById(R.id.text_end_time);

		if (from != null && from.equals("LineShipsActivity")) {
			mTextStartTime.setText(starttime);
			mTextEndTime.setText(endtime);
		} else {

			long nt = System.currentTimeMillis();
			startTime = getStringDate((nt - 432000 * 1000));
			mTextStartTime.setText(startTime);

			endTime = getStringDate(nt);
			mTextEndTime.setText(endTime);
		}
		mmsi = ship.m;
	}

	class DeleteTeamShipsThread extends AsyncTask<String, Void, Void> {

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
						null)
						+ IndexConstants.DELETE_MYFLEET_SHIP
						+ "mmsi="
						+ mmsi;
				// String
				// httpPost="http://www.hifleet.com/savemyship.do?mmsi=441736000&callsign=&name=moon&dname=moon&groups=&group=moon";
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
				conn.setConnectTimeout(10000);
				System.out.println("delete finish......");
				InputStream inStream = conn.getInputStream();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				Toast.makeText(ShipInfoActivity.this, "网络超时 删除失败 ",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			System.err.println("delete finish already" + heartBeatBean.size());

			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					if (h.message != null && !h.message.equals("")) {
						Toast.makeText(ShipInfoActivity.this,
								"删除失败: " + h.message, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ShipInfoActivity.this,
								"删除失败 网络连接超时 " + h.message, Toast.LENGTH_LONG)
								.show();
					}
					return;
				} else if (Integer.valueOf(h.flag).intValue() == 1) {
					myfleet = false + "";
					mTextShipEdit.setText("加入船队");
					// for (int i = 0; i < MyTeamShipsThread.shipsBeans.size();
					// i++) {
					if (MyTeamShipsThread.shipsBeans.containsKey(mmsi)) {
						MyTeamShipsThread.shipsBeans.remove(mmsi);
					}
					// }
					Toast.makeText(ShipInfoActivity.this, "删除成功 ",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	class SaveTeamShipsThread extends AsyncTask<String, Void, Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String xmString = new String(ship.c.toString()
						.getBytes("UTF-8"));
//				String csUTF8 = URLEncoder.encode(xmString, "UTF-8");
				// String mmString = new
				// String(mm.toString().getBytes("UTF-8"));
				// String mmUTF8 = URLEncoder.encode(mmString, "UTF-8");
				String anString = new String(ship.n.toString()
						.replace(" ", "%20")); //.getBytes("UTF-8"));
//				String anUTF8 = URLEncoder.encode(anString, "UTF-8");

				String grString = new String(gr.toString().replace(" ", "%20"));
						//.getBytes("UTF-8"));
//				String grUTF8 = URLEncoder.encode(grString, "UTF-8");
				String cnmString = new String(ship.cname.toString().replace(" ", "%20"));//.getBytes("UTF-8"));
//				String cnmUTF8 = URLEncoder.encode(cnmString, "UTF-8");
				// String csString = new
				// String(cs.toString().getBytes("UTF-8"));
				// String csUTF8 = URLEncoder.encode(csString, "UTF-8");
				// String grString = new
				// String(gr.toString().getBytes("UTF-8"));
				// String grUTF8 = URLEncoder.encode(grString, "UTF-8");

				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.SAVE_MYFLEET_SHIPS
						+ "mmsi="
						+ mmsi
						+ "&callsign="
						+ xmString
						+ "&name="
						+ anString
						+ "&dname=" + cnmString + "&group=" + grString;
				// String
				// httpPost="http://www.hifleet.com/savemyship.do?mmsi=441736000&callsign=&name=moon&dname=moon&groups=&group=moon";
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
				conn.setConnectTimeout(10000);
				// System.out.println("add finish......");
				InputStream inStream = conn.getInputStream();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
				// Toast.makeText(ShipInfoActivity.this, "网络超时 添加失败",
				// Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			System.err.println("add finish already");

			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					if (h.message != null && !h.message.equals("")) {
						Toast.makeText(ShipInfoActivity.this,
								"添加失败: " + h.message, Toast.LENGTH_LONG).show();
						return;
					} else {
						Toast.makeText(ShipInfoActivity.this, "网络超时 添加失败",
								Toast.LENGTH_LONG).show();
					}
				} else if (Integer.valueOf(h.flag).intValue() == 1) {
					myfleet = true + "";
					mTextShipEdit.setText("退出船队");
					MyTeamShipsThread.shipsBeans.put(ship.getM(), ship);
					String g = gr;
					Toast.makeText(ShipInfoActivity.this, "添加成功 已加入到" + g,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("savemyship") == 0) {
			heartBeatBean.clear();
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			// System.err.println("添加得到了回应:" + heartBeatBean.get(0).flag
			// + heartBeatBean.get(0).message);
		}
		if (root.getNodeName().compareTo("delmyship") == 0) {
			heartBeatBean.clear();
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			// System.err.println("添加得到了回应:" + heartBeatBean.get(0).flag
			// + heartBeatBean.get(0).message);
		}
		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.clear();
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}
	}

	private void parseTeamGroupXMLnew(InputStream inStream) throws Exception {
		mMyTeamBean.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);

		Element root = document.getDocumentElement();
		// System.out.println(root.getTextContent());
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("fleet") == 0) {
					mMyTeamBean.add(XmlParseUtility.parse(childElement,
							MyTeamBean.class));
				}
			}
		}
	}

	public void setShipInfo() {
//		System.out.println("打印了没11");
//		System.out.println("打印了没11"+app.getMyrole());
		TextView textName = (TextView) findViewById(R.id.ship_name);
		TextView textMMSI = (TextView) findViewById(R.id.ship_mmsi);
		TextView textIMO = (TextView) findViewById(R.id.ship_imo);
		TextView textLo = (TextView) findViewById(R.id.ship_lo);
		TextView textLa = (TextView) findViewById(R.id.ship_la);
		TextView textCourse = (TextView) findViewById(R.id.ship_course);
		TextView textHeading = (TextView) findViewById(R.id.ship_heading);
		TextView textSpeed = (TextView) findViewById(R.id.ship_speed);
		TextView textType = (TextView) findViewById(R.id.ship_type);
		TextView textState = (TextView) findViewById(R.id.ship_state);
		TextView textLength = (TextView) findViewById(R.id.ship_length);
		TextView textWidth = (TextView) findViewById(R.id.ship_width);
		TextView textWaterline = (TextView) findViewById(R.id.ship_waterline);
		TextView textDestination = (TextView) findViewById(R.id.ship_destination);
		TextView textEta = (TextView) findViewById(R.id.ship_eta);
		TextView textFinishTime = (TextView) findViewById(R.id.ship_finish_time);
		TextView sattiWarning = (TextView) findViewById(R.id.satti_warning);
		
		if ((ship.getSatti()!=null&&!ship.getSatti().equals(""))||ship.rs.equals("0")||!app.getMyrole().equals("vvip")||app.getLoginbean().getTraffic().equals("1")
				|| (app.getLoginbean().getFleets().equals("1") && MyTeamShipsThread.shipsBeans
						.containsKey(ship.getM()))) {
//		if (ship.cname == null || ship.cname.equals("")
//				|| ship.cname.equals("N/A") || ship.cname.equals("null")) {
//			if (ship.n == null || ship.n.equals("") || ship.n.equals("N/A")
//					|| ship.n.equals("null")) {
//				textName.setText("暂无船名");
//				mTextShipName.setText("暂无船名");
//			} else {
//				textName.setText(ship.n);
//				mTextShipName.setText(ship.n);
//			}
//		} else {
//			textName.setText(ship.cname);
//			mTextShipName.setText(ship.cname);
//		}

		if (ship.m.equals("null") || ship.m.equals("N/A")) {
			textMMSI.setText(" ");
		} else {
			textMMSI.setText(ship.m);
		}

		if (ship.i.equals("null") || ship.i.equals("N/A")) {
			textIMO.setText(" ");
		} else {
			textIMO.setText(ship.i);
		}

		String lo = convertToSexagesimalLo(Double.parseDouble(ship.lo));
		String la = convertToSexagesimalLa(Double.parseDouble(ship.la));
		textLo.setText(lo);
		textLa.setText(la);

		double course = Math.floor(Double.parseDouble(ship.co));
		int co = (int) course;
		textCourse.setText(co + "°");

		textHeading.setText(ship.h + "°");
		if (ship.sp.equals("null") || ship.sp.equals("N/A")) {
			textSpeed.setText(" ");
		} else {
			textSpeed.setText(ship.sp + "节");
		}

		if (ship.getSubt()==null||ship.getSubt().equals("null") || ship.getSubt().equals("N/A")) {
			textType.setText(" ");
		} else {
			textType.setText(ship.getSubt());
		}

		if (ship.s.equals("null") || ship.s.equals("N/A")) {
			textState.setText(" ");
		} else {
			textState.setText(ship.s);
		}

		if (ship.l.equals("null") || ship.l.equals("N/A")) {
			textLength.setText(" ");
		} else {
			textLength.setText(ship.l + "米");
		}

		if (ship.b.equals("null") || ship.b.equals("N/A")) {
			textWidth.setText(" ");
		} else {
			textWidth.setText(ship.b + "米");
		}

		if (ship.dr.equals("null") || ship.dr.equals("N/A")) {
			textWaterline.setText(" ");
		} else {
			textWaterline.setText(ship.dr + "米");
		}

		if (ship.d.equals("null") || ship.d.equals("N/A")) {
			textDestination.setText(" ");
		} else {
			textDestination.setText(ship.d);
		}

		if (ship.e.length() > 11) {
			textEta.setText(ship.e.substring(ship.e.length() - 11,
					ship.e.length()));
		} else {
			if (ship.e.equals("null") || ship.e.equals("N/A")) {
				textEta.setText(" ");
			} else {
				textEta.setText(ship.e);
			}
		}

		if (ship.ti.equals("null") || ship.ti.equals("N/A")) {
			textFinishTime.setText(" ");
		} else {
			textFinishTime.setText(ship.getformatti(ship.getDateupdatetime()));
//			if (myfleet.equals(true + "")||!ship.getSatti().equals("")) {
//				textFinishTime.setText(ship.ti.substring(0, ship.ti.length()));
//			}else{
//				textFinishTime.setText(ship.ti.substring(0, ship.ti.length()-2));
//			}
		}
			if(ship.getSatti()!=null&&!ship.getSatti().equals("")&&(!ship.getDateupdatetime().equals(ship.getDatesatti()))){
				sattiWarning.setText("该船有 "+ship.getformatti(ship.getDatesatti())+" 时刻的卫星AIS船位，购买请联系客服");
				sattiWarning.setTextColor(Color.RED);
			}
		}else{
//			if (ship.cname == null || ship.cname.equals("")
//					|| ship.cname.equals("N/A") || ship.cname.equals("null")) {
//				if (ship.n == null || ship.n.equals("") || ship.n.equals("N/A")
//						|| ship.n.equals("null")) {
//					textName.setText("暂无船名");
//					mTextShipName.setText("暂无船名");
//				} else {
//			if(ship.n.length()>2){
//					textName.setText(ship.n.substring(0, 1)+"***");
//					mTextShipName.setText(ship.n.substring(0, 1)+"***");
//			}else{
//				textName.setText("***");
//				mTextShipName.setText("****");
//			}
//				}
//			} else {
//				textName.setText(ship.cname);
//				mTextShipName.setText(ship.cname);
//			}

			if (ship.m.equals("null") || ship.m.equals("N/A")) {
				textMMSI.setText(" ");
			} else {
				textMMSI.setText(ship.m.substring(0, 4)+"****");
			}

			if (ship.i.equals("null") || ship.i.equals("N/A")) {
				textIMO.setText(" ");
			} else {
				textIMO.setText(ship.i.substring(0, 0)+"****");
			}

			String lo = convertToSexagesimalLo(Double.parseDouble(ship.lo));
			String la = convertToSexagesimalLa(Double.parseDouble(ship.la));
			textLo.setText(lo);
			textLa.setText(la);

			double course = Math.floor(Double.parseDouble(ship.co));
			int co = (int) course;
			textCourse.setText(co + "°");

			textHeading.setText(ship.h + "°");
			if (ship.sp.equals("null") || ship.sp.equals("N/A")) {
				textSpeed.setText(" ");
			} else {
				textSpeed.setText(ship.sp + "节");
			}

//			if (ship.getSubt()==null||ship.getSubt().equals("null") || ship.getSubt().equals("N/A")) {
//				textType.setText(" ");
//			} else {
				textType.setText("*******");
//			}

//			if (ship.s.equals("null") || ship.s.equals("N/A")) {
//				textState.setText(" ");
//			} else {
				textState.setText("*******");
//			}

//			if (ship.l.equals("null") || ship.l.equals("N/A")) {
//				textLength.setText(" ");
//			} else {
				textLength.setText("*******");
//			}

//			if (ship.b.equals("null") || ship.b.equals("N/A")) {
//				textWidth.setText(" ");
//			} else {
				textWidth.setText("*******");
//			}

//			if (ship.dr.equals("null") || ship.dr.equals("N/A")) {
//				textWaterline.setText(" ");
//			} else {
				textWaterline.setText("*******");
//			}

//			if (ship.d.equals("null") || ship.d.equals("N/A")) {
//				textDestination.setText(" ");
//			} else {
				textDestination.setText("*******");
//			}

//			if (ship.e.length() > 11) {
//				textEta.setText(ship.e.substring(ship.e.length() - 11,
//						ship.e.length()));
//			} else {
//				if (ship.e.equals("null") || ship.e.equals("N/A")) {
//					textEta.setText(" ");
//				} else {
					textEta.setText("*******");
//				}
//			}

//			if (ship.ti.equals("null") || ship.ti.equals("N/A")) {
//				textFinishTime.setText(" ");
//			} else {
				textFinishTime.setText("*******");
//			}
			}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_write_image:
			startTime = mTextStartTime.getText().toString();
			endTime = mTextEndTime.getText().toString();
			// System.out.println("看看用户权限===" + mRole);
			if (!app.myPreferences.getBoolean("IsLogin", false)) {
				new AlertDialog.Builder(ShipInfoActivity.this)
						.setTitle("提示")
						.setCancelable(false)
						.setMessage("请先注册或登录")
						.setNegativeButton("取消", null)
						.setPositiveButton(
								"登录",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(
												ShipInfoActivity.this,
												IsLoginActivity.class);
										startActivity(intent);
										overridePendingTransition(
												R.drawable.activity_open, 0);
									}
								}).show();
			} else {
				System.out.println("看看这个==="
						+ app.myPreferences.getBoolean("IsLogin", false));
				if ((ship.getSatti()!=null&&ship.getSatti().equals(ship.ti))||!mRole.equals("ship")&&(ship.rs.equals("0")||!app.getMyrole().equals("vvip")||app.getLoginbean().getTraffic().equals("1")
						|| (app.getLoginbean().getFleets().equals("1") && MyTeamShipsThread.shipsBeans
						.containsKey(ship.getM())))) {
					System.out.println("用户登录");
					if (startTime.equals("请选择轨迹起始时间")
							|| endTime.equals("请选择轨迹终止时间")) {
						Toast.makeText(ShipInfoActivity.this, "请选择时间",
								Toast.LENGTH_LONG).show();
						return;
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd-HH-mm-ss");
						try {
							Date startDate = sdf.parse(startTime);
							Date endDate = sdf.parse(endTime);
							Long checkLong = endDate.getTime()
									- startDate.getTime();
							if (checkLong < 7776000000l) {
								Intent intent = new Intent(
										ShipInfoActivity.this,
										MapActivity1.class);
								Bundle b = new Bundle();
								b.putString("mmsi", mmsi);
								b.putString("la", ship.la);
								b.putString("lo", ship.lo);
								System.out.println("查看轨迹  加入la" + ship.la
										+ " LO::" + ship.lo);
								b.putString("needlayer", "trace");
								b.putString("starttime", startTime);
								b.putString("endtime", endTime);
								b.putString("from", "ShipInfoActivity");
								b.putString("key", "1");
								intent.putExtras(b);
								startActivity(intent);
							} else {
								new AlertDialog.Builder(ShipInfoActivity.this)
										.setTitle("提示").setCancelable(false)
										.setMessage("时间跨度请不要超过90天！")
										.setNegativeButton("确定", null).show();
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					new AlertDialog.Builder(ShipInfoActivity.this)
							.setTitle("提示").setCancelable(false)
							.setMessage("权限不足！请升级用户权限")
							.setNegativeButton("确定", null).show();
				}
			}
			break;
		case R.id.ll_start_time:
			// DateTimePickDialogUtil dateTimePicKDialog = new
			// DateTimePickDialogUtil(
			// ShipInfoActivity.this, null);
			// dateTimePicKDialog.dateTimePicKDialog(mTextStartTime,
			// "yyyy-MM-dd-HH-mm-ss");
			showDateDialog("起始时间", 1);
			break;
		case R.id.ll_end_time:
			// DateTimePickDialogUtil dateTimePicKDialog1 = new
			// DateTimePickDialogUtil(
			// ShipInfoActivity.this, null);
			// dateTimePicKDialog1.dateTimePicKDialog(mTextEndTime,
			// "yyyy-MM-dd-HH-mm-ss");
			showDateDialog("终止时间", 2);
			break;
		case R.id.ll_take_photos:
			// Intent intent = new Intent(this, PhotoToolActivity.class);
			// intent.putExtra(name, value)
			// startActivityForResult(intent, 0);
			// library
			// PhotoToolActivity.go2PhotoToolActivity(this, false, 10);

			// Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// startActivityForResult(intent3, 1);
			new ActionSheetDialog(ShipInfoActivity.this)
					.builder()
					.setTitle("请选择操作")
					.setCancelable(false)
					.setCanceledOnTouchOutside(true)
					.addSheetItem("拍照", SheetItemColor.Blue,
							new OnSheetItemClickListener() {

								@Override
								public void onClick(int which) {
									// TODO Auto-generated method stub
									Intent i = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									temp_file_path = "/storage/emulated/0/hifleet/tiles"
											+ UUID.randomUUID().toString()
											+ ".png";
									Uri imageUri = Uri.fromFile(new File(
											temp_file_path));
									startActivityForResult(i, REQ_CODE_CAMERA);
								}
							})

					.addSheetItem("相册", SheetItemColor.Blue,
							new OnSheetItemClickListener() {

								@Override
								public void onClick(int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											Intent.ACTION_PICK);
									intent.setType("image/*");// 相片类型
									startActivityForResult(intent,
											REQUEST_CODE_PICK_IMAGE);
								}
							}).show();


			break;
		case R.id.ll_back:
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			break;
		case R.id.ll_ship_details:
			Intent intent = new Intent(ShipInfoActivity.this,
					ShipDetailsActivity.class);
			Bundle b = new Bundle();
			b.putString("shipmmsi", mmsi);
			intent.putExtras(b);
			startActivity(intent);
			break;
		case R.id.ll_shuiwen:
			if (app.myPreferences.getBoolean("IsLogin", false)
					&& !app.myPreferences.getString("role", null)
							.equals("ship")&&((ship.getSatti()!=null&&ship.getSatti().equals(ship.ti))||ship.rs.equals("0")||!app.getMyrole().equals("vvip")||app.getLoginbean().getTraffic().equals("1")
					|| (app.getLoginbean().getFleets().equals("1") && MyTeamShipsThread.shipsBeans
					.containsKey(ship.getM())))) {
				Intent intent2 = new Intent(ShipInfoActivity.this,
						WeatherShuiwenActivity.class);
				Bundle a = new Bundle();

				if (ship.cname == null || ship.cname.equals("")
				|| ship.cname.equals("N/A") || ship.cname.equals("null")) {
			if (ship.n == null || ship.n.equals("") || ship.n.equals("N/A")
					|| ship.n.equals("null")) {
				a.putString("shipname", ship.getM());
			} else {
				a.putString("shipname", ship.n);
			}
		} else {
					a.putString("shipname", ship.cname);
		}
				a.putString("shipname", ship.n);
				a.putString("la", ship.la);
				a.putString("lo", ship.lo);
				intent2.putExtras(a);
				startActivity(intent2);
			} else {
				new AlertDialog.Builder(ShipInfoActivity.this).setTitle("提示")
						.setCancelable(false).setMessage("权限不足！请升级权限。")
						.setNegativeButton("确定", null).show();
			}
			break;
		case ll_ship_psc:
			Intent intent2 = new Intent(ShipInfoActivity.this,
					ShipPSCcheckActivity.class);
			Bundle a = new Bundle();
			a.putString("shipname", ship.n);
//			a.putString("la", ship.la);
//			a.putString("lo", ship.lo);
			a.putString("imo", ship.i);
			a.putString("url", "http://212.45.16.136/isss/public_apcis.php?Action=searchByIMO&Skip=0");
			intent2.putExtras(a);
			startActivity(intent2);
			//请求 URL: http://212.45.16.136/isss/public_apcis.php?Action=searchByIMO&Skip=0
				break;
		case R.id.ship_addteam:
			if (from != null) {
				System.err.println("准备图上显示的");
				Intent mintent = new Intent(ShipInfoActivity.this,
						MapActivity1.class);
				Bundle bundle = new Bundle();
				bundle.putString("mmsi", mmsi);
				bundle.putString("la", ship.la);
				bundle.putString("lo", ship.lo);
				bundle.putString("co", ship.co);
				bundle.putString("sp", ship.sp);
				// bundle.putString("starttime", startTime);
				// bundle.putString("endtime", endTime);
				bundle.putString("needlayer", "ship");
				bundle.putString("from", "ShipInfoActivity");
				bundle.putString("key", "1");
				mintent.putExtras(bundle);
				startActivity(mintent);
			} else {
				if (myfleet.equals(true + "")) {
					DeleteTeamShipsThread deleship = new DeleteTeamShipsThread();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// System.out.println("del team thread start");
						deleship.executeOnExecutor(
								Executors.newCachedThreadPool(), new String[0]);
					} else {
						deleship.execute();
					}

				} else {
					if (app.myPreferences.getBoolean("IsLogin", false)
							&& !app.myPreferences.getString("role", null)
							.equals("ship")&&((ship.getSatti()!=null&&ship.getSatti().equals(ship.ti))||ship.rs.equals("0")||!app.getMyrole().equals("vvip")||app.getLoginbean().getTraffic().equals("1")
							|| (app.getLoginbean().getFleets().equals("1") && MyTeamShipsThread.shipsBeans
							.containsKey(ship.getM())))) {

					Builder builder2 = new AlertDialog.Builder(this);
					// LayoutInflater inflater = (LayoutInflater)
					// getApplicationContext()
					// .getSystemService(LAYOUT_INFLATER_SERVICE);
					// View inview = inflater.inflate(R.layout.addteam_dialog,
					// null);
					View inview = ShipInfoActivity.this.getLayoutInflater()
							.inflate(R.layout.addmyteam_dialog, null, false);
					// spgroup = (Spinner)
					// inview.findViewById(R.id.Spinnergroup);
					// rgroup = new String[] { "缺省分组" };
					// spGroupadapter = new ArrayAdapter<CharSequence>(
					// ShipInfoActivity.this,
					// android.R.layout.simple_dropdown_item_1line, rgroup);
					// spgroup.setAdapter(spGroupadapter);
					// spgroup.setOnItemSelectedListener(new
					// OnItemSelectedListener() {
					//
					// @Override
					// public void onItemSelected(AdapterView<?> arg0, View v,
					// int arg2, long arg3) {
					// // TODO Auto-generated method stub
					// TextView v1 = (TextView) v;
					// v1.setTextColor(Color.BLACK);
					// gr = spgroup.getSelectedItem().toString();
					// //
					// System.out.println("spArea选择了：：："+sarea+"areaid:::"+areaid.size());
					// }
					//
					// @Override
					// public void onNothingSelected(AdapterView<?> arg0) {
					// // TODO Auto-generated method stub
					//
					// }
					// });

					MyTeamThread mt = new MyTeamThread();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
						mt.executeOnExecutor(Executors.newCachedThreadPool(),
								new String[0]);
					} else {
						mt.execute();
					}

					emmsi = (EditText) inview.findViewById(R.id.add_mymmsi);
					ecallsign = (EditText) inview
							.findViewById(R.id.add_mycallsign);
					ecname = (EditText) inview.findViewById(R.id.add_mycname);
					eaisname = (EditText) inview
							.findViewById(R.id.add_myaisname);
					egroup = (EditText) inview
							.findViewById(R.id.add_mygroupchoose);
					image = (ImageView) inview
							.findViewById(R.id.btn_select_mygroup);

					emmsi.setText(ship.m);
					ecallsign.setText(ship.c);
					eaisname.setText(ship.n);
					ecname.setText(ship.cname);
					// egroup.setText("缺省分组");

					builder2.setView(inview);
					builder2.setCancelable(false);
					builder2.setTitle("加入船队").setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mm = emmsi.getText().toString()
											.replace(" ", "%20");
									if (mm == null || mm.equals("")) {
										Toast.makeText(ShipInfoActivity.this,
												"mmsi 不能为空", Toast.LENGTH_LONG)
												.show();
										return;
									}
									cnm = ecname.getText().toString()
											.replace(" ", "%20");
									an = eaisname.getText().toString()
											.replace(" ", "%20");
									cs = ecallsign.getText().toString()
											.replace(" ", "%20");
									gr = egroup.getText().toString()
											.replace(" ", "%20");
									System.out.println("team editddddd111" + mm
											+ cnm + an + gr + cs);
									SaveTeamShipsThread saveship = new SaveTeamShipsThread();
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
										// System.out.println("add team thread start");
										saveship.executeOnExecutor(
												Executors.newCachedThreadPool(),
												new String[0]);
									} else {
										saveship.execute();
									}
									// inedit=false;
									dialog.dismiss();
								}
							});
					builder2.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface cdialog,
										int which) {
									// inedit=false;
									cdialog.dismiss();
								}
							}).create().show();// 选中
					} else {
						new AlertDialog.Builder(ShipInfoActivity.this).setTitle("提示")
								.setCancelable(false).setMessage("权限不足！请升级卫星数据权限。")
								.setNegativeButton("确定", null).show();
					}
				}
			}
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
				String httpPost = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_MY_TEAM_NAME_URL;
				System.out.println("my team group httpPost: " + httpPost);
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
				parseTeamGroupXMLnew(inStream);
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
			// System.out.println("看看mMyTeamBean的内容===" + mMyTeamBean.size());
			if (mMyTeamBean.size() > 0) {

				image.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						popupWindwShowing();
						// System.err.println("popupWindwShowingpopupWindwShowing  pwidth:::"+pwidth);
					}

				});
				initPopuWindow(); // listview 让分组选择 未做好

			}
		}

		private void parseTeamGroupXMLnew(InputStream inStream)
				throws Exception {
			mMyTeamBean.clear();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inStream);

			Element root = document.getDocumentElement();
			// System.out.println(root.getTextContent());
			NodeList childNodes = root.getChildNodes();

			if (root.getNodeName().compareTo("session__timeout") == 0) {
				heartBeatBean.add(XmlParseUtility.parse(root,
						HeartBeatBean.class));
			}

			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = (Node) childNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;
					if (childElement.getNodeName().compareTo("fleet") == 0) {
						mMyTeamBean.add(XmlParseUtility.parse(childElement,
								MyTeamBean.class));
					}
				}
			}
		}
	}

	private ArrayList<String> datas = new ArrayList<String>();

	private ListView listView;

	private com.hifleet.adapter.OptionsAdapter optionsAdapter;

	private PopupWindow selectPopupWindow;;

	/**
	 * 初始化填充Adapter所用List数据 暂时测试。。
	 */
	private void initDatas() {

		datas.clear();

		for (MyTeamBean t : mMyTeamBean) {
			datas.add(t.getName());
		}
		//
	}

	/**
	 * 初始化PopupWindow
	 */
	private void initPopuWindow() {

		initDatas();

		// PopupWindow浮动下拉框布局
		View loginwindow = (View) ShipInfoActivity.this.getLayoutInflater()
				.inflate(R.layout.popupwindow_select, null);
		listView = (ListView) loginwindow.findViewById(R.id.pop_sel_list);

		// 设置自定义Adapter
		optionsAdapter = new com.hifleet.adapter.OptionsAdapter(this, handler,
				datas);
		listView.setAdapter(optionsAdapter);
		// System.err.println("initPopuWindow"+datas.toArray().length+group.getWidth());
		selectPopupWindow = new PopupWindow(loginwindow, 430,
				LayoutParams.WRAP_CONTENT, true);

		selectPopupWindow.setOutsideTouchable(true);

		// 这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
		// 没有这一句则效果不能出来，但并不会影响背景
		// 本人能力极其有限，不明白其原因，还望高手、知情者指点一下
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	} // 让分组可以选择的list

	/**
	 * 显示PopupWindow窗口
	 * 
	 * @param popupwindow
	 */
	public void popupWindwShowing() {
		// 将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
		// 这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
		// （是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
		selectPopupWindow.showAsDropDown(egroup, 0, -3);
		// selectPopupWindow.set
	}

	/**
	 * PopupWindow消失
	 */
	public void dismiss() {
		selectPopupWindow.dismiss();
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			Bundle data = message.getData();
			// System.err.println("dianjile   popwindow  getmessage");
			switch (message.what) {
			case 1:
				// 选中下拉项，下拉框消失
				int selIndex = data.getInt("selIndex");
				egroup.setText(datas.get(selIndex));
				// System.err.println("dianjile   popwindow  ");
				dismiss();
				break;
			case 2:
				// 移除下拉项数据
				int delIndex = data.getInt("delIndex");
				datas.remove(delIndex);
				// 刷新下拉列表
				optionsAdapter.notifyDataSetChanged();
				break;
			case 3:
				Toast.makeText((Context)ShipInfoActivity.this, data.getString("upload"), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void showDateDialog(final String tip, final int i) {
		DateTimePickerDialog dialog = new DateTimePickerDialog(this,
				System.currentTimeMillis());
		dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
			public void OnDateTimeSet(AlertDialog dialog, long date) {
				Toast.makeText(ShipInfoActivity.this,
						tip + getStringDate(date), Toast.LENGTH_LONG).show();
				switch (i) {
				case 1:
					startTime = getStringDate(date);
					mTextStartTime.setText(startTime);
					break;
				case 2:
					endTime = getStringDate(date);
					mTextEndTime.setText(endTime);
					break;
				}
			}
		});
		dialog.show();
	}

	public static String getStringDate(Long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String dateString = formatter.format(date);
		return dateString;
	}

	public String convertToSexagesimalLa(double num) {

		int du = (int) Math.floor(Math.abs(num)); // 获取整数部分
		double temp = getdPoint(Math.abs(num)) * 60;
		int fen = (int) Math.floor(temp); // 获取整数部分
		DecimalFormat df = new DecimalFormat("0.0");
		String miao = df.format(getdPoint(temp) * 60);

		if (num < 0) {
			return du + "°" + fen + "′" + miao + "″" + " S";
		} else {
			return du + "°" + fen + "′" + miao + "″" + " N";
		}
	}

	public String convertToSexagesimalLo(double num) {

		int du = (int) Math.floor(Math.abs(num)); // 获取整数部分
		double temp = getdPoint(Math.abs(num)) * 60;
		int fen = (int) Math.floor(temp); // 获取整数部分
		DecimalFormat df = new DecimalFormat("0.0");
		String miao = df.format(getdPoint(temp) * 60);

		if (num < 0) {
			return du + "°" + fen + "′" + miao + "″" + " W";
		} else {
			return du + "°" + fen + "′" + miao + "″" + " E";
		}
	}

	public double getdPoint(double num) {
		double d = num;
		int fInt = (int) d;
		BigDecimal b1 = new BigDecimal(Double.toString(d));
		BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
		double dPoint = b1.subtract(b2).floatValue();
		return dPoint;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (resultCode == IDENTITY.ACTIVITY_CHOOSEFILE_CODE) {
		// final String path = data.getExtras().getString(
		// IDENTITY.IDENTITY_FILEPATH);
		// if (path != null) {
		// new Thread() {
		// public void run() {
		// try {
		// File file;
		// if (path.contains("@@")) {
		// String a[] = path.split("@@");
		// System.out.println("a[]===" + a);
		// for (String p : a) {
		// file = new File(p);
		// System.out.println("file===" + file);
		// uploadFile(file);
		// }
		// } else {
		// file = new File(path);
		// uploadFile(file);
		// }
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// };
		// }.start();
		// }
		// System.out.println("path1111===" + path);
		// }
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICK_IMAGE&&data!=null) {
			Uri uri = data.getData();
			// to do find the path of pic
			String[] proj = { MediaStore.Images.Media.DATA };
			// 好像是android多媒体数据库的封装接口，具体的看Android文档
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			// 按我个人理解 这个是获得用户选择的图片的索引值
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// 将光标移至开头 ，这个很重要，不小心很容易引起越界
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			final String path = cursor.getString(column_index);
//			new Thread() {
//				public void run() {
					try {
						File file;
						file = new File(path);
						if(file.exists()){
							Bitmap bm = revitionImageSize(path);
//							LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
							Builder builder = new AlertDialog.Builder(ShipInfoActivity.this);
							View view = LayoutInflater.from(ShipInfoActivity.this).inflate(R.layout.alert_photo_upload, null);
			                builder.setView(view);
							 ImageView img = (ImageView) view.findViewById(R.id.photo_up);
	                         img.setImageBitmap(bm);
							builder.setTitle("上传照片");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									new Thread() {
										public void run() {
									uploadFile(new File(path));
									};
									}.start();
								}
							});
							builder.setNegativeButton(R.string.default_buttons_cancel, null);
							builder.show();
							}
					} catch (Exception e) {
						e.printStackTrace();
					}
//				};
//			}.start();
			System.out.println(path);
		}
		if (resultCode == RESULT_OK && requestCode == REQ_CODE_CAMERA) {
			Bundle extras = data.getExtras();
			Bitmap mImageBitmap = (Bitmap) extras.get("data");
			ImageUtility.storeImage(temp_file_path, mImageBitmap);
			System.out.println("path===" + mImageBitmap);
			if (temp_file_path != null) {
//				new Thread() {
//					public void run() {
						try {
							File file;
							file = new File(temp_file_path);
							if(file.exists()){
								Bitmap bm = revitionImageSize(temp_file_path);
//								LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
								Builder builder = new AlertDialog.Builder(ShipInfoActivity.this);
								View view = LayoutInflater.from(ShipInfoActivity.this).inflate(R.layout.alert_photo_upload, null);
				                builder.setView(view);
								 ImageView img = (ImageView) view.findViewById(R.id.photo_up);
		                         img.setImageBitmap(bm);
								builder.setTitle("上传照片");
								builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										new Thread() {
											public void run() {
										uploadFile(new File(temp_file_path));
										};
										}.start();
									}
								});
								builder.setNegativeButton(R.string.default_buttons_cancel, null);
								builder.show();
								}
						} catch (Exception e) {
							e.printStackTrace();
						}
//					};
//				}.start();
			}
			System.out.println("path1111===" + temp_file_path);
		}
	}

	public Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 256)
					&& (options.outHeight >> i <= 256)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
	
	/* 上传文件至Server的方法 */
	private void uploadFile(File file) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String httpPost = app.myPreferences.getString("loginserver", null)
				+ IndexConstants.UPLOAD_DATA_ACTION + mmsi;
		try {
			URL url = new URL(httpPost);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "text/html");
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			// ds.writeBytes(twoHyphens + boundary + end);
			// ds.writeBytes("Content-Disposition: form-data; "
			// + "name=\"file1\";filename=\"" + newName + "\"" + end);
			// ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(file);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			if(is!=null){
			parseReXMLnew(is);
			is.close();
			}
//			int ch;
//			StringBuffer b = new StringBuffer();
//			while ((ch = is.read()) != -1) {
//				b.append((char) ch);
//			}
			/* 将Response显示于Dialog */
			/* 关闭DataOutputStream */
			ds.close();
			System.out.println("上传成功"+photoResultBeans.get(0).flag);
			
			if(photoResultBeans.size()>0&&photoResultBeans.get(0).flag.equals("1")){
				System.out.println("handler 上传成功");
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("upload", "上传成功");
				msg.setData(data);
				msg.what = 3;
				handler.sendMessage(msg);
				}else{
					
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("upload", "上传失败");
					msg.setData(data);
					msg.what = 3;
					handler.sendMessage(msg);
				}
//			Toast.makeText((Context)ShipInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			System.out.println("上传失败" + e);
//			Toast.makeText((Context)ShipInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	 List<HeartBeatBean> photoResultBeans=new ArrayList<HeartBeatBean>();
	  private void parseReXMLnew(InputStream inStream) throws Exception {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inStream);
			Element root = document.getDocumentElement();
			photoResultBeans.clear();
			if (root.getNodeName().compareTo("result") == 0) {
				photoResultBeans.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			}
		}
	  public OsmandApplication getMyApplication() {
			return ((OsmandApplication) getApplication());
		}
}
