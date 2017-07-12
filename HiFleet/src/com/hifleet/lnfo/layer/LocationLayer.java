package com.hifleet.lnfo.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.Log;

import com.hifleet.map.Location;
import com.hifleet.map.OsmAndLocationProvider;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.RotatedTileBox;
import com.hifleet.plus.R;

/**    
 * @{#} LocationLayer.java Create on 2015年6月15日 下午2:30:25    
 *    
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 * @description
 *	
 */
public class LocationLayer extends OsmandMapLayer {

	protected final static int RADIUS = 7;
	protected final static float HEADING_ANGLE = 60;
	
	private OsmAndLocationProvider locationProvider;
	private OsmandMapTileView view;
	private Paint locationPaint;
	private Paint area;
	private Paint aroundArea;
	private Paint headingPaint;
	
	private Bitmap bearingIcon;
	private Bitmap locationIcon;
	
	/* (non-Javadoc)
	 * @see com.hifleet.map.OsmandMapLayer#initLayer(com.hifleet.map.OsmandMapTileView)
	 */
	@Override
	public void initLayer(OsmandMapTileView view) {
		// TODO Auto-generated method stub
		this.view = view;
		initUI();
	}

	
	
	private void initUI(){
		locationProvider = view.getApplication().getLocationProvider();
		
		locationPaint = new Paint();
		locationPaint.setAntiAlias(true);
		locationPaint.setFilterBitmap(true);
		locationPaint.setDither(true);
		
		
		area = new Paint();
		area.setColor(view.getResources().getColor(R.color.pos_area));
		
		aroundArea = new Paint();
		aroundArea.setColor(view.getResources().getColor(R.color.pos_around));
		aroundArea.setStyle(Style.STROKE);
		aroundArea.setStrokeWidth(1);
		aroundArea.setAntiAlias(true);
		
		headingPaint = new Paint();
		headingPaint.setColor(view.getResources().getColor(R.color.pos_heading));
		headingPaint.setAntiAlias(true);
		headingPaint.setStyle(Style.FILL);
		
		bearingIcon = BitmapFactory.decodeResource(view.getResources(), R.drawable.pedestrian_bearing);
		locationIcon = BitmapFactory.decodeResource(view.getResources(), R.drawable.pedestrian_location);
	}
	
	private RectF getHeadingRect(int locationX, int locationY){
		int rad = Math.min(3 * view.getWidth() / 8, 3 * view.getHeight() / 8);
		return new RectF(locationX - rad, locationY - rad, locationX + rad, locationY + rad);
	}
	
	/* (non-Javadoc)
	 * @see com.hifleet.map.OsmandMapLayer#onDraw(android.graphics.Canvas, com.hifleet.map.RotatedTileBox, com.hifleet.map.OsmandMapLayer.DrawSettings)
	 */
	@Override
	public void onDraw(Canvas canvas, RotatedTileBox box,
			DrawSettings settings) {
		// TODO Auto-generated method stub
		
		Location lastKnownLocation = locationProvider.getLastKnownLocation();
		
		if(lastKnownLocation == null || view == null){
			//if(lastKnownLocation==null)
			//print("lastKonwLoction null. return.in LocationLayer. 上次位置是空的。");
			return;
		}
		
		double last_lat = lastKnownLocation.getLatitude();
		double last_lon = lastKnownLocation.getLongitude();
		
		int locationX = box.getPixXFromLonNoRot(last_lon);
		int locationY = box.getPixYFromLatNoRot(last_lat);
		
		final double dist = box.getDistance(0, box.getPixHeight() / 2, box.getPixWidth(), box.getPixHeight() / 2);
//		int radius = (int) (((double) box.getPixWidth()) / dist * lastKnownLocation.getAccuracy());
		
//		if (radius > RADIUS * box.getDensity()) {
//			int allowedRad = Math.min(box.getPixWidth() / 2, box.getPixHeight() / 2);
//			canvas.drawCircle(locationX, locationY, Math.min(radius, allowedRad), area);
//			canvas.drawCircle(locationX, locationY, Math.min(radius, allowedRad), aroundArea);
//		}
		// draw bearing/direction/location
		if (isLocationVisible(box, lastKnownLocation)) {
			//boolean isBearing = lastKnownLocation.hasBearing();
			//if (!isBearing) {
				canvas.drawBitmap(locationIcon, locationX - locationIcon.getWidth() / 2, locationY - locationIcon.getHeight() / 2,
						locationPaint);
			//}
//			Float heading = locationProvider.getHeading();
//			if (heading != null && view.getSettings().SHOW_VIEW_ANGLE.get()) {
//				canvas.drawArc(getHeadingRect(locationX, locationY), heading - HEADING_ANGLE / 2 - 90, HEADING_ANGLE, true, headingPaint);
//			}
//
//			if (isBearing) {
//				float bearing = lastKnownLocation.getBearing();
//				canvas.rotate(bearing - 90, locationX, locationY);
//				canvas.drawBitmap(bearingIcon, locationX - bearingIcon.getWidth() / 2, locationY - bearingIcon.getHeight() / 2,
//						locationPaint);
//			}

		}
		
	}

	/* (non-Javadoc)
	 * @see com.hifleet.map.OsmandMapLayer#destroyLayer()
	 */
	@Override
	public void destroyLayer() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.hifleet.map.OsmandMapLayer#drawInScreenPixels()
	 */
	@Override
	public boolean drawInScreenPixels() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isLocationVisible(RotatedTileBox tb, Location l){
		if(l == null ){
			return false;
		}
		return tb.containsLatLon(l.getLatitude(), l.getLongitude());
	}
	
	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public static final String TAG = "FileDownloader";
}
