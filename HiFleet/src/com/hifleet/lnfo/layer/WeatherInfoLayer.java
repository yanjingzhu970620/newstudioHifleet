package com.hifleet.lnfo.layer;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hifleet.plus.R;
import com.hifleet.bean.loginSession;
import com.hifleet.map.AnimateDraggingMapThread;
import com.hifleet.map.ITileSource;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapTileAdapter;
import com.hifleet.map.MapTileLayer.IMapRefreshCallback;
import com.hifleet.map.MyTextView;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.OsmandSettings;
import com.hifleet.map.ResourceManager;
import com.hifleet.map.RotatedTileBox;
import com.hifleet.ship.WeatherInfoObject;

public class WeatherInfoLayer extends OsmandMapLayer implements
		IMapRefreshCallback {

	protected static final int emptyTileDivisor = 16;
	public static final int OVERZOOM_IN = 0;// 2;

	private final boolean mainMap;
	protected ITileSource map = null;
	protected MapTileAdapter mapTileAdapter = null;

	Paint paintBitmap, locationPaint;

	protected RectF bitmapToDraw = new RectF();

	protected Rect bitmapToZoom = new Rect();

	protected OsmandMapTileView view;
	protected ResourceManager resourceManager;
	private OsmandSettings settings;
	private boolean visible = true;
	OsmandApplication app;
	private HashMap<String, WeatherInfoObject> _seaareawxs = new HashMap<String, WeatherInfoObject>();
	private List<WeatherInfoObject> weatherObjList = new ArrayList<WeatherInfoObject>();
	private TextView textView;
	private static int NOT_SHOW_WEATHERINFO_ZOOM = 9;

	Bitmap icon;

	public WeatherInfoLayer(boolean mainMap) {
		this.mainMap = mainMap;
	}

	@Override
	public boolean drawInScreenPixels() {
		return false;
	}

	@Override
	public void initLayer(OsmandMapTileView view) {
		this.view = view;
		settings = view.getSettings();
		resourceManager = view.getApplication().getResourceManager();

		// paintBitmap = new Paint();
		// paintBitmap.setFilterBitmap(true);
		textView = new MyTextView(view.getContext());

		locationPaint = new Paint();
		locationPaint.setAntiAlias(true);
		locationPaint.setFilterBitmap(true);
		locationPaint.setDither(true);

		paint_mid_point = new Paint();
		paint_mid_point.setStyle(Style.FILL_AND_STROKE);
		paint_mid_point.setAntiAlias(true);
		paint_mid_point.setColor(view.getResources().getColor(
				com.hifleet.plus.R.color.blue));
		// paintBitmap.setAlpha(getAlpha());

		BitmapDrawable bd = (BitmapDrawable) view.getResources().getDrawable(
				R.drawable.icon_ships_test);
		icon = bd.getBitmap();

		initialize();

	}

	private void initialize() {
		WeatherInfoObject obj0 = new WeatherInfoObject();
		obj0.setId("0");
		obj0.setAreaname("渤海");
		obj0.setLon(118.95);
		obj0.setLat(38.72);
		_seaareawxs.put("0", obj0);

		WeatherInfoObject obj1 = new WeatherInfoObject();
		obj1.setId("1");
		obj1.setAreaname("渤海海峡");
		obj1.setLon(121.06);
		obj1.setLat(38.05);
		_seaareawxs.put("1", obj1);

		WeatherInfoObject weather2 = new WeatherInfoObject();
		weather2.setId("2");
		weather2.setAreaname("黄海北部");
		weather2.setLon(123.59);
		weather2.setLat(38.48);
		_seaareawxs.put("2", weather2);

		WeatherInfoObject obj3 = new WeatherInfoObject();
		obj3.setId("3");
		obj3.setAreaname("黄海中部");
		obj3.setLon(123.24);
		obj3.setLat(35.71);
		_seaareawxs.put("3", obj3);

		WeatherInfoObject obj4 = new WeatherInfoObject();
		obj4.setId("4");
		obj4.setAreaname("黄海南部");
		obj4.setLon(122.63);
		obj4.setLat(33.5);
		_seaareawxs.put("4", obj4);

		WeatherInfoObject obj5 = new WeatherInfoObject();
		obj5.setId("5");
		obj5.setAreaname("东海北部");
		obj5.setLon(124.2);
		obj5.setLat(31.75);
		_seaareawxs.put("5", obj5);

		WeatherInfoObject obj6 = new WeatherInfoObject();
		obj6.setId("6");
		obj6.setAreaname("东海南部");
		obj6.setLon(122.67);
		obj6.setLat(27.27);
		_seaareawxs.put("6", obj6);

		WeatherInfoObject obj7 = new WeatherInfoObject();
		obj7.setId("7");
		obj7.setAreaname("台湾海峡");
		obj7.setLon(119.33);
		obj7.setLat(24.2);
		_seaareawxs.put("7", obj7);

		WeatherInfoObject obj8 = new WeatherInfoObject();
		obj8.setId("8");
		obj8.setAreaname("台湾以东洋面");
		obj8.setLon(122.65);
		obj8.setLat(23.26);
		_seaareawxs.put("8", obj8);

		WeatherInfoObject obj9 = new WeatherInfoObject();
		obj9.setId("9");
		obj9.setAreaname("巴士海峡");
		obj9.setLon(120.85);
		obj9.setLat(20.9);
		_seaareawxs.put("9", obj9);

		WeatherInfoObject obj10 = new WeatherInfoObject();
		obj10.setId("10");
		obj10.setAreaname("北部湾");
		obj10.setLon(106.89);
		obj10.setLat(20.1);
		_seaareawxs.put("10", obj10);

		WeatherInfoObject obj11 = new WeatherInfoObject();
		obj11.setId("11");
		obj11.setAreaname("琼州海峡");
		obj11.setLon(110.1);
		obj11.setLat(20.2);
		_seaareawxs.put("11", obj11);

		WeatherInfoObject obj12 = new WeatherInfoObject();
		obj12.setId("12");
		obj12.setAreaname("南海西北部");
		obj12.setLon(113.24);
		obj12.setLat(21.03);
		_seaareawxs.put("12", obj12);

		WeatherInfoObject obj13 = new WeatherInfoObject();
		obj13.setId("13");
		obj13.setAreaname("南海东北部");
		obj13.setLon(117.18);
		obj13.setLat(22);
		_seaareawxs.put("13", obj13);

		WeatherInfoObject obj14 = new WeatherInfoObject();
		obj14.setId("14");
		obj14.setAreaname("南海中西部");
		obj14.setLon(111.8);
		obj14.setLat(16.21);
		_seaareawxs.put("14", obj14);

		WeatherInfoObject obj15 = new WeatherInfoObject();
		obj15.setId("15");
		obj15.setAreaname("南海中东部");
		obj15.setLon(116.59);
		obj15.setLat(17.03);
		_seaareawxs.put("15", obj15);

		WeatherInfoObject obj16 = new WeatherInfoObject();
		obj16.setId("16");
		obj16.setAreaname("南海西南部");
		obj16.setLon(112.54);
		obj16.setLat(12.53);
		_seaareawxs.put("16", obj16);

		WeatherInfoObject obj17 = new WeatherInfoObject();
		obj17.setId("17");
		obj17.setAreaname("南海东南部");
		obj17.setLon(117);
		obj17.setLat(13.6);
		_seaareawxs.put("17", obj17);

	}

	private Canvas canvas;
	private RotatedTileBox tileBox;
	private HashMap<String, LoadingWeatherXMLThread> asyntaskmap = new HashMap<String, LoadingWeatherXMLThread>();

	private void callWeatherInfoAction() {
		closeReqest();
		String uuid = UUID.randomUUID().toString();
		LoadingWeatherXMLThread task = new LoadingWeatherXMLThread(uuid);
		asyntaskmap.put(uuid, task);
		execute(task);
	}

	private void closeReqest() {
		if (asyntaskmap.isEmpty())
			return;
		Iterator<String> it = asyntaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			LoadingWeatherXMLThread task = asyntaskmap.get(uuid);
			task.cancel(true);
		}
	}

	private void clearMapByUUID(String uuid) {
		asyntaskmap.remove(uuid);
	}

	// 判断android版本
	private void execute(LoadingWeatherXMLThread task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		} else {
			task.execute(new Void[0]);
		}
	}

	@Override
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {

		this.canvas = canvas;
		this.tileBox = tileBox;
		if (tileBox.getZoom() > NOT_SHOW_WEATHERINFO_ZOOM) {
			// print("当前比例尺大于9，不再显示天气信息了" + tileBox.getZoom());
			return;
		}
		// print("刷新气象图层");
		refreashWeatherInfoLayer(canvas, tileBox);
	}

	// 开始解析天气数据xml
	private void parseXMLNew(InputStream inStream/*
												 * , Canvas canvas,
												 * RotatedTileBox tileBox
												 */) throws Exception {
		// print("进入解析天气数据xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("searea") == 0) {
					String seareaid = childElement.getAttribute("seareaid");
					String weather = childElement.getAttribute("weather");
					String wxid = childElement.getAttribute("wxid");
					String windd = childElement.getAttribute("windd");
					String windf = childElement.getAttribute("windf");
					String vis = childElement.getAttribute("vis");
					String sea = childElement.getAttribute("sea");
					String startime = childElement.getAttribute("startime");
					String endtime = childElement.getAttribute("endtime");
					// print("startime： "+startime);
					WeatherInfoObject obj = _seaareawxs.get(seareaid);
					obj.setWeather(weather);
					obj.setWeatherid(wxid);
					obj.setWinddirection(windd);
					obj.setWindforce(windf);
					obj.setVisibility(vis);
					obj.setSea(sea);
					obj.setStartime(startime);
					obj.setEndtime(endtime);
					_seaareawxs.put(seareaid, obj);

					weatherObjList.add(obj);
				}
			}
		}

		// print("weatherObjList.size()?: " + weatherObjList.size());
	}

	private void refreashWeatherInfoLayer(Canvas canvas, RotatedTileBox tileBox) {

		if (weatherObjList.size() > 0) {
			// print("刷新气象。");
			for (WeatherInfoObject obj : weatherObjList) {
				if (obj == null) {
					// print("obj null 继续");
					continue;
				}
				try {
					int iweatherid = Integer.parseInt(obj.getWeatherid());
					String weathericon = "";
					if (iweatherid < 10) {
						weathericon = "0" + iweatherid;
					} else {
						weathericon = weathericon + iweatherid;
					}

					try {
						canvas.save();
						java.io.InputStream ins = null;
						try{
						ins = view
								.getApplication()
								.getAssets()
								.open("images/weathers/" + weathericon + ".png");
						}catch (java.io.FileNotFoundException ex) {
						}
						Bitmap weatherIcon = BitmapFactory.decodeStream(ins);
						int locationX = tileBox.getPixXFromLonNoRot(obj
								.getLon());
						int locationY = tileBox.getPixYFromLatNoRot(obj
								.getLat());
						if(weatherIcon!=null){
						canvas.drawBitmap(big(weatherIcon),
								locationX - big(weatherIcon).getWidth() / 2,
								locationY - big(weatherIcon).getHeight() / 2,
								locationPaint);
						}
						canvas.restore();
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} catch (java.lang.NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
		}

	}

	//bitmap 放大2倍
	private static Bitmap big(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(2.0f, 2.0f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private Paint paint_mid_point;
	private boolean weatherflag = true;

	@Override
	public void onDraw(Canvas can, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		// if(tileBox.getZoom()<=NOT_SHOW_WEATHERINFO_ZOOM){
		// refreashWeatherInfoLayer(canvas, tileBox);
		// }
	}

	@Override
	public void destroyLayer() {
	}

	class LoadingWeatherXMLThread extends AsyncTask<Void, String, String> {
		String uuid;

		public LoadingWeatherXMLThread(String uuid) {
			this.uuid = uuid;
		}

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(String... values) {

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			clearMapByUUID(uuid);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				String bboxurl = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_SEAREAWX_ACTION_URL;// "http://58.40.126.151:8080/cnooc/GetSeareaWxAction.do";
				//print("url=" + bboxurl);
				URL url = new URL(bboxurl);

				if (this.isCancelled()) {
					// print("线程取消 1.");
					return "";
				}

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}

				conn.setConnectTimeout(5000);

				if (this.isCancelled()) {
					// print("线程取消 1.");
					return "";
				}

				InputStream inStream = conn.getInputStream();

				if (this.isCancelled()) {
					// print("线程取消 1.");
					return "";
				}
				parseXMLNew(inStream);

				inStream.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	// 触发点击
	@Override
	public boolean onSingleTap(PointF point, RotatedTileBox tileBox) {
		// System.out.println("点击天气的图标函数触发。");
		// final double lat = tileBox.getLatFromPixel(point.x, point.y);
		// final double lon = tileBox.getLonFromPixel(point.x, point.y);
		List<WeatherInfoObject> infoObjects = new ArrayList<WeatherInfoObject>();
		getWeatherInfo(tileBox, point, infoObjects);
		if (tileBox.getZoom() <= NOT_SHOW_WEATHERINFO_ZOOM) {
			if (!infoObjects.isEmpty()) {
				for (WeatherInfoObject fav : infoObjects) {
					Double la = fav.getLat();
					Double lo = fav.getLon();
					AnimateDraggingMapThread thread = view
							.getAnimatedDraggingThread();
					thread.startMoving(la, lo, tileBox.getZoom(), false);
					int locationX = tileBox.getCenterPixelX();
					int locationY = tileBox.getCenterPixelY();
					showPopupWindow(view, locationX, locationY, fav);
				}
				return true;
			}
		}
		return false;
	}

	public void getWeatherInfo(RotatedTileBox tb, PointF point,
			List<? super WeatherInfoObject> res) {
		int r = (int) (10 * tb.getDensity());
		int ex = (int) point.x;
		int ey = (int) point.y;
		List<WeatherInfoObject> weather = new ArrayList<WeatherInfoObject>();
		for (int i = 0; i < _seaareawxs.size(); i++) {
			String l = String.valueOf(i);
			weather.add(_seaareawxs.get(l));
		}
		// System.out.println("weather=" + weather.get(1).getWeather());
		for (WeatherInfoObject n : weather) {
			int x = (int) tb.getPixXFromLatLon(n.getLat(), n.getLon());
			int y = (int) tb.getPixYFromLatLon(n.getLat(), n.getLon());
			if (calculateBelongs(ex, ey, x, y, r)) {
				res.add(n);
			}
		}
	}

	private boolean calculateBelongs(int ex, int ey, int objx, int objy,
			int radius) {
		return Math.abs(objx - ex) <= radius && (ey - objy) <= radius / 2
				&& (objy - ey) <= 3 * radius;
	}

	// 气泡图
	public void showPopupWindow(View view, int ex, int ey, WeatherInfoObject fav) {
		View contentView = LayoutInflater.from(view.getContext()).inflate(
				R.layout.balloon_overlay, null);
		TextView textSeaArea = (TextView) contentView
				.findViewById(R.id.text_sea_area);
		TextView textWeather = (TextView) contentView
				.findViewById(R.id.text_wave_height);
		TextView textWindDirection = (TextView) contentView
				.findViewById(R.id.text_wind_direction);
		TextView textWaveHeight = (TextView) contentView
				.findViewById(R.id.text_wave_height);
		TextView textTime = (TextView) contentView.findViewById(R.id.text_time);
		final PopupWindow pop = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		textSeaArea.setText("海区：" + fav.getAreaname());
		textWeather.setText("天气/能见度：" + fav.getWeather() + "/"
				+ fav.getVisibility() + "km");
		textWindDirection.setText("风向/风力：" + fav.getWinddirection() + "/"
				+ fav.getWindforce() + "级");
		textWaveHeight.setText("海浪高度：" + fav.getSea() + "米");
		textTime.setText("有效时间：" + fav.getStartime() + "-" + fav.getEndtime());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		pop.getContentView().measure(0, 0);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + ex
				- pop.getContentView().getMeasuredWidth() / 2, location[1] + ey
				- pop.getContentView().getMeasuredHeight());

		// System.out.print(pop.getContentView().getMeasuredWidth() + "x");
		// System.out.print(pop.getContentView().getMeasuredHeight() + "y");
	}

	@Override
	public void mapRefreshed(RotatedTileBox tileBox) {
		// TODO Auto-generated method stub
		// print("气象层收到通知。");
		if (tileBox.getZoom() > NOT_SHOW_WEATHERINFO_ZOOM) {
			// print("当前比例尺大于9，不再显示天气信息了 mapRefreshed 直接返回" +
			// tileBox.getZoom());
			return;
		}
		try {
			// // print("当前比例尺大于9，不再显示天气信息了     moveend" );
			// File indexesFile = new File(view.getApplication().getAppPath(
			// IndexConstants.TILES_INDEX_DIR), "weatherinfo.xml");
			// if (!indexesFile.exists()) {
			// callWeatherInfoAction();
			// return;
			// }
			//
			// java.io.InputStream ins = new FileInputStream((indexesFile));//
			// view.getApplication().getAppPath(IndexConstants.TILES_INDEX_DIR).
			// parseXML(ins, canvas, tileBox);
			//
			// // 刷新数据
			// refreashWeatherInfoLayer(canvas, tileBox);

			if (weatherObjList.size() <= 0) {
				// print("异步下载气象。");
				callWeatherInfoAction();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
