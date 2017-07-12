package com.hifleet.data;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;

import com.hifleet.activity.DownloadIndexActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandSettings;
import com.hifleet.plus.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Environment;


public class SuggestExternalDirectoryDialog {
	
	public static void print(String msg){
        android.util.Log.i(TAG, msg);
	} 
	private static final String TAG = "FileDownloader";

	public static boolean showDialog(DownloadIndexActivity a, final DialogInterface.OnClickListener otherListener,
			final CallbackWithObject<String> selector){
		
		if(a!=null && !a.getAdapter().getDownloadUrlDownloadServiceMap().isEmpty()){
			Builder builder = new AlertDialog.Builder(a);
			builder.setTitle(a.getResources().getString(R.string.change_storage_place_warning_dialog_title)/*"退出离线包下载界面"*/);
			builder.setMessage(a.getResources().getString(R.string.change_storage_place_warning_message)/*"尚有离线包在下载中,此时退出将暂停下载。您确定退出吗？"*/);
			builder.setPositiveButton(/*"确定退出"*/a.getResources().getString(R.string.defalut_button_queding),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.create().show();
			return true;
		}
		
		//此处加警告：如果切换，原来离线包和缓存就删除了。让他选择确定还是取消。
		
		
		
		final boolean showOther = otherListener != null;
		final OsmandApplication app = (OsmandApplication) a.getApplication();
		Builder bld = new AlertDialog.Builder(a);
		HashSet<String> externalMounts;
		//HashSet<String> externalMountsAliasName;
		
		boolean hasSdCard=false;
		
		//print(Build.VERSION.SDK_INT +"-------------"+ OsmandSettings.VERSION_DEFAULTLOCATION_CHANGED);
		
		if(Build.VERSION.SDK_INT < OsmandSettings.VERSION_DEFAULTLOCATION_CHANGED) {
			//print("sdk< 19，不支持外置存储卡。");
			externalMounts = getExternalMounts();
			//externalMountsAliasName = getExternalMounts();
			
		} else {			
			//print("sdk>=19，支持外置存储卡。");
			externalMounts = new HashSet<String>(app.getSettings().getWritableSecondaryStorageDirectorys());
			if(externalMounts.size()>0){
				//print("检测到有外置的sd卡。");
				hasSdCard=true;
			}
		}
		
		String apath = app.getSettings().getExternalStorageDirectory().getAbsolutePath();
		
		externalMounts.add(app.getSettings().getDefaultExternalStorageLocation());
		final String[] extMounts = new String[showOther ?  externalMounts.size()+1 : externalMounts.size()];
		final String[] extMountAlisName = new String[showOther ?  externalMounts.size()+1 : externalMounts.size()];
		
		 
		
	//	print("showOther? "+showOther);		
		externalMounts.toArray(extMounts);
		
		int inde=0,sdcardinde=1;
		for(String s:extMounts){
			if(s.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())){
				extMountAlisName[inde] = app.getResources().getString(R.string.storage_place_phone_memory);//"手机内存";
			}else{				
				extMountAlisName[inde] = /*"SD卡"*/app.getResources().getString(R.string.storage_place_sdcard)+ (inde>0?"":""+inde);
			}
			inde++;
		}
		
		if (showOther) {
			extMounts[extMounts.length - 1] = a.getString(R.string.other_location);
		}
		
		if (extMounts.length > 1) {
			int checkedItem = 0;
			for (int j = 0; j < extMounts.length; j++) {
				if (extMounts[j].equals(apath)) {
					checkedItem = j;
					break;
				}
			}
			bld.setTitle(R.string.application_dir);
			bld.setSingleChoiceItems(/*extMounts*/extMountAlisName, checkedItem, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(showOther && which == extMounts.length -1) {
						otherListener.onClick(dialog, which);
					} else {
						dialog.dismiss();
						if(selector != null) {
							selector.processResult(extMounts[which]);	
						} else {
							
							//print("更换前的地址："+app.getSettings().getExternalStorageDirectory());
							//获得改变前的老路径
							String oldpath = app.getSettings().getExternalStorageDirectoryPath();
							//保存老路径。
							app.getSettings().setExternalStorageDirectoryBeforeChangeStoragePlace(oldpath);
							
							//print("settings 更换目的地："+extMounts[which]);
							
							//设置新路径
							app.getSettings().setExternalStorageDirectory(extMounts[which]);
							
							//print("更换后的地址："+app.getSettings().getExternalStorageDirectory());
							
							//print("更换后的地址别名："+app.getSettings().getExternalStorageDirectoryAlias());
							
							app.getSettings().setExternalStorageDirectoryAlias(extMountAlisName[which]);
//							if(app.getMainWindowSettingsActivity()!=null){
//								app.getMainWindowSettingsActivity().updateCurrentStoragePlace(extMountAlisName[which]);
//								//print("更换的存储目的地 别名："+extMountAlisName[which]);								
//							}
							//app.getResourceManager().resetStoreDirectory();		
							app.getResourceManager().resetStoreDirectory4ChooseDir();//调用此处函数，处理拷贝作业。
							app.getSettings().SELECTED_OFFLINE_MAP_SAVE_DIR.set(true);					
						}
					}
				}
			});
			bld.setPositiveButton(R.string.defalut_button_queding, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					app.getSettings().SELECTED_OFFLINE_MAP_SAVE_DIR.set(true);
				}
			});
			bld.show();
			return true;
		}else{
			
			bld.setTitle(R.string.application_dir);
			bld.setMessage(R.string.no_other_storage_place_or_sd_card);
			bld.setPositiveButton(R.string.defalut_button_queding, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					app.getSettings().SELECTED_OFFLINE_MAP_SAVE_DIR.set(true);
				}
			});
			bld.show();
			//return true;
		}
		return false;
	}
	
	public static HashSet<String> getExternalMounts() {
	    final HashSet<String> out = new HashSet<String>();
	    String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
	    String s = "";
	    try {
	        final Process process = new ProcessBuilder().command("mount")
	                .redirectErrorStream(true).start();
	        process.waitFor();
	        final InputStream is = process.getInputStream();
	        final byte[] buffer = new byte[1024];
	        while (is.read(buffer) != -1) {
	            s = s + new String(buffer);
	        }
	        is.close();
	    } catch (final Exception e) {
	        e.printStackTrace();
	    }

	    // parse output
	    final String[] lines = s.split("\n");
	    for (String line : lines) {
	        if (!line.toLowerCase(Locale.US).contains("asec")) {
	            if (line.matches(reg)) {
	                String[] parts = line.split(" ");
	                for (String part : parts) {
	                    if (part.startsWith("/"))
	                        if (!part.toLowerCase(Locale.US).contains("vold"))
	                            out.add(part);
	                }
	            }
	        }
	    }
	    return out;
	}

}
