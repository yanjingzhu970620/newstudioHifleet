package com.hifleet.map;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.hifleet.plus.R;

import android.content.Context;
import android.widget.Toast;




public class MoveFilesThread extends BasicProgressAsyncTask<String, Object, String>{
	OsmandApplication app;
	String oldPath,newPath;
	
	public MoveFilesThread(Context context,String oldPath,String newPath){
		super(context);
		app = (OsmandApplication) context.getApplicationContext();
		this.oldPath = oldPath;
		this.newPath = newPath;
	}
	protected void onPreExecute() {
		//print("删除缓存： onPreExecute");
	}
	protected String doInBackground(String... filesToDownload) {
		print("后台运行："+oldPath+", \n"+newPath);
		try{
		//File file2move = new File(oldPath);
		
		
		
		final Map<String,String> localDownloaedIndexFileNames = new HashMap<String,String>();
		
		File file = new File(oldPath,IndexConstants.APP_DIR+IndexConstants.TILES_INDEX_DIR);
		final String ext1 = IndexConstants.SQLITE_EXT;
		final String ext2 = IndexConstants.DOWNLOAD_SQLITE_EXT;
		if(file.isDirectory()){
			file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(ext1)) {						
						localDownloaedIndexFileNames.put(filename, filename);
						// print(filename+", 最近更新时间："+date);
						return true;
					} else {
						return false;
					}
				}
			});
			
			file.list(new FilenameFilter(){
				public boolean accept(File dir,String filename){
					if(filename.endsWith(ext2)){
						localDownloaedIndexFileNames.put(filename, filename);
						return true;
					}
					return false;
				}
			});
		}
		
		Iterator<String> it = localDownloaedIndexFileNames.keySet().iterator();
		File destDir = new File(newPath,IndexConstants.APP_DIR+IndexConstants.TILES_INDEX_DIR);
		while(it.hasNext()){
			//print("离线包： "+it.next());
			File file2mv = new File(oldPath,IndexConstants.APP_DIR+IndexConstants.TILES_INDEX_DIR+it.next());
			FileUtils.moveFileToDirectory(file2mv, destDir, false);
			print("把\n"+file2mv.getPath()+" \n移动到\n "+destDir.getPath());
		}
		return app.getResources().getString(R.string.change_storage_dir_success);//"移动成功！";
		}catch(Exception ex){
			ex.printStackTrace();
			print("切换失败！");
			return app.getResources().getString(R.string.change_storage_dir_failure);//"更换存储位置发生错误！";
		}
		//return "";
	}
	
	private void deleteFile(File file){
		File[] childfile = file.listFiles();
		if(childfile==null || childfile.length==0){
			file.delete();
			return;
		}
		for(int i=0;i<childfile.length;i++){
			deleteFile(childfile[i]);
			print("删除文件。");
		}
		file.delete();
		
	}
	
protected void onProgressUpdate(Object... values) {
		
	}
	protected void updateProgress(boolean updateOnlyProgress) {
		
	}
	protected void onPostExecute(String result) {
		if(result.length()>0 && result.trim().compareTo("")!=0)
		AccessibleToast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
	}
	private static void print(String msg){
        android.util.Log.i(TAG, msg);
    }
	public static final String TAG = "FileDownloader";
}
