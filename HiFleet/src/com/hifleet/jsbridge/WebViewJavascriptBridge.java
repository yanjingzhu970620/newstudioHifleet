package com.hifleet.jsbridge;


public interface WebViewJavascriptBridge {
	
	public void send(String data);
	public void send(String data, CallBackFunction responseCallback);
	public void callHandler(String name,String data);
	

}
