package com.hifleet.map;

import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;

public class MultiTouchSupport {

	// static final Log //log = PlatformUtil.getLog(MultiTouchSupport.class);
	
    public static final int ACTION_MASK = 255;
    public static final int ACTION_POINTER_ID_SHIFT = 8;
    public static final int ACTION_POINTER_DOWN     = 5;
    public static final int ACTION_POINTER_UP     = 6;
	private float angleStarted;
	private float angleRelative;

	public interface MultiTouchZoomListener {
    	
    	public void onZoomStarted(PointF centerPoint);
    	
    	public void onZoomingOrRotating(double relativeToStart, float angle);
    	
    	public void onZoomEnded(double relativeToStart, float angleRelative);
    	
    	public void onGestureInit(float x1, float y1, float x2, float y2);
    	
    }
    
    private boolean multiTouchAPISupported = false;
	private final MultiTouchZoomListener listener;
	protected final Context ctx;

	protected Method getPointerCount;
	protected Method getX;
	protected Method getY;
	protected Method getPointerId;
	
	
    
    public MultiTouchSupport(Context ctx, MultiTouchZoomListener listener){
		this.ctx = ctx;
		this.listener = listener;
		initMethods();
    }
    
    public boolean isMultiTouchSupported(){
    	return multiTouchAPISupported; 
    }
    
    public boolean isInZoomMode(){
    	return inZoomMode;
    }
    public void setInZoomMode(boolean value){
    	inZoomMode=value;
    }
    public int getTouchNumber(){
    	return previousPointersNumber;
    }
    private void initMethods(){
    	try {
    		getPointerCount = MotionEvent.class.getMethod("getPointerCount"); //$NON-NLS-1$
    		getPointerId = MotionEvent.class.getMethod("getPointerId", Integer.TYPE); //$NON-NLS-1$
    		getX = MotionEvent.class.getMethod("getX", Integer.TYPE); //$NON-NLS-1$
    		getY = MotionEvent.class.getMethod("getY", Integer.TYPE); //$NON-NLS-1$
    		multiTouchAPISupported = true;
    	} catch (Exception e) {
    		multiTouchAPISupported = false;
    		//log.info("Multi touch not supported", e); //$NON-NLS-1$
		}
    }
    
    private boolean inZoomMode = false;
    private double zoomStartedDistance = 100;
    private double zoomRelative = 1;
    private PointF centerPoint = new PointF();
    private int previousPointersNumber=0;
    
    public boolean onTouchEvent(MotionEvent event){
    	
    	//System.err.println("多点触控支持.");
    	
    	if(!isMultiTouchSupported()){
    		return false;
    	}
    	
    	
    	
    	int actionCode = event.getAction() & ACTION_MASK;
    	
    	try {    		
    		
			Integer pointCount = (Integer) getPointerCount.invoke(event);
			
			if(pointCount < 2){
				if(2<=previousPointersNumber){
					previousPointersNumber = pointCount;
					//System.out.println("11111111111 ：return true");
//					listener.onZoomEnded(zoomRelative, angleRelative);
//					inZoomMode=true;
					return true;
				}
				else {
					//System.out.println("222222222 return false.");
					inZoomMode=true;
					return false;
				}
				
			}else if(pointCount > 2){
				previousPointersNumber = pointCount;
				return false;
			}
			
			
			Float x1 = (Float) getX.invoke(event, 0);
			Float x2 = (Float) getX.invoke(event, 1);
			Float y1 = (Float) getY.invoke(event, 0);
			Float y2 = (Float) getY.invoke(event, 1);
			float distance = FloatMath.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		    float angle = 0;
		    boolean angleDefined = false;
		    if(x1 != x2 || y1 != y2) {
			    angleDefined = true;
			    angle = (float) (Math.atan2(y2 - y1, x2 -x1) * 180 / Math.PI);
		    }
		    angle=0;
		    
		    if (actionCode == ACTION_POINTER_DOWN) {
		    	previousPointersNumber=2;
				centerPoint = new PointF((x1 + x2) / 2, (y1 + y2) / 2);
				listener.onGestureInit(x1, y1, x2, y2);
				listener.onZoomStarted(centerPoint);
				zoomStartedDistance = distance;
				angleStarted = angle;
				inZoomMode = true;
				//System.out.println("33333333333 return true");
				return true;
			} else if(actionCode == ACTION_POINTER_UP){
				previousPointersNumber=2;
				angleRelative = 0;
				
				if(inZoomMode){
					//System.out.println("调用监听器的zoomended函数。");
					listener.onZoomEnded(zoomRelative, angleRelative);
					inZoomMode = false;
				}
				return true;
			} else if(inZoomMode && actionCode == MotionEvent.ACTION_MOVE){
				if(angleDefined) {
					angleRelative = MapUtils.unifyRotationTo360(angle - angleStarted);
				}
				
				angleRelative = 0;
				
				zoomRelative = distance / zoomStartedDistance;
				//System.out.println("调用监听器的onZoomingOrRotating函数。");
				listener.onZoomingOrRotating(zoomRelative, angleRelative);
				return true;
			}
    	} catch (Exception e) {
    		return false;
		}
    	return false;
    }
    
    private static void print(String msg){
    	android.util.Log.i(TAG, msg);
    }
public static final String TAG = "FileDownloader";

}
