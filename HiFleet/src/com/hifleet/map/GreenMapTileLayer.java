package com.hifleet.map;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.hifleet.map.TileSourceManager.TileSourceTemplate;

@SuppressLint("WrongCall")
public class GreenMapTileLayer extends BaseMapLayer {

	OsmandApplication app;
	protected static final int emptyTileDivisor = 16;
	public static final int OVERZOOM_IN = 0;//2;
	
	private final boolean mainMap;
	protected ITileSource map = null;
	protected MapTileAdapter mapTileAdapter = null;
	
	Paint paintBitmap;
	
	protected RectF bitmapToDraw = new RectF();
	
	protected Rect bitmapToZoom = new Rect();
	

	protected OsmandMapTileView view;
	protected ResourceManager resourceManager;
	private OsmandSettings settings;
	private boolean visible = true;
	private boolean flag=true;
	
	public GreenMapTileLayer(boolean mainMap){
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

		paintBitmap = new Paint();
		//paintBitmap.setFilterBitmap(true);
		//paintBitmap.setAlpha(200);//getAlpha());
		//paintBitmap.setStyle(Paint.Style.STROKE);
//<<<<<<< .mine
//		paintBitmap.setFilterBitmap(true);
//		paintBitmap.setAlpha(getAlpha());
//=======
		//paintBitmap.setFilterBitmap(true);
		paintBitmap.setAlpha(getAlpha());
		paintBitmap.setStyle(Paint.Style.STROKE);
		
		if(mapTileAdapter != null && view != null){
			mapTileAdapter.initLayerAdapter(this, view);
		}		
	}
	
	@Override
	public void setAlpha(int alpha) {
		super.setAlpha(alpha);
		if (paintBitmap != null) {
			paintBitmap.setAlpha(alpha);
		}
	}
	
	public void setMapTileAdapter(MapTileAdapter mapTileAdapter) {
		if(this.mapTileAdapter == mapTileAdapter){
			return;
		}
		if(this.mapTileAdapter != null){
			this.mapTileAdapter.onClear();
		}
		this.mapTileAdapter = mapTileAdapter;
		if(mapTileAdapter != null && view != null){
			mapTileAdapter.initLayerAdapter(this, view);
			mapTileAdapter.onInit();
		}
	}
	
	public void setMapForMapTileAdapter(ITileSource map, MapTileAdapter mapTileAdapter) {
		//System.err.println("setMapForMapTileAdapter in MapTileLayer.");
		if(mapTileAdapter == this.mapTileAdapter){
			this.map = map;
		}
	}
	
	public void setMap(ITileSource map) {
		//System.out.println("setMap in MapTileLayer.");
		MapTileAdapter target = null;
		if(map instanceof TileSourceTemplate){
//			if(TileSourceManager.RULE_YANDEX_TRAFFIC.equals(((TileSourceTemplate) map).getRule())){
//				map = null;
//				target = new YandexTrafficAdapter();
//			}
			
		}
		this.map = map;
		setMapTileAdapter(target);
	}
	
	public MapTileAdapter getMapTileAdapter() {
		return mapTileAdapter;
	}
	private static void print(String msg){
        Log.i(TAG, msg);
    }
 public static final String TAG = "FileDownloader";
	
	@Override
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		if(app.myPreferences.getBoolean("isShowdot",
				false)&&tileBox.getZoom()<10){
		//System.out.println("MapTile layer 中onPrepareBufferImage函数。");
		if ((map == null && mapTileAdapter == null) || !visible) {			
			return;
		}
		if(mapTileAdapter != null){
			//print("海图层 onPrepareBufferImage 中调用 onDraw函数。");
			mapTileAdapter.onDraw(canvas, tileBox, drawSettings);
		}
		

		//print("MapTileLayer.onPrepareBufferImage: "+tileBox.getLongitude()+", "+tileBox.getLatitude());
		//print("刷海图");
		drawTileMap(canvas, tileBox);
		}else if(tileBox.getZoom()<6){
//			for(DMapRefreshCallback call :new ArrayList<DMapRefreshCallback>(calls)){
//				call.dotmapRefreshed(tileBox);
//			}	
		}
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings drawSettings) {
		//print("海图层 onDraw函数。");
	}
	
	public void drawTileMap(Canvas canvas, RotatedTileBox tileBox) {
//		System.err.println("  green   MapTileLayer draw tile map ?"+this.map.getName()+"getExpirationTimeMillis()"+map.getExpirationTimeMillis());		
		ITileSource map = this.map;
		
		if(map == null){			
			return;
		}
		
		ResourceManager mgr = resourceManager;
		
		int nzoom = tileBox.getZoom();
		final QuadRect tilesRect = tileBox.getTileBounds();
		
		// recalculate for ellipsoid coordinates
		float ellipticTileCorrection  = 0;
		if (map.isEllipticYTile()) {
			ellipticTileCorrection = (float) (MapUtils.getTileEllipsoidNumberY(nzoom, 
					tileBox.getLatitude()) - tileBox.getCenterTileY());
		}


		int left = (int) Math.floor(tilesRect.left);
		int top = (int) Math.floor(tilesRect.top + ellipticTileCorrection);
		int width = (int) Math.ceil(tilesRect.right - left);
		int height = (int) Math.ceil(tilesRect.bottom + ellipticTileCorrection - top);

		//是否使用互联网，true
		boolean useInternet = settings.USE_INTERNET_TO_DOWNLOAD_TILES.get()
					&& settings.isInternetConnectionAvailable() && map.couldBeDownloadedFromInternet();
		
		
		int maxLevel = 16;//Math.min(view.getSettings().MAX_LEVEL_TO_DOWNLOAD_TILE.get(), map.getMaximumZoomSupported());
		
		
		int tileSize = map.getTileSize();
		
		boolean oneTileShown = false;
//           needmap=new ArrayList<String>();
           
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				
				
				int leftPlusI = left + i;
				int topPlusJ = top + j;

				int x1 = tileBox.getPixXFromTileXNoRot(leftPlusI);
				int x2 = tileBox.getPixXFromTileXNoRot(leftPlusI + 1);

				int y1 = tileBox.getPixYFromTileYNoRot(topPlusJ -  ellipticTileCorrection);
				int y2 = tileBox.getPixYFromTileYNoRot(topPlusJ + 1 -  ellipticTileCorrection);
				
				bitmapToDraw.set(x1, y1, x2 , y2);
				

				int tileX = leftPlusI;
				int tileY = topPlusJ;	
				int limit=1<<nzoom;
				if(tileY<0||tileY>=limit||tileX<0||tileX>=limit){
					if(tileX<0){
						tileX+=limit;
					}
					if(tileX>=limit){
						tileX=tileX%limit;
					}
					
//					if(tileY<0){
//						tileY+=limit;
//					}
//					if(tileY>=limit){
//						tileY=tileY%limit;
//					}
					
				}
				
				Bitmap bmp = null;				
				String ordImgTile = mgr.calculateTileId(map, tileX, tileY, nzoom);
				boolean imgExist;
//				if(MapActivity.mapselect.equals("land")){
				imgExist = mgr.tileExistOnFileSystem(ordImgTile, this.map, tileX, tileY, nzoom);
//				}else{
//					imgExist=false;
//				}
				
				ordImgTile = mgr.getFilePathAfterCheckTileExistence();
//				needmap.add(ordImgTile);
				
				if(imgExist){
					map = mgr.getTheExactTileSource();				
				}	
				
				boolean originalWillBeLoaded = useInternet && nzoom <= maxLevel;

				if (imgExist || originalWillBeLoaded) {					
					
					if(!imgExist){
						map =this.map; 
					}					
					bmp = mgr.getTileImageForMapAsync(ordImgTile, map, tileX, tileY, nzoom, useInternet);
				}
				
				if (bmp == null) {
					//print("bmp null 没能从缓存中获得图："+ordImgTile);
//					needmap.add(ordImgTile);
					int div = 1;
					boolean readFromCache = originalWillBeLoaded || imgExist;
					boolean loadIfExists = !readFromCache;
					// asking if there is small version of the map (in cache)
					int allowedScale = Math.min(OVERZOOM_IN + Math.max(0, nzoom - map.getMaximumZoomSupported()), 8);
					//print("allowedScale: "+allowedScale);
					int kzoom = 1;
					for (; kzoom <= allowedScale; kzoom++) {
						div *= 2;
						String imgTileId = mgr.calculateTileId(map, tileX / div, tileY / div, nzoom - kzoom);
//						print("要的图的imgTileId是： "+imgTileId);
						if (readFromCache) {
//							print("mgr.getTileImageFromCache");
							bmp = mgr.getTileImageFromCache(imgTileId);
							if (bmp != null) {
							//	print("bmp != null. break;");
								break;
							}
						} else if (loadIfExists) {
						//	print("else if loadIfExists true...");
							if (mgr.tileExistOnFileSystem(imgTileId, map, tileX / div, tileY / div, nzoom - kzoom) 
									|| (useInternet && nzoom - kzoom <= maxLevel)) {
//								print("异步调用请求图。2");
								bmp = mgr.getTileImageForMapAsync(imgTileId, map, tileX / div, tileY / div, nzoom
										- kzoom, useInternet);
							//	print("break;;;;");
								break;
							}
						}

					}					
					
					if (bmp != null) {
						//print("find bmp in filesystem. bmp != null @ MapTileLayer."+ordImgTile);
						int xZoom = (tileX % div) * tileSize / div;
						int yZoom = (tileY % div) * tileSize / div;
						// nice scale
						boolean useSampling = kzoom > 4;
						int margin = useSampling ? 1 : 0;
						bitmapToZoom.set(Math.max(xZoom - margin, 0), 
								Math.max(yZoom - margin , 0), 
								Math.min(margin + xZoom + tileSize / div, tileSize), 
								Math.min(margin + yZoom + tileSize / div, tileSize));
						if(!useSampling) {
//							print("不要 useSampling");
							canvas.drawBitmap(bmp, bitmapToZoom, bitmapToDraw, paintBitmap);
						} else {
//							print("useSampling，every expensive");
							int scaledSize = tileSize / div;
							RectF src = new RectF(0.5f, 0.5f,
									scaledSize + 2 * margin - 0.5f, scaledSize + 2 * margin - 0.5f);
							RectF dest = new RectF(0, 0, tileSize, tileSize);
			                Matrix m = new Matrix();
			                m.setRectToRect(src, dest, Matrix.ScaleToFit.FILL);
			                
			                Bitmap s = Bitmap.createBitmap(scaledSize + 2 * margin - 1, 
									scaledSize + 2 * margin - 1, Config.ARGB_8888);
			                Bitmap sampled=  s.createBitmap(bmp, bitmapToZoom.left, 
									bitmapToZoom.top,									
									scaledSize + 2 * margin - 1, 
									scaledSize + 2 * margin - 1, 
									m, 
									true);
							bitmapToZoom.set(0, 0, tileSize, tileSize);
							// very expensive that's why put in the cache
							mgr.putTileInTheCache(ordImgTile, sampled);							
							canvas.drawBitmap(sampled, bitmapToZoom, bitmapToDraw, paintBitmap);
						}
					}
				} else {
					bitmapToZoom.set(0, 0, tileSize, tileSize);	

					canvas.drawBitmap(eraseColor(bmp), bitmapToZoom, bitmapToDraw, paintBitmap);
				}
				
				if(bmp != null) {
					oneTileShown = true;
				}
			}
		}	
//		print("刷greenmap图结束*************发出通知 ");	
//		for(DMapRefreshCallback call :new ArrayList<DMapRefreshCallback>(calls)){
//			call.dotmapRefreshed(tileBox);
//		}
	}
	
	private Bitmap eraseColor(Bitmap src){
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap b = src.copy(Config.ARGB_8888, true);
		b.setHasAlpha(true);

		int[] pixels = new int[width * height];
		src.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int i = 0; i < width * height; i++) {
		    if (pixels[i] == -16777216) {
		        pixels[i] = 0;
		    }
		}

		b.setPixels(pixels, 0, width, 0, 0, width, height);

		return b;
	}
	
	
//	private List<DMapRefreshCallback> calls = new ArrayList<DMapRefreshCallback>();
//	
//	public interface DMapRefreshCallback{
//		public void dotmapRefreshed(RotatedTileBox tileBox);
//	}
//	
//	public void addMapRefreshCallsback(DMapRefreshCallback callback){
//		calls.add(callback);
//	}
	
	public int getSourceTileSize() {
		return map == null ? 256 : map.getTileSize();
	}
	
	
	@Override
	public int getMaximumShownMapZoom(){
		if(map == null){
			return 16;
		} else {
			//System.err.println("MapTileLayer max zoom: "+map.getMaximumZoomSupported()+", overzoom_in: "+OVERZOOM_IN);
			return map.getMaximumZoomSupported() + OVERZOOM_IN;
		}
	}
	
	@Override
	public int getMinimumShownMapZoom(){
		if(map == null){
			return 2;
		} else {
			//System.err.println("MapTileLayer min zoom: "+map.getMinimumZoomSupported()+", map name: "+map.getName());
			return map.getMinimumZoomSupported();
		}
	}//不是mainlayer
		
	@Override
	public void destroyLayer() {
		// TODO clear map cache
		setMapTileAdapter(null);
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
		// TODO clear map cache
	}
	
	public ITileSource getMap() {
		return map;
	}
	

}
