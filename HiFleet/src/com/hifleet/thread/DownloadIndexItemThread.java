package com.hifleet.thread;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Message;
import android.widget.Toast;

import com.hifleet.activity.DownloadIndexActivity;
import com.hifleet.adapter.DownloadIndexAdapter;
import com.hifleet.adapter.IndexItemCategory;
import com.hifleet.data.DBHelper;
import com.hifleet.data.DownloadActivityType;
import com.hifleet.data.DownloadEntry;
import com.hifleet.data.DownloadOsmandIndexesHelper;
import com.hifleet.data.IndexFileList;
import com.hifleet.data.IndexItem;
import com.hifleet.map.AccessibleToast;
import com.hifleet.map.Algorithms;
import com.hifleet.map.BasicProgressAsyncTask;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;

public class DownloadIndexItemThread {

	private OsmandApplication app;
	// private IndexItem targetOfflineMap;
	private DBHelper dbHelper;
	private Context ctx;
	private ArrayList<DownloadOfflineMapAsynTask> currentRunningTaskList = new ArrayList<DownloadOfflineMapAsynTask>();
	private DownloadIndexAdapter adapter;
	private DownloadIndexActivity uiActivity = null;

	public DownloadIndexItemThread(Context ctx) {
		this.ctx = ctx;
		app = (OsmandApplication) ctx.getApplicationContext();
	}

	public DownloadIndexItemThread(Context ctx, DBHelper dbHelper,
			DownloadIndexAdapter adapter) {
		this.ctx = ctx;
		app = (OsmandApplication) ctx.getApplicationContext();
		this.dbHelper = dbHelper;// new DBHelper(ctx);
		this.adapter = adapter;
	}

	public void downloadOfflineMap(IndexItem item) {
		DownloadOfflineMapAsynTask downloadtask = new DownloadOfflineMapAsynTask(
				app, item);
		// targetOfflineMap = item;
		// currentRunningTaskList.add(downloadtask);
		taskMap.put(item.getFileName(), downloadtask);
		execute(downloadtask, new IndexItem[0]);
		// print("taskmap put: "+item.getFileName());
	}

	private HashMap<String, DownloadOfflineMapAsynTask> taskMap = new HashMap<String, DownloadOfflineMapAsynTask>();

	public HashMap<String, DownloadOfflineMapAsynTask> getCurrentRunningTaskMap() {
		// Iterator<String> it = taskMap.keySet().iterator();
		// while(it.hasNext()){
		// print("taskMap key: "+it.next());
		// }
		return taskMap;
	}

	public DownloadOfflineMapAsynTask getAsynTaskByItem(IndexItem item) {
		if (taskMap.containsKey(item.getFileName())) {
			return taskMap.get(item.getFileName());
		} else
			return null;
	}

	public DownloadOfflineMapAsynTask getCurrentRunningDownloadAsynTask() {
		if (taskMap != null && taskMap.keySet().size() > 0) {
			Iterator<String> it = taskMap.keySet().iterator();

			if (it.hasNext()) {
				String key = it.next();
				return taskMap.get(key);
			}
			return null;
		} else {
			return null;
		}
	}

	// public IndexItem getTargetIndexItem(){
	// return targetOfflineMap;
	// }

	private <P> void execute(BasicProgressAsyncTask<P, ?, ?> task,
			P... indexItems) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), indexItems);
		} else {
			task.execute(indexItems);
		}
	}

	public void setUiActivity(DownloadIndexActivity uiActivity) {
		this.uiActivity = uiActivity;
	}

	public class DownloadOfflineMapAsynTask extends
			BasicProgressAsyncTask<IndexItem, Object, String> {

		OsmandApplication app;

		private IndexItem myTargetItem;

		public int fileSize;
		/* 每条线程需要下载的数据量 */
		private int block;
		/* 保存文件地目录 */
		private File savedFile;

		private File targetFile;
		/* 下载地址 */
		private String path;
		private String o_path;

		/* 线程下载任务的起始点 */
		public int start;
		/* 线程下载任务的结束点 */
		private int end;

		/* 当前已下载量 */
		public int currentDownloadSize = 0;
		private boolean interruptedFlag = false;

		private Map<String, Integer> downloadedLength = new ConcurrentHashMap<String, Integer>();

		public DownloadOfflineMapAsynTask(Context context, IndexItem item) {
			super(context);
			app = (OsmandApplication) context.getApplicationContext();
			myTargetItem = item;
			// app.getMainLooper();
		}

		// 设下载中断
		public void setInterrupted(boolean interrupted) {
			// print("thread 设置中断。");
			interruptedFlag = interrupted;
		}

		protected void onPreExecute() {

		}

		protected String doInBackground(IndexItem... filesToDownload) {
			String msgReturn = "";
			try {
				// app.getMainLooper();
				int thread_size = 1;
				int id = 0;
				DownloadEntry de = myTargetItem.createDownloadEntry(app,
						DownloadActivityType.NORMAL_FILE);
				String target = de.urlToDownload;

				path = target;

				o_path = target;

				String docname = target.substring(target.lastIndexOf("/") + 1);
				String docnamenoext = docname
						.substring(0, docname.indexOf("."));

				File destinationParentDir = de.targetFileParentDir;
				String urlStr = path.substring(0, path.lastIndexOf("/")) + "/"
						+ URLEncoder.encode(docname, "utf-8");

				this.path = urlStr;
				final URL url = new URL(urlStr);
//				System.out.println("pppppp===" + url);
				HttpURLConnection conn = null;
				try {
					conn = (HttpURLConnection) url.openConnection();

					conn.setRequestProperty("User-Agent", "com.test3.app");
					conn.setReadTimeout(30000);
					conn.setConnectTimeout(30000);

					conn.setRequestMethod("GET");

					if (conn.getResponseCode() != 200) {
						// print("server no response!");
						//
						// app.showShortToastMessage("服务器没有反应，请检查网络连接状况！");
						// AccessibleToast.makeText(ctx, "服务器没有反应，请检查网络连接状况。",
						// Toast.LENGTH_SHORT).show();
						throw new RuntimeException(app.getResources()
								.getString(R.string.server_no_response));// "server no response!");
					}

					fileSize = conn.getContentLength();
					// app.getMainLooper();
					if (fileSize <= 0) {
						// print("file is incorrect!");
						// app.showShortToastMessage("文件错误！");
						// AccessibleToast.makeText(ctx, "文件错误。",
						// Toast.LENGTH_SHORT).show();
						throw new RuntimeException(app.getResources()
								.getString(R.string.file_error));// "文件错误。");
					}
				} catch (Exception ex) {
					// app.showShortToastMessage("连接服务器发生异常，请检查网络连接状况。");
					// AccessibleToast.makeText(ctx, "连接服务器发生异常，请检查网络连接状况。",
					// Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
					taskMap.remove(myTargetItem.getFileName());
					return app.getResources().getString(
							R.string.network_connection_error);// "连接服务器发生异常，请检查网络连接状况。";
				}

				// if(conn==null) {
				// //app.showShortToastMessage("未能建立有效的网络连接，请检查网络连接状况。");
				// //AccessibleToast.makeText(ctx, "未能建立有效的网络连接，请检查网络连接状况。",
				// Toast.LENGTH_SHORT).show();
				// return "未能建立有效的网络连接，请检查网络连接状况。";
				// }

				String fileName = getFileName(conn);

				this.savedFile = new File(destinationParentDir, fileName
						+ ".download");
				this.targetFile = new File(destinationParentDir, fileName);

				// print("保存路径2："+this.savedFile.getPath()+", "+this.savedFile.getName());
				RandomAccessFile doOut = new RandomAccessFile(savedFile, "rwd");
				doOut.setLength(fileSize);

				// doOut.close();

				// conn.disconnect();

				if (isPathInTable(path)) {
					// print("当前下载有记录保存："+docname);
					// print("查进度： "+docname);
					downloadedLength = getDownloadedLength(path);
					if (downloadedLength != null
							&& downloadedLength.keySet().size() > 0) {
						currentDownloadSize = downloadedLength.get(path);
						// print("上次进度查到了： "+currentDownloadSize);
					} else {
						// print("没查到进度。");
						currentDownloadSize = 0;
					}
				} else {
					// print("这是一个新下载的任务。");
					saveDownloading(path, 0, 0, 0);
					currentDownloadSize = 0;
				}

				// print("currentDownloadSize: "+currentDownloadSize);

				int p_gress = (int) ((double) currentDownloadSize / (double) fileSize) * 1000;

				block = fileSize % thread_size == 0 ? fileSize / thread_size
						: fileSize / thread_size + 1;

				start = id * block + currentDownloadSize;
				end = (id + 1) * block;
				// print("block: "+block);

				// 先删除原先的记录，再重新添加。
				// print("先删除原先的记录，再重新添加。");
				// deleteDownloading(path);
				// saveDownloading(path,0,currentDownloadSize,p_gress);
				HttpURLConnection conn2 = null;
				try {

					conn2 = (HttpURLConnection) new URL(urlStr)
							.openConnection();
					conn2.setConnectTimeout(5000);
					conn2.setRequestMethod("GET");
					conn2.setRequestProperty("Range", "bytes=" + start + "-"
							+ end); // 设置获取数据的范围
				} catch (Exception ex) {
					ex.printStackTrace();
					// app.showShortToastMessage("连接服务器发生异常，请检查网络连接状况。");
					// AccessibleToast.makeText(ctx, "连接服务器发生异常，请检查网络连接状况。",
					// Toast.LENGTH_SHORT).show();
					taskMap.remove(myTargetItem.getFileName());
					adapter.pauseDownload(myTargetItem);
					return app.getResources().getString(
							R.string.network_connection_error);// "未能建立有效的网络连接，请检查网络连接状况。";
				}

				// conn.setRequestProperty("Range", "bytes=" + start + "-" +
				// end); // 设置获取数据的范围

				// print("数据请求范围： "+start+"-- "+end);

				// if(conn2 ==null){
				// //app.showShortToastMessage("未能建立有效的网络连接，请检查网络连接状况。");
				// AccessibleToast.makeText(ctx, "未能建立有效的网络连接，请检查网络连接状况。",
				// Toast.LENGTH_SHORT).show();
				// return "";
				// }

				InputStream in = conn2.getInputStream();
				byte[] buffer = new byte[32256];// 31.5 KB
				int len = 0;
				doOut.seek(start);
				int previousProgress = p_gress;
               System.err.println("previousProgress "+previousProgress);
				int lengthLimit = 0;
				long time =0;
				try {
					while (!interruptedFlag && (len = in.read(buffer)) != -1) {
						// 写文件
						doOut.write(buffer, 0, len);
						//
						lengthLimit+=len;
						// if(lengthLimit>=1024*1024){
						// //已经超过1M大小了
						// }

						currentDownloadSize += len;
                        
//						System.out.println("my progresss:::"+(int) (((double) currentDownloadSize / (double) fileSize) * 1000f));
						progress = (int) (((double) currentDownloadSize / (double) fileSize) * 1000f);
						// 更新进度
						updateDownloading(currentDownloadSize, progress, path);// 实际记录下载的进度信息
//                        System.err.println("progress"+progress+"pre"+previousProgress);
						if (progress > 0 && progress > previousProgress) {
							long t=(System.currentTimeMillis()-time);
//							if((t/1000)!=0&&1024/(t/1000)!=0)
//								print("progress: "+progress+" time"+t+" "+lengthLimit/1024f/(t/1000f)+"kB/s"+"lengthLimit"+lengthLimit+" "+fileSize);
							lengthLimit=0;
							time=System.currentTimeMillis();
							Message message = new Message();
							message.what = 1;
							DownloadIndexActivity.handler.sendMessage(message);
//							publishProgress(progress);
							previousProgress = progress;
						}

					}
				} catch (Exception ex) {
					ex.printStackTrace();
					// app.showShortToastMessage("读取离线数据包文件发生错误，请检查网络连接状况。");
					// AccessibleToast.makeText(ctx, "读取离线数据包文件发生错误，请检查网络连接状况。",
					// Toast.LENGTH_SHORT).show();
					taskMap.remove(myTargetItem.getFileName());
					this.setInterrupted(true);
					adapter.pauseDownload(myTargetItem);
					return app.getResources().getString(
							R.string.read_offline_map_file_error);// "读取离线数据包文件发生错误，请检查网络连接状况。";
				}

				// print(myTargetItem.getFileName()+" 跳出while读取循环。");

				if (interruptedFlag) {
					// 是暂停退出的
					// print("是暂停退出的。要通知一下.");
					// adapter.updateLocalOfflineMapList();
					// adapter.notifyTaskPaused();
					msgReturn = "";// docnamenoext +" 下载暂停。";
				} else {
					// 是文件下载完毕退出的。
					// print("是文件下载完毕退出的。把记录中的数据清除掉。");

					// app.showShortToastMessage(docnamenoext+"下载成功。");
					// AccessibleToast.makeText(ctx, docnamenoext+"下载成功。",
					// Toast.LENGTH_SHORT).show();

					// print("然后要对文件进行重命名！还要通知一下");
					msgReturn = "";// docnamenoext +" 下载成功。";

					boolean renameFlag = this.savedFile
							.renameTo((this.targetFile));

					if (renameFlag) {
						// print("重命名成功！现在要删除文件。");
						if (Algorithms.removeAllFiles(this.savedFile)) {
							// print("删除.download文件成功！");
						} else {
							// print("删除.download文件失败！");
						}
					} else {
						// print("重命名失败，还不能删除文件呢。");
					}
					adapter.updateLocalOfflineMapList();
					deleteDownloading(path);
					adapter.notifyTaskCompleted(myTargetItem.getFileName());
				}

				// print("thread准备从当前队列中移除当前任务。");

				// if(currentRunningTaskList.size()>0){
				// currentRunningTaskList.remove(0);
				// }
				if (!taskMap.isEmpty()) {
					// print("thread taskMap非空。移除一个："+myTargetItem.getFileName());
					taskMap.remove(myTargetItem.getFileName());
				}

				// print("准备关闭随机读写文件。");
				doOut.close();
				// print("准备关闭输入流");
				// in.close();
				// print("准备关闭http连接。");
				conn2.disconnect();
				// print("关闭http连接 2。");
				conn.disconnect();
				// print("关闭http连接 1。");

				// RandomAccessFile targetSavedFile = new
				// RandomAccessFile(savedFile, "rwd");
				// targetSavedFile.seek(start);

			} catch (Exception ex) {
				ex.printStackTrace();
				print("出错了！" + ex.getMessage());
				msgReturn = app.getResources().getString(
						R.string.exception_occured)/* "出现异常：" */
						+ ex.getMessage();
				taskMap.remove(myTargetItem.getFileName());
			}
			return msgReturn;
		}

		private String getFileName(HttpURLConnection conn) {
			String fileName = o_path.substring(o_path.lastIndexOf("/") + 1,
					o_path.length());
			// print("fileName: "+fileName);
			return fileName;// +".download";
		}

		protected void onProgressUpdate(Object... values) {
			for (Object o : values) {
				if (o instanceof Integer) {
//					 print("进度信息： "+o);
//					adapter.notifyDataSetChanged();
				}

			}

			super.onProgressUpdate(values);

		}

		protected void updateProgress(boolean updateOnlyProgress) {

		}

		protected void onPostExecute(String result) {
			if (result.length() > 1) {
				print(result);
				AccessibleToast.makeText(ctx, result, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	SQLiteDatabase db1;

	// 在数据表中是否有当前下载地址对应的下载进度记录？
	private boolean isPathInTable(String path) {
		Cursor cursor = null;
		if (db1 == null)
			db1 = dbHelper.getReadableDatabase();
		try {
			String sql = "SELECT downPath,downLength FROM fileDownloading WHERE downPath=?";
			// String sql ="SELECT downPath,downLength FROM fileDownloading";
			// print("查这个下载地址是否已存在： "+docfileName);
			cursor = db1.rawQuery(sql, new String[] { path });
			// Cursor cursor = db.rawQuery(sql, null);
			// while(cursor.moveToNext()){
			// print(cursor.getString(0)+", "+cursor.getInt(1));
			// }
			if (cursor.moveToNext()) {
				// print("数据表中有的。");
				return true;
			} else {
				// print("数据表中没有的。");
				return false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return false;
	}

	// 保存一下进度信息，相当于初始化。
	SQLiteDatabase db2;

	private void saveDownloading(String path, int threadid, int dwLength,
			int pgrs) {
		if (db2 == null)
			db2 = dbHelper.getWritableDatabase();
		try {
			db2.beginTransaction();
			String sql = "INSERT INTO fileDownloading(downPath,downLength,progress) values(?,?,?)";
			db2.execSQL(sql, new Object[] { path, dwLength, pgrs });
			db2.setTransactionSuccessful();

			// print("保存进度信息： "+docfileName+", "+dwLength+", "+pgrs);
		} catch (Exception ex) {
			ex.printStackTrace();
			print("保存进度信息出错了！" + ex.getMessage());
		} finally {
			db2.endTransaction();
			// db.close();
		}
	}

	SQLiteDatabase db3;

	private void deleteDownloading(String path) {
		// print("deleteDownloading "+docfileName);
		if (db3 == null)
			db3 = dbHelper.getWritableDatabase();
		String sql = "DELETE FROM fileDownloading WHERE downPath=?";
		db3.execSQL(sql, new Object[] { path });
		// db.close();
	}

	SQLiteDatabase db4;

	public void updateDownloading(int currentDownloadSize, int progress,
			String path) {
		if (db4 == null)
			db4 = dbHelper.getWritableDatabase();
		try {
			db4.beginTransaction();
			String sql = "UPDATE fileDownloading SET progress=?, downLength=? WHERE downPath=?";
			float p=progress/10f;
//			System.err.println("progress/10:"+p+" " +progress);
			db4.execSQL(sql, new String[] { p + "",
					currentDownloadSize + "", path });

			db4.setTransactionSuccessful();

			// print(progress+", "+currentDownloadSize+", "+docfileName);
		} catch (Exception ex) {
			print("更新进度信息出错了！" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			db4.endTransaction();
			// db.close();
		}
	}

	SQLiteDatabase db5;

	private Map<String, Integer> getDownloadedLength(String path) {
		if (db5 == null)
			db5 = dbHelper.getReadableDatabase();
		String sql = "SELECT downPath,downLength FROM fileDownloading WHERE downPath=?";
		// print("查进度信息： "+docfileName);
		Cursor cursor = db5.rawQuery(sql, new String[] { path });

		Map<String, Integer> data = new HashMap<String, Integer>();
		// print("cursor getcount: "+cursor.getCount());
		while (cursor.moveToNext()) {
			// print("cursor moveToNext");
			data.put(cursor.getString(0), cursor.getInt(1));
		}
		cursor.close();
		cursor = null;
		// db.close();
		// print("查完进度信息，return;");
		return data;
	}

	// 后台再启动一个线程，尝试去下载一下网络上的离线包目录indexes.xml
	public void runLoadIndexFilesFromInternetInBackground() {
		final BasicProgressAsyncTask<Void, Void, IndexFileList> inst = new BasicProgressAsyncTask<Void, Void, IndexFileList>(
				ctx) {
			@Override
			protected IndexFileList doInBackground(Void... params) {
				return DownloadOsmandIndexesHelper
						.getIndexesListFromIneternet(ctx);
			};

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(IndexFileList result) {
			}

			@Override
			protected void updateProgress(boolean updateOnlyProgress) {

			};

		};
		execute(inst, new Void[0]);
	}

	// 异步线程请求离线包里列表的xml文件，然后在list中展示这个列表。
	public void runReloadIndexFiles() {
		final BasicProgressAsyncTask<Void, Void, IndexFileList> inst = new BasicProgressAsyncTask<Void, Void, IndexFileList>(
				ctx) {
			@Override
			protected IndexFileList doInBackground(Void... params) {
				return DownloadOsmandIndexesHelper.getIndexesList(ctx);
			};

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(IndexFileList result) {
				indexFiles = result;
				// uiActivity.getAdapter().setLoadedFiles(indexActivatedFileNames,
				// indexFileNames);
				// uiActivity.getAdapter().notifyDataSetChanged();

				if (uiActivity != null) {
					runCategorization(uiActivity.getType());
				}
			}

			@Override
			protected void updateProgress(boolean updateOnlyProgress) {

			};

		};
		execute(inst, new Void[0]);
	}

	public void runCategorization(final DownloadActivityType type) {

		final BasicProgressAsyncTask<Void, Void, List<IndexItem>> inst = new BasicProgressAsyncTask<Void, Void, List<IndexItem>>(
				ctx) {

			private List<IndexItemCategory> cats;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				this.message = ctx.getString(R.string.downloading_list_indexes);
			}

			@Override
			protected List<IndexItem> doInBackground(Void... params) {
				final List<IndexItem> filtered = getFilteredByType();
				cats = IndexItemCategory.categorizeIndexItems(app, filtered);
				updateLoadedFiles();
				return filtered;
			};

			public List<IndexItem> getFilteredByType() {
				final List<IndexItem> filtered = new ArrayList<IndexItem>();
				List<IndexItem> cachedIndexFiles = getCachedIndexFiles();
				if (cachedIndexFiles != null) {
					for (IndexItem file : cachedIndexFiles) {
						if (file.getType() == type) {
							filtered.add(file);
						}
					}
				}
				return filtered;
			}

			@Override
			protected void onPostExecute(List<IndexItem> filtered) {
				if (uiActivity != null) {
					DownloadIndexAdapter a = ((DownloadIndexAdapter) uiActivity
							.getExpandableListAdapter());
					// DownloadIndexAdapter a=uiActivity.getAdapter();
					a.setLoadedFiles(indexActivatedFileNames, indexFileNames);
					a.setIndexFiles(filtered, cats);
					a.notifyDataSetChanged();
					// //试图展开下载列表
//					for (IndexItem l : filtered) {
//						print("试图展开下载列表。" + l.getFileName());
//					}
					uiActivity.expandList();
					print("试图展开下载列表。完成" );
				}
				// currentRunningTask.remove(this);
			}

			@Override
			protected void updateProgress(boolean updateOnlyProgress) {

			};

		};

		execute(inst, new Void[0]);
	}

	// 看看本地已经下载的离线海图文件
	public void updateLoadedFiles() {
		DownloadIndexActivity.listWithAlternatives(app,
				app.getAppPath(IndexConstants.TILES_INDEX_DIR),
				IndexConstants.SQLITE_EXT, indexFileNames);
	}

	private Map<String, String> indexFileNames = new LinkedHashMap<String, String>();
	private Map<String, String> indexActivatedFileNames = new LinkedHashMap<String, String>();
	private IndexFileList indexFiles = null;

	public List<IndexItem> getCachedIndexFiles() {
		return indexFiles != null ? indexFiles.getIndexFiles() : null;
	}

	private static void print(String msg) {
		android.util.Log.i(TAG, msg);
	}

	public static final String TAG = "DownloadIndexItemThread";
}
