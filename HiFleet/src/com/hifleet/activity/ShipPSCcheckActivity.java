package com.hifleet.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * @{# WeatherShuiwenActivity.java Create on 2015年7月17日 下午1:20:36
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class ShipPSCcheckActivity extends Activity {
	WebView pscWebview;
	TextView textBack,textShipName;
	ProgressBar progressBar;
	private String mShipName, mShipImo, mShipLo,url;
	OsmandApplication app;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_psccheck);
		app=this.getMyApplication();
		pscWebview = (WebView) findViewById(R.id.web_psccheck);
		textBack = (TextView) findViewById(R.id.psc_text_back);
		progressBar = (ProgressBar) findViewById(R.id.psc_progress);
		textShipName = (TextView)findViewById(R.id.psc_text_shipname);

		Bundle bundle = this.getIntent().getExtras();
		mShipName = bundle.getString("shipname");
		mShipImo = bundle.getString("imo");
		url = bundle.getString("url");

		textShipName.setText(mShipName);

		if(app.pscfirst) {
			LodingFirstcookieThread getcookiethread= new LodingFirstcookieThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// print("MoreActivity 》 Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
				getcookiethread.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				getcookiethread.execute();
			}
		} else {
			pscWebview.getSettings().setJavaScriptEnabled(true);
			// 设置可以支持缩放
			pscWebview.getSettings().setSupportZoom(true);
			// 设置出现缩放工具
			pscWebview.getSettings().setBuiltInZoomControls(true);
//		       //扩大比例的缩放
//		webview.getSettings().setUseWideViewPort(true);
			//自适应屏幕
			pscWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
			pscWebview.getSettings().setLoadWithOverviewMode(true);

//		webview.loadUrl("http://www.hifleet.com:8081/getMarineWeatherMobile/30,121");
			String postdata = "Request=" + mShipImo + "&Selector=IMO_No";


			pscWebview.setWebChromeClient(new WebChromeClient() {
				/*
                 * (non-Javadoc)
                 *
                 * @see
                 * android.webkit.WebChromeClient#onProgressChanged(android.webkit
                 * .WebView, int)
                 */
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					// TODO Auto-generated method stub
					super.onProgressChanged(view, newProgress);
					if (newProgress == 100) {
						progressBar.setVisibility(View.GONE);
					}
				}

			});
			pscWebview.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					return false;

				}

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					Toast.makeText(getApplicationContext(), "网络连接失败 ,请连接网络。", Toast.LENGTH_SHORT).show();
				}
			});
			pscWebview.postUrl(url, postdata.getBytes());
		}
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		}
	}

	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}

	class LodingFirstcookieThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		int responsecode=0;
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String postparams ="Request="+mShipImo+"&Selector=IMO_No";
				byte[] data = postparams.getBytes();

				URL posturl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) posturl.openConnection();
				conn.setConnectTimeout(3000);
				//这是请求方式为POST
				conn.setRequestMethod("POST");
				//设置post请求必要的请求头
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 请求头, 必须设置
				conn.setRequestProperty("Content-Length", data.length + ""); // 注意是字节长度, 不是字符长度

				conn.setDoOutput(true); // 准备写出
				conn.getOutputStream().write(data);
				responsecode=conn.getResponseCode();
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println("未能获取网络数据");
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
                if(responsecode==200){
					pscWebview.getSettings().setJavaScriptEnabled(true);
					// 设置可以支持缩放
					pscWebview.getSettings().setSupportZoom(true);
					// 设置出现缩放工具
					pscWebview.getSettings().setBuiltInZoomControls(true);
//		       //扩大比例的缩放
//		webview.getSettings().setUseWideViewPort(true);
					//自适应屏幕
					pscWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
					pscWebview.getSettings().setLoadWithOverviewMode(true);

//		webview.loadUrl("http://www.hifleet.com:8081/getMarineWeatherMobile/30,121");
					String postdata = "Request=" + mShipImo + "&Selector=IMO_No";


					pscWebview.setWebChromeClient(new WebChromeClient() {
						/*
                         * (non-Javadoc)
                         *
                         * @see
                         * android.webkit.WebChromeClient#onProgressChanged(android.webkit
                         * .WebView, int)
                         */
						@Override
						public void onProgressChanged(WebView view, int newProgress) {
							// TODO Auto-generated method stub
							super.onProgressChanged(view, newProgress);
							if (newProgress == 100) {
								progressBar.setVisibility(View.GONE);
							}
						}

					});
					pscWebview.setWebViewClient(new WebViewClient() {
						@Override
						public boolean shouldOverrideUrlLoading(WebView view, String url) {

							return false;

						}

						@Override
						public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
							super.onReceivedError(view, errorCode, description, failingUrl);
							Toast.makeText(getApplicationContext(), "网络连接失败 ,请连接网络。", Toast.LENGTH_SHORT).show();
						}
					});
					pscWebview.postUrl(url, postdata.getBytes());
				}
		}
	}

	public boolean post(String address) throws Exception {
		String params ="Request="+mShipImo+"&Selector=IMO_No";
		byte[] data = params.getBytes();

		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		//这是请求方式为POST
		conn.setRequestMethod("POST");
		//设置post请求必要的请求头
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 请求头, 必须设置
		conn.setRequestProperty("Content-Length", data.length + ""); // 注意是字节长度, 不是字符长度

		conn.setDoOutput(true); // 准备写出
		conn.getOutputStream().write(data); // 写出数据

		return conn.getResponseCode() == 200;
	}

}
