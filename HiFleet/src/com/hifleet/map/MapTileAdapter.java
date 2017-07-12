package com.hifleet.map;


import android.graphics.Canvas;

/**
 * This class is designed to represent adapter for specific map sources
 * that requires additional computation or updates
 */
public abstract class MapTileAdapter {
	
	protected MapTileLayer layer;
	protected OsmandMapTileView view;
	private GreenMapTileLayer layer1;
	private WeatherMapTileLayer layer2;
	private WaveMapTileLayer layerwave;
	private WindMapTileLayer layerwind;
	private OceanMapTileLayer layerocean;
	public void initLayerAdapter(MapTileLayer layer, OsmandMapTileView view){
		this.layer = layer;
		this.view = view;
	}
	
	public void initLayerAdapter(GreenMapTileLayer layer, OsmandMapTileView view){
		this.layer1 = layer;
		this.view = view;
	}
	
	public void initLayerAdapter(WeatherMapTileLayer layer, OsmandMapTileView view){
		this.layer2 = layer;
		this.view = view;
	}
	public void initLayerAdapter(WaveMapTileLayer layer, OsmandMapTileView view){
		this.layerwave = layer;
		this.view = view;
	}
	public void initLayerAdapter(WindMapTileLayer layer, OsmandMapTileView view){
		this.layerwind= layer;
		this.view = view;
	}
	public void initLayerAdapter(OceanMapTileLayer layer, OsmandMapTileView view){
		this.layerocean = layer;
		this.view = view;
	}
	public abstract void onDraw(Canvas canvas, RotatedTileBox tileBox, OsmandMapLayer.DrawSettings drawSettings);
	
	public abstract void onClear(); 

	public abstract void onInit();
}
