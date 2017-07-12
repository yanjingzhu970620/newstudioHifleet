package com.hifleet.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ScrollView;

import com.actionbarsherlock.view.Menu;
import com.hifleet.adapter.DownloadIndexAdapter;
import com.hifleet.data.ActionItem;
import com.hifleet.data.AndroidUtils;
import com.hifleet.data.DeleteAllDownloadedOfflineMapThread;
import com.hifleet.data.DownloadActivityType;
import com.hifleet.data.IndexItem;
import com.hifleet.data.MoveFilesToDifferentDirectory;
import com.hifleet.data.QuickAction;
import com.hifleet.data.SuggestExternalDirectoryDialog;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandSettings;
import com.hifleet.map.ResourceManager;
import com.hifleet.plus.R;
import com.hifleet.thread.DownloadIndexItemThread;
import com.hifleet.thread.DownloadIndexItemThread.DownloadOfflineMapAsynTask;

public class DownloadIndexActivity extends OsmandExpandableListActivity {

	private static OsmandApplication app;
	private OsmandSettings settings;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"dd.MM.yyyy");
	private DownloadActivityType type = DownloadActivityType.NORMAL_FILE;

	private static ResourceManager mgr;
	private File dirWithTiles;
	static DownloadIndexAdapter adapter;
	private static DownloadIndexItemThread totalDownloadListIndexThread;
	static DownloadIndexActivity activity;
	private Map<String, String> indexFileNames = new LinkedHashMap<String, String>();
	static boolean isDownloadFromCurrentNetWork = false;
	static boolean freeSpaceAvailable = false;
	private static final int CHOOSE_STORAGE_PLACE = 7;

	private Button mDowloadAll, mPauseAll, mDeleteAll;
	Boolean mDowload = true, mPause = true, mDelete = true;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// 更改存储路径
		// createMenuItem(menu, CHOOSE_STORAGE_PLACE,
		// R.string.choose_storage_place,
		// R.drawable.ic_action_gsave_light,
		// R.drawable.ic_action_gsave_dark,
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// getMenuInflater().inflate(R.menu.choose_storage_place_actionbar_layout,
		// (android.view.Menu) menu);
		this.getSupportMenuInflater().inflate(
				R.menu.choose_storage_place_actionbar_layout, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		// print("onOptionsItemSelected");

		if (item.getItemId() == /* CHOOSE_STORAGE_PLACE */R.id.choose_storage_place_id) {
			// System.err.println("全部下载");
			SuggestExternalDirectoryDialog.showDialog(this, null, null);
			return true;
		}

		HashMap<String, DownloadOfflineMapAsynTask> downloadUrlDownloadServiceMap = getAdapter()
				.getDownloadUrlDownloadServiceMap();

		if (downloadUrlDownloadServiceMap.isEmpty()) {
			// 没有正在下载的作业。
			// print("没有正在下载的作业，直接退出。");
			finishThisActivity();
			return true;
		}

		if (!downloadUrlDownloadServiceMap.isEmpty()) {
			// print("有在下载的动作，打开警告框");
			openWaringDialogWhenExit(downloadUrlDownloadServiceMap);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			HashMap<String, DownloadOfflineMapAsynTask> downloadUrlDownloadServiceMap = getAdapter()
					.getDownloadUrlDownloadServiceMap();

			if (downloadUrlDownloadServiceMap.isEmpty()) {
				finishThisActivity();
				return true;
			}

			// 如果有在下载的，则弹出对话框警告一下。
			if (!downloadUrlDownloadServiceMap.isEmpty()) {
				// print("有在下载的动作，打开警告框");
				openWaringDialogWhenExit(downloadUrlDownloadServiceMap);
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	// 当退出当前界面时，如果有正在下载的作业或者正在暂停的作业。需要弹出提示对话框
	private void openWaringDialogWhenExit(
			final HashMap<String, DownloadOfflineMapAsynTask> progressingDownloading) {
		Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getResources().getString(
				R.string.exit_offline_map_download_wnd_warning_dlg_title)/* "退出离线包下载界面" */);
		builder.setMessage(activity.getResources().getString(
				R.string.warning_message)/* "尚有离线包在下载中,此时退出将暂停下载。您确定退出吗？" */);
		builder.setPositiveButton(
		/* "确定退出" */activity.getResources().getString(R.string.quedingtuichu),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Iterator<String> it = progressingDownloading.keySet()
								.iterator();
						while (it.hasNext()) {
							DownloadOfflineMapAsynTask downloadService = progressingDownloading
									.get(it.next());
							// downloadService.isPause=true;
							downloadService.setInterrupted(true);
						}
						getAdapter()
								.clearCurrentRunningAndWaitingWhenDownloadActivityFinish();
						finishThisActivity();
					}
				});

		builder.setNegativeButton(
				activity.getResources().getString(R.string.quxiaotuichu), null);

		builder.create().show();
	}

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public static DownloadIndexAdapter getAdapter() {
		return adapter;
	}

	// 全部下载
	private void downloadAllOfflineMap() {
		List<IndexItem> offlineMapList = getAdapter()
				.getUnTouchedOfflineMapList();
		if (offlineMapList.size() > 0) {
			Iterator<IndexItem> it = offlineMapList.iterator();
			while (it.hasNext()) {
				IndexItem item = it.next();
				getAdapter().download(item);
			}
			System.out.println("开始全部下载");
		}
	}

	// 全部暂停
	private void puseAllDownloadActions() {
		List<IndexItem> offlineMapList = getAdapter()
				.getCurrentRunningAndWaitingOfflineMapList();
		if (offlineMapList.size() > 0) {
			Iterator<IndexItem> it = offlineMapList.iterator();
			while (it.hasNext()) {
				IndexItem item = it.next();
				getAdapter().pauseDownloadNoAutoActivateNextTask(item);
			}
		}
		isDowloadAll();
		isPauseAll();
		isDeleteAll();

	}

	// 全部删除
	private void deleteAllOfflineMap() {
		openDeleteAllOfflineMapConfirmDialog();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.offline_map_activity);
		activity = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		app = getMyApplication();
		app.showShortToastMessage(getResources().getString(
				R.string.offline_map_attention));
		getSupportActionBar().setTitle(R.string.offline_map_activity_title);
		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		// setSupportProgressBarIndeterminateVisibility(false);

		mDowloadAll = (Button) findViewById(R.id.button_download_all);
		mPauseAll = (Button) findViewById(R.id.button_pause_all);
		mDeleteAll = (Button) findViewById(R.id.button_delete_all);

		settings = app.getSettings();
		mgr = app.getResourceManager();
		dirWithTiles = app.getAppPath(IndexConstants.TILES_INDEX_DIR);
		if (totalDownloadListIndexThread == null) {
			totalDownloadListIndexThread = new DownloadIndexItemThread(this);
		}

		List<IndexItem> list = new ArrayList<IndexItem>();

		adapter = new DownloadIndexAdapter(this, list);
		setListAdapter(adapter);

		totalDownloadListIndexThread.setUiActivity(this);
		totalDownloadListIndexThread.runReloadIndexFiles();

		if (app.getResourceManager().getIndexFileNames().isEmpty()) {
			boolean showedDialog = false;
			boolean hasSelectedStoragePosition = settings.SELECTED_OFFLINE_MAP_SAVE_DIR
					.get();
			if (Build.VERSION.SDK_INT <= OsmandSettings.VERSION_DEFAULTLOCATION_CHANGED
					&& !hasSelectedStoragePosition) {
				SuggestExternalDirectoryDialog.showDialog(this, null, null);
			}

		}

		listWithAlternatives(app,
				app.getAppPath(IndexConstants.TILES_INDEX_DIR),
				IndexConstants.SQLITE_EXT, indexFileNames);
	}

	public void isDowloadAll() {
		if (getAdapter().getUnTouchedOfflineMapList().size() == 0) {// 符合条件，禁用按钮
			// mDowloadAll.setTextColor(0x99000000);
			mDowloadAll.setTextColor(getResources().getColor(
					R.color.disable_background_color));
			mDowloadAll.setBackgroundResource(R.drawable.button_menu_pressed);
			mDowload = false;// 全部已下载或者全部在排队中
			// print("没有未动过的离线包。全部下载按钮不能触发。");
		} else {
			// mDowloadAll.setTextColor(0xff000000);
			mDowloadAll.setTextColor(getResources().getColor(
					R.color.enable_background_color));
			mDowloadAll
					.setBackgroundResource(R.drawable.bg_button_download_side);
			mDowload = true;// 可以点击全部下载按钮
			// print("有未动过的离线包。全部现在按钮能触发。");
		}
	}

	public void isPauseAll() {
		if (getAdapter().getCurrentRunningAndWaitingOfflineMapList().size() == 0) {// 符合条件，禁用按钮
			// mPauseAll.setTextColor(0x99000000);
			mPauseAll.setTextColor(getResources().getColor(
					R.color.disable_background_color));
			mPauseAll.setBackgroundResource(R.drawable.button_menu_mid_pressed);
			mPause = false;// 不可以点击全部暂停按钮
		} else {
			// mPauseAll.setTextColor(0xff000000);
			mPauseAll.setTextColor(getResources().getColor(
					R.color.enable_background_color));
			mPauseAll.setBackgroundResource(R.drawable.bg_button_download);
			mPause = true;// 可以点击全部暂停按钮
		}
	}

	public void isDeleteAll() {
		if (getAdapter().getTouchedMoreOfflineMapList().size() == 0) {// 符合条件，禁用按钮
			// mDeleteAll.setTextColor(0x99000000);
			mDeleteAll.setTextColor(getResources().getColor(
					R.color.disable_background_color));
			mDeleteAll.setBackgroundResource(R.drawable.button_menu_pressed);
			mDelete = false;// 不可以点击全部删除按钮
			// print("禁用全部删除按钮。");
		} else {
			// mDeleteAll.setTextColor(0xff000000);
			mDeleteAll.setTextColor(getResources().getColor(
					R.color.enable_background_color));
			mDeleteAll
					.setBackgroundResource(R.drawable.bg_button_download_side);
			mDelete = true;// 可以点击全部删除按钮
			// print("开放全部删除按钮。");
		}
	}

	@SuppressLint("ResourceAsColor")
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_download_all:
			if (mDowload) {
				// mDowloadAll.setTextColor(0x99000000);
				mDowloadAll.setTextColor(getResources().getColor(
						R.color.disable_background_color));
				mDowloadAll
						.setBackgroundResource(R.drawable.button_menu_pressed);
				downloadAllOfflineMap();
			}
			isPauseAll();
			isDeleteAll();
			break;
		case R.id.button_pause_all:
			if (mPause) {
				mPause = false;
				// mPauseAll.setTextColor(0x99000000);
				mPauseAll.setTextColor(getResources().getColor(
						R.color.disable_background_color));
				mPauseAll.setBackgroundResource(R.drawable.bg_button_download);
				puseAllDownloadActions();
			}
			isDowloadAll();
			isDeleteAll();
			break;
		case R.id.button_delete_all:
			if (mDelete) {
				mDelete = false;
				// mDeleteAll.setTextColor(0x99000000);
				mDeleteAll.setTextColor(getResources().getColor(
						R.color.disable_background_color));
				mDeleteAll
						.setBackgroundResource(R.drawable.button_menu_pressed);
				deleteAllOfflineMap();
			}
			isDowloadAll();
			isPauseAll();
			break;
		}
	}

	// 此函数可以忽略，不起作用了。
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		final DownloadIndexAdapter listAdapter = (DownloadIndexAdapter) getExpandableListAdapter();
		final IndexItem e = (IndexItem) listAdapter.getChild(groupPosition,
				childPosition);
		final QuickAction qa = new QuickAction(v);

		// 开始下载
		final ActionItem downladOfflineMap = new ActionItem();
		downladOfflineMap.setIcon(activity.getResources().getDrawable(
				R.drawable.ic_action_gdown_light));

		if (e.getIsDownloading()) {
			downladOfflineMap.setTitle(activity
					.getString(R.string.pause_offline_map));
		} else if (e.isPaused()) {
			downladOfflineMap.setTitle(activity
					.getString(R.string.resume_offline_map));
		} else {
			downladOfflineMap.setTitle(activity
					.getString(R.string.download_offline_map));
		}

		downladOfflineMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// deleteMyPlanedRoute(point);
				if (downladOfflineMap.getTitle().compareTo("开始下载") == 0
						|| downladOfflineMap.getTitle().compareTo("继续下载") == 0) {
					// print("开始下载命令！");
					listAdapter.download(e);
				}

				if (downladOfflineMap.getTitle().compareTo("暂停下载") == 0) {
					// print("暂停下载命令！");
					listAdapter.pauseDownload(e);
				}

				qa.dismiss();
			}
		});

		qa.addActionItem(downladOfflineMap);

		ActionItem deleteOfflineMap = new ActionItem();
		deleteOfflineMap.setTitle(getString(R.string.delete_offline_map));
		deleteOfflineMap.setIcon(getResources().getDrawable(
				R.drawable.ic_action_delete_light));

		deleteOfflineMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// deleteMyPlanedRoute(point);
				openDeleteOfflineMapConfirmDialog(e, e.getFileName(),
						e.getDescription());
				qa.dismiss();
			}
		});

		qa.addActionItem(deleteOfflineMap);
		qa.show();

		return true;

	}

	// 当点击删除所有时，打开一个确认窗口。
	private void openDeleteAllOfflineMapConfirmDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.confirm_to_delete_all_offline_map_dialog_title));// "确认删除所有已下载离线数据包");
		builder.setMessage(getResources().getString(
				R.string.delete_all_downloaded_offline_maps_warning_message));// "删除离线数据包，将导致在离线环境下无法正常显示某些区域的大比例尺海图。\n您确定要删除所有已下载的离线海图包吗？");
		builder.setNegativeButton(
				getResources().getString(R.string.cancel_delete_all)/* "取消删除" */,
				null);
		builder.setPositiveButton(
		/* "确认删除" */getResources().getString(R.string.confirm_delete_all),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						List<IndexItem> offlineMapList = getAdapter()
								.getOfflineMapList();
						Iterator<IndexItem> it = offlineMapList.iterator();
						while (it.hasNext()) {
							IndexItem item = it.next();
							getAdapter().pauseDownloadNoAutoActivateNextTask(
									item);
							app.mEditor.putString(item.getDescription(), "不存在")
									.commit();
							System.out.println(item.getDescription());
						}

						DeleteAllDownloadedOfflineMapThread tHread = new DeleteAllDownloadedOfflineMapThread(
								app, getAdapter());
						tHread.startDelete();

					}
				});

		builder.create().show();
	}

	// 检查按钮的状态。
	public void checkButtonStatus() {
		isDowloadAll();
		isPauseAll();
		isDeleteAll();
	}

	// 打开确认删除离线数据包的窗口
	public void openDeleteOfflineMapConfirmDialog(final IndexItem e,
			String offlineMapDataFileName, final String areaName) {
		final String fileNameToDelete = offlineMapDataFileName;
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.confirm_to_delete_offline_map_dialog_title)/* "确认删除离线数据包" */);
		builder.setMessage(getResources().getString(
				R.string.delete_all_downloaded_offline_maps_warning_message)/* "删除离线数据包，将导致在离线环境下无法正常显示某些区域的大比例尺海图。\n您确定要删除吗？" */);
		builder.setNegativeButton(
				getResources().getString(R.string.cancel_delete_all)/* "取消删除" */,
				null);
		builder.setPositiveButton(
				getResources().getString(R.string.confirm_delete_all)/* "确认删除" */,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// print("用户确认删除: "+fileNameToDelete);
						// 执行删除操作！
						// print("通知adapter暂停该离线包下载。");
						// getAdapter().pauseDownload(e);

						getAdapter().deleteDownload(e);

						OsmandApplication app = (OsmandApplication) getApplication();
						File file2delete = new File(app
								.getAppPath(IndexConstants.TILES_INDEX_DIR)
								+ "/" + fileNameToDelete);
						print("删除文件路径1： " + file2delete.getPath());
						File file2delete_download = new File(app
								.getAppPath(IndexConstants.TILES_INDEX_DIR)
								+ "/" + fileNameToDelete + ".download");
						print("删除文件路径2： " + file2delete_download.getPath());
						if (file2delete.delete()
								|| file2delete_download.delete()) {
							// print("删除成功！");
							app.showShortToastMessage(getResources().getString(
									R.string.delete_all_success)/* "删除成功！" */);
							getAdapter().updateDeletedItem(e);
						} else {
							app.showShortToastMessage(getResources().getString(
									R.string.delete_all_failure)/* "删除失败！" */);
						}
						// print("通知ListView，数据集已经改变了。");
						listWithAlternatives(app,
								app.getAppPath(IndexConstants.TILES_INDEX_DIR),
								IndexConstants.SQLITE_EXT, indexFileNames);

						checkButtonStatus();
						app.mEditor.putString(areaName, "不存在").commit();
						System.out.println(areaName);
						getAdapter().notifyDataSetChanged();
					}
				});
		builder.create().show();
	}

	// 展开列表（函数不对，作废，不起作用）
	public void expandList() {
		 System.err.println("！尝试展开下载列表！");
		for (int i = 0; i < getExpandableListAdapter().getGroupCount(); i++) {
			getExpandableListView().expandGroup(i);
		}
	}

	private void copyFilesForAndroid19(final String newLoc) {
		MoveFilesToDifferentDirectory task = new MoveFilesToDifferentDirectory(
				DownloadIndexActivity.this, new File(
						settings.getExternalStorageDirectory(),
						IndexConstants.APP_DIR), new File(newLoc,
						IndexConstants.APP_DIR)) {
			protected Boolean doInBackground(Void[] params) {
				Boolean result = super.doInBackground(params);
				if (result) {
					settings.setExternalStorageDirectory(newLoc);
					getMyApplication().getResourceManager()
							.resetStoreDirectory();
				}
				return result;
			};
		};
		task.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public static Map<String, String> listWithAlternatives(final Context ctx,
			File file, final String ext, final Map<String, String> files) {
		if (file.isDirectory()) {
			System.out.println("离线包存储路径： " + file.getAbsolutePath());
			final java.text.DateFormat format = DateFormat.getDateFormat(ctx);
			format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(ext)) {
						String date = format.format(new File(dir, filename)
								.lastModified());
						files.put(filename, date);
						System.out.println(filename + ", 最近更新时间：" + date);
						return true;
					} else {
						return false;
					}
				}
			});

		}

		getAdapter().setLoadedFiles(null, files);
		return files;

	}

	public DownloadActivityType getType() {
		return type;
	}

	protected static String reparseDate(String date) {
		try {
			Date d = simpleDateFormat.parse(date);
			return AndroidUtils.formatDate(app, d.getTime());
		} catch (ParseException e) {
			return date;
		}
	}

	// 是否在非WIFI环境下下载。
	protected boolean downloadFilesCheckInternet2() {
		if (!getMyApplication().getSettings().isWifiConnected()) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.download_using_mobile_internet));
			builder.setPositiveButton(R.string.default_buttons_yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							isDownloadFromCurrentNetWork = true;
						}
					});
			builder.setNegativeButton(R.string.default_buttons_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							isDownloadFromCurrentNetWork = false;
						}
					});
			builder.show();
		} else {
			return true;
		}
		System.err.println("isDownloadFromCurrentNetWork: "
				+ isDownloadFromCurrentNetWork);
		return isDownloadFromCurrentNetWork;
	}

	// 存储空间是否足够。
	protected boolean downloadFilesPreCheckSpace2(IndexItem item) {
		double sz = 0;
		sz += Double.parseDouble(item.getSize());
		// get availabile space
		double asz = getAvailableSpace();

		if (asz != -1 && asz > 0 && sz / asz > 0.4) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(MessageFormat.format(
					getString(R.string.download_files_question_space), 1, sz,
					asz));
			builder.setPositiveButton(R.string.default_buttons_yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// totalDownloadListIndexThread.runDownloadFiles();
							freeSpaceAvailable = true;
						}
					});
			builder.setNegativeButton(R.string.default_buttons_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// totalDownloadListIndexThread.runDownloadFiles();
							freeSpaceAvailable = false;
						}
					});
			builder.show();
		} else {
			return true;
		}
		return freeSpaceAvailable;
	}

	public double getAvailableSpace() {
		File dir = app.getAppPath("").getParentFile();
		double asz = -1;
		if (dir.canRead()) {
			StatFs fs = new StatFs(dir.getAbsolutePath());
			asz = (((long) fs.getAvailableBlocks()) * fs.getBlockSize())
					/ (1 << 20);
		}
		return asz;
	}

	private void finishThisActivity() {
		finish();
	}

	// 按下按钮
	private final static float[] /* BUTTON_RELEASED */BUTTON_PRESSED = new float[] {
			0.0f, 0, 0, 0, -50, 0, 0.0f, 0, 0, -50, 0, 0, 0.0f, 0, -50, 0, 0,
			0, 0, 0 };

	// 按钮弹起
	private final static float[] /* BUTTON_PRESSED */BUTTON_RELEASED = new float[] {
			1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	private static final OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundDrawable(app.getResources().getDrawable(
						R.color.offline_button_pressed_background_color));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(app.getResources().getDrawable(
						R.color.offline_button_released_background_color));
			}
			return false;
		}
	};

	public static void setButtonStateChangeListener(View v) {
		v.setOnTouchListener(touchListener);
	}
}
