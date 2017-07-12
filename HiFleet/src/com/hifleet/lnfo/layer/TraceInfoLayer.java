package com.hifleet.lnfo.layer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.TraceBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.AnimateDraggingMapThread;
import com.hifleet.map.ITileSource;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.LatLon;
import com.hifleet.map.MapActivity1;
import com.hifleet.map.MapTileAdapter;
import com.hifleet.map.MapTileLayer.IMapRefreshCallback;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.OsmandSettings;
import com.hifleet.map.QuadRect;
import com.hifleet.map.ResourceManager;
import com.hifleet.map.RotatedTileBox;
import com.hifleet.plus.R;
import com.hifleet.utility.XmlParseUtility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @{# PlotInfoLayer.java Create on 2015年5月18日 下午2:13:50
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 * 
 */
public class TraceInfoLayer extends OsmandMapLayer implements
		IMapRefreshCallback {

	OsmandApplication app;

	private QuadRect quadRect;

	protected static final int emptyTileDivisor = 16;
	public static final int OVERZOOM_IN = 0;// 2;

	public static final int START_SHOW = 10;
	public static List<TraceBean> listp = new ArrayList<TraceBean>();

	public static String la, lo;

	List<TraceBean> msgs = null;
	private final boolean mainMap;
	protected ITileSource map = null;
	protected MapTileAdapter mapTileAdapter = null;
	List<TraceBean> list1 = new ArrayList<TraceBean>();
	public static List<Map<String, String>> pointList = new ArrayList<Map<String, String>>();
	public static List<Map<String, String>> pointList1 = new ArrayList<Map<String, String>>();

	public static List<TraceBean> currentTraceBeans = new ArrayList<TraceBean>();
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	Paint paintBitmap, locationPaint,locationwhitePaint;

	protected RectF bitmapToDraw = new RectF();

	protected Rect bitmapToZoom = new Rect();

	protected MapActivity1 mapActivity1;
	protected OsmandMapTileView view;
	protected ResourceManager resourceManager;
	private OsmandSettings settings;
	private boolean visible = true;

	private List<TraceBean> _plot = new ArrayList<TraceBean>();

	private Paint paint_mid_point;
	private Paint shipShapPaint, shipShapPaint1;
	// public static int justflag = 0;
	private Paint focusedShipShapePaint, focusedShipShapePaint1,areaShapePaint;
	// LayerOperateListener listener;

	String Polygon;
	float drawpoint[];
	private List<String> _point = new ArrayList<String>();
	private RotatedTileBox tileBox;

	private Paint paint;

	private boolean flag = true;
	private boolean moveflag = true;

	DisplayMetrics dm;
	private int radius = 4;// 轨迹点的半径

	private boolean tracelable;

	private Canvas canvas;
	public static final int ALERTIMEOUT = 20000;
	public static final int CANCLEBACKGROUND = 20001;
	public static final int FINISHACTIVITY = 20002;
	public static final int SESSIONOUTTIME = 20003;

	public TraceInfoLayer(boolean mainMap) {
		this.mainMap = mainMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hifleet.map.OsmandMapLayer#initLayer(com.hifleet.map.OsmandMapTileView
	 * )
	 */
	@Override
	public void initLayer(OsmandMapTileView view) {
		// TODO Auto-generated method stub
		this.view = view;
		settings = view.getSettings();
		resourceManager = view.getApplication().getResourceManager();
		dm = view.getResources().getDisplayMetrics();
		radius = (int) (radius * dm.density);
		// paintBitmap = new Paint();
		// paintBitmap.setFilterBitmap(true);

		locationPaint = new Paint();
		locationPaint.setAntiAlias(true);
		locationPaint.setStyle(Style.STROKE);
		locationPaint.setDither(true);
		locationPaint.setStrokeWidth(2*dm.density);
		locationPaint.setColor(0xff993333);
		
//		locationwhitePaint = new Paint();
//		locationwhitePaint.setColor(Color.WHITE);
//		locationwhitePaint.setAntiAlias(true);
//		locationwhitePaint.setStrokeJoin(Paint.Join.ROUND);
//		locationwhitePaint.setStrokeCap(Paint.Cap.ROUND);
//		locationwhitePaint.setStrokeWidth(3);
//		locationwhitePaint.setAlpha(50);
//		locationwhitePaint.setStyle(Style.FILL_AND_STROKE);
		locationwhitePaint = new Paint();
		locationwhitePaint.setAntiAlias(true);
		locationwhitePaint.setStyle(Style.FILL_AND_STROKE);
//		locationwhitePaint.setDither(true);
		locationwhitePaint.setStrokeJoin(Paint.Join.ROUND);
		locationwhitePaint.setStrokeCap(Paint.Cap.ROUND);
		locationwhitePaint.setStrokeWidth(2*dm.density);
		locationwhitePaint.setColor(Color.WHITE);
		locationwhitePaint.setAlpha(130);
//		locationwhitePaint.setColor(Color.WHITE);

		paint_mid_point = new Paint();
		paint_mid_point.setStyle(Style.STROKE);
		paint_mid_point.setAntiAlias(true);
		paint_mid_point.setColor(view.getResources().getColor(
				com.hifleet.plus.R.color.red));

		paint_mid_point.setStrokeWidth(4*dm.density);
		paint_mid_point.setAlpha(127);
		paint = new Paint(); // 设置一个笔刷大小是3的heise的画笔
		paint.setColor(view.getResources().getColor(R.color.blue));
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(1);

		shipShapPaint = new Paint();
		shipShapPaint.setColor(0xff28e54e);
		shipShapPaint.setAntiAlias(true);
		shipShapPaint.setStrokeWidth(0);
		shipShapPaint.setStyle(Style.FILL_AND_STROKE);

		shipShapPaint1 = new Paint();
		shipShapPaint1.setColor(view.getResources().getColor(R.color.black));
		shipShapPaint1.setAntiAlias(true);
		shipShapPaint1.setStrokeWidth(2 * dm.density);
		shipShapPaint1.setStyle(Style.STROKE);

		focusedShipShapePaint = new Paint();
		focusedShipShapePaint.setColor(view.getResources()
				.getColor(R.color.red));
		focusedShipShapePaint.setAntiAlias(true);
		focusedShipShapePaint.setStrokeWidth(0);
		focusedShipShapePaint.setStyle(Style.FILL_AND_STROKE);

		focusedShipShapePaint1 = new Paint();
		focusedShipShapePaint1.setColor(view.getResources().getColor(
				R.color.black));
		focusedShipShapePaint1.setAntiAlias(true);
		focusedShipShapePaint1.setStrokeWidth(2 * dm.density);
		focusedShipShapePaint1.setStyle(Style.STROKE);
		
		areaShapePaint = new Paint();
		areaShapePaint.setColor(view.getResources().getColor(
				R.color.red));
		areaShapePaint.setAntiAlias(true);
		areaShapePaint.setStrokeWidth(2 * dm.density);
		areaShapePaint.setStyle(Style.STROKE);
		// 像素密度dm
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	private int basicLength = 12;
	private int exactLength;
	private float bmoffset = 0.5f;

	// 画一个蓝色填充色的船�?  
	@SuppressLint("NewApi")
	private Bitmap getShipBitmap(double shipSp, boolean focused) {
		exactLength = (int) dm.densityDpi * basicLength;
		// exactLength = (int)dm.densityDpi *basicLength;
		Bitmap shipbm;// = Bitmap.createBitmap(dm,
						// 14*(int)dm.density,38*(int)dm.density,
						// Bitmap.Config.ARGB_8888);

		Canvas can;// = new Canvas(shipbm);

		Path path = new Path();
		Path path1 = new Path();
		if (shipSp > 0) {
			// 画一个三角形带头
			shipbm = Bitmap.createBitmap(dm, 14 * (int) dm.density,
					38 * (int) dm.density, Bitmap.Config.ARGB_8888);
			path.moveTo(0 + bmoffset, 0 + bmoffset);
			path.lineTo(12 * dm.density + bmoffset, 0 + bmoffset);
			path.lineTo(6 * dm.density + bmoffset, 24 * dm.density + bmoffset);
			path.lineTo(0 + bmoffset, 0 + bmoffset);
			path.moveTo(6 * dm.density + bmoffset, 24 * dm.density + bmoffset);
			path.lineTo(6 * dm.density + bmoffset, (24 + 12) * dm.density
					+ bmoffset);

		} else {
			// 画一个菱�?
			shipbm = Bitmap.createBitmap(dm, 18 * (int) dm.density,
					18 * (int) dm.density, Bitmap.Config.ARGB_8888);
			path.moveTo(0 + bmoffset, 8 * dm.density + bmoffset);
			path.lineTo(8 * dm.density + bmoffset, 16 * dm.density + bmoffset);
			path.lineTo(16 * dm.density + bmoffset, 8 * dm.density + bmoffset);
			path.lineTo(8 * dm.density + bmoffset, 0 * dm.density + bmoffset);
			path.lineTo(0 * dm.density + bmoffset, 8 * dm.density + bmoffset);

		}
		can = new Canvas(shipbm);

		if (!focused) {
			can.drawPath(path, shipShapPaint);
			can.drawPath(path, shipShapPaint1);
		} else {
			can.drawPath(path, focusedShipShapePaint);
			can.drawPath(path, focusedShipShapePaint1);
		}

		return shipbm;
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings settings) {
		// print("");
		this.tileBox = tileBox;
		this.canvas = canvas;
		// if (flag) {
//		print("执行onDraw  trace");
		// new LoadingTraceThread(tileBox, canvas).execute();
		// refreashShipTraceLayer(canvas, tileBox);
		// } else {
		//
		// refreashShipTraceLayer(canvas, tileBox);
		// }
	}

	@Override
	public void destroyLayer() {
	}

	@Override
	public boolean drawInScreenPixels() {

		return false;
	}

	class LoadingTraceThread extends AsyncTask<Void, String, String> {
		private RotatedTileBox privateTileBox;
		private Canvas privateCanvas;

		public LoadingTraceThread(RotatedTileBox box, Canvas canvas) {
			this.privateTileBox = box;
			this.privateCanvas = canvas;

		}

		@Override
		protected String doInBackground(Void... arg0) {
//			print("后台运行。。。  traceinfo");
			try {
				// System.out.println("LoadingTraceThread");
				QuadRect rect = privateTileBox.getLatLonBounds();
				LatLon p3 = new LatLon(rect.top, rect.right);
				LatLon p2 = new LatLon(rect.top, rect.left);
				LatLon p1 = new LatLon(rect.bottom, rect.left);
				LatLon p4 = new LatLon(rect.bottom, rect.right);
				String s[] = MapActivity1.startTime.split("-");
				String e[] = MapActivity1.endTime.split("-");
				// int myzoom=1;
				// if(MapActivity1.fromac.equals("LineShipsActivity")){
				// myzoom=1;
				// }else{
				// myzoom=privateTileBox.getZoom();
				// }
				String polygon = "((" + p2.getLongitude() + "%20"
						+ p2.getLatitude() + "," + p1.getLongitude() + "%20"
						+ p1.getLatitude() + "," + p4.getLongitude() + "%20"
						+ p4.getLatitude() + "," + p3.getLongitude() + "%20"
						+ p3.getLatitude() + "," + p2.getLongitude() + "%20"
						+ p2.getLatitude() + "))" + "&mmsi="
						+ MapActivity1.mymmsi + "&startdate=" + s[0] + "-"
						+ s[1] + "-" + s[2] + "&starthour=" + s[3]
						+ "&startminute=" + s[4] + "&endate=" + e[0] + "-"
						+ e[1] + "-" + e[2] + "&endhour=" + e[3]
						+ "&endminute=" + e[4] + "&zoom="
						+ privateTileBox.getZoom();

				String bboxurl = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.GET_VESSEL_TRAJECTORY_DATA
						+ polygon;

				print("trace  bboxurl===" + bboxurl);

				URL url = new URL(bboxurl);
				try {
					HttpURLConnection conn = (HttpURLConnection) url
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
					if (this.isCancelled()) {
						return "";
					}
					msgs = new ArrayList<TraceBean>();
					parseXMLnew(inStream, msgs, this);
					if (this.isCancelled())
						return "";
					if (msgs.size() > 0) {
						listp.clear();
						for (int i = 0; i < msgs.size(); i++) {
							listp.add(msgs.get(i));
						}
					}
//					print("轨迹解析完成啦");
					inStream.close();

					if (listp.size() > 0) {
						tracelable = true;
						test(listp, privateCanvas, privateTileBox);
						// print("trace画完  准备加入时间标签");
					}
				} catch (java.net.SocketTimeoutException stout) {
					stout.printStackTrace();
					tipsMsg1(ALERTIMEOUT, "timeout");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				print("轨迹出错啦。。。");
				Message message = new Message();
				message.obj = 11;
				message.what = CANCLEBACKGROUND;
				MapActivity1.tracHandler.sendMessage(message);
			}
			return null;
		}

		protected void onPostExecute(String result) {
			Message message = new Message();
			for (HeartBeatBean h : heartBeatBean) {
				if (Integer.valueOf(h.flag).intValue() == 0) {
					message.obj = 123;
					message.what = SESSIONOUTTIME;
					MapActivity1.tracHandler.sendMessage(message);
					return;
				}
			}
			if (msgs.size() == 0) {
				message.obj = 111;
				message.what = FINISHACTIVITY;
				MapActivity1.tracHandler.sendMessage(message);
				return;
			}
			message.obj = 11;
			message.what = CANCLEBACKGROUND;
			MapActivity1.tracHandler.sendMessage(message);
		}
	}

	public String getStringLon(double lon){
		String getlon="";
		String sign="";
		if(lon<0){
//			getlat= Math.abs(lat);
			sign="W";
		}else{
//			getlat=lat;
			sign="E";
		}
		double l=Math.abs(lon);
		int d=(int)Math.floor(l);
		if (d < 10) {
			getlon ="00" + d +"°";
		} else if (d > 10 && d < 100) {
			getlon ="0" + d +"°";
		} else {
			getlon ="" + d +"°";
		}
		double m=(l-d)*60;
		int m1=(int)Math.floor(m);
		if(m<10){
			getlon=getlon +"0"+ m1+ "'";
		}else{
			getlon=getlon + m1 + "'";
		}
		double s=m-m1;
		java.text.DecimalFormat dft = new java.text.DecimalFormat(".###");
		// double ll = l / 60.0;
		getlon=getlon + dft.format(s);
//		System.err.println("lon："+lon+" String lon:"+getlon);
		return getlon+sign;
		
	}
	
public String getStringLat(double lat){
		
		String getlat="";
		String sign="";
		if(lat<0){
//			getlat= Math.abs(lat);
			sign="S";
		}else{
//			getlat=lat;
			sign="N";
		}
		double l=Math.abs(lat);
		int d=(int)Math.floor(l);
//		System.err.println("l："+l+" d:"+d);
		if (d < 10) {
			getlat ="0" + d +"°";
		} else if (d > 10 && d < 100) {
			getlat ="" + d +"°";
		} else {
			getlat ="" + d +"°";
		}
		double m=(l-d)*60;
		int m1=(int)Math.floor(m);
//		System.err.println("m："+m+" m1:"+m1);
		if(m1<10){
			getlat=getlat +"0"+ m1+ "'";
		}else{
			getlat=getlat + m1 + "'";
		}
		double s=m-m1;
		java.text.DecimalFormat dft = new java.text.DecimalFormat(".###");
		// double ll = l / 60.0;
		getlat=getlat + dft.format(s);
//		System.err.println("lat："+lat+" String lat:"+getlat);
		return getlat+sign;
		
	}
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		// print("准备刷新trace");
		this.canvas = canvas;
		this.tileBox = tileBox;
		if (MapActivity1.needlayer.equals("area")) {
			    Path p=new Path();
				String latlon=MapActivity1.latlon.replace("POLYGON((", "").replace("))", "");
				String[] l=latlon.split(",");
				for(int i=0;i<l.length;i++){
					String[] lalo=l[i].split(" ");
					int locationX = tileBox.getPixXFromLonNoRot(Double.valueOf(lalo[0]));
					int locationY = tileBox.getPixYFromLatNoRot(Double.valueOf(lalo[1]));
					
					if(lalo[0]!=null&&lalo[1]!=null&&i==0){
						p.moveTo(locationX, locationY);
						if (moveflag) {
							moveflag = false;
						AnimateDraggingMapThread thread = view
								.getAnimatedDraggingThread();
						Double la = Double.valueOf(lalo[1]);
						Double lo = Double.valueOf(lalo[0]);
						thread.startMoving(la, lo, tileBox.getZoom(), false);
						}
						MapActivity1.lo=Double.valueOf(lalo[0]);
						MapActivity1.la=Double.valueOf(lalo[1]);
				
			canvas.save();
//			canvas.drawCircle(locationX, locationY,10, focusedShipShapePaint);
			paint.setTextSize(30);
			canvas.drawText("区域("+MapActivity1.areaname+")", locationX+5, locationY+5, paint);
			canvas.restore();
			continue;
					}
					p.lineTo(locationX, locationY);
					}
				
				canvas.save();
				canvas.drawPath(p, areaShapePaint);
				canvas.restore();
			return;
		}
		
		if (MapActivity1.needlayer.equals("alert")) {
//			print("onPrepareBufferImage MapActivity1.needlayer.equals(ship)");
			int locationX = tileBox.getPixXFromLonNoRot(MapActivity1.lo);
			int locationY = tileBox.getPixYFromLatNoRot(MapActivity1.la);
	String slat=getStringLat(Double.valueOf(MapActivity1.la));
	String slon=getStringLon(Double.valueOf(MapActivity1.lo));
//			if (moveflag) {
//				moveflag = false;
//				AnimateDraggingMapThread thread = view
//						.getAnimatedDraggingThread();
//				thread.startMoving(MapActivity1.la, MapActivity1.lo, 9, false);
//			}//移动到轨迹起始点。
			
			canvas.save();
			canvas.drawCircle(locationX, locationY,10, focusedShipShapePaint);
			focusedShipShapePaint.setTextSize(30);
			canvas.drawText("报警位置("+slon+","+slat+")", locationX+5, locationY+5, focusedShipShapePaint);
			canvas.restore();

			return;
		}
		if (MapActivity1.needlayer.equals("ship")) {
//			print("onPrepareBufferImage MapActivity1.needlayer.equals(ship)");
			int locationX = tileBox.getPixXFromLonNoRot(MapActivity1.lo);
			int locationY = tileBox.getPixYFromLatNoRot(MapActivity1.la);
//			
//			if (moveflag) {
//				moveflag = false;
//				AnimateDraggingMapThread thread = view
//						.getAnimatedDraggingThread();
//				thread.startMoving(MapActivity1.la, MapActivity1.lo, 9, false);
//			}//移动到轨迹起始点。
			
			Bitmap shipbm1 = getShipBitmap(1, false);
			Bitmap shipbm0 = getShipBitmap(0, false);
			canvas.save();
			if (shipbm1 != null && shipbm0 != null) {
				if (MapActivity1.sp > 0) {
					canvas.rotate(MapActivity1.co + 180, locationX, locationY);
					canvas.drawBitmap(shipbm1, locationX - shipbm1.getWidth()
							/ 2, locationY - shipbm1.getHeight() / 2 + 12
							* dm.density, shipShapPaint);
				} else {
					canvas.drawBitmap(shipbm0, locationX - shipbm0.getWidth()
							/ 2 + 2 * bmoffset, locationY - shipbm0.getHeight()
							/ 2 + 2 * bmoffset, shipShapPaint);
					canvas.drawCircle(locationX, locationY, 2, shipShapPaint1);
				}
			}
			canvas.restore();

			return;
		}
		refreshShipTraceLayer(canvas, tileBox);
	}

	private List<afterTrace> calltl = new ArrayList<afterTrace>();

//	private boolean trfresh = false;

	public interface afterTrace {
		public void traceRefreshed(RotatedTileBox tileBox);
	}

	public void addAftertrace(afterTrace tl) {
		calltl.add(tl);
	}

	private void parseXMLnew(InputStream inStream, List<TraceBean> msgs,
			LoadingTraceThread task) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		msgs.clear();
		// print("解析时ships： "+msgs.size());

		// 判断是否超时
		if (root.getNodeName().compareTo("session__timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			// print("轨迹层解析数据 拿到 超时了。");
			return;
		}

		print("数据条数： " + childNodes.getLength());
		// TODO 更新缓存部分
		// 船舶数据解析并加入_ships
		for (int j = 0; j < childNodes.getLength(); j++) {
			if (task.isCancelled()) {
				return;
			}
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					msgs.add(XmlParseUtility.parse(childElement,
							TraceBean.class));
				}
			}
		}

		if (msgs.size() > 0) {
			la = msgs.get(0).la;
			lo = msgs.get(0).lo;
		}
	}

	public void addPointInCurrentWindow(ArrayList<TraceBean> showArray,
			List<TraceBean> TraceBeans, RotatedTileBox tileBox) {
		// long startcount=System.currentTimeMillis();
		// int justflag=0;
		if (TraceBeans != null && tileBox != null) {
			LatLon lefttop = tileBox.getLeftTopLatLon();
			LatLon rightbottom = tileBox.getRightBottomLatLon();

			double minlon = lefttop.getLongitude();
			double maxlat = lefttop.getLatitude();
			double maxlon = rightbottom.getLongitude();
			double minlat = rightbottom.getLatitude();
//			showArray.clear();
			currentTraceBeans = new ArrayList<TraceBean>();
			try {
				for (TraceBean s : TraceBeans) {
					if(s.la.equals("")||s.lo.equals("")){
						continue;
					}
					if (Double.valueOf(s.la) <= maxlat
							&& Double.valueOf(s.la) >= minlat
							&& Double.valueOf(s.lo) >= minlon
							&& Double.valueOf(s.lo) <= maxlon) {
						currentTraceBeans.add(s);
					}
					else{
//						print("这个点在屏幕外");
						}
				}//计算当前屏幕内的点   后台已经完成。
//				currentTraceBeans = new ArrayList<TraceBean>();
//				currentTraceBeans.addAll(showArray);
				showArray.addAll(TraceBeans);
//				print("计算当前屏幕内的点" + showArray.size());
//				if (trfresh) {
//					trfresh = false;
					for (afterTrace call : new ArrayList<afterTrace>(calltl)) {
						call.traceRefreshed(tileBox);
					}
//				}
			} catch (java.util.ConcurrentModificationException cme) {
				cme.printStackTrace();
				currentTraceBeans = new ArrayList<TraceBean>();
				currentTraceBeans.addAll(TraceBeans);
				showArray.addAll(TraceBeans);
//				print("计算当前屏幕内的点" + showArray.size());
//				if (trfresh) {
//					trfresh = false;
					for (afterTrace call : new ArrayList<afterTrace>(calltl)) {
						call.traceRefreshed(tileBox);
					}
//				}
			}
		}

	}

	private void refreshShipTraceLayer(Canvas canvas, RotatedTileBox tileBox) {
//		print("准备刷新trace");
		flag = false;
		ArrayList<TraceBean> showArray = new ArrayList<TraceBean>();
		addPointInCurrentWindow(showArray, list1, tileBox);
		try {
			Path path = new Path();

			// print("point" + list1.size());

			for (int i = 0; i < showArray.size(); i++) {

				if (i >= 0) {

					double xx = showArray.get(i).getLo();
					double yy = showArray.get(i).getLa();
					if(showArray.get(i).z.equals("")){
//					print("trace z:"+list1.get(i).z+":::i:::"+i+"::la::"+list1.get(i).la+"::lo::"+list1.get(i).lo);
					continue;
					}

					int locationX = tileBox.getPixXFromLonNoRot(xx);
					int locationY = tileBox.getPixYFromLatNoRot(yy);

					if (i == 0) {
						path.moveTo(locationX, locationY);
						// 画起始的轨迹点
//						canvas.drawCircle(locationX - radius / 2 + 2
//								* dm.density, locationY - radius / 2 + 2
//								* dm.density, radius, locationPaint);
						canvas.drawCircle(locationX - radius / 2 + 2
								* dm.density, locationY - radius / 2 + 2
								* dm.density, radius+4*dm.density, locationPaint);

					} else {
						int j=0;
						if(showArray.get(i-1).z.equals("")){
							j=i-2;
						}else{
							j=i-1;
						}//xml包含这个错误导致的判断 <ship reference="../ship[403]"/>
						if (showArray.get(j).z.equals("1")) {
							// 在画当前的轨迹点之前，获得它前面一个轨迹点的屏幕位置（x1,y1),z=1不和下个点连线
//							int x1 = tileBox.getPixXFromLonNoRot(showArray.get(i - 1).getLo());
//							int y1 = tileBox.getPixYFromLatNoRot(showArray.get(i - 1).getLa());

//							// 画在前面一个点和当前一个点之间画一个箭头
//							drawArrow(path, x1, y1, locationX, locationY);

							// 由于前面画了一个箭头，需要移动画笔的位置到前一个点上，然后连线到当前轨迹点
							path.moveTo(locationX, locationY);

//							// 连线到当前轨迹点
//							path.lineTo(locationX, locationY);

							// 画一个轨迹点
//							canvas.drawCircle(locationX - radius / 2 + 2
//									* dm.density, locationY - radius / 2 + 2
//									* dm.density, radius, paint_mid_point);
//							if (showArray.get(i).z.equals("0")){
//								canvas.drawCircle(locationX - radius / 2 + 2
//										* dm.density, locationY - radius / 2 + 2
//										* dm.density, radius, locationPaint);
//								}

//								if (!showArray.get(i).ti.equals("")){
//									canvas.drawCircle(locationX - radius / 2 + 2
//											* dm.density, locationY - radius / 2 + 2
//											* dm.density, radius, locationPaint);
//									}
							
//							canvas.save();
//							canvas.drawPath(path, locationPaint);
//							canvas.restore();
//							path.moveTo(locationX, locationY);
						} else if (showArray.get(j).z.equals("0")){
							// 在画当前的轨迹点之前，获得它前面一个轨迹点的屏幕位置（x1,y1)
							int x1 = tileBox.getPixXFromLonNoRot(showArray.get(j).getLo());
							int y1 = tileBox.getPixYFromLatNoRot(showArray.get(j).getLa());

							// 画在前面一个点和当前一个点之间画一个箭头
							drawArrow(path, x1, y1, locationX, locationY);

							// 由于前面画了一个箭头，需要移动画笔的位置到前一个点上，然后连线到当前轨迹点
							path.moveTo(x1, y1);

							// 连线到当前轨迹点
							path.lineTo(locationX, locationY);

							// 画一个轨迹点
//							if (!showArray.get(i).ti.equals("")){
//								canvas.drawCircle(locationX - radius / 2 + 2
//										* dm.density, locationY - radius / 2 + 2
//										* dm.density, radius, locationPaint);
//								}

//							if (showArray.get(i).z.equals("1")){
//								canvas.drawCircle(locationX - radius / 2 + 2
//										* dm.density, locationY - radius / 2 + 2
//										* dm.density, radius, paint_mid_point);
//								}
						}
					}
					if (i == showArray.size() - 1) {
						canvas.drawCircle(locationX - radius / 2 + 2
								* dm.density, locationY - radius / 2 + 2
								* dm.density, radius+4*dm.density, locationPaint);
						canvas.save();
						canvas.drawPath(path, locationPaint);
						canvas.restore();
					}
				}
			}
			
			for (int i = 0; i < showArray.size(); i++) {

				if (i >= 0) {

					double xx = showArray.get(i).getLo();
					double yy = showArray.get(i).getLa();
					if(showArray.get(i).z.equals("")){
//					print("trace z:"+list1.get(i).z+":::i:::"+i+"::la::"+list1.get(i).la+"::lo::"+list1.get(i).lo);
					continue;
					}

					int locationX = tileBox.getPixXFromLonNoRot(xx);
					int locationY = tileBox.getPixYFromLatNoRot(yy);

					if (i == 0) {
						// 画起始的轨迹点
						canvas.drawCircle(locationX - radius / 2 + 2
								* dm.density, locationY - radius / 2 + 2
								* dm.density, radius, locationwhitePaint);
						canvas.drawCircle(locationX - radius / 2 + 2
								* dm.density, locationY - radius / 2 + 2
								* dm.density, radius, locationPaint);
//						canvas.drawCircle(locationX - radius / 2 + 2
//								* dm.density, locationY - radius / 2 + 2
//								* dm.density, radius, locationwhitePaint);

					} else {
						int j=0;
						if(showArray.get(i-1).z.equals("")){
							j=i-2;
						}else{
							j=i-1;
						}//xml包含这个错误导致的判断 <ship reference="../ship[403]"/>
						
						if (!showArray.get(i).ti.equals("")){
							canvas.drawCircle(locationX - radius / 2 + 2
									* dm.density, locationY - radius / 2 + 2
									* dm.density, radius, locationwhitePaint);
							}
							if (!showArray.get(i).ti.equals("")){
								canvas.drawCircle(locationX - radius / 2 + 2
										* dm.density, locationY - radius / 2 + 2
										* dm.density, radius, locationPaint);
								}
//							if (!showArray.get(i).ti.equals("")){
//								canvas.drawCircle(locationX - radius / 2 + 2
//										* dm.density, locationY - radius / 2 + 2
//										* dm.density, radius, locationwhitePaint);
//								}
					}
				}
			}
			// if(tracelable){
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 画一个箭头
	private void drawArrow(Path path, float x1, float y1, float x2, float y2) {

		// 如果两个点之间离开的很近，就不要画箭头了。
		if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) < 100)
			return;

		float nArrowHeadLength = 18 * dm.density; // 箭头的基本长度
		float nArrowHeadAngle = 15; // 箭头的夹角 15*2=30°
		nArrowHeadAngle = (float) (nArrowHeadAngle * Math.PI / 180); // radians

		float ang;
		float tang;
		float x3;
		float y3;
		float sign;

		ang = (float) Math.atan((y2 - y1) / (x2 - x1));

		// check for vertical path -> atan fails
		if ((x2 - x1) == 0) {
			if (y2 > y1)
				ang = 4.712389f; // 270 deg
			else
				ang = 1.570796f; // 90 deg
		}

		// if (x2 >= x1 && y2 >= y1) // quad 1
		// sign = -1;
		// else if (x2 <= x1 && y2 >= y1) // quad 2
		// sign = 1;
		// else if (x2 <= x1 && y2 <= y1) // quad 3
		// sign = 1;
		// else
		// sign = -1;

		if (x2 > x1)
			sign = -1;
		else {
			sign = 1;
		}// 好像可以控制方向。

		tang = nArrowHeadAngle + ang;
		x3 = (float) (x2 + x1) / 2
				+ (float) (sign * (nArrowHeadLength * Math.cos(tang)));
		y3 = (float) (y2 + y1) / 2
				+ (float) (sign * (nArrowHeadLength * Math.sin(tang)));

		path.moveTo((int) (x2 + x1) / 2, (int) (y2 + y1) / 2);
		path.lineTo((int) x3, (int) y3);

		path.moveTo((int) (x2 + x1) / 2, (int) (y2 + y1) / 2);

		tang = nArrowHeadAngle - ang;
		x3 = (float) (x2 + x1) / 2
				+ (float) (sign * (nArrowHeadLength * Math.cos(tang)));
		y3 = (float) (y2 + y1) / 2
				+ (float) (-sign * (nArrowHeadLength * Math.sin(tang)));
		path.lineTo((int) x3, (int) y3);
	}

	public void test(List<TraceBean> list, Canvas canvas, RotatedTileBox tileBox) {
		if (list.size() >= 1) {
			list1 = new ArrayList<TraceBean>();
			print("轨迹listsize: " + list.size());
			for (int i = 0; i < list.size(); i++) {
				// Log.v("mes"+i+"=", list.get(i).Polygon);
				list1.add(list.get(i));
			}
			if (moveflag) {
				moveflag = false;
				AnimateDraggingMapThread thread = view
						.getAnimatedDraggingThread();
				Double la = list1.get(0).getLa();
				Double lo = list1.get(0).getLo();
				thread.startMoving(la, lo, tileBox.getZoom(), false);
			}//移动到轨迹起始点。
			// currentzoom=tileBox.getZoom();
//			print("test轨迹刷新   traceInfoLayer请求");
//			trfresh = true;
//			view.callPrepareBufferImage("traceinfolayer", tileBox, false);
			view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox, false);
		}
	}

	public static void tipsMsg1(int cmd, Object str) {
		Message m = new Message();
		m.what = cmd;
		m.obj = str;
		MapActivity1.tracHandler.sendMessage(m);
	}

	 int currentzoom=-1;
	private HashMap<String, LoadingTraceThread> asyntaskmap = new HashMap<String, LoadingTraceThread>();

	private void clearMapByUUID(String uuid) {
		asyntaskmap.remove(uuid);
	}

	private void closeReqest() {
		if (asyntaskmap.isEmpty())
			return;
		Iterator<String> it = asyntaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			LoadingTraceThread task = asyntaskmap.get(uuid);
			task.cancel(true);
		}
	}

	private boolean isTheSameTileBox(RotatedTileBox tileBox) {
		if (tileBox.getZoom() != lastQueryTileBox.getZoom()) {
			return false;
		}
		if (lastQueryTileBox != null) {
			LatLon lastLatLon = lastQueryTileBox.getCenterLatLon();
			LatLon thisLatLon = tileBox.getCenterLatLon();

			if ((lastLatLon.getLatitude() != thisLatLon.getLatitude())
					|| lastLatLon.getLongitude() != thisLatLon.getLongitude()) {
				return false;
			}
			return true;
		}
		return false;
	}

	private RotatedTileBox lastQueryTileBox = null;
	private boolean isTheSameTileBox = false;

	@Override
	public void mapRefreshed(RotatedTileBox tileBox) {
		// TODO Auto-generated method stub
		this.tileBox = tileBox;
		if (MapActivity1.needlayer.equals("ship")||MapActivity1.needlayer.equals("alert")||MapActivity1.needlayer.equals("area")) {
			list1.clear();
			listp.clear();
			print("maprefresh MapActivity1.needlayer.equals(ship)");
			return;
		}
		if (flag) {
			// print("执行onDraw  trace");
			LoadingTraceThread lth = new LoadingTraceThread(tileBox, canvas);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				lth.executeOnExecutor(Executors.newCachedThreadPool(),
						new Void[0]);
			} else {
				lth.execute();
			}
			 currentzoom=tileBox.getZoom();
//			lastQueryTileBox = tileBox;
			// refreashShipTraceLayer(canvas, tileBox);
		} else {
//			if (lastQueryTileBox == null) {
//				lastQueryTileBox = tileBox;
//				isTheSameTileBox = false;
//			} else {
//				isTheSameTileBox = isTheSameTileBox(tileBox);
//			}

//			if (!isTheSameTileBox) {
			if(currentzoom==-1){
				currentzoom=tileBox.getZoom();
			}
			if(tileBox.getZoom()!=currentzoom){
				print("zoom 改变" + tileBox.getZoom());
				closeReqest();
				String uuid = UUID.randomUUID().toString();
				LoadingTraceThread lth = new LoadingTraceThread(tileBox, canvas);
				asyntaskmap.put(uuid, lth);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
					lth.executeOnExecutor(Executors.newCachedThreadPool(),
							new Void[0]);
				} else {
					lth.execute();
				}
//				lastQueryTileBox = tileBox;
				 currentzoom=tileBox.getZoom();
				// refreashShipTraceLayer(canvas, tileBox);
			}
		}
	}

}
