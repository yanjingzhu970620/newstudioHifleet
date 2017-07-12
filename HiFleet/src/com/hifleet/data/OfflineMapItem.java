package com.hifleet.data;

import com.hifleet.map.OfflineDataRegion;




public class OfflineMapItem {
	
	private static final long serialVersionUID = 729654300829771466L;
	private String name;
	private OfflineDataRegion mapRegion;
	private double fileSize;
	private String updateTime;
	private boolean downloaded=false;
	private boolean needUpate=false;
	
	public OfflineMapItem(){}
	public OfflineMapItem(String name,double size,OfflineDataRegion mapRegion,String updatetime
			){
		this.name = name;
		this.fileSize=size;
		this.mapRegion = mapRegion;
		this.updateTime = updatetime;
		
	}
	
	public void setDownloadedFlag(boolean flag){
		downloaded = flag;
	}
	
	public boolean isDownloaded(){
		return downloaded;
	}
	
	public void setNeedUpdateFlag(boolean flag){
		needUpate = flag;
	}
	public boolean isNeedUpdate(){
		
		return needUpate;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public OfflineDataRegion getMapRegion() {
		return mapRegion;
	}
	public void setMapRegion(OfflineDataRegion mapRegion) {
		this.mapRegion = mapRegion;
	}
	public double getFileSize() {
		return fileSize;
	}
	public void setFileSize(double size) {
		this.fileSize = size;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	
	
}
