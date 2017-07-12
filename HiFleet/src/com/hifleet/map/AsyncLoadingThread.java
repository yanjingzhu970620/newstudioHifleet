package com.hifleet.map;


import java.io.File;
import java.util.Stack;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.hifleet.map.MapTileDownloader.IMapDownloaderCallback;

/**
 * Thread to load map objects (POI, transport stops )async
 */
public class AsyncLoadingThread extends Thread {
	
	public static final int LIMIT_TRANSPORT = 200;
	
	//private static final Log log = PlatformUtil.getLog(AsyncLoadingThread.class); 
	
	private Handler asyncLoadingPoi; 
	private Handler asyncLoadingTransport;
	
	Stack<Object> requests = new Stack<Object>();
	
	
	
	private final ResourceManager resourceManger;

	public AsyncLoadingThread(ResourceManager resourceManger) {
		super("Loader map objects (synchronizer)"); //$NON-NLS-1$
		
		this.resourceManger = resourceManger;
		//
	}
	
	private void startPoiLoadingThread() {
		HandlerThread h = new HandlerThread("Loading poi");
		h.start();
		asyncLoadingPoi = new Handler(h.getLooper());
	}
	
	private void startTransportLoadingThread() {
		HandlerThread h = new HandlerThread("Loading transport");
		h.start();
		asyncLoadingTransport = new Handler(h.getLooper());
	}

	private int calculateProgressStatus() {
		int progress = 0;
		if (resourceManger.getMapTileDownloader() != null && resourceManger.getMapTileDownloader().isSomethingBeingDownloaded()) {
			//progress = BusyIndicator.STATUS_GREEN;
		}  else if (!requests.isEmpty()) {
			//progress = BusyIndicator.STATUS_BLACK;
		} 
		return progress;
	}

	@Override
	public void run() {
		while (true) {
			try {
				boolean tileLoaded = false;
				while (!requests.isEmpty()) {					
					Object req = requests.pop();
					//System.err.println("取出下载链接请求");
					if (req instanceof TileLoadDownloadRequest) {
						TileLoadDownloadRequest r = (TileLoadDownloadRequest) req;
						tileLoaded |= resourceManger.getRequestedImageTile(r) != null;
					} 
					
				}
				if (tileLoaded) {
					for (IMapDownloaderCallback c : resourceManger.getMapTileDownloader().getDownloaderCallbacks()) {
						c.tileDownloaded(null);
					}
				}
				sleep(250);//250 这个是原来的频率间隔
			} 
			catch (InterruptedException e) {
				//System.err.println(e.getMessage());
			}
			catch (RuntimeException e) {
				//log.error(e, e);
				//System.err.println(e.getMessage());
			}catch(Exception ex){ex.printStackTrace();}
		}
	}
	private static void print(String msg){
        Log.i(TAG, msg);
    }
 public static final String TAG = "FileDownloader";
	public void requestToLoadImage(TileLoadDownloadRequest req) {
		try{ 
//			print("放入堆栈： "+req.fileToSave.getPath());
			requests.push(req);
			//requests.setSize(10);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//System.out.println("size: "+requests.size()+", capacity: "+requests.capacity());
		
	}
	public void requestToLoadMap(MapLoadRequest req) {
		requests.push(req);
	}

	
	
	public boolean isFileCurrentlyDownloaded(File fileToSave) {
		return resourceManger.getMapTileDownloader().isFileCurrentlyDownloaded(fileToSave);
	}

	public void requestToDownload(TileLoadDownloadRequest req) {
		resourceManger.getMapTileDownloader().requestToDownload(req);
	}

	public static class TileLoadDownloadRequest extends MapTileDownloader.DownloadRequest {

		public final String tileId;
		public final File dirWithTiles;
		public final ITileSource tileSource;

		public TileLoadDownloadRequest(File dirWithTiles, String url, File fileToSave, String tileId, ITileSource source, int tileX,
				int tileY, int zoom) {
			super(url, fileToSave, tileX, tileY, zoom);
			this.dirWithTiles = dirWithTiles;
			this.tileSource = source;
			this.tileId = tileId;
		}
	}


protected static class MapLoadRequest {
		public final RotatedTileBox tileBox;

		public MapLoadRequest(RotatedTileBox tileBox) {
			super();
			this.tileBox = tileBox;
		}
	}


}