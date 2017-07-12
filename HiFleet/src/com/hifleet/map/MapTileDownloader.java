package com.hifleet.map;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hifleet.bean.loginSession;

import android.util.Log;

public class MapTileDownloader {
	// Download manager tile settings

	public static int TILE_DOWNLOAD_THREADS = 4;// 核心线程�?
	public static int MAX_TILE_DOWNLOAD_THREADS = 4;// �?��线程�?
	public static int TILE_DOWNLOAD_SECONDS_TO_WORK = 25;// 超出核心线程数以外的线程保持的空闲时间�?在此时间后，将关闭线程�?

	public static final long TIMEOUT_AFTER_EXCEEDING_LIMIT_ERRORS = 15000;// 15000

	public static final int TILE_DOWNLOAD_MAX_ERRORS_PER_TIMEOUT = 50;

	private static final int CONNECTION_TIMEOUT = 30000;

	private static OsmandApplication ctx;

	private static MapTileDownloader downloader = null;
	// private static Log log = PlatformUtil.getLog(MapTileDownloader.class);

	private static boolean LogDebug = false;

	public static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:29.0) Gecko/20100101 Firefox/29.0";

	private ThreadPoolExecutor threadPoolExecutor;
	private List<IMapDownloaderCallback> callbacks = new ArrayList<IMapDownloaderCallback>();

	private Set<File> currentlyDownloaded;

	private int currentErrors = 0;
	private long timeForErrorCounter = 0;

	public static MapTileDownloader getInstance(String userAgent,
			OsmandApplication ctx) {

		if (downloader == null) {
			downloader = new MapTileDownloader(TILE_DOWNLOAD_THREADS,
					MAX_TILE_DOWNLOAD_THREADS, ctx);
			if (userAgent != null) {
				MapTileDownloader.USER_AGENT = userAgent;
			}

		}
		// System.out.println("��ʼ���������� "+userAgent);
		return downloader;
	}

	/**
	 * Callback for map downloader
	 */
	public interface IMapDownloaderCallback {

		/**
		 * Sometimes null cold be passed as request That means that there were a
		 * lot of requests but once method is called (in order to not create a
		 * collection of request & reduce calling times)
		 * 
		 * @param fileSaved
		 */
		public void tileDownloaded(DownloadRequest request);
	}

	/**
	 * Download request could subclassed to create own detailed request
	 */
	public static class DownloadRequest {
		public final File fileToSave;
		public final int zoom;
		public final int xTile;
		public final int yTile;
		public final String url;
		public boolean error;

		public DownloadRequest(String url, File fileToSave, int xTile,
				int yTile, int zoom) {
			// System.out.println("url in MapTileDownloadr: "+url);
			this.url = url;
			this.fileToSave = fileToSave;
			this.xTile = xTile;
			this.yTile = yTile;
			this.zoom = zoom;
		}

		public DownloadRequest(String url, File fileToSave) {
			this.url = url;
			this.fileToSave = fileToSave;
			xTile = -1;
			yTile = -1;
			zoom = -1;
		}

		public void setError(boolean error) {
			this.error = error;
		}
	}

	public MapTileDownloader(int numberOfCoreThreads, int numberOfMaxThreads,
			OsmandApplication ctx) {
		this.ctx = ctx;
		threadPoolExecutor = new ThreadPoolExecutor(numberOfCoreThreads,
				numberOfMaxThreads, TILE_DOWNLOAD_SECONDS_TO_WORK,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		// 1.6 method but very useful to kill non-running threads
		// threadPoolExecutor.allowCoreThreadTimeOut(true);//允许核心线程超时策略�?���?
		currentlyDownloaded = Collections.synchronizedSet(new HashSet<File>());

	}

	public void addDownloaderCallback(IMapDownloaderCallback callback) {
		callbacks.add(callback);
	}

	public void removeDownloaderCallback(IMapDownloaderCallback callback) {
		callbacks.remove(callback);
	}

	public List<IMapDownloaderCallback> getDownloaderCallbacks() {
		return callbacks;
	}

	public boolean isFileCurrentlyDownloaded(File f) {
		return currentlyDownloaded.contains(f);
	}

	public boolean isSomethingBeingDownloaded() {
		return !currentlyDownloaded.isEmpty();
	}

	public int getRemainingWorkers() {
		return (int) (threadPoolExecutor.getTaskCount());
	}

	public void refuseAllPreviousRequests() {
		// That's very strange because exception in impl of queue (possibly
		// wrong impl)
		// threadPoolExecutor.getQueue().clear();
		while (!threadPoolExecutor.getQueue().isEmpty()) {
			threadPoolExecutor.getQueue().poll();
		}
	}

	private HashMap<String, DownloadRequest> downloadRequestMap = new HashMap<String, DownloadRequest>();

	public void requestToDownload(DownloadRequest request) {

		BlockingQueue<Runnable> threads = threadPoolExecutor.getQueue();
		// print("队列中线程数： "+threads.size());
		for (Runnable r : threads) {
			if (r instanceof DownloadMapWorker) {
				// print("取消一个线程： ");
				DownloadMapWorker w = (DownloadMapWorker) r;
				if (request.fileToSave.getPath().compareTo(
						w.request.fileToSave.getPath()) == 0) {
					// print("新请求的："+request.fileToSave.getAbsolutePath());
					// print("队列中已有的："+w.request.fileToSave.getAbsolutePath());
					// print("相同的，不再继续请求新的。");
					// continue;
					return;
				}
				// ((DownloadMapWorker) r).setRunFlag(false);
			}
		}
		long now = System.currentTimeMillis();
		if ((int) (now - timeForErrorCounter) > TIMEOUT_AFTER_EXCEEDING_LIMIT_ERRORS) {
			timeForErrorCounter = now;
			currentErrors = 0;
		} else if (currentErrors > TILE_DOWNLOAD_MAX_ERRORS_PER_TIMEOUT) {
			// System.err.println("currenterrors: "+currentErrors+", TILE_DOWNLOAD_MAX_ERRORS_PER_TIMEOUT: "+TILE_DOWNLOAD_MAX_ERRORS_PER_TIMEOUT+" ��һ��return��");
			return;
		}

		if (request.url == null) {
			// System.err.println("request.url == null return void.");
			return;
		}

		// print("存一个： "+request.fileToSave.getPath());
		downloadRequestMap.put(request.fileToSave.getPath(), request);
		DownloadMapWorker worker = new DownloadMapWorker(request);
		threadPoolExecutor.execute(worker);
	}

	public boolean isMapEmpty() {
		return downloadRequestMap.isEmpty();
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";

	private class DownloadMapWorker implements Runnable,
			Comparable<DownloadMapWorker> {

		private DownloadRequest request;
		OsmandApplication app;
		private DownloadMapWorker(DownloadRequest request) {
			this.request = request;
		}

		private boolean checkWIFIorGPRS() {
			boolean isWIFIConnected = ctx.getSettings().isWifiConnected();
			boolean isGPRSConnected = ctx.getSettings()
					.isInternetConnectionAvailable();
			return (isWIFIConnected || isGPRSConnected);
		}

		public void setRunFlag(boolean flag) {
			runflag = flag;
		}

		boolean runflag = true;

		@Override
		public void run() {

			if (!runflag) {
				// print("取消运行线程： "+request.fileToSave);
				return;
			}

			if (request != null && request.fileToSave != null
					&& request.url != null) {
				if (currentlyDownloaded.contains(request.fileToSave)) {
					// print("当前下载任务已包含了该下载请求了。return void.");
					return;

				}

				// print("fileToSave path in run: "+request.fileToSave.getAbsolutePath());

				currentlyDownloaded.add(request.fileToSave);

				if (!checkWIFIorGPRS()) {
				}

				try {
					request.fileToSave.getParentFile().mkdirs();
					URL url;
					String urlstr = request.url;
					System.out.println("plplplplpl==" + urlstr);
					url = new URL(urlstr);
					URLConnection connection = url.openConnection();

					if (loginSession.getSessionid() != null) {
						connection.setRequestProperty("cookie",
								loginSession.getSessionid());
					} else {
						connection.setRequestProperty("cookie",
								app.myPreferences.getString("sessionid", ""));
					}
					
					connection.setRequestProperty("User-Agent", USER_AGENT); //$NON-NLS-1$

					connection.setConnectTimeout(CONNECTION_TIMEOUT);
					connection.setReadTimeout(CONNECTION_TIMEOUT);

					BufferedInputStream inputStream = new BufferedInputStream(
							connection.getInputStream(), 8 * 1024);
					FileOutputStream stream = null;
					try {
						stream = new FileOutputStream(request.fileToSave);
						Algorithms.streamCopy(inputStream, stream);
						stream.flush();
//						System.out.println("在线海图瓦片请求成功！刷到存储上的路径："+"url"+urlstr+" 大小 "+connection.getInputStream().read()
//								+ request.fileToSave);

					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						Algorithms.closeStream(inputStream);
						Algorithms.closeStream(stream);
					}

				} catch (UnknownHostException e) {
					print("UnknownHostException: " + e.getMessage());
					currentErrors++;
					// timeForErrorCounter = System.currentTimeMillis();
					request.setError(true);
					//log.error("UnknownHostException, cannot download tile " + request.url + " " + e.getMessage()); //$NON-NLS-1$  //$NON-NLS-2$
				} catch (java.io.FileNotFoundException fnfe) {
					FileOutputStream stream = null;
					java.io.InputStream ins = null;
					// System.err.println("未发现文�?: " + request.url+", 使用默认图片�?);
					// //使用默认图片
					try {
						stream = new FileOutputStream(request.fileToSave);
						ins = ctx.getAssets().open("missing.png");
						Algorithms.streamCopy(ins, stream);
						stream.flush();
						// System.err.println("在线海图瓦片请求成功！刷到存储上的路径："+request.fileToSave);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						Algorithms.closeStream(ins);
						Algorithms.closeStream(stream);
					}

				} catch (IOException e) {
					 e.printStackTrace();
					currentErrors++;
					// timeForErrorCounter = System.currentTimeMillis();
					// System.err.println(e.getMessage());
					request.setError(true);
					System.err.println("下载瓦片失败 : " + request.url); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					currentlyDownloaded.remove(request.fileToSave);
				}

				if (!request.error) {
					// System.out.println("去一个。"+request.fileToSave.getPath());
					downloadRequestMap.remove(request.fileToSave.getPath());

					// print("群发通知。"+request.fileToSave.getPath());
					for (IMapDownloaderCallback c : new ArrayList<IMapDownloaderCallback>(
							callbacks)) {
						c.tileDownloaded(request);
					}
				}
			}

		}

		private void try2CheckAssets(DownloadRequest request) {
			// System.out.println("try2checkassets: "+request.zoom+", X: "+request.xTile+", Y: "+request.yTile);
		}

		private String caculateQuadkey(int tx, int ty, int zoom) {
			String quad = "";
			for (int i = zoom; i > 0; i--) {
				int mask = 1 << (i - 1);
				int cell = 0;
				if ((tx & mask) != 0) {
					cell += 1;
				}
				if ((ty & mask) != 0) {
					cell += 2;
				}
				quad += cell;
			}
			return quad;
		}

		@Override
		public int compareTo(DownloadMapWorker o) {
			return 0; // (int) (time - o.time);
		}

	}
}
