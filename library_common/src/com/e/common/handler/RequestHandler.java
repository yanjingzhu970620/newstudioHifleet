package com.e.common.handler;

import org.json.JSONObject;

import android.content.Context;
import android.os.Message;

import com.e.common.handler.callback.HandlerCallback;
import com.e.common.i.IJsonOperate;
import com.e.common.utility.CommonUtility.Utility;

public class RequestHandler extends BaseHandler {

	private HandlerCallback mCallBack;
	private Context context;

	public RequestHandler(Context context, HandlerCallback mCallback) {
		super(context);
		this.context = context;
		this.mCallBack = mCallback;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		if (!Utility.isNull(mCallBack)) {
			Object object = msg.obj;
			if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (!Utility.isNull(jsonObject)) {
					if (((IJsonOperate) context.getApplicationContext())
							.isRespSuccess(jsonObject)) {
						mCallBack.onSuccess(jsonObject);
					} else {
						mCallBack.onFailure(context, jsonObject);
					}
				} else {
					mCallBack.onRespNull();
				}
			} else {
				if (!Utility.isNull(object)) {
					mCallBack.onSuccess(object);
				} else {
					mCallBack.onFailure(context, object);
				}
			}
		}
		mCallBack = null;
	}

}
