package com.hifleet.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {
	 private int sroke_width = 0;  
	 private int stroke=3;
	 private int reserved = 20;
	 private int blank_space=36;
	 private int extend = 46;
	 private Context ctx;
	public MyTextView(Context context) {  
        super(context);  
        ctx = context;
    }
	public MyTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        ctx = context;
    }
	@Override  
    protected void onDraw(Canvas canvas){
		//this.setHeight(80);
		Paint paint = new Paint();  
        //  将边框设为GRAY  
       // paint.setColor(android.graphics.Color.GRAY);  
       // paint.setColor(0x009966);
		//paint.setColor(ctx.getResources().getColor(R.color.background_fill_color_1));
		paint.setARGB(224, 255, 219, 143);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(stroke);
        
        //  画TextView的4个边  
        
        //RectF oval = new RectF(0,0,this.getWidth(),this.getHeight());
        //canvas.drawArc(oval, 90, 180, true, paint);
       // canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);  
        //canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);  
      //  canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);  
       
        /*
         * 画底边那条线左边一半
      
        canvas.drawLine(
        		reserved, 
        		this.getHeight() - sroke_width,
        		
        		(this.getWidth()/2)-blank_space,        		
        		this.getHeight() - sroke_width,
        		
        		paint);  
        */
        
        /*
        canvas.drawLine(
        		(this.getWidth()/2)-blank_space,        		
        		this.getHeight() - sroke_width, 
        		
        		this.getWidth()/2, 
        		(this.getHeight() - sroke_width+extend), 
        		
        		paint);
        //画左边那条腿
        
        canvas.drawLine(
        		this.getWidth()/2, 
        		(this.getHeight() - sroke_width+extend),  
        		
        		(this.getWidth()/2)+blank_space, 
        		this.getHeight() - sroke_width, 
        		
        		paint);
        //画右边那条腿
        */
        
        Path path = new Path();
        path.moveTo((this.getWidth()/2)-blank_space,        		
        		this.getHeight() - sroke_width);
        path.lineTo(this.getWidth()/2, 
        		(this.getHeight() - sroke_width+extend));
        path.lineTo(this.getWidth()/2, 
        		(this.getHeight() - sroke_width+extend));
        path.lineTo((this.getWidth()/2)+blank_space, 
        		this.getHeight() - sroke_width);
        path.lineTo((this.getWidth()/2)-blank_space,        		
        		this.getHeight() - sroke_width);
        path.close();
        canvas.drawPath(path,paint);
        
        /*
        canvas.drawLine(
        		
        		(this.getWidth()/2)+blank_space, 
        		this.getHeight() - sroke_width,
        		
        		this.getWidth()-reserved,
        		this.getHeight() - sroke_width,
        		
        		paint);
        */
        super.onDraw(canvas);  
	}
}
