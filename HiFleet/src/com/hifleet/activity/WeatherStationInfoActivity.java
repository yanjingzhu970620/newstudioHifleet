package com.hifleet.activity;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.hifleet.plus.R;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.WeatherBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.thread.UserLogout;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# WeatherStationInfoActivity.java Create on 2015年6月5日 下午2:58:25
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WeatherStationInfoActivity extends HBaseActivity {

	String stationID, stationName;

	private TextView textLo, textLa, textWindd, textWindf, textTemperature,
			textPressure, textVisibility, textHumidity, textWaterlevel,
			textCurrent, textCurrentdirection, textSea, textSeadir,
			textSeatemp, textMudtemp, textSalinity, textUpdatetime;

	OsmandApplication app;

	private List<WeatherBean> mWeatherBeans = new ArrayList<WeatherBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hifleet.activity.HBaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_information);

		Bundle bundle = this.getIntent().getExtras();
		stationID = bundle.getString("stationId");
		stationName = bundle.getString("stationName");

		initNav();
		setNavTitle(stationName);

		textLo = (TextView) findViewById(R.id.text_weather_lo);
		textLa = (TextView) findViewById(R.id.text_weather_la);
		textWindd = (TextView) findViewById(R.id.text_weather_windd);
		textWindf = (TextView) findViewById(R.id.text_weather_windf);
		textTemperature = (TextView) findViewById(R.id.text_weather_temperature);
		textPressure = (TextView) findViewById(R.id.text_weather_pressure);
		textVisibility = (TextView) findViewById(R.id.text_weather_visibility);
		textHumidity = (TextView) findViewById(R.id.text_weather_humidity);
		textWaterlevel = (TextView) findViewById(R.id.text_weather_waterlevel);
		textCurrent = (TextView) findViewById(R.id.text_weather_current);
		textCurrentdirection = (TextView) findViewById(R.id.text_weather_currentdirection);
		textSea = (TextView) findViewById(R.id.text_weather_sea);
		textSeadir = (TextView) findViewById(R.id.text_weather_seadir);
		textSeatemp = (TextView) findViewById(R.id.text_weather_seatemp);
		textMudtemp = (TextView) findViewById(R.id.text_weather_mudtemp);
		textSalinity = (TextView) findViewById(R.id.text_weather_salinity);
		textUpdatetime = (TextView) findViewById(R.id.text_weather_updatetime);

		WeatherThread wthread=new WeatherThread();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			wthread.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);}
			else{
				wthread.execute();
			}
	}

	class WeatherThread extends AsyncTask<String, Void, Void> {
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
						null) + IndexConstants.GET_WEATHER_INFO + stationID;
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
			for (WeatherBean weather : mWeatherBeans) {
				if (weather.lon.equals("N/A")) {
					textLo.setText(weather.lon);
				} else {
					textLo.setText(convertToSexagesimalLo(weather.getLo()));
				}
				if (weather.lat.equals("N/A")) {
					textLa.setText(weather.lat);
				} else {
					textLa.setText(convertToSexagesimalLa(weather.getLa()));
				}
				textWindd.setText(weather.windd);
				textWindf.setText(weather.windf);
				textTemperature.setText(weather.temperature);
				textPressure.setText(weather.pressure);
				textVisibility.setText(weather.vis);
				textHumidity.setText(weather.humidity);
				textWaterlevel.setText(weather.waterlevel);
				textCurrent.setText(weather.current);
				textCurrentdirection.setText(weather.currentdirection);
				textSea.setText(weather.sea);
				textSeadir.setText(weather.seadir);
				textSeatemp.setText(weather.seatemp);
				textMudtemp.setText(weather.mudtemp);
				textSalinity.setText(weather.salinity);
				textUpdatetime.setText(weather.updatetime);
			}
		}
	}

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();

		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
		}

		if (root.getNodeName().compareTo("weather-info") == 0) {
			mWeatherBeans.add(XmlParseUtility.parse(root, WeatherBean.class));
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

	public double getdPoint(double num) {
		double d = num;
		int fInt = (int) d;
		BigDecimal b1 = new BigDecimal(Double.toString(d));
		BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
		double dPoint = b1.subtract(b2).floatValue();
		return dPoint;
	}
}
