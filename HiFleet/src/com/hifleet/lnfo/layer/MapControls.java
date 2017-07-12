package com.hifleet.lnfo.layer;


import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandMapLayer.DrawSettings;
import com.hifleet.map.RotatedTileBox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;


public abstract class MapControls {
	protected MapActivity mapActivity;
	protected float scaleCoefficient;
	protected Handler showUIHandler;
	protected int shadowColor;
	private boolean visible;
	private long delayTime;
	
	protected int gravity = Gravity.BOTTOM | Gravity.RIGHT;
	protected int margin = 30;
	protected int vmargin;
	protected int width;
	protected int height;
	
	public MapControls(MapActivity mapActivity, Handler showUIHandler, float scaleCoefficient) {
		this.mapActivity = mapActivity;
		this.showUIHandler = showUIHandler;
		this.scaleCoefficient = scaleCoefficient;
	}
	
	
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}
	
	public void setMargin(int margin) {
		this.margin = margin;
	}
	public void setVerticalMargin(int vmargin) {
		this.vmargin = vmargin;
	}
	
	protected ImageButton addImageButton(FrameLayout parent, int stringId, int resourceId) {
		Context ctx = mapActivity;
		ImageButton  button = new ImageButton(ctx);
		applyAttributes(ctx, parent, button, stringId, resourceId, 0);
		return button;
	}
	
	protected Button addButtonOnLeft(FrameLayout parent, int stringId, int resourceId){
		return addButtonOnLeft(parent, stringId, resourceId, 0);
	}
	
	protected Button addButton(FrameLayout parent, int stringId, int resourceId) {
		return addButton(parent, stringId, resourceId, 0);
	}
	
	protected Button addDistanceCalculatorButton(FrameLayout parent, int stringId, int resourceId){
		return addButton(parent, stringId, resourceId, 30);
	}
	
	protected Button addButtonOnLeft(FrameLayout parent, int stringId, int resourceId, int extraMargin){
		Context ctx = mapActivity;
		
		Button button = new Button(ctx);
		applyAttributesOnLeft(ctx,parent,button,stringId,resourceId,extraMargin);
		return button;
	}
	
	
	protected Button addButton(FrameLayout parent, int stringId, int resourceId, int extraMargin) {
		Context ctx = mapActivity;
		Button button = new Button(ctx);
		applyAttributes(ctx, parent, button, stringId, resourceId, extraMargin);
		return button;
	}

	private void applyAttributesOnLeft(Context ctx, FrameLayout parent, View button, int stringId, int resourceId,
			int extraMargin){
		button.setContentDescription(ctx.getString(stringId));
		button.setBackgroundResource(resourceId);
		Drawable d = ctx.getResources().getDrawable(resourceId);
		int gravityLeft = Gravity.BOTTOM | Gravity.LEFT;
		android.widget.FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(d.getMinimumWidth(), d.getMinimumHeight(),
				gravityLeft);
		params.leftMargin = margin+extraMargin;// + extraMargin;
		params.bottomMargin = /*vmargin*/margin + extraMargin;
		parent.addView(button, params);
		button.setEnabled(true);
		mapActivity.accessibleContent.add(button);
	}

	private void applyAttributes(Context ctx, FrameLayout parent, View button, int stringId, int resourceId,
			int extraMargin) {
		button.setContentDescription(ctx.getString(stringId));
		button.setBackgroundResource(resourceId);
		Drawable d = ctx.getResources().getDrawable(resourceId);
		android.widget.FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(d.getMinimumWidth(), d.getMinimumHeight(),
				gravity);
		
		if((gravity & Gravity.LEFT) == Gravity.LEFT) {
			params.leftMargin = margin + extraMargin;
			//System.err.println("left margin: "+params.leftMargin);
		} else {
			params.rightMargin = margin /*+ extraMargin*/;
			//System.err.println("right margin: "+params.rightMargin);
		}
		if((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
			params.bottomMargin = /*vmarginmargin + */extraMargin;
			//System.err.println("bottom margin: "+params.bottomMargin);
		} else {
			params.topMargin = vmargin;
			//System.err.println("top margin: "+params.topMargin);
		}
		parent.addView(button, params);
		button.setEnabled(true);
		mapActivity.accessibleContent.add(button);
	}
	
	protected void removeButton(FrameLayout layout, View b) {
		layout.removeView(b);
		mapActivity.accessibleContent.remove(b);
	}

	public void updateTextColor(int textColor, int shadowColor) {
		this.shadowColor = shadowColor;
	}

	public final void init(FrameLayout layout) {
		initControls(layout);
	}
	
	public final void show(FrameLayout layout) {
		visible = true;
		showControls(layout);
	}
	
	public final void showWithDelay(final FrameLayout layout, final long delay) {
		this.delayTime = System.currentTimeMillis() + delay;
		if(!visible) {
			visible = true;
			showControls(layout);
			runWithDelay(layout, delay);
			mapActivity.getMapView().refreshMap();
		}
	}

	private void runWithDelay(final FrameLayout layout, final long delay) {
		showUIHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				long ctime = System.currentTimeMillis();
				if(MapControls.this.delayTime <= ctime) {
					if (MapControls.this.delayTime != 0) {
						MapControls.this.delayTime = 0;
						visible = false;
						hideControls(layout);
						mapActivity.getMapView().refreshMap();
					}
				} else {
					runWithDelay(layout, MapControls.this.delayTime - ctime);
				}
			}
		}, delay);
	}
	
	public final void hide(FrameLayout layout) {
		if(this.delayTime == 0) {
			visible = false;
			hideControls(layout);
		}
	}
	
	public final void forceHide(FrameLayout layout) {
		delayTime = 0;
		visible = false;
		hideControls(layout);
		mapActivity.getMapView().refreshMap();
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	
	protected void initControls(FrameLayout layout) {
	}

	protected abstract void hideControls(FrameLayout layout);
	
	protected abstract void showControls(FrameLayout layout);
	

	public abstract void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings nightMode);
	
	public boolean onSingleTap(PointF point, RotatedTileBox tileBox) {
		return false;
	}
}
