package com.hifleet.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e.common.activity.BaseActivity;
import com.e.common.widget.effect.button.EffectColorButton;
import com.hifleet.plus.R;
import com.hifleet.bean.LoginBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.OsmandApplication;
import com.hifleet.utility.XmlParseUtility;

/**
 * @{# RegisterActivity.java Create on 2015年9月16日 上午10:23:33
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class RegisterActivity extends BaseActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.e.common.activity.BaseActivity#onCreate(android.os.Bundle)
	 */

	private EditText mPhoneNumber, mEditTextAuthCode;
	private TextView mSendCodeteTextView, mTimeTextView;
	private RelativeLayout mSendAuthCode, mTime;
	private String mPhoneNumberLength, mFinalPhoneNumber, mPermission="vvip",
			mAuthCode;
	private EffectColorButton mSendOn, mRegister;
	private RadioButton mRadioAntenna, mRadioFleet, mRaidoProfessional,
			mRadioBeidou;

	private OsmandApplication app;

	private TimeCount time;

	private List<LoginBean> mRegisterSendCodeBeans = new ArrayList<LoginBean>();
	private List<LoginBean> mRegisterBeans = new ArrayList<LoginBean>();
	public static String sessionid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		mPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
		mEditTextAuthCode = (EditText) findViewById(R.id.edit_auth_code);
		mSendAuthCode = (RelativeLayout) findViewById(R.id.rl_send_auth_code);
		mSendOn = (EffectColorButton) findViewById(R.id.effectButton_send_on);
		mRegister = (EffectColorButton) findViewById(R.id.effectButton_register);
		mTime = (RelativeLayout) findViewById(R.id.rl_time);
		mTimeTextView = (TextView) findViewById(R.id.text_time);
		mSendCodeteTextView = (TextView) findViewById(R.id.text_send_code);
		mRadioAntenna = (RadioButton) findViewById(R.id.radio_antenna);
		mRadioFleet = (RadioButton) findViewById(R.id.radio_fleet);
		mRaidoProfessional = (RadioButton) findViewById(R.id.radio_professional);
		mRadioBeidou = (RadioButton) findViewById(R.id.radio_beidou);

		mPhoneNumber.addTextChangedListener(watcher);
		mEditTextAuthCode.addTextChangedListener(watcher);

		mRegister.setClickable(false);
		mRegister.setBackgroundColor(0xffe6e6e6);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.effectButton_send_on:
			mFinalPhoneNumber = mPhoneNumber.getText().toString();
			GetcodeThread loginthread = new GetcodeThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				loginthread.executeOnExecutor(Executors.newCachedThreadPool(),
						new String[0]);
			} else {
				loginthread.execute();
			}
			mTime.setVisibility(View.VISIBLE);
			mSendOn.setVisibility(View.GONE);
			time = new TimeCount(60000, 1000);
			time.start();
			break;
		case R.id.effectButton_register:
//			if (mRadioAntenna.isChecked()) {
//				mPermission = "ship";
//			}
//			if (mRadioFleet.isChecked()) {
//				mPermission = "fleet";
//			}
//			if (mRaidoProfessional.isChecked()) {
//				mPermission = "shipping";
//			}
//			if (mRadioBeidou.isChecked()) {
//				mPermission = "vip";
//			}
			mAuthCode = mEditTextAuthCode.getText().toString();
			RegisterThread registerthread = new RegisterThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				registerthread.executeOnExecutor(
						Executors.newCachedThreadPool(), new String[0]);
			} else {
				registerthread.execute();
			}
			break;
		case R.id.effectButton_cancel:
			finish();
			break;
		case R.id.ll_back:
			finish();
			break;
		case R.id.ll_register:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			break;
		}

	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			mPhoneNumberLength = mPhoneNumber.getText().toString();
			if (mPhoneNumberLength.length() == 11) {
				mSendOn.setVisibility(View.VISIBLE);
				mSendAuthCode.setVisibility(View.GONE);
			} else {
				mSendOn.setVisibility(View.GONE);
				mSendAuthCode.setVisibility(View.VISIBLE);
			}

			if (mPhoneNumberLength.length() > 0
					&& mEditTextAuthCode.length() > 0) {
				mRegister.setClickable(true);
				mRegister.setBackgroundColor(0xff70baff);
			} else {
				mRegister.setClickable(false);
				mRegister.setBackgroundColor(0xffe6e6e6);
			}
		}
	};

	private void parseXMLnew(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("result") == 0) {
			mRegisterSendCodeBeans.add(XmlParseUtility.parse(root,
					LoginBean.class));
		}
	}

	class GetcodeThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.GET_NOTE_CODE
						+ mFinalPhoneNumber;
				System.out.println(httpPost);
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (sessionid != null) {

					conn.setRequestProperty("cookie", sessionid);
					// response.encodeURL(sessionid);
				}
				// String cookieval = conn.getHeaderField("set-cookie");
				String cookieval = null;
				Map<String, List<String>> headers = conn.getHeaderFields();
				for (String s : headers.keySet()) {
					List<String> headerslist = (headers.get(s));
					for (String ss : headerslist) {
						System.out.println("value: " + ss);
						if (ss.contains("JSESSIONID")) {
							cookieval = ss;
						}
					}
				}

				if (cookieval != null) {
					sessionid = cookieval.substring(0, cookieval.indexOf(";"));
					// System.out.println("保存sessionid： "+sessionid);
					loginSession.setSessionid(sessionid);
					app.mEditor.putString("sessionid", sessionid);
					app.mEditor.commit();
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				parseXMLnew(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			Toast.makeText(activity, "已发送验证码", Toast.LENGTH_LONG).show();
			for (LoginBean l : mRegisterSendCodeBeans) {
				if(l==null){Toast.makeText(activity, "注册 验证码返回 null", Toast.LENGTH_LONG).show();}else{
				Toast.makeText(activity, l.getMsg(), Toast.LENGTH_LONG).show();
				}
			}
			mRegisterSendCodeBeans.clear();
		}
	}

	private void parseRegisterXML(InputStream inStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();

		if (root.getNodeName().compareTo("result") == 0) {
			mRegisterBeans.add(XmlParseUtility.parse(root, LoginBean.class));
		}
	}

	class RegisterThread extends AsyncTask<String, Void, Void> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String httpPost = app.myPreferences.getString("loginserver",
						null)
						+ IndexConstants.CREATE_MOBILE_USER
						+ mFinalPhoneNumber
						+ "&code="
						+ mAuthCode
						+ "&roleSelect=" + mPermission;
				System.out.println("看看注册URL===" + httpPost);
				URL shipsUrl = new URL(httpPost);
				HttpURLConnection conn = (HttpURLConnection) shipsUrl
						.openConnection();
				if (sessionid != null) {

					conn.setRequestProperty("cookie", sessionid);
					// response.encodeURL(sessionid);
				}
				// String cookieval = conn.getHeaderField("set-cookie");
				String cookieval = null;
				Map<String, List<String>> headers = conn.getHeaderFields();
				for (String s : headers.keySet()) {
					List<String> headerslist = (headers.get(s));
					for (String ss : headerslist) {
						System.out.println("value: " + ss);
						if (ss.contains("JSESSIONID")) {
							cookieval = ss;
						}
					}
				}

				if (cookieval != null) {
					sessionid = cookieval.substring(0, cookieval.indexOf(";"));
					// System.out.println("保存sessionid： "+sessionid);
					loginSession.setSessionid(sessionid);
					app.mEditor.putString("sessionid", sessionid);
					app.mEditor.commit();
				}
				conn.setConnectTimeout(10000);
				InputStream inStream = conn.getInputStream();
				parseRegisterXML(inStream);
				inStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("未能获取网络数据");
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			for (LoginBean l : mRegisterBeans) {
				Toast.makeText(activity, "注册："+l.getMsg(), Toast.LENGTH_LONG).show();
			}
			mRegisterBeans.clear();
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			mSendOn.setText("重新验证");
			mPhoneNumber.setEnabled(true);
			mSendOn.setVisibility(View.VISIBLE);
			mTime.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			mTimeTextView.setText(millisUntilFinished / 1000 + "秒");
			mPhoneNumber.setEnabled(false);
		}
	}

}
