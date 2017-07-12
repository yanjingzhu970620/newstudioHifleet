package com.hifleet.lnfo.layer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.hifleet.bean.ShipsBean;
import com.hifleet.bean.lableBean;
import com.hifleet.lnfo.layer.ShipsInfoLayer.afterShip;
import com.hifleet.map.LatLon;
import com.hifleet.map.MapTileLayer.IMapRefreshCallback;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.RotatedTileBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Executors;

import static android.R.attr.value;

public class ShipLableLayer extends OsmandMapLayer implements
IMapRefreshCallback, afterShip{
	OsmandApplication app;
	DisplayMetrics dm;
	private boolean lableadd = true;
//	public static boolean lablePost = false;
	private final boolean mainMap;
	public RotatedTileBox tileBox;
	protected static OsmandMapTileView view;
	public static List<lableBean> addedlable = new ArrayList<lableBean>();
	public List<lableBean> addedlable1 = new ArrayList<lableBean>();
	public static List<lableBean> countclable = new ArrayList<lableBean>();//用来计算当前屏幕内的lable
	
//	public List<String> xy = new ArrayList<String>();
//	public List<String> xy1 = new ArrayList<String>();
	public List<ShipsBean> tempship = new ArrayList<ShipsBean>();
	public HashMap<String,ShipsBean> tempshipp=new HashMap<String,ShipsBean>();;
	public HashMap<String,ShipsBean> tempshipp2=new HashMap<String,ShipsBean>();;//划分后   所有需要对比的船中    在选中船附近的船
	public List<String> point;
//	public List<ShipsBean> teamShipsBeans;
//	public List<ShipsBean> teamShipsBeans1 = new ArrayList<ShipsBean>();
//	private List<ShipsBean> teamShipsBeans2 = new ArrayList<ShipsBean>();
	public List<Rect> RectList = new ArrayList<Rect>();
	private Paint paint, paint1, paint2;
	private long lastCallAsynTaskTime = 0;
	private double callIntervalLimit1 = 1 * 500L;

	private RotatedTileBox lastQueryTileBox = null;
	private boolean isTheSameTileBox = false;
	public static boolean refreshflag = false;
	private boolean clearlable=false;
	LableThread task;
	private boolean mapr=false;
	public static List<Map<String, String>> checkpoint = new ArrayList<Map<String, String>>();

	public ShipLableLayer(boolean mainMap) {
		this.mainMap = mainMap;
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	public void initLayer(OsmandMapTileView view) {
		// print("创建lablelayer");

		this.view = view;
		app=view.getApplication();
		dm = view.getResources().getDisplayMetrics();
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(3);
//		paint.setAlpha(180);
		paint.setStyle(Style.FILL_AND_STROKE);
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
		
		int ii = dip2px(dm.density,42);
		int jj =dip2px(dm.density,40);
		setCheckPoint(ii, jj);//设置可以选的点的位置。
	}

	public void setLable(HashMap<String, ShipsBean> list1, RotatedTileBox tilebox,LableThread task) {
//        System.out.println("setLable setLable "+list1.size());
//		 long time1=System.currentTimeMillis();
	try{
		HashMap<String, ShipsBean> list=new HashMap<String, ShipsBean>();
		list.putAll(list1);
		
		addedlable = new ArrayList<lableBean>() ;
//		xy.clear();
		tempshipp = new HashMap<String,ShipsBean>();
		// tempshipp.clear();
		// tempshipp.clear();
		 if (list.size() > 60) {
			 Iterator<Entry<String, ShipsBean>> ite = list.entrySet().iterator();
				while(ite.hasNext()){
					Map.Entry entry = (Map.Entry) ite.next();
					String key=entry.getKey().toString();
					ShipsBean value=list.get(key);
					if (value.rs.equals("0")
							|| !app.getMyrole().equals("vvip")
							|| app.getLoginbean().getTraffic().equals("1") ){

					}else{
						continue;
					}
//				justflag++;
				if(task!=null&&task.isCancelled()){
					addedlable.clear();
					print("setlable  时线程取消");
					return;
				}
				
					int lX = tilebox.getPixXFromLonNoRot(
							value.getLo());
					int lY = tilebox.getPixYFromLatNoRot(
							value.getLa());
					double X = Math.floor(lX / dip2px(dm.density,20));
					double Y = Math.floor(lY / dip2px(dm.density,20));
					
					if (!tempshipp.containsKey(X + "_" + Y)) {
//						xy.add(X + "_" + Y);
//						print("格子增加一条船"+(X + "_" + Y)+"  "+xy.size()+"name"+list.get(i).n);
						//tempship.add(list.get(i));
						tempshipp.put(X + "_" + Y, value);
					}
//					tempshipp.put(X + "_" + Y, list.get(i));
			}	
		}else{

			tempshipp.putAll(list);	
		
		}
		// for (int j = 0; j < xy.size(); j++) {
		// tempship.add(tempshipp.get(xy.get(j)));
		// }
		// }
		//
		// else {
		// tempship.addAll(list);
		// }
		// if (teamShipsBeans2.size() > 60) {

//			for (int i1 = 0; i1< teamShipsBeans2.size(); i1++) {
////				justflag1++;
//				if(task!=null&&task.isCancelled()){
//					print("setlable  时线程取消");
//					return;
//				}
//				if (!teamShipsBeans2.get(i1).fle.equals("N/A")
//						&& !teamShipsBeans2.get(i1).fle.equals("0")) {
//					int lX = tilebox.getPixXFromLonNoRot(teamShipsBeans2.get(i1).getLo());
//					int lY = tilebox.getPixYFromLatNoRot(teamShipsBeans2.get(i1).getLa());
//					double X = Math.floor(lX / 40);
//					double Y = Math.floor(lY / 40);
//					
//					if (!xy1.contains(X + "_" + Y)) {
//						xy1.add(X + "_" + Y);
//					//	tempship1.add(teamShipsBeans2.get(i1));
//						tempshipp.put(X + "_" + Y, teamShipsBeans2.get(i1));
////						print(X + "_" + Y);
//					}
//				}
//			}
		// }
		//
		// else {
		// tempship1.addAll(teamShipsBeans2);
		// }
		// long time=System.currentTimeMillis();
//		 print("筛选出的船舶数量  时间花费"+(System.currentTimeMillis()-time1)+"checksize"+checkpoint.size()+"teamShipsBeans2"+teamShipsBeans2.size());

//		for (int s = 0; s < tempship.size(); s++) {
			Iterator<String> it=tempshipp.keySet().iterator();
			while(it.hasNext()){
				if(task!=null&&task.isCancelled()){
					addedlable.clear();
					print("setlable  时线程取消");
					return;
				}
//				long t2=System.currentTimeMillis();
				String key=it.next().toString();
                if (tempshipp.get(key).rs.equals("0")
                        || !app.getMyrole().equals("vvip")
                        || app.getLoginbean().getTraffic().equals("1") ){

                }else{
                    continue;
                }
				tempshipp2 = new HashMap<String,ShipsBean>();
				if(tempshipp.get(key).cname!=null&&!tempshipp.get(key).cname.equals("")
						&&!tempshipp.get(key).cname.equals("-")&&!tempshipp.get(key).cname.equals("N/A")){
					tempshipp.get(key).n=tempshipp.get(key).cname;
				}
				if(tempshipp.get(key).n==null||tempshipp.get(key).n.equals("null")||tempshipp.get(key).n.equals("")
						||tempshipp.get(key).n.equals("-")||tempshipp.get(key).n.equals("N/A")){
					tempshipp.get(key).n=tempshipp.get(key).getM();
//					continue;
				}
				String x_ = null;
				String y_=null;
				if(key.contains("_")){
				String xys[]=key.split("_");
					try{
						x_=xys[0];
						y_=xys[1];
					for(int xx=0;xx<=15;xx++){
						double X=Double.valueOf(x_)-7+xx;
						for(int yy=0;yy<=11;yy++){
							double Y=Double.valueOf(y_)-5+yy;
							if(tempshipp.containsKey(X+"_"+Y)){
							tempshipp2.put(X+"_"+Y, tempshipp.get(X+"_"+Y));
//							print("tempshipp2 add::: X++Y:"+X+"_"+Y+"tempshipp1.get"+tempshipp1.get(X+"_"+Y).n+"time spend "+(System.currentTimeMillis()-t2t));
							}
						}
					}
					}catch (Exception e){
						tempshipp2.putAll(tempshipp);
					}
				}else{
					tempshipp2.putAll(tempshipp);
				}
//					print(x_+"y"+y_+xys.length+"time spend"+(System.currentTimeMillis()-t2));
			// justflag_myteam++;
//			 System.out.println("船舶名称"+tempshipp.get(key).n);
//			if (tempshipp.get(key).n.equals("N/A")||tempshipp.get(key).n.equals("")) {
//				
//				tempshipp.get(key).n=tempshipp.get(key).n;
//			}
			int locationx = tilebox
					.getPixXFromLonNoRot(tempshipp.get(key).getLo());
			int locationy = tilebox
					.getPixYFromLatNoRot(tempshipp.get(key).getLa());
			// ship location
			Rect rect = new Rect();
			paint.setTextSize(dip2px(dm.density,16));
			paint.getTextBounds(tempshipp.get(key).n, 0,
					tempshipp.get(key).n.length(), rect);
			// paint2.setTextSize(30);
//			print("tempshipp.get(key).n:::"+tempshipp.get(key).n.length()+tempshipp.get(key).n);
			int w = rect.width();
			int h = rect.height();
//			Iterator<String> it1=tempshipp.keySet().iterator();
//			while(it1.hasNext()){
//				if(task!=null&&task.isCancelled()){
//					addedlable.clear();
//					print("setlable  时线程取消");
//					return;
//				}
//				String key1=it1.next().toString();
//				String x1_=null;
//				String y1_=null;
//				String xy1s[]=key1.split("_");
//					x1_=xy1s[0];
//					y1_=xy1s[1];
////					print("xx"+x1_+"yy"+y1_+tempshipp.size());
//				double xx=Math.abs(Double.valueOf(x1_)*dip2px(dm.density,10)-Double.valueOf(x_)*dip2px(dm.density,10));
//				double yy=Math.abs(Double.valueOf(y1_)*dip2px(dm.density,10)-Double.valueOf(y_)*dip2px(dm.density,10));
////				if(tempshipp.get(key1).m.equals(tempshipp.get(key).m)){
////				print("xx"+xx+"yy"+yy+tempshipp.get(key1).n+tempshipp.get(key).n);
////				}
//				if(xx<dip2px(dm.density,130)&&yy<dip2px(dm.density,130)){
//					tempshipp2.put(key1, tempshipp.get(key1));
////					if(tempshipp.get(key1).m.equals(tempshipp.get(key).m)){
////						print("比较时加入了自己"+tempshipp.get(key1).n);
////					}
//			}else{
////				print("lable距离太远  不作比较");
//				continue;
//			}
//			}
			// int textWidth = getTextWidth(paint, list.get(s).n);
//			long t0=System.currentTimeMillis();
			for (int i = 0; i < checkpoint.size(); i++) {
//				long t1=System.currentTimeMillis();
				// justflag_point++;
				// System.out.println("选了几个点"+i+"checksize"+checkpoint.size());
				if(task!=null&&task.isCancelled()){
					addedlable.clear();
					print("setlable  时线程取消");
					return;
				}
				boolean addlable = true;
				int x = Integer.parseInt(checkpoint.get(i).get("x"));
				int y = Integer.parseInt(checkpoint.get(i).get("y"));
				// prepare point
				rect.set(locationx + x - ((w + dip2px(dm.density,2)) / 2), locationy + y,
						locationx + x + ((w) / 2), locationy + y + h);

				Iterator<String> it2=tempshipp2.keySet().iterator();
//				print(tempshipp2.size()+"");
				while(it2.hasNext()){
					try{
						if(task!=null&&task.isCancelled()){
							addedlable.clear();
							print("setlable  时线程取消");
							return;
						}
					String key2=it2.next().toString();
//					String x1_=null;
//					String y1_=null;
//					String xy1s[]=key.split("_");
//						x1_=xy1s[0];
//						y1_=xy1s[1];
//					double xx=Math.abs(Double.valueOf(x1_)-Double.valueOf(x_));
//					double yy=Math.abs(Double.valueOf(y1_)-Double.valueOf(y_));
//					if(xx<80&&yy<80){
						try{
					int locationx1 = tilebox.getPixXFromLonNoRot(tempshipp2.get(
							key2).getLo());
					int locationy1 = tilebox.getPixYFromLatNoRot(tempshipp2.get(
							key2).getLa());
					int _x = Math.abs(locationx1 - x - locationx);
					int _y = Math.abs(locationy1 - y - locationy);
//					print(tempshipp2.get(key2).n);
//					 if(tempshipp2.get(key2).m.equals(tempshipp.get(key).m)){
//					 print("这是自己船"+"x距离"+_x+"y距离"+_y+"x限制"+(dip2px(dm.density,18) + w / 2)
//							 +"y限制"+(dip2px(dm.density,18) + h)+tempshipp.get(key).n);
//					 }
//					if(tempshipp2.get(key2).getSp()>0){
					if ((_x >= dip2px(dm.density,18) + w / 2) || (_y >= dip2px(dm.density,18) + h)) {
						continue;
					}
//					}
//					else if(tempshipp2.get(key2).getSp()==0){
//						if ((_x >= dip2px(dm.density,11) + w / 2) || (_y >= dip2px(dm.density,11) + h)) {
//							continue;
//						}
//					}
					}catch(java.lang.NullPointerException nul){
						print("shiplable  tempshipp2  null");
						break;
					}
					addlable = false;
					break;
					}catch( java.util.ConcurrentModificationException juc){
						print("shiplable"+juc);
						break;
					}
				}
				// for (int q = 0; q < addedlable.size(); q++) {
				// int lx = addedlable.get(q).x;
				// int ly = addedlable.get(q).y;
				// int wt = addedlable.get(q).width;
				// if(wt<w){
				// wt=w;
				// }
				// int __x = Math.abs(locationx + x - ((w + 4) / 2) - lx);
				// int __y = Math.abs(locationy + y - ly);
				// // Rect rect2 = new Rect(lx, ly, lx + wt, ly + ht);
				// if ((__x >= wt) || (__y >= h)) {
				// continue;
				// }
				// addlable = false;
				// break;
				// }
				if(!addlable){
					continue;
				}
				for (int q = 0; q < addedlable.size(); q++) {
					// justflag_lable++;
					try{
						if(task!=null&&task.isCancelled()){
							addedlable.clear();
							print("setlable  时线程取消");
							return;
						}
						int wt = addedlable.get(q).width;
//						int ht = addedlable1.get(q).heigth;
						
						int lonx=tilebox
							.getPixXFromLonNoRot(addedlable.get(q).lon);
						int laty=tilebox
							.getPixYFromLatNoRot(addedlable.get(q).lat);
						int lx = lonx + addedlable.get(q).x - ((wt + dip2px(dm.density,2)) / 2);
						int ly = laty + addedlable.get(q).y;
//					int lx = addedlable.get(q).x;
//					int ly = addedlable.get(q).y;
//					int wt = addedlable.get(q).width;
					if (wt < w) {
						wt = w;
					}
					int __x = Math.abs(locationx + x - ((w + dip2px(dm.density,2)) / 2) - lx);
					int __y = Math.abs(locationy + y - ly);
					// Rect rect2 = new Rect(lx, ly, lx + wt, ly + ht);
					if ((__x >= wt) || (__y >= h+dip2px(dm.density,6))) {
						continue;
					}
					addlable = false;
					break;
					}catch(java.lang.NullPointerException nul){
						print("shiplable  addedlable null"+nul);
						break;
					}
				}

				if (addlable) {
					if(task!=null&&task.isCancelled()){
						addedlable.clear();
						print("setlable  时线程取消");
						return;
					}
					lableBean l = new lableBean();
//					l.x = locationx + x - ((w + dip2px(dm.density,2)) / 2);
//					l.y = locationy + y;
					l.x = x;
					l.y = y;
					l.heigth = h;
					l.width = w;
//					l.shipx = locationx;
//					l.shipy = locationy;
					l.lat=tempshipp.get(key).getLa();
					l.lon=tempshipp.get(key).getLo();
//					print("可以显示标签"+tempshipp.get(key).getLa()+tempshipp.get(key).getLo());
					l.n = tempshipp.get(key).n;
					addedlable.add(l);
					// addedlablee.put(s+"", l);
					break;
				}
//				print("check point 一次 标签计算花费的时间"+(System.currentTimeMillis()-t1));
			}
//			print("check point 一艘标签花费的时间：："+(System.currentTimeMillis()-t0));
//			if (s == tempship.size() - 1) {
				// print("可以显示标签");
//				refreshflag = true;
//				clearLayer1();
//			}
		}
			tempshipp.clear();
			if(tempshipp2!=null){
			tempshipp2.clear();
			}
//			xy.clear();
//			teamShipsBeans.clear();
//			checkpoint.clear();
//			teamShipsBeans2.clear();
//		 long time2=System.currentTimeMillis();
//		 print("标签计算花费的时间"+(time2-time1));
			countclable= new ArrayList<lableBean>();
			 countclable.addAll(addedlable);
			 ShipsInfoLayer.addlableInCurrentWindow("");
			refreshflag = true;
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
//		print("清空   lable");
		addedlable.clear();
		// view.callPrepareBufferImage("shiplablelayer");
	}

//	public void clearLayer1() {
//		refreshflag=true;
//		clearlable=true;
//		view.callPrepareBufferImage("shiplablelayer", tileBox, false);
//	}

	public static int getTextWidth(Paint paint, String str) {
		int iRet = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			paint.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				iRet += (int) Math.ceil(widths[j]);
			}
		}
		return iRet;
	}

	// 接收来到MapTileLayer发出的海图刷新结束的通知
	public void mapRefreshed(RotatedTileBox tileBox) {
//		if(lastQueryTileBox!=null&&isTheSameTileBox(tileBox)){
//		if(!app.myPreferences.getBoolean("isShowdot",true)||tileBox.getZoom()>=10){
		refreshflag = true;
//		}
//		}
//		mapr=true;
		// print(" 标签层 收到刷图结束通知。");
		// lableadd = app.myPreferences.getBoolean("isShowMyTeamName", true);
		//
//		 if (lableadd) {
//		 if(tileBox.getZoom()<ShipsInfoLayer.START_SHOW){
//		 callLableAction(tileBox,MyTeamShipsThread.currentLableshipsBeans);
//		 }else{
//		 clearLayer();
//		 }
//		 }
	}

	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {

		this.tileBox = tileBox;
//		if ((tileBox.getZoom()<=9||tileBox.getZoom() >= 14)&&refreshflag&&view.lable) {
//			refreshflag = false;
//			addedlable1 = new ArrayList<lableBean>();
////			if(!clearlable){
//				addedlable1.addAll(ShipsInfoLayer.caddedlable);
////				}else{
////					clearlable=false;
////				}
////			print("执行lable 刷新 " + addedlable1.size());
//			for (int q = 0; q < addedlable1.size(); q++) {
////				int lx = addedlable1.get(q).x;
////				int ly = addedlable1.get(q).y;
//				int wt = addedlable1.get(q).width;
//				int ht = addedlable1.get(q).heigth;
////				int x = addedlable1.get(q).shipx;
////				int y = addedlable1.get(q).shipy;
//				String n = addedlable1.get(q).n;
//				
//				int cx=addedlable1.get(q).x;
//				int cy=addedlable1.get(q).y;
//				int x=tileBox
//					.getPixXFromLonNoRot(addedlable1.get(q).lon);
//				int y=tileBox
//					.getPixYFromLatNoRot(addedlable1.get(q).lat);
//				int lx = x + cx - ((wt + dip2px(dm.density,2)) / 2);
//				int ly = y + cy;
////				print("执行lable 刷新 " + lx+":::"+ly+"x"+x+"y"+y);
//				
//				Rect rect = new Rect(lx, ly, lx + wt, ly + ht+dip2px(dm.density,4));
//				Rect rect2 = new Rect(lx-dip2px(dm.density,1), ly-dip2px(dm.density,1),
//						lx + wt+dip2px(dm.density,1), ly + ht+dip2px(dm.density,4)+dip2px(dm.density,1));
//				canvas.save();
//				if(cx==0){
//					if(cy<0){
//					canvas.drawLine(x, y, lx + wt/2, ly + ht+dip2px(dm.density,4), paint1);
//					}else{
//						canvas.drawLine(x, y, lx + wt/2, ly, paint1);	
//					}
//					
//				}else if(cx>0){
//					if(cy<0){
//						canvas.drawLine(x, y, lx + wt/2, ly + ht+dip2px(dm.density,4), paint1);
//					}else if(cy==0){
//						canvas.drawLine(x, y, lx + wt/2, ly + (ht+dip2px(dm.density,4))/2, paint1);
//					}else if(cy>0){
//						canvas.drawLine(x, y, lx + wt/2, ly, paint1);
//					}
//				}else if(cx<0){
//					if(cy<0){
//						canvas.drawLine(x, y, lx + wt/2, ly + ht+dip2px(dm.density,4), paint1);
//					}else if(cy==0){
//						canvas.drawLine(x, y, lx + wt/2, ly + (ht+dip2px(dm.density,4))/2, paint1);
//					}else if(cy>0){
//						canvas.drawLine(x, y, lx + wt/2, ly, paint1);
//					}
//				}
//				canvas.drawRect(rect, paint);
//				canvas.drawRect(rect2, paint2);
//				paint1.setTextSize(dip2px(dm.density,14));
//				canvas.drawText(n, lx + 4 * dm.density, ly + ht +1
//						* dm.density, paint1);
//				canvas.restore();
//			}
////			if(!isTheSameTileBox(tileBox))
////			clearLayer();
//			// long endlable=System.currentTimeMillis();
//			// print("结束lable"+(endlable-startlable));
//		}
	}

	private HashMap<String, LableThread> asyntaskmap = new HashMap<String, LableThread>();
//	private boolean teamshipfirst = true;//是否是第一次计算。
	public static boolean teamlable=false;//船队异步加载完成  是否需要计算lable。

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
//			print("取消lable计算线程"+uuid);
		}
	}

	// 判断前后两次请求的屏幕范围是否是相同的。返回true就是相同的，返回false就是不同的。
	private boolean isTheSameTileBox(RotatedTileBox tileBox) {
//		if(mapr){
//			mapr=false;
//			return false;
//		}
		if(teamlable){
			teamlable=false;
			return false;
		}
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

	public void callLableAction(RotatedTileBox viewportToDraw,HashMap<String, ShipsBean> currentshipsBeans) {
//		print("计算lable");
		if (lastQueryTileBox == null) {
			// print("lastQueryTileBox null isTheSameTileBox=false. ");
			lastQueryTileBox = viewportToDraw;
			isTheSameTileBox = false;
		} else {

			isTheSameTileBox = isTheSameTileBox(viewportToDraw);
			// print("判断结果： "+isTheSameTileBox);
		}

		if (!isTheSameTileBox) {
//			 print("屏幕范围不同，启动线程计算标签。");
			lastQueryTileBox = viewportToDraw;

			closeReqest();

			String uuid = UUID.randomUUID().toString();
//			clearLayer();
			if(tileBox==null){
				System.err.println("tilebox == null");
				return;
			}
			task = new LableThread(uuid, tileBox,currentshipsBeans);
			asyntaskmap.put(uuid, task);

//			 print("" + uuid + " LabelThread 启动。"+currentshipsBeans.size());

			execute(task);

		}
//		else{
//			refreshflag = true;
//		}

	}

	private void execute(LableThread task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), new String[0]);
		} else {
			task.execute();
		}
	}

	// long startlable;
	class LableThread extends AsyncTask<String, Void, String> {
		String uuid;
		private RotatedTileBox privateTileBox;
//		private ArrayList<ShipsBean> currentshipsBeans;
		HashMap<String, ShipsBean> currentshipsBeans;

		public LableThread(String uuid, RotatedTileBox box,HashMap<String, ShipsBean> currentshipsBeans) {
			// startlable=System.currentTimeMillis();
			this.currentshipsBeans=new HashMap<String, ShipsBean>();
			this.uuid = uuid;
			this.privateTileBox = box;
//			Iterator<Entry<String, ShipsBean>> ite = currentshipsBeans.entrySet().iterator();
//			while(ite.hasNext()){
//				Map.Entry entry = (Map.Entry) ite.next();
//				String key=entry.getKey().toString();
//				ShipsBean value=currentshipsBeans.get(key);
				
			this.currentshipsBeans.putAll(currentshipsBeans);//.add(value);
//			}
			
//			print("起动ship lable thread，准备计算"+currentshipsBeans.size());
		}

		protected String doInBackground(String... arg0) {
//			teamShipsBeans = new ArrayList<ShipsBean>();
////			teamShipsBeans1 = new ArrayList<ShipsBean>();
//			teamShipsBeans2 = new ArrayList<ShipsBean>();
//			if (this.isCancelled())
//				return "";
//			teamShipsBeans.addAll(currentshipsBeans);
//			// teamShipsBeans.addAll(MyTeamShipsThread.shipsBeans);
////			if (currentshipsBeans.size() > 0
////					&& !MyTeamShipsThread.teamshipfirst) {
//////				teamshipfirst = false;
////			}
//			if (this.isCancelled())
//				return "";
////			if(ShipsInfoLayer.all&&privateTileBox!=null){
////			 double minla =
////			 privateTileBox.getRightBottomLatLon().getLatitude();
////			 double maxlo =
////			 privateTileBox.getRightBottomLatLon().getLongitude();
////			 double maxla = privateTileBox.getLeftTopLatLon().getLatitude();
////			 double minlo = privateTileBox.getLeftTopLatLon().getLongitude();
////
////			 for (int i = 0; i < teamShipsBeans.size(); i++) {
////			
////			 if (this.isCancelled())
////			 return "";
////			
////			 double sla = Double.valueOf(teamShipsBeans.get(i).la);
////			 double slo = Double.valueOf(teamShipsBeans.get(i).lo);
//////			
//////			 if (sla > minla
//////			 && sla < maxla
//////			 && slo > minlo
//////			 && slo < maxlo
//////			 && teamShipsBeans.get(i).fle
//////			 .equals(LoginActivity.myFleet)) {
//////			 teamShipsBeans1.add(teamShipsBeans.get(i));
//////			 }
////			 if (sla > minla && sla < maxla && slo > minlo && slo < maxlo) {
////			 teamShipsBeans2.add(teamShipsBeans.get(i));
////			 }
////			 }
//////			 print("执行了all setlable  传入的列表长" + teamShipsBeans.size() + "  "+teamShipsBeans2.size());
////			}else{
////			for (int i = 0; i < teamShipsBeans.size(); i++) {
//
//				if (this.isCancelled())
//					return "";
////				if (teamShipsBeans.get(i).fle.equals(LoginActivity.myFleet)) {
////					teamShipsBeans1.add(teamShipsBeans.get(i));
////				}
//
////			}
//			teamShipsBeans2.addAll(teamShipsBeans);
////			 print("执行了setlable  传入的列表长" + teamShipsBeans.size() +"  "+teamShipsBeans2.size());
////			}
//			if (this.isCancelled())
//				return "";
			setLable(currentshipsBeans, privateTileBox,this);
			if (this.isCancelled())
				return "";
			return null;
		}

		protected void onPostExecute(String result) {
			clearMapByUUID(uuid);
//			lablePost = true;
//			print("标签层异步方法 " + uuid + " 调用 call PrepareBufferImage方法。");
//			if(tileBox.getZoom()<6&&app.myPreferences.getBoolean("isShowdot",
//					true)){
//				view.callPrepareBufferImage("shiplablelayer", privateTileBox, false);
//			}else{
			view.callPrepareBufferImage("shiplablelayer", privateTileBox, true);//刷新一次海图。
//			}
		}

		
	}

	private void setCheckPoint(int i, int j) {
		point= new ArrayList<String>();
		point.add(1 * i + "");
		point.add(-1 * j + "");

		 point.add(0 * i + "");
		 point.add(-1 * j + "");

		point.add(-1 * i + "");
		point.add(-1 * j + "");

		 point.add(-1 * i + "");
		 point.add(0 * j + "");

		point.add(-1 * i + "");
		point.add(1 * j + "");

		 point.add(0 * i + "");
		 point.add(1 * j + "");

		point.add(1 * i + "");
		point.add(1 * j + "");

		 point.add(1 * i + "");
		 point.add(0 * j + "");

		point.add(3/2 * i + "");
		point.add(-1 * j + "");

		point.add(1 * i + "");
		point.add(-3/2 * j + "");

		point.add(0 * i + "");
		point.add(-3/2 * j + "");

		point.add(1 * i + "");
		point.add(1 * j + "");

		point.add(-1 * i + "");
		point.add(-3/2 * j + "");

		point.add(-3/2 * i + "");
		point.add(0 * j + "");

		point.add(-3/2 * i + "");
		point.add(1 * j + "");

		point.add(-1 * i + "");
		point.add(3/2 * j + "");

		point.add(0 * i + "");
		point.add(3/2 * j + "");

		point.add(1 * i + "");
		point.add(3/2 * j + "");

		point.add(3/2 * i + "");
		point.add(1 * j + "");

		point.add(3/2 * i + "");
		point.add(0 * j + "");

		point.add(3/2 * i + "");
		point.add(-3/2 * j + "");

		point.add(-3/2 * i + "");
		point.add(-3/2 * j + "");

		point.add(-3/2 * i + "");
		point.add(3/2 * j + "");

		point.add(3/2 * i + "");
		point.add(3/2 * j + "");

		checkpoint.clear();
		// print("check清空"+checkpoint.size());
		if(point.size()>1){
		for (int mi = 0; mi < point.size() - 1; mi += 2) {
			Map map = new HashMap<String, String>();
			map.put("x", point.get(mi));
			map.put("y", point.get(mi + 1));
			checkpoint.add(map);
		}

		point.clear();
	}
		// print("check完成"+checkpoint.size());
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings settings) {
		// clearLayer();
		this.tileBox=tileBox;

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

	@Override
	public void shipRefresh(RotatedTileBox tileBox,HashMap<String, ShipsBean> currentshipsBeans) {
		// TODO Auto-generated method stub
//		print("收到添加lable通知"+tileBox.getZoom()+view.lable+lableadd);
		lableadd = app.isLabeladd();
		if (lableadd) {
//			if (teamshipfirst) {
//				closeReqest();
//
//				String uuid = UUID.randomUUID().toString();
//
//				LableThread task = new LableThread(uuid, tileBox,currentshipsBeans);
//				asyntaskmap.put(uuid, task);
//
//				print("初始化船队请求完成刷新lable");
//
//				execute(task);
//			}
			if (//((tileBox.getZoom()<=9||tileBox.getZoom() >= 14))&&
					view.lable) {
//				print("收到添加lable通知  进行判断");
				callLableAction(tileBox,currentshipsBeans);
			}
//				else {
//				print("收到添加lable通知  清空lablelayer");
//				ShipsInfoLayer.caddedlable.clear();
//				clearLayer();
////				view.callPrepareBufferImage("shiplablelayer", tileBox, false);
//			}
		} else {
			ShipsInfoLayer.caddedlable.clear();
			clearLayer();
//			view.callPrepareBufferImage("shiplablelayer", tileBox, true);
		}
	}

//	@Override
//	public void dotmapRefreshed(RotatedTileBox tileBox) {
//		// TODO Auto-generated method stub
//		refreshflag = true;
//	}
}
