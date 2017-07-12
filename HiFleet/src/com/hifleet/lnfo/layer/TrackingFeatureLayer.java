package com.hifleet.lnfo.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import com.hifleet.plus.R;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.OsmandSettings;
import com.hifleet.map.ResourceManager;
import com.hifleet.map.RotatedTileBox;

public class TrackingFeatureLayer extends OsmandMapLayer{
	protected OsmandMapTileView view;
	protected ResourceManager resourceManager;
	private OsmandSettings settings;
	public static final int START_SHOW = IndexConstants.START_TO_SHOW_SHIPS_ZOOM;
	private int basicLength = 20;
	private int exactLength;
	private float bmoffset = 0.5f;
	private int baseoffset = 20;
	private DisplayMetrics dm;

	private Paint trackFeaturePaint,locationPaint;
	private PointF point;
	
	@Override
	public boolean onSingleTap(PointF point, RotatedTileBox tileBox) {
		
		if (tileBox.getZoom() < START_SHOW) {
			return true;
		}
		this.point = point;
		//view.callPrepareBufferImage();
		
		return true;
		}
	
	private Bitmap getTrackFeatureBitmap(){
		exactLength = (int)dm.densityDpi *basicLength; 
		
		Path path = new Path(); 

		
		path.moveTo((-20+baseoffset)* dm.density + bmoffset, (-12+baseoffset)* dm.density + bmoffset);
		path.lineTo((-20+baseoffset)* dm.density + bmoffset, (-20+baseoffset)* dm.density + bmoffset);
		path.lineTo((-12+baseoffset)* dm.density + bmoffset, (-20+baseoffset)* dm.density + bmoffset);
		
		path.moveTo((12+baseoffset)* dm.density + bmoffset, (-20+baseoffset)* dm.density + bmoffset);			
		path.lineTo((20+baseoffset)* dm.density + bmoffset, (-20+baseoffset)* dm.density + bmoffset);
		path.lineTo((20+baseoffset)* dm.density + bmoffset, (-12+baseoffset)* dm.density + bmoffset);

		path.moveTo((20+baseoffset)* dm.density + bmoffset, (12+baseoffset)* dm.density + bmoffset);			
		path.lineTo((20+baseoffset)* dm.density + bmoffset, (20+baseoffset)* dm.density + bmoffset);
		path.lineTo((12+baseoffset)* dm.density + bmoffset, (20+baseoffset)* dm.density + bmoffset);
		
		path.moveTo((-12+baseoffset)* dm.density + bmoffset, (20+baseoffset)* dm.density + bmoffset);			
		path.lineTo((-20+baseoffset)* dm.density + bmoffset, (20+baseoffset)* dm.density + bmoffset);
		path.lineTo((-20+baseoffset)* dm.density + bmoffset, (12+baseoffset)* dm.density + bmoffset);
		
		
		Bitmap trackbm = Bitmap.createBitmap(dm, 42 * (int) dm.density,
				42 * (int) dm.density, Bitmap.Config.ARGB_8888);
		Canvas can =new Canvas(trackbm);
		
		can.drawPath(path,trackFeaturePaint);
		
		return trackbm;
		
	}
	
	@Override
	public void initLayer(OsmandMapTileView view) {
		this.view = view;
		settings = view.getSettings();
		resourceManager = view.getApplication().getResourceManager();
		dm = view.getResources().getDisplayMetrics();
		
		trackFeaturePaint = new Paint();
		trackFeaturePaint.setColor(view.getResources().getColor(R.color.red));
		trackFeaturePaint.setAntiAlias(true);
		trackFeaturePaint.setStrokeWidth(2);
		
		locationPaint = new Paint();
		locationPaint.setAntiAlias(true);
		locationPaint.setFilterBitmap(true);
		locationPaint.setDither(true);
		
		//focusedShipShapePaint.setStyle(Style.FILL_AND_STROKE);
	}
	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {}
	@Override
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox,
			DrawSettings drawSettings) {
		
		System.out.println("vsd onPrepareBufferImage");
		
		if(point == null) return;
		
		final double lat = tileBox.getLatFromPixel(point.x, point.y);
		final double lon = tileBox.getLonFromPixel(point.x, point.y);
		
		try {
			canvas.save();				
			//canvas.rotate(obj.getCo() - 90, locationX, locationY);		
			//canvas.rotate(obj.getCo()+180 , point.x, locationY);	
			Bitmap trackbm = getTrackFeatureBitmap();
			canvas.drawBitmap(trackbm, point.x - trackbm.getWidth() / 2, 
					point.y- trackbm.getHeight() / 2, locationPaint);

			canvas.restore();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public void destroyLayer() {
	}
	@Override
	public boolean drawInScreenPixels() {
		return false;
	}
	
	public TrackingFeatureLayer(boolean mainLayer){}
}
