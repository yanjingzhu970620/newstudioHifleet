package com.hifleet.utility;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;


public class ShadowText {
private String text;
	
	public ShadowText(String text) {
		this.text = text;
	}
	public static ShadowText create(String text) {
		return new ShadowText(text);
	}

	
	public void drawString(String text, Canvas cv, 
	float centerX, float centerY, Paint textPaint, int shadowColor){

		int c = textPaint.getColor();
		textPaint.setStyle(Style.STROKE);
		textPaint.setColor(shadowColor);
		textPaint.setStrokeWidth(0.8f);
		textPaint.setTextSize(45);
		cv.drawText(text, centerX, centerY, textPaint);
		
		// reset
		textPaint.setStrokeWidth(0.8f);
		textPaint.setStyle(Style.FILL);
		textPaint.setColor(c);
		cv.drawText(text, centerX, centerY, textPaint);
		
	}

	
	public void draw(Canvas cv, float centerX, float centerY, Paint textPaint, int shadowColor) {
		draw(text, cv, centerX, centerY, textPaint, shadowColor);
	}
	public static void draw(String text, Canvas cv, 
			float centerX, float centerY, Paint textPaint, int shadowColor) {
		
		
//		textPaint.setAntiAlias(true);                       //设置画笔为无锯齿  
//		textPaint.setColor(Color.BLACK);                    //设置画笔颜色  
//	    cv.drawColor(R.color.background_fill_color_1);      //白色背景  
//	    textPaint.setStrokeWidth((float) 1.0);              //线宽  
//	    textPaint.setStyle(Style.STROKE);                   //空心效果  
//	    RectF r1=new RectF();                         		//Rect对象  
//	    r1.left=centerX;                              		//左边  
//	    r1.top=centerY;                                  	//上边  
//	    r1.right=150;                                   	//右边  
//	    r1.bottom=150;                              		//下边  
//	    cv.drawRoundRect(r1,20,20, textPaint);            	//绘制圆角矩形  
//	    RectF r2=new RectF();                           	//RectF对象  
//	    r2.left=50;                                 		//左边  
//	    r2.top=400;                                 		//上边  
//	    r2.right=450;                                   	//右边  
//	    r2.bottom=600;                              		//下边  
//	    cv.drawRoundRect(r2, 10, 10, textPaint);		
	  
		
//		TextPaint tPaint = new TextPaint();
//		tPaint.setColor(Color.BLACK);
//		tPaint.setStrokeWidth(0.8f);
//		tPaint.setTextSize(45.0F);
//		Rect rect = new Rect(); 
//		tPaint.getTextBounds(text, 0, 1, rect); 
//		
//		int strwid = rect.width();   
//		int strhei = rect.height();
//		
//		System.err.println("画布的尺寸, H: "+cv.getHeight()+", W: "+cv.getWidth());
//		System.err.println("字符的尺寸, H: "+strhei+", W: "+strwid );
//		//tPaint.measureText(text);
//		
//		
//		StaticLayout layout = new StaticLayout(text,tPaint,60,
//				Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
//		cv.translate((cv.getWidth()-strwid-10),(strhei+10));
//		layout.draw(cv);
		
		
		
		int c = textPaint.getColor();
		textPaint.setStyle(Style.STROKE);
		textPaint.setColor(shadowColor);
		textPaint.setStrokeWidth(0.8f);
		textPaint.setTextSize(55);
		cv.drawText(text, centerX, centerY, textPaint);
		
		// reset
		textPaint.setStrokeWidth(0.8f);
		textPaint.setStyle(Style.FILL);
		textPaint.setColor(c);
		cv.drawText(text, centerX, centerY, textPaint);
		
		//cv.draw
	}
	
	public String getText() {
		return text;
	}
}
