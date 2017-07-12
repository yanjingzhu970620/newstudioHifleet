package com.hifleet.data;

import java.util.HashMap;
import java.util.Map;

public class DownloadInfo {
	private boolean completeFlag = true;
	private Map<String,Float> data = new HashMap<String,Float>();
	public boolean isCompleteFlag() {
		return completeFlag;
	}
	public void setCompleteFlag(boolean completeFlag) {
		this.completeFlag = completeFlag;
	}
	public Map<String, Float> getData() {
		return data;
	}
	public void setData(Map<String, Float> data) {
		this.data = data;
	}
}
