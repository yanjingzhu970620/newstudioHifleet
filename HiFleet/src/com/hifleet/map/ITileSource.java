package com.hifleet.map;


public interface ITileSource {
	
	public int getMaximumZoomSupported();
	
	public String getName();
	
	public int getTileSize();
	
	public String getUrlToLoad(int x, int y, int zoom);
	
	public int getMinimumZoomSupported();
	
	public String getTileFormat();
	
	public int getBitDensity();
	
	public boolean isEllipticYTile();
	
	public boolean couldBeDownloadedFromInternet();
	
	public int getExpirationTimeMillis();
	
	public int getExpirationTimeMinutes();
	
}
