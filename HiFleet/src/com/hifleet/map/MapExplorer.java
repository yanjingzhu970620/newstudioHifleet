package com.hifleet.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.os.Build;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.hifleet.plus.R;

// Provide touch exploration mode for map view
// when scrolling it by gestures is disabled.
//
public class MapExplorer implements OnGestureListener {

    private static final float VICINITY_RADIUS = 15;

    private OsmandMapTileView mapView;
    private OnGestureListener fallback;


    // OnGestureListener specified as a second argument
    // will be used when scrolling map by gestures
    // is enabled.
    public MapExplorer(OsmandMapTileView mapView, OnGestureListener fallback) {
        this.mapView = mapView;
        this.fallback = fallback;
    }


    // Compare two lists by content.
    private boolean different(Object l1, Object l2) {
    	if(l1 == null || l2 == null) {
    		return l1 != l2;
    	}
    	return l1.equals(l2);
    }

    // Find touched objects if any and emit accessible toast message
    // with it's brief description.
    private void describePointedObjects(RotatedTileBox tb,  MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        List<Object> ns = new ArrayList<Object>();     
    }


    // OnGestureListener interface implementation.

    @Override
    public boolean onDown(MotionEvent e) {
        if ((Build.VERSION.SDK_INT >= 14) || mapView.getSettings().SCROLL_MAP_BY_GESTURES.get())
            return fallback.onDown(e);       
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if ((Build.VERSION.SDK_INT >= 14) || mapView.getSettings().SCROLL_MAP_BY_GESTURES.get())
            return fallback.onFling(e1, e2, velocityX, velocityY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        fallback.onLongPress(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if ((Build.VERSION.SDK_INT >= 14) || mapView.getSettings().SCROLL_MAP_BY_GESTURES.get()) {
//        	System.err.println("mapexplorer onscroll");
//        
    		
            return fallback.onScroll(e1, e2, distanceX, distanceY);
        } else {
            describePointedObjects(mapView.getCurrentRotatedTileBox(), e2);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        if ((Build.VERSION.SDK_INT >= 14) || mapView.getSettings().SCROLL_MAP_BY_GESTURES.get())
            fallback.onShowPress(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return fallback.onSingleTapUp(e);
    }


    // IContextMenuProvider interface implementation.

   
    public void collectObjectsFromPoint(PointF point, RotatedTileBox tileBox, List<Object> objects) {
        int radius = (int)(VICINITY_RADIUS * tileBox.getDensity());
	    final QuadPoint p = tileBox.getCenterPixelPoint();
	    int dx = (int)Math.abs(point.x - p.x);
        int dy = (int)Math.abs(point.y - p.y);
        if ((dx < radius) && (dy < radius))
            objects.add(this);
    }

  
    public LatLon getObjectLocation(Object o) {
	    final RotatedTileBox tb = mapView.getCurrentRotatedTileBox();
	    return tb.getCenterLatLon();
    }

  
//    public String getObjectDescription(Object o) {
//        return mapView.getContext().getString(R.string.i_am_here);
//    }
//
//
//    public String getObjectName(Object o) {
//        return mapView.getContext().getString(R.string.i_am_here);
//    }

}
