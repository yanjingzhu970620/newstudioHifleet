package com.hifleet.data;


import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateFormat;

import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;

public class IndexItem implements Comparable<IndexItem> {
	
	String description;
	String date;
	String parts;
	String fileName;
	String filename1;//拼音
	String size;
	String rlon,rlat,llon,llat;
	double maxlon,maxlat,minlon,minlat;
	IndexItem attachedItem;
	DownloadActivityType type;
	String ids;
	private boolean isDownloaded=false;
	private boolean isNeedUpdate=false;
	private boolean isDownloading = false;
	private int downloadingPercentage=0;
	private boolean paused=false;
	private float progress;
	private boolean isWaitingFlag = false;
	private boolean hasNotActivateToDownload=false;
	
	public void setHasNotActivateToDownload(boolean flag){
		hasNotActivateToDownload = flag;
	}
	
	public boolean getHasNotActivateToDownload(){
		return hasNotActivateToDownload;
	}
	
	
	public void setWaitingFlag(boolean flag){
		isWaitingFlag = flag;
	}
	
	public boolean isWaiting(){
		return isWaitingFlag;
	}
	
	public void setProgress(float progress){this.progress = progress;}
	public float getProgress(){return progress;}
	
	public void setPaused(boolean flag){
		paused = flag;
	}
	public boolean isPaused(){
		return paused;
	}
	
//	public void setDownloaedFlag(boolean flag){
//		isDownloaded = flag;
//	}
	
	public void setDownloadingPercentage(int percentage){
		downloadingPercentage = percentage;
	}
	public int getDownloadingPercentage(){
		return downloadingPercentage;
	}
	
	public void setIsDownloading(boolean flag){
		if(flag){
			paused = false;
		}
		isDownloading = flag;
	}
	public boolean getIsDownloading(){
		return isDownloading;
	}
	
	public boolean getDownloadedFlag(){
		return isDownloaded;
	}

	public void setId(String v){
		ids = v;
	}
	public String getId(){
		return ids;
	}
	
	public IndexItem(String fileName, String description, String date, String size,
			String rlon,String rlat,String llon,String llat,String filename1
			) {
		
		this.fileName = fileName;
//		System.err.println("download fileName"+fileName);
		this.filename1=filename1;
		this.description = description;
		this.date = date;
		this.size = size;
		this.rlon = rlon;
		this.rlat = rlat;
		this.llon = llon;
		this.llat=llat;
		this.type = DownloadActivityType.NORMAL_FILE;
	}
	
	public String getrlon(){return rlon;}
	public String getrlat(){return rlat;}
	public String getllon(){return llon;}
	public String getllat(){return llat;}
	public String getfilename1(){return filename1;}
	
	public DownloadActivityType getType() {
		return type;
	}

	public void setType(DownloadActivityType type) {
		this.type = type;
	}

	public String getVisibleDescription(Context ctx) {
		String s = ""; //$NON-NLS-1$		
		return s;
	}
	public String getVisibleName(Context ctx) {
		
		return this.description;
		
	}

	public String getBasename() {
		if (fileName.endsWith(IndexConstants.SQLITE_EXT)) {
			return fileName.substring(0, fileName.length() - IndexConstants.SQLITE_EXT.length()).replace('_', ' ');
		}
		int ls = fileName.lastIndexOf('_');
		if (ls >= 0) {
			return fileName.substring(0, ls);
		}
		return fileName;
	}

	public boolean isAccepted() {
		// POI index download is not supported any longer
		if (fileName.endsWith(IndexConstants.SQLITE_EXT)) {
			return true;
		}
		return false;
	}

	protected static String addVersionToExt(String ext, int version) {
		return "_" + version + ext;
	}
	
	public boolean isDownloaded(){
		return isDownloaded;
	}
	public void setDownloadedFlag(boolean flag)
	{
		isDownloaded = flag;
	}
	
	public boolean isNeedUpdate(){
		return isNeedUpdate;
	}
	
	public void setNeedUpdateFlag(boolean flag){
		isNeedUpdate  =flag;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}
	
	public String getSizeDescription(Context ctx) {
		return size + " MB";
	}

	public String getSize() {
		return size;
	}
	
	public DownloadEntry createDownloadEntry(OsmandApplication ctx, DownloadActivityType type){
		String fileName = this.filename1+".sqlitedb";
		File parent = null;
		String extension = null;
		boolean unzipDir = false;
		boolean zipStream = false;
		boolean preventMediaIndexing = false;
		if (fileName.endsWith(IndexConstants.SQLITE_EXT)) {
			parent = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
			extension = IndexConstants.SQLITE_EXT;
		} 
		if (parent != null) {
			parent.mkdirs();			
		}
		DownloadEntry entry = null;
		if (parent == null || !parent.exists()) {
			ctx.showToastMessage(R.string.sd_dir_not_accessible);
		} else {
			entry = new DownloadEntry(this);
			entry.type = type;
			entry.baseName = getBasename();
			String url="http://"+IndexConstants.INDEX_DOWNLOAD_DOMAIN+"";
			entry.urlToDownload = url+"/"+fileName;
			entry.zipStream = zipStream;
			entry.unzipFolder = unzipDir;
			
//			System.out.println("download url:"+url);
			try {
				final java.text.DateFormat format = DateFormat.getDateFormat((Context) ctx);
				format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
				Date d = format.parse(date);
				entry.dateModified = d.getTime();
			} catch (ParseException e1) {
				//log.error("ParseException", e1);
			}
			try {
				entry.sizeMB = Double.parseDouble(size);
			} catch (NumberFormatException e1) {
				//log.error("ParseException", e1);
			}
			entry.parts = 1;
			if (parts != null) {
				entry.parts = Integer.parseInt(parts);
			}
			entry.targetFile = new File(parent, entry.baseName + extension);
			
			entry.targetFileParentDir = parent;
			
			File backup = new File(ctx.getAppPath(IndexConstants.BACKUP_INDEX_DIR), entry.targetFile.getName());
			
			if (backup.exists()) {
				entry.existingBackupFile = backup;
			}
			if (attachedItem != null) {
				ArrayList<DownloadEntry> sz = new ArrayList<DownloadEntry>();
				attachedItem.createDownloadEntry(ctx, type, sz);
				if(sz.size() > 0) {
					entry.attachedEntry = sz.get(0);
				}
			}
			}
		return entry;
	}

	public List<DownloadEntry> createDownloadEntry(OsmandApplication ctx, DownloadActivityType type, 
			List<DownloadEntry> downloadEntries) {
		
		String fileName = this.filename1+IndexConstants.SQLITE_EXT;
		File parent = null;
		String extension = null;
		boolean unzipDir = false;
		boolean zipStream = false;
		boolean preventMediaIndexing = false;

			if (fileName.endsWith(IndexConstants.SQLITE_EXT)) {
			parent = ctx.getAppPath(IndexConstants.TILES_INDEX_DIR);
			extension = IndexConstants.SQLITE_EXT;
		} 
		if (parent != null) {
			parent.mkdirs();
		}
		final DownloadEntry entry;
		if (parent == null || !parent.exists()) {
			ctx.showToastMessage(R.string.sd_dir_not_accessible);
		} else {
			entry = new DownloadEntry(this);
			entry.type = type;
			entry.baseName = getBasename();
			String url="http://"+IndexConstants.INDEX_DOWNLOAD_DOMAIN+"";
			
//			System.out.println("download url:"+url);
			//entry.urlToDownload = url + "file=" + fileName;//原版
			entry.urlToDownload = url+"/"+fileName;
			entry.zipStream = zipStream;
			entry.unzipFolder = unzipDir;
			try {
				final java.text.DateFormat format = DateFormat.getDateFormat((Context) ctx);
				format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
				Date d = format.parse(date);
				entry.dateModified = d.getTime();
			} catch (ParseException e1) {
				//log.error("ParseException", e1);
			}
			try {
				entry.sizeMB = Double.parseDouble(size);
			} catch (NumberFormatException e1) {
				//log.error("ParseException", e1);
			}
			entry.parts = 1;
			if (parts != null) {
				entry.parts = Integer.parseInt(parts);
			}
			entry.targetFile = new File(parent, entry.baseName + extension);
			File backup = new File(ctx.getAppPath(IndexConstants.BACKUP_INDEX_DIR), entry.targetFile.getName());
			if (backup.exists()) {
				entry.existingBackupFile = backup;
			}
			if (attachedItem != null) {
				ArrayList<DownloadEntry> sz = new ArrayList<DownloadEntry>();
				attachedItem.createDownloadEntry(ctx, type, sz);
				if(sz.size() > 0) {
					entry.attachedEntry = sz.get(0);
				}
			}
			downloadEntries.add(entry);
		}
		return downloadEntries;
	}
	
	public String getTargetFileName(){
		String e = getFileName();
		
		if(e.endsWith(IndexConstants.SQLITE_EXT)){
			return e.replace('_', ' ');
		}
		return e;
	}

	@Override
	public int compareTo(IndexItem another) {
		if(another == null) {
			return -1;
		}
		return getFileName().compareTo(another.getFileName());
	}

	public boolean isAlreadyDownloaded(Map<String, String> listAlreadyDownloaded) {
		return listAlreadyDownloaded.containsKey(getTargetFileName());
	}

}