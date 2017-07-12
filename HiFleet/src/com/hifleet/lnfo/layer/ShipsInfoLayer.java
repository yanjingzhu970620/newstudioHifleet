package com.hifleet.lnfo.layer;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hifleet.activity.ShipInfoActivity;
import com.hifleet.adapter.ShipsListAdapter;
import com.hifleet.adapter.TeamShipsListAdapter;
import com.hifleet.bean.HeartBeatBean;
import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.TyphoonBean;
import com.hifleet.bean.TyphoonInfoBean;
import com.hifleet.bean.WeatherInfoBean;
import com.hifleet.bean.lableBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.AnimateDraggingMapThread;
import com.hifleet.map.ITileSource;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.LatLon;
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
import com.hifleet.thread.MyTeamShipsThread;
import com.hifleet.utility.Cell;
import com.hifleet.utility.XmlParseUtility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ShipsInfoLayer extends OsmandMapLayer implements
		IMapRefreshCallback {
    public boolean showallships=false;
	private QuadRect quadRect;

	protected static final int emptyTileDivisor = 16;
	public static final int OVERZOOM_IN = 0;// 2;

	public static final int START_SHOW = IndexConstants.START_TO_SHOW_SHIPS_ZOOM;
	public static final int TYPHOON_SHOW = IndexConstants.START_TO_SHOW_TYPHOON_ZOOM;
	double lengthPerpix;

	public static boolean allshipsrefresh = false;

	private final boolean mainMap;
	protected ITileSource map = null;
	protected MapTileAdapter mapTileAdapter = null;

	Paint paintBitmap, locationPaint, linePaint, colinepaint;

	protected RectF bitmapToDraw = new RectF();
	protected Rect bitmapToZoom = new Rect();
	private boolean shipfirst = true;

	protected static OsmandMapTileView view;
	protected ResourceManager resourceManager;
	private OsmandSettings settings;
	AnimateDraggingMapThread thread;
	private boolean visible = true;
	private Timer timer = null;
	private TimerTask task = null;
	// public static boolean lableadd = false;
	// public String myrole="";
	// public static boolean islogin = false;
	private List<HeartBeatBean> heartBeatBean = new ArrayList<HeartBeatBean>();
	private List<ShipsBean> _ships = new ArrayList<ShipsBean>();
	private ArrayList<ShipsBean> highlightedships = new ArrayList<ShipsBean>();// 更新得到的高亮船舶。
	private List<ShipsBean> focusedships;// 需要画的红色船舶

	public static List<ShipsBean> tap_ships = new ArrayList<ShipsBean>();// 存储当前点击的船舶
	public static List<ShipsBean> tap_shipsPoint = new ArrayList<ShipsBean>();// 当前点击的船舶的历史点。
	public static List<ShipsBean> alltap_ships = new ArrayList<ShipsBean>();// 存储点击过的船舶
	public static List<ShipsBean> teamship = new ArrayList<ShipsBean>();// 存储船队列表当前点击的船
	public static List<ShipsBean> allteamship = new ArrayList<ShipsBean>();// 存储船队列表点击的所有船。//如果移动海图获取的是所有船队船舶就不需要了。
	public static List<ShipsBean> searchshipsBeans = new ArrayList<ShipsBean>();// 存储当前搜索到的船舶
	public static List<ShipsBean> allsearchshipsBeans = new ArrayList<ShipsBean>();// 存储搜索过的船舶

	public static List<lableBean> caddedlable = new ArrayList<lableBean>();// 当前屏幕内标签

	private HashMap<String, ShipsBean> _ships2Draw = new HashMap<String, ShipsBean>();// =
																						// new
																						// ArrayList<ShipsBean>();

	Date lastcomparedate=null;
	public static boolean loadweather=true;
	private List<WeatherInfoBean> weatherbeans=new ArrayList<WeatherInfoBean>();
	private List<TyphoonBean> typhoonbeans=new ArrayList<TyphoonBean>();
//	private List<TyphoonInfoBean> typhooninfobeans=new ArrayList<TyphoonInfoBean>();
//	List<TyphoonInfoBean> foretyphooninfobeans=new ArrayList<TyphoonInfoBean>();
	private HashMap<String,List<TyphoonInfoBean>> typhoonmap=new HashMap<String,List<TyphoonInfoBean>>();
	private HashMap<String,List<TyphoonInfoBean>> typhoonforemap=new HashMap<String,List<TyphoonInfoBean>>();

	private Bitmap weatherIcon, weatherIcon1;

	Bitmap shipbm1tt, shipbm0tt, shipbm1tf, shipbm0tf, shipbm1fft, shipbm1fff,
			shipbm0fft, shipbm0fff;
	Bitmap shipbm = null;
	// public long bwidth=0;

	private Paint paint_mid_point;
	private Paint shipShapPaint, shipShapHighPaint, shipShapPaint1,
			shipShapHighPaint1,weatherPaint,typhoonPaint,foretyphoonPaint,typhoonfillPaint,typhooncirclepaint;

	private Paint paint, paint1, paint2;
	private Paint focusedShipShapePaint, focusedShipShapePaint1,
			focusedShipShapePaint2;

	private HashMap<String, Cell> _cellHashMap = new HashMap<String, Cell>();// 保存cellid和cell的对应关�?
	private HashMap<String, String> _shipCellMap = new HashMap<String, String>();// 保存mmsi和该船所在的cell的对应关�?

	// public List<ShipsBean> allteamShipsBeans = new ArrayList<ShipsBean>();

	public ShipsInfoLayer(boolean mainMap) {
		this.mainMap = mainMap;
	}

	@Override
	public boolean drawInScreenPixels() {
		return false;
	}

	DisplayMetrics dm;
	static OsmandApplication app;

	@Override
	public void initLayer(OsmandMapTileView view) {
		System.out.println("create shipsinfolayer");
		this.view = view;
		app = view.getApplication();
		settings = view.getSettings();
		resourceManager = view.getApplication().getResourceManager();
		thread = view.getAnimatedDraggingThread();
		dm = view.getResources().getDisplayMetrics();
		showallships=app.myPreferences.getBoolean("isShowdot",
				false);
		bmoffset =dip2px(dm.density, 3);

		// System.out.println("==================");
		// paintBitmap = new Paint();
		// paintBitmap.setFilterBitmap(true);

		locationPaint = new Paint();
		locationPaint.setColor(view.getResources().getColor(R.color.black));
		locationPaint.setStyle(Style.FILL_AND_STROKE);
		locationPaint.setAntiAlias(true);
		locationPaint.setFilterBitmap(true);
		locationPaint.setDither(true);

		linePaint = new Paint();
		linePaint.setColor(view.getResources().getColor(R.color.black));
		linePaint.setStyle(Style.STROKE);
		linePaint.setAntiAlias(true);
		linePaint.setFilterBitmap(true);
		linePaint.setDither(true);

		colinepaint = new Paint();
		colinepaint.setColor(view.getResources().getColor(R.color.black));
		colinepaint.setStrokeWidth(3 / 2 * dm.density);
		colinepaint.setStyle(Style.STROKE);
		colinepaint.setAntiAlias(true);
		colinepaint.setFilterBitmap(true);
		colinepaint.setDither(true);
		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
		colinepaint.setPathEffect(effects);

		foretyphoonPaint = new Paint();
		foretyphoonPaint.setColor(view.getResources().getColor(R.color.red));
		foretyphoonPaint.setStrokeWidth(3/2 * dm.density);
		foretyphoonPaint.setStyle(Style.STROKE);
		foretyphoonPaint.setAntiAlias(true);
		foretyphoonPaint.setFilterBitmap(true);
		foretyphoonPaint.setDither(true);
		PathEffect effects1 = new DashPathEffect(new float[]{8,8,8,8},4);  
		foretyphoonPaint.setPathEffect(effects1);
		
		typhooncirclepaint = new Paint();
		typhooncirclepaint.setColor(0xfffefe83);
		typhooncirclepaint.setAntiAlias(true);
		typhooncirclepaint.setStrokeWidth(0);
//		typhooncirclepaint.setAlpha(150);
		typhooncirclepaint.setStyle(Style.FILL_AND_STROKE);// 台风填充色
		

		shipShapPaint = new Paint();
		shipShapPaint.setColor(0xfffefe83);
		shipShapPaint.setAntiAlias(true);
		shipShapPaint.setStrokeWidth(0);
		shipShapPaint.setStyle(Style.FILL_AND_STROKE);// 普通船填充色

		shipShapPaint1 = new Paint();
		shipShapPaint1.setColor(view.getResources().getColor(R.color.black));
		shipShapPaint1.setAntiAlias(true);
		shipShapPaint1.setStrokeWidth(3 / 2 * dm.density);
		shipShapPaint1.setStyle(Style.STROKE);// 普通船边框

		typhoonPaint = new Paint();
		typhoonPaint.setColor(view.getResources().getColor(R.color.red));
		typhoonPaint.setAntiAlias(true);
		typhoonPaint.setStrokeWidth(2 * dm.density);
		typhoonPaint.setStyle(Style.STROKE);// 台风红色边框

		typhoonfillPaint = new Paint();
		typhoonfillPaint.setColor(view.getResources().getColor(R.color.yellow));
		typhoonfillPaint.setAntiAlias(true);
		typhoonfillPaint.setStrokeWidth(0);
		typhoonfillPaint.setStyle(Style.FILL_AND_STROKE);// 台风黄色圈
		// typhoonfillPaint
		weatherPaint = new Paint();
		weatherPaint.setAntiAlias(true);
		weatherPaint.setStrokeWidth(0);

		focusedShipShapePaint = new Paint();
		focusedShipShapePaint.setColor(view.getResources()
				.getColor(R.color.red));
		focusedShipShapePaint.setAntiAlias(true);
		focusedShipShapePaint.setStrokeWidth(0);
		focusedShipShapePaint.setStyle(Style.FILL_AND_STROKE);// 中间填充色

		focusedShipShapePaint1 = new Paint();
		focusedShipShapePaint1.setColor(view.getResources().getColor(
				R.color.black));
		focusedShipShapePaint1.setAntiAlias(true);
		focusedShipShapePaint1.setStrokeWidth(3 / 2 * dm.density);
		focusedShipShapePaint1.setStyle(Style.STROKE);// 边框

		focusedShipShapePaint2 = new Paint();
		focusedShipShapePaint2.setColor(view.getResources().getColor(
				R.color.red));
		focusedShipShapePaint2.setAntiAlias(true);
		focusedShipShapePaint2.setStrokeWidth(3 / 2 * dm.density);
		focusedShipShapePaint2.setStyle(Style.STROKE);// 聚焦框

		shipShapHighPaint = new Paint();
		shipShapHighPaint.setAntiAlias(true);
		shipShapHighPaint.setColor(view.getResources().getColor(R.color.red));
		shipShapHighPaint.setStrokeWidth(3 / 2 * dm.density);
		shipShapHighPaint.setStyle(Style.FILL_AND_STROKE);

		paint_mid_point = new Paint();
		paint_mid_point.setStyle(Style.FILL_AND_STROKE);
		paint_mid_point.setAntiAlias(true);
		paint_mid_point.setColor(view.getResources().getColor(
				com.hifleet.plus.R.color.blue));

		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(3);
		paint.setAlpha(180);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint1 = new Paint();
		paint1.setColor(Color.BLACK);
		paint1.setAntiAlias(true);
		paint1.setStrokeJoin(Paint.Join.ROUND);
		paint1.setStrokeCap(Paint.Cap.ROUND);
		paint1.setStrokeWidth(dip2px(dm.density, 3 / 2));
		paint2 = new Paint();
		paint2.setColor(Color.BLACK);
		paint2.setAntiAlias(true);
		paint2.setStrokeWidth(dip2px(dm.density, 3 / 2));
		paint2.setStyle(Style.STROKE);

		BitmapDrawable bd = (BitmapDrawable) view.getResources().getDrawable(
				R.drawable.icon_weather_windwave);
		weatherIcon = bd.getBitmap();

		// shipbm1tt = getShipBitmap(1, true, true);
		// shipbm0tt = getShipBitmap(0, true, true);
		// shipbm1tf = getShipBitmap(1, true, false);
		// shipbm0tf = getShipBitmap(0, true, false);

		if (timer == null) {
			timer = new Timer(true);
		}
		if (task == null) {
			task = new TimerTask() {
				public void run() {
					timerRefreshShipsData(tileBox);
				}
			};
			timer.schedule(task, 30 * 1000, 50 * 1000);
		}
//		System.out.println("create shipsinfolayer ready");
//		System.out.println("role typhoon 权限"+app.getMyrole());
//		if(view.getApplication().getMyrole().equals("vvip")){
//		System.out.println("create shipsinfolayer finish"+app.getLoginbean().weather);
//		}else{
//			System.out.println("create shipsinfolayer finish finish");
//		}
		
//		if(!view.getApplication().getMyrole().equals("vvip")||view.getApplication().getLoginbean().weather.equals("1")){
		callTyphoonAction();
//	    }
	}

	private int basicLength = 12;
	private int exactLength;
	private float bmoffset = // 0.5f*dm.density;
	2f;

	// 画一个填充色的船�?
	private Bitmap getShipBitmap(double shipSp, boolean focused,
			boolean singletap, boolean heading, int color, int alpha) {
		if (color == -1 && alpha == -1) {
			if (shipSp > 0) {
				if (heading) {
					if (shipbm1fft != null) {
						return shipbm1fft;
					} else {
						shipbm1fft = drawShipBitmap(shipSp, focused, singletap,
								heading, color, alpha);
						return shipbm1fft;
					}
				} else {
					if (shipbm1fff != null) {
						return shipbm1fff;
					} else {
						shipbm1fff = drawShipBitmap(shipSp, focused, singletap,
								heading, color, alpha);
						return shipbm1fff;
					}
				}
			} else {
				if (heading) {
					if (shipbm0fft != null) {
						return shipbm0fft;
					} else {
						shipbm0fft = drawShipBitmap(shipSp, focused, singletap,
								heading, color, alpha);
						return shipbm0fft;
					}
				} else {
					if (shipbm0fff != null) {
						return shipbm0fff;
					} else {
						shipbm0fff = drawShipBitmap(shipSp, focused, singletap,
								heading, color, alpha);
						return shipbm0fff;
					}
				}
			}
		}// 固定类型直接返回。
		return drawShipBitmap(shipSp, focused, singletap, heading, color, alpha);
	}

	@SuppressLint("NewApi")
	private Bitmap drawShipBitmap(double shipSp, boolean focused,
			boolean singletap, boolean heading, int color, int alpha) {

		if (color != -1) {
			focusedShipShapePaint.setColor(color);
		} else {
			focusedShipShapePaint.setColor(view.getResources().getColor(
					R.color.red));
		}
		if (alpha != -1) {
			focusedShipShapePaint.setAlpha(alpha);
			shipShapPaint.setAlpha(alpha);
		} else {
			focusedShipShapePaint.setAlpha(255);
			shipShapPaint.setAlpha(255);
		}
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
			if (singletap) {
				shipbm = Bitmap.createBitmap(dm, (int) (24 * dm.density)
						+ (int) (3 * bmoffset), (int) (42 * dm.density)
						+ (int) (3 * bmoffset), Bitmap.Config.ARGB_8888);
				path1.moveTo(0, 0);
				path1.lineTo(0, 6 * dm.density);
				path1.moveTo(0, 0);
				path1.lineTo(6 * dm.density, 0);

				path1.moveTo(0, 42 * dm.density);
				path1.lineTo(0, 36 * dm.density);
				path1.moveTo(0, 42 * dm.density);
				path1.lineTo(6 * dm.density, 42 * dm.density);

				path1.moveTo(24 * dm.density, 0);
				path1.lineTo(24 * dm.density, 6 * dm.density);
				path1.moveTo(24 * dm.density, 0);
				path1.lineTo(18 * dm.density, 0);

				path1.moveTo(24 * dm.density, 42 * dm.density);
				path1.lineTo(18 * dm.density, 42 * dm.density);
				path1.moveTo(24 * dm.density, 42 * dm.density);
				path1.lineTo(24 * dm.density, 36 * dm.density);

				path.moveTo(6, 6);
				path.lineTo(18 * dm.density, 6);
				path.lineTo(12 * dm.density, 30 * dm.density);
				path.lineTo(6, 6);
				if (heading) {
					path.moveTo(12 * dm.density, 30 * dm.density);
					path.lineTo(12 * dm.density, (24 + 18) * dm.density);
				}
			} else {
				shipbm = Bitmap.createBitmap(dm, (int) (12 * dm.density)
						+ (int) (3 * bmoffset), (int) (36 * dm.density)
						+ (int) (3 * bmoffset), Bitmap.Config.ARGB_8888);
				path.moveTo(0, 0);
				path.lineTo(12 * dm.density, 0);
				path.lineTo(6 * dm.density, 24 * dm.density);
				path.lineTo(0, 0);
				if (heading) {
					path.moveTo(6 * dm.density, 24 * dm.density);
					path.lineTo(6 * dm.density, (24 + 12) * dm.density);
				}
			}
		} else {
			if (singletap) {
				shipbm = Bitmap.createBitmap(dm, (int) (28 * dm.density)
						+ (int) (3 * bmoffset), (int) (28 * dm.density)
						+ (int) (3 * bmoffset), Bitmap.Config.ARGB_8888);
				path1.moveTo(0, 0);
				path1.lineTo(0, 6 * dm.density);
				path1.moveTo(0, 0);
				path1.lineTo(6 * dm.density, 0);

				path1.moveTo(0, 28 * dm.density);
				path1.lineTo(0, 22 * dm.density);
				path1.moveTo(0, 28 * dm.density);
				path1.lineTo(6 * dm.density, 28 * dm.density);

				path1.moveTo(28 * dm.density, 0);
				path1.lineTo(28 * dm.density, 6 * dm.density);
				path1.moveTo(28 * dm.density, 0);
				path1.lineTo(22 * dm.density, 0);

				path1.moveTo(28 * dm.density, 28 * dm.density);
				path1.lineTo(22 * dm.density, 28 * dm.density);
				path1.moveTo(28 * dm.density, 28 * dm.density);
				path1.lineTo(28 * dm.density, 22 * dm.density);

				path.moveTo(6 * dm.density, 14 * dm.density);
				path.lineTo(14 * dm.density, 22 * dm.density);
				path.lineTo(22 * dm.density, 14 * dm.density);
				path.lineTo(14 * dm.density, 6 * dm.density);
				path.lineTo(6 * dm.density, 14 * dm.density);
			} else {
				// 画一个菱�?
				shipbm = Bitmap.createBitmap(dm, (int) (16 * dm.density)
						+ (int) (3 * bmoffset), (int) (16 * dm.density)
						+ (int) (3 * bmoffset), Bitmap.Config.ARGB_8888);
				path.moveTo(0, 8 * dm.density);
				path.lineTo(8 * dm.density, 16 * dm.density);
				path.lineTo(16 * dm.density, 8 * dm.density);
				path.lineTo(8 * dm.density, 0 * dm.density);
				path.lineTo(0 * dm.density, 8 * dm.density);
			}
		}
		can = new Canvas(shipbm);
		if (singletap) {
			can.drawPath(path1, focusedShipShapePaint2);
			can.drawPath(path, focusedShipShapePaint);
			can.drawPath(path, focusedShipShapePaint1);
		} else {
			if (!focused) {
				can.drawPath(path, shipShapPaint);
				can.drawPath(path, shipShapPaint1);
			} else {
				can.drawPath(path, focusedShipShapePaint);
				can.drawPath(path, focusedShipShapePaint1);
			}
		}

		return shipbm;
	}

	@SuppressLint("NewApi")
	// 根据长宽画出。
	private Bitmap getTrueshapeShipBitmap(double width, double length,
			double shipSp, boolean focused, boolean singletap, boolean heading,
			int color, int alpha) {
		if (color != -1) {
			focusedShipShapePaint.setColor(color);
		} else {
			focusedShipShapePaint.setColor(view.getResources().getColor(
					R.color.red));
		}
		if (alpha != -1) {
			focusedShipShapePaint.setAlpha(alpha);
			shipShapPaint.setAlpha(alpha);
		} else {
			focusedShipShapePaint.setAlpha(255);
			shipShapPaint.setAlpha(255);
		}
		exactLength = (int) dm.densityDpi * basicLength;
		int wlen = (int) (width / lengthPerpix);
		int llen = (int) (length / lengthPerpix);
		// exactLength = (int)dm.densityDpi *basicLength;
		Bitmap shipbm;// = Bitmap.createBitmap(dm,
		// 14*(int)dm.density,38*(int)dm.density,
		// Bitmap.Config.ARGB_8888);

		Canvas can;// = new Canvas(shipbm);

		Path path = new Path();
		Path path1 = new Path();
		if (shipSp > 0) {
			if (singletap) {
				shipbm = Bitmap.createBitmap(dm,
						(int) (12 * dm.density + wlen + 3 * (int) bmoffset),
						(int) (22 * dm.density + llen + 3 * (int) bmoffset),
						Bitmap.Config.ARGB_8888);// 带了方向
				path1.moveTo(0, 0);
				path1.lineTo(0, 6 * dm.density);
				path1.moveTo(0, 0);
				path1.lineTo(6 * dm.density, 0);

				path1.moveTo(0, 12 * dm.density + llen);
				path1.lineTo(0, 6 * dm.density + llen);
				path1.moveTo(0, 12 * dm.density + llen);
				path1.lineTo(6 * dm.density, 12 * dm.density + llen);

				path1.moveTo(12 * dm.density + wlen, 0);
				path1.lineTo(12 * dm.density + wlen, 6 * dm.density);
				path1.moveTo(12 * dm.density + wlen, 0);
				path1.lineTo(6 * dm.density + wlen, 0);

				path1.moveTo(12 * dm.density + wlen, 12 * dm.density + llen);
				path1.lineTo(6 * dm.density + wlen, 12 * dm.density + llen);
				path1.moveTo(12 * dm.density + wlen, 12 * dm.density + llen);
				path1.lineTo(12 * dm.density + wlen, 6 * dm.density + llen);

				path.moveTo(6 * dm.density, 3 * dm.density);
				path.lineTo(6 * dm.density + wlen / 3, 0);
				path.lineTo(6 * dm.density + wlen * 2 / 3, 0);
				path.lineTo(6 * dm.density + wlen, 3 * dm.density);
				path.lineTo(6 * dm.density + wlen, 3 * dm.density + llen);
				path.lineTo(6 * dm.density + wlen / 2, 3 * dm.density + llen
						+ 6 * dm.density);
				path.lineTo(6 * dm.density, 3 * dm.density + llen);
				path.lineTo(6 * dm.density, 3 * dm.density);

				if (heading) {
					path.moveTo(6 * dm.density + wlen / 2, 3 * dm.density
							+ llen + 6 * dm.density);
					path.lineTo(6 * dm.density + wlen / 2, 3 * dm.density
							+ llen + 6 * dm.density + 12 * dm.density);// 箭头部分。
				}

			} else {
				shipbm = Bitmap.createBitmap(dm,
						(int) (0 * dm.density + wlen + 3 * (int) bmoffset),
						(int) (22 * dm.density + llen + 3 * (int) bmoffset),
						Bitmap.Config.ARGB_8888);
				path.moveTo(0 * dm.density, 3 * dm.density);
				path.lineTo(0 * dm.density + wlen / 3, 0);
				path.lineTo(0 * dm.density + wlen * 2 / 3, 0);
				path.lineTo(0 * dm.density + wlen, 3 * dm.density);
				path.lineTo(0 * dm.density + wlen, 3 * dm.density + llen);
				path.lineTo(0 * dm.density + wlen / 2, 3 * dm.density + llen
						+ 6 * dm.density);
				path.lineTo(0 * dm.density, 3 * dm.density + llen);
				path.lineTo(0 * dm.density, 3 * dm.density);

				if (heading) {
					path.moveTo(0 * dm.density + wlen / 2, 3 * dm.density
							+ llen + 6 * dm.density);
					path.lineTo(0 * dm.density + wlen / 2, 3 * dm.density
							+ llen + 6 * dm.density + 12 * dm.density);
				}
			}
		} else {
			if (singletap) {
				shipbm = Bitmap.createBitmap(dm,
						(int) (12 * dm.density + wlen + 3 * (int) bmoffset),
						(int) (12 * dm.density + llen + 3 * (int) bmoffset),
						Bitmap.Config.ARGB_8888);
				path1.moveTo(0, 0);
				path1.lineTo(0, 6 * dm.density);
				path1.moveTo(0, 0);
				path1.lineTo(6 * dm.density, 0);

				path1.moveTo(0, 12 * dm.density + llen);
				path1.lineTo(0, 6 * dm.density + llen);
				path1.moveTo(0, 12 * dm.density + llen);
				path1.lineTo(6 * dm.density, 12 * dm.density + llen);

				path1.moveTo(12 * dm.density + wlen, 0);
				path1.lineTo(12 * dm.density + wlen, 6 * dm.density);
				path1.moveTo(12 * dm.density + wlen, 0);
				path1.lineTo(6 * dm.density + wlen, 0);

				path1.moveTo(12 * dm.density + wlen, 12 * dm.density + llen);
				path1.lineTo(6 * dm.density + wlen, 12 * dm.density + llen);
				path1.moveTo(12 * dm.density + wlen, 12 * dm.density + llen);
				path1.lineTo(12 * dm.density + wlen, 6 * dm.density + llen);

				path.moveTo(6 * dm.density, 3 * dm.density);
				path.lineTo(6 * dm.density + wlen / 3, 0);
				path.lineTo(6 * dm.density + wlen * 2 / 3, 0);
				path.lineTo(6 * dm.density + wlen, 3 * dm.density);
				path.lineTo(6 * dm.density + wlen, 3 * dm.density + llen);
				path.lineTo(6 * dm.density + wlen / 2, 3 * dm.density + llen
						+ 6 * dm.density);
				path.lineTo(6 * dm.density, 3 * dm.density + llen);
				path.lineTo(6 * dm.density, 3 * dm.density);
				// if(heading){
				// path.moveTo(6 * dm.density + wlen / 2, 4 * dm.density + llen
				// + 6 * dm.density);
				// path.lineTo(6 * dm.density + wlen / 2, 4 * dm.density + llen
				// + 6 * dm.density);
				// }

			} else {
				shipbm = Bitmap.createBitmap(dm,
						(int) (0 * dm.density + wlen + 3 * (int) bmoffset),
						(int) (10 * dm.density + llen + 3 * (int) bmoffset),
						Bitmap.Config.ARGB_8888);
				path.moveTo(0 * dm.density, 3 * dm.density);
				path.lineTo(0 * dm.density + wlen / 3, 0);
				path.lineTo(0 * dm.density + wlen * 2 / 3, 0);
				path.lineTo(0 * dm.density + wlen, 3 * dm.density);
				path.lineTo(0 * dm.density + wlen, 3 * dm.density + llen);
				path.lineTo(0 * dm.density + wlen / 2, 3 * dm.density + llen
						+ 6 * dm.density);
				path.lineTo(0 * dm.density, 3 * dm.density + llen);
				path.lineTo(0 * dm.density, 3 * dm.density);
				// path.moveTo(0 * dm.density + wlen / 2, 4
				// * dm.density + llen + 6 * dm.density
				// );
				// path.lineTo(0 * dm.density + wlen / 2, 4
				// * dm.density + llen + 6 * dm.density
				// );
			}
		}
		can = new Canvas(shipbm);
		if (singletap) {
			can.drawPath(path1, focusedShipShapePaint2);
			can.drawPath(path, focusedShipShapePaint);
			can.drawPath(path, focusedShipShapePaint1);
		} else {
			if (!focused) {
				can.drawPath(path, shipShapPaint);
				can.drawPath(path, shipShapPaint1);
			} else {
				can.drawPath(path, focusedShipShapePaint);
				can.drawPath(path, focusedShipShapePaint1);
			}
		}
		return shipbm;
	}

	private Cell getCellIndex(double lon, double lat) {
		double _x = Math.floor((lon + 180) * 10);
		double _y = Math.floor((lat + 90) * 10);
		return new Cell(_x, _y);
	}

	public Canvas canvas;
	public static RotatedTileBox tileBox;
	private long lastCallAsynTaskTime = 0;
	private long lastCallTime = 0;
	private double callIntervalLimit = 0.5 * 1000L;
	private double callIntervalLimit1 = 1 * 1000L;
	private boolean teamflag = true;

	private boolean teamflag1 = true;

	HashMap<String, ShipsBean> shipsBeans = new HashMap<String, ShipsBean>();

	public static void callbuffer(boolean teamfirst) {
		ShipLableLayer.teamlable = true;
		if (teamfirst) {
			if (app.isLabeladd()) {
				view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox,
						false);
				// }
			} else {
				// if((tileBox.getZoom()>=8&&tileBox.getZoom()<10)){
				// print("lableadd其实不是true。。。");
				view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);// 刷新一次海图。
				// }else{
				// view.callPrepareBufferImage("shipsInfoLayer",
				// tileBox,false);
				// }
			}// 船队异步刷新
		}
	}

	public static void teamCallLable(
			HashMap<String, ShipsBean> currentshipsBeans) {
		// System.err.println("发送label 请求");
		for (afterShip call : new ArrayList<afterShip>(c)) {
			call.shipRefresh(tileBox, currentshipsBeans);
		}
	}

	long predrawtime = 0;
	long nowdrawtime;

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {

		this.tileBox = tileBox;
		this.canvas = canvas;

	}

	private void timerRefreshShipsData(RotatedTileBox tileBox) {
		// System.err.println("timer 刷新船舶数据");
		if (tileBox.getZoom() >= START_SHOW) {
			// 此时要刷新其他船舶
			allshipsrefresh = true;
			callShipInfoAction(tileBox, "timer");
			callHighLightedShipInfoAction(tileBox, "timer");// 高亮船舶查询,参数区分是否是移动刷新

			if(app.isIslogin()&&(!app.getMyrole().equals("vvip")||app.getLoginbean().getObservatory().equals("1"))){
				callWeatherInfoAction(tileBox, "move");
				 }
			return;

		} else {
			callHighLightedShipInfoAction(tileBox, "timer");
			if(app.isIslogin()&&(!app.getMyrole().equals("vvip")||app.getLoginbean().getObservatory().equals("1"))){
				callWeatherInfoAction(tileBox, "move");
				 }
			return;
		}
		// else {
		//
		// if (!isTheSameTileBox) {
		// closeReqest1();
		// if (app.myPreferences.getBoolean("IsLogin", false)) {
		// String uuid = UUID.randomUUID().toString();
		// MyTeamShipsThread thread = new MyTeamShipsThread();//加载船队船舶
		// teamntaskmap.put(uuid, thread);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// //
		// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
		// thread.executeOnExecutor(Executors.newCachedThreadPool(),
		// new String[0]);
		// } else {
		// thread.execute();
		// }
		// }
		// return;
		// }
		// }
	}

	// 接收来到MapTileLayer发出的海图刷新结束的通知
	public void mapRefreshed(RotatedTileBox tileBox) {
		this.tileBox = tileBox;
		// if (!app.myPreferences.getBoolean("isShowdot", true)||
		// tileBox.getZoom() >= START_SHOW) {
		// print("收到刷图结束通知。");
		callDownloadShipsData(tileBox);
		// }
	}

	// long justCallTime = 0, thisCall = 0;
	private RotatedTileBox lastQueryTileBox = null;
	private boolean isTheSameTileBox = false;

	// 收到海图刷新结束，开始做以下几件事情：
	// 1.请求船舶数据（在适当的比例尺下，并且屏幕有移动，即屏幕的中心点前后发生了变化）
	// 2，刷新船舶。（在适当的比例尺下刷新交通流船舶，总是刷新船队船舶）
	private void callDownloadShipsData(RotatedTileBox tileBox) {
		// print("收到通知。"+tileBox.getZoom());
		// thisCall = System.currentTimeMillis();
		// long timeGap = thisCall - justCallTime;
		// print("timeGap： "+timeGap+(timeGap > (1 * 800L)));
		if (tileBox.getZoom() >= START_SHOW) {
			// 此时要刷新其他船舶
			if (lastQueryTileBox == null) {
				double dist = tileBox.getDistance(0, tileBox.getPixHeight() / 2, tileBox.getPixWidth(), tileBox.getPixHeight() / 2);
				lengthPerpix = dist/tileBox.getPixWidth() ;
//				lengthPerpix = tileBox.getDistance(0, 0, 0, 1);
				lastQueryTileBox = tileBox;
				isTheSameTileBox = false;
			} else {
				isTheSameTileBox = isTheSameTileBox(tileBox);
			}

			if (!isTheSameTileBox) {
				// 如果前后两次收到的刷新船舶时，屏幕范围不同，那么就发起请求最近一次的。
				// justCallTime = thisCall;
				// print("shipinfo call方法被调用请求");
				allshipsrefresh = true;
				if(showallships){
				callShipInfoAction(tileBox, "move");
				}

				callHighLightedShipInfoAction(tileBox, "move");// 高亮船舶查询
				lastQueryTileBox = tileBox;
				return;
			}

			// print("时间间隔： "+(thisCall - justCallTime)
			// +", thisCall: "+thisCall+", justCall: "+justCallTime);

			// 如果前后收到两次通知的时间间隔比较短，就不做处理

			// if (timeGap > (1 * 200L)) {
			// print("超过时间间隔，调用view中的callPrepareBufferImage函数。");
			// view.callPrepareBufferImage("shipsInfoLayer", tileBox, false);
			// view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox,
			// false);
			// justCallTime = thisCall;
			// }

		} else {

			if (lastQueryTileBox == null) {
				double dist = tileBox.getDistance(0, tileBox.getPixHeight() / 2, tileBox.getPixWidth(), tileBox.getPixHeight() / 2);
				lengthPerpix = dist/tileBox.getPixWidth() ;
				lastQueryTileBox = tileBox;
				isTheSameTileBox = false;
			} else {
				isTheSameTileBox = isTheSameTileBox(tileBox);
			}

			if (!isTheSameTileBox) {
				// 如果前后两次收到的刷新船舶时，屏幕范围不同，那么就发起请求最近一次的。
				// justCallTime = thisCall;
				// print("再次加载船舶");
				// refresh=true;
				// if(tileBox.getZoom()<6){
				// callbuffer();
				// lableadd=app.myPreferences.getBoolean("isShowMyTeamName",
				// true);
				// }else{
				closeReqest1();
				if (app.isIslogin()) {
					String uuid = UUID.randomUUID().toString();
					MyTeamShipsThread thread = new MyTeamShipsThread(app);// 加载船队船舶
					teamntaskmap.put(uuid, thread);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
						thread.executeOnExecutor(
								Executors.newCachedThreadPool(), new String[0]);
					} else {
						thread.execute();
					}
				}
				if(app==null){System.err.println("shipsinfolayer app nukll");}
				if(loadweather&&app.isIslogin()&&(!app.getMyrole().equals("vvip")||app.getLoginbean().getObservatory().equals("1"))){
				callWeatherInfoAction(tileBox, "move");
				 }
				callHighLightedShipInfoAction(tileBox, "move");
				lastQueryTileBox = tileBox;
				return;
			}

			// if(timeGap > (1 * 200L)){
			// 这种情况下，当前比例尺比较小，还没有到该显示其他船舶的时候，那就只刷新船队中的船舶。

			// print("船舶层 else中调用veiw中的callPrepareBufferImage函数。");

			// view.callPrepareBufferImage("shipsInfoLayer", tileBox, false);
			// view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox,
			// false);//刷完图先刷新一次，貌似循环时已经刷新，取消！
			// justCallTime = thisCall;
			// }
		}
	}

	// 判断前后两次请求的屏幕范围是否是相同的。返回true就是相同的，返回false就是不同的。
	private boolean isTheSameTileBox(RotatedTileBox tileBox) {
		if (tileBox.getZoom() != lastQueryTileBox.getZoom()) {
			// System.err.println("lastQueryTileBox !=");
			double dist = tileBox.getDistance(0, tileBox.getPixHeight() / 2, tileBox.getPixWidth(), tileBox.getPixHeight() / 2);
			lengthPerpix = dist/tileBox.getPixWidth() ;
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

	@Override
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		// print("刷船。");
		this.tileBox = tileBox;
		this.canvas = canvas;

		refreshShipsInfoLayer(canvas, tileBox);
	}

	public static void cleartapships() {
		
		tap_ships.clear();
		alltap_ships.clear();
		tap_shipsPoint.clear();
		searchshipsBeans.clear();
		allsearchshipsBeans.clear();
		
		ShipLableLayer.teamlable = true;
//		view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
	}

	// 触发点击
	@Override
	public boolean onSingleTap(PointF point, RotatedTileBox rtileBox) {
		try {
			if (rtileBox.getZoom() > TYPHOON_SHOW) {
				List<TyphoonInfoBean> typhooninfoObjects = new ArrayList<TyphoonInfoBean>();
				gettyphoonInfo(rtileBox, point, typhooninfoObjects);
				gettyphoonForeInfo(rtileBox, point, typhooninfoObjects);
				for (TyphoonInfoBean fav : typhooninfoObjects) {
					Double la = fav.getLa();
					Double lo = fav.getLo();

					AnimateDraggingMapThread thread = view
							.getAnimatedDraggingThread();
					// clearLayer();
					thread.stopAnimating();
					thread.startMoving(la, lo, rtileBox.getZoom(), false);

					int locationX = tileBox.getCenterPixelX();//
					int locationY = tileBox.getCenterPixelY();//

					showPopupWindow(view, locationX, locationY, fav);
					return true;
				}
			
			}
			
			if (rtileBox.getZoom() < START_SHOW) {
				// System.out.println("点击触发");
				if (rtileBox.getZoom() > 5) {
					List<WeatherInfoBean> weatherinfoObjects = new ArrayList<WeatherInfoBean>();
					getweatherInfo(rtileBox, point, weatherinfoObjects);
					for (WeatherInfoBean fav : weatherinfoObjects) {
						Double la = fav.getLat();
						Double lo = fav.getLon();

						AnimateDraggingMapThread thread = view
								.getAnimatedDraggingThread();
						// clearLayer();
						thread.startMoving(la, lo, rtileBox.getZoom(), false);

						int locationX = tileBox.getCenterPixelX();//
						int locationY = tileBox.getCenterPixelY();//

						showPopupWindow(view, locationX, locationY, fav);
						return true;
					}
				}
				List<ShipsBean> infoObjects = new ArrayList<ShipsBean>();
				getShipsInfo2(rtileBox, point, infoObjects);
				if (infoObjects != null) {
					for (ShipsBean fav : infoObjects) {
						Double la = fav.getLa();
						Double lo = fav.getLo();
						AnimateDraggingMapThread thread = view
								.getAnimatedDraggingThread();
						// clearLayer();
						thread.startMoving(la, lo, rtileBox.getZoom(), false);

						int locationX = tileBox.getCenterPixelX();//
						int locationY = tileBox.getCenterPixelY();// fav);
						if (teamship.size() > 0) {
							for (int i1 = 0; i1 < teamship.size(); i1++) {
								boolean have = false;
								for (int i = 0; i < allteamship.size(); i++) {

									if (teamship.get(i1).m.equals(allteamship
											.get(i).m)) {
										allteamship.remove(i);
										allteamship.add(teamship.get(i1));
										have = true;
										break;
									}
								}
								if (!have) {
									allteamship.add(teamship.get(i1));
								}
							}
							teamship.clear();
						}
						if (searchshipsBeans.size() > 0) {
							for (int i1 = 0; i1 < searchshipsBeans.size(); i1++) {
								boolean have = false;
								for (int i = 0; i < allsearchshipsBeans.size(); i++) {

									if (searchshipsBeans.get(i1).m
											.equals(allsearchshipsBeans.get(i).m)) {
										allsearchshipsBeans.remove(i);
										allsearchshipsBeans
												.add(searchshipsBeans.get(i1));
										have = true;
										break;
									}
								}
								if (!have) {
									allsearchshipsBeans.add(searchshipsBeans
											.get(i1));
								}
							}
							searchshipsBeans.clear();
						}
						if (tap_ships.size() >= 1) {
							int have = 0;
							for (int at = 0; at < alltap_ships.size(); at++) {
								if (alltap_ships.get(at).m.equals(tap_ships
										.get(0).m)) {
									alltap_ships.remove(at);
									alltap_ships.add(tap_ships.get(0));
									tap_ships.clear();
									have++;
									break;
								}
							}
							if (have == 0) {
								alltap_ships.add(tap_ships.get(0));
								tap_ships.clear();
							}
						}
						tap_ships.add(fav);
						if (tap_shipsPoint.size() > 0) {
							if (!tap_shipsPoint.get(0).getM()
									.equals(fav.getM())) {
								tap_shipsPoint.clear();
								tap_shipsPoint.add(fav);// 记录轨迹点
							}
						} else if (tap_shipsPoint.size() == 0) {
							tap_shipsPoint.clear();
							tap_shipsPoint.add(fav);// 记录轨迹点
						}
						showPopupWindow(view, locationX, locationY, fav);
					}
					return true;
				}
			} else {
				List<ShipsBean> infoObjects = new ArrayList<ShipsBean>();
				getShipsInfo(rtileBox, point, infoObjects);
				if (!infoObjects.isEmpty()) {
					for (ShipsBean fav : infoObjects) {
						Double la = Double.parseDouble(fav.la);
						Double lo = Double.parseDouble(fav.lo);
						AnimateDraggingMapThread thread = view
								.getAnimatedDraggingThread();
						thread.startMoving(la, lo, rtileBox.getZoom(), false);
						int locationX = tileBox.getCenterPixelX();// tileBox.getPixXFromLonNoRot(lo);
						int locationY = tileBox.getCenterPixelY();//
						// tileBox.getPixYFromLatNoRot(la);
						if (teamship.size() > 0) {
							for (int i1 = 0; i1 < teamship.size(); i1++) {
								boolean have = false;
								for (int i = 0; i < allteamship.size(); i++) {

									if (teamship.get(i1).m.equals(allteamship
											.get(i).m)) {
										allteamship.remove(i);
										allteamship.add(teamship.get(i1));
										have = true;
										break;
									}
								}
								if (!have) {
									allteamship.add(teamship.get(i1));
								}
							}
							teamship.clear();
						}
						System.err.println("teamship getShipsInfo");
						if (searchshipsBeans.size() > 0) {
							for (int i1 = 0; i1 < searchshipsBeans.size(); i1++) {
								boolean have = false;
								for (int i = 0; i < allsearchshipsBeans.size(); i++) {

									if (searchshipsBeans.get(i1).m
											.equals(allsearchshipsBeans.get(i).m)) {
										allsearchshipsBeans.remove(i);
										allsearchshipsBeans
												.add(searchshipsBeans.get(i1));
										have = true;
										break;
									}
								}
								if (!have) {
									allsearchshipsBeans.add(searchshipsBeans
											.get(i1));
								}
							}
							searchshipsBeans.clear();
						}
						teamship.clear();// ？？？
						if (tap_ships.size() >= 1) {
							int have = 0;
							for (int at = 0; at < alltap_ships.size(); at++) {
								if (alltap_ships.get(at).m.equals(tap_ships
										.get(0).m)) {
									alltap_ships.remove(at);
									alltap_ships.add(tap_ships.get(0));
									tap_ships.clear();
									have++;
									break;
								}
							}
							if (have == 0) {
								alltap_ships.add(tap_ships.get(0));
								tap_ships.clear();
							}
						}
						tap_ships.add(fav);
						if (tap_shipsPoint.size() > 0) {
							if (!tap_shipsPoint.get(0).getM()
									.equals(fav.getM())) {
								tap_shipsPoint.clear();
								tap_shipsPoint.add(fav);// 记录轨迹点
							}
						} else if (tap_shipsPoint.size() == 0) {
							// tap_shipsPoint.clear();
							tap_shipsPoint.add(fav);// 记录轨迹点
						}
						showPopupWindow(view, locationX, locationY, fav);
					}
					return true;
				}
			}
		} catch (Exception e) {
			System.err.println("shipslayer singletap exception" + e);
		}
		return true;

	}

	private HashMap<String, LoadingShipsXMLThread> asyntaskmap = new HashMap<String, LoadingShipsXMLThread>();
	private HashMap<String, LoadingWeatherXMLThread> weathertaskmap = new HashMap<String, LoadingWeatherXMLThread>();
	private HashMap<String, MyTeamShipsThread> teamntaskmap = new HashMap<String, MyTeamShipsThread>();
	private HashMap<String, LoadingHighlightedShipsXMLThread> hightaskmap = new HashMap<String, LoadingHighlightedShipsXMLThread>();
//	private boolean settfore=false;

	public static boolean clearship = false;

	// public static boolean all=true;

	private void clearMapByUUID(String uuid) {
		asyntaskmap.remove(uuid);
	}

	private void clearWeatherMapByUUID(String uuid) {
		weathertaskmap.remove(uuid);
	}

	private void closeReqest() {
		if (asyntaskmap.isEmpty())
			return;
		Iterator<String> it = asyntaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			LoadingShipsXMLThread task = asyntaskmap.get(uuid);
			task.cancel(true);
		}
	}

	private void closeReqest1() {
		if (teamntaskmap.isEmpty())
			return;
		Iterator<String> it = teamntaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			MyTeamShipsThread task = teamntaskmap.get(uuid);
			task.cancel(true);
		}
	}

	private void closehighReqest() {
		if (hightaskmap.isEmpty())
			return;
		Iterator<String> it = hightaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			LoadingHighlightedShipsXMLThread task = hightaskmap.get(uuid);
			task.cancel(true);
		}
	}

	private void closeWeatherReqest() {
		if (weathertaskmap.isEmpty())
			return;
		Iterator<String> it = weathertaskmap.keySet().iterator();
		while (it.hasNext()) {
			String uuid = it.next();
			LoadingWeatherXMLThread task = weathertaskmap.get(uuid);
			task.cancel(true);
		}
	}

	private void callShipInfoAction(RotatedTileBox viewportToDraw, String s) {
		closeReqest();
		String uuid = UUID.randomUUID().toString();
		LoadingShipsXMLThread task = new LoadingShipsXMLThread(uuid,
				viewportToDraw, canvas, s);
		asyntaskmap.put(uuid, task);
		// print("" + uuid + " 启动");
		execute(task);
	}

	private void callHighLightedShipInfoAction(RotatedTileBox viewportToDraw,
			String s) {
		closehighReqest();
		String uuid = UUID.randomUUID().toString();
		LoadingHighlightedShipsXMLThread task = new LoadingHighlightedShipsXMLThread(
				uuid, viewportToDraw, canvas, s);
		hightaskmap.put(uuid, task);
		// print("" + uuid + " 启动");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		} else {
			task.execute(new Void[0]);
		}
	}

	private void callWeatherInfoAction(RotatedTileBox viewportToDraw, String s) {
		closeWeatherReqest();
		String uuid = UUID.randomUUID().toString();
		LoadingWeatherXMLThread task = new LoadingWeatherXMLThread(uuid,
				viewportToDraw, canvas, s);
		weathertaskmap.put(uuid, task);
		// print("" + uuid + " 启动");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		} else {
			task.execute(new Void[0]);
		}
	}

	private void callTyphoonAction() {
		LoadingTyphoonXMLThread task = new LoadingTyphoonXMLThread();
		// weathertaskmap.put(uuid, task);
		// print("" + uuid + " 启动");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		} else {
			task.execute(new Void[0]);
		}
	}

	// 判断android版本
	private void execute(LoadingShipsXMLThread task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		} else {
			task.execute(new Void[0]);
		}
	}

	// 解析xml
	private void parseXMLnew(InputStream inStream, String s) throws Exception {
//		 System.out.println("all ships 解析xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		// System.out.println(document.toString());
		_ships.clear();
		// print("解析时ships"+_ships.size());

		// 判断是否超时
		if (root.getNodeName().compareTo("session_timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			return;
		}

		// TODO 更新缓存部分
		// 船舶数据解析并加入_ships
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					_ships.add(XmlParseUtility.parse(childElement,
							ShipsBean.class));
				}
			}
		}
		 System.out.println("_ships.size====" + _ships.size());
		// for(ShipsBean s:_ships){
		// System.out.println("_ships mmsi" +s.m);
		// }

//		for (HeartBeatBean h : heartBeatBean) {
//			// System.out.println("uuuuuuuuuuuu=" + h.message);
//		}

		List<ShipsBean> myShips = new ArrayList<ShipsBean>();

		// System.out.println("_ships 长度�? "+_ships.size());

		myShips.addAll(_ships);
		_ships.clear();
		deleAllCurrentWindowCellShips();

		for (ShipsBean ship : myShips) {
			// System.out.println("MMSI: " + ship.m);
			// System.out.println("航向:" + "======" + ship.co);

			for (int t = 0; t < tap_ships.size(); t++) {

				if (tap_ships.get(t).getM().equals(ship.getM())) {

					if(tap_ships.get(t).getSatti()!=null&&!tap_ships.get(t).getSatti().equals("")){
						ship = tap_ships.get(t);
						break;
					}

					if (ship.getDateupdatetime().getTime() > tap_ships.get(t)
							.getDateupdatetime().getTime()) {
						tap_ships.set(t, ship);
						if (tap_shipsPoint.size() <= 0) {
							tap_shipsPoint.add(ship);
							print("tap_shipsPoint 0 allship update size add"
									+ tap_shipsPoint.size());
						} else if (!tap_shipsPoint
								.get(tap_shipsPoint.size() - 1).ti
								.equals(ship.ti)) {
							tap_shipsPoint.add(ship);
							print("tap_shipsPoint allship update size add"
									+ tap_shipsPoint.size());
						} else {
							print("tap_shipsPoint 时间相同未添加"
									+ tap_shipsPoint.size());
						}

						if (tap_shipsPoint.size() > 20) {
							tap_shipsPoint.remove(0);
						}

					} else {
						// System.err.println("highlight update this tapship width::"+tap_ships.get(t).b+" L::"+tap_ships.get(t).l);
						// System.err.println("updatetime 不对   "
						// + ship.getDateupdatetime());
						ship = tap_ships.get(t);

					}
				}
			}
			for (int t = 0; t < alltap_ships.size(); t++) {

				if (alltap_ships.get(t).getM().equals(ship.getM())) {

					if(alltap_ships.get(t).getSatti()!=null&&!alltap_ships.get(t).getSatti().equals("")){
						ship = alltap_ships.get(t);
						break;
					}

					if (ship.getDateupdatetime().getTime() > alltap_ships
							.get(t).getDateupdatetime().getTime()) {
						alltap_ships.set(t, ship);
					} else {
						ship = alltap_ships.get(t);
					}
				}
			}
			for (int t = 0; t < searchshipsBeans.size(); t++) {

				if (searchshipsBeans.get(t).getM().equals(ship.getM())) {
					if(searchshipsBeans.get(t).getSatti()!=null&&!searchshipsBeans.get(t).getSatti().equals("")){
						ship = searchshipsBeans.get(t);
						break;
					}

					if (ship.getDateupdatetime().getTime() > searchshipsBeans
							.get(t).getDateupdatetime().getTime()) {
						searchshipsBeans.set(t, ship);
						if (tap_shipsPoint.size() <= 0) {
							tap_shipsPoint.add(ship);
							print("tap_shipsPoint 0 allship update size add"
									+ tap_shipsPoint.size());
						} else if (!tap_shipsPoint
								.get(tap_shipsPoint.size() - 1).ti
								.equals(ship.ti)) {
							tap_shipsPoint.add(ship);
							print("tap_shipsPoint allship update size add"
									+ tap_shipsPoint.size());
						} else {
							print("tap_shipsPoint 时间相同未添加"
									+ tap_shipsPoint.size());
						}

						if (tap_shipsPoint.size() > 20) {
							tap_shipsPoint.remove(0);
						}
					} else {
						ship = searchshipsBeans.get(t);
					}
				}
			}
			for (int t = 0; t < allsearchshipsBeans.size(); t++) {

				if (allsearchshipsBeans.get(t).getM().equals(ship.getM())) {
					if(allsearchshipsBeans.get(t).getSatti()!=null&&!allsearchshipsBeans.get(t).getSatti().equals("")){
						ship = allsearchshipsBeans.get(t);
						break;
					}

					if (ship.getDateupdatetime().getTime() > allsearchshipsBeans
							.get(t).getDateupdatetime().getTime()) {
						allsearchshipsBeans.set(t, ship);
					} else {
						ship = allsearchshipsBeans.get(t);
					}
				}
			}
			if (tileBox.getZoom() > START_SHOW) {
				// for (int t = 0; t <
				// MyTeamShipsThread.currentshipsBeans.size(); t++) {
				if (MyTeamShipsThread.currentshipsBeans
						.containsKey(ship.getM())) {
					if (ship.getDateupdatetime().getTime() > MyTeamShipsThread.currentshipsBeans
							.get(ship.getM()).getDateupdatetime().getTime()) {
						MyTeamShipsThread.currentshipsBeans.put(ship.getM(),
								ship);

					} else {
						ship = MyTeamShipsThread.currentshipsBeans.get(ship
								.getM());
					}
				}
				// }

			}// 选中船舶取最新值。

			Cell c = getCellIndex(ship.getLo(), ship.getLa());// null

			if (_shipCellMap.containsKey(ship.getM())) {
				// 如果shipcellmap中包括该船舶的mmsi，先删除在该cell中船舶队列中该船舶的信息
				Cell ccell = _cellHashMap.get(_shipCellMap.get(ship.getM()));
				ccell.mmsiHashMapShipInfo().remove(ship.getM());
			}

			if (!_cellHashMap.containsKey(c.get_id())) {
				// 如果cellmap队列中没有这个cell，那么要把这个cell添加到cellmap进去�?
				// 同时把该船舶也添加到这个cell中船舶队列中去�??
				c.mmsiHashMapShipInfo().put(ship.getM(), ship);
				_cellHashMap.put(c.get_id(), c);
			} else {
				// cellmap中有这个cell了，那么要更新这个cell中船舶队列中该船舶的信息�?
				_cellHashMap.get(c.get_id()).mmsiHashMapShipInfo()
						.put(ship.getM(), ship);
			}

			// 更新ship-cell 索引
			_shipCellMap.put(ship.getM(), c.get_id());
		}
		MyTeamShipsThread.deleAllCurrentWindowCellShips(tileBox);
		MyTeamShipsThread.updateCellships(MyTeamShipsThread.currentshipsBeans);
	}

	private void parseHships(InputStream inStream, String s) throws Exception {
		// System.out.println("解析xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		// System.out.println(document.toString());
		highlightedships.clear();
		// print("解析时ships"+_ships.size());

		// 判断是否超时
		if (root.getNodeName().compareTo("session_timeout") == 0) {
			heartBeatBean.add(XmlParseUtility.parse(root, HeartBeatBean.class));
			return;
		}

		// TODO 更新缓存部分
		// 船舶数据解析并加入_ships
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				if (childElement.getNodeName().compareTo("ship") == 0) {
					highlightedships.add(XmlParseUtility.parse(childElement,
							ShipsBean.class));
				}
			}
		}
		// System.out.println("highlightedships.size====" +
		// highlightedships.size());
		// for(ShipsBean s:_ships){
		// System.out.println("_ships mmsi" +s.m);
		// }

		for (HeartBeatBean h : heartBeatBean) {
			// System.out.println("uuuuuuuuuuuu=" + h.message);
		}
		for (int i = 0; i < highlightedships.size(); i++) {
			for (int t = 0; t < tap_ships.size(); t++) {
				if (tap_ships.get(t).getM()
						.equals(highlightedships.get(i).getM())) {

					if (highlightedships.get(i).getDateupdatetime().getTime() > tap_ships
							.get(t).getDateupdatetime().getTime()) {
						tap_ships.set(t, highlightedships.get(i));
						// System.err.println("highlight update this tapship width::"+tap_ships.get(t).b+" L::"+tap_ships.get(t).l);
						if (tap_shipsPoint.size() <= 0) {
							tap_shipsPoint.add(highlightedships.get(i));
							print("tap_shipsPoint 0 size add"
									+ tap_shipsPoint.size());
						} else if (!tap_shipsPoint
								.get(tap_shipsPoint.size() - 1).ti
								.equals(highlightedships.get(i).ti)) {
							tap_shipsPoint.add(highlightedships.get(i));
							print("tap_shipsPoint add size"
									+ tap_shipsPoint.size());
						} else {
							print("tap_shipsPoint 时间相同未添加"
									+ tap_shipsPoint.size());
						}

						if (tap_shipsPoint.size() > 20) {
							tap_shipsPoint.remove(0);
						}
					} else {
						// System.err.println("high updatetime 不对   "
						// + "high ::"
						// + highlightedships.get(i).getDateupdatetime()
						// .getTime()
						// + " tap::"
						// + tap_ships.get(t).getDateupdatetime()
						// .getTime());
					}
				}
			}
			for (int t = 0; t < alltap_ships.size(); t++) {
				if (alltap_ships.get(t).getM()
						.equals(highlightedships.get(i).getM())) {
					if (highlightedships.get(i).getDateupdatetime().getTime() > alltap_ships
							.get(t).getDateupdatetime().getTime()) {
						alltap_ships.set(t, highlightedships.get(i));
					}
				}
			}
			for (int t = 0; t < searchshipsBeans.size(); t++) {
				if (searchshipsBeans.get(t).getM()
						.equals(highlightedships.get(i).getM())) {
					if (highlightedships.get(i).getDateupdatetime().getTime() > searchshipsBeans
							.get(t).getDateupdatetime().getTime()) {
						searchshipsBeans.set(t, highlightedships.get(i));
						if (tap_shipsPoint.size() <= 0) {
							tap_shipsPoint.add(highlightedships.get(i));
						} else if (!tap_shipsPoint
								.get(tap_shipsPoint.size() - 1).ti
								.equals(highlightedships.get(i).ti)) {
							tap_shipsPoint.add(highlightedships.get(i));
						} else {
							print("tap_shipsPoint 时间相同未添加"
									+ tap_shipsPoint.size());
						}

						if (tap_shipsPoint.size() > 20) {
							tap_shipsPoint.remove(0);
						}
					}
				}
			}
			for (int t = 0; t < allsearchshipsBeans.size(); t++) {
				if (allsearchshipsBeans.get(t).getM()
						.equals(highlightedships.get(i).getM())) {
					if (highlightedships.get(i).getDateupdatetime().getTime() > allsearchshipsBeans
							.get(t).getDateupdatetime().getTime()) {
						allsearchshipsBeans.set(t, highlightedships.get(i));
					}
				}
			}
			if (tileBox.getZoom() > START_SHOW) {
				// for (int t = 0; t <
				// MyTeamShipsThread.currentshipsBeans.size(); t++) {
				if (MyTeamShipsThread.currentshipsBeans
						.containsKey(highlightedships.get(i).getM())) {
					if (highlightedships.get(i).getDateupdatetime().getTime() > MyTeamShipsThread.currentshipsBeans
							.get(highlightedships.get(i).getM())
							.getDateupdatetime().getTime()) {
						MyTeamShipsThread.currentshipsBeans.put(
								highlightedships.get(i).getM(),
								highlightedships.get(i));

					}
				}
				// }

			}
		}// 更新高亮船舶。

		MyTeamShipsThread.deleAllCurrentWindowCellShips(tileBox);
		MyTeamShipsThread.updateCellships(MyTeamShipsThread.currentshipsBeans);
	}

	// 将当前窗口所有cells里面的船舶删�?
	private void deleAllCurrentWindowCellShips() {
		QuadRect rect = tileBox.getLatLonBounds();
		LatLon p3 = new LatLon(rect.top, rect.right);
		LatLon p1 = new LatLon(rect.bottom, rect.left);

		Cell blIndex = getCellIndex(p1.getLongitude(), p1.getLatitude());
		Cell trIndex = getCellIndex(p3.getLongitude(), p3.getLatitude());

		double rd = 0;

		for (double i = blIndex.get_x(); i <= trIndex.get_x() + rd; i++) {
			for (double j = blIndex.get_y(); j <= trIndex.get_y() + rd; j++) {
				String cellkey = "x" + i + "y" + j;
				if (_cellHashMap.containsKey(cellkey)) {
					(_cellHashMap.get(cellkey)).clearMMSIShipInfoMap();
				} else {
					continue;
				}
			}
		}
	}

	// 将属于当前窗口内的船舶加入到�?个显示队列中去�??
	private void addAllCellShips(HashMap<String, ShipsBean> showArray) {
		// System.out.println("调用 addAllCellShips");
		QuadRect rect = tileBox.getLatLonBounds();
		LatLon p3 = new LatLon(rect.top, rect.right);
		LatLon p1 = new LatLon(rect.bottom, rect.left);

		Cell blIndex = getCellIndex(p1.getLongitude(), p1.getLatitude());
		Cell trIndex = getCellIndex(p3.getLongitude(), p3.getLatitude());
		// System.out.println(blIndex.get_x()+" "+blIndex.get_y()+"----�?"+trIndex.get_x()+" "+trIndex.get_y()
		// +"lo"+p1.getLongitude()+" "+p3.getLongitude()+"la"+p1.getLatitude()+" "+p3.getLatitude());
		double rd = 0;

		int celling = 1000;

		// if (tileBox.getZoom() <= 11) {
		// celling = 4;
		// } else if (tileBox.getZoom() >= 12 && tileBox.getZoom() < 13) {
		// celling = 10;
		// } else if (tileBox.getZoom() >= 13) {
		// celling = 100;
		// }

		// int cellcount=0;
		// print("_ships2Draw准备添加"+_ships2Draw.size());
		// _ships2Draw.clear();
		for (double i = blIndex.get_x(); i <= trIndex.get_x() + rd; i++) {
			for (double j = blIndex.get_y(); j <= trIndex.get_y() + rd; j++) {
				String cellkey = "x" + i + "y" + j;
				// cellcount++;
				if (_cellHashMap.containsKey(cellkey)) {
					// print("cell添加船舶"+cellkey);
					addCellShipsLimited(_cellHashMap.get(cellkey), celling,
							showArray);

				}
			}
			// System.out.println("第一层for结束�?");
		}
		// print("cell数目："+cellcount);

	}

	private void addCellShipsLimited(Cell cell, int celling,
			HashMap<String, ShipsBean> showArray) {

		HashMap<String, ShipsBean> iMMSIHashMap = cell.mmsiHashMapShipInfo();
		// HashMap<String, ShipsBean> teamiMMSIHashMap =
		// cell.mmsiTeamShipInfo();

		if (iMMSIHashMap != null) {

			try {
				int ceilingInner = celling > iMMSIHashMap.size() ? iMMSIHashMap
						.keySet().toArray().length : celling;

				// print("ceilingInner: "+ceilingInner);

				String keysarray[] = (String[]) iMMSIHashMap.keySet().toArray(
						new String[0]); //
				// print("cell  contains"+keysarray.length);
				// String keysTarray[] = (String[])
				// iMMSIHashMap.keySet().toArray(
				// new String[0]);
				for (int i = 0; i < ceilingInner; i++) {
					String key = keysarray[i];
					// String Tkey = keysTarray[i];
					// if(keysTarray.length>1){
					// if(iMMSIHashMap.containsKey(Tkey)){
					// ShipsBean ship = iMMSIHashMap.get(Tkey);
					// Cell c=getCellIndex(ship.getLo(),ship.getLa());
					// System.out.println(key+"tkey 添加到了显示队列！： "+"length"+keysTarray.length+ship.getM()+"la"+ship.getLa()+"lo"+ship.getLo()
					// +"cx"+c.get_x()+"cy"+c.get_y());
					// _ships2Draw.add(ship);
					// }else{
					// ShipsBean ship = teamiMMSIHashMap.get(Tkey);
					// Cell c=getCellIndex(ship.getLo(),ship.getLa());
					// System.out.println(key+"tkey team添加到了显示队列！： "+ship.getM()+"la"+ship.getLa()+"lo"+ship.getLo()
					// +"cx"+c.get_x()+"cy"+c.get_y());
					// _ships2Draw.add(ship);
					// }
					// }else{
					ShipsBean ship = iMMSIHashMap.get(key);
					if (ship != null && !ship.equals(null)) {
						// Cell c = getCellIndex(ship.getLo(),
						// ship.getLa());//貌似没啥用处。
						// System.out.println(key+"添加到了显示队列！： "+ship.getM()+"la"+ship.getLa()+"lo"+ship.getLo()
						// +"cx"+c.get_x()+"cy"+c.get_y());
						showArray.put(ship.getM(), ship);
					}
					// }// 加入过期的船队船舶
				}
			} catch (java.util.ConcurrentModificationException ex) {
				System.out.println("ConcurrentModificationException shipsinfo");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		// System.out.println("待显示队列： "+_ships2Draw.size());
	}

	public static void addlableInCurrentWindow(String s) {
		caddedlable = new ArrayList<lableBean>();
		// double minla =
		// tileBox.getRightBottomLatLon().getLatitude();
		// double maxlo =
		// tileBox.getRightBottomLatLon().getLongitude();
		// double maxla = tileBox.getLeftTopLatLon().getLatitude();
		// double minlo = tileBox.getLeftTopLatLon().getLongitude();
		//
		// for (int i = 0; i < ShipLableLayer.countclable.size(); i++) {
		//
		// double sla = ShipLableLayer.countclable.get(i).lat;
		// double slo = ShipLableLayer.countclable.get(i).lon;
		//
		//
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// caddedlable.add( ShipLableLayer.countclable.get(i));
		// }
		// }//计算当前屏幕内的 lable 未使用暂时注销掉
		caddedlable.addAll(ShipLableLayer.countclable);
		// print("执行了addlableInCurrentWindow  传入的列表长"
		// + ShipLableLayer.countclable.size() + " hehe"
		// + caddedlable.size());

	}

	public static void addFleetVesselsInCurrentWindow(
			HashMap<String, ShipsBean> showArray, boolean teamfresh) {// 第二个参数暂时取消没用。
		HashMap<String, ShipsBean> currentshipsBeans = new HashMap<String, ShipsBean>();// 计算的当前船舶。
		// MyTeamShipsThread.currentshipsBeans.clear();
		if (tileBox.getZoom() > 6) {
			MyTeamShipsThread.addAllCellShips();
			// MyTeamShipsThread.addLableCellShips();
			// print("addFleetVesselsInCurrentWindow MyTeamShipsThread.currentshipsBeans"+MyTeamShipsThread.currentshipsBeans.size());
			currentshipsBeans.putAll(MyTeamShipsThread.currentshipsBeans);
		}
		if (currentshipsBeans.size() > 0 || tileBox.getZoom() >= START_SHOW) {
			Iterator<Entry<String, ShipsBean>> iter = currentshipsBeans
					.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = entry.getKey().toString();
				ShipsBean value = currentshipsBeans.get(key);

				if (!showArray.containsKey(currentshipsBeans.get(key).getM())) {
					showArray.put(currentshipsBeans.get(key).getM(), value);
				}
				// }
			}// 加入当前船队的船。

			// showArray.addAll(currentshipsBeans);
			// print("计算当前屏幕的船队船舶" + currentshipsBeans.size() + "showarray"
			// + showArray.size());
			for (int s = 0; s < searchshipsBeans.size(); s++) {

				if (!showArray.containsKey(searchshipsBeans.get(s).getM())) {
					showArray.put(searchshipsBeans.get(s).getM(),
							searchshipsBeans.get(s));
				}
				// }
			}
			// showArray.addAll(searchshipsBeans);// 搜索到的船舶
			for (int s = 0; s < allsearchshipsBeans.size(); s++) {

				if (!showArray.containsKey(allsearchshipsBeans.get(s).getM())) {
					showArray.put(allsearchshipsBeans.get(s).getM(),
							allsearchshipsBeans.get(s));
				}
				// }
			}

			for (int i = 0; i < alltap_ships.size(); i++) {

				if (!showArray.containsKey(alltap_ships.get(i).getM())) {
					showArray.put(alltap_ships.get(i).getM(),
							alltap_ships.get(i));
				}
				// print("增加所有点击船舶" + showArray.size());
				// }
			}
			if (tap_ships.size() >= 1) {

				if (!showArray.containsKey(tap_ships.get(0).getM())) {
					showArray.put(tap_ships.get(0).getM(), tap_ships.get(0));
				}
				// print("增加点击船舶" + showArray.size());
				// }
			}

			// 计算当前屏幕 比例尺较小情况下运算缓慢。 直接计算当前屏幕内船舶比较方便。
		} else if (tileBox.getZoom() < START_SHOW
				&& MyTeamShipsThread.shipsBeans != null) {

			double minla = tileBox.getRightBottomLatLon().getLatitude();
			double maxlo = tileBox.getRightBottomLatLon().getLongitude();
			double maxla = tileBox.getLeftTopLatLon().getLatitude();
			double minlo = tileBox.getLeftTopLatLon().getLongitude();

			Iterator iter = MyTeamShipsThread.shipsBeans.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = entry.getKey().toString();
				ShipsBean value = MyTeamShipsThread.shipsBeans.get(key);

				double sla = Double.valueOf(MyTeamShipsThread.shipsBeans
						.get(key).la);
				double slo = Double.valueOf(MyTeamShipsThread.shipsBeans
						.get(key).lo);

				if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {

					if (!showArray.containsKey(key)) {
						// showArray.add(MyTeamShipsThread.shipsBeans.get(i));
						showArray.put(key, value);
					}
					// }// 显示加入船队的船
					// showArray.add(MyTeamShipsThread.shipsBeans.get(i));
					MyTeamShipsThread.currentshipsBeans.put(key, value);
				}
			}
			for (int s = 0; s < currentshipsBeans.size(); s++) {
				if (!showArray.containsKey(currentshipsBeans.get(s).getM())) {
					showArray.put(currentshipsBeans.get(s).getM(),
							currentshipsBeans.get(s));
				}
				// }
			}// 加入当前船队的船。

			// showArray.addAll(currentshipsBeans);
//			print("计算当前屏幕的船队船舶currentshipsBeans==0" + currentshipsBeans.size()
//					+ "showarray" + showArray.size()
//					+ "MyTeamShipsThread.shipsBeans"
//					+ MyTeamShipsThread.shipsBeans.size());
			for (int s = 0; s < searchshipsBeans.size(); s++) {

				if (!showArray.containsKey(searchshipsBeans.get(s).getM())) {
					showArray.put(searchshipsBeans.get(s).getM(),
							searchshipsBeans.get(s));
				}
				// }
			}
			// showArray.addAll(searchshipsBeans);// 搜索到的船舶
			for (int s = 0; s < allsearchshipsBeans.size(); s++) {

				if (!showArray.containsKey(allsearchshipsBeans.get(s).getM())) {
					showArray.put(allsearchshipsBeans.get(s).getM(),
							allsearchshipsBeans.get(s));
				}
				// }
			}

			for (int i = 0; i < alltap_ships.size(); i++) {

				if (!showArray.containsKey(alltap_ships.get(i).getM())) {
					showArray.put(alltap_ships.get(i).getM(),
							alltap_ships.get(i));
				}
				// print("增加所有点击船舶" + showArray.size());
				// }
			}
			if (tap_ships.size() >= 1) {

				if (!showArray.containsKey(tap_ships.get(0).getM())) {
					showArray.put(tap_ships.get(0).getM(), tap_ships.get(0));
				}
				// print("增加点击船舶" + showArray.size());
				// }
			}

		}
		// print("退出   addfleet")；//存在问题！！！！
	}

	private void initShipsShowarray(HashMap<String, ShipsBean> showArray) {
		if (tileBox.getZoom() >= START_SHOW) {
			// 如果缩放比例尺到了该显示其他船舶的层级，那就把当前窗口范围内的船舶加入到待显示队列中。
			// _ships2Draw = new ArrayList<ShipsBean>();
			if(showallships){
				addAllCellShips(showArray);
			}
			
			addFleetVesselsInCurrentWindow(showArray, false);// 加入船队的船，和搜索到的船舶。

			if (allshipsrefresh) {
				allshipsrefresh = false;
				if (tileBox.getZoom() >= 14) {
					if (showArray.size() > 0) {
						// all=false;
						teamCallLable(showArray);
					}
				}

			}
		} else {
			if (MyTeamShipsThread.shipsBeans != null && !app.isIslogin()) {
				MyTeamShipsThread.shipsBeans.clear();
				MyTeamShipsThread.deleAllCurrentCellShips();
			}
			if (app.isIslogin()) {
				addFleetVesselsInCurrentWindow(showArray, false);
			} else {
				caddedlable.clear();
				for (int s = 0; s < searchshipsBeans.size(); s++) {

					if (!showArray.containsKey(searchshipsBeans.get(s).getM())) {
						showArray.put(searchshipsBeans.get(s).getM(),
								searchshipsBeans.get(s));
					}
					// }
				}
				// showArray.addAll(searchshipsBeans);// 搜索到的船舶
				for (int s = 0; s < allsearchshipsBeans.size(); s++) {

					if (!showArray.containsKey(allsearchshipsBeans.get(s)
							.getM())) {
						showArray.put(allsearchshipsBeans.get(s).getM(),
								allsearchshipsBeans.get(s));
					}
					// }
				}

				for (int i = 0; i < alltap_ships.size(); i++) {

					if (!showArray.containsKey(alltap_ships.get(i).getM())) {
						showArray.put(alltap_ships.get(i).getM(),
								alltap_ships.get(i));
					}
					// print("增加所有点击船舶" + showArray.size());
					// }
				}
				if (tap_ships.size() >= 1) {

					if (!showArray.containsKey(tap_ships.get(0).getM())) {
						showArray
								.put(tap_ships.get(0).getM(), tap_ships.get(0));
					}
					// print("增加点击船舶" + showArray.size());
					// }
				}

			}
			// print("刷新船舶" + searchshipsBeans.size()+"zoom"+tileBox.getZoom());
			// showArray.addAll(MyTeamShipsThread.currentshipsBeans);
			// print("船舶加载完毕" + showArray.size());
		}
	}// 加入当前的船舶。

	// public static void clearLayer() {
	// clearship = true;
	// view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
	// }

	private Path drawShipOwnPoint(Canvas canvas, RotatedTileBox tileBox) {
		Path path = new Path();
		for (int o = 0; o < tap_shipsPoint.size(); o++) {
			ShipsBean obj = tap_shipsPoint.get(o);
			// for (ShipsBean obj : tap_shipsPoint) {
			// print("zhixingyici tap_shipsPoint"+tap_shipsPoint.size());
			if (obj == null) {
				return path;
			}
			// print("tap_shipsPoint la"+obj.getLa()+" lo:"+obj.getLo()+
			// "tap_ships la"+tap_ships.get(0).getLa()+"tap_ships lo"+tap_ships.get(0).getLo()
			// +"size::"+tap_shipsPoint.size());
			// if(obj.getLa()==tap_ships.get(0).getLa()
			// &&obj.getLo()==tap_ships.get(0).getLo()){
			// continue;
			// }

			int locationX = tileBox.getPixXFromLonNoRot(obj.getLo());
			int locationY = tileBox.getPixYFromLatNoRot(obj.getLa());
			if (o == 0) {
				path.moveTo(locationX, locationY);
			} else {
				path.lineTo(locationX, locationY);
			}
			if (tap_ships.size() > 0) {
				if (obj.getLa() == tap_ships.get(0).getLa()
						&& obj.getLo() == tap_ships.get(0).getLo()) {
					continue;
				}
			} else if (searchshipsBeans.size() > 0) {
				if (obj.getLa() == searchshipsBeans.get(0).getLa()
						&& obj.getLo() == searchshipsBeans.get(0).getLo()) {
					continue;
				}
			}
			try {
				canvas.save();
				canvas.drawCircle(locationX, locationY, 5, locationPaint);
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally{
				canvas.restore();
			}

		}
		return path;
	}// 船舶尾迹

	private void drawShipLable(Canvas canvas, RotatedTileBox tileBox) {
		// System.err.println("drawShipLabledrawShipLable"+caddedlable.size());
		for (int q = 0; q < caddedlable.size(); q++) {
			// int lx = addedlable1.get(q).x;
			// int ly = addedlable1.get(q).y;
			int wt = caddedlable.get(q).width;
			int ht = caddedlable.get(q).heigth;
			// int x = addedlable1.get(q).shipx;
			// int y = addedlable1.get(q).shipy;
			String n = caddedlable.get(q).n;

			int cx = caddedlable.get(q).x;
			int cy = caddedlable.get(q).y;
			int x = tileBox.getPixXFromLonNoRot(caddedlable.get(q).lon);
			int y = tileBox.getPixYFromLatNoRot(caddedlable.get(q).lat);
			int lx = x + cx - ((wt + dip2px(dm.density, 2)) / 2);
			int ly = y + cy;
			// print("执行lable 刷新 " + lx+":::"+ly+"x"+x+"y"+y);

			Rect rect = new Rect(lx, ly, lx + wt, ly + ht
					+ dip2px(dm.density, 4));
			Rect rect2 = new Rect(lx - dip2px(dm.density, 1), ly
					- dip2px(dm.density, 1), lx + wt + dip2px(dm.density, 1),
					ly + ht + dip2px(dm.density, 4) + dip2px(dm.density, 1));
			canvas.save();
			if (cx == 0) {
				if (cy < 0) {
					canvas.drawLine(x, y, lx + wt / 2,
							ly + ht + dip2px(dm.density, 4), paint1);
				} else {
					canvas.drawLine(x, y, lx + wt / 2, ly, paint1);
				}

			} else if (cx > 0) {
				if (cy < 0) {
					canvas.drawLine(x, y, lx + wt / 2,
							ly + ht + dip2px(dm.density, 4), paint1);
				} else if (cy == 0) {
					canvas.drawLine(x, y, lx + wt / 2,
							ly + (ht + dip2px(dm.density, 4)) / 2, paint1);
				} else if (cy > 0) {
					canvas.drawLine(x, y, lx + wt / 2, ly, paint1);
				}
			} else if (cx < 0) {
				if (cy < 0) {
					canvas.drawLine(x, y, lx + wt / 2,
							ly + ht + dip2px(dm.density, 4), paint1);
				} else if (cy == 0) {
					canvas.drawLine(x, y, lx + wt / 2,
							ly + (ht + dip2px(dm.density, 4)) / 2, paint1);
				} else if (cy > 0) {
					canvas.drawLine(x, y, lx + wt / 2, ly, paint1);
				}
			}
			canvas.drawRect(rect, paint);
			canvas.drawRect(rect2, paint2);
			paint1.setTextSize(dip2px(dm.density, 14));
			canvas.drawText(n, lx + 4 * dm.density, ly + ht + 1 * dm.density,
					paint1);
			canvas.restore();
		}// 暂时在这 画出船名
	}// 中间层的船名显示。

	public void showSearchships() {
		int z=12;
		if(tileBox!=null&&tileBox.getZoom()>12){
			z=tileBox.getZoom();
		}
		if (TeamShipsListAdapter.isMove && teamship != null
				&& teamship.size() >= 1) {
			// print("进入   moveteam");
			// TeamShipsListAdapter.isMove = false;
			// view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
			// if(teamship.size()>0){
			String m = teamship.get(0).getM();
			if (MyTeamShipsThread.shipsBeans.containsKey(m)) {
				teamship.remove(0);
				teamship.add(MyTeamShipsThread.shipsBeans.get(m));
			} else {
				MyTeamShipsThread.shipsBeans.put(teamship.get(0).getM(),
						teamship.get(0));
			}
			// 列表查看的船队的船 可能在shipsBeans存在也可能不存在。 存在的使用allfleetships替换
			Double la = null, lo = null;
			la = Double.parseDouble(teamship.get(0).la);
			lo = Double.parseDouble(teamship.get(0).lo);
			thread.startMoving(la, lo, z, false);
			// view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
			int locationX1 = tileBox.getCenterPixelX();// tileBox.getPixXFromLonNoRot(fav.getLo());
			int locationY1 = tileBox.getCenterPixelY();// fav);
			showPopupWindow(view, locationX1, locationY1, teamship.get(0));
			// ShipLableLayer.clearLayer();
			ShipLableLayer.teamlable = true;
			view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
			TeamShipsListAdapter.isMove = false;
		}
		// if (app.myPreferences.getBoolean("IsLogin", false)) {
		if (ShipsListAdapter.isMove && searchshipsBeans != null
				&& searchshipsBeans.size() >= 1) {

			// view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
			// if(){
			Double la = null, lo = null;
			la = Double.parseDouble(searchshipsBeans.get(searchshipsBeans
					.size() - 1).la);
			lo = Double.parseDouble(searchshipsBeans.get(searchshipsBeans
					.size() - 1).lo);
			thread.startMoving(la, lo, z, false);
			// view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
			int locationX1 = tileBox.getCenterPixelX();// tileBox.getPixXFromLonNoRot(fav.getLo());
			int locationY1 = tileBox.getCenterPixelY();// fav);

			showPopupWindow(view, locationX1, locationY1,
					searchshipsBeans.get(searchshipsBeans.size() - 1));
			// ShipLableLayer.clearLayer();
			ShipLableLayer.teamlable = true;
			view.callPrepareBufferImage("shipsInfoLayer", tileBox, true);
			ShipsListAdapter.isMove = false;
		}
		// }
	}

	private void refreshShipsInfoLayer(Canvas canvas, RotatedTileBox tileBox) {
		// long rstime=System.currentTimeMillis();
		try {
			HashMap<String, ShipsBean> showArray = new HashMap<String, ShipsBean>();// 刷新时用的showarray。
			initShipsShowarray(showArray);
			// 将属于当前窗口范围内的船队船舶加入到待显示队列。
			focusedships = new ArrayList<ShipsBean>();
			if (tileBox.getZoom() >= START_SHOW) {
				Iterator<Entry<String, ShipsBean>> ite = showArray.entrySet()
						.iterator();
				// System.out.println("MMSI: " + ship.m);
				// System.out.println("航向:" + "======" + ship.co);

				while (ite.hasNext()) {
					Map.Entry entry = (Map.Entry) ite.next();
					String key = entry.getKey().toString();
					ShipsBean obj = showArray.get(key);
					if (obj == null) {
						print("obj null.");
						continue;
					}
					// print("obj getm."+obj.getM()+showArray.size());
					// if(app.getMyrole().equals("coastal")&){
					// if(app.getMyrole().equals("ship")&&obj.){
					// }//判断ais数据时加的权限

					int have1 = 0;// 判断是否红色
					for (int tap = 0; tap < alltap_ships.size(); tap++) {
						if (alltap_ships.get(tap).m.equals(obj.m)) {
							have1++;
							focusedships.add(obj);
							break;
						}
					}
					if (MyTeamShipsThread.shipsBeans != null && have1 == 0) {
						// for (int tap = 0; tap <
						// MyTeamShipsThread.shipsBeans.size(); tap++) {
						if (MyTeamShipsThread.shipsBeans.containsKey(obj.m)) {
							have1++;
							focusedships.add(obj);
							// break;
							// print("这是船队的船");
						}
						// }
					} else {
						continue;
					}
					// for (int tap = 0; tap < allteamship.size(); tap++) {
					// if (allteamship.get(tap).m.equals(obj.m)) {
					// have1++;
					// }
					// }//如果移动海图获取的是所有船队船舶就不需要了。
					if (have1 == 0) {
						for (int tap = 0; tap < allsearchshipsBeans.size(); tap++) {
							if (allsearchshipsBeans.get(tap).m.equals(obj.m)) {
								have1++;
								focusedships.add(obj);
								break;
							}
						}
					} else {
						continue;
					}

					if (have1 != 0) {
						continue;
					}
					int have = 0;// 判断是否被选中
					for (int tap = 0; tap < tap_ships.size(); tap++) {
						if (tap_ships.get(tap).m.equals(obj.m)) {
							have++;
							focusedships.add(obj);
							break;
						}
					}
					if (have == 0) {
						for (int tap = 0; tap < searchshipsBeans.size(); tap++) {
							if (searchshipsBeans.get(tap).m.equals(obj.m)) {
								have++;
								focusedships.add(obj);
								break;
							}
						}
					} else {
						continue;
					}
					if (have == 0) {
						for (int tap = 0; tap < teamship.size(); tap++) {
							if (teamship.get(tap).m.equals(obj.m)) {
								have++;
								focusedships.add(obj);
								break;
							}
						}
					} else {
						continue;
					}

					if (have != 0) {
						continue;
					}
					shipbm = null;
					boolean trueshape = false;
					int color = -1;
					double w;
					double l;
					if (!obj.b.equals("-") && !obj.l.equals("-")) {
						w = Double.valueOf(obj.b);
						l = Double.valueOf(obj.l);
					} else {
						w = 1;
						l = 1;
					}

					float co;
					boolean heading = false;
					boolean courseline = false;
					if (obj.h != null && !obj.h.equals("-")
							&& Float.valueOf(obj.h) <= 360) {
						co = Float.valueOf(obj.h);
						heading = true;
						if (obj.getSp() >= 3 && tileBox.getZoom() > 14) {
							courseline = true;
						} else {
							courseline = false;
						}
					} else {
						co = obj.getCo();
						heading = false;
						courseline = true;
					}// 航首向超过 360 采用航向
					int alpha = -1;
					if (System.currentTimeMillis()
							- obj.getDateupdatetime().getTime() > 1 * 60 * 60 * 1000) {
						alpha = 80;
					}
					if ((tileBox.getZoom() == 15 && w > 15 && l > 20)
							|| (tileBox.getZoom() == 16 && w > 10 && l > 15)
							|| (tileBox.getZoom() == 17 && w > 5 && l > 10)) {
						trueshape = true;
						if (obj.getSp() > 0.5) {
							shipbm = getTrueshapeShipBitmap(w, l, 1, false,
									false, heading, color, alpha);
						} else {
							shipbm = getTrueshapeShipBitmap(w, l, 0, false,
									false, heading, color, alpha);
						}// 都不是 focuse的 不需要比较

					} else {
						// if (have == 0) {
						// if (have1 == 0) {
						if (obj.getSp() > 0.5) {
							shipbm = getShipBitmap(1, false, false, heading,
									-1, alpha);
						} else {
							shipbm = getShipBitmap(0, false, false, heading,
									-1, alpha);
						}// 都不是 focuse的 函数内部判断bitmap返回值

					}
					// print("fleet: "+obj.dn);

					int locationX = tileBox.getPixXFromLonNoRot(obj.getLo());
					int locationY = tileBox.getPixYFromLatNoRot(obj.getLa());

					try {
						canvas.save();
						if (shipbm != null) {
							// print("draw shipbm"+obj.getM());
							if (obj.getSp() > 0.5) {
								float ox = 0;
								float oy = 0;
								if (obj.ob != null
										&& obj.oc != null
										&& !obj.ob.equals("-")
										&& !obj.oc.equals("-")
										&& (!obj.od.equals("0") || !obj.oc
												.equals("0"))) {
									// double
									// pixa=Double.valueOf(obj.oa)/lengthPerpix;
									double pixb = Double.valueOf(obj.ob);
									// double
									// pixc=Double.valueOf(obj.oc)/lengthPerpix;
									double pixd = Double.valueOf(obj.od);

									ox = (float) ((w / 2 - pixd) / lengthPerpix);
									// System.err.println("w"+obj.b+"d"+obj.od);
									oy = (float) ((l / 2 - pixb) / lengthPerpix);
								}

								if (trueshape) {
									canvas.rotate(co + 180, locationX,
											locationY);
									canvas.drawBitmap(shipbm, locationX
											- shipbm.getWidth() / 2 + 3 / 2
											* (int) bmoffset + 1 / 1
											* dm.density + ox, locationY
											- shipbm.getHeight() / 2 + 12
											* dm.density + 3 / 2
											* (int) bmoffset + 1 / 1
											* dm.density + oy, colinepaint);
									// canvas.drawCircle(locationX, locationY,
									// 4,
									// locationPaint);
									// canvas.rotate(-(co + 180), locationX,
									// locationY);
									float disangle = (float) (-(obj.getCo() - co)
											* Math.PI / 180);
									// if(disangle==90||disangle==180||disangle==270||disangle==360){
									// ty=0;
									// }else{
									// ty=(float) Math.tan(disangle);
									// }
									// canvas.save();
									// canvas.rotate(obj.getCo() + 180,
									// locationX+ox, locationY+oy);
									if (courseline) {
										float linex = locationX + ox - 0
												* dm.density;
										float liney = locationY + oy - 0
												* dm.density;
										float disy = shipbm.getHeight() / 2
												+ 10 / 1 * dm.density;
										canvas.drawLine(
												linex,
												liney,
												(float) (linex + disy
														* Math.sin(disangle)),
												(float) (liney + disy
														* Math.cos(disangle)),
												colinepaint);
										// System.err.println(ox+"oy"+oy+" linex:"+linex+" locationY+oy:"+(locationY+oy)+" linex+disy*Math.sin(disangle)"+
										// (linex+disy*Math.sin(disangle))+" liney+disy*Math.cos(disangle)"+(liney+disy*Math.cos(disangle)));
										// colinepaint.setColor(view.getResources().getColor(R.color.black));
									}
									// canvas.restore();
								} else {
									canvas.rotate(co + 180, locationX,
											locationY);
									canvas.drawBitmap(shipbm, locationX
											- shipbm.getWidth() / 2 + 3 / 2
											* (int) bmoffset + 1 / 1
											* dm.density,
											locationY - shipbm.getHeight() / 2
													+ 12 * dm.density + 3 / 2
													* (int) bmoffset + 1 / 1
													* dm.density, colinepaint);

									canvas.rotate(-(co + 180), locationX,
											locationY);
									canvas.rotate(obj.getCo() + 180, locationX,
											locationY);
									if (courseline) {
										canvas.drawLine(
												locationX + ox - 0 * dm.density,
												locationY + oy,
												locationX + ox - 0 * dm.density,
												locationY + shipbm.getHeight()
														/ 2 + 10 / 1
														* dm.density + oy,
												colinepaint);
									}
								}
							} else {
								if (trueshape) {
									float ox = 0;
									float oy = 0;
									if (obj.ob != null
											&& obj.oc != null
											&& !obj.ob.equals("-")
											&& !obj.oc.equals("-")
											&& (!obj.od.equals("0") || !obj.oc
													.equals("0"))) {
										// double
										// pixa=Double.valueOf(obj.oa)/lengthPerpix;
										double pixb = Double.valueOf(obj.ob);
										// double
										// pixc=Double.valueOf(obj.oc)/lengthPerpix;
										double pixd = Double.valueOf(obj.od);

										ox = (float) ((w / 2 - pixd) / lengthPerpix);
										// System.err.println("w"+obj.b+"d"+obj.od);
										oy = (float) ((l / 2 - pixb) / lengthPerpix);
									}
									canvas.rotate(co + 180, locationX,
											locationY);
									canvas.drawBitmap(shipbm, locationX
											- shipbm.getWidth() / 2 + 3 / 2
											* (int) bmoffset + 1 / 1
											* dm.density + ox, locationY
											- shipbm.getHeight() / 2 + 3 / 2
											* (int) bmoffset + 1 / 1
											* dm.density + oy, colinepaint);
									// canvas.drawCircle(locationX, locationY,
									// 2,
									// locationPaint);
									// canvas.rotate(-(co + 180), locationX,
									// locationY);
									// canvas.rotate(obj.getCo() + 180,
									// locationX, locationY);
									// canvas.drawLine(locationX, locationY,
									// locationX , locationY
									// + shipbm.getHeight() / 2, colinepaint);
								} else {
									canvas.drawBitmap(shipbm, locationX
											- shipbm.getWidth() / 2 + 3 / 2
											* (int) bmoffset + 1 / 1
											* dm.density,
											locationY - shipbm.getHeight() / 2
													+ 3 / 2 * (int) bmoffset
													+ 1 / 1 * dm.density,
											colinepaint);
									canvas.drawCircle(locationX, locationY, 2,
											locationPaint);
								}
							}
						} else {
							System.err.println("shipbm====null   不能画出");
						}
						canvas.restore();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				// System.err.println(" ite  ite size"+i);
			} else {
				Iterator<Entry<String, ShipsBean>> ite = showArray.entrySet()
						.iterator();
				// System.out.println("MMSI: " + ship.m);
				// System.out.println("航向:" + "======" + ship.co);

				while (ite.hasNext()) {
					Map.Entry entry = (Map.Entry) ite.next();
					String key = entry.getKey().toString();
					ShipsBean obj = showArray.get(key);
					focusedships.add(obj);
				}
			}

			if (tileBox.getZoom() < 14) {
				HashMap<String, ShipsBean> highshowArray = new HashMap<String, ShipsBean>();
				for (ShipsBean s : focusedships) {
					highshowArray.put(s.getM(), s);
				}
				teamCallLable(highshowArray);
			}

			for (ShipsBean obj : focusedships) {
				// print("focusedshipsfocusedships"+focusedships.size());
				if (obj == null) {
					print("obj null.");
					continue;
				}
				// int redships = 1;// 判断是否红色
				// for (int tap = 0; tap < alltap_ships.size(); tap++) {
				// if (alltap_ships.get(tap).m.equals(obj.m)) {
				// redships++;
				// break;
				// }
				// }
				// // for (int tap = 0; tap < allteamship.size(); tap++) {
				// // if (allteamship.get(tap).m.equals(obj.m)) {
				// // have1++;
				// // }
				// // }//如果移动海图获取的是所有船队船舶就不需要了。
				// if (redships == 0) {
				// for (int tap = 0; tap < allsearchshipsBeans.size(); tap++) {
				// if (allsearchshipsBeans.get(tap).m.equals(obj.m)) {
				// redships++;
				// break;
				// }
				// }
				// }// 肯定是红色不用判断

				// int have2=0;
				// if (MyTeamShipsThread.shipsBeans != null) {
				// for (int tap = 0; tap < MyTeamShipsThread.shipsBeans.size();
				// tap++) {
				// if (MyTeamShipsThread.shipsBeans.containsKey(obj.m)) {
				// have2++;
				// break;
				// print("这是船队的船");
				// }
				// }
				// }//船队的船 颜色单独设置。
				int color = view.getResources().getColor(R.color.red);
				if (MyTeamShipsThread.shipsBeans.containsKey(obj.getM())) {
					String groupname = MyTeamShipsThread.getTeammmsigroup()
							.get(obj.getM());
					if (groupname != null) {
						String tcolor = "#"
								+ MyTeamShipsThread.getTeamgroup().get(
										groupname);
						// System.err.println("color groupname "+groupname+tcolor);
						color = Color.parseColor(tcolor);
					}
				}

				int have = 0;// 判断是否被选中
				for (int tap = 0; tap < tap_ships.size(); tap++) {
					if (tap_ships.get(tap).m.equals(obj.m)) {
						have++;
						// System.err.println("this tapship width::"+tap_ships.get(tap).b+" LL::"+tap_ships.get(tap).l);
						break;
					}
				}
				if (have == 0) {
					for (int tap = 0; tap < searchshipsBeans.size(); tap++) {
						if (searchshipsBeans.get(tap).m.equals(obj.m)) {
							have++;
							break;
						}
					}
				}
				if (have == 0) {
					for (int tap = 0; tap < teamship.size(); tap++) {
						if (teamship.get(tap).m.equals(obj.m)) {
							have++;
							break;
						}
					}
				}
				boolean trueshape = false;
				double w;
				double l;
				if (obj.b != null && !obj.b.equals("-") && obj.l != null
						&& !obj.l.equals("-")) {
					w = Double.valueOf(obj.b);
					l = Double.valueOf(obj.l);
				} else {
					w = 0;
					l = 0;
				}
				float co;
				boolean heading = false;
				boolean courseline = false;
				if (obj.h != null && !obj.h.equals("-")
						&& Float.valueOf(obj.h) <= 360) {
					co = Float.valueOf(obj.h);
					heading = true;
					if (obj.getSp() >= 3 && tileBox.getZoom() > 14) {
						courseline = true;
					} else {
						courseline = false;
					}
				} else {
					co = obj.getCo();
					heading = false;
					courseline = true;
				}// 航首向超过 360 采用航向

				int alpha = -1;
				if (System.currentTimeMillis()
						- obj.getDateupdatetime().getTime() > 1 * 60 * 60 * 1000) {
					alpha = 80;
				}
				if ((tileBox.getZoom() == 15 && w > 15 && l > 20)
						|| (tileBox.getZoom() == 16 && w > 10 && l > 15)
						|| (tileBox.getZoom() == 17 && w > 5 && l > 10)) {
					trueshape = true;
					if (have == 0) {
						// if (have1 == 0) {
						// if (obj.getSp() > 0) {
						// shipbm = getTrueshapeShipBitmap(w, l, 1, false,
						// false);
						// } else {
						// shipbm = getTrueshapeShipBitmap(w, l, 0, false,
						// false);
						// }
						// } else {//都是focuse的不用判断
						if (obj.getSp() > 0.5) {
							shipbm = getTrueshapeShipBitmap(w, l, 1, true,
									false, heading, color, alpha);
						} else {
							shipbm = getTrueshapeShipBitmap(w, l, 0, true,
									false, heading, color, alpha);
						}
						// }
					} else {
						// print("点击的 fleet: "+obj.n+"角度"+obj.co+"size"+showArray.size());
						if (obj.getSp() > 0.5) {
							shipbm = getTrueshapeShipBitmap(w, l, 1, true,
									true, heading, color, alpha);
						} else {
							shipbm = getTrueshapeShipBitmap(w, l, 0, true,
									true, heading, color, alpha);
						}
					}
				} else {
					if (have == 0) {
						// if (have1 == 0) {
						// if (obj.getSp() > 0) {
						// shipbm = //shipbm1ff;
						// getShipBitmap(1,false,false,color);
						// } else {
						// shipbm = //shipbm0ff;
						// getShipBitmap(0,false,false,color);
						// }
						// } else {
						if (obj.getSp() > 0.5) {
							shipbm = // shipbm1tf;
							getShipBitmap(1, true, false, heading, color, alpha);
						} else {
							shipbm = // shipbm0tf;
							getShipBitmap(0, true, false, heading, color, alpha);
						}
						// }
					} else {
						// print("点击的 fleet: "+obj.n+"角度"+obj.co+"size"+showArray.size());
						if (obj.getSp() > 0.5) {
							shipbm = // shipbm1tt;
							getShipBitmap(1, true, true, heading, color, alpha);
						} else {
							shipbm = // shipbm0tt;
							getShipBitmap(0, true, true, heading, color, alpha);
						}
					}
				}
				// print("fleet: "+obj.dn);

				int locationX = tileBox.getPixXFromLonNoRot(obj.getLo());
				int locationY = tileBox.getPixYFromLatNoRot(obj.getLa());

				try {
					canvas.save();
					if (shipbm != null) {
						if (obj.getSp() > 0.5) {
							if (trueshape) {
								float ox = 0;
								float oy = 0;
								if (obj.ob != null
										&& obj.oc != null
										&& !obj.ob.equals("-")
										&& !obj.oc.equals("-")
										&& (!obj.od.equals("0") || !obj.oc
												.equals("0"))) {
									// double
									// pixa=Double.valueOf(obj.oa)/lengthPerpix;
									double pixb = Double.valueOf(obj.ob);
									// double
									// pixc=Double.valueOf(obj.oc)/lengthPerpix;
									double pixd = Double.valueOf(obj.od);

									ox = (float) ((w / 2 - pixd) / lengthPerpix);
									// System.err.println("w"+obj.b+"d"+obj.od);
									oy = (float) ((l / 2 - pixb) / lengthPerpix);
								}
								canvas.rotate(co + 180, locationX, locationY);
								canvas.drawBitmap(shipbm,
										locationX - shipbm.getWidth() / 2 + 3
												/ 2 * (int) bmoffset + 1 / 1
												* dm.density + ox, locationY
												- shipbm.getHeight() / 2 + 12
												* dm.density + 3 / 2
												* (int) bmoffset + 1 / 1
												* dm.density + oy, colinepaint);
								// canvas.drawCircle(locationX, locationY, 2,
								// locationPaint);

								float disangle = (float) (-(obj.getCo() - co)
										* Math.PI / 180);
								if (courseline) {
									float linex = locationX + ox - 0
											* dm.density;
									float liney = locationY + oy - 0
											* dm.density;
									float disy = shipbm.getHeight() / 2 + 10
											/ 1 * dm.density;
									canvas.drawLine(
											linex,
											liney,
											(float) (linex + disy
													* Math.sin(disangle)),
											(float) (liney + disy
													* Math.cos(disangle)),
											colinepaint);
									// System.err.println("disangle"+disangle+" ox:"+ox+"oy"+oy+" linex:"+linex+" locationY+oy:"+(locationY+oy)+" linex+disy*Math.sin(disangle)"+
									// (linex+disy*Math.sin(disangle))+" liney+disy*Math.cos(disangle)"+(liney+disy*Math.cos(disangle)));
									// colinepaint.setColor(view.getResources().getColor(R.color.black));
								}
							} else {
								canvas.rotate(co + 180, locationX, locationY);
								canvas.drawBitmap(shipbm,
										locationX - shipbm.getWidth() / 2 + 3
												/ 2 * (int) bmoffset + 1 / 1
												* dm.density, locationY
												- shipbm.getHeight() / 2 + 12
												* dm.density + 3 / 2
												* (int) bmoffset + 1 / 1
												* dm.density, colinepaint);

								if (courseline) {
									canvas.rotate(-(co + 180), locationX,
											locationY);
									canvas.rotate(obj.getCo() + 180, locationX,
											locationY);
									canvas.drawLine(locationX, locationY,
											locationX,
											locationY + shipbm.getHeight() / 2
													+ 10 / 1 * dm.density,
											colinepaint);
								}
							}
						} else {
							if (trueshape) {
								float ox = 0;
								float oy = 0;
								if (obj.ob != null
										&& obj.oc != null
										&& !obj.ob.equals("-")
										&& !obj.oc.equals("-")
										&& (!obj.od.equals("0") || !obj.oc
												.equals("0"))) {
									// double
									// pixa=Double.valueOf(obj.oa)/lengthPerpix;
									double pixb = Double.valueOf(obj.ob);
									// double
									// pixc=Double.valueOf(obj.oc)/lengthPerpix;
									double pixd = Double.valueOf(obj.od);

									ox = (float) ((w / 2 - pixd) / lengthPerpix);
									// System.err.println("w"+obj.b+"d"+obj.od);
									oy = (float) ((l / 2 - pixb) / lengthPerpix);
								}

								canvas.rotate(co + 180, locationX, locationY);
								canvas.drawBitmap(shipbm,
										locationX - shipbm.getWidth() / 2 + 3
												/ 2 * (int) bmoffset + 1 / 1
												* dm.density + ox, locationY
												- shipbm.getHeight() / 2 + 3
												/ 2 * (int) bmoffset + 1 / 1
												* dm.density + oy, colinepaint);
								// canvas.drawCircle(locationX, locationY, 2,
								// locationPaint);

								// canvas.rotate(-(co + 180), locationX,
								// locationY);
								// canvas.rotate(obj.getCo() + 180, locationX,
								// locationY);
								// canvas.drawLine(locationX, locationY,
								// locationX , locationY
								// + shipbm.getHeight() / 2, colinepaint);
							} else {
								canvas.drawBitmap(shipbm,
										locationX - shipbm.getWidth() / 2 + 3
												/ 2 * (int) bmoffset + 1 / 1
												* dm.density, locationY
												- shipbm.getHeight() / 2 + 3
												/ 2 * (int) bmoffset + 1 / 1
												* dm.density, colinepaint);
								canvas.drawCircle(locationX, locationY, 2,
										locationPaint);
							}
						}
					} else {
						System.err.println("shipbm====null   不能画出");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}finally{
					canvas.restore();
				}

			}
			Path path = drawShipOwnPoint(canvas, tileBox);
			canvas.save();
			canvas.drawPath(path, linePaint);
			canvas.restore();

			// if (ShipLableLayer.refreshflag&&view.lable) {
			// ShipLableLayer.refreshflag = false;
			// addedlable1 = new ArrayList<lableBean>();
			// if(!clearlable){
			// addedlable1.addAll(ShipsInfoLayer.caddedlable);
			// }else{
			// clearlable=false;
			// }
			// if ((tileBox.getZoom() > 9)) {
			// print("shipinfolayer 执行lable 刷新 " +
			// caddedlable.size()+"tileBox.getZoom()"+tileBox.getZoom());
			drawShipLable(canvas, tileBox);
			// }
			focusedships.clear();

			// }
			if (tileBox.getZoom() >= START_SHOW) {
				_ships2Draw.clear();
				_ships2Draw.putAll(showArray);

				drawTyphoon(canvas,tileBox);
			} else {
				if (tileBox.getZoom() <=TYPHOON_SHOW) {
					List<TyphoonBean> typhoonbeans1 = new ArrayList<TyphoonBean>();
					typhoonbeans1.addAll(typhoonbeans);
					canvas.save();
					for (TyphoonBean ty : typhoonbeans1) {
//						System.err.println("typhoon double drawcircle");
						int locationX = tileBox.getPixXFromLonNoRot(ty.getLo());
						int locationY = tileBox.getPixYFromLatNoRot(ty.getLa());
						canvas.drawCircle(locationX, locationY, 5 * dm.density,
								typhoonfillPaint);
						canvas.drawCircle(locationX, locationY, 5 * dm.density,
								typhoonPaint);
						canvas.drawCircle(locationX, locationY, 9 * dm.density,
								typhoonPaint);
						paint1.setTextSize(dip2px(dm.density, 14));
						canvas.drawText(ty.name, locationX + 9 * dm.density,
								locationY + 9 * dm.density, paint1);
						// canvas.drawBitmap(weatherIcon,
						// locationX - weatherIcon.getWidth() /
						// 2+1/1*dm.density,
						// locationY - weatherIcon.getHeight() /
						// 2+1/1*dm.density, weatherPaint);
					}
					canvas.restore();
				}//画个台风圆点

				if(tileBox.getZoom() > TYPHOON_SHOW){
					drawTyphoon(canvas,tileBox);
			}
				
				if (tileBox.getZoom() > 5) {
					List<WeatherInfoBean> weatherbeans1 = new ArrayList<WeatherInfoBean>();
					weatherbeans1.addAll(weatherbeans);
					canvas.save();
					for (WeatherInfoBean w : weatherbeans1) {
						int locationX = tileBox.getPixXFromLonNoRot(w.getLon());
						int locationY = tileBox.getPixYFromLatNoRot(w.getLat());
						canvas.drawBitmap(weatherIcon,
								locationX - weatherIcon.getWidth() / 2 + 1 / 1
										* dm.density,
								locationY - weatherIcon.getHeight() / 2 + 1 / 1
										* dm.density, weatherPaint);
					}
					canvas.restore();
//
				}
			}
			showArray.clear();
		} catch (Exception e) {
			System.err.println("refreshshipslayer singletap exception" + e);
		}
//		 System.err.println("refresh ships finish");//测试refresh函数执行的时间
	}


	private void drawTyphoon(Canvas canvas,RotatedTileBox tileBox) {
//		System.err.println("typhoon draw"+typhoonbeans.size());
		for (TyphoonBean ty1 : typhoonbeans) {
			Path p = new Path();
			List<TyphoonInfoBean> typhooninfobeans1 = new ArrayList<TyphoonInfoBean>();
			typhooninfobeans1.addAll(typhoonmap.get(ty1.xuhao));
			// for(TyphoonInfoBean tyi:typhooninfobeans1){
			// int locationX = tileBox.getPixXFromLonNoRot(tyi.getLo());
			// int locationY = tileBox.getPixYFromLatNoRot(tyi.getLa());
			// canvas.drawCircle(locationX, locationY, 5*dm.density,
			// typhoonfillPaint);
			// canvas.drawCircle(locationX, locationY, 5*dm.density,
			// typhoonPaint);
			// canvas.drawCircle(locationX, locationY, 9*dm.density,
			// typhoonPaint);
			// paint1.setTextSize(dip2px(dm.density, 14));
			// canvas.drawText(ty.name, locationX+ 9 * dm.density, locationY + 9
			// * dm.density, paint1);
			// // typhoonmap
			// }
			TyphoonInfoBean lasttyphoon = typhooninfobeans1
					.get(typhooninfobeans1.size() - 1);
			
			if (lasttyphoon.circle7quad1 != null
					&& lasttyphoon.circle7quad2 != null
					&& lasttyphoon.circle7quad3 != null
					&& lasttyphoon.circle7quad4 != null
					&& !lasttyphoon.circle7quad1.equals("")
					&& !lasttyphoon.circle7quad2.equals("")
					&& !lasttyphoon.circle7quad3.equals("")
					&& !lasttyphoon.circle7quad4.equals("")) {
				drawtyphoonCircle(canvas,tileBox,lasttyphoon.circle7quad4,
						lasttyphoon.circle7quad3, lasttyphoon.circle7quad2,
						lasttyphoon.circle7quad1, "7", lasttyphoon);
			}
			if (lasttyphoon.circle10quad1 != null
					&& lasttyphoon.circle10quad2 != null
					&& lasttyphoon.circle10quad3 != null
					&& lasttyphoon.circle10quad4 != null
					&& !lasttyphoon.circle10quad1.equals("")
					&& !lasttyphoon.circle10quad2.equals("")
					&& !lasttyphoon.circle10quad3.equals("")
					&& !lasttyphoon.circle10quad4.equals("")) {
				drawtyphoonCircle(canvas,tileBox,lasttyphoon.circle10quad4,
						lasttyphoon.circle10quad3, lasttyphoon.circle10quad2,
						lasttyphoon.circle10quad1, "10", lasttyphoon);
			}
			if (lasttyphoon.circle12quad1 != null
					&& lasttyphoon.circle12quad2 != null
					&& lasttyphoon.circle12quad3 != null
					&& lasttyphoon.circle12quad4 != null
					&& !lasttyphoon.circle12quad1.equals("")
					&& !lasttyphoon.circle12quad2.equals("")
					&& !lasttyphoon.circle12quad3.equals("")
					&& !lasttyphoon.circle12quad4.equals("")) {
				drawtyphoonCircle(canvas,tileBox,lasttyphoon.circle12quad4,
						lasttyphoon.circle12quad3, lasttyphoon.circle12quad2,
						lasttyphoon.circle12quad1, "12", lasttyphoon);
			}
			for (int i = 0; i < typhooninfobeans1.size(); i++) {
				canvas.save();
//				System.err.println("typhoon draw line prepare" + i
//						+ "pointsize" + typhooninfobeans1.size());
				int locationX = tileBox.getPixXFromLonNoRot(typhooninfobeans1
						.get(i).getLo());
				int locationY = tileBox.getPixYFromLatNoRot(typhooninfobeans1
						.get(i).getLa());
				// int locationX1 = 0;int locationY1 = 0;
				if (i == 0) {
					p.moveTo(locationX, locationY);
				}
				if (i < typhooninfobeans1.size() - 1) {
//					System.err.println("typhoon draw line" + i + "pointsize"
//							+ typhooninfobeans1.size());
					int locationX1 = tileBox
							.getPixXFromLonNoRot(typhooninfobeans1.get(i + 1)
									.getLo());
					int locationY1 = tileBox
							.getPixYFromLatNoRot(typhooninfobeans1.get(i + 1)
									.getLa());// 后一个点
					p.lineTo(locationX1, locationY1);
					// canvas.drawLine(locationX, locationY, locationX1,
					// locationY1, typhoonPaint);
				}

				int color = gettyphoonColor(typhooninfobeans1.get(i).level);
				typhoonfillPaint.setColor(color);
				canvas.drawCircle(locationX, locationY, 5 * dm.density,
						typhoonfillPaint);
				
				if (i == typhooninfobeans1.size() - 1) {
//					System.err.println("typhoon draw now point");
//					canvas.drawPath(p, typhoonPaint);
					canvas.drawCircle(locationX, locationY, 5 * dm.density,
							typhoonPaint);
					canvas.drawCircle(locationX, locationY, 9 * dm.density,
							typhoonPaint);

					paint1.setTextSize(dip2px(dm.density, 14));
					canvas.drawText(typhooninfobeans1.get(i).name, locationX
							+ 9 * dm.density, locationY + 9 * dm.density,
							paint1);
					
				}
				canvas.restore();
			}
			
			drawforeTyphoon(p,canvas,tileBox,typhoonforemap.get(ty1.xuhao));
			
		}

	}
	
	private void drawforeTyphoon(Path p1,Canvas canvas,RotatedTileBox tileBox,List<TyphoonInfoBean> foretyphooninfobeans){
//		System.err.println("drawtyphoonfore ");
			List<TyphoonInfoBean> typhooninfobeans1=new ArrayList<TyphoonInfoBean>();
			typhooninfobeans1.addAll(foretyphooninfobeans);
			Path p=new Path();
			for(int i=0;i<typhooninfobeans1.size();i++){
				int locationX = tileBox.getPixXFromLonNoRot(typhooninfobeans1.get(i).getLo());
				int locationY = tileBox.getPixYFromLatNoRot(typhooninfobeans1.get(i).getLa());
//				int locationX1 = 0;int locationY1 = 0;
				if(i==0){p.moveTo(locationX, locationY);}
				if(i<typhooninfobeans1.size()-1){
				int locationX1 = tileBox.getPixXFromLonNoRot(typhooninfobeans1.get(i+1).getLo());
				int locationY1 = tileBox.getPixYFromLatNoRot(typhooninfobeans1.get(i+1).getLa());//后一个点
//				canvas.drawLine(locationX, locationY, locationX1, locationY1, foretyphoonPaint);
				p.lineTo(locationX1, locationY1);
				}
//				if(i==0){continue;}
				int color=gettyphoonColor(typhooninfobeans1.get(i).level);
				typhoonfillPaint.setColor(color);
				canvas.drawCircle(locationX, locationY, 5*dm.density, typhoonfillPaint);
				if(i==typhooninfobeans1.size()-1){
//				canvas.drawCircle(locationX, locationY, 9*dm.density, typhoonPaint);
//				
//				paint1.setTextSize(dip2px(dm.density, 14));
//				canvas.drawText(typhooninfobeans1.get(i).name, locationX+ 9 * dm.density, locationY + 9
//						* dm.density, paint1);
//				System.err.println("drawtyphoonfore end ");
					canvas.save();
					canvas.drawPath(p1, typhoonPaint);
					canvas.drawPath(p, foretyphoonPaint);
					canvas.restore();
			}
				
			}
	}
	private void drawtyphoonCircle(Canvas canvas,RotatedTileBox tileBox,String q4,String q3,String q2,String q1,String l,TyphoonInfoBean typhooninfobean){
//		System.err.println("typhoon draw circle");
		canvas.save();
		if(l.equals("7")){
			typhooncirclepaint.setColor(0xffFFCC33);
		}else if(l.equals("10")){
			typhooncirclepaint.setColor(0xffFF9933);
		}else if(l.equals("12")){
			typhooncirclepaint.setColor(0xffFF6633);
		}
		typhooncirclepaint.setAlpha(150);
		int locationXc = tileBox.getPixXFromLonNoRot(typhooninfobean.getLo());
		int locationYc = tileBox.getPixYFromLatNoRot(typhooninfobean.getLa());
		int ra2=(int) (Double.valueOf(q2)*1000/lengthPerpix);
//		float r=100;
//		System.err.println("ra2:::"+ra2+" "+"lengthPerpix"+lengthPerpix);
		RectF rect2=new RectF(locationXc-ra2,locationYc-ra2,locationXc+ra2,locationYc+ra2);
		canvas.drawArc(rect2, 0, 90, true, typhooncirclepaint);
		paint1.setTextSize(dip2px(dm.density, 14));
//		canvas.drawText(l+"级风圈"+" "+"半径"+q2+"km", locationXc+ra2/3, locationYc+ra2/3, paint1);
		
		int ra3=(int) (Double.valueOf(q3)*1000/lengthPerpix);
//		float r=100;
		RectF rect3=new RectF(locationXc-ra3,locationYc-ra3,locationXc+ra3,locationYc+ra3);
		canvas.drawArc(rect3, 90, 90, true, typhooncirclepaint);
//		canvas.drawText(l+"级风圈"+" "+"半径"+q3+"km", locationXc-ra3, locationYc+ra3, paint1);
		
		int ra4=(int) (Double.valueOf(q4)*1000/lengthPerpix);
//		float r=100;
		RectF rect4=new RectF(locationXc-ra4,locationYc-ra4,locationXc+ra4,locationYc+ra4);
		canvas.drawArc(rect4, 180, 90, true, typhooncirclepaint);
//		canvas.drawText(l+"级风圈"+" "+"半径"+q4+"km", locationXc-ra4/3, locationYc-ra4/3, paint1);
		
		int ra1=(int) (Double.valueOf(q1)*1000/lengthPerpix);
//		float r=100;
		RectF rect1=new RectF(locationXc-ra1,locationYc-ra1,locationXc+ra1,locationYc+ra1);
		canvas.drawArc(rect1, 270, 90, true, typhooncirclepaint);//4个象限 风圈
//		canvas.drawText(l+"级风圈"+" "+"半径"+q1+"km", locationXc+ra1, locationYc-ra1, paint1);
		canvas.drawText(l+"级风圈", locationXc-ra4/2, locationYc-ra4/2, paint1);
		
		canvas.restore();
	}
	
	private int gettyphoonColor(String level){
		if(level==null||level.equals("N/A")||level.equals("-")||level.equals("")){
			return 0xffF6F200;
		}
		int l=Integer.parseInt(level);
		int color=0xffF6F200;
		if(l<=7){
			color = 0xffF6F200;
		}else if(l>7 && l<=10){
			color= 0xffF4D000;
		}else if(l==11){
			color = 0xffFDB700;
		}else if(l==12){
			color = 0xffFD8B00;
		}else if(l>12 && l<=15){
			color = 0xffFD5C1C;
		}else if(l>15){
			color = 0xffFD0026;
		}
		return color;
		
	}
	public static int dip2px(float density, float dpValue) {
		final float scale = density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float density, float pxValue) {
		final float scale = density;
		return (int) (pxValue / scale + 0.5f);
	}

	class LoadingTyphoonXMLThread extends AsyncTask<Void, String, String> {
		public LoadingTyphoonXMLThread() {

		}

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(String... values) {

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			// print("船舶层异步结束。view . shipInfoLayer请求: "+uuid);
			// clearWeatherMapByUUID(uuid);
			// view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox,
			// false);

			// 刷新数据
		}

		protected void onCancelled() {
			// print("" + uuid + " cancelled.");
		}

		HttpURLConnection conn;

		@Override
		protected String doInBackground(Void... arg0) {
			// flag = false;
			try {

				if (this.isCancelled()) {
					// print("线程取消 1.");
					return null;
				}

				String bboxurl = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_TYPHOON_URL;

				System.out.println("typhoonbeans URL" + "====" + bboxurl);

				URL url = new URL(bboxurl);

				if (this.isCancelled()) {
					// print("线程取消 2 .");
					return null;
				}

				conn = (HttpURLConnection) url.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				// System.out.println("ships sessionid" + "===="
				// +app.myPreferences.getString("sessionid", ""));
				conn.setConnectTimeout(10000);

				InputStream inStream = conn.getInputStream();

				if (this.isCancelled()) {
					// print("线程取消 3.");
					inStream.close();
					return null;
				}

				parseXMLnew(inStream);
				inStream.close();
				// if(weatherbeans!=null&&weatherbeans.size()>0){
				// lastcomparedate=comparedate;
				// }
				// print(uuid+" 获得数据。");
				// flag = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		private void parseXMLnew(InputStream inStream) throws Exception {
			// System.out.println("解析xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inStream);
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getChildNodes();
			// System.out.println(document.toString());
			// typhoonbeans.clear();
			// print("解析时ships"+_ships.size());

			// 判断是否超时
			if (root.getNodeName().compareTo("session_timeout") == 0) {
				heartBeatBean.add(XmlParseUtility.parse(root,
						HeartBeatBean.class));
				return;
			}

			// TODO 更新缓存部分
			// 船舶数据解析并加入_ships
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = (Node) childNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;
					if (childElement.getNodeName().compareTo("typhoon") == 0) {
						typhoonbeans.add(XmlParseUtility.parse(childElement,
								TyphoonBean.class));
					}
				}
			}
			// System.out.println("_ships.size====" + _ships.size());
			for (TyphoonBean s1 : typhoonbeans) {
				System.out.println("typhoonbeans mmsi" + s1.name);
				LoadingTyphoonInfoXMLThread task = new LoadingTyphoonInfoXMLThread(
						s1.xuhao);
				// weathertaskmap.put(uuid, task);
				// print("" + uuid + " 启动");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					task.executeOnExecutor(Executors.newCachedThreadPool(),
							new Void[0]);
				} else {
					task.execute(new Void[0]);
				}
			}

			// for (HeartBeatBean h : heartBeatBean) {
			// // System.out.println("uuuuuuuuuuuu=" + h.message);
			// }
		}
	}

	class LoadingTyphoonInfoXMLThread extends AsyncTask<Void, String, String> {
		String x;

		public LoadingTyphoonInfoXMLThread(String s) {
			this.x = s;
		}

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(String... values) {

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			// print("船舶层异步结束。view . shipInfoLayer请求: "+uuid);
			// clearWeatherMapByUUID(uuid);
			view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox, false);

			// 刷新数据
		}

		protected void onCancelled() {
			// print("" + uuid + " cancelled.");
		}

		HttpURLConnection conn;

		@Override
		protected String doInBackground(Void... arg0) {
			// flag = false;
			try {

				if (this.isCancelled()) {
					// print("线程取消 1.");
					return null;
				}

				String bboxurl = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_TYPHOONINFO_URL + x;

				System.out.println("typhooninfo URL" + "====" + bboxurl);

				URL url = new URL(bboxurl);

				if (this.isCancelled()) {
					// print("线程取消 2 .");
					return null;
				}

				conn = (HttpURLConnection) url.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				// System.out.println("ships sessionid" + "===="
				// +app.myPreferences.getString("sessionid", ""));
				conn.setConnectTimeout(10000);

				InputStream inStream = conn.getInputStream();

				if (this.isCancelled()) {
					// print("线程取消 3.");
					inStream.close();
					return null;
				}

				parseXMLnew(inStream);
				inStream.close();
				// if(weatherbeans!=null&&weatherbeans.size()>0){
				// lastcomparedate=comparedate;
				// }
				// print(uuid+" 获得数据。");
				// flag = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		private void parseXMLnew(InputStream inStream) throws Exception {
			// System.out.println("解析xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inStream);
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getChildNodes();
			// System.out.println(document.toString());
			List<TyphoonInfoBean> typhooninfobeans = new ArrayList<TyphoonInfoBean>();
			// print("解析时ships"+_ships.size());

			// 判断是否超时
			if (root.getNodeName().compareTo("session_timeout") == 0) {
				heartBeatBean.add(XmlParseUtility.parse(root,
						HeartBeatBean.class));
				return;
			}

			// TODO 更新缓存部分
			// 船舶数据解析并加入_ships
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = (Node) childNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;
					if (childElement.getNodeName().compareTo("position") == 0) {
						typhooninfobeans.add(XmlParseUtility.parse(
								childElement, TyphoonInfoBean.class));
					}
				}
			}
			
			TyphoonInfoBean tfore = null;
			ArrayList<TyphoonInfoBean> foretyphooninfobeans;
			for(int i=typhooninfobeans.size()-1;i>=0;i--){
//				if (typhooninfobeans.get(i).fore6lat != null
//				&& !typhooninfobeans.get(i).fore6lat.equals("")&&!typhooninfobeans.get(i).fore6lat.equals("N/A")) {
////			System.err.println("typhoon draw set tfore");
			foretyphooninfobeans = new ArrayList<TyphoonInfoBean>();
			tfore = typhooninfobeans.get(i);
//			break;
//		  }	
				getforeTyphoonbeans(tfore,foretyphooninfobeans);
				if(foretyphooninfobeans.size()>1){
					typhoonforemap.put(typhooninfobeans.get(0).xuhao, foretyphooninfobeans);
					break;
				}
		 }
//			if (tfore == null) {
//				tfore = typhooninfobeans.get(typhooninfobeans.size()-1);
//			}
			
			typhoonmap.put(typhooninfobeans.get(0).xuhao, typhooninfobeans);
			// System.out.println("_ships.size====" + _ships.size());
			// for(TyphoonInfoBean s1:typhooninfobeans){
			// System.out.println("typhooninfobeans mmsi"
			// +s1.lat+"level"+s1.level);
			// }

			// for (HeartBeatBean h : heartBeatBean) {
			// // System.out.println("uuuuuuuuuuuu=" + h.message);
			// }
		}
	}

	private void getforeTyphoonbeans(TyphoonInfoBean tfore,ArrayList<TyphoonInfoBean> foretyphooninfobeans){
		if (tfore != null) {
//			foretyphooninfobeans = new ArrayList<TyphoonInfoBean>();
			foretyphooninfobeans.add(tfore);
			SimpleDateFormat sfd = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date tftime;
			try {
				tftime = sfd.parse(tfore.updatetime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				tftime = new Date();
			}
			if (tfore.fore6lat != null && !tfore.fore6lat.equals("")
					&& !tfore.fore6lat.equals("N/A")) {

				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore6lat;
				t.lon = tfore.fore6lon;
				t.level = tfore.fore6level;
				t.pressure = tfore.fore6pressure;
				t.windspeed = tfore.fore6windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 6);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);
			}
			if (tfore.fore12lat != null && !tfore.fore12lat.equals("")
					&& !tfore.fore12lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore12lat;
				t.lon = tfore.fore12lon;
				t.level = tfore.fore12level;
				t.pressure = tfore.fore12pressure;
				t.windspeed = tfore.fore12windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 12);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);

			}
			if (tfore.fore18lat != null && !tfore.fore18lat.equals("")
					&& !tfore.fore18lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore18lat;
				t.lon = tfore.fore18lon;
				t.level = tfore.fore18level;
				t.pressure = tfore.fore18pressure;
				t.windspeed = tfore.fore18windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 18);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);

			}
			if (tfore.fore24lat != null && !tfore.fore24lat.equals("")
					&& !tfore.fore24lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore24lat;
				t.lon = tfore.fore24lon;
				t.level = tfore.fore24level;
				t.pressure = tfore.fore24pressure;
				t.windspeed = tfore.fore24windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 24);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);
			}
			if (tfore.fore36lat != null && !tfore.fore36lat.equals("")
					&& !tfore.fore36lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore36lat;
				t.lon = tfore.fore36lon;
				t.level = tfore.fore36level;
				t.pressure = tfore.fore36pressure;
				t.windspeed = tfore.fore36windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 36);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);

			}
			if (tfore.fore48lat != null && !tfore.fore48lat.equals("")
					&& !tfore.fore48lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore48lat;
				t.lon = tfore.fore48lon;
				t.level = tfore.fore48level;
				t.pressure = tfore.fore48pressure;
				t.windspeed = tfore.fore48windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 48);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);
			}
			if (tfore.fore60lat != null && !tfore.fore60lat.equals("")
					&& !tfore.fore60lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore60lat;
				t.lon = tfore.fore60lon;
				t.level = tfore.fore60level;
				t.pressure = tfore.fore60pressure;
				t.windspeed = tfore.fore60windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 60);
				t.updatetime = sfd.format(f);
				foretyphooninfobeans.add(t);
			}
			if (tfore.fore72lat != null && !tfore.fore72lat.equals("")
					&& !tfore.fore72lat.equals("N/A")) {
				TyphoonInfoBean t = new TyphoonInfoBean();
				t.name = tfore.name;
				t.lat = tfore.fore72lat;
				t.lon = tfore.fore72lon;
				t.level = tfore.fore72level;
				t.pressure = tfore.fore72pressure;
				t.windspeed = tfore.fore72windspeed;
				Date f=(Date) tftime.clone();
				int h = f.getHours();
				f.setHours(h + 72);
				t.updatetime = sfd.format(f);
//				t.updatetime = tfore.updatetime;
				foretyphooninfobeans.add(t);
			}


		} 
	}
	class LoadingShipsXMLThread extends AsyncTask<Void, String, String> {
		String uuid;
		String s;
		private RotatedTileBox privateTileBox;
		private Canvas privateCanvas;

		public LoadingShipsXMLThread(String uuid, RotatedTileBox box,
				Canvas canvas, String s) {
			this.uuid = uuid;
			this.privateTileBox = box;
			this.privateCanvas = canvas;
			this.s = s;
			// System.out.println("LoadingShipsXMLThread");
			// refreshShipsInfoLayer(privateCanvas, privateTileBox);
		}

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(String... values) {

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			// print("船舶层异步结束。view . shipInfoLayer请求: "+uuid);
			clearMapByUUID(uuid);
			if (tileBox.getZoom() > 13 && app.isLabeladd()) {
				allshipsrefresh = true;
				ShipLableLayer.teamlable = true;
				if (shipfirst) {
					view.callPrepareBufferImagebylayer("shipsInfoLayer",
							tileBox, false);
					shipfirst = false;
					// System.err.println("view.callPrepareBufferImagebylayer false");
				} else if (s.equals("timer")) {
					view.callPrepareBufferImage("shipsInfoLayer",
							privateTileBox, true);// 刷新一次海图。
				}
			} else {
				view.callPrepareBufferImage("shipsInfoLayer", privateTileBox,
						true);// 刷新一次海图。
				// System.err.println("view.callPrepareBufferImage true");
			}
			// 刷新数据
			// refreshShipsInfoLayer(privateCanvas, privateTileBox);
			// if(canvas!=null&&!canvas.equals(null)){
			// Paint paint = new Paint(); //设置�?个笔刷大小是3的黄色的画笔
			// paint.setColor(Color.blue);
			// paint.setStrokeJoin(Paint.Join.ROUND);
			// paint.setStrokeCap(Paint.Cap.ROUND);
			// paint.setStrokeWidth(3);
			// canvas.drawCircle(100, 100, 90, paint);
			// }
			// System.out.println("主线程里面的onPostExecute");

			// refreshShipsInfoLayer(privateCanvas, privateTileBox);
		}

		protected void onCancelled() {
			// print("" + uuid + " cancelled.");
		}

		HttpURLConnection conn;

		@Override
		protected String doInBackground(Void... arg0) {
			// flag = false;
			try {
				if (this.isCancelled()) {
					// print("线程取消 1.");
					return null;
				}
				QuadRect rect = privateTileBox.getLatLonBounds();
				LatLon p3 = new LatLon(rect.top, rect.right);
				LatLon p2 = new LatLon(rect.top, rect.left);
				LatLon p1 = new LatLon(rect.bottom, rect.left);
				LatLon p4 = new LatLon(rect.bottom, rect.right);
				String polygon = "Polygon((" + p2.getLongitude() + "%20"
						+ p2.getLatitude() + "," + p1.getLongitude() + "%20"
						+ p1.getLatitude() + "," + p4.getLongitude() + "%20"
						+ p4.getLatitude() + "," + p3.getLongitude() + "%20"
						+ p3.getLatitude() + "," + p2.getLongitude() + "%20"
						+ p2.getLatitude() + "))";
				String bboxurl = app.myPreferences.getString("loginserver",
						null) + IndexConstants.GET_BBOX_SHIPS_URL + polygon;

				 System.out.println("all ships URL" + "====" + bboxurl);

				URL url = new URL(bboxurl);

				if (this.isCancelled()) {
					// print("线程取消 2 .");
					return null;
				}

				conn = (HttpURLConnection) url.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				// System.out.println("ships sessionid" + "===="
				// +app.myPreferences.getString("sessionid", ""));
				conn.setConnectTimeout(10000);

				InputStream inStream = conn.getInputStream();

				if (this.isCancelled()) {
					// print("线程取消 3.");
					inStream.close();
					return null;
				}

				parseXMLnew(inStream, s);
				inStream.close();
				// print(uuid+" 获得数据。");
				// flag = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}

	class LoadingHighlightedShipsXMLThread extends
			AsyncTask<Void, String, String> {
		String uuid;
		String s;
		private RotatedTileBox privateTileBox;
		private Canvas privateCanvas;

		public LoadingHighlightedShipsXMLThread(String uuid,
				RotatedTileBox box, Canvas canvas, String s) {
			this.uuid = uuid;
			this.privateTileBox = box;
			this.privateCanvas = canvas;
			this.s = s;
			// System.out.println("LoadingShipsXMLThread");
			// refreshShipsInfoLayer(privateCanvas, privateTileBox);
		}

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(String... values) {

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			// print("船舶层异步结束。view . shipInfoLayer请求: "+uuid);
			// allshipsrefresh = true;
			// ShipLableLayer.teamlable = true;
			// view.callPrepareBufferImage("shipsInfoLayer", privateTileBox,
			// true);
		}

		protected void onCancelled() {
			// print("" + uuid + " cancelled.");
		}

		HttpURLConnection conn;

		@Override
		protected String doInBackground(Void... arg0) {
			// flag = false;
			try {
				if (this.isCancelled()) {
					// print("线程取消 1.");
					return null;
				}
				// QuadRect rect = privateTileBox.getLatLonBounds();
				// LatLon p3 = new LatLon(rect.top, rect.right);
				// LatLon p2 = new LatLon(rect.top, rect.left);
				// LatLon p1 = new LatLon(rect.bottom, rect.left);
				// LatLon p4 = new LatLon(rect.bottom, rect.right);
				// String polygon = "Polygon((" + p2.getLongitude() + "%20"
				// + p2.getLatitude() + "," + p1.getLongitude() + "%20"
				// + p1.getLatitude() + "," + p4.getLongitude() + "%20"
				// + p4.getLatitude() + "," + p3.getLongitude() + "%20"
				// + p3.getLatitude() + "," + p2.getLongitude() + "%20"
				// + p2.getLatitude() + "))";
				String mmsis = "";
				String ormmsiString = new String("'or mmsi='".getBytes("UTF-8"));
				String ormmsi = URLEncoder.encode(ormmsiString, "UTF-8");
				// String ormmsi
				// = URLEncoder.encode("'or mmsi='", "UTF-8");
				for (int t = 0; t < tap_ships.size(); t++) {
					if(tap_ships.get(t).getSatti()!=null&&!tap_ships.get(t).getSatti().equals("")){
//						System.out.println("tap_ships sattti "+tap_ships.get(t).getSatti());
						break;
					}
					if (mmsis.equals("")) {
						mmsis = tap_ships.get(t).getM();
					} else {
						mmsis = mmsis + ormmsi + tap_ships.get(t).getM();
					}
				}
				for (int t = 0; t < alltap_ships.size(); t++) {
					if(alltap_ships.get(t).getSatti()!=null&&!alltap_ships.get(t).getSatti().equals("")){
//						System.out.println("tap_ships sattti "+alltap_ships.get(t).getSatti());
						break;
					}
					if (mmsis.equals("")) {
						mmsis = alltap_ships.get(t).getM();
					} else {
						mmsis = mmsis + ormmsi + alltap_ships.get(t).getM();
					}
				}
				for (int t = 0; t < searchshipsBeans.size(); t++) {
					if(searchshipsBeans.get(t).getSatti()!=null&&!searchshipsBeans.get(t).getSatti().equals("")){
//                           System.out.println("sattti "+searchshipsBeans.get(t).getSatti());
						break;
					}
					if (mmsis.equals("")) {
						mmsis = searchshipsBeans.get(t).getM();
					} else {
						mmsis = mmsis + ormmsi + searchshipsBeans.get(t).getM();
					}
				}
				for (int t = 0; t < allsearchshipsBeans.size(); t++) {
					if(allsearchshipsBeans.get(t).getSatti()!=null&&!allsearchshipsBeans.get(t).getSatti().equals("")){
//						System.out.println("all search sattti "+allsearchshipsBeans.get(t).getSatti());
						break;
					}
					if (mmsis.equals("")) {
						mmsis = allsearchshipsBeans.get(t).getM();
					} else {
						mmsis = mmsis + ormmsi
								+ allsearchshipsBeans.get(t).getM();
					}
				}
				if (tileBox.getZoom() >= START_SHOW) {
					Iterator<Entry<String, ShipsBean>> ite = MyTeamShipsThread.currentshipsBeans
							.entrySet().iterator();
					// System.out.println("MMSI: " + ship.m);
					// System.out.println("航向:" + "======" + ship.co);

					while (ite.hasNext()) {
						Map.Entry entry = (Map.Entry) ite.next();
						String key = entry.getKey().toString();
						ShipsBean obj = MyTeamShipsThread.currentshipsBeans
								.get(key);
						// _ships2.add(obj);

						if (mmsis.equals("")) {
							mmsis = obj.getM();
						} else {
							mmsis = mmsis + ormmsi + obj.getM();
						}
					}
				}
				if (mmsis.equals("")) {
					// System.err.println("mmsis ==null"+MyTeamShipsThread.currentshipsBeans.size());
					return "";
				}
				String highshipsurl = app.myPreferences.getString(
						"loginserver", null)
						+ IndexConstants.GET_HIGHlIGHTED_SHIPS_URL + mmsis;
				// String highshipsurl= URLEncoder.encode(hurl, "UTF-8");
				System.out.println("HighlightedShipsURL" + "===="
						+ highshipsurl + "  s::" + s);

				URL url = new URL(highshipsurl);

				if (this.isCancelled()) {
					// print("线程取消 2 .");
					return null;
				}

				conn = (HttpURLConnection) url.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				// System.out.println("ships sessionid" + "===="
				// +app.myPreferences.getString("sessionid", ""));
				conn.setConnectTimeout(10000);

				InputStream inStream = conn.getInputStream();

				if (this.isCancelled()) {
					// print("线程取消 3.");
					inStream.close();
					return null;
				}

				parseHships(inStream, s);
				inStream.close();
				// print(uuid+" 获得数据。");
				// flag = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}// 更新highlightedships。高亮船舶。

	class LoadingWeatherXMLThread extends AsyncTask<Void, String, String> {
		String uuid;
		String s;
		private RotatedTileBox privateTileBox;
		private Canvas privateCanvas;
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();

		public LoadingWeatherXMLThread(String uuid, RotatedTileBox box,
				Canvas canvas, String s) {
			this.uuid = uuid;
			this.privateTileBox = box;
			this.privateCanvas = canvas;
			this.s = s;
			// System.out.println("LoadingShipsXMLThread");
			// refreshShipsInfoLayer(privateCanvas, privateTileBox);

		}

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(String... values) {

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			// print("船舶层异步结束。view . shipInfoLayer请求: "+uuid);
			clearWeatherMapByUUID(uuid);
			loadweather=false;
			// view.callPrepareBufferImagebylayer("shipsInfoLayer", tileBox,
			// false);

			// 刷新数据
		}

		protected void onCancelled() {
			// print("" + uuid + " cancelled.");
		}

		HttpURLConnection conn;

		@Override
		protected String doInBackground(Void... arg0) {
			// flag = false;
			try {

				if (lastcomparedate != null) {
					// System.err.println("lastcomparedate"+lastcomparedate+"date"+d);
					Date cdate = (Date) lastcomparedate.clone();
					cdate.setHours(lastcomparedate.getHours() + 1);
					cdate.setMinutes(15);
					if (d.before(cdate)) {
						return null;
					}
				}
				if (this.isCancelled()) {
					// print("线程取消 1.");
					return null;
				}
				String nowtimestring = "";
				Date comparedate;
				comparedate = (Date) d.clone();
				comparedate.setMinutes(15);
				comparedate.setSeconds(0);
				if (d.before(comparedate)) {
					comparedate.setHours(d.getHours() - 1);
					comparedate.setMinutes(0);
					comparedate.setSeconds(0);
					nowtimestring = dfm.format(comparedate);
				} else {
					comparedate.setHours(d.getHours());
					comparedate.setMinutes(0);
					comparedate.setSeconds(0);
					nowtimestring = dfm.format(comparedate);
				}
				String bboxurl = IndexConstants.GET_WEATHERINFO_URL
						+ "time="
						+ URLEncoder.encode(new String(nowtimestring.toString()
								.getBytes("UTF-8")), "UTF-8");

				System.out.println("weatherinfo URL" + "====" + bboxurl);

				URL url = new URL(bboxurl);

				if (this.isCancelled()) {
					// print("线程取消 2 .");
					return null;
				}

				conn = (HttpURLConnection) url.openConnection();
				if (loginSession.getSessionid() != null) {
					conn.setRequestProperty("cookie",
							loginSession.getSessionid());
				} else {
					conn.setRequestProperty("cookie",
							app.myPreferences.getString("sessionid", ""));
				}
				// System.out.println("ships sessionid" + "===="
				// +app.myPreferences.getString("sessionid", ""));
				conn.setConnectTimeout(10000);

				InputStream inStream = conn.getInputStream();

				if (this.isCancelled()) {
					// print("线程取消 3.");
					inStream.close();
					return null;
				}

				parseXMLnew(inStream, s);
				inStream.close();
				if (weatherbeans != null && weatherbeans.size() > 0) {
					lastcomparedate = comparedate;
				}
				// print(uuid+" 获得数据。");
				// flag = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		private void parseXMLnew(InputStream inStream, String s)
				throws Exception {
			// System.out.println("解析xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inStream);
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getChildNodes();
			// System.out.println(document.toString());
			weatherbeans.clear();
			// print("解析时ships"+_ships.size());

			// 判断是否超时
			if (root.getNodeName().compareTo("session_timeout") == 0) {
				heartBeatBean.add(XmlParseUtility.parse(root,
						HeartBeatBean.class));
				return;
			}

			// TODO 更新缓存部分
			// 船舶数据解析并加入_ships
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = (Node) childNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;
					if (childElement.getNodeName().compareTo("station") == 0) {
						weatherbeans.add(XmlParseUtility.parse(childElement,
								WeatherInfoBean.class));
					}
				}
			}
			// System.out.println("_ships.size====" + _ships.size());
			for (WeatherInfoBean s1 : weatherbeans) {
				System.out.println("weatherbeans mmsi" + s1.getName());
			}

			// for (HeartBeatBean h : heartBeatBean) {
			// // System.out.println("uuuuuuuuuuuu=" + h.message);
			// }
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	public void getShipsInfo2(RotatedTileBox tb, PointF point,
			List<? super ShipsBean> res) {
		int r = (int) (12 * tb.getDensity());
		int ex = (int) point.x;
		int ey = (int) point.y;
		// double minla = tb.getRightBottomLatLon().getLatitude();
		// double maxlo = tb.getRightBottomLatLon().getLongitude();
		// double maxla = tb.getLeftTopLatLon().getLatitude();
		// double minlo = tb.getLeftTopLatLon().getLongitude();
		List<ShipsBean> _ships2 = new ArrayList<ShipsBean>();
		// if (allsearchshipsBeans != null
		// && allsearchshipsBeans.size() >= 1) {
		// for (int i = 0; i < allsearchshipsBeans.size(); i++) {
		// double sla = Double
		// .valueOf(allsearchshipsBeans.get(i).la);
		// double slo = Double
		// .valueOf(allsearchshipsBeans.get(i).lo);
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// _ships2.add(allsearchshipsBeans.get(i));
		// }
		// }
		// }
		// if (searchshipsBeans != null
		// && searchshipsBeans.size() >= 1) {
		// for (int i = 0; i < searchshipsBeans.size(); i++) {
		// double sla = Double
		// .valueOf(searchshipsBeans.get(i).la);
		// double slo = Double
		// .valueOf(searchshipsBeans.get(i).lo);
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// _ships2.add(searchshipsBeans.get(i));
		// }
		// }
		// }
		// 船队船舶
		// System.out.println("ready huoquchuanduichuanboxinxi!!!");
		if (app.isLabeladd() && app.isIslogin()) {
			if (MyTeamShipsThread.currentshipsBeans.size() >= 1) {
				// for (int i = 0; i < MyTeamShipsThread.shipsBeans.size(); i++)
				// {
				// double sla =
				// Double.valueOf(MyTeamShipsThread.shipsBeans.get(i).la);
				// double slo =
				// Double.valueOf(MyTeamShipsThread.shipsBeans.get(i).lo);
				// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo)
				// {
				// _ships2.add(MyTeamShipsThread.shipsBeans.get(i));
				// }
				// }
				// System.out
				// .println("huoquchuanduichuanboxinxi currentshipsBeans!!!");

				Iterator<Entry<String, ShipsBean>> ite = MyTeamShipsThread.currentshipsBeans
						.entrySet().iterator();
				// System.out.println("MMSI: " + ship.m);
				// System.out.println("航向:" + "======" + ship.co);

				while (ite.hasNext()) {
					Map.Entry entry = (Map.Entry) ite.next();
					String key = entry.getKey().toString();
					ShipsBean obj = MyTeamShipsThread.currentshipsBeans
							.get(key);
					_ships2.add(obj);
				}
				// _ships2.addAll(MyTeamShipsThread.currentshipsBeans);
			} else {
				Iterator<Entry<String, ShipsBean>> ite = MyTeamShipsThread.shipsBeans
						.entrySet().iterator();
				// System.out.println("MMSI: " + ship.m);
				// System.out.println("航向:" + "======" + ship.co);

				while (ite.hasNext()) {
					Map.Entry entry = (Map.Entry) ite.next();
					String key = entry.getKey().toString();
					ShipsBean obj = MyTeamShipsThread.shipsBeans.get(key);
					_ships2.add(obj);
				}
				// _ships2.addAll(MyTeamShipsThread.shipsBeans);
				// System.out.println("MyTeamShipsThread.shipsBeans!!!"
				// + MyTeamShipsThread.shipsBeans.size());
			}
		}
		// 搜索船舶
		for (int s = 0; s < searchshipsBeans.size(); s++) {
			int c = 0;
			for (int cs = 0; cs < _ships2.size(); cs++) {
				if (_ships2.get(cs).m.equals(searchshipsBeans.get(s).m)) {
					c++;
					break;
				}
			}
			if (c == 0) {
				_ships2.add(searchshipsBeans.get(s));
			}
		}
		for (int s = 0; s < allsearchshipsBeans.size(); s++) {
			int c = 0;
			for (int cs = 0; cs < _ships2.size(); cs++) {
				if (_ships2.get(cs).m.equals(allsearchshipsBeans.get(s).m)) {
					c++;
					break;
				}
			}
			if (c == 0) {
				_ships2.add(allsearchshipsBeans.get(s));
			}
		}
		// 船队单个船舶//如果移动海图获取的是所有船队船舶就不需要了。
		// if (teamship != null && teamship.size() >= 1) {
		// for (int i = 0; i < teamship.size(); i++) {
		// double sla = Double
		// .valueOf(teamship.get(i).la);
		// double slo = Double
		// .valueOf(teamship.get(i).lo);
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// _ships2.add(teamship.get(i));
		// }
		// }
		// }
		// 点击的船舶
		for (int i = 0; i < alltap_ships.size(); i++) {
			// print("进入比较循环1");
			boolean alltap_shipshave = false;
			for (int j = 0; j < _ships2.size(); j++) {
				if (alltap_ships.get(i).m.equals(_ships2.get(j).m)) {
					// System.out.println("比例尺较小全部点击的无船队船舶");
					alltap_shipshave = true;
					break;
				}
			}
			if (!alltap_shipshave) {
				_ships2.add(alltap_ships.get(i));
				// print("增加所有点击船舶" + showArray.size());
			}
		}
		for (int j = 0; j < _ships2.size(); j++) {
			boolean tap_shipshave = false;
			// print("进入比较循环2"+tap_ships.size());
			if (tap_ships.size() >= 1) {
				// print("tap_ships不是空");
				if (_ships2.get(j).m.equals(tap_ships.get(0).m)) {
					// System.out.println("比例尺较小加入点击的无船队船舶");
					tap_shipshave = true;
					break;
				}
				if (!tap_shipshave) {
					_ships2.add(tap_ships.get(0));
					// print("增加点击船舶" + showArray.size());
				}
			}
			// print("结束比较循环2");
		}
		if (_ships2 != null && _ships2.size() >= 1) {
			for (ShipsBean n : _ships2) {
				if (tb == null)
					System.out.println("tb null!!!");
				int x = (int) tb.getPixXFromLatLon(n.getLa(), n.getLo());
				int y = (int) tb.getPixYFromLatLon(n.getLa(), n.getLo());
				if (calculateBelongs(ex, ey, x, y, r)) {
					res.add(n);
					break;// TODO ?
				}
			}
			_ships2.clear();
		}
	}

	public void getShipsInfo(RotatedTileBox tb, PointF point,
			List<? super ShipsBean> res) {
		int r = (int) (12 * tb.getDensity());
		int ex = (int) point.x;
		int ey = (int) point.y;
		double minla = tb.getRightBottomLatLon().getLatitude();
		double maxlo = tb.getRightBottomLatLon().getLongitude();
		double maxla = tb.getLeftTopLatLon().getLatitude();
		double minlo = tb.getLeftTopLatLon().getLongitude();
		List<ShipsBean> _ships2 = new ArrayList<ShipsBean>();
		// 搜索船舶//所有船舶包含则不用添加。
		// if (allsearchshipsBeans != null
		// && allsearchshipsBeans.size() >= 1) {
		// for (int i = 0; i < allsearchshipsBeans.size(); i++) {
		// double sla = Double
		// .valueOf(allsearchshipsBeans.get(i).la);
		// double slo = Double
		// .valueOf(allsearchshipsBeans.get(i).lo);
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// _ships2.add(allsearchshipsBeans.get(i));
		// }
		// }
		// }
		// if (searchshipsBeans != null
		// && searchshipsBeans.size() >= 1) {
		// for (int i = 0; i < searchshipsBeans.size(); i++) {
		// double sla = Double
		// .valueOf(searchshipsBeans.get(i).la);
		// double slo = Double
		// .valueOf(searchshipsBeans.get(i).lo);
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// _ships2.add(searchshipsBeans.get(i));
		// }
		// }
		// }
		// 船队单个船舶//如果移动海图获取的是所有船队船舶就不需要了。
		// if (teamship != null && teamship.size() >= 1) {
		// for (int i = 0; i < teamship.size(); i++) {
		// double sla = Double
		// .valueOf(teamship.get(i).la);
		// double slo = Double
		// .valueOf(teamship.get(i).lo);
		// if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
		// _ships2.add(teamship.get(i));
		// }
		// }
		// }
		ArrayList<ShipsBean> _allships2Draw = new ArrayList<ShipsBean>();
		HashMap<String, ShipsBean> _allships2Draw1 = new HashMap<String, ShipsBean>();// 获取可点击的船舶。
		if (_ships2Draw != null) {
			try {
				_allships2Draw1.putAll(_ships2Draw);
				Iterator<Entry<String, ShipsBean>> ite = _allships2Draw1
						.entrySet().iterator();
				// System.out.println("MMSI: " + ship.m);
				// System.out.println("航向:" + "======" + ship.co);

				while (ite.hasNext()) {
					Map.Entry entry = (Map.Entry) ite.next();
					String key = entry.getKey().toString();
					ShipsBean obj = _allships2Draw1.get(key);
					_allships2Draw.add(obj);
				}
				// _allships2Draw.addAll(_ships2Draw);
				// 所有船舶
				if (_allships2Draw != null && _allships2Draw.size() >= 1) {
					for (int ii = 0; ii < _allships2Draw.size(); ii++) {
						// _ships.get(ii);
						double sla = Double.valueOf(_allships2Draw.get(ii).la);
						double slo = Double.valueOf(_allships2Draw.get(ii).lo);
						if (sla > minla && sla < maxla && slo > minlo
								&& slo < maxlo) {
							_ships2.add(_allships2Draw.get(ii));
						}
					}
				}
				// 搜索船舶 没有就加上。
				if (MyTeamShipsThread.currentshipsBeans.size() >= 1) {
					Iterator iter = MyTeamShipsThread.currentshipsBeans
							.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = entry.getKey().toString();
						ShipsBean value = MyTeamShipsThread.currentshipsBeans
								.get(key);
						int c = 0;
						for (int cs = 0; cs < _ships2.size(); cs++) {
							if (_ships2.get(cs).m.equals(key)) {
								c++;
								break;
							}
						}
						if (c == 0) {
							_ships2.add(value);
						}
					}
					// _ships2.addAll(MyTeamShipsThread.currentshipsBeans);
				} else {
					Iterator iter = MyTeamShipsThread.shipsBeans.entrySet()
							.iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = entry.getKey().toString();
						ShipsBean value = MyTeamShipsThread.shipsBeans.get(key);
						int c = 0;
						for (int cs = 0; cs < _ships2.size(); cs++) {
							if (_ships2.get(cs).m.equals(key)) {
								c++;
								break;
							}
						}
						if (c == 0) {
							_ships2.add(value);
						}
					}
					// _ships2.addAll(MyTeamShipsThread.shipsBeans);
				}
				for (int s = 0; s < searchshipsBeans.size(); s++) {
					int c = 0;
					for (int cs = 0; cs < _ships2.size(); cs++) {
						if (_ships2.get(cs).m.equals(searchshipsBeans.get(s).m)) {
							c++;
							break;
						}
					}
					if (c == 0) {
						_ships2.add(searchshipsBeans.get(s));
					}
				}
				for (int s = 0; s < allsearchshipsBeans.size(); s++) {
					int c = 0;
					for (int cs = 0; cs < _ships2.size(); cs++) {
						if (_ships2.get(cs).m
								.equals(allsearchshipsBeans.get(s).m)) {
							c++;
							break;
						}
					}
					if (c == 0) {
						_ships2.add(allsearchshipsBeans.get(s));
					}
				}
				// 点击的船舶
				for (int i = 0; i < alltap_ships.size(); i++) {
					// print("进入比较循环1");
					boolean alltap_shipshave = false;
					for (int j = 0; j < _ships2.size(); j++) {
						if (alltap_ships.get(i).m.equals(_ships2.get(j).m)) {
							// System.out.println("比例尺较小全部点击的无船队船舶");
							alltap_shipshave = true;
							break;
						}
					}
					if (!alltap_shipshave) {
						_ships2.add(alltap_ships.get(i));
						// print("增加所有点击船舶" + showArray.size());
					}
				}
				for (int j = 0; j < _ships2.size(); j++) {
					boolean tap_shipshave = false;
					// print("进入比较循环2"+tap_ships.size());
					if (tap_ships.size() >= 1) {
						// print("tap_ships不是空");
						if (_ships2.get(j).m.equals(tap_ships.get(0).m)) {
							// System.out.println("比例尺较小加入点击的无船队船舶");
							tap_shipshave = true;
							break;
						}
						if (!tap_shipshave) {
							_ships2.add(tap_ships.get(0));
							// print("增加点击船舶" + showArray.size());
						}
					}
					// print("结束比较循环2");
				}
			} catch (java.util.ConcurrentModificationException nul) {
				System.err
						.println("ConcurrentModificationException getshipinfo");
				return;
			}
		}
		if (_ships2 != null && _ships2.size() >= 1) {
			for (ShipsBean n : _ships2) {
				if (tb == null)
					System.out.println("tb null!!!");
				int x = (int) tb.getPixXFromLatLon(n.getLa(), n.getLo());
				int y = (int) tb.getPixYFromLatLon(n.getLa(), n.getLo());
				if (calculateBelongs(ex, ey, x, y, r)) {
					res.add(n);
					break;// TODO ?
				}
			}
			_ships2.clear();
		}

	}

	public void getweatherInfo(RotatedTileBox tb, PointF point,
			List<? super WeatherInfoBean> res) {
		int r = (int) (15 * tb.getDensity());
		int ex = (int) point.x;
		int ey = (int) point.y;
		List<WeatherInfoBean> weatherobjects = new ArrayList<WeatherInfoBean>();
		weatherobjects.addAll(weatherbeans);
		if (weatherobjects != null && weatherobjects.size() >= 1) {
			for (WeatherInfoBean n : weatherobjects) {
				if (tb == null)
					System.out.println("tb null!!!");
				int x = (int) tb.getPixXFromLatLon(n.getLat(), n.getLon());
				int y = (int) tb.getPixYFromLatLon(n.getLat(), n.getLon());
				if (calculateBelongs(ex, ey, x, y, r)) {
					res.add(n);
					break;// TODO ?
				}
			}
			weatherobjects.clear();
		}
	}

	public void gettyphoonInfo(RotatedTileBox tb, PointF point,
			List<? super TyphoonInfoBean> res) {
		int r = (int) (15 * tb.getDensity());
		int ex = (int) point.x;
		int ey = (int) point.y;
		List<TyphoonInfoBean> weatherobjects = new ArrayList<TyphoonInfoBean>();
		for(TyphoonBean ty1:typhoonbeans){
			List<TyphoonInfoBean> typhooninfobeans1=new ArrayList<TyphoonInfoBean>();
			typhooninfobeans1.addAll(typhoonmap.get(ty1.xuhao));
			weatherobjects.addAll(typhooninfobeans1);
		}
		if (weatherobjects != null && weatherobjects.size() >= 1) {
			for (TyphoonInfoBean n : weatherobjects) {
				if (tb == null)
					System.out.println("tb null!!!");
				int x = (int) tb.getPixXFromLatLon(n.getLa(), n.getLo());
				int y = (int) tb.getPixYFromLatLon(n.getLa(), n.getLo());
				if (calculateBelongs(ex, ey, x, y, r)) {
					res.add(n);
					break;// TODO ?
				}
			}
			weatherobjects.clear();
		}
	}
	public void gettyphoonForeInfo(RotatedTileBox tb, PointF point,
			List<? super TyphoonInfoBean> res) {
		int r = (int) (15 * tb.getDensity());
		int ex = (int) point.x;
		int ey = (int) point.y;
		List<TyphoonInfoBean> weatherobjects = new ArrayList<TyphoonInfoBean>();
		for(TyphoonBean ty1:typhoonbeans){
			List<TyphoonInfoBean> typhooninfobeans1=new ArrayList<TyphoonInfoBean>();
			typhooninfobeans1.addAll(typhoonforemap.get(ty1.xuhao));
			weatherobjects.addAll(typhooninfobeans1);
		}
//		weatherobjects.addAll(foretyphooninfobeans);
		
		if (weatherobjects != null && weatherobjects.size() >= 1) {
			for (TyphoonInfoBean n : weatherobjects) {
				if (tb == null)
					System.out.println("tb null!!!");
				int x = (int) tb.getPixXFromLatLon(n.getLa(), n.getLo());
				int y = (int) tb.getPixYFromLatLon(n.getLa(), n.getLo());
				if (calculateBelongs(ex, ey, x, y, r)) {
					res.add(n);
					break;// TODO ?
				}
			}
			weatherobjects.clear();
		}
	}
	private boolean calculateBelongs(int ex, int ey, int objx, int objy,
			int radius) {
		return Math.abs(objx - ex) <= radius && (ey - objy) <= radius / 2
				&& (objy - ey) <= 3 * radius;
	}

	public void showPopupWindow(final View view, int ex, int ey,
			final ShipsBean fav) {
		View contentView = LayoutInflater.from(view.getContext()).inflate(
				R.layout.balloon_overlay_ships, null);
		LinearLayout info = (LinearLayout) contentView
				.findViewById(R.id.balloon_main_layout);

		info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(view.getContext(),
						ShipInfoActivity.class);
				Bundle data = new Bundle();
				boolean myfleet = false;
				// for (int i = 0; i < MyTeamShipsThread.shipsBeans.size(); i++)
				// {
				if (MyTeamShipsThread.shipsBeans.containsKey(fav.getM())) {
					myfleet = true;
				}
				// }
				data.putString("myfleet", myfleet + "");
				data.putString("shipn", fav.n);
				data.putString("shipcs", fav.c);
				data.putString("shipcname", fav.cname);
				data.putString("shipm", fav.m);
				data.putString("shipi", fav.i);
				data.putString("shiplo", fav.lo);
				data.putString("shipla", fav.la);
				data.putString("shipco", fav.co);
				data.putString("shipsp", fav.sp);
				data.putString("shipsubt", fav.getSubt());
				data.putString("ships", fav.s);
				data.putString("shipl", fav.l);
				data.putString("shipb", fav.b);
				data.putString("shipdr", fav.dr);
				data.putString("shipd", fav.d);
				data.putString("shipe", fav.e);
				data.putString("shipti", fav.ti);
				data.putString("shipan", fav.an);
				data.putString("shipc", fav.c);
				data.putString("shipdn", fav.dn);
				data.putString("shipflag", fav.flag);
				data.putString("shipfle", fav.fle);
				data.putString("shiph", fav.h);
				data.putString("shipmes", fav.message);
				data.putString("shiprot", fav.rot);
				data.putString("shiprs", fav.rs);
				if(fav.getSatti()!=null) {
					data.putString("shipsatti", fav.getSatti());
				}
//				System.out.println("shipsatti "+fav.getSatti()+" ti"+fav.ti);
				// data.putString("from", "shipsinfolayer");
				intent.putExtras(data);
				view.getContext().startActivity(intent);
			}
		});
		TextView textShisName = (TextView) contentView
				.findViewById(R.id.text_pop_shipname);
		TextView textMmsi = (TextView) contentView
				.findViewById(R.id.text_pop_mmsi);
		TextView textTime = (TextView) contentView
				.findViewById(R.id.text_pop_time);
		final PopupWindow pop = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		if ((fav.getSatti()!=null&&!fav.getSatti().equals(""))||fav.rs.equals("0")
				|| !app.getMyrole().equals("vvip")
				|| app.getLoginbean().getTraffic().equals("1")
				|| (app.getLoginbean().getFleets().equals("1") && MyTeamShipsThread.shipsBeans
						.containsKey(fav.getM()))) {
			// System.out.println("showPopupWindow t");
			// System.out.println("showPopupWindow"+MyTeamShipsThread.shipsBeans.size());
			if (fav.cname != null && !fav.cname.equals("null")
					&& !fav.cname.equals("") && !fav.cname.equals("N/A")) {
				textShisName.setText(fav.cname);
			} else {
				if (fav.n == null || fav.n.equals("") || fav.n.equals("N/A")
						|| fav.n.equals("null")) {
					textShisName.setText("暂无船名");
				} else {
					textShisName.setText(fav.n);
				}
			}
			textMmsi.setText("MMSI: " + fav.m);
			textTime.setText(fav.getformatti(fav.getDateupdatetime()));
		} else {
//			System.out.println("showPopupWindow");
			if (fav.n != null && !fav.n.equals("null") && !fav.n.equals("")
					&& !fav.n.equals("N/A")) {
				if (fav.n.length() > 2) {
					textShisName.setText(fav.n.substring(0, 1) + "****");
				} else {
					textShisName.setText("****");
				}
//				System.out.println("showPopupWindow textShisName"
//						+ textShisName.getText());
			}
			textMmsi.setText("MMSI: " + fav.m.substring(0, 5) + "***");
			textTime.setText(fav.ti.substring(0, fav.ti.length() - 13)
					+ "** ** **");
		}
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		pop.getContentView().measure(0, 0);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + ex
				- pop.getContentView().getMeasuredWidth() / 2, location[1] + ey
				- pop.getContentView().getMeasuredHeight());

	}

	public void showPopupWindow(final View view, int ex, int ey,
			final WeatherInfoBean fav) {
		// System.out.println("showPopupWindow");
		View contentView = LayoutInflater.from(view.getContext()).inflate(
				R.layout.balloon_overlay, null);
		LinearLayout info = (LinearLayout) contentView
				.findViewById(R.id.balloon_main_layout);

		info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		TextView textName = (TextView) contentView
				.findViewById(R.id.text_sea_area);
		TextView textWinddirection = (TextView) contentView
				.findViewById(R.id.text_wind_direction);
		TextView textWindspeed = (TextView) contentView
				.findViewById(R.id.text_wind_speed);
		TextView textWaveheight = (TextView) contentView
				.findViewById(R.id.text_wave_height);
		TextView textTime = (TextView) contentView.findViewById(R.id.text_time);

		final PopupWindow pop = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		if (fav.getName() != null && !fav.getName().equals("null")
				&& !fav.getName().equals("") && !fav.getName().equals("N/A")) {
			textName.setText("名称: " + fav.getName());
		} else {
			// if (fav.n == null || fav.n.equals("") || fav.n.equals("N/A")
			// || fav.n.equals("null")) {
			textName.setText("暂无名称");
			// } else {
			// textShisName.setText(fav.n);
			// }
		}
		textWinddirection.setText("风向: " + fav.getWinddirection());
		textWindspeed.setText("风速: " + fav.getWindspeed() + " "
				+ fav.getCompasswinddirection() + "/" + fav.getWindforce());
		textWaveheight.setText("浪高: " + fav.getWave() + " 最大浪高:"
				+ fav.getMaxwave());
		textTime.setText("时间: " + fav.getUpdatetime());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		pop.getContentView().measure(0, 0);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + ex
				- pop.getContentView().getMeasuredWidth() / 2, location[1] + ey
				- pop.getContentView().getMeasuredHeight());

	}

	public void showPopupWindow(final View view, int ex, int ey,
			final TyphoonInfoBean fav) {
		// System.out.println("showPopupWindow");
		View contentView = LayoutInflater.from(view.getContext()).inflate(
				R.layout.balloon_typhoon_overlay, null);
//		LinearLayout info = (LinearLayout) contentView
//				.findViewById(R.id.balloon_main_layout);
//
//		info.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//			}
//		});
		TextView textTyName = (TextView) contentView
				.findViewById(R.id.text_ty_name);
		TextView textTylat = (TextView) contentView
				.findViewById(R.id.text_ty_lat);
		TextView textTylon = (TextView) contentView
				.findViewById(R.id.text_ty_lon);
		TextView textTydirection = (TextView) contentView
				.findViewById(R.id.text_ty_direction);
		TextView textTyspeed = (TextView) contentView
				.findViewById(R.id.text_ty_speed);
		TextView textTypressure = (TextView) contentView
				.findViewById(R.id.text_ty_pressure);
		TextView textTime = (TextView) contentView.findViewById(R.id.text_ty_time);

		final PopupWindow pop = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		if (fav.name != null && !fav.name.equals("null")
				&& !fav.name.equals("") && !fav.name.equals("N/A")) {
			textTyName.setText("台风: " + fav.name);
		} else {
			// if (fav.n == null || fav.n.equals("") || fav.n.equals("N/A")
			// || fav.n.equals("null")) {
			textTyName.setText("暂无名称");
			// } else {
			// textShisName.setText(fav.n);
			// }
		}
		textTylat.setText("纬度: "+getStringLat(Double.valueOf(fav.lat)));
		textTylon.setText("经度: "+getStringLon(Double.valueOf(fav.lon)));
		if(fav.movespeed==null){
			fav.movespeed="-";
		}
		if(fav.movedirection==null){
			fav.movedirection="-";
		}
		textTydirection.setText("移速: " + fav.movespeed+"公里/小时"+" 方向："+fav.movedirection);
		textTyspeed.setText("风速: " + fav.windspeed + " m/s "+ fav.level+"级");
		textTypressure.setText("气压: " + fav.pressure + "百帕");
		textTime.setText("时间: " + fav.updatetime);
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		pop.getContentView().measure(0, 0);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + ex
				- pop.getContentView().getMeasuredWidth() / 2, location[1] + ey
				- pop.getContentView().getMeasuredHeight());

	}
	
//	public String convertToSexagesimalLa(double num) {
//
//		int du = (int) Math.floor(Math.abs(num)); // 获取整数部分
//		double temp = getdPoint(Math.abs(num)) * 60;
//		int fen = (int) Math.floor(temp); // 获取整数部分
//		DecimalFormat df = new DecimalFormat("0.0");
//		String miao = df.format(getdPoint(temp) * 60);
//
//		if (num < 0) {
//			return du + "°" + fen + "′" + miao + "″" + " S";
//		} else {
//			return du + "°" + fen + "′" + miao + "″" + " N";
//		}
//	}
//
//	public String convertToSexagesimalLo(double num) {
//
//		int du = (int) Math.floor(Math.abs(num)); // 获取整数部分
//		double temp = getdPoint(Math.abs(num)) * 60;
//		int fen = (int) Math.floor(temp); // 获取整数部分
//		DecimalFormat df = new DecimalFormat("0.0");
//		String miao = df.format(getdPoint(temp) * 60);
//
//		if (num < 0) {
//			return du + "°" + fen + "′" + miao + "″" + " W";
//		} else {
//			return du + "°" + fen + "′" + miao + "″" + " E";
//		}
//	}
	
	public String getStringLat(Double lat){
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
	
	public String getStringLon(Double lon){
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
	public double getdPoint(double num) {
		double d = num;
		int fInt = (int) d;
		BigDecimal b1 = new BigDecimal(Double.toString(d));
		BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
		double dPoint = b1.subtract(b2).floatValue();
		return dPoint;
	}
	
	private static List<afterShip> c = new ArrayList<afterShip>();

	public interface afterShip {
		public void shipRefresh(RotatedTileBox tileBox,
				HashMap<String, ShipsBean> currentshipsBeans);
	}

	public void addafterShip(afterShip callas) {
		c.add(callas);
	}

	@Override
	public void destroyLayer() {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void dotmapRefreshed(RotatedTileBox tileBox) {
	// // TODO Auto-generated method stub
	// this.tileBox = tileBox;
	// callDownloadShipsData(tileBox);
	// }
}
