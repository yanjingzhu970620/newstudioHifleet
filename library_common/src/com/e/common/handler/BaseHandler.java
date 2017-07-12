package com.e.common.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.e.common.utility.CommonUtility.UIUtility;

public class BaseHandler extends Handler {

	private Context context;

	public BaseHandler(Context context) {
		this.context = context;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what) {
		default:
			try {
				if (msg.what != 0) {
					UIUtility.toast(context, msg.what);
				}
				// LoadingProgressExt.dismiss();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
		}
	}

}
