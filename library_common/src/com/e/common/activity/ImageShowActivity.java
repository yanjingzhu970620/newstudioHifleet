/**    
 * @{#} ImageShowActivity.java Create on 2013-8-21 下午4:12:37    
 *        
 * @author Evan
 *
 * @email evan0502@qq.com
 *
 * @version 1.0    
 */
package com.e.common.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;

import com.e.common.constant.Constants.IDENTITY;
import com.e.common.widget.TouchImageView;
import com.e.library_common.R;

@SuppressLint("HandlerLeak")
public class ImageShowActivity extends BaseActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bluemobi.diabetes.patinenter.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		try {
			String path = getIntent()
					.getStringExtra(IDENTITY.IDENTITY_FILEPATH).toString();
			TouchImageView imageView = (TouchImageView) findViewById(R.id.image_big_show);
			imageView.loadImage(path);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		// destoryBitmap(bitmap);
		return true;
	}

}
