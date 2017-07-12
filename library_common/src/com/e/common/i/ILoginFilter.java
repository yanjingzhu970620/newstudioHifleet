package com.e.common.i;

import android.content.Context;
import android.view.View;

/**
 * @{# ILoginFilter.java Create on 2014年11月25日 下午2:47:20
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public interface ILoginFilter {

	/**
	 * method desc：判断是否需要登陆后才能操作
	 *
	 * @param view
	 */
	boolean doFilterLogin(Context context, int resId);

	/**
	 * method desc：需要登陆后操作的标识，在进入activity或者fragment时进行添加 e.g 通常为view.getId() see
	 * {@link #doFilterLogin(Context context, View view)}
	 *
	 * @param key
	 */
	void putFilterKey(Context context, int resId);

}
