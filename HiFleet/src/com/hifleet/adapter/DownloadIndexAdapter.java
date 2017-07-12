package com.hifleet.adapter;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hifleet.activity.DownloadIndexActivity;
import com.hifleet.data.DBHelper;
import com.hifleet.data.DownloadActivityType;
import com.hifleet.data.DownloadEntry;
import com.hifleet.data.DownloadInfo;
import com.hifleet.data.IndexItem;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;
import com.hifleet.thread.DownloadIndexItemThread;
import com.hifleet.thread.DownloadIndexItemThread.DownloadOfflineMapAsynTask;

public class DownloadIndexAdapter extends OsmandBaseExpandableListAdapter2 {
	private DownloadIndexActivity downloadActivity;
	private final List<IndexItemCategory> list = new ArrayList<IndexItemCategory>();
	private Map<String, String> localDownloaedIndexFileNames = null;// 保存已经下载的离线海图包名字和时间的对应关系
	private Map<String, String> localPausedIndexFileNames = null;
	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
			"yyyy-MM-dd");

	private Map<String, String> indexActivatedFileNames = null;
	private final List<IndexItem> indexFiles;
	OsmandApplication app;
	private DBHelper dbHelper;
	private boolean firstRun = true;

	private int okColor;
	private int defaultColor;
	private int progressColor;
	private int pauseColor;

	private static HashMap<String, IndexItem> currentRunning = new HashMap<String, IndexItem>();

	private static Map<String, IndexItem> waiting = new HashMap<String, IndexItem>();
	private static Map<String, IndexItem> waiting2Paused = new HashMap<String, IndexItem>();

	private static Map<String, IndexItem> paused = new HashMap<String, IndexItem>();

	// 初始化时从数据库中获得的等待任务文件名。
	private Map<String, IndexItem> watingTaskFileNameListFromDB;

	DownloadIndexItemThread downloadThread3;

	public DownloadIndexAdapter(DownloadIndexActivity downloadActivity,
			List<IndexItem> indexFiles) {
		this.downloadActivity = downloadActivity;
		this.indexFiles = new ArrayList<IndexItem>(indexFiles);
		app = (OsmandApplication) downloadActivity.getApplication();
		// handleXML.setApp(app);
		dbHelper = new DBHelper(app);

		currentRunning.clear();
		waiting.clear();

		watingTaskFileNameListFromDB = getWaitingQueueFileNames();

		List<IndexItemCategory> cats = IndexItemCategory.categorizeIndexItems(
				downloadActivity.getMyApplication(), indexFiles);

		synchronized (this) {
			list.clear();
			list.addAll(cats);
		}

		if (!watingTaskFileNameListFromDB.isEmpty()) {
			waiting.putAll(watingTaskFileNameListFromDB);
		}

		okColor = downloadActivity.getResources()
				.getColor(R.color.color_update);
		defaultColor = downloadActivity.getResources().getColor(R.color.black);
		progressColor = downloadActivity.getResources().getColor(
				R.color.mediumslateblue);
		pauseColor = downloadActivity.getResources().getColor(R.color.darkred);

		// 创建一个下载的线程句柄
		downloadThread3 = new DownloadIndexItemThread(app, dbHelper, this);
		// 自动激活一个在等待的下载任务
		autoActivateWaitingDownloadTask();

	}

	// 检查按钮的状态。
	public void checkButtonStatus() {
		downloadActivity.isDowloadAll();
		downloadActivity.isPauseAll();
		downloadActivity.isDeleteAll();
	}

	// 通知ListView，该离线包删除了。需要做两件事，第一是刷新一下本地还有的离线数据包文件
	// 第二件事：删除数据库中的下载记录。

	SQLiteDatabase db0;

	public void updateDeletedAllIndexItems() {
		// print("删除下载记录数据库中所有的下载记录。");
		try {
			if (db0 == null)
				db0 = dbHelper.getWritableDatabase();
			String sql = "DELETE FROM fileDownloading";
			db0.execSQL(sql);
			notifyDataSetChanged();
			// print("从下载记录数据库中删除下载记录。");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	SQLiteDatabase db1;

	public void updateDeletedItem(IndexItem e) {
		try {
			DownloadEntry de = e.createDownloadEntry(app,
					DownloadActivityType.NORMAL_FILE);
			String target = de.urlToDownload;
			String path = de.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);
			String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");

			if (db1 == null)
				db1 = dbHelper.getWritableDatabase();
			String sql = "DELETE FROM fileDownloading WHERE downPath=?";
			db1.execSQL(sql, new Object[] { urlStr });

			updateLocalOfflineMapList();

			notifyDataSetChanged();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// 暂停任务，但是不自动激活下一个。
	public void pauseDownloadNoAutoActivateNextTask(IndexItem e) {

		DownloadEntry download = e.createDownloadEntry(app,
				DownloadActivityType.NORMAL_FILE);

		DownloadOfflineMapAsynTask task = downloadThread3.getAsynTaskByItem(e);

		if (task != null) {
			task.setInterrupted(true);
		}
 
		task = downloadThread3.getCurrentRunningDownloadAsynTask();
		if (task != null) {
			task.setInterrupted(true);
		}

		if (currentRunning.containsKey(e.getFileName())) {
			currentRunning.remove(e.getFileName());
		}

		if (waiting.containsKey(e.getFileName())) {
			waiting.remove(e.getFileName());
			deleteFromWaitingQueue(e);
			saveWaiting2Downloading(download);
		}

		updateLocalOfflineMapList();
		updateWaitingQueue();

		notifyDataSetChanged();
	}

	// 删除一个在等待，暂停或已经下载的任务。
	public void deleteDownload(IndexItem e) {
		// DownloadEntry download = e.createDownloadEntry(app,
		// DownloadActivityType.NORMAL_FILE);

		DownloadOfflineMapAsynTask task = downloadThread3.getAsynTaskByItem(e);
		if (task != null) {
			// print("adapter当前有这个正在运行的下载任务。现在设暂停。任务名：" + e.getFileName());
			task.setInterrupted(true);
		}

		task = downloadThread3.getCurrentRunningDownloadAsynTask();
		if (task != null) {
			// print("adapter当前有正在运行的下载任务。现在从downloadThread中设暂停。");
			task.setInterrupted(true);
		}

		if (currentRunning.containsKey(e.getFileName())) {
			// print("当前运行队列中有它，移除。");
			currentRunning.remove(e.getFileName());
		}

		if (waiting.containsKey(e.getFileName())) {
			// print("等待队列中有它，移除: "+e.getFileName()+"，然后加入到已暂停中。");
			// waiting.remove(e);
			waiting.remove(e.getFileName());

			deleteFromWaitingQueue(e);
			// print("存入的url: "+download.urlToDownload);
			// saveWaiting2Downloading(download);
		}

		updateLocalOfflineMapList();
		updateWaitingQueue();

		autoActivateWaitingDownloadTask();

		checkButtonStatus();

		notifyDataSetChanged();
	}

	// 暂停下载
	public void pauseDownload(IndexItem e) {

		// print("adapter暂停任务。任务名：" + e.getFileName());

		DownloadEntry download = e.createDownloadEntry(app,
				DownloadActivityType.NORMAL_FILE);

		DownloadOfflineMapAsynTask task = downloadThread3.getAsynTaskByItem(e);

		if (task != null) {
			// print("adapter当前有这个正在运行的下载任务。现在设暂停。任务名：" + e.getFileName());
			task.setInterrupted(true);
		}

		task = downloadThread3.getCurrentRunningDownloadAsynTask();
		if (task != null) {
			// print("adapter当前有正在运行的下载任务。现在从downloadThread中设暂停。");
			task.setInterrupted(true);
		}

		if (currentRunning.containsKey(e.getFileName())) {
			// print("当前运行队列中有它，移除。");
			currentRunning.remove(e.getFileName());
		}

		if (waiting.containsKey(e.getFileName())) {
			// print("等待队列中有它，移除: "+e.getFileName()+"，然后加入到已暂停中。");
			// waiting.remove(e);
			waiting.remove(e.getFileName());

			deleteFromWaitingQueue(e);
			// print("存入的url: "+download.urlToDownload);
			saveWaiting2Downloading(download);

			waiting2Paused.put(e.getFileName(), e);
		}

		updateLocalOfflineMapList();
		updateWaitingQueue();

		autoActivateWaitingDownloadTask();

		checkButtonStatus();

		notifyDataSetChanged();
	}

	// 开始下载
	public void download(IndexItem e) {

		try {
			final DownloadEntry download = e.createDownloadEntry(app,
					DownloadActivityType.NORMAL_FILE);
			// print("开始下载（可能需要排队）： " + download.urlToDownload);

			if (currentRunning.isEmpty()) {
				// print("当前运行任务列表为空。加入运行列表，开始后台下载异步进程。");
				downloadThread3.downloadOfflineMap(e);
				currentRunning.put(e.getFileName(), e);
			} else if (!waiting.containsKey(e.getFileName())) {
				// print("当前有任务在运行，加入到等待队列中。");
				// waiting.put(e.getFileName(), e);
				insertIntoWaitingQueue(e);// 把等待信息写入到数据库的等待任务信息表
			}

			updateLocalOfflineMapList();
			updateWaitingQueue();

			checkButtonStatus();
//			SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
//			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
//			String timeString = formatter.format(curDate);
//			app.mEditor.putString(e.getDescription(), timeString).commit();
//			System.out.println(timeString);
			Message message = new Message();
			message.what = 1;
			DownloadIndexActivity.handler.sendMessage(message);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// 自动激活一个待下载的任务，这个应该在下载成功了一个任务后激活。
	private void autoActivateWaitingDownloadTask() {
		// print("准备自动激活一个任务。");

		if (!currentRunning.isEmpty()) {
			// print("当前还有正在运行的任务呢，怎么激活呢？当前正在运行的任务数量： " +
			// currentRunning.size());
			return;
		}

		if (!waiting.isEmpty()) {
			// print("当前等待下载任务列表非空。可以从中取一个出来。");
			Iterator<String> keysIt = waiting.keySet().iterator();
			String key = null;
			IndexItem item = null;

			if (keysIt.hasNext()) {
				key = keysIt.next();
				item = waiting.get(key);
			}

			if (key != null && item != null) {
				// print("排在任务队列的第一个的是： " + item.getFileName() +
				// "， 现在要开始下载它了。");

				download(item);// 开始下载
				waiting.remove(key);// 从等待队列中删除这个排队项目
				// print("删除这个包在等待记录里的信息。");
				deleteFromWaitingQueue(item);// 从数据库的等待任务信息表中删除记录
			}
		}

		updateLocalOfflineMapList();
		updateWaitingQueue();
	}

	// 开始下载
	private void startDownload_New(IndexItem e, ImageButton imgageButton) {
		imgageButton.setImageResource(R.drawable.pause);
		imgageButton.setTag("暂停下载");
		// print("开始下载了，现在设置成暂停下载。");
		download(e);

		// downloadActivity.isDowloadAll();
		// downloadActivity.isPauseAll();
		// downloadActivity.isDeleteAll();

	}

	// 暂停下载
	private void pauseDownload_New(IndexItem e, ImageButton imgageButton) {
		imgageButton.setImageResource(R.drawable.ic_action_gplay_over_light);
		imgageButton.setTag("继续下载");
		pauseDownload(e);
	}

	// 继续下载
	private void continueDownload(IndexItem e, ImageButton imgageButton) {
		imgageButton.setImageResource(R.drawable.pause);
		imgageButton.setTag("暂停下载");
		download(e);
	}

	// 删除
	private void deleteOfflineMapData(IndexItem e, ImageButton imgageButton) {
		downloadActivity.openDeleteOfflineMapConfirmDialog(e, e.getFileName(),
				e.getDescription());
	}

	public void notifyTaskPaused() {
	}

	public void notifyTaskCompleted(String fileName) {
		currentRunning.remove(fileName);
		autoActivateWaitingDownloadTask();
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = downloadActivity.getLayoutInflater();
			v = inflater.inflate(R.layout.download_index_list_item,
					parent, false);
		}

		final View row = v;

		TextView itemName = (TextView) row.findViewById(R.id.download_item);
		TextView itemDescription = (TextView) row
				.findViewById(R.id.download_descr);

		final ImageButton controlImageButton = (ImageButton) row
				.findViewById(R.id.downloadImageButton);
		setButtonStateChangeListener(controlImageButton);
		//
		final ImageButton deleteImageButton = (ImageButton) row
				.findViewById(R.id.deleteImageButton);
		setButtonStateChangeListener(deleteImageButton);

		final IndexItem e = (IndexItem) getChild(groupPosition, childPosition);

		final DownloadEntry download = e.createDownloadEntry(app,
				DownloadActivityType.NORMAL_FILE);

		String urlStr = "";
		try {
			String target = download.urlToDownload;
			String path = download.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);
			urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");
		} catch (Exception ex) {

		}

		String leftInfo = "";
		String rightInfo = "";
		OsmandApplication clctx = downloadActivity.getMyApplication();

		// leftInfo 显示的是离线包的名字和大小，在左侧。
		leftInfo = e.getVisibleName(clctx) + " (" + e.getSizeDescription(clctx)
				+ ")";

		// 刷新一下本地已下载的离线包目录
		// updateLocalOfflineMapList();

		// print(e.getFileName());

		// 以下是对离线包状态的判断过程：
		// 状态有这几类：1，已下载，2，从未触发过任何下载或暂停动作，3，已暂停，4，在等待中，5，正在下载
		// 本地有这个文件吗？
		if (localDownloaedIndexFileNames.containsKey(e.getFileName())) {
			// 有的，表示已下载
			e.setDownloadedFlag(true);
			// print("已下载。");
			e.setHasNotActivateToDownload(false);

		} else {
			// 没的，表示未下载。
			e.setDownloadedFlag(false);

			// 当前正在运行的任务中有吗？
			if (currentRunning.containsKey(e.getFileName())) {
				// 有的，表示正在下载
				DownloadInfo downloadInfo = isDownloadComplenet(download);// 查数据库中下载进度信息表
				Map<String, Float> data = downloadInfo.getData();
				if (data.get(urlStr) != null) {
					e.setProgress(data.get(urlStr));
				} else {
					e.setProgress(0);
				}

				e.setIsDownloading(true);
				// print("正在下载。");
				e.setHasNotActivateToDownload(false);
				e.setPaused(false);
				e.setWaitingFlag(false);

			} else {
				// 没的，表示没有处于正在下载，可能的情况：在排队等待，下载暂停，根本就没有下载过。
				e.setIsDownloading(false);
				// 等待队列中有吗？
				if (waiting.containsKey(e.getFileName())) {
					// 有的，表示在排队等待中。
					e.setWaitingFlag(true);
					// print("正在等待");
					e.setHasNotActivateToDownload(false);
					e.setPaused(false);
				} else {
					// 不在等待，可能的情况，下载暂停或根本就没下载过。
					e.setWaitingFlag(false);

					DownloadInfo downloadInfo = isDownloadComplenet(download);// 查数据库中下载进度信息表
					// print("查询的urltodownload: "+download.urlToDownload);
					if (!downloadInfo.isCompleteFlag()) {
						e.setPaused(true);
						e.setHasNotActivateToDownload(false);
						// print(e.getFileName()+" 已暂停");
						Map<String, Float> data = downloadInfo.getData();
						e.setProgress(data.get(urlStr));
					} else {
						// print(e.getFileName()+" 未下载");
						e.setPaused(false);
						e.setHasNotActivateToDownload(true);
					}
				}

			}

		}

		if (e.isDownloaded()) {
			// 确实已经下载完毕了。
			if (e.isNeedUpdate()) {
				// 已下载，但是需要更新
				// print("已下载，但是需要更新。");
				leftInfo = e.getVisibleName(clctx)
						+ downloadActivity
								.getResources()
								.getString(
										R.string.offline_map_download_status_needupdate)/* " 有更新" */;

				controlImageButton
						.setImageResource(R.drawable.ic_action_down_light);
				controlImageButton.setTag("开始下载");
				deleteImageButton.setEnabled(true);

				// itemName.setTextColor(okColor);

			} else {
				// 已下载且不需要更新
				// print("已下载且不需要更新。");
				leftInfo = e.getVisibleName(clctx) + " ("
						+ e.getSizeDescription(clctx) + ")";
				rightInfo = downloadActivity.getResources().getString(
						R.string.offline_map_download_status_downloaed);// "已下载";

				controlImageButton
						.setImageResource(R.drawable.ic_action_down_light);
				controlImageButton.setEnabled(false);
				deleteImageButton.setEnabled(true);

				// itemName.setTextColor(okColor);

				itemDescription.setTextColor(okColor);
			}
		}

		else {
			if (e.getIsDownloading()) {
				// 正在下载
				float progress = e.getProgress();
				if (progress >= 100) {
					// 正在下载中，且刚好已经下载完毕了
					rightInfo = downloadActivity
							.getResources()
							.getString(
									R.string.offline_map_download_status_download_completed);// "下载完毕";
					controlImageButton
							.setImageResource(R.drawable.ic_action_down_light);
					controlImageButton.setEnabled(false);
					deleteImageButton.setEnabled(true);

					// itemName.setTextColor(okColor);
					itemDescription.setTextColor(okColor);

				} else {
					// 正在下载中，要更新它的进度信息
//					System.err.println("progress::"+progress);
					if (progress >= 0)
						rightInfo = downloadActivity
								.getResources()
								.getString(
										R.string.offline_map_download_status_downloading)/* "下载：" */
								+ "：" + progress + " %";
					else
						rightInfo = downloadActivity
								.getResources()
								.getString(
										R.string.offline_map_download_status_downloading)
								+ "：--0 %";// "下载：0 %";

					controlImageButton.setImageResource(R.drawable.pause);
					controlImageButton.setEnabled(true);
					controlImageButton.setTag("暂停下载");

					deleteImageButton.setEnabled(true);

					itemDescription.setTextColor(progressColor);

				}

			} else {
				// 不是正在下载
				if (waiting.containsKey(e.getFileName()) && e.isWaiting()) {
					// 在排队等待
					// print(""+e.getFileName()+" 等待下载中。");
					rightInfo = rightInfo = downloadActivity
							.getResources()
							.getString(
									R.string.offline_map_download_status_waiting);// "等待下载";
					controlImageButton
							.setImageResource(R.drawable.ic_action_gplay_over_light/*
																					 * R
																					 * .
																					 * drawable
																					 * .
																					 * pause
																					 */);
					controlImageButton.setEnabled(false);
					controlImageButton.setTag("暂停下载");
					deleteImageButton.setEnabled(true);
					itemDescription.setTextColor(pauseColor);
				} else {
					// 不在排队等待
					if (e.isPaused()) {
						// 在暂停中
						rightInfo = downloadActivity.getResources().getString(
								R.string.offline_map_download_status_paused)
								+ "：" + e.getProgress() + " %";// 已暂停：" + e.getProgress() + "
																// %";
						controlImageButton
								.setImageResource(R.drawable.ic_action_gplay_over_light);
						controlImageButton.setEnabled(true);
						controlImageButton.setTag("继续下载");
						deleteImageButton.setEnabled(true);
						itemDescription.setTextColor(pauseColor);
					} else {
						// 压根就没下载过。
						rightInfo = "";
						controlImageButton
								.setImageResource(R.drawable.ic_action_down_light);
						controlImageButton.setEnabled(true);
						controlImageButton.setTag("开始下载");
						deleteImageButton.setEnabled(false);

						itemDescription.setTextColor(defaultColor);
					}
				}
			}
		}

		itemName.setText(leftInfo);
		itemDescription.setText(rightInfo);
		itemName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

		controlImageButton
				.setOnClickListener(new ImageButton.OnClickListener() {
					public void onClick(View v) {
						// print("点击下载按钮。");
						// print("刚被点击的tag: "+v.getTag().toString());
						if (v.getTag().toString().compareTo("开始下载") == 0) {
							startDownload_New(e, controlImageButton);
						} else if (v.getTag().toString().compareTo("暂停下载") == 0) {
							pauseDownload_New(e, controlImageButton);
						} else if (v.getTag().toString().compareTo("继续下载") == 0) {
							continueDownload(e, controlImageButton);
						}
					}
				});

		deleteImageButton.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				// print("点击删除按钮");
				deleteOfflineMapData(e, deleteImageButton);
				// System.out.println("我要开始删除了" + e.getDescription());
			}
		});
		return row;
	}

	// 检查该下载任务是否在下载等待队列中。
	SQLiteDatabase db2;

	private boolean isInWaitingQueue(IndexItem e) {
		Cursor cursor = null;
		if (db2 == null)
			db2 = dbHelper.getReadableDatabase();
		try {

			DownloadEntry download = e.createDownloadEntry(app,
					DownloadActivityType.NORMAL_FILE/*
													 * , new
													 * ArrayList<DownloadEntry
													 * >()
													 */);
			String target = download.urlToDownload;
			String path = download.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);

			String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");

			String sql = "SELECT _id, downPath FROM fileDownloadWaitingQueue WHERE downPath=?";

			cursor = db2.rawQuery(sql, new String[] { urlStr });
			Map<Integer, String> data = new HashMap<Integer, String>();
			boolean flag = false;
			while (cursor.moveToNext()) {
				flag = true;
				break;
			}
			return flag;

		} catch (Exception ex) {
			ex.printStackTrace();
			print("查询该下载任务是否存在在等待列表中出错了：" + ex.getMessage());
		} finally {
			// db.close();
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return false;
	}

	// 从下载等队列中删除该下载任务。
	SQLiteDatabase db3;

	private void deleteFromWaitingQueue(IndexItem e) {
		try {
			if (db3 == null)
				db3 = dbHelper.getReadableDatabase();

			DownloadEntry download = e.createDownloadEntry(app,
					DownloadActivityType.NORMAL_FILE/*
													 * , new
													 * ArrayList<DownloadEntry
													 * >()
													 */);

			String target = download.urlToDownload;
			String path = download.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);

			String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");

			String sql = "DELETE FROM fileDownloadWaitingQueue WHERE downPath=?";

			db3.execSQL(sql, new Object[] { urlStr });

			// db.close();

		} catch (Exception ex) {
			print("把这个下载任务从等待列表中移除出错了： " + ex.getMessage());
		}
	}

	// 向下载等待队列中插入下载等待任务。
	SQLiteDatabase db4;

	private void insertIntoWaitingQueue(IndexItem e) {
		if (db4 == null)
			db4 = dbHelper.getWritableDatabase();
		try {
			String fileName = e.getFileName();
			String descr = e.getDescription();
			String size = e.getSize();
			String date = e.getDate();
			String rlon = e.getrlon();
			String rlat = e.getrlat();
			String llon = e.getllon();
			String llat = e.getllat();
            String filename1=e.getfilename1();
			DownloadEntry download = e.createDownloadEntry(app,
					DownloadActivityType.NORMAL_FILE/*
													 * , new
													 * ArrayList<DownloadEntry
													 * >()
													 */);
			String target = download.urlToDownload;
			String path = download.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);

			String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");

			db4.beginTransaction();
			String sql = "INSERT INTO fileDownloadWaitingQueue(fileName,description,size,date,rlon,rlat,llon,llat,filename1,downPath) values(?,?,?,?,?,?,?,?,?,?)";
			// print("向数据库中插入待下载队列数据： " + fileName);
			db4.execSQL(sql, new Object[] { fileName, descr, size, date, rlon,
					rlat, llon, llat,filename1, urlStr });
			db4.setTransactionSuccessful();
		} catch (Exception ex) {
			ex.printStackTrace();
			print("向等待队列中插入等待下载任务出错： " + ex.getMessage());
		} finally {
			db4.endTransaction();
			// db.close();
		}
	}

	// 从数据库中查询有多少任务在等待下载中。
	SQLiteDatabase db5;

	private Map<String, IndexItem> getWaitingQueueFileNames() {
		Cursor cursor = null;
		Map<String, IndexItem> names = new HashMap<String, IndexItem>();
		if (db5 == null)
			db5 = dbHelper.getWritableDatabase();
		String fileName, descr, size, date, rlon, rlat, llon, llat,filename1;
		try {

			String sql = "SELECT fileName,description,date,size,rlon,rlat,llon,llat,filename1 FROM fileDownloadWaitingQueue";
			cursor = db5.rawQuery(sql, null);

			while (cursor.moveToNext()) {
				fileName = cursor.getString(0);
				descr = cursor.getString(1);
				date = cursor.getString(2);
				size = cursor.getString(3);
				rlon = cursor.getString(4);
				rlat = cursor.getString(5);
				llon = cursor.getString(6);
				llat = cursor.getString(7);
				filename1=cursor.getString(8);
				names.put(fileName, new IndexItem(fileName, descr, date, size,
						rlon, rlat, llon, llat,filename1));
				// names.add(new
				// IndexItem(fileName,descr,date,size,rlon,rlat,llon,llat));
				// print("数据库中记录： "+fileName+", "+descr+" "+date+" "+size+" "+rlon+" "+rlat+" "+llon+" "+llat);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			print("查询数据库中有多少待下载队列时出错了： " + ex.getMessage());
		} finally {
			// db.close();
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return names;

	}

	// 查询数据库，判断是否已经下载完毕了。
	SQLiteDatabase db6;

	private DownloadInfo isDownloadComplenet(DownloadEntry download) {
		Cursor cursor = null;
		DownloadInfo downloadInfo = new DownloadInfo();
		if (db6 == null)
			db6 = dbHelper.getReadableDatabase();

		try {
			String target = download.urlToDownload;
			String path = download.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);

			String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");

			String sql = "SELECT downPath,progress FROM fileDownloading WHERE downPath=?";
			// print(sql+", "+urlStr);
			cursor = db6.rawQuery(sql, new String[] { urlStr });
			// print("检索数据库是否已经下载完毕： 。"+urlStr);
			Map<String, Float> data = new HashMap<String, Float>();
			boolean flag = true;
			while (cursor.moveToNext()) {
				// 有记录
				data.put(cursor.getString(0), cursor.getFloat(1));
				flag = false;
				// print("数据库中的下载完成标志： false");
				break;
			}
//			 print("下载完成标志： "+flag+" "+data.get(urlStr));
			downloadInfo.setCompleteFlag(flag);
			downloadInfo.setData(data);

			return downloadInfo;
		} catch (Exception ex) {
			print("查询数据库是否已经下载完毕发生了出错信息： " + ex.getMessage());
			ex.printStackTrace();
			return downloadInfo;
		} finally {
			// db6.close();
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	SQLiteDatabase db7;

	public void saveWaiting2Downloading(DownloadEntry download) {
		if (db7 == null)
			db7 = dbHelper.getWritableDatabase();
		try {

			String target = download.urlToDownload;
			String path = download.urlToDownload;
			String docname = target.substring(target.lastIndexOf("/") + 1);

			String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
					+ URLEncoder.encode(docname, "utf-8");

			db7.beginTransaction();
			String sql = "INSERT INTO fileDownloading(downPath,downLength,progress) values(?,?,?)";
			db7.execSQL(sql, new Object[] { urlStr, "0", "0" });
			// db7.execSQL(sql, new String[] {progress+"", currentDownloadSize +
			// "", path });

			db7.setTransactionSuccessful();

			// print(progress+", "+currentDownloadSize+", "+docfileName);
		} catch (Exception ex) {
			print("更新进度信息出错了！" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			db7.endTransaction();
			// db.close();
		}
	}

	SQLiteDatabase db8;

	public boolean getDownloadingInfo() {

		if (db8 == null)
			db8 = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {

			String sql = "SELECT downPath,progress FROM fileDownloading";

			cursor = db8.rawQuery(sql, new String[] {});
			// print("检索数据库是否已经下载完毕： 。"+urlStr);
			Map<String, Integer> data = new HashMap<String, Integer>();
			boolean flag = false;
			while (cursor.moveToNext()) {
				// 有记录
				data.put(cursor.getString(0), cursor.getInt(1));
				flag = false;
				break;
			}

			return flag;
		} catch (Exception ex) {
			print("查询数据库是否已经下载完毕发生了出错信息： " + ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
			// db8.close();
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public HashMap<String, DownloadOfflineMapAsynTask> getDownloadUrlDownloadServiceMap() {
		return downloadThread3.getCurrentRunningTaskMap();// downloadUrlDownloadServiceMap;
	}

	public void clearCurrentRunningAndWaitingWhenDownloadActivityFinish() {
		if (currentRunning.size() > 0) {
			currentRunning.clear();
		}
		if (!waiting.isEmpty()) {
			waiting.clear();
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
//		print("getGroupViewgetGroupView");
		View v = convertView;
		IndexItemCategory group = getGroup(groupPosition);
		if (v == null) {
			LayoutInflater inflater = downloadActivity.getLayoutInflater();
			v = inflater
					.inflate(R.layout.expandable_list_item_category,
							parent, false);
		}

		if(group.name==null){print("item.group.name null");}
		if(v==null){print("item.view null");}
		final View row = v;
		TextView item = (TextView) v.findViewById(R.id.category_name);
		if(item==null){print("item null");}
		item.setText(group.name);
//		print("item.setText"+group.name);
		adjustIndicator(groupPosition, isExpanded, v);
		return row;

	}

	public void setIndexFiles(List<IndexItem> indexFiles,
			Collection<? extends IndexItemCategory> cats) {
		this.indexFiles.clear();
		this.indexFiles.addAll(indexFiles);
		list.clear();
		list.addAll(cats);

		downloadActivity.isDowloadAll();
		downloadActivity.isPauseAll();
		downloadActivity.isDeleteAll();
		for (IndexItem l : indexFiles) {
			
//			print("得到的名称" + l.getDate()+l.getDescription());
			final java.text.DateFormat format = DateFormat.getDateFormat((Context) downloadActivity.getMyApplication());
//			print("得到的名称111" + l.getDate()+l.getDescription());
			format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			Date d = null;
			try {
				d = format.parse(l.getDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
			String date ="01.01.2015";
			if(d!=null){
				date=sdf.format(d);
			}
			print("date 得到的名称" + date+l.getDescription());
			
			app.mEditor.putString("easynav2.2"+l.getDescription(),date );
		}
     	notifyDataSetChanged();
//     	print("date 得到的名称 notifyDataSetChanged");
	}

	public List<IndexItem> getOfflineMapList() {
		return indexFiles;
	}

	public List<String> getDeletableOfflineMapNameList() {
		List<String> deletableList = new ArrayList<String>();
		updateLocalOfflineMapList();
		updateLocalPausedOfflineMapList();
		if (localDownloaedIndexFileNames != null
				&& localDownloaedIndexFileNames.keySet().size() > 0) {
			deletableList.addAll(localDownloaedIndexFileNames.keySet());
		}

		if (localPausedIndexFileNames != null
				&& localPausedIndexFileNames.keySet().size() > 0) {
			deletableList.addAll(localPausedIndexFileNames.keySet());
		}

		if (currentRunning != null && currentRunning.keySet().size() > 0) {
			Iterator<String> it = currentRunning.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				IndexItem e = currentRunning.get(key);
				pauseDownload(e);
				updateDeletedItem(e);
			}
		}

		if (waiting != null && waiting.keySet().size() > 0) {
			Iterator<String> it = waiting.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				IndexItem e = waiting.get(key);
				pauseDownload(e);
				updateDeletedItem(e);
			}
		}

		return deletableList;

	}

	// 看看有哪些离线包是“动过”的。指的是：正在下载的，等待下载的。
	public List<IndexItem> getCurrentRunningAndWaitingOfflineMapList() {
		List<IndexItem> touchedList = new ArrayList<IndexItem>();
		if (currentRunning != null && currentRunning.keySet().size() > 0) {
			Iterator<String> it = currentRunning.keySet().iterator();
			while (it.hasNext()) {

				String key = it.next();
				// print("正在下载的： "+key+", 现在要加入到要暂停的目标队列中。");
				IndexItem item = currentRunning.get(key);
				touchedList.add(item);
			}
		}

		if (waiting != null && waiting.keySet().size() > 0) {
			Iterator<String> it = waiting.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				// print("正在等待的： "+key+"，现在要加入到要暂停的目标队列中。");
				IndexItem item = waiting.get(key);
				touchedList.add(item);
			}
		}

		return touchedList;
	}

	public List<IndexItem> getTouchedMoreOfflineMapList() {

		List<IndexItem> touchedMoreList = new ArrayList<IndexItem>();

		updateLocalPausedOfflineMapList();// 已暂停的离线包

		if (currentRunning != null && currentRunning.keySet().size() > 0) {
			Iterator<String> it = currentRunning.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				IndexItem item = currentRunning.get(key);
				touchedMoreList.add(item);
				// print("将正在下载的对象添加到准备全部删除的队列。");
			}
		}

		if (waiting != null && waiting.keySet().size() > 0) {
			Iterator<String> it = waiting.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				IndexItem item = waiting.get(key);
				touchedMoreList.add(item);
				// print("将在等待中的对象添加到准备全部删除的队列。");
			}
		}

		if (indexFiles != null && indexFiles.size() > 0) {
			Iterator<IndexItem> it = indexFiles.iterator();
			while (it.hasNext()) {
				IndexItem item = it.next();

				// 满足下面的这个条件，表示已经下载了。
				if (localDownloaedIndexFileNames
						.containsKey(item.getFileName())) {
					// print("将已经下载的对象添加到准备全部删除的队列。");
					touchedMoreList.add(item);
				}

				// 满足下面的条件，表示已经在暂停状态了。
				if (localPausedIndexFileNames.containsKey(item.getFileName())) {
					// print("将下载暂停的对象添加到准备全部删除的队列。");
					touchedMoreList.add(item);
				}

			}
		}

		if (getDownloadingInfo()) {
			touchedMoreList.add(new IndexItem("", "", "", "", "", "", "", "",""));
		}

		return touchedMoreList;

	}

	// 看看有哪些离线包是未“动过”的。
	public List<IndexItem> getUnTouchedOfflineMapList() {
		List<IndexItem> unTouchedList = new ArrayList<IndexItem>();

		updateLocalOfflineMapList();// 本地已下载的离线包
		updateLocalPausedOfflineMapList();// 已暂停的离线包

		if (indexFiles != null && indexFiles.size() > 0) {
			Iterator<IndexItem> it = indexFiles.iterator();
			while (it.hasNext()) {
				IndexItem item = it.next();
				// 满足下面的这个条件，表示已经下载了。
				if (localDownloaedIndexFileNames
						.containsKey(item.getFileName())) {
					print(item.getFileName() + " 已下载，不加入到全部下载队列。");
					continue;
				}
				// 满足以下这个条件，说明已经在下载中了。
				if (currentRunning.containsKey(item.getFileName())) {
					// print(item.getFileName() + " 正在下载中，不加入到全部下载队列。");
					continue;
				}

				if (waiting.containsKey(item.getFileName())) {
					// print(item.getFileName() + " 已经在等待队列中，不加入到下载队列。");
					continue;
				}

				if (localPausedIndexFileNames.containsKey(item.getFileName())) {
					// print(item.getFileName() + " 在暂停中，应该加入到全部下载队列。");
					unTouchedList.add(item);
					continue;
				}

				// print("未动过： "+item.getFileName());
				unTouchedList.add(item);
			}
		} else {
			if (indexFiles != null) {
				print("indexFiles != null");
				print("size: " + indexFiles.size());
			} else {
				print("indexFiles 是空的： ");
			}
		}
		return unTouchedList;
	}

	// 刷新一下本地已有的离线地图文件清单。
	public void updateLocalOfflineMapList() {
		if (localDownloaedIndexFileNames != null) {
			localDownloaedIndexFileNames.clear();
		} else {
			localDownloaedIndexFileNames = new HashMap<String, String>();
		}

		File file = app.getAppPath(IndexConstants.TILES_INDEX_DIR);

		final String ext = IndexConstants.SQLITE_EXT;

		if (file.isDirectory()) {
			final java.text.DateFormat format = DateFormat.getDateFormat(app);
			format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(ext)) {
						String date = format.format(new File(dir, filename)
								.lastModified());
						localDownloaedIndexFileNames.put(filename, date);
						return true;
					} else {
						return false;
					}
				}
			});

		}
	}

	public void updateLocalPausedOfflineMapList() {
		if (localPausedIndexFileNames != null) {
			localPausedIndexFileNames.clear();
		} else {
			localPausedIndexFileNames = new HashMap<String, String>();
		}
		File file = app.getAppPath(IndexConstants.TILES_INDEX_DIR);
		final String ext = IndexConstants.DOWNLOAD_SQLITE_EXT;
		if (file.isDirectory()) {
			final java.text.DateFormat format = DateFormat.getDateFormat(app);
			format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(ext)) {
						String date = format.format(new File(dir, filename)
								.lastModified());
						filename = filename.substring(0,
								filename.lastIndexOf("."));
						localPausedIndexFileNames.put(filename, date);
						return true;
					} else {
						return false;
					}
				}
			});

		}
	}

	// 刷新等待任务信息数据
	public void updateWaitingQueue() {

		watingTaskFileNameListFromDB.clear();
		watingTaskFileNameListFromDB = getWaitingQueueFileNames();// 读取数据库中等待任务信息列表数据
		waiting.clear();
		if (!watingTaskFileNameListFromDB.isEmpty()) {
			waiting.putAll(watingTaskFileNameListFromDB);
		}
	}

	public void setLoadedFiles(Map<String, String> indexActivatedFileNames,
			Map<String, String> indexFileName) {
		print("setLoadedFilessetLoadedFiles");
		File file = app.getAppPath(IndexConstants.TILES_INDEX_DIR);
		final String ext = IndexConstants.SQLITE_EXT;
		if (localDownloaedIndexFileNames != null) {
			localDownloaedIndexFileNames.clear();
		} else {
			localDownloaedIndexFileNames = new HashMap<String, String>();
		}

		if (file.isDirectory()) {
			final java.text.DateFormat format = DateFormat.getDateFormat(app);
			format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(ext)) {
						String date = format.format(new File(dir, filename)
								.lastModified());
						localDownloaedIndexFileNames.put(filename, date);
						return true;
					} else {
						return false;
					}
				}
			});
		}
		print("setLoadedFilessetLoadedFileswan");
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public int getGroupCount() {
//		print("group count: " + list.size());
		return list.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
//		print("item size: " + list.get(groupPosition).items.size());
		return list.get(groupPosition).items.size();
	}

	@Override
	public IndexItemCategory getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public IndexItem getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).items.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition + (childPosition + 1) * 10000;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	private static void print(String msg) {

		android.util.Log.i("downloadadapter", msg);

	}

	private static final String TAG = "FileDownloader";

	// 按下按钮
	private final static float[] BUTTON_RELEASED = new float[] { 0.0f, 0, 0, 0,
			-50, 0, 0.0f, 0, 0, -50, 0, 0, 0.0f, 0, -50, 0, 0, 0, 0, 0 };

	// 按钮弹起
	private final static float[] BUTTON_PRESSED = new float[] { 1, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	private static final OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BUTTON_PRESSED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {

				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BUTTON_RELEASED));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}
	};

	public static void setButtonStateChangeListener(View v) {
		v.setOnTouchListener(touchListener);
	}

}
