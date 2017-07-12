package com.e.common.event;

import org.json.JSONObject;

/**
 * @{# EventType.java Create on 2015年1月20日 下午5:30:22
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description 处理网络请求后返回的实体
 */
public final class EventTypeRequest extends EventType {

	public static final int RESULT_CODE_OK = 200, RESULT_CODE_EXCEPTION_SERVER = 400, RESULT_CODE_EXCEPTION_DEVICE = 600;

    private Object target; // 特定的接收类
	private JSONObject data;
	private String dataStr;
	public String getDataStr() {
		return dataStr;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}

	private int resultCode = 200;


    /**
     * @return the target
     */
    public Object getTarget() {
        return target;
    }

    /**
     * @param target
     *            the target to set
     */
    public void setTarget(Object target) {
        this.target = target;
    }
	/**
	 * @return the data
	 */
	public JSONObject getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(JSONObject data) {
		this.data = data;
	}

	/**
	 * @return the resultCode
	 */
	public int getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode
	 *            the resultCode to set
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

}
