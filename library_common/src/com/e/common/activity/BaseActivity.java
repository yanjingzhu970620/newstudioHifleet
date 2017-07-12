/**    
 * @{#} BaseActivity.java Create on 2014年10月28日 上午10:37:04    
 *    
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 * @description
 *	
 */
package com.e.common.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.common.event.EventTypeLoginOrLogout;
import com.e.common.event.EventTypeRequest;
import com.e.common.i.IType;
import com.e.common.utility.CommonUtility;
import com.e.common.utility.CommonUtility.BitmapOperateUtility;
import com.e.common.utility.CommonUtility.DebugLog;
import com.e.common.utility.CommonUtility.Utility;
import com.e.common.widget.LoadingView;
import com.e.library_common.R;

import de.greenrobot.event.EventBus;

public class BaseActivity extends FragmentActivity implements IType {

	public Activity activity;

	// 缓存当前activity图片资源，finish后release
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

	private ArrayList<BroadcastReceiver> receivers;

	public void onEventMainThread(EventTypeRequest event) {
        if (event.getTarget() == this) {
            if (event instanceof EventTypeRequest) {
                int resultCode = event.getResultCode();
                if (resultCode == EventTypeRequest.RESULT_CODE_EXCEPTION_SERVER) {
                    CommonUtility.UIUtility.toast(activity, "服务器正在维修，请稍后再试");
                    onRequestNetBad(event);
                } else if (resultCode == EventTypeRequest.RESULT_CODE_EXCEPTION_DEVICE) {
                    CommonUtility.UIUtility.toast(activity, "请检查网络设置");
                    onRequestNetBad(event);
                } else {
                    onRequestFinish(event);
                }
            }
        }
    }

    public void onEventMainThread(EventTypeLoginOrLogout event) {
        if (event.getTarget().getSimpleName()
                .equals(getClass().getSimpleName())) {
            onLoginOrLogout((EventTypeLoginOrLogout) event);
        }
    }

	/**
	 * method desc：网络请求成功
	 *
	 * @param event
	 */
	public void onRequestFinish(EventTypeRequest event) {
		LoadingView.hide(this);
	}

	/**
	 * method desc：网络有问题
	 *
	 * @param event
	 */
	public void onRequestNetBad(EventTypeRequest event) {
		LoadingView.hide(this);
	}

	public void onLoginOrLogout(EventTypeLoginOrLogout event) {
		DebugLog.log(event.getTarget().getSimpleName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		activity = this;
		EventBus.getDefault().register(this);
	}

	@Override
	public void addBitmap(Bitmap bitmap) {
		// TODO Auto-generated method stub
		BitmapOperateUtility.addBitmap(bitmap, bitmaps);
	}

	@Override
	public void destoryBitmaps() {
		BitmapOperateUtility.destoryBitmaps(bitmaps);
	}

	@Override
	public void destoryBitmap(Bitmap bitmap) {
		BitmapOperateUtility.destoryBitmap(bitmap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BitmapOperateUtility.destoryBitmaps(bitmaps);
		if (!Utility.isNull(receivers)) {
			for (BroadcastReceiver receiver : receivers) {
				LocalBroadcastManager.getInstance(activity).unregisterReceiver(
						receiver);
			}
		}
		EventBus.getDefault().unregister(this);
	}

	public void registerBroadcastReceiver(BroadcastReceiver receiver,
			String action) {
		if (Utility.isNull(receivers)) {
			receivers = new ArrayList<BroadcastReceiver>();
		}
		receivers.add(receiver);
		LocalBroadcastManager.getInstance(activity).registerReceiver(receiver,
				new IntentFilter(action));
	}

	protected TextView mTextLeft, mTextNav, mTextRight, mTextBackup;

	protected ImageView mImageLeft, mImageRight;

	protected void initNav() {
		mImageRight = (ImageView) findViewById(R.id.img_nav_right);
		mTextRight = (TextView) findViewById(R.id.text_nav_right);

		mImageLeft = (ImageView) findViewById(R.id.img_nav_left);
		mTextLeft = (TextView) findViewById(R.id.text_nav_left);

		mTextNav = (TextView) findViewById(R.id.text_nav);
		mTextBackup = (TextView) findViewById(R.id.text_backup);
	}

	public void setImageNavLeftResource(int resId) {
		if (!Utility.isNull(mImageLeft)) {
			mImageLeft.setImageResource(resId);
			mImageLeft.setVisibility(View.VISIBLE);
		}
	}

	public void setTextNavLeft(String text) {
		if (!Utility.isNull(mTextLeft)) {
			mTextLeft.setText(text);
			mTextLeft.setVisibility(View.VISIBLE);
		}
	}

	public void setImageNavRightResource(int resId) {
		if (!Utility.isNull(mImageRight)) {
			mImageRight.setImageResource(resId);
			mImageRight.setVisibility(View.VISIBLE);
		}
	}

	public void setTextNavRight(String text) {
		if (!Utility.isNull(mTextRight)) {
			mTextRight.setText(text);
			mTextRight.setVisibility(View.VISIBLE);
		}
	}

	public void setNavTitle(String text) {
		if (!Utility.isNull(mTextNav)) {
			mTextNav.setText(text);
			mTextNav.setVisibility(View.VISIBLE);
		}
	}

	public void setNavBackTitle(String text) {
		if (!Utility.isNull(mTextBackup)) {
			mTextBackup.setText(text);
			mTextBackup.setVisibility(View.VISIBLE);
		}
	}

	public void hideNavLeftImage() {
		if (!Utility.isNull(mImageLeft)) {
			mImageLeft.setVisibility(View.GONE);
		}
	}

	public void onClick(View view) {
		if (view.getId() == R.id.ll_nav_left) {
			finish();
		}
	}

}
