package com.hifleet.data;

public enum DownloadActivityType {
	NORMAL_FILE,VERSION_FILE;

	public static boolean isCountedInDownloads(IndexItem es) {
		return true;
	}
	
	public static boolean isCountedInDownloads(DownloadActivityType tp) {
		return true;
	}
}
