package com.e.common.handler.callback;

import android.content.Context;

import com.e.common.i.IHandlerCallback;
import com.e.common.i.IJsonOperate;
import com.e.common.utility.CommonUtility.UIUtility;
import com.e.common.utility.CommonUtility.Utility;

/**
 * @{# HandlerCallback.java Create on 2014年11月25日 下午5:09:11
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public abstract class HandlerCallback implements IHandlerCallback {

	public abstract void onSuccess(Object o);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.commonproject.e.handler.callback.IHandlerCallback#onFailure(java
	 * .lang.Object)
	 */
	@Override
	public void onFailure(Context context, Object o) {
		// TODO Auto-generated method stub
		String result = ((IJsonOperate) context.getApplicationContext())
				.getRespMsg(o);
		if (!Utility.isNull(result)) {
			UIUtility.toast(context, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.commonproject.e.handler.callback.IHandlerCallback#onRespNull()
	 */
	@Override
	public void onRespNull() {
		// TODO Auto-generated method stub

	}

}
