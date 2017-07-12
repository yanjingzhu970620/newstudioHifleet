package com.hifleet.lnfo.layer;

import java.util.ArrayList;
import java.util.List;

import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandMapLayer;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.RotatedTileBox;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;





@SuppressLint("WrongCall")
public class MapControlsLayer extends OsmandMapLayer{
	
	private RulerControl rulerControl;	
	private MapActivity mapActivity;
	
	public MapControlsLayer(MapActivity activity){
		
		this.mapActivity = activity;
	}
	
	@Override
	public boolean drawInScreenPixels() {
		return true;
	}
	
	private float scaleCoefficient;
	private List<MapControls> allControls = new ArrayList<MapControls>();
	private float density;
	@Override
	public void initLayer(final OsmandMapTileView view) {
		scaleCoefficient = view.getScaleCoefficient();
		FrameLayout parent = (FrameLayout) view.getParent();
		Handler showUIHandler = new Handler();
		int rightGravity = Gravity.RIGHT | Gravity.BOTTOM;
		int leftGravity = Gravity.LEFT | Gravity.BOTTOM;
		density = view.getApplication().getResources().getDisplayMetrics().density;
		rulerControl = init(new RulerControl(/*zoomControls,*/ mapActivity, showUIHandler, scaleCoefficient), parent, 
				rightGravity);
	}
	
	private <T extends MapControls> T init(T c, FrameLayout parent, int gravity) {
		c.init(parent);
		c.setGravity(gravity);
		allControls.add(c);
		return c;
	}
	
	@Override
	public void destroyLayer() {
		
	}
	
	private static final int NIGHT_COLOR = 0xffC8C8C8;
	private int shadowColor = -1;
	
	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings nightMode) {
		boolean isNight = nightMode != null && nightMode.isNightMode();
		int shadw = isNight ? Color.TRANSPARENT : Color.WHITE;
		int textColor = isNight ? NIGHT_COLOR : Color.BLACK ;
		if(shadowColor != shadw) {
			shadowColor = shadw;			
		}
		//float density = view.getApplication().getResources().getDisplayMetrics().density;
		int vmargin = dip2px(density,50);//200;
		
		rulerControl.setVerticalMargin(vmargin);
		checkVisibilityAndDraw(true, rulerControl, canvas, tileBox, nightMode);
	}
	
	//将dp算成px
		 public static int dip2px(float density, float dpValue) {  
		        final float scale = density;  
		        return (int) (dpValue * scale + 0.5f);  
		  }  
	
	private void checkVisibilityAndDraw(boolean visibility, MapControls controls, Canvas canvas,
			RotatedTileBox tileBox, DrawSettings nightMode) {
		if(visibility != controls.isVisible()){
			//if(visibility) {
				controls.show((FrameLayout) mapActivity.getMapView().getParent());
			//} else {
			//	controls.hide((FrameLayout) mapActivity.getMapView().getParent());
			//}
		}
		
		
		if(controls.isVisible()) {
			controls.onDraw(canvas, tileBox, nightMode);
		}		
	}
}
