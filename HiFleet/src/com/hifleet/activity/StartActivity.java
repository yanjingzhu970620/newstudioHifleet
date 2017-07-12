package com.hifleet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hifleet.plus.R;

/**
 * @{# StartActivity.java Create on 2015年6月4日 下午1:19:16
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class StartActivity extends HBaseActivity {
	Boolean isStop = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hifleet.activity.HBaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		new Thread(new MyThread()).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (isStop) {
				Intent intent = new Intent(activity, LoginActivity.class);
				startActivity(intent);
				isStop = false;
				finish();
			}
			super.handleMessage(msg);
		}
	};

	public class MyThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isStop) {
				try {
					Thread.sleep(2000);// 线程暂停10秒，单位毫秒
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);// 发送消息
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
