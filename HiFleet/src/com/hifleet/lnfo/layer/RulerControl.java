package com.hifleet.lnfo.layer;

import java.text.MessageFormat;

import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;
import com.hifleet.map.OsmandMapLayer.DrawSettings;
import com.hifleet.map.OsmandMapTileView;
import com.hifleet.map.OsmandSettings;
import com.hifleet.map.OsmandSettings.MetricsConstants;
import com.hifleet.map.RotatedTileBox;
import com.hifleet.plus.R;
import com.hifleet.utility.OsmAndFormatter;
import com.hifleet.utility.ShadowText;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.widget.FrameLayout;


public class RulerControl extends MapControls{
	ShadowText cacheRulerText = null;
	float cacheRulerZoom = 0;
	double cacheRulerTileX = 0;
	double cacheRulerTileY = 0;
	float cacheRulerTextLen = 0;
	//MapZoomControls zoomControls;
	Drawable rulerDrawable;
	TextPaint rulerTextPaint;
	final static double screenRulerPercent = 0.25/2.0;//把这个值弄小点，比例尺的线就短了，基本满足了。原来没有除以2的。
	
	public RulerControl(/*MapZoomControls zoomControls,*/ MapActivity mapActivity, Handler showUIHandler, float scaleCoefficient) {
		super(mapActivity, showUIHandler, scaleCoefficient);
		//this.zoomControls = zoomControls;
		rulerTextPaint = new TextPaint();
		rulerTextPaint.setTextSize(15 * scaleCoefficient);//原来是20的。
		rulerTextPaint.setAntiAlias(true);
		rulerDrawable = mapActivity.getResources().getDrawable(R.drawable.ruler);
	}
	
	@Override
	protected void hideControls(FrameLayout layout) {
	}
	
	@Override
	public void updateTextColor(int textColor, int shadowColor) {
		super.updateTextColor(textColor, shadowColor);
		rulerTextPaint.setColor(textColor);
	}

	@Override
	protected void showControls(FrameLayout layout) {
	}

	String rulerStringContent;
	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tb, DrawSettings nightMode) {
//		if( (zoomControls.isVisible() && zoomControls.isShowZoomLevel()) || !mapActivity.getMyApplication().getSettings().SHOW_RULER.get()){
//			return;
//		}
		OsmandMapTileView view = mapActivity.getMapView();
		float density = view.getApplication().getResources().getDisplayMetrics().density;
		// update cache
//		if (view.isZooming()) {
//			cacheRulerText = null;
//		} else if((tb.getZoom() + tb.getZoomScale()) != cacheRulerZoom ||
//				Math.abs(tb.getCenterTileX() - cacheRulerTileX) +  Math.abs(tb.getCenterTileY() - cacheRulerTileY) > 1){
			
			cacheRulerZoom = (tb.getZoom() + tb.getZoomScale());
			cacheRulerTileX = tb.getCenterTileX();
			cacheRulerTileY = tb.getCenterTileY();
			final double dist = tb.getDistance(0, tb.getPixHeight() / 2, tb.getPixWidth(), tb.getPixHeight() / 2);
			double pixDensity = tb.getPixWidth() / dist;
			
			double roundedDist = OsmAndFormatter.calculateRoundedDist(dist * screenRulerPercent, view.getApplication());			
			
			//以下原版
			int cacheRulerDistPix = (int) (pixDensity * roundedDist);
			
			int i5cacheRulerDistPix =(int)( pixDensity* calculateMyCacheRulerDistPix(roundedDist));
			//System.out.println("原版： "+cacheRulerDistPix+"，改版： "+i5cacheRulerDistPix);
			//以下原版
			//cacheRulerText = ShadowText.create(OsmAndFormatter.getFormattedDistance((float) roundedDist, view.getApplication()));
			
			//以下整数版
			rulerStringContent = getMyFormattedDistance((float)roundedDist, view.getApplication());
			cacheRulerText = ShadowText.create(rulerStringContent);
					
			
			cacheRulerTextLen = rulerTextPaint.measureText(cacheRulerText.getText());
			Rect bounds = rulerDrawable.getBounds();
			

			bounds.right = (int) (view.getWidth() - 7 * scaleCoefficient);
			
			//System.err.println("right: "+bounds.right+", i5cacheRulerDistPix: "+i5cacheRulerDistPix);
			//System.err.println("ii5cacheRulerDistPix 应该小一些: "+i5cacheRulerDistPix);
			bounds.left = bounds.right - i5cacheRulerDistPix;//bounds.left这个值需要大一些，意思是i5chcheRulerDistPix这个值要小一些。
			
			
			int r = bounds.right;
			int l = bounds.left;
			int gap = r - l;
			
			bounds.left=dip2px(density,60);//168  设定比例尺marginleft;
			
			//System.err.println("bounds.left: "+bounds.left);
			
			bounds.right = bounds.left+gap; 
			
//			System.err.println("r: "+r+", l: "+l+", gap: "+gap+", left: "+bounds.left+
//					", right: "+bounds.right);
			
			rulerDrawable.setBounds(bounds);	
			//System.err.println("MapActivity: bounds.right: "+bounds.right+", bounds.bottom是: "+bounds.bottom);
			//bounds.
			
			rulerDrawable.invalidateSelf();
		
		//} 
		
		if (cacheRulerText != null) {
			Rect rulerBounds = rulerDrawable.getBounds();
			//System.err.println("margin in RulerControl: "+vmargin);
			int bottom = (int) (view.getHeight() - vmargin);
			//System.err.println("Activity rulerBounds.bottom: "+rulerBounds.bottom+", 目标bottom："+bottom);
			if(rulerBounds.bottom != bottom) {
				rulerBounds.bottom = bottom- dip2px(density,10);//bottom-20;//多加了20个像素//设定比例尺的marginButton
				//System.err.println("10dp: "+dip2px(density,10));
				rulerBounds.top = rulerBounds.bottom - rulerDrawable.getMinimumHeight();
				rulerDrawable.setBounds(rulerBounds);
				rulerDrawable.invalidateSelf();
			}
			
			//System.err.println("right: "+bounds.right+", left: "+bounds.left+", top: "+bounds.top+", bottom: "+bounds.bottom);
			rulerDrawable.draw(canvas);
			
			cacheRulerText.drawString(rulerStringContent,canvas, 
					rulerBounds.left + (rulerBounds.width() - cacheRulerTextLen) / 2, 
					rulerBounds.bottom - 8 * scaleCoefficient-20,//设置字和比例尺的margin
					rulerTextPaint, 
					shadowColor);
//			cacheRulerText.draw(canvas, bounds.left + (bounds.width() - cacheRulerTextLen) / 2, bounds.bottom - 8 * scaleCoefficient,
//					rulerTextPaint, shadowColor);
		}
	}
	

	//将dp算成px
	 public static int dip2px(float density, float dpValue) {  
	        final float scale = density;  
	        return (int) (dpValue * scale + 0.5f);  
	  }  
	  
	    /** 
	     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	     */  
	public static int px2dip(float density, float pxValue) {  
	        final float scale = density;  
	        return (int) (pxValue / scale + 0.5f);  
	    }  
	    
	
	private final static float METERS_IN_KILOMETER = 1000f;
	private final static float YARDS_IN_ONE_METER = 1.0936f;
	private final static float FOOTS_IN_ONE_METER = YARDS_IN_ONE_METER * 3f;
	private final static float METERS_IN_ONE_MILE = 1609.344f; // 1609.344
	
	
	private int calculateMyCacheRulerDistPix(double meters){
		int mainUnitStr;
		int mainUnitStrNautical;
		float mainUnitInMeters;
		mainUnitStr = R.string.km;
		mainUnitInMeters = METERS_IN_KILOMETER;
		
//		if(meters>=200* mainUnitInMeters){
//			double t = (meters / mainUnitInMeters + 0.5)/1.852;
//			int t10 = (int) t/10;
//			double mod = t % 10;
//			if(mod>5){t10++;}
//			if(t10==0) {t10++;};
//			int re = t10 * 10;	
//			return re*1852;
//		}
//		
//		else 
//		if(meters >=500 * 1.9 * mainUnitInMeters)	{
//			double t = (meters / mainUnitInMeters +0.5)/1.852;
//			int t500 = (int) t/500;
//			double mod = t % 500;
//			if(mod>300){t500++;}
//			if(t500==0){t500++;}
//			int re = t500*50;//t500*500;
//			System.out.println("超过500： "+re);
//			return re*1852;
//		}
//		else if (meters >= 100*1.9 * mainUnitInMeters) {//超过100海里
//			double t = (meters / mainUnitInMeters + 0.5)/1.852;
//			int t100 = (int) t/100;
//			double mod = t % 100;
//			if(mod>50){t100++;}
//			if(t100==0) {t100++;};
//			int re = t100*50;//t100 * 100;	
//			System.out.println("超过100： "+re);
//			return re*1852;
//		}else 
			
			if(meters > 50*1.9*mainUnitInMeters){//超过50海里
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t50 = (int) t/50;
			double mod = t % 50;
			if(mod>30){t50++;}
			if(t50==0) {t50++;};
			int re = t50 * 50;	
			///System.out.println("超过50： "+re);
			return re*1852;
		}else if(meters > 10*1.9*mainUnitInMeters){//超过10海里
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t10 = (int) t/10;
			double mod = t % 10;
			if(mod>7){t10++;}
			if(t10==0) {t10++;};
			int re = t10 * 10;	
			//System.out.println("超过10： "+re);
			return re*1852;
		}			
		else if (meters > 9.99f * mainUnitInMeters) {
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t5 = (int) t/5;
			double mod = t % 5;
			if(mod>5){t5++;}
			if(t5==0) {t5++;};
			int re = t5 * 5;		
			return re*1852;
		}else if (meters > 1.9f * mainUnitInMeters){
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t5 = (int) t;
			double mod = t % 1;
			if(mod>0.5){t5++;}
			int re = t5;
			return re*1852;
		}
		else {
			return ((int) (meters + 0.5));
		}			
		
	}
	
	private String getMyFormattedDistance(float meters, OsmandApplication ctx){
		OsmandSettings settings = ctx.getSettings();
		MetricsConstants mc = settings.METRIC_SYSTEM.get();
		int mainUnitStr;
		int mainUnitStrNautical;
		float mainUnitInMeters;
		if (mc == MetricsConstants.KILOMETERS_AND_METERS) {
			mainUnitStr = R.string.km;
			mainUnitStrNautical = R.string.nautical_miles;
			mainUnitInMeters = METERS_IN_KILOMETER;
		} else {
			mainUnitStr = R.string.mile;
			mainUnitStrNautical = R.string.nautical_miles;
			mainUnitInMeters = METERS_IN_ONE_MILE;
		}
		
//		if(meters>=200* mainUnitInMeters){
//			double t = (meters / mainUnitInMeters + 0.5)/1.852;
//			int t10 = (int) t/10;
//			double mod = t % 10;
//			if(mod>5){t10++;}
//			if(t10==0) {t10++;};
//			int re = t10 * 10;	
//			return  re+" "+ctx.getString(mainUnitStrNautical);
//		}else 
			
//		if(meters >=500 * 1.9 * mainUnitInMeters){
//			double t = (meters / mainUnitInMeters + 0.5)/1.852;
//			int t500 = (int) t/500;
//			double mod = t % 500;
//			if(mod>300) t500++;
//			if(t500==0) t500++;
//			int re = t500*500;
//			return re+" "+ctx.getString(mainUnitStrNautical);
//		}
//		else if (meters >= 100 *1.9* mainUnitInMeters) {				
//			double t = (meters / mainUnitInMeters + 0.5)/1.852;
//			int t100 = (int) t/100;
//			double mod = t % 100;
//			if(mod>80){t100++;}
//			if(t100==0) {t100++;};
//			int re = t100 * 100;		
//			System.out.println(re+" "+ctx.getString(mainUnitStrNautical));
//			return  re+" "+ctx.getString(mainUnitStrNautical);				
//		} else 
			
			if(meters > 50*1.9*mainUnitInMeters){
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t50 = (int) t/50;
			double mod = t % 50;
			if(mod>30){t50++;}
			if(t50==0) {t50++;};
			int re = t50 * 50;	
			return re+" "+ctx.getString(mainUnitStrNautical);	
		}
		else if(meters > 10*1.9*mainUnitInMeters){//超过10海里
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t10 = (int) t/10;
			double mod = t % 10;
			if(mod>7){t10++;}
			if(t10==0) {t10++;};
			int re = t10 * 10;	
			return re+" "+ctx.getString(mainUnitStrNautical);
		}	
			
			else if (meters > 9.99f * mainUnitInMeters) {				
			double t = ((float) meters) / (mainUnitInMeters*1.825);
			int t5 = (int) t/5;
			double mod = t % 5;
			if(mod>5){t5++;}
			if(t5==0){t5++;}
			int re = t5 * 5;
			return MessageFormat.format("{0,number,#} " + ctx.getString(mainUnitStrNautical), re);
		
		
		} 
		else if (meters > 1.9f * mainUnitInMeters){
			double t = (meters / mainUnitInMeters + 0.5)/1.852;
			int t5 = (int) t;
			double mod = t % 1;
			if(mod>0.5){t5++;}
			int re = t5;
			return MessageFormat.format("{0,number,#} " + ctx.getString(mainUnitStrNautical), re);
		}

		else {
			if (mc == MetricsConstants.KILOMETERS_AND_METERS) {
				return ((int) (meters + 0.5)) + " " + ctx.getString(R.string.m); //$NON-NLS-1$
			} else if (mc == MetricsConstants.MILES_AND_FOOTS) {
				int foots = (int) (meters * FOOTS_IN_ONE_METER + 0.5);
				return foots + " " + ctx.getString(R.string.foot); //$NON-NLS-1$
			} else if (mc == MetricsConstants.MILES_AND_YARDS) {
				int yards = (int) (meters * YARDS_IN_ONE_METER + 0.5);
				return yards + " " + ctx.getString(R.string.yard); //$NON-NLS-1$
			}
			return ((int) (meters + 0.5)) + " " + ctx.getString(R.string.m); //$NON-NLS-1$
		}
	}
}
