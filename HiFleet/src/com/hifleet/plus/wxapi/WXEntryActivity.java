package com.hifleet.plus.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.hifleet.activity.AccessTokenKeeper;
import com.hifleet.map.OsmandApplication;
import com.hifleet.plus.R;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * @{# Test.java Create on 2015年9月25日 下午4:36:05
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler,
		IWeiboHandler.Response {

	/**
	 * 文字
	 */
	public static final int SINA_SHARE_WAY_TEXT = 1;
	/**
	 * 图片
	 */
	public static final int SINA_SHARE_WAY_PIC = 2;
	/**
	 * 链接
	 */
	public static final int SINA_SHARE_WAY_WEBPAGE = 3;
	public static final int SHARE_CLIENT = 1;
	public static final int SHARE_ALL_IN_ONE = 2;
	private IWXAPI api;
	private OsmandApplication app;
	private AuthInfo mAuthInfo;
	private SsoHandler mSsoHandler;
	private Oauth2AccessToken mAccessToken;
	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;
	private int mShareType = SHARE_CLIENT;
	String sharemmsi;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aa_test);

		app=getMyApplication();
		api = app.getwxApi();
		api.handleIntent(getIntent(), this);
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		sharemmsi = bundle.getString("sharemmsi");

//		mAuthInfo = new AuthInfo(this, Constants.APP_KEY,
//				Constants.REDIRECT_URL, null);
//		mSsoHandler = new SsoHandler(WXEntryActivity.this, mAuthInfo);
//
//		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
//		mWeiboShareAPI.registerApp();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_share_weixin:
			share2weixin(0);
			break;
		case R.id.button_share_xinlang:
			Toast.makeText(WXEntryActivity.this, "微博暂不可用，后续加入", Toast.LENGTH_SHORT).show();
//			 mSsoHandler.authorizeClientSso(new AuthListener());
//			 sendMultiMessage(true);
			break;
		case R.id.button_share_weixing_friends:
			share2weixin(1);
			break;
		case R.id.btn_share_close:
			finish();
			break;
		}
	}

	private void share2weixin(int flag) {
//		System.out.println("test 微信分享 share2weixin");
//        Toast.makeText(WXEntryActivity.this, "share2weixin", Toast.LENGTH_SHORT).show();
		if (!api.isWXAppInstalled()) {
			Toast.makeText(WXEntryActivity.this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}

		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.hifleet.com/app.html?isapp=1&&state="+sharemmsi;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "上海迈利船舶科技有限公司";
		msg.description = "主页分享到微信";
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_desk_1);
		msg.setThumbImage(thumb);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag;
		api.sendReq(req);
		finish();
		System.out.println("test 微信分享 sendReq finish");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onReq(com.tencent.mm.sdk
	 * .modelbase.BaseReq)
	 */
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		System.out.println("wx分享 onReq  "+arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onResp(com.tencent.mm.sdk
	 * .modelbase.BaseResp)
	 */
	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub

//		System.out.println("wx分享 onResp "+resp.toString()+"errcode "+resp.errCode);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
//			System.out.println("分享成功"+resp.transaction);
			Toast.makeText(WXEntryActivity.this, "分享成功", Toast.LENGTH_LONG);
			// 分享成功
			String code = ((SendAuth.Resp) resp).code;
			System.out.println("分享成功 code"+code);
			app.wxLogin(code);
			finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			System.out.println("分享取消");
			Toast.makeText(WXEntryActivity.this, "分享取消", Toast.LENGTH_LONG);
			// 分享取消
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			System.out.println("分享拒绝");
			Toast.makeText(WXEntryActivity.this, "分享拒绝", Toast.LENGTH_LONG);
			// 分享拒绝
			finish();
			break;
		}
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
//			System.out.println("!!!!!!!!!授权失败");
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			// 从这里获取用户输入的 电话号码信息
			String phoneNum = mAccessToken.getPhoneNum();
			if (mAccessToken.isSessionValid()) {
				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(WXEntryActivity.this, mAccessToken);
				Toast.makeText(WXEntryActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
//				System.out.println("授权成功");
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = "授权失败";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(WXEntryActivity.this, message, Toast.LENGTH_LONG).show();
			}
			// sendMultiMessage(true);
			System.out.println("授权失败 code"+values.getString("code"));
		}

		@Override
		public void onCancel() {
			// Toast.makeText(Test.this,
			// R.string.weibosdk_demo_toast_auth_canceled,
			// Toast.LENGTH_LONG).show();
			System.out.println("授权失败1111");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(WXEntryActivity.this, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			System.out.println("授权失败222222"+e.getMessage());
		}
	}

	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = "迈利船舶";
		return textObject;
	}

	private void sendMultiMessage(boolean hasText) {

		mSsoHandler.authorizeClientSso(new AuthListener());

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		if (mShareType == SHARE_CLIENT) {
			System.out.println("看看是啥===" + mShareType);
			mWeiboShareAPI.sendRequest(WXEntryActivity.this, request);
		} else if (mShareType == SHARE_ALL_IN_ONE) {
			AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY,
					Constants.REDIRECT_URL, Constants.SCOPE);
			Oauth2AccessToken accessToken = AccessTokenKeeper
					.readAccessToken(getApplicationContext());
			String token = "";
			if (accessToken != null) {
				token = accessToken.getToken();
				System.out.println("认证过了");
			}
			mWeiboShareAPI.sendRequest(this, request, authInfo, token,
					new WeiboAuthListener() {

						@Override
						public void onWeiboException(WeiboException arg0) {
						}

						@Override
						public void onComplete(Bundle bundle) {
							// TODO Auto-generated method stub
							Oauth2AccessToken newToken = Oauth2AccessToken
									.parseAccessToken(bundle);
							AccessTokenKeeper.writeAccessToken(
									getApplicationContext(), newToken);
							Toast.makeText(
									getApplicationContext(),
									"onAuthorizeComplete token = "
											+ newToken.getToken(), 0).show();
						}

						@Override
						public void onCancel() {
						}
					});
		}
	}

	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	/**
	 * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
	 * 
	 * @param baseRequest
	 *            微博请求数据对象
	 * @see {@link IWeiboShareAPI#handleWeiboRequest}
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "取消分享", Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败" + "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	public interface Constants {
		public static final String APP_KEY = "2666307392"; // 应用的APP_KEY
		public static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";// 应用的回调页
		public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
				+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
				+ "follow_app_official_microblog," + "invitation_write";// 应用申请的高级权限
	}

	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}
}
