package com.hifleet.map;

public abstract class BaseMapLayer extends OsmandMapLayer {

	
	private int alpha = 255;
	protected int warningToSwitchMapShown = 0;
	protected final static int MIN_ZOOM_LEVEL=10;
	protected final static int MAX_ZOOM_LEVEL=21;

	public int getMaximumShownMapZoom(){
		return MAX_ZOOM_LEVEL;
	}
	
	public int getMinimumShownMapZoom(){
		return MIN_ZOOM_LEVEL;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public int getAlpha() {
		return alpha;
	}
	
	
}
