/**    
 * @{#} ImageViewExt.java Create on 2014-7-5 下午3:58:51    
 *          
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 */    
package com.e.filechoose.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewExt extends ImageView {

	/**
	 * @param context
	 */
	public ImageViewExt(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ImageViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ImageViewExt(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		try {
			super.onDraw(canvas);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
