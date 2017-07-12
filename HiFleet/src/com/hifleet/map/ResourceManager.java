package com.hifleet.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import net.sf.junidecode.App;

import org.apache.commons.logging.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hifleet.plus.R;
import com.hifleet.data.DownloadActivityType;
import com.hifleet.data.IndexFileList;
import com.hifleet.data.IndexItem;
import com.hifleet.data.SQLiteTileSource;
import com.hifleet.map.AsyncLoadingThread.TileLoadDownloadRequest;
import com.hifleet.map.MapTileDownloader.DownloadRequest;
import com.hifleet.map.OfflineDataRegion;
import com.hifleet.utility.GifDecoder;

/**
 * Resource manager is responsible to work with all resources that could consume
 * memory (especially with file resources). Such as indexes, tiles. Also it is
 * responsible to create cache for that resources if they can't be loaded fully
 * into memory & clear them on request.
 */
public class ResourceManager {

	public static final String VECTOR_MAP = "#vector_map"; //$NON-NLS-1$
	private static final String INDEXES_CACHE = "ind.cache";
	private static final String CHART_MAP_SOURCE_NAME = "海图";

	private static final Log log = PlatformUtil.getLog(ResourceManager.class);

	protected static ResourceManager manager = null;

	// it is not good investigated but no more than 64 (satellite images)
	// Only 8 MB (from 16 Mb whole mem) available for images : image 64K * 128 =
	// 8 MB (8 bit), 64 - 16 bit, 32 - 32 bit
	// at least 3*9?
	protected int maxImgCacheSize = 6;// 28;

	protected Map<String, Bitmap> cacheOfImages = new LinkedHashMap<String, Bitmap>();
	protected Map<String, Boolean> imagesOnFS = new LinkedHashMap<String, Boolean>();

	protected File dirWithTiles;

	private final OsmandApplication context;

	public interface ResourceWatcher {

		public boolean indexResource(File f);

		public List<String> getWatchWorkspaceFolder();
	}

	protected final Map<String, String> indexFileNames = new ConcurrentHashMap<String, String>();

	protected final Map<String, String> basemapFileNames = new ConcurrentHashMap<String, String>();

	protected final MapTileDownloader tileDownloader;

	public final AsyncLoadingThread asyncLoadingThread = new AsyncLoadingThread(
			this);
	private HandlerThread renderingBufferImageThread;

	protected boolean internetIsNotAccessible = false;
	private OsmandSettings settings;

	private HashMap<String, OfflineDataRegion> offlineDataRegionMapping = null;

	public void setOfflineDataRegionMapping(String name,
			OfflineDataRegion region) {
		if (offlineDataRegionMapping == null) {
			offlineDataRegionMapping = new HashMap<String, OfflineDataRegion>();
		}
		offlineDataRegionMapping.put(name, region);
	}

	public IndexFileList getIndexFileListFromAssets(){
		IndexFileList result = new IndexFileList();
		try{			
			java.io.InputStream ins =context.getAssets().open("indexes.xml");
			InputStreamReader isr=null;	
			//isr=new InputStreamReader(new FileInputStream(indexesFile),"gb2312");
			isr=new InputStreamReader(ins,"utf-8");
			java.io.BufferedReader br = new java.io.BufferedReader(isr);
			
			String s;
			String str;
			StringBuffer stringbuffer = new StringBuffer();
			while( (s =br.readLine())!=null){
				stringbuffer.append(s);
			}
			str = new String(stringbuffer);
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(new StringReader(str));
			int next;
			
			
			while((next = parser.next()) != XmlPullParser.END_DOCUMENT) {
				if (next == XmlPullParser.START_TAG) {
					DownloadActivityType tp = getIndexType(parser.getName());
					if (tp != null) {
						String name = parser.getAttributeValue(null, "filename")+".sqlitedb"; //$NON-NLS-1$
						String filename1 = parser.getAttributeValue(null, "filename");
						String size = parser.getAttributeValue(null, "size"); //$NON-NLS-1$
						String date = parser.getAttributeValue(null, "date"); //$NON-NLS-1$
						String description = parser.getAttributeValue(null, "description"); //$NON-NLS-1$
						System.err.println("assets 离线包文件名�?"+new String(name.getBytes("ISO_8859_1"),"gb2312"));
						String rlon = parser.getAttributeValue(null,"rlon");
						String rlat = parser.getAttributeValue(null,"rlat");
						String llon = parser.getAttributeValue(null,"llon");
						String llat = parser.getAttributeValue(null,"llat");
						String id = parser.getAttributeValue(null,"id");
						date = reparseDate(context, date);
						IndexItem it = new IndexItem(name, description, date, size,rlon,rlat,llon,llat,filename1);
						it.setType(tp);
						it.setId(id);
						result.add(it);
					} 
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	//	print("从assets中获得了indexes.xml文件�?);
		return result;
	}
	
	private void readOfflineDataRegionMappingFromFileSystem(){
		if(offlineDataRegionMapping!=null){
			return;
		}
		InputStreamReader isr=null;	
		
		try{
			boolean downloadedXMLOnline=false;
			java.io.InputStream ins = null;
			File indexesFile = new File(dirWithTiles,"indexes.xml");
			if(!indexesFile.exists()) {
				//print("未访问在线的indexes.xml");				
				ins = context.getAssets().open("indexes.xml");	
				if(ins==null) {
				//	print("在assets中也没有indexes.xml文件");
					return;
				}
			}else{
				downloadedXMLOnline=true;
				print("找到了在线访问获得的indexes.xml文件");
			}
			
			offlineDataRegionMapping = new HashMap<String,OfflineDataRegion>();	
			
			if(downloadedXMLOnline){					
				isr=new InputStreamReader(new FileInputStream(indexesFile),"gb2312");
			}else{
				isr=new InputStreamReader(ins,"utf-8");
			}
			
			java.io.BufferedReader br = new java.io.BufferedReader(isr);
			
			String s;
			String str;
			StringBuffer stringbuffer = new StringBuffer();
			while( (s =br.readLine())!=null){
				stringbuffer.append(s);
			}
			str = new String(stringbuffer);
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(new StringReader(str));
			int next;
			
			while((next = parser.next()) != XmlPullParser.END_DOCUMENT) {
				if (next == XmlPullParser.START_TAG) {
					DownloadActivityType tp = getIndexType(parser.getName());
					if (tp != null) {
						String name = parser.getAttributeValue(null, "filename")+".sqlitedb";//parser.getAttributeValue(null, "name"); //$NON-NLS-1$
						String filename1 = parser.getAttributeValue(null, "filename");
						//System.err.println("离线包文件名�?"+new String(name.getBytes("ISO_8859_1"),"gb2312"));
						String rlon = parser.getAttributeValue(null,"rlon");
						String rlat = parser.getAttributeValue(null,"rlat");
						String llon = parser.getAttributeValue(null,"llon");
						String llat = parser.getAttributeValue(null,"llat");
						String id = parser.getAttributeValue(null,"id");
						OfflineDataRegion region = new OfflineDataRegion();
						region.setMaxlat(Double.parseDouble(rlat));
						region.setMaxlon(Double.parseDouble(rlon));
						region.setMinlat(Double.parseDouble(llat));
						region.setMinlon(Double.parseDouble(llon));
						region.setId(Integer.parseInt(id));
						setOfflineDataRegionMapping(name, region);
					} 
				}
			}
			
			
		}catch(java.io.FileNotFoundException f){}
		catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
			if(isr!=null) isr.close();}catch(Exception ex){}
		}
		
	}
	
	public void resetStoreDirectory4ChooseDir(){
		String oldpath = context.getOldAppPath(IndexConstants.TILES_INDEX_DIR);
		dirWithTiles = context.getAppPath(IndexConstants.TILES_INDEX_DIR);
		String newpath = context.getNewPath("");
		dirWithTiles.mkdirs();
		try {
			//context.getAppPath(".nomedia").createNewFile(); //$NON-NLS-1$
			execute(new MoveFilesThread(context,oldpath,newpath), "");//此处异步线程�?��删除老的目录下的数据�?			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void print(String msg) {

		android.util.Log.i(TAG, msg);

	}

	public static final String TAG = "FileDownloader";

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"dd.MM.yyyy");

	protected static String reparseDate(Context ctx, String date) {
		try {
			Date d = simpleDateFormat.parse(date);
			return com.hifleet.data.AndroidUtils.formatDate(ctx, d.getTime());
		} catch (ParseException e) {
			return date;
		}
	}
	
	private static DownloadActivityType getIndexType(String tagName){
		if("region".equals(tagName) ||
							"multiregion".equals(tagName)) {
			return DownloadActivityType.NORMAL_FILE;
		} 
		
		return null;
	}
	
	public ResourceManager(OsmandApplication context) {
		this.context = context;
		this.settings = context.getSettings();

		// settings
		asyncLoadingThread.start();
		renderingBufferImageThread = new HandlerThread("RenderingBaseImage");
		renderingBufferImageThread.start();

		tileDownloader = MapTileDownloader.getInstance(
				Version.getFullVersion(context), context);

		resetStoreDirectory();

		WindowManager mgr = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		// System.out.println("dm pixels: "+dm.widthPixels+", "+dm.heightPixels);
		mgr.getDefaultDisplay().getMetrics(dm);
		// Only 8 MB (from 16 Mb whole mem) available for images : image 64K *
		// 128 = 8 MB (8 bit), 64 - 16 bit, 32 - 32 bit
		// at least 3*9?
		float tiles = (dm.widthPixels / 256 + 2) * (dm.heightPixels / 256 + 2)
				* 9;
		// System.out.println("Tiles to load in memory : " + tiles);
		maxImgCacheSize = (int) (tiles);
	}

	public MapTileDownloader getMapTileDownloader() {
		return tileDownloader;
	}

	public HandlerThread getRenderingBufferImageThread() {
		return renderingBufferImageThread;
	}

	private <P> void execute(BasicProgressAsyncTask<P, ?, String> task,
			P... param) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), param);
		} else {
			task.execute(param);
		}
	}

	public void resetStoreDirectory() {
		// String oldpath =
		// context.getOldAppPath(IndexConstants.TILES_INDEX_DIR);
		dirWithTiles = context.getAppPath(IndexConstants.TILES_INDEX_DIR);
		// String newpath = context.getNewPath("");
		dirWithTiles.mkdirs();
		try {
			//context.getAppPath(".nomedia").createNewFile(); //$NON-NLS-1$
			// execute(new MoveFilesThread(context,oldpath,newpath), "");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File getDirWithTiles() {
		return dirWithTiles;
	}

	public OsmandApplication getContext() {
		return context;
	}

	// //////////////////////////////////////////// Working with tiles
	// ////////////////////////////////////////////////

	public Bitmap getTileImageForMapAsync(String file, ITileSource map, int x,
			int y, int zoom, boolean loadFromInternetIfNeeded) {
		return getTileImageForMap(file, map, x, y, zoom,
				loadFromInternetIfNeeded, false, true);
	}

	public synchronized Bitmap getTileImageFromCache(String file) {
		return cacheOfImages.get(file);
	}

	public synchronized void putTileInTheCache(String file, Bitmap bmp) {
		cacheOfImages.put(file, bmp);
	}

	public Bitmap getTileImageForMapSync(String file, ITileSource map, int x,
			int y, int zoom, boolean loadFromInternetIfNeeded) {
		return getTileImageForMap(file, map, x, y, zoom,
				loadFromInternetIfNeeded, true, true);
	}

	public synchronized void tileDownloaded(DownloadRequest request) {

		if (request instanceof TileLoadDownloadRequest) {
			TileLoadDownloadRequest req = ((TileLoadDownloadRequest) request);
			String tileLoadDownloadRequestName = req.tileSource.getName();
			 if(tileLoadDownloadRequestName.compareTo("green")==0){
//					print("rm: lalalala===" + tileLoadDownloadRequestName);
					imagesOnFS.put(req.tileId, Boolean.TRUE);				
					File probExistFile = new File(dirWithTiles, req.tileId);
					try{
//					GifDecoder gd = new GifDecoder();
//					gd.read(new FileInputStream(probExistFile.getAbsolutePath()));
					
					cacheOfImages.put(req.tileId,						
//					gd.getBitmap()
							 BitmapFactory
								.decodeFile(probExistFile.getAbsolutePath())
					);
					}catch(Exception ex){
						ex.printStackTrace();
					}
//					return gd.getBitmap();
//							BitmapFactory
//							.decodeFile(probExistFile.getAbsolutePath())
//							);
				}
			 else if (tileLoadDownloadRequestName.compareTo("海图") != 0) {
				// System.out.println(tileLoadDownloadRequestName+" ？名称");
				String newreqtileId = tileLoadDownloadRequestName+"/" + req.zoom + "/" + req.xTile + "/"
						+ req.yTile + "/" + req.xTile + "_" + req.yTile
						+ ".png.tile";
				File probExistFile = new File(dirWithTiles, newreqtileId);
				try{
				cacheOfImages.put(newreqtileId, BitmapFactory
						.decodeFile(probExistFile.getAbsolutePath()));
				}catch(Exception ex){
							ex.printStackTrace();
						}
				imagesOnFS.put(newreqtileId, Boolean.TRUE);
			} else
			{
				//System.err.println("else cacheOfImages.key: " + "==============" + req.tileId);

				imagesOnFS.put(req.tileId, Boolean.TRUE);
				
				File probExistFile = new File(dirWithTiles, req.tileId);
				
				cacheOfImages.put(req.tileId, BitmapFactory
						.decodeFile(probExistFile.getAbsolutePath()));
			}
		}

	}

	public String getFilePathAfterCheckTileExistence() {
		return filepathonfs;
	}

	public ITileSource getTheExactTileSource() {
		return theExactTileSource;
	}

	private String filepathonfs;
	private ITileSource theExactTileSource;

	public synchronized boolean tileExistOnFileSystem(String file,
			ITileSource map, int x, int y, int zoom) {
		filepathonfs = file;
		theExactTileSource = map;
		boolean isChartMapSource = map.getName().compareTo(
				CHART_MAP_SOURCE_NAME) == 0 ? true : false;

		if (!imagesOnFS.containsKey(file)) {

			boolean ex = false;

			if (zoom <= 9 && isChartMapSource) {
				// print("zoom<=9: "+zoom);
				java.io.InputStream ins = null;
				try {
					ins = context.getAssets().open(
							"tiles/Z" + zoom + "/" + x + "/" + y + "/" + x
									+ "_" + y + ".png");

					if (ins != null) {
						filepathonfs = "/assets/tiles/Z" + zoom + "/" + x + "/"
								+ y + "/" + x + "_" + y + ".png";

						imagesOnFS.put(filepathonfs, Boolean.TRUE);
						cacheOfImages.put(filepathonfs,
								BitmapFactory.decodeStream(ins));
						theExactTileSource = map;
						ex = true;
						return true;
					}

				} catch (Exception ex2) {
					ex2.printStackTrace();
				} finally {
					try {
						if (ins != null)
							ins.close();
					} catch (Exception ex3) {
						ex3.printStackTrace();
					}
				}
			}

			file = calculateTileId(map,/* null */x, y, zoom);

			File probExistFile = new File(dirWithTiles, file);
			ex = probExistFile.exists();

			if (ex) {
				// System.out.println("缓存中有海图。");
				imagesOnFS.put(file, Boolean.TRUE);
				filepathonfs = file;
				try{
//					System.out.println("缓存图路径： "+probExistFile.getAbsolutePath());
				cacheOfImages.put(file, BitmapFactory.decodeFile(probExistFile
						.getAbsolutePath()));
				}catch(Exception eex){
							eex.printStackTrace();
						}
				theExactTileSource = map;
				return true;
			}
//print("缓存中仍然没有找到，第三步开始检索离线海图包。�?。�?。�?目标�?+file);	
			
			readOfflineDataRegionMappingFromFileSystem();
			
			if(offlineDataRegionMapping ==null){
//				print("尚未�?��过离线包或尚未下载过离线包数据�?");				
			}else if(MapActivity.mapKey==1){
//				System.err.println("是中国海图  读取离线包");
				final LinkedHashMap<String, File> entriesMap = new LinkedHashMap<String, File>();
				entriesMap.putAll(settings.getChartTileSourceNameByTilexy(x,y,zoom,offlineDataRegionMapping));
//				final List<Entry<String, File>> entriesMapList = new ArrayList<Entry<String, File>>(
//						entriesMap.entrySet());
				ITileSource tileSource = null;	
				boolean hasRightTileSource=false;//如果这个为true，说明下载了对应的离线包，尽管可能在这个离线包中没有找到图�?
				for(Entry<String, File> entry : entriesMap.entrySet()){
					File f = entry.getValue();
					if(f.isFile() && f.getName().endsWith(SQLiteTileSource.EXT)){
						if(isChartMapSource){
							tileSource = new SQLiteTileSource(context,f,TileSourceManager.getKnownSourceTemplates());
							if (((SQLiteTileSource) tileSource).isLocked()) {
//								print("离线包 isLocked："+tileSource.getName());
								continue;
							}
							//System.err.println("for 循环里的tilesource name�?"+tileSource.getName());
							ex = ((SQLiteTileSource) tileSource).exists(x, y, zoom);
//							print("离线包 ex："+ex);
							hasRightTileSource =true;
							if(ex){													//
								//return true;
								filepathonfs = tileSource.getName()+"@"+zoom+"/"+x+"/"+y+".png.tile";		
//							    print("存在在离线的sqlite数据包中�?+filepathonfs+");		
								break;
							}
						}
					}
				}
				
				if(ex){
					imagesOnFS.put(filepathonfs, Boolean.TRUE);
					long[] tm = new long[1];
					cacheOfImages.put(filepathonfs, ((SQLiteTileSource) tileSource).getImage(x, y, zoom, tm));
					theExactTileSource = tileSource;
//				print("在已下载的离线数据包中找到了�?"+filepathonfs);
					return true;
				}else{		
					print("离线包未找到");
					return false;
				}
		}
		}
		theExactTileSource = map;
		return imagesOnFS.get(file) != null || cacheOfImages.get(file) != null;
	}

	private long lastShowTime = 0;
	private final static int SHOW_INTERVAL = 5000;

	public void clearTileImageForMap(String file, ITileSource map, int x,
			int y, int zoom) {
		getTileImageForMap(file, map, x, y, zoom, true, false, true, true);
	}

	/**
	 * @param file
	 *            - null could be passed if you do not call very often with that
	 *            param
	 */
	protected Bitmap getTileImageForMap(String file, ITileSource map, int x,
			int y, int zoom, boolean loadFromInternetIfNeeded, boolean sync,
			boolean loadFromFs) {
		return getTileImageForMap(file, map, x, y, zoom,
				loadFromInternetIfNeeded, sync, loadFromFs, false);
	}

	// introduce cache in order save memory

	protected StringBuilder builder = new StringBuilder(40);
	protected char[] tileId = new char[120];

	// private GeoidAltitudeCorrection geoidAltitudeCorrection;

	public synchronized String calculateTileId(ITileSource map, int x, int y,
			int zoom) {
		//System.out.println("调用： "+map.getName());
		builder.setLength(0);
		if (map == null) {
			builder.append("海图");
		} else {
			// System.err.println("builder.append(): "+map.getName());
			builder.append(map.getName());
			
		}
		builder.append('/');
		builder.append(zoom).append('/').append(x).append('/').append(y)
				.append('/').append(x).append('_').append(y)
				//.append(map == null ? ".jpg" : map.getTileFormat()).append(".tile"); //$NON-NLS-1$ //$NON-NLS-2$
				.append(".png").append(".tile");
		//System.out.println("path: "+builder.toString());
		return builder.toString();
	}

	protected synchronized Bitmap getTileImageForMap(String tileId,
			ITileSource map, int x, int y, int zoom,
			boolean loadFromInternetIfNeeded, boolean sync, boolean loadFromFs,
			boolean deleteBefore) {

		if (tileId == null) {
			tileId = calculateTileId(map, x, y, zoom);
			if (tileId == null) {
				// System.err.println("tileId null, 杩斿洖null锟�);
				return null;
			}
		}

		if (deleteBefore) {
			cacheOfImages.remove(tileId);

			File f = new File(dirWithTiles, tileId);
			if (f.exists()) {
				f.delete();
			}

			imagesOnFS.put(tileId, null);
		}

		if (loadFromFs && cacheOfImages.get(tileId) == null && map != null) {
			// print("进入if");
			File toSave = null;
			if (!loadFromInternetIfNeeded
					&& !tileExistOnFileSystem(tileId, map, x, y, zoom)) {
				return null;
			}
			String url = loadFromInternetIfNeeded ? map
					.getUrlToLoad(x, y, zoom) : null;
			if (url != null) {
				toSave = new File(dirWithTiles, tileId);
			}
			TileLoadDownloadRequest req = new TileLoadDownloadRequest(
					dirWithTiles, url, toSave, tileId, map, x, y, zoom);
//			 print("异步请求。 TileLoadDownloadRequest"+url);
			if (sync) {
				return getRequestedImageTile(req);
			} else {
				// print("异步请求。"+req.fileToSave);
				asyncLoadingThread.requestToLoadImage(req);
			}
		}

		return cacheOfImages.get(tileId);
	}

	protected Bitmap getRequestedImageTile(TileLoadDownloadRequest req) {

		if (req.tileId == null || req.dirWithTiles == null) {
			System.out.println("return null 1");
			return null;
		}
		Bitmap cacheBmp = cacheOfImages.get(req.tileId);
		if (cacheBmp != null) {
			return cacheBmp;
		}
		if (cacheOfImages.size() > maxImgCacheSize) {
			clearTiles();
		}
		if (req.dirWithTiles.canRead()) {

			long time = System.currentTimeMillis();

			Bitmap bmp = null;

			File en = new File(req.dirWithTiles, req.tileId);
			if (en.exists()) {
				try {
					bmp = BitmapFactory.decodeFile(en.getAbsolutePath());
					int ts = req.tileSource.getExpirationTimeMillis();//????这是时间间隔大于多少重新请求吗？
//					System.out.println("resoursemanager没判断  ts: " + ts + ", req.url: "
//							+ req.url + ", time: " + time);
					if (ts != -1 && req.url != null
							&& time - en.lastModified() > ts) {
						asyncLoadingThread.requestToDownload(req);
					} else {
//						System.out.println("resoursemanager ts: " + ts + ", req.url: "
//								+ req.url + ", time: " + time);
					}
				}
				catch (OutOfMemoryError e) {
					System.err.println("decodeFile decodeFile err");
					e.printStackTrace();
					clearTiles();
				}
			}

			// }

			if (bmp != null) {
				cacheOfImages.put(req.tileId, bmp);
			}

			if (cacheOfImages.get(req.tileId) == null && req.url != null) {
				// System.out.println("请求下载："+req.fileToSave.getPath());
				asyncLoadingThread.requestToDownload(req);
			}
		}
		return cacheOfImages.get(req.tileId);
	}

	// //////////////////////////////////////////// Working with indexes
	// ////////////////////////////////////////////////

	private void indexRegionsBoundaries(IProgress progress, boolean overwrite) {
	}

	private List<String> checkAssets(IProgress progress) {
		if (!Version.getFullVersion(context).equalsIgnoreCase(
				context.getSettings().PREVIOUS_INSTALLED_VERSION.get())) {
			File applicationDataDir = context.getAppPath(null);
			applicationDataDir.mkdirs();
			if (applicationDataDir.canWrite()) {
				try {
					progress.startTask(context
							.getString(R.string.installing_new_resources), -1);
					indexRegionsBoundaries(progress, true);
					AssetManager assetManager = context.getAssets();
					boolean isFirstInstall = context.getSettings().PREVIOUS_INSTALLED_VERSION
							.get().equals("");
					unpackBundledAssets(assetManager, applicationDataDir,
							progress, isFirstInstall);
					context.getSettings().PREVIOUS_INSTALLED_VERSION
							.set(Version.getFullVersion(context));
					// context.getPoiFilters().updateFilters(false);
				} catch (SQLiteException e) {
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				} catch (XmlPullParserException e) {
					log.error(e.getMessage(), e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return Collections.emptyList();
	}

	private final static String ASSET_INSTALL_MODE__alwaysCopyOnFirstInstall = "alwaysCopyOnFirstInstall";
	private final static String ASSET_COPY_MODE__overwriteOnlyIfExists = "overwriteOnlyIfExists";
	private final static String ASSET_COPY_MODE__alwaysOverwriteOrCopy = "alwaysOverwriteOrCopy";
	private final static String ASSET_COPY_MODE__copyOnlyIfDoesNotExist = "copyOnlyIfDoesNotExist";

	private void unpackBundledAssets(AssetManager assetManager,
			File appDataDir, IProgress progress, boolean isFirstInstall)
			throws IOException, XmlPullParserException {
		XmlPullParser xmlParser = XmlPullParserFactory.newInstance()
				.newPullParser();
		InputStream isBundledAssetsXml = assetManager
				.open("bundled_assets.xml");
		xmlParser.setInput(isBundledAssetsXml, "UTF-8");

		int next = 0;
		while ((next = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
			if (next == XmlPullParser.START_TAG
					&& xmlParser.getName().equals("asset")) {
				final String source = xmlParser.getAttributeValue(null,
						"source");
				final String destination = xmlParser.getAttributeValue(null,
						"destination");
				final String combinedMode = xmlParser.getAttributeValue(null,
						"mode");

				final String[] modes = combinedMode.split("\\|");
				if (modes.length == 0) {
					// .error("Mode '" + combinedMode + "' is not valid");
					continue;
				}
				String installMode = null;
				String copyMode = null;
				for (String mode : modes) {
					if (ASSET_INSTALL_MODE__alwaysCopyOnFirstInstall
							.equals(mode))
						installMode = mode;
					else if (ASSET_COPY_MODE__overwriteOnlyIfExists
							.equals(mode)
							|| ASSET_COPY_MODE__alwaysOverwriteOrCopy
									.equals(mode)
							|| ASSET_COPY_MODE__copyOnlyIfDoesNotExist
									.equals(mode))
						copyMode = mode;
					// else
					// log.error("Mode '" + mode + "' is unknown");
				}

				final File destinationFile = new File(appDataDir, destination);

				boolean unconditional = false;
				if (installMode != null)
					unconditional = unconditional
							|| (ASSET_INSTALL_MODE__alwaysCopyOnFirstInstall
									.equals(installMode) && isFirstInstall);
				// if (copyMode == null)
				// .error("No copy mode was defined for " + source);
				unconditional = unconditional
						|| ASSET_COPY_MODE__alwaysOverwriteOrCopy
								.equals(copyMode);

				boolean shouldCopy = unconditional;
				shouldCopy = shouldCopy
						|| (ASSET_COPY_MODE__overwriteOnlyIfExists
								.equals(copyMode) && destinationFile.exists());
				shouldCopy = shouldCopy
						|| (ASSET_COPY_MODE__copyOnlyIfDoesNotExist
								.equals(copyMode) && !destinationFile.exists());

				if (shouldCopy)
					copyAssets(assetManager, source, destinationFile);
			}
		}

		isBundledAssetsXml.close();
	}

	// TODO consider some other place for this method?
	public static void copyAssets(AssetManager assetManager, String assetName,
			File file) throws IOException {
		if (file.exists()) {
			Algorithms.removeAllFiles(file);
		}
		file.getParentFile().mkdirs();
		InputStream is = assetManager.open(assetName,
				AssetManager.ACCESS_STREAMING);
		FileOutputStream out = new FileOutputStream(file);
		Algorithms.streamCopy(is, out);
		Algorithms.closeStream(out);
		Algorithms.closeStream(is);
	}

	
	
	private void initRenderers(IProgress progress) {
		File file = context.getAppPath(IndexConstants.RENDERERS_DIR);
		file.mkdirs();
		Map<String, File> externalRenderers = new LinkedHashMap<String, File>();
		if (file.exists() && file.canRead()) {
			File[] lf = file.listFiles();
			if (lf != null) {
				for (File f : lf) {
					if (f != null
							&& f.getName().endsWith(
									IndexConstants.RENDERER_INDEX_EXT)) {
						String name = f.getName().substring(
								0,
								f.getName().length()
										- IndexConstants.RENDERER_INDEX_EXT
												.length());
						externalRenderers.put(name, f);
					}
				}
			}
		}
		// context.getRendererRegistry().setExternalRenderers(externalRenderers);
		// String r = context.getSettings().RENDERER.get();
		// if (r != null) {
		// RenderingRulesStorage obj = context.getRendererRegistry()
		// .getRenderer(r);
		// if (obj != null) {
		// context.getRendererRegistry().setCurrentSelectedRender(obj);
		// }
		// }
	}

	private List<File> collectFiles(File dir, String ext, List<File> files) {
		if (dir.exists() && dir.canRead()) {
			File[] lf = dir.listFiles();
			if (lf == null) {
				lf = new File[0];
			}
			for (File f : lf) {
				if (f.getName().endsWith(ext)) {
					files.add(f);
				}
			}
		}
		return files;
	}

	public synchronized void close() {
		imagesOnFS.clear();
		indexFileNames.clear();
		basemapFileNames.clear();
	}

	public Map<String, String> getIndexFileNames() {
		return new LinkedHashMap<String, String>(indexFileNames);
	}

	public boolean containsBasemap() {
		return !basemapFileNames.isEmpty();
	}

	public synchronized void reloadTilesFromFS() {
		imagesOnFS.clear();
	}

	// / On low memory method ///
	public void onLowMemory() {
		//log.info("On low memory : cleaning tiles - size = " + cacheOfImages.size()); //$NON-NLS-1$
		clearTiles();
		System.gc();
	}

	protected synchronized void clearTiles() {
		//log.info("Cleaning tiles - size = " + cacheOfImages.size()); //$NON-NLS-1$
		ArrayList<String> list = new ArrayList<String>(cacheOfImages.keySet());
		// remove first images (as we think they are older)
		for (int i = 0; i < list.size() / 2; i++) {
			cacheOfImages.remove(list.get(i));
		}
	}
	 public void clearAllTiles(){

			//log.info("Cleaning tiles - size = " + cacheOfImages.size()); //$NON-NLS-1$
			ArrayList<String> list = new ArrayList<String>(cacheOfImages.keySet());
			// remove first images (as we think they are older)
			for (int i = 0; i < list.size(); i++) {
				cacheOfImages.remove(list.get(i));
			}
		
	 }
}
