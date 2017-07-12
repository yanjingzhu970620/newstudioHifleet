/**    
 * @{#} ImageHandler.java Create on 2013-7-3 上午9:17:47    
 *        
 * @author Evan
 *
 * @email evan0502@qq.com
 *
 * @version 1.0    
 */
package com.e.common.handler;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.e.common.i.ImageHandlerCallback;

public class ImageHandler extends Handler {
	private ImageHandlerCallback mCallBack;
	public View view;

	public ImageHandler(Activity activity, ImageHandlerCallback callBack,
			View view) {
		this.mCallBack = callBack;
		this.view = view;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		mCallBack.callback(msg.obj, view);
		mCallBack = null;
	}
}
