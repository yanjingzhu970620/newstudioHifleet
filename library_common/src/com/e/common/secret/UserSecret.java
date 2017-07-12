package com.e.common.secret;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.RootContext;
import org.json.JSONObject;

import android.content.Context;

import com.e.common.utility.CommonUtility.SharedPreferencesUtility;
import com.e.common.utility.CommonUtility.Utility;

/**
 * @{# userSecret.java Create on 2015年1月21日 下午9:15:24
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
@EBean(scope = Scope.Singleton)
public class UserSecret {

	@RootContext
	protected Context mContext;

	private JSONObject mUser;

	private final String KEY_USER = "user";

	public JSONObject getUserSecret() {
		if (Utility.isNull(mUser)) {
			try {
				String content = SharedPreferencesUtility.getSharedPreferences(
						mContext).getString(KEY_USER, null);
				if (!Utility.isNull(content)) {
					mUser = new JSONObject(content);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mUser;
	}

	public void saveUserSecret(Object object) {
		if (!Utility.isNull(object)) {
			SharedPreferencesUtility.getSharedPreferences(mContext).putString(
					KEY_USER, object.toString());
			getUserSecret();
		}
	}

	public void clearUser() {
		mUser = null;
		SharedPreferencesUtility.getSharedPreferences(mContext)
				.getSharedPreference().edit().remove(KEY_USER).commit();
	}
}
