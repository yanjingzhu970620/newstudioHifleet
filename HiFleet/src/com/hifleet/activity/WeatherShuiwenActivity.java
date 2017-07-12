package com.hifleet.activity;

import com.hifleet.plus.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @{# WeatherShuiwenActivity.java Create on 2015年7月17日 下午1:20:36
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WeatherShuiwenActivity extends Activity {
	WebView shuiwen;
	TextView textBack,textShipName;
	ProgressBar progressBar;
	private String mShipName, mShipLa, mShipLo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_shuiwen);

		shuiwen = (WebView) findViewById(R.id.web_shuiwen);
		textBack = (TextView) findViewById(R.id.text_back);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		textShipName = (TextView)findViewById(R.id.text_ship_name);

		Bundle bundle = this.getIntent().getExtras();
		mShipName = bundle.getString("shipname");
		mShipLa = bundle.getString("la");
		mShipLo = bundle.getString("lo");

		textShipName.setText(mShipName);

		shuiwen.loadUrl("http://www.hifleet.com:8081/getMarineWeatherMobile/"
				+ mShipLa + "," + mShipLo);
		shuiwen.setWebChromeClient(new WebChromeClient() {
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
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		}
	}
}
