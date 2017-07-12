package com.hifleet.lnfo.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.hifleet.bean.TraceBean;
import com.hifleet.bean.lableBean;

import com.hifleet.lnfo.layer.TraceInfoLayer.afterTrace;
import com.hifleet.map.LatLon;
import com.hifleet.map.MapTileLayer.IMapRefreshCallback;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.RotatedTileBox;

public class timeLableLayer extends OsmandMapLayer implements
		IMapRefreshCallback,afterTrace {
	OsmandApplication app;
	DisplayMetrics dm;
//	public static boolean lablePost = false;
	private final boolean mainMap;
	public static RotatedTileBox tileBox;
	protected static OsmandMapTileView view;
	public static List<lableBean> addedlable = new ArrayList<lableBean>();
	public List<lableBean> addedlable1 = new ArrayList<lableBean>();
	public List<String> xy = new ArrayList<String>();
	public List<String> xy1 = new ArrayList<String>();
	public List<TraceBean> tempTraceBeans = new ArrayList<TraceBean>();
	public List<String> point ;
	public List<TraceBean> TraceBeans = new ArrayList<TraceBean>();
	public List<Rect> RectList = new ArrayList<Rect>();
	private Paint paint, paint1, paint2;
	// private long lastCallAsynTaskTime = 0;
	// private double callIntervalLimit1 = 1 * 500L;

	private RotatedTileBox lastQueryTileBox = null;
	private boolean isTheSameTileBox = false;
	private static boolean refreshflag = false;
	private static boolean clearlable=false;
	public static List<Map<String, String>> checkpoint = new ArrayList<Map<String, String>>();

	public timeLableLayer(boolean mainMap) {
		this.mainMap = mainMap;
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	public void initLayer(OsmandMapTileView view) {
		// print("创建time lablelayer");
		this.view = view;
		dm = view.getResources().getDisplayMetrics();
		paint = new Paint(); // 设置�?个笔刷大小是3的黄色的画笔
		paint.setColor(Color.WHITE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAlpha(180);
		paint.setStrokeWidth(3);
		paint1 = new Paint();
		paint1.setColor(Color.BLACK);
		paint1.setAntiAlias(true);
		paint1.setStrokeJoin(Paint.Join.ROUND);
		paint1.setStrokeCap(Paint.Cap.ROUND);
		paint1.setStrokeWidth(dip2px(dm.density,3/2));
		paint2 = new Paint();
		paint2.setColor(Color.BLACK);
		paint2.setAntiAlias(true);
		paint2.setStrokeWidth(dip2px(dm.density,3/2));
		paint2.setStyle(Style.STROKE);
		
		int ii = dip2px(dm.density,30);

		int jj = dip2px(dm.density,28);

		setCheckPoint(ii, jj);
	}

	public void setLable(List<TraceBean> list, RotatedTileBox tilebox) {
//		refreshflag = false;
     try{
		addedlable = new ArrayList<lableBean>();
		xy.clear();
		tempTraceBeans = new ArrayList<TraceBean>();
if(list.size()>60){
			for (int i = 0; i < list.size(); i++) {
					int lX = tilebox.getPixXFromLonNoRot(Double.valueOf(list.get(i).getLo()));
					int lY = tilebox.getPixYFromLatNoRot(Double.valueOf(list.get(i).getLa()));
					double X = Math.floor(lX /dip2px(dm.density,10));
					double Y = Math.floor(lY / dip2px(dm.density,10));
					
				if(!xy.contains(X + "_" + Y)){
					xy.add(X + "_" + Y);
					tempTraceBeans.add(list.get(i));
				}

			}
}else{
	tempTraceBeans.addAll(list);
}
//		print("timelable筛选后的点的数量" + tempTraceBeans.size());
		try {
			for (int s = 0; s < tempTraceBeans.size(); s++) {
				// System.out.println("船舶名称"+tempTraceBeans.get(s).cname);
				if(tempTraceBeans.get(s).ti.equals("")||tempTraceBeans.get(s).ti==null){
					continue;
				}
				int locationx = tilebox.getPixXFromLonNoRot(tempTraceBeans.get(s).getLo());
				int locationy = tilebox.getPixYFromLatNoRot(tempTraceBeans.get(s).getLa());
				// ship location
				Rect rect = new Rect();
				paint.setTextSize(dip2px(dm.density,16));
				if (tempTraceBeans.size() > s) {
					paint.getTextBounds(
							tempTraceBeans.get(s).ti.substring(5,
									tempTraceBeans.get(s).ti.length()),
							0,
							tempTraceBeans.get(s).ti.substring(5,
									tempTraceBeans.get(s).ti.length()).length(),
							rect);
				}
				int w = rect.width();
				int h = rect.height();
				// int textWidth = getTextWidth(paint, list.get(s).cname);
				for (int i = 0; i < checkpoint.size(); i++) {
					// System.out.println("选了几个点"+i+"checksize"+checkpoint.size());
					boolean addlable = true;
					int x = Integer.parseInt(checkpoint.get(i).get("x"));
					int y = Integer.parseInt(checkpoint.get(i).get("y"));
					// prepare point
					rect.set(locationx + x - ((w + dip2px(dm.density,2)) / 2), locationy + y,
							locationx + x + ((w) / 2), locationy + y + h);
					for (int s1 = 0; s1 < tempTraceBeans.size(); s1++) {
						int locationx1 = tilebox.getPixXFromLonNoRot(tempTraceBeans.get(s1).getLo());
						int locationy1 = tilebox.getPixYFromLatNoRot(tempTraceBeans.get(s1).getLa());
						if (rect.contains(locationx1, locationy1)) {
							addlable = false;
							break;
						}
					}
					if(!addlable){
						continue;
					}
					for (int q = 0; q < addedlable.size(); q++) {
						int wt = addedlable.get(q).width;
						int ht = addedlable.get(q).heigth;
						int lox = tilebox.getPixXFromLonNoRot(addedlable.get(q).lon);
						int loy = tilebox.getPixYFromLatNoRot(addedlable.get(q).lat);
						int lx = lox + addedlable.get(q).x - ((wt + dip2px(dm.density,2)) / 2);
						int ly = loy + addedlable.get(q).y;
						if (w > wt) {
							wt = w;
						}
						int __x = Math.abs(locationx + x - ((w + dip2px(dm.density,2)) / 2) - lx);
						int __y = Math.abs(locationy + y - ly);
						if ((__x > wt) || (__y > ht+dip2px(dm.density,6))) {
							continue;
						}
						addlable = false;
						break;
					}
					if (addlable && tempTraceBeans.size() > s) {
						lableBean l = new lableBean();
//						l.x = locationx + x - ((w + dip2px(dm.density,2)) / 2);
//						l.y = locationy + y;
						l.x =x;
						l.y =y;
						l.heigth = h;
						l.width = w;
//						l.shipx = locationx;
//						l.shipy = locationy;
						l.lat=tempTraceBeans.get(s).getLa();
						l.lon=tempTraceBeans.get(s).getLo();
						l.n = tempTraceBeans.get(s).ti.substring(5,
								tempTraceBeans.get(s).ti.length());
						addedlable.add(l);
						break;
					}
				}
//				if (s == tempTraceBeans.size() - 1) {
					// print("可以显示标签");
//					refreshflag = true;
//				}
//				clearLayer1();
			}
			refreshflag = true;
		} catch (java.lang.IndexOutOfBoundsException jie) {
			System.out.println("IndexOutOfBoundsException timelable");
			return;
		}
     }catch(Exception e){
 		System.err.println("refreshshipslayer singletap exception"+e);
 	}

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
	public static void clearLayer() {
		addedlable.clear();
		// view.callPrepareBufferImage("shiplablelayer");
	}

//	public static void clearLayer1() {
//		refreshflag=true;
//		clearlable=true;
//		view.callPrepareBufferImage("shiplablelayer",tileBox,false);
//	}

	// 接收来到MapTileLayer发出的海图刷新结束的通知
	public void mapRefreshed(RotatedTileBox tileBox) {
//		//print(" 标签层 收到刷图结束通知。");
//		callLableAction(tileBox);
//		
       refreshflag=true;
	}

	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {

		this.tileBox = tileBox;
		if(refreshflag){
			refreshflag=false;//控制计算完成前的刷新不执行
			addedlable1 = new ArrayList<lableBean>();
//		if(!clearlable){
		addedlable1.addAll(addedlable);
//		}else{
//			clearlable=false;
//		}
		print("执行timelable 刷新 " + addedlable1.size());
		paint2.setAntiAlias(true);
		for (int q = 0; q < addedlable1.size(); q++) {
			int wt = addedlable1.get(q).width;
			int ht = addedlable1.get(q).heigth;
			
			int cx=addedlable1.get(q).x;
			int cy=addedlable1.get(q).y;
			int x = tileBox.getPixXFromLonNoRot(addedlable1.get(q).lon);
			int y = tileBox.getPixYFromLatNoRot(addedlable1.get(q).lat);
			int lx = x + cx - ((wt + dip2px(dm.density,2)) / 2);
			int ly = y + cy;
//			int lx = addedlable1.get(q).x;
//			int ly = addedlable1.get(q).y;
//			int x = addedlable1.get(q).shipx;
//			int y = addedlable1.get(q).shipy;
			String n = addedlable1.get(q).n;
			Rect rect = new Rect(lx, ly, lx + wt, ly + ht+dip2px(dm.density,4));
			Rect rect2 = new Rect(lx-dip2px(dm.density,1), ly-dip2px(dm.density,1),
					lx + wt+dip2px(dm.density,1), ly + ht+dip2px(dm.density,4)+dip2px(dm.density,1));
			canvas.save();
			
			if(cx==0){
				if(cy<0){
				canvas.drawLine(x, y, lx + wt/2, ly + ht+dip2px(dm.density,4), paint1);
				}else{
					canvas.drawLine(x, y, lx + wt/2, ly, paint1);	
				}
				
			}else if(cx>0){
				if(cy<0){
					canvas.drawLine(x, y, lx + wt/2, ly + ht+dip2px(dm.density,4), paint1);
				}else if(cy==0){
					canvas.drawLine(x, y, lx + wt/2, ly + (ht+dip2px(dm.density,4))/2, paint1);
				}else if(cy>0){
					canvas.drawLine(x, y, lx + wt/2, ly, paint1);
				}
			}else if(cx<0){
				if(cy<0){
					canvas.drawLine(x, y, lx + wt/2, ly + ht+dip2px(dm.density,4), paint1);
				}else if(cy==0){
					canvas.drawLine(x, y, lx + wt/2, ly + (ht+dip2px(dm.density,4))/2, paint1);
				}else if(cy>0){
					canvas.drawLine(x, y, lx + wt/2, ly, paint1);
				}
			}
			
//			canvas.drawLine(locationx, locationy, lx + wt / 2, ly + ht / 2, paint1);
			canvas.drawRect(rect, paint);
			canvas.drawRect(rect2, paint2);
			paint1.setTextSize(dip2px(dm.density,14));
			canvas.drawText(n, lx + 4 * dm.density, ly + ht + 1
					* dm.density, paint1);
			canvas.restore();
		}
//		clearLayer();
		 }
	}

	private HashMap<String, LableThread> asyntaskmap = new HashMap<String, LableThread>();

	private void clearMapByUUID(String uuid) {
		asyntaskmap.remove(uuid);
	}

	private void closeReqest() {
		if (asyntaskmap.isEmpty())
			return;

		Iterator<String> it = asyntaskmap.keySet().iterator();

		while (it.hasNext()) {
			String uuid = it.next();
			LableThread task = asyntaskmap.get(uuid);
			task.cancel(true);
		}
	}

	// 判断前后两次请求的屏幕范围是否是相同的。返回true就是相同的，返回false就是不同的。
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

	// long justCallTime = 0, thisCall = 0;
	public void callLableAction(RotatedTileBox viewportToDraw) {
		// thisCall = System.currentTimeMillis();
		// long timeGap = thisCall - justCallTime;

		if (lastQueryTileBox == null) {
			// print("lastQueryTileBox null isTheSameTileBox=false. ");
			lastQueryTileBox = viewportToDraw;
			isTheSameTileBox = false;
		} else {

			isTheSameTileBox = isTheSameTileBox(viewportToDraw);
			// print("判断结果： "+isTheSameTileBox);
		}

		if (!isTheSameTileBox) {

			 print("屏幕范围不同，启动timelable线程计算标签。");
			lastQueryTileBox = viewportToDraw;

			closeReqest();

			String uuid = UUID.randomUUID().toString();
//			clearLayer();
			LableThread task = new LableThread(uuid, viewportToDraw);
			asyntaskmap.put(uuid, task);

			// print("" + uuid + " LabelThread 启动。");

			execute(task);

//		}else{
//			refreshflag=true;
		}

	}

	private void execute(LableThread task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
		} else {
			task.execute(new Void[0]);
		}
	}

	class LableThread extends AsyncTask<Void, String, String> {
		String uuid;
		private RotatedTileBox privateTileBox;
		private Canvas privateCanvas;

		public LableThread(String uuid, RotatedTileBox box) {
			this.uuid = uuid;
			this.privateTileBox = box;
//			print("启动LableThread" );
		}

		@Override
		protected String doInBackground(Void... arg0) {
			TraceBeans.clear();
			if (this.isCancelled())
				return "";
			TraceBeans.addAll(TraceInfoLayer.currentTraceBeans);
			if (this.isCancelled())
				return "";
			print("执行了setlable  传入的列表长" + TraceBeans.size());

			setLable(TraceBeans, privateTileBox);
			return null;
		}

		protected void onPostExecute(String result) {
			clearMapByUUID(uuid);
//			lablePost = true;
			print("time标签层异步方法 " + uuid + " 调用 call PrepareBufferImage方法。");
			view.callPrepareBufferImage("timelablelayer", privateTileBox, true);
		}
	}

	private void setCheckPoint(int i, int j) {
		point= new ArrayList<String>();
		point.add(1 * i + "");
		point.add(-1 * j + "");

//		point.add(0 * i + "");
//		point.add(-1 * j + "");

		point.add(-1 * i + "");
		point.add(-1 * j + "");

//		point.add(-1 * i + "");
//		point.add(0 * j + "");

		point.add(-1 * i + "");
		point.add(1 * j + "");

//		point.add(0 * i + "");
//		point.add(1 * j + "");

		point.add(1 * i + "");
		point.add(1 * j + "");

//		point.add(1 * i + "");
//		point.add(0 * j + "");

		point.add(1 * i + "");
		point.add(1 * j + "");

		point.add(3/2 * i + "");
		point.add(-3/2 * j + "");

//		point.add(0 * i + "");
//		point.add(-2 * j + "");

		point.add(-3/2 * i + "");
		point.add(-3/2 * j + "");

//		point.add(-2 * i + "");
//		point.add(0 * j + "");

		point.add(-3/2 * i + "");
		point.add(3/2 * j + "");

//		point.add(0 * i + "");
//		point.add(2 * j + "");

		point.add(3/2 * i + "");
		point.add(3/2 * j + "");

//		point.add(2 * i + "");
//		point.add(0 * j + "");

		checkpoint.clear();

		for (int mi = 0; mi < point.size(); mi += 2) {
			Map map = new HashMap<String, String>();
			map.put("x", point.get(mi));
			map.put("y", point.get(mi + 1));
			checkpoint.add(map);
		}
		point.clear();
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings settings) {
		// clearLayer();

	}

	@Override
	public void destroyLayer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean drawInScreenPixels() {
		// TODO Auto-generated method stub
		return false;
	}


//	@Override
	public void traceRefreshed(RotatedTileBox tileBox) {
		// TODO Auto-generated method stub
//		if(tileBox.getZoom()<ShipsInfoLayer.START_SHOW){
			callLableAction(tileBox);
//			}else{
//				clearLayer();
//			}
	}


}
