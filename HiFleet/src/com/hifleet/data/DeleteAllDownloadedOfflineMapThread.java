package com.hifleet.data;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

import com.hifleet.adapter.DownloadIndexAdapter;
import com.hifleet.map.AccessibleToast;
import com.hifleet.map.BasicProgressAsyncTask;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class DeleteAllDownloadedOfflineMapThread {
	OsmandApplication app;
	DownloadIndexAdapter adapter;
	
	public DeleteAllDownloadedOfflineMapThread(OsmandApplication app,DownloadIndexAdapter adpater){
		this.app = app;//(OsmandApplication) context.getApplicationContext();
		this.adapter = adpater;
	}
	
	public void startDelete(){
		DeletTask task = new DeletTask(app,adapter);
		execute(task,"");		
	}
	
	public <P> void execute(BasicProgressAsyncTask<P, ?, String> task,
			P... param) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(Executors.newCachedThreadPool(), param);
		} else {
			task.execute(param);
		}
	}
	
	
	class DeletTask extends BasicProgressAsyncTask<String, Object, String>{
		OsmandApplication app;
		DownloadIndexAdapter adapter ;
		public DeletTask(Context context,DownloadIndexAdapter adpater){
			super(context);
			app = (OsmandApplication) context.getApplicationContext();
			this.adapter= adpater;			 
		}
		protected void onPreExecute() {
			//print("删除全部离线包： onPreExecute");
		}
		protected String doInBackground(String... filesToDownload) {
			try{
				
			List<String> offlineMapList = adapter.getDeletableOfflineMapNameList();
			//offlineMapList.size()
			Iterator<String> it = offlineMapList.iterator();
			//print("进入while循环。"+offlineMapList.size());
			
			while (it.hasNext()) {
				String itemName = it.next();
				//IndexItem item = offlineMapList.get(itemName);
				print("itemName: "+itemName);
				//adpater.pauseDownload(item);
				String fileNameToDelete = itemName;//item.getFileName();
				File file2delete = new File(app
						.getAppPath(IndexConstants.TILES_INDEX_DIR)
						+ "/" + fileNameToDelete);
				if (file2delete.delete()) {
					//print(fileNameToDelete + " .sqlitedb文件删除成功！");
					//adpater.updateDeletedItem(item);
				}
				else{
					print(fileNameToDelete + " .sqlitedb文件删除失败！");
				}

				File file2delete_download = new File(app
						.getAppPath(IndexConstants.TILES_INDEX_DIR)
						+ "/" + fileNameToDelete + ".download");
				if (file2delete_download.delete()) {
					// getAdapter().updateDeletedItem(item);
					//print(fileNameToDelete + " .download文件删除成功！");
					//adpater.updateDeletedItem(item);
				}else{
					print(fileNameToDelete + " .download文件删除失败！");
				}
			}
				//adpater.notifyDataSetChanged();	
				print("删除成功！");
				return /*"删除成功！"*/app.getResources().getString(R.string.delete_all_success);
			}
			catch(Exception ex){
				ex.printStackTrace();
				print("删除失败！");
			return /*"删除失败！"*/app.getResources().getString(R.string.delete_all_failure);
		}		
			
		}
		protected void onProgressUpdate(Object... values) {
			
		}
		protected void updateProgress(boolean updateOnlyProgress) {
			
		}
		protected void onPostExecute(String result) {
			adapter.updateDeletedAllIndexItems();
			adapter.checkButtonStatus();
			AccessibleToast.makeText(ctx, result, Toast.LENGTH_SHORT).show();			
			//adpater.notifyDataSetChanged();	
		}
		
	}
	
	private static void print(String msg) {

		Log.i(TAG, msg);

	}

	private static final String TAG = "FileDownloader";
}
