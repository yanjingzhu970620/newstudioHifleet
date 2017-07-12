package com.hifleet.map;

import com.hifleet.lnfo.layer.LocationLayer;
import com.hifleet.lnfo.layer.MapControlsLayer;
import com.hifleet.lnfo.layer.ShipLableLayer;
import com.hifleet.lnfo.layer.ShipsInfoLayer;
import com.hifleet.lnfo.layer.TraceInfoLayer;
import com.hifleet.lnfo.layer.TrackingFeatureLayer;
import com.hifleet.lnfo.layer.timeLableLayer;

/**
 * Object is responsible to maintain layers using by map activity
 */
public class MapActivityLayers {

	OsmandApplication app;

	private MapActivity activity;
	private MapActivity1 activity1;
//	private MapActivity_Nav mapActivity_Nav;
	// the order of layer should be preserved ! when you are inserting new layer
	public MapTileLayer mapTileLayer;
	public OceanMapTileLayer oceanmaptilelayer;
	public GreenMapTileLayer greenMapTileLayer;
	public WeatherMapTileLayer weatherMapTileLayer;
	public WaveMapTileLayer waveMapTileLayer;
	public WindMapTileLayer windMapTileLayer;
	public ShipsLayer shipsLayer;
	public ShipsInfoLayer shipsInfoLayer;
	public TraceInfoLayer traceinfolayer;
	public timeLableLayer timelablelayer;
	public ShipLableLayer shiplablelayer;
	public LocationLayer locationLayer;// 本机位置层
	private MapControlsLayer mapControlsLayer;

	private TrackingFeatureLayer trackFeatureLayer;

	// private GPXLayer gpxLayer;

	public MapActivityLayers(MapActivity activity) {
		this.activity = activity;
	}

	public MapActivityLayers(MapActivity1 activity1) {
		this.activity1 = activity1;
	}

//	public MapActivityLayers(MapActivity_Nav activity) {
//		this.mapActivity_Nav = activity;
//	}
	public OsmandApplication getApplication() {
		return (OsmandApplication) activity.getApplication();
	}

	public OsmandApplication getApplication1() {
		return (OsmandApplication) activity1.getApplication();
	}

//	public OsmandApplication getApplication_nav() {
//		return (OsmandApplication) mapActivity_Nav.getApplication();
//	}
	public void createLayers(final OsmandMapTileView mapView, int i) {
		if (i == 1) {
			OsmandApplication app = (OsmandApplication) getApplication1();
			OsmandSettings settings = app.getSettings();
			mapTileLayer = new MapTileLayer(true);
			mapTileLayer.setLayerName("mapTileLayer");
			mapView.addLayer(mapTileLayer, 0.0f);
			mapView.setMainLayer(mapTileLayer);
			String mapsourcename = settings.MAP_TILE_SOURCES.get().toString();
			ITileSource newSource = settings.getMapTileSource(false);
			mapTileLayer.setMap(newSource);

				traceinfolayer = new TraceInfoLayer(false);
				traceinfolayer.setLayerName("traceInfoLayer");
				mapView.addLayer(traceinfolayer, 1.0f);
				mapTileLayer.addMapRefreshCallsback(traceinfolayer);

				timelablelayer = new timeLableLayer(false);
				timelablelayer.setLayerName("timelablelayer");
				mapView.addLayer(timelablelayer, 2.0f);
				traceinfolayer.addAftertrace(timelablelayer);
				mapTileLayer.addMapRefreshCallsback(timelablelayer);

//				mapControlsLayer = new MapControlsLayer(activity);
//				mapView.addLayer(mapControlsLayer, 3.0f);
				
		} else if(i==0){
			OsmandApplication app = (OsmandApplication) getApplication();
			OsmandSettings settings = app.getSettings();
			mapTileLayer = new MapTileLayer(true);
			mapTileLayer.setLayerName("mapTileLayer");

			mapView.addLayer(mapTileLayer, 1.0f);
			mapView.setMainLayer(mapTileLayer);
			String mapsourcename = settings.MAP_TILE_SOURCES.get().toString();
			ITileSource newSource = settings.getMapTileSource(false);
			mapTileLayer.setMap(newSource);

			weatherMapTileLayer = new WeatherMapTileLayer(true);
			weatherMapTileLayer.setLayerName("weatherMapTileLayer");
			mapView.addLayer(weatherMapTileLayer, 5.0f);
			ITileSource newweatherSource = settings.getWeatherMapTileSource(false);
			weatherMapTileLayer.setMap(newweatherSource);
			
			waveMapTileLayer = new WaveMapTileLayer(true);
			waveMapTileLayer.setLayerName("waveMapTileLayer");
			mapView.addLayer(waveMapTileLayer, 3.0f);
			ITileSource newwaveSource = settings.getWaveMapTileSource(false);
			waveMapTileLayer.setMap(newwaveSource);
			
			windMapTileLayer = new WindMapTileLayer(true);
			windMapTileLayer.setLayerName("windMapTileLayer");
			mapView.addLayer(windMapTileLayer, 4.0f);
			ITileSource newwindSource = settings.getWindMapTileSource(false);
			windMapTileLayer.setMap(newwindSource);
			
			oceanmaptilelayer =new OceanMapTileLayer(true);
			oceanmaptilelayer.setLayerName("oceanMapTileLayer");
			mapView.addLayer(oceanmaptilelayer, 5.0f);
			ITileSource newoceanSource = settings.getOceanMapTileSource(false);
			oceanmaptilelayer.setMap(newoceanSource);
			
			greenMapTileLayer = new GreenMapTileLayer(true);
			greenMapTileLayer.setLayerName("greenMapTileLayer");
			mapView.addLayer(greenMapTileLayer, 2.0f);
			ITileSource newgreenSource = settings.getGreenMapTileSource(false);
			greenMapTileLayer.setMap(newgreenSource);
			

			shipsInfoLayer = new ShipsInfoLayer(false);
			shipsInfoLayer.setLayerName("shipsInfoLayer");
			mapView.addLayer(shipsInfoLayer, 8.0f);
			mapTileLayer.addMapRefreshCallsback(shipsInfoLayer);
//			greenMapTileLayer.addMapRefreshCallsback(shipsInfoLayer);

			shiplablelayer = new ShipLableLayer(false);
			shiplablelayer.setLayerName("shiplablelayer");
			mapView.addLayer(shiplablelayer, 7.0f);
			shipsInfoLayer.addafterShip(shiplablelayer);
			mapTileLayer.addMapRefreshCallsback(shiplablelayer);
//			greenMapTileLayer.addMapRefreshCallsback(shiplablelayer);

			locationLayer = new LocationLayer();
			locationLayer.setLayerName("locationLayer");
			mapView.addLayer(locationLayer, 6.0f);
			
			mapControlsLayer = new MapControlsLayer(activity);
			mapView.addLayer(mapControlsLayer, 7.0f);

		}else{
			OsmandApplication app = (OsmandApplication) getApplication1();
			OsmandSettings settings = app.getSettings();
			mapTileLayer = new MapTileLayer(true);
			mapTileLayer.setLayerName("mapTileLayer");
			mapView.addLayer(mapTileLayer, 0.0f);
			mapView.setMainLayer(mapTileLayer);
			String mapsourcename = settings.MAP_TILE_SOURCES.get().toString();
			ITileSource newSource = settings.getMapTileSource(false);
			mapTileLayer.setMap(newSource);

			mapControlsLayer = new MapControlsLayer(activity);
			mapView.addLayer(mapControlsLayer, 1.0f);

			locationLayer = new LocationLayer();
			locationLayer.setLayerName("locationLayer");
			mapView.addLayer(locationLayer, 2.0f);
		}
	}

	public void updateLayers(OsmandMapTileView mapView, int i) {
		// System.err.println("updateLayers in MapActivityLayers.");
		if (i == 1) {
			OsmandSettings settings = getApplication1().getSettings();
		} else if(i==0){
			OsmandSettings settings = getApplication().getSettings();
		}else{
			OsmandSettings settings = getApplication().getSettings();
		}
		// updateMapSource(mapView, settings.MAP_TILE_SOURCES);

	}

	private String getString(int resId) {
		return activity.getString(resId);
	}

	public MapTileLayer getMapTileLayer() {
		return mapTileLayer;
	}

	public ShipsLayer getShipslayer() {
		return shipsLayer;
	}

	public ShipsInfoLayer getShipsInfoLayer() {
		return shipsInfoLayer;
	}

}
