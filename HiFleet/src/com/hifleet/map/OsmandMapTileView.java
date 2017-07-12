package com.hifleet.map;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hifleet.plus.R;

import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.hifleet.lnfo.layer.PlotInfoLayer;
import com.hifleet.lnfo.layer.ShipLableLayer;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.lnfo.layer.timeLableLayer;
import com.hifleet.map.MapTileDownloader.DownloadRequest;
import com.hifleet.map.MapTileDownloader.IMapDownloaderCallback;
import com.hifleet.map.MultiTouchSupport;
import com.hifleet.map.MultiTouchSupport.MultiTouchZoomListener;
import com.hifleet.map.OsmandMapLayer.DrawSettings;

public class OsmandMapTileView extends SurfaceView implements
		IMapDownloaderCallback, Callback {
	private static String mapSourceName = "海图";

	private static final int MAP_REFRESH_MESSAGE = OsmAndConstants.UI_HANDLER_MAP_VIEW + 4;
	private static final int MAP_FORCE_REFRESH_MESSAGE = OsmAndConstants.UI_HANDLER_MAP_VIEW + 5;
	private static final int BASE_REFRESH_MESSAGE = OsmAndConstants.UI_HANDLER_MAP_VIEW + 3;
	private static final int OTHER_REFRESH_MESSAGE = OsmAndConstants.UI_HANDLER_MAP_VIEW + 6;

	protected final static int LOWEST_ZOOM_TO_ROTATE = 9;// 原来是9，可能是定义最小可以“扭”的层级
	private IMapLocationListener locationListener;
	// private boolean MEASURE_FPS = false;z
	// private FPSMeasurement main = new FPSMeasurement();
	// private FPSMeasurement additional = new FPSMeasurement();
	// private class FPSMeasurement {
	// int fpsMeasureCount = 0;
	// int fpsMeasureMs = 0;
	// long fpsFirstMeasurement = 0;
	// float fps;
	//
	// void calculateFPS(long start, long end) {
	// fpsMeasureMs += end - start;
	// fpsMeasureCount++;
	// if (fpsMeasureCount > 10 || (start - fpsFirstMeasurement) > 400) {
	// fpsFirstMeasurement = start;
	// fps = (1000f * fpsMeasureCount / fpsMeasureMs);
	// fpsMeasureCount = 0;
	// fpsMeasureMs = 0;
	// }
	// }
	// }

	protected static final int emptyTileDivisor = 16;

	public void setMapLocationListener(IMapLocationListener l) {
		locationListener = l;
	}
	
	public interface OnTrackBallListener {
		public boolean onTrackBallEvent(MotionEvent e);
	}

	public interface OnLongClickListener {
		public boolean onLongPressEvent(PointF point);
	}

	public interface OnClickListener {
		public boolean onPressEvent(PointF point);
	}

	// protected static final Log //log =
	// PlatformUtil.getLog(OsmandMapTileView.class);

	private RotatedTileBox currentViewport;

	private float rotate; // accumulate

	private int mapPosition;

	private boolean showMapPosition = true;

	// private IMapLocationListener locationListener;

	private OnLongClickListener onLongClickListener;

	private OnClickListener onClickListener;

	private OnTrackBallListener trackBallDelegate;

	// private AccessibilityActionsProvider accessibilityActions;

	private List<OsmandMapLayer> layers = new ArrayList<OsmandMapLayer>();

	private BaseMapLayer mainLayer;

	private Map<OsmandMapLayer, Float> zOrders = new HashMap<OsmandMapLayer, Float>();

	// UI Part
	// handler to refresh map (in ui thread - ui thread is not necessary, but
	// msg queue is required).
	protected Handler handler;
	private Handler baseHandler;

	private AnimateDraggingMapThread animatedDraggingThread;

	private GestureDetector gestureDetector;

	private MultiTouchSupport multiTouchSupport;

	Paint paintGrayFill;
	Paint paintBlackFill;
	Paint paintWhiteFill;
	Paint paintCenter;

	private DisplayMetrics dm;

	private final OsmandApplication application;

	protected OsmandSettings settings = null;

	private Bitmap bufferBitmap;
	private Bitmap bufferBitmapBackup;
	private RotatedTileBox bufferImgLoc;
	private Bitmap bufferBitmapTmp;

	private Paint paintImg;

	public OsmandMapTileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		application = (OsmandApplication) context.getApplicationContext();
		//setZOrderOnTop(true);
		//getHolder().setFormat(PixelFormat.TRANSLUCENT);
		initView();

	}

	public OsmandMapTileView(Context context) {
		super(context);
		application = (OsmandApplication) context.getApplicationContext();
		initView();
	}

	// ///////////////////////////// INITIALIZING UI PART
	// ///////////////////////////////////
	public void initView() {
		paintGrayFill = new Paint();
		paintGrayFill.setColor(Color.GRAY);
		paintGrayFill.setStyle(Style.FILL);
		// when map rotate
		paintGrayFill.setAntiAlias(true);

		paintBlackFill = new Paint();
		paintBlackFill.setColor(Color.BLACK);
		paintBlackFill.setStyle(Style.FILL);
		// when map rotate
		paintBlackFill.setAntiAlias(true);

		paintWhiteFill = new Paint();
		paintWhiteFill.setColor(Color.WHITE);
		paintWhiteFill.setStyle(Style.FILL);
		// when map rotate
		paintWhiteFill.setAntiAlias(true);

		paintCenter = new Paint();
		paintCenter.setStyle(Style.STROKE);
		paintCenter.setColor(Color.rgb(60, 60, 60));
		paintCenter.setStrokeWidth(2);
		paintCenter.setAntiAlias(true);

		paintImg = new Paint();
		paintImg.setFilterBitmap(true);
		// paintImg.setDither(true);

		setClickable(true);
		setLongClickable(true);
		setFocusable(true);

		backgroundbitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background_plane);

		handler = new Handler();

		baseHandler = new Handler(application.getResourceManager()
				.getRenderingBufferImageThread().getLooper());

		getHolder().addCallback(this);

		animatedDraggingThread = new AnimateDraggingMapThread(this);

		gestureDetector = new GestureDetector(getContext(), new MapExplorer(
				this, new MapTileViewOnGestureListener()));

		multiTouchSupport = new MultiTouchSupport(getContext(),
				new MapTileViewMultiTouchZoomListener());

		gestureDetector
				.setOnDoubleTapListener(new MapTileViewOnDoubleTapListener());

		WindowManager mgr = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		dm = new DisplayMetrics();
		mgr.getDefaultDisplay().getMetrics(dm);

		currentViewport = new RotatedTileBox.RotatedTileBoxBuilder()
				.setLocation(0, 0).setZoomAndScale(8, 0)
				.setPixelDimensions(getWidth(), getHeight()).build();

		currentViewport.setDensity(dm.density);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// refreshMap();
		//print("surfaceChanged 调用 refreshMapForceDraw");
		refreshMapForceDraw(false,true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// refreshMap();
		//print("surfaceCreated 调用 refreshMapForceDraw");
		refreshMapForceDraw(false,true);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return application.accessibilityEnabled() ? false : super.onKeyDown(
				keyCode, event);
	}

	public synchronized void addLayer(OsmandMapLayer layer, float zOrder) {
		int i = 0;
		for (i = 0; i < layers.size(); i++) {
			if (zOrders.get(layers.get(i)) > zOrder) {
				break;
			}
		}
		layer.initLayer(this);
		layers.add(i, layer);
		zOrders.put(layer, zOrder);
	}

	public synchronized void removeLayer(OsmandMapLayer layer) {
		while (layers.remove(layer))
			;
		zOrders.remove(layer);
		layer.destroyLayer();
	}

	public List<OsmandMapLayer> getLayers() {
		return layers;
	}

	@SuppressWarnings("unchecked")
	public <T extends OsmandMapLayer> T getLayerByClass(Class<T> cl) {
		for (OsmandMapLayer lr : layers) {
			if (cl.isInstance(lr)) {
				return (T) lr;
			}
		}
		return null;
	}

	public OsmandApplication getApplication() {
		return application;
	}

	// ///////////////////////// NON UI PART (could be extracted in common)
	// /////////////////////////////
	public void setIntZoom(int zoom) {
//		print("setIntZoom getMinimumShownMapZoom"+mainLayer.getMinimumShownMapZoom()+"zoom"+zoom);
		if (mainLayer != null && zoom <= mainLayer.getMaximumShownMapZoom()
				&& zoom >= mainLayer.getMinimumShownMapZoom()) {
			animatedDraggingThread.stopAnimating();
			currentViewport.setZoomAndAnimation(zoom, 0);
			currentViewport
					.setRotate(zoom > LOWEST_ZOOM_TO_ROTATE ? rotate : 0);
			// refreshMap();
//			print("setIntZoom 调用 refreshMapForceDraw"+mainLayer.getMinimumShownMapZoom());
			refreshMapForceDraw(false,true);
		}
	}

	// public int getMax

	public void setComplexZoom(int zoom, float scale) {
		if (mainLayer != null && zoom <= mainLayer.getMaximumShownMapZoom()
				&& zoom >= mainLayer.getMinimumShownMapZoom()) {
			animatedDraggingThread.stopAnimating();
			//print("setComplexZoom: "+scale);
			currentViewport.setZoom(zoom, scale, 0);
			currentViewport
					.setRotate(zoom > LOWEST_ZOOM_TO_ROTATE ? rotate : 0);
			// refreshMap();
			//print("setComplexZoom 调用 refreshMapForceDraw");
			refreshMapForceDraw(false,true);
		}
	}

	public boolean isMapRotateEnabled() {
		return getZoom() > LOWEST_ZOOM_TO_ROTATE;
	}

	public void setRotate(float rotate) {
		if (isMapRotateEnabled()) {
			float diff = MapUtils.unifyRotationDiff(rotate, getRotate());
			if (Math.abs(diff) > 5) { // check smallest rotation
				// System.err.println("check smallest rotation true. start rotate.");
				animatedDraggingThread.startRotate(rotate);
			}
		}
	}

	public boolean isShowMapPosition() {
		return showMapPosition;
	}

	public void setShowMapPosition(boolean showMapPosition) {
		this.showMapPosition = showMapPosition;
	}

	public RotatedTileBox getCurrentView() {
		return currentViewport.copy();
	}

	public float getRotate() {
		return currentViewport.getRotate();
	}

	public void setMapSourceName(String mapsourceName) {
		this.mapSourceName = mapsourceName;
	}

	public String getMapSourceName() {
		return mapSourceName;
	}

	public void setLatLon(double latitude, double longitude) {
		// 此处是设置当前屏幕的中心点坐标的函数。
		animatedDraggingThread.stopAnimating();
		// System.out.println("set lon lat in OsmandMapTileView class. map source name: "+this.mapSourceName);

		// System.out.println("set center: "+);
		currentViewport.setLatLonCenter(latitude, longitude);
		//print("setLatLon: "+longitude+", "+latitude);
		// System.err.println("setLatLon in OsmandMapTileView: "+currentViewport.getLongitude()+" "+currentViewport.getLatitude());
		// System.err.println("在 setLatLon 函数中调用 refreshMap函数。");
		refreshMap();
		// refreshMapForceDraw();
	}

	public double getLatitude() {
		return currentViewport.getLatitude();
	}

	public double getLongitude() {
		return currentViewport.getLongitude();
	}

	public int getZoom() {
		return currentViewport.getZoom();
	}

	public float getSettingsZoomScale() {
		// print("osmtileview: settings zoom scale: "+settings.getSettingsZoomScale(getDensity()));
		return settings.getSettingsZoomScale(getDensity());
	}

	public float getZoomScale() {
		//print("osm tile view: get zoom scale: "+currentViewport.getZoomScale());
		return currentViewport.getZoomScale();
	}

	public boolean isZooming() {
		return currentViewport.isZoomAnimated();
	}

	// ////////////////////////////// DRAWING MAP PART
	// /////////////////////////////////////////////
	public BaseMapLayer getMainLayer() {
		return mainLayer;
	}

	public void setMainLayer(BaseMapLayer mainLayer) {
		this.mainLayer = mainLayer;
		int zoom = currentViewport.getZoom();
		if (mainLayer.getMaximumShownMapZoom() < zoom) {
			zoom = mainLayer.getMaximumShownMapZoom();
		}
		if (mainLayer.getMinimumShownMapZoom() > zoom) {
			zoom = mainLayer.getMinimumShownMapZoom();
		}
		currentViewport.setZoomAndAnimation(zoom, 0);
		// refreshMap();
		//print("setMainLayer 调用 refreshMapForceDraw");
		refreshMapForceDraw(false,true);
	}

	public void setMapPosition(int type) {
		this.mapPosition = type;
	}

	public OsmandSettings getSettings() {
		if (settings == null) {
			settings = getApplication().getSettings();
		}
		return settings;
	}

	//
	@SuppressLint("WrongCall")
	private void drawOverMap(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		final QuadPoint c = tileBox.getCenterPixelPoint();

//		 print("drawOverMap 函数");

		synchronized (this) {

			if (bufferBitmap != null && !bufferBitmap.isRecycled()) {
				//canvas.save();
				canvas.rotate(tileBox.getRotate(), c.x, c.y);

				pixelValue = bufferBitmap.getPixel(10, 10);
				if (pixelValue != 0) {
					bufferBitmapBackup = bufferBitmap;
				}

				fillCanvas2(canvas, drawSettings);

				// print("在 drawOvermap中调用 drawBasemap");

				drawBasemap(canvas);

				//canvas.restore();
			}
		}

		for (int i = 0; i < layers.size(); i++) {
			try {
				OsmandMapLayer layer = layers.get(i);
				//canvas.save();
				layer.onDraw(canvas, tileBox, drawSettings);
				//canvas.restore();
			} catch (IndexOutOfBoundsException e) {
				// skip it
			}
		}
		// canvas.drawCircle(50, 50, 10* dm.density, paintBlackFill);
		// showMapPosition = true;
		if (showMapPosition) {
			// System.out.println(" draw showMapPosition true");
			canvas.drawCircle(c.x, c.y, 3 * dm.density, paintCenter);
			canvas.drawCircle(c.x, c.y, 7 * dm.density, paintCenter);
		}
	}

	private boolean bufferBitmapDrawed = false;
	int pixelValue;

	private void drawBasemap(Canvas canvas) {
//         print("draw basemap");
		if (bufferImgLoc != null) {

			float rot = -bufferImgLoc.getRotate();
			canvas.rotate(rot, currentViewport.getCenterPixelX(),
					currentViewport.getCenterPixelY());
			//print("drawBasMap: zoomScale: "+currentViewport.getZoomScale());
			final RotatedTileBox calc = currentViewport.copy();
			calc.setRotate(bufferImgLoc.getRotate());

			int cz = getZoom();
			QuadPointDouble lt = bufferImgLoc.getLeftTopTile(cz);
			QuadPointDouble rb = bufferImgLoc.getRightBottomTile(cz);

			final float x1 = calc.getPixXFromTile(lt.x, lt.y, cz);
			final float x2 = calc.getPixXFromTile(rb.x, rb.y, cz);
			final float y1 = calc.getPixYFromTile(lt.x, lt.y, cz);
			final float y2 = calc.getPixYFromTile(rb.x, rb.y, cz);

			if (!bufferBitmap.isRecycled()) {
				RectF rct = new RectF(x1, y1, x2, y2);
				pixelValue = bufferBitmap.getPixel(10, 10);
				if (pixelValue == 0) {
					canvas.drawBitmap(bufferBitmapBackup, null, rct, paintImg);
				} else {
					canvas.drawBitmap(bufferBitmap, null, rct, paintImg);
				}
			}

			canvas.rotate(-rot, currentViewport.getCenterPixelX(),
					currentViewport.getCenterPixelY());
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
	private void callRefreshBaseMapInternal(RotatedTileBox tileBox,
			DrawSettings drawSettings,boolean notifyMapTileLayer,String layername) {

		//print("调用refreshBaseMapInternal");

		if (tileBox.getPixHeight() == 0 || tileBox.getPixWidth() == 0) {
//			print("调用refreshBaseMapInternal tileBox.getPixHeight()==0");
			return;
		}

		if (bufferBitmapTmp == null
				|| tileBox.getPixHeight() != bufferBitmapTmp.getHeight()
				|| tileBox.getPixWidth() != bufferBitmapTmp.getWidth()) {

			bufferBitmapTmp = Bitmap.createBitmap(tileBox.getPixWidth(),
					tileBox.getPixHeight(), Config.RGB_565);
		}

//		final QuadPoint c = tileBox.getCenterPixelPoint();
		Canvas canvas = new Canvas(bufferBitmapTmp);

		// 白屏的原因在此处。
		fillCanvas(canvas, drawSettings);//本来是有的，被我注释

		if (bufferBitmap != null) {
			pixelValue = bufferBitmap.getPixel(10, 10);
			if (pixelValue != 0) {
				drawBasemap(canvas);// 如果闪屏，就注释掉此行代码
			}
		}
		String layerName="";
		for (int i = 0; i < layers.size(); i++) {
			try {
				OsmandMapLayer layer = layers.get(i);
				layerName = layer.getLayerName();
//				print("callr::"+layerName+!notifyMapTileLayer);
				
				if(layerName.compareTo(layername)==0){
					//canvas.save();
//					print(" onPrepareBufferImage，通知海图层。"+layerName);
					layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
					//canvas.restore();
				}
				
				

			} catch (IndexOutOfBoundsException e) {
				// skip it
			}
		}

		Bitmap t = bufferBitmap;

		synchronized (this) {
			bufferImgLoc = tileBox;
			bufferBitmap = bufferBitmapTmp;
			bufferBitmapTmp = t;
		}

	}
	
	private void refreshBaseMapInternal(RotatedTileBox tileBox,
			DrawSettings drawSettings,boolean notifyMapTileLayer) {

		//print("调用refreshBaseMapInternal");

		if (tileBox.getPixHeight() == 0 || tileBox.getPixWidth() == 0) {
//			print("调用refreshBaseMapInternal tileBox.getPixHeight()==0");
			return;
		}

		if (bufferBitmapTmp == null
				|| tileBox.getPixHeight() != bufferBitmapTmp.getHeight()
				|| tileBox.getPixWidth() != bufferBitmapTmp.getWidth()) {

			bufferBitmapTmp = Bitmap.createBitmap(tileBox.getPixWidth(),
					tileBox.getPixHeight(), Config.RGB_565);
		}

		final QuadPoint c = tileBox.getCenterPixelPoint();
		Canvas canvas = new Canvas(bufferBitmapTmp);

		// 白屏的原因在此处。
		fillCanvas(canvas, drawSettings);//本来是有的，被我注释

		if (bufferBitmap != null) {
			pixelValue = bufferBitmap.getPixel(10, 10);
			if (pixelValue != 0) {
				drawBasemap(canvas);// 如果闪屏，就注释掉此行代码
			}
		}
		String layerName="";
		for (int i = 0; i < layers.size(); i++) {
			try {
				OsmandMapLayer layer = layers.get(i);
				layerName = layer.getLayerName();
//				print(layerName+!notifyMapTileLayer);
//				if(notifyMapTileLayer && layerName.compareTo("mapTileLayer")==0){
//					canvas.save();
//					layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
//					canvas.restore();
//				}else if(notifyMapTileLayer && layerName.compareTo("greenMapTileLayer")==0){
//						canvas.save();
//						layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
//						canvas.restore();
//					}else if(notifyMapTileLayer && layerName.compareTo("weatherMapTileLayer")==0){
//						canvas.save();
//						layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
//						canvas.restore();
//					}else if(notifyMapTileLayer && layerName.compareTo("waveMapTileLayer")==0){
//						canvas.save();
//						layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
//						canvas.restore();
//					}else if(notifyMapTileLayer && layerName.compareTo("windMapTileLayer")==0){
//						canvas.save();
//						layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
//						canvas.restore();
//					}else if(layerName.compareTo("shipsInfoLayer")==0){
//						canvas.save();
//						layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
//						canvas.restore();
//					}else{
//						continue;
//					}//true 只刷新地图 由地图刷新别的。
				if(!notifyMapTileLayer && layerName.compareTo("mapTileLayer")==0){
//					print("调用： onPrepareBufferImage，但不通知海图层。");
					continue;
				}
				if(!notifyMapTileLayer && layerName.compareTo("greenMapTileLayer")==0){
//					print("调用： onPrepareBufferImage，但不通知green海图层。");
					continue;
				}
				if(!notifyMapTileLayer && layerName.compareTo("weatherMapTileLayer")==0){
//					print("调用： onPrepareBufferImage，但不通知green海图层。");
					continue;
				}
				if(!notifyMapTileLayer && layerName.compareTo("waveMapTileLayer")==0){
//					print("调用： onPrepareBufferImage，但不通知green海图层。");
					continue;
				}
				if(!notifyMapTileLayer && layerName.compareTo("windMapTileLayer")==0){
//					print("调用： onPrepareBufferImage，但不通知green海图层。");
					continue;
				}
				//canvas.save();
//				print(" onPrepareBufferImage，通知海图层。"+layerName);
				layer.onPrepareBufferImage(canvas, tileBox, drawSettings);
				//canvas.restore();

			} catch (IndexOutOfBoundsException e) {
				// skip it
			}
		}

		Bitmap t = bufferBitmap;

		synchronized (this) {
			bufferImgLoc = tileBox;
			bufferBitmap = bufferBitmapTmp;
			bufferBitmapTmp = t;
		}

	}

	// 开始内部的刷新海图
	private void refreshMapInternal(DrawSettings drawSettings) {

		SurfaceHolder holder = getHolder();
		long ms = SystemClock.elapsedRealtime();

		synchronized (holder) {
			Canvas canvas = holder.lockCanvas();
			if (canvas != null) {
				try {
					final float ratioy = mapPosition == OsmandSettings.BOTTOM_CONSTANT ? 0.9f
							: 0.5f;
					final int cy = (int) (ratioy * getHeight());

					if (currentViewport.getPixWidth() != getWidth()
							|| currentViewport.getPixHeight() != getHeight()
							|| currentViewport.getCenterPixelY() != cy) {
						//
						currentViewport.setPixelDimensions(getWidth(),
								getHeight(), 0.5f, ratioy);

						// print("不知道这个函数干嘛的。");
						refreshBufferImage(drawSettings,true);

					}

					RotatedTileBox viewportToDraw = currentViewport.copy();
					// print("refreshMapInternal 调用 drawOverMap，onDraw（）");
					drawOverMap(canvas, viewportToDraw, drawSettings);
				} catch (Exception ex) {
					print(ex.getMessage());
				} finally {
					holder.unlockCanvasAndPost(canvas);
					// print("holder 放开画布。");
				}
			}

		}

	}

	private Bitmap backgroundbitmap;

	private void fillCanvas(Canvas canvas, DrawSettings drawSettings) {
//		 System.out.println("call fill cavas.");

		// if (drawSettings.isNightMode()) {
		// canvas.drawARGB(0, 100, 100, 100);
		// } else {
//		 canvas.drawARGB(200, 200, 200, 200);//100, 200, 200, 200
		canvas.drawARGB(255, 216, 244, 225);

		// }

	}

	private void fillCanvas2(Canvas canvas, DrawSettings drawSettings) {
		canvas.drawARGB(255, 216, 244, 225);
	}

	// this method could be called in non UI thread
	public void refreshMap() {
		// System.err.println("在refreshMap 函数中调用 refreshMap （false）");
		refreshMap(false);
	}

	//
	//long justActivate = 0;

	public boolean lable=true;

	public void refreshMapForceDraw(boolean force,boolean notifyMapTileLayer) {
		if (isShown()) {
//			print("准备刷新所有");
			boolean nightMode = false;
			DrawSettings drawSettings = new DrawSettings(nightMode, false);
				refreshBufferImage(drawSettings,notifyMapTileLayer);// 强制绘制一次海图
			
		}		
	}

	public void callRefreshMapForceDraw(boolean force,boolean notifyMapTileLayer,String layername) {
		if (isShown()) {
//			print("准备刷新所有");
			boolean nightMode = false;
			DrawSettings drawSettings = new DrawSettings(nightMode, false);
				callRefreshBufferImage(drawSettings,notifyMapTileLayer,layername);// 强制绘制一次海图
			
		}		
	}
	// this method could be called in non UI thread
	public void refreshMap(final boolean updateVectorRendering) {
		// print("refreshMap  调用 sendRefreshMapMsg  调用 drawOverMap，onDraw");
		if (isShown()) {
			// System.out.println("is Shown true.");
			boolean nightMode = false;
			DrawSettings drawSettings = new DrawSettings(nightMode,
					updateVectorRendering);
			// System.err.println("refreshMap 函数将要调用 sendRefreshMapMsg 函数.");
			sendRefreshMapMsg(drawSettings, -1);// 发送刷新地图信息

			// 将以下这条语句注释掉，使得在用手指拖动的时候，不会随着拖动而连续的请求海图。当拖动结束时由
			// 触发的相应事件而开始请求和绘制海图。
			// refreshBufferImage(drawSettings);
		}
	}

	public void callPrepareBufferImage(String layername,RotatedTileBox tileBox,boolean notifyMapTileLayer) {
//		print("callPrepareBufferImage 调用 refreshMapForceDraw： "+notifyMapTileLayer+layername);
		refreshMapForceDraw(false,notifyMapTileLayer);		
	
	}
	
	
	public void callPrepareBufferImagebylayer(String layername,RotatedTileBox tileBox,boolean notifyMapTileLayer) {
//		print("callPrepareBufferImage 调用 refreshMapForceDraw： "+notifyMapTileLayer+layername);
		callRefreshMapForceDraw(false,notifyMapTileLayer,layername);		
	
	}

	// 发送刷新地图信息（有delay）
	private void sendRefreshMapMsg(final DrawSettings drawSettings, int delay) {
		if (!handler.hasMessages(MAP_REFRESH_MESSAGE)) {
			Message msg = Message.obtain(handler, new Runnable() {
				@Override
				public void run() {
					try {
						DrawSettings param = drawSettings;
						handler.removeMessages(MAP_REFRESH_MESSAGE);
						refreshMapInternal(param);
					} catch (Exception ex) {
						ex.printStackTrace();
						// print("sendRefreshMapMsg 出错了！ "+ex.getMessage());
					}
				}

			});

			msg.what = MAP_REFRESH_MESSAGE;
			if (delay > 0) {
				handler.sendMessageDelayed(msg, delay);
			} else {
				handler.sendMessageAtFrontOfQueue(msg);
			}
		}
	}

	private void refreshBufferImage(final DrawSettings drawSettings,final boolean notifyMapTileLayer) {
		if (!baseHandler.hasMessages(BASE_REFRESH_MESSAGE)) {
			Message msg = Message.obtain(baseHandler, new Runnable() {
				@Override				
				public void run() {
//					print("refreshBufferImage  ");
					baseHandler.removeMessages(BASE_REFRESH_MESSAGE);
					
					try {
						DrawSettings param = drawSettings;
						refreshBaseMapInternal(currentViewport.copy(), param,notifyMapTileLayer);
						sendRefreshMapMsg(param, 0);
					} catch (Exception e) {
						e.printStackTrace();
						print("refreshBufferImage 出错了！ " + e.getMessage());
					}
				}
			});
			msg.what = BASE_REFRESH_MESSAGE;
			
			baseHandler.sendMessage(msg);
		}
	}
	
	private void callRefreshBufferImage(final DrawSettings drawSettings,final boolean notifyMapTileLayer,final String layername) {
		if (!baseHandler.hasMessages(BASE_REFRESH_MESSAGE)) {
			Message msg = Message.obtain(baseHandler, new Runnable() {
				@Override				
				public void run() {
//					print("refreshBufferImage  ");
					baseHandler.removeMessages(BASE_REFRESH_MESSAGE);
					
					try {
						DrawSettings param = drawSettings;
						callRefreshBaseMapInternal(currentViewport.copy(), param,notifyMapTileLayer,layername);
						sendRefreshMapMsg(param, 0);
					} catch (Exception e) {
						e.printStackTrace();
						print("refreshBufferImage 出错了！ " + e.getMessage());
					}
				}
			});
			msg.what = BASE_REFRESH_MESSAGE;
			
			baseHandler.sendMessage(msg);
		}
	}
	@Override
	public void tileDownloaded(DownloadRequest request) {
		// force to refresh map because image can be loaded from different
		// threads
		// and threads can block each other especially for sqlite images when
		// they
		// are inserting into db they block main thread

		// refreshMap();

		// if (request instanceof TileLoadDownloadRequest) {
		// TileLoadDownloadRequest req = ((TileLoadDownloadRequest) request);
		// print("xtile: "+req.xTile+", ytile: "+req.yTile);
		// }else if(request==null){
		// print("request null....");
		// }
		//
		//
		//print("OSMMapTileView 类中获得 获得瓦片下载成功通知。调用refreshMapForceDraw函数。");

		// print("获得瓦片下载完毕通知，调用refreshMapForceDraw");
		
		refreshMapForceDraw(false,true);

	}

	// ///////////////////////////////// DRAGGING PART
	// ///////////////////////////////////////

	public RotatedTileBox getCurrentRotatedTileBox() {
		return currentViewport;
	}

	public float getDensity() {
		return currentViewport.getDensity();
	}

	public float getScaleCoefficient() {
		float scaleCoefficient = getDensity();
		if (Math.min(dm.widthPixels / (dm.density * 160), dm.heightPixels
				/ (dm.density * 160)) > 2.5f) {
			// large screen
			scaleCoefficient *= 1.5f;
		}
		return scaleCoefficient;
	}

	/**
	 * These methods do not consider rotating
	 */
	protected void dragToAnimate(float fromX, float fromY, float toX,
			float toY, boolean notify) {
//		System.err.println("map tileview drag map!!!");
		float dx = (fromX - toX);
		float dy = (fromY - toY);
		
//		else{
			moveTo(dx, dy);
//			}
	}

	protected void rotateToAnimate(float rotate) {
		if (isMapRotateEnabled()) {
			// System.out.println("in roateToAnimate. isMapRotateEnabled.");
			this.rotate = MapUtils.unifyRotationTo360(rotate);
			currentViewport.setRotate(this.rotate);
			refreshMap();
		}
	}

	protected void rotateToAnimateNew(float rotate) {
		if (isMapRotateEnabled()) {
			//print("roateToAnimate. 调用  refreshMapForceDraw.");
			this.rotate = MapUtils.unifyRotationTo360(rotate);
			currentViewport.setRotate(this.rotate);
			refreshMap();
			refreshMapForceDraw(false,true);
		}
	}

	protected void rotateToAnimateWhileZoomEnd(float rotate) {
		// print("缩放结束，旋转动画，调用刷海图。");
		if (isMapRotateEnabled()) {
			this.rotate = MapUtils.unifyRotationTo360(rotate);
			currentViewport.setRotate(this.rotate);
			if (isShown()) {
				boolean nightMode = false;// application.getDaynightHelper().isNightMode();
				DrawSettings drawSettings = new DrawSettings(nightMode, false);
				// sendRefreshMapMsg(drawSettings, 20);//发送刷新地图信息
				//print("在rotateToAnimateWhileZoomEnd 函数中调用 refreshBufferImage。");
				refreshBufferImage(drawSettings,true);
				// 将这个注释掉，使得在用手指拖动的时候，不会随着拖动而连续的请求海图。当拖动结束时由
				// 触发的相应事件而开始请求和绘制海图。
				// moveTo(1, 1);
			}
		}
	}

	protected void setLatLonAnimate(double latitude, double longitude,
			boolean notify) {
		currentViewport.setLatLonCenter(latitude, longitude);

		// refreshMap();
		//print("setLatLonAnimate 调用 refreshMapForceDraw");
		refreshMapForceDraw(false,true);// yang chun

	}

	protected void setZoomAnimate(int zoom, float zoomScale, boolean notify) {
		//print("setZoomAnimate: "+zoomScale);
		currentViewport.setZoom(zoom, zoomScale, 0);
		// refreshMap();
		//print("setZoomAnimate 调用 refreshMapForceDraw");
		refreshMapForceDraw(false,true);// yang chun

	}

	// for internal usage
	protected void zoomToAnimate(float tzoom, boolean notify) {
		int zoom = getZoom();
		float zoomToAnimate = tzoom - zoom - getZoomScale();
		if (zoomToAnimate >= 1) {
			zoom += (int) zoomToAnimate;
			zoomToAnimate -= (int) zoomToAnimate;
		}
		while (zoomToAnimate < 0) {
			zoom--;
			zoomToAnimate += 1;
		}
		if (mainLayer != null && mainLayer.getMaximumShownMapZoom() >= zoom
				&& mainLayer.getMinimumShownMapZoom() <= zoom) {

			// System.out.println("zoom: "+zoom+", zoomToAnimate: "+zoomToAnimate);
			currentViewport.setZoomAndAnimation(zoom, zoomToAnimate);

			currentViewport
					.setRotate(zoom > LOWEST_ZOOM_TO_ROTATE ? rotate : 0);
		//	print("在 zoomToAnimate 函数中调用 refreshMap 函数。");
			refreshMap();
			// refreshMapForceDraw();
		}
	}

	public void moveTo(float dx, float dy) {
		final QuadPoint cp = currentViewport.getCenterPixelPoint();
		final LatLon latlon = currentViewport.getLatLonFromPixel(cp.x + dx,
				cp.y + dy);
//		else{
		currentViewport.setLatLonCenter(latlon.getLatitude(),
				latlon.getLongitude());
		// System.err.println("在 moveTo 函数中 调用 refreshMap函数。");
		refreshMap();
//		}
		// refreshMapForceDraw();

		// do not notify here listener

	}
	float downx;
	float downy;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 手指刚按下去的事件
//			print("手指刚按下去的事件");
			animatedDraggingThread.stopAnimating();
			lable=false;
			downx=event.getX();
			downy=event.getY();

		}
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
////			print("X"+event.getX()+"Y"+event.getY()+"downx"+downx+"downy"+downy);
////			if((event.getX()-downx)>0||(event.getY()-downy)>0){
//////			ShipLableLayer.clearLayer();
//////			timeLableLayer.clearLayer();
//////			PlotInfoLayer.clearLable();
//////			lable=false;
////			}
//
//		}//自己添加 。。
		for (int i = layers.size() - 1; i >= 0; i--) {
			if (layers.get(i).onTouchEvent(event, getCurrentRotatedTileBox())) {
				return true;
			}
		}

		if (!multiTouchSupport.onTouchEvent(event)) {
			// print("进入gestureDetector.onTouchEvent(event)");

			gestureDetector.onTouchEvent(event);
			multiTouchSupport.setInZoomMode(false);// 这条语句显式的调用函数，设置一个标志值，避免出发onLongPress事件时
			// 出现位置框。
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
//			print("手指放开时的事件。调用refreshMapForceDraw.");
			// 拖动时，手指放开时的事件。开始刷新海图
			// boolean nightMode =
			// application.getDaynightHelper().isNightMode();
//			DrawSettings drawSettings = new DrawSettings(false, false);
			// print("手指放开，调用 refreshMapForceDraw 可能会调用 刷海图。");
			lable=true;
			refreshMapForceDraw(true,true);
//			if(getCurrentRotatedTileBox().getZoom()<6){
//				ShipsInfoLayer.callbuffer();
//			}
//			if(Math.abs(event.getX()-downx)>0||Math.abs(event.getY()-downy)>0){
//				ShipLableLayer.clearLayer();
//				timeLableLayer.clearLayer();
//				PlotInfoLayer.clearLable();
//				}
			// refreshBufferImage(drawSettings);
		}

		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (trackBallDelegate != null) {
			trackBallDelegate.onTrackBallEvent(event);
		}
		return super.onTrackballEvent(event);
	}

	public void setTrackBallDelegate(OnTrackBallListener trackBallDelegate) {
		this.trackBallDelegate = trackBallDelegate;
	}

	public void setOnLongClickListener(OnLongClickListener l) {
		this.onLongClickListener = l;
	}

	public void setOnClickListener(OnClickListener l) {
		this.onClickListener = l;
	}

	// public void setAccessibilityActions(AccessibilityActionsProvider actions)
	// {
	// accessibilityActions = actions;
	// }

	public AnimateDraggingMapThread getAnimatedDraggingThread() {
		return animatedDraggingThread;
	}

	public void showMessage(final String msg) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				AccessibleToast.makeText(getContext(), msg, Toast.LENGTH_SHORT)
						.show(); //$NON-NLS-1$
			}
		});
	}

	// 多点触控时的缩放代码。
	private class MapTileViewMultiTouchZoomListener implements
			MultiTouchZoomListener {
		private PointF initialMultiTouchCenterPoint;
		private RotatedTileBox initialViewport;
		private float x1;
		private float y1;
		private float x2;
		private float y2;
		private LatLon initialCenterLatLon;
		private boolean startRotating = false;
		private static final float ANGLE_THRESHOLD = 20;

		@Override
		public void onZoomEnded(double relativeToStart, float angleRelative) {

			// System.out.println("osmandmaptileview on Zoom ended: ");
			startRotating = true;

			// 1.5 works better even on dm.density=1 devices
			float dz = (float) (Math.log(relativeToStart) / Math.log(2)) * 0.5f;

			int anewZoom = Math.round(dz) + initialViewport.getZoom();
			// System.err.println("当前zoom： "+initialViewport.getZoom()+", 要求放大的新zoom: "+anewZoom);
			if (anewZoom > 17 && ((initialViewport.getZoom()) >= 17)) {
				// System.err.println("已放15了，你还要放大了啊！返回得了！");
				application.getMapActivity()
						.updateZoomButtonStatus(false, true);
				return;
			} else {
				application.getMapActivity().updateZoomButtonStatus(true, true);
			}

			setIntZoom(Math.round(dz) + initialViewport.getZoom());
			if (Math.abs(angleRelative) < ANGLE_THRESHOLD * relativeToStart
					|| Math.abs(angleRelative) < ANGLE_THRESHOLD
							/ relativeToStart) {
				angleRelative = 0;
			}

			angleRelative = 0;// 不要旋转，试试看。

			// rotateToAnimate(initialViewport.getRotate() + angleRelative);
			// System.err.println("在onZoonEnd中调用 refreshBufferImage。");
			// rotateToAnimateWhileZoomEnd(initialViewport.getRotate() +
			// angleRelative);

			final int newZoom = getZoom();

			if (application.accessibilityEnabled()) {
				if (newZoom != initialViewport.getZoom()) {
//					showMessage(getContext().getString(R.string.zoomIs) + " "
//							+ newZoom);
				} else {
//					final LatLon p1 = initialViewport
//							.getLatLonFromPixel(x1, y1);
//					final LatLon p2 = initialViewport
//							.getLatLonFromPixel(x2, y2);
					// showMessage(OsmAndFormatter.getFormattedDistance((float)MapUtils.getDistance(p1.getLatitude(),
					// p1.getLongitude(), p2.getLatitude(), p2.getLongitude()),
					// application));
				}
			}

		}

		@Override
		public void onGestureInit(float x1, float y1, float x2, float y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		@Override
		public void onZoomStarted(PointF centerPoint) {
			initialMultiTouchCenterPoint = centerPoint;
			initialViewport = getCurrentRotatedTileBox().copy();
			initialCenterLatLon = initialViewport.getLatLonFromPixel(
					initialMultiTouchCenterPoint.x,
					initialMultiTouchCenterPoint.y);
			startRotating = false;
		}

		@Override
		public void onZoomingOrRotating(double relativeToStart, float relAngle) {

			startRotating = false;
			double dz = (Math.log(relativeToStart) / Math.log(2)) * 0.5;
			if (Math.abs(dz) <= 0.1) {
				// keep only rotating
				dz = 0;
			}
			if (Math.abs(relAngle) < ANGLE_THRESHOLD && !startRotating) {
				relAngle = 0;
			} else {
				startRotating = true;
			}

			// if(dz != 0 || relAngle != 0) {
			changeZoomPosition((float) dz, relAngle);
			// }

		}

		private void changeZoomPosition(float dz, float angle) {

			final RotatedTileBox calc = initialViewport.copy();
			// 加下面的ifelse判断是为了在最小缩放等级时，再次缩小就返回，在最大缩放等级时，再次放大就返回。
			float calcZoom = initialViewport.getZoom() + dz;
			// System.err.println(initialViewport.getZoom()+", "+calcZoom+", "+initialViewport.getZoomScale());
			if (calcZoom >= mainLayer.getMaximumShownMapZoom()
					|| calcZoom <= mainLayer.getMinimumShownMapZoom()) {
				// System.err.println("return 缩放目标zoom： "+calcZoom+", max: "+mainLayer.getMaximumShownMapZoom()+", min: "+mainLayer.getMinimumShownMapZoom());
				application.getMapActivity()
						.updateZoomButtonStatus(true, false);
				return;
			} else {
				application.getMapActivity().updateZoomButtonStatus(true, true);
			}

			final QuadPoint cp = initialViewport.getCenterPixelPoint();
			float dx = cp.x - initialMultiTouchCenterPoint.x;
			float dy = cp.y - initialMultiTouchCenterPoint.y;

			calcZoom = initialViewport.getZoom() + dz
					+ initialViewport.getZoomScale();

			calc.setLatLonCenter(initialCenterLatLon.getLatitude(),
					initialCenterLatLon.getLongitude());

			float calcRotate = calc.getRotate() + angle;
			calc.setRotate(calcRotate);
			calc.setZoomAnimation(dz);
			final LatLon r = calc.getLatLonFromPixel(cp.x + dx, cp.y + dy);

			setLatLon(r.getLatitude(), r.getLongitude());

			zoomToAnimate(calcZoom, true);

			rotateToAnimate(calcRotate);// 不要旋转

		}

	}

	private class MapTileViewOnGestureListener implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
//			if(multiTouchSupport.getTouchNumber()>2){
//				ShipsInfoLayer.cleartapships();
//			}
			return false;
		}

		//
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// System.out.println("OnGestureListener. onFling");
			// 不要有惯性效果
			 animatedDraggingThread.startDragging(velocityX*0.5f,
			 velocityY*0.5f,
			 e1.getX(), e1.getY(), e2.getX(), e2.getY(), true);

			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// print("在OSM Map Tile View 中检测到了onLongPress事件.");
			
			if (multiTouchSupport.isInZoomMode()) {
				// print("Multi touch support inzoomMode 为true。返回。");
				return;
			}
			
//			ShipsInfoLayer.cleartapships();
			// if (//log.isDebugEnabled()) {
			//				//log.debug("On long click event " + e.getX() + " " + e.getY()); //$NON-NLS-1$ //$NON-NLS-2$
			// }

			PointF point = new PointF(e.getX(), e.getY());
			// if ((accessibilityActions != null) &&
			// accessibilityActions.onLongClick(point,
			// getCurrentRotatedTileBox() )) {
			// //System.err.println("1 On long click event (accessibilityActions.onLongClick)");
			// return;
			// }
			for (int i = layers.size() - 1; i >= 0; i--) {
				if (layers.get(i).onLongPressEvent(point,
						getCurrentRotatedTileBox())) {
					//print("2 On long click event (layers.get(i).onLongPressEvent)");
					return;
				}
			}
			if (onLongClickListener != null
					&& onLongClickListener.onLongPressEvent(point)) {
				//print("3 On long click event (onLongClickListener.onLongPressEvent(point))");
				return;
			}
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
//			System.err.println("map tile view onscroll x::"+distanceX+"distanceY:"+distanceY);
			LatLon LeftTopLatLon=currentViewport.getLeftTopLatLon();
			LatLon RightBottomLatLon=currentViewport.getRightBottomLatLon();
//			QuadPoint cp = currentViewport.getCenterPixelPoint();
//			LatLon latlon1 = currentViewport.getLatLonFromPixel(cp.x,
//					cp.y);
			
//			System.err.println("map tile view drag! LeftTopLatLon"+LeftTopLatLon.getLatitude()+"RightBottomLatLon"+RightBottomLatLon.getLatitude());
			if(LeftTopLatLon.getLatitude()>80.2){
				if(distanceY>0){
//					currentViewport.setLatLonCenter(
//							latlon1.getLatitude(), latlon1.getLongitude()
//							);
					dragToAnimate(e2.getX() + distanceX, e2.getY() + distanceY,
							e2.getX(), e2.getY(), true);
					return true;
				}else{
					dragToAnimate(e2.getX() + distanceX, e2.getY(),
							e2.getX(), e2.getY(), true);
					return true;
				}
				
			}
			if(RightBottomLatLon.getLatitude()<(-83)){
				if(distanceY<0){
//					currentViewport.setLatLonCenter(
//							latlon1.getLatitude(), latlon1.getLongitude()
//							);
					dragToAnimate(e2.getX() + distanceX, e2.getY() + distanceY,
							e2.getX(), e2.getY(), true);
					return true;
				}else{
					dragToAnimate(e2.getX() + distanceX, e2.getY(),
							e2.getX(), e2.getY(), true);
					return true;
				}
				
			}
			
			//自己添加  控制地图移动
			
			
			dragToAnimate(e2.getX() + distanceX, e2.getY() + distanceY,
					e2.getX(), e2.getY(), true);
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			PointF point = new PointF(e.getX(), e.getY());
			for (int i = layers.size() - 1; i >= 0; i--) {
				if (layers.get(i).onSingleTap(point, getCurrentRotatedTileBox())) {
					//print("onSingleTap in OsmapView: " + layers.get(i));
					return true;
				}
			}
			if (onClickListener != null && onClickListener.onPressEvent(point)) {
				//print("onClickListener.onPressEvent(point)");
				return true;
			}
			return false;
		}
	}

	private class MapTileViewOnDoubleTapListener implements OnDoubleTapListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
//			final RotatedTileBox tb = getCurrentRotatedTileBox();
//			final double lat = tb.getLatFromPixel(e.getX(), e.getY());
//			final double lon = tb.getLonFromPixel(e.getX(), e.getY());
			// System.out.println("double tap zoom in in: "+lon+", "+lat);

			int newZoom = getZoom() + 1;
			if (newZoom > mainLayer.getMaximumShownMapZoom()) {
				application.getMapActivity()
						.updateZoomButtonStatus(false, true);
				return false;
			}

			getAnimatedDraggingThread().startMoving(currentViewport.getLatitude(), currentViewport.getLongitude(), newZoom, true);
//			ShipsInfoLayer.cleartapships();
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}
	}

}
