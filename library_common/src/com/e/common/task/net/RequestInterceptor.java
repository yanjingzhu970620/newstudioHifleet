package com.e.common.task.net;

import android.content.Context;

import com.e.common.event.EventTypeRequest;
import com.e.common.manager.net.NetManager;
import com.e.common.utility.CommonUtility.Utility;
import com.e.common.widget.LoadingView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @{# RequestIntercepter.java Create on 2015年1月20日 下午4:00:00
 * @description
 */
@EBean(scope = Scope.Singleton)
public class RequestInterceptor {

    public static final int NO_TAG = 0x0001;

    public static final boolean MULTIPART = true;

    public static final boolean SHOWDIALOG = true;

    public String[] mKeys;
    public Object[] mValues;

    /**
     * method desc：发出一个默认显示loading框的请求，并且无标识，适用于当前界面只有一个请求
     *
     * @param context
     * @param url
     * @param params
     */
    public void request(Context context, String url,
                        HashMap<String, Object> params, Object target) {
        request(context, url, params, target, NO_TAG, SHOWDIALOG, !MULTIPART);
    }

    /**
     * method desc：发出一个带有tag，并且标记是否显示loading框
     *
     * @param context
     * @param url
     * @param params
     * @param isShowDialog
     */
    public void request(Context context, String url,
                        HashMap<String, Object> params, Object target, boolean isShowDialog) {
        request(context, url, params, target, NO_TAG, isShowDialog, !MULTIPART);
    }

    /**
     * method desc：发出一个带有tag的请求，主要用于一个界面有多个请求时区分
     *
     * @param context
     * @param url
     * @param params
     * @param tag
     */
    public void request(Context context, String url,
                        HashMap<String, Object> params, Object target, int tag) {
        request(context, url, params, target, tag, SHOWDIALOG, !MULTIPART);
    }

    /**
     * method desc：发出一个带有tag，并且标记是否显示loading框，主要用于一个界面有多个请求时区分
     *
     * @param context
     * @param url
     * @param params
     * @param tag
     * @param isShowDialog
     */
    public void request(Context context, String url,
                        HashMap<String, Object> params, Object target, int tag,
                        boolean isShowDialog) {
        request(context, url, params, target, tag, isShowDialog, !MULTIPART);
    }

    /**
     * method desc：发出一个带有tag，并且标记是否显示loading框，主要用于一个界面有多个请求时区分
     *
     * @param context
     * @param url
     * @param params
     * @param tag
     * @param isShowDialog
     * @param isMultipart
     */
    public void request(Context context, String url,
                        HashMap<String, Object> params, Object target, int tag,
                        boolean isShowDialog, boolean isMultipart) {
        if (isShowDialog) {
            LoadingView.show(context);
        }
        requestInvoke(context, url, params, target, tag, isMultipart);
    }

    /**
     * method desc：后台请求
     *
     * @param context
     * @param url
     * @param params
     * @param tag
     */
    @Background
    void requestInvoke(Context context, String url,
                       HashMap<String, Object> params, Object target, int tag, boolean isMultipart) {
        if (!Utility.isNull(mKeys) && !Utility.isNull(mValues)) {
            if (mKeys.length != mValues.length) {
                throw new ArrayIndexOutOfBoundsException("键值 length不一致");
            }
            for (int i = 0; i < mKeys.length; i++) {
                params.put(mKeys[i], mValues[i]);
            }
        }
        String data = "";
        if (isMultipart) {
            data = NetManager.getInstance(context)
                    .sendRequestFromHttpClientByOkHttpMultipart(url, params);
        } else {
            data = NetManager.getInstance(context)
                    .sendRequestFromHttpClientByOkHttpForm(url, params);
        }
        if (!Utility.isNull(data)) {
            EventTypeRequest eventType = new EventTypeRequest();
            eventType.setTag(tag);
            eventType.setTarget(target);
            if (data.equals(NetManager.NET_EXCEPTION_BY_SERVER)) {
                eventType.setResultCode(EventTypeRequest.RESULT_CODE_EXCEPTION_SERVER);
            } else if (data.equals(NetManager.NET_EXCEPTION_BY_DEVICE)) {
                eventType.setResultCode(EventTypeRequest.RESULT_CODE_EXCEPTION_DEVICE);
            } else {
                try {
                	if(data.startsWith("{")) {
                		eventType.setData(new JSONObject(data));
                	} else {
                		eventType.setDataStr(data);
                	}
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            EventBus.getDefault().post(eventType);
        }
    }

    /**
     * 设置公共参数， 类似token
     *
     * @param keys
     * @param values
     */
    public void setCommonParams(String[] keys, Object[] values) {
        mKeys = keys;
        mValues = values;
    }
}
