/**    
 * @{#} IActivity.java Create on 2014年10月28日 上午10:37:04    
 *    
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 * @description
 *	
 */
package com.e.common.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.common.event.EventTypeLoginOrLogout;
import com.e.common.event.EventTypeRequest;
import com.e.common.utility.CommonUtility;
import com.e.common.utility.CommonUtility.DebugLog;
import com.e.common.utility.CommonUtility.Utility;
import com.e.common.widget.LoadingView;
import com.e.library_common.R;

import de.greenrobot.event.EventBus;

public class BaseFragment extends Fragment {

	public Activity activity;

	public Fragment fragment;

	// 当前界面的view
	public View view;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fragment = this;
		activity = getActivity();
		EventBus.getDefault().register(this);
	}

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
		LoadingView.hide(activity);
	}

	/**
	 * method desc：网络有问题
	 *
	 * @param event
	 */
	public void onRequestNetBad(EventTypeRequest event) {
		LoadingView.hide(activity);
	}

	public void onLoginOrLogout(EventTypeLoginOrLogout event) {
		DebugLog.log(event.getTarget().getSimpleName());
	}
	
	protected TextView mTextLeft, mTextNav, mTextRight, mTextBackup;

	protected ImageView mImageLeft, mImageRight;

	protected void initNav() {
		mImageLeft = (ImageView) getView().findViewById(R.id.img_nav_left);
		mTextLeft = (TextView) getView().findViewById(R.id.text_nav_left);

		mImageRight = (ImageView) getView().findViewById(R.id.img_nav_right);
		mTextRight = (TextView) getView().findViewById(R.id.text_nav_right);

		mTextNav = (TextView) getView().findViewById(R.id.text_nav);
		mTextBackup = (TextView) getView().findViewById(R.id.text_backup);
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
			activity.finish();
		}
	}

}
