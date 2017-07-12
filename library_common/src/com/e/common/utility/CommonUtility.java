/**    
 * @{#} Utility.java Create on 2013-10-18 上午11:16:24    
 *        
 * @author Evan
 *
 * @email evan0502@qq.com
 *
 * @version 1.0    
 */
package com.e.common.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e.common.constant.Constants;
import com.e.common.manager.net.NetManager;
import com.e.common.widget.DialogExt;
import com.e.library_common.BuildConfig;
import com.e.library_common.R;

/**
 * @{# Utility.java Create on 2014年11月30日 上午10:47:39
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class CommonUtility {

	public static final String PLATFORM = "Android";

	/**
	 * 普通操作工具类
	 */
	public static final class Utility {
		/**
		 * method desc：判断参数值是否为空 null，空字符串，或者全部空格字符串或者"null"字符串都视为空
		 *
		 * @param o
		 * @return
		 */
		public static boolean isNull(Object o) {
			try {
				return null == o || "".equals(o.toString().replaceAll(" ", ""))
						|| "null".equals(o.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;
		}

		public static void putAll(ArrayList<Object> list, JSONArray src) {
			try {
				Object object = null;
				for (int i = 0; i < src.length(); i++) {
					object = src.get(i);
					list.add(object);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		public static HashMap<String, Object> getPostMap() {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(NetManager.KEY_REQUEST_TYPE,
                    NetManager.REQUEST_TYPE_POST);
            return params;
        }

        public static HashMap<String, Object> getGetMap() {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(NetManager.KEY_REQUEST_TYPE, NetManager.REQUEST_TYPE_GET);
            return params;
        }

        public static HashMap<String, Object> getPutMap() {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(NetManager.KEY_REQUEST_TYPE, NetManager.REQUEST_TYPE_PUT);
            return params;
        }

        public static HashMap<String, Object> getDeleteMap() {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(NetManager.KEY_REQUEST_TYPE,
                    NetManager.REQUEST_TYPE_DELETE);
            return params;
        }

        public static HashMap<String, Object> getCacheGetMap(int maxStale) {
            HashMap<String, Object> params = getGetMap();
            params.put(NetManager.KEY_REQUEST_CACHE, maxStale);
            return params;
        }

        public static HashMap<String, Object> getNoCacheGetMap() {
            return getCacheGetMap(0);
        }

		/**
		 * 验证邮箱地址是否正确
		 * 
		 * @param email
		 * @return
		 */
		public static boolean checkEmail(String email) {
			boolean flag = false;
			try {
				String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
				Pattern regex = Pattern.compile(check);
				Matcher matcher = regex.matcher(email);
				flag = matcher.matches();
			} catch (Exception e) {
				flag = false;
			}

			return flag;
		}

		/**
		 * 验证手机号码
		 * 
		 * @param mobiles
		 * @return [0-9]{5,9}
		 */
		public static boolean isMobileNO(String mobiles) {
			boolean flag = false;
			try {
				Pattern p = Pattern
						.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
				Matcher m = p.matcher(mobiles);
				flag = m.matches();
			} catch (Exception e) {
				flag = false;
			}
			return flag;
		}

		/**
		 * method desc：验证是否为数字
		 *
		 * @param number
		 * @return
		 */
		public static boolean isNum(String number) {
			boolean flag = false;
			try {
				Pattern p = Pattern.compile("^[0-9]{5}$");
				Matcher m = p.matcher(number);
				flag = m.matches();
			} catch (Exception e) {
				flag = false;
			}
			return flag;
		}

		/**
		 * method desc： 判断密码是否有至少一位数字和字母
		 * 
		 * @param password
		 * @return
		 */
		public static boolean isAlphanumerics(String password) {
			boolean flag = false;
			try {
				Pattern p = Pattern
						.compile(".*[A-Za-z].*[0-9]|.*[0-9].*[A-Za-z]");
				Matcher m = p.matcher(password);
				flag = m.matches();
			} catch (Exception e) {
				flag = false;
			}
			return flag;
		}

		/**
		 * method desc：产生一个唯一标识，跟当前实例绑定
		 *
		 * @param object
		 * @param tag
		 * @return
		 */
		public static int getRequestTag(Object object, int tag) {
			return object.hashCode() + tag;
		}
	}

	public static class DebugLog {

		private static final String TAG_LOG = "log";

		public static void log(Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG) {
				Log.v(TAG_LOG, obj);
			}
		}

		public static void v(final String tag, Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG) {
				Log.v(tag, obj);
			}
		}

		public static void d(String tag, Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG) {
				Log.d(tag, obj);
			}
		}

		public static void i(final String tag, Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG)
				Log.i(tag, obj);
		}

		public static void w(String tag, Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG) {
				Log.w(tag, obj);
			}
		}

		public static void e(final String tag, Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG)
				Log.e(tag, obj);
		}

		public static void wtf(String tag, Object object) {
			String obj = Utility.isNull(object) ? "null" : object.toString();
			if (BuildConfig.DEBUG) {
				Log.wtf(tag, obj);
			}
		}
	}

	/**
	 * 跟用户界面相关的操作工具类
	 */
	public static final class UIUtility {
		/**
		 * method desc：将dipValue换算成px
		 *
		 * @param context
		 * @param dipValue
		 * @return
		 */
		public static int dip2px(Context context, float dipValue) {
			float m = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * m + 0.5f);
		}

		/**
		 * method desc：将pxValue换算成dip
		 *
		 * @param context
		 * @param pxValue
		 * @return
		 */
		public static int px2dip(Context context, float pxValue) {
			float m = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / m + 0.5f);
		}

		/**
		 * method desc：将spValue换算成px
		 *
		 * @param context
		 * @param spValue
		 * @return
		 */
		public static int sp2px(Context context, float spValue) {
			final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
			return (int) (spValue * fontScale + 0.5f);
		}

		/**
		 * method desc：显示提示
		 *
		 * @param context
		 * @param str
		 */
		public static void toast(Context context, String str) {
			try {
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		public static void toast(Context context, int strRes) {
			try {
				Toast.makeText(context, context.getString(strRes),
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		public static void toastLong(Context context, String str) {
			try {
				Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		public static void toastLong(Context context, int strRes) {
			try {
				Toast.makeText(context, context.getString(strRes),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		/**
		 * method desc：获取对应应用的service是否运行
		 *
		 * @param context
		 * @param serviceName
		 * @return
		 */
		public static boolean serviceIsRunning(Context context,
				String serviceName) {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Activity.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
					.getRunningServices(100);
			for (RunningServiceInfo service : mServiceList) {
				if (serviceName.equals(service.service.getClassName())) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 获取版本名称
		 * 
		 * @return 当前应用的版本名称
		 */
		public static String getVersionName(Context context) {
			try {
				PackageManager manager = context.getPackageManager();
				PackageInfo info = manager.getPackageInfo(
						context.getPackageName(), 0);
				String version = info.versionName;
				return version;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

		/**
		 * 获取版本号
		 * 
		 * @return 当前应用的版本号
		 */
		public static int getVersionCode(Context context) {
			try {
				PackageManager manager = context.getPackageManager();
				PackageInfo info = manager.getPackageInfo(
						context.getPackageName(), 0);
				int version = info.versionCode;
				return version;
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}

		/**
		 * method desc：获取指定路径图片方向，并返回竖向矩阵
		 *
		 * @param filePath
		 * @return
		 */
		public static Matrix convertMartix(String filePath) {
			Matrix matrix = new Matrix();
			try {
				ExifInterface exifInterface = new ExifInterface(filePath);
				if (Utility.isNull(exifInterface)) {
					return matrix;
				}
				int orientation = exifInterface.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_UNDEFINED);
				switch (orientation) {
				case ExifInterface.ORIENTATION_UNDEFINED:
					break;
				case ExifInterface.ORIENTATION_NORMAL:
					break;
				case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
					matrix.postScale(-1f, 1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					matrix.postRotate(180f);
					break;
				case ExifInterface.ORIENTATION_FLIP_VERTICAL:
					matrix.postScale(1f, -1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					matrix.postRotate(90f);
					break;
				case ExifInterface.ORIENTATION_TRANSVERSE:
					matrix.postRotate(-90f);
					matrix.postScale(1f, -1f);
					break;
				case ExifInterface.ORIENTATION_TRANSPOSE:
					matrix.postRotate(90f);
					matrix.postScale(1f, -1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					matrix.postRotate(-90f);
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return matrix;
		}

		public static void addView(Activity activity, View view, int anim) {
			ViewGroup rootView = (ViewGroup) activity.getWindow()
					.getDecorView();
			rootView.addView(view);
			if (anim > 0) {
				view.startAnimation(AnimationUtils
						.loadAnimation(activity, anim));
			}
		}

		public static void removeView(final Activity activity, final View view,
				int anim) {
			if (!Utility.isNull(view)) {
				Animation animation = AnimationUtils.loadAnimation(activity,
						anim);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						ViewGroup rootView = (ViewGroup) activity.getWindow()
								.getDecorView();
						rootView.removeView(view);
					}
				});
				view.startAnimation(animation);

			}
		}

		public static void removeView(Activity activity, View view) {
			if (!Utility.isNull(view)) {
				ViewGroup rootView = (ViewGroup) activity.getWindow()
						.getDecorView();
				rootView.removeView(view);
			}
		}

		/**
		 * method desc：隐藏虚拟键盘
		 *
		 * @param activity
		 * @param view
		 */
		public static void hideKeyboard(Context context, View view) {
			InputMethodManager imm = (InputMethodManager) context
					.getApplicationContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
			// 显示或者隐藏输入法
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		/**
		 * method desc：在对应的view上显示虚拟键盘
		 *
		 * @param view
		 */
		public static void showKeyboard(final View view) {
			Timer timer = new Timer();

			timer.schedule(new TimerTask() {
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) view
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(view, 0);
				}
			}, 150);

		}

		/**
		 * method desc：屏幕截图，只能截当前应用
		 *
		 * @param activity
		 * @param v
		 *            为空则获取activity根目录
		 * @return
		 */
		public static Bitmap takeScreenShot(Activity activity, View v) {
			View view = v;
			if (Utility.isNull(v)) {
				view = activity.getWindow().getDecorView();
			}

			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap b1 = view.getDrawingCache();

			Bitmap b = Bitmap.createBitmap(b1, 0, (int) view.getY(),
					view.getWidth(), view.getHeight());
			view.destroyDrawingCache();
			return b;
		}

		/**
		 * Get the screen height.
		 * 
		 * @param context
		 * @return the screen height
		 */
		@SuppressLint("NewApi")
		public static int getScreenHeight(Context context) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = context.getResources().getDisplayMetrics();
			return dm.heightPixels;
		}

		/**
		 * Get the screen width.
		 * 
		 * @param context
		 * @return the screen width
		 */
		@SuppressLint("NewApi")
		public static int getScreenWidth(Context context) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = context.getResources().getDisplayMetrics();
			return dm.widthPixels;
		}

		public static int getStatusBarHeight(Activity activity) {
			Class<?> c = null;
			Object obj = null;
			Field field = null;
			int x = 0, sbar = 0;
			try {
				c = Class.forName("com.android.internal.R$dimen");
				obj = c.newInstance();
				field = c.getField("status_bar_height");
				x = Integer.parseInt(field.get(obj).toString());
				sbar = activity.getResources().getDimensionPixelSize(x);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return sbar;
		}

		public static String getClientName(Context context) {
			String client = context.getPackageName().substring(
					context.getPackageName().lastIndexOf(".") + 1);
			return client;
		}

		/**
		 * method desc：判断当前系统语言
		 *
		 * @param context
		 * @return
		 */
		public static boolean isZh(Context context) {
			Locale locale = context.getResources().getConfiguration().locale;
			String language = locale.getLanguage();
			if (language.endsWith("zh"))
				return true;
			else
				return false;
		}

		/**
		 * method desc：卸载广播
		 * 
		 * @param activity
		 * @param receiver
		 */
		public static void unRegisteReciver(Context context,
				BroadcastReceiver receiver) {
			try {
				if (receiver != null) {
					context.unregisterReceiver(receiver);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			receiver = null;
		}

		/**
		 * method desc：获取TextView 内容
		 *
		 * @param textView
		 * @return
		 */
		public static String getText(TextView textView) {
			return textView.getText().toString();
		}

		/**
		 * method desc：获取EditText内容
		 *
		 * @param editText
		 * @return
		 */
		public static String getText(EditText editText) {
			return editText.getText().toString();
		}

		/**
		 * method desc： 判断是否有sdcard
		 * 
		 * @return
		 */
		public static boolean isExistSDCard() {
			if (Environment.getExternalStorageDirectory().exists()) {
				return true;
			}
			return false;
		}

		public static void setViewHolderTag(View view, Object object) {
			view.setTag(R.id.tag_obj_viewholder, object);
		}

		public static Object getViewHolderTag(View view) {
			return view.getTag(R.id.tag_obj_viewholder);
		}
	}

	public static class SharedPreferencesUtility {

		private static SharedPreferencesUtility mSharedPreferencesUtility = null;
		private static SharedPreferences mSharedPreferences = null;

		public static SharedPreferencesUtility getSharedPreferences(
				Context context) {
			if (Utility.isNull(mSharedPreferencesUtility)) {
				mSharedPreferencesUtility = new SharedPreferencesUtility();
				mSharedPreferences = context.getSharedPreferences(
						Constants.SHARE_PREF, 0);
			}
			return mSharedPreferencesUtility;
		}

		private SharedPreferencesUtility() {
		}

		public SharedPreferences getSharedPreference() {
			return mSharedPreferences;
		}

		public void putString(String key, Object value) {
			if (value instanceof String) {
				mSharedPreferences.edit().putString(key, value.toString())
						.commit();
			} else if (value instanceof Integer) {
				mSharedPreferences.edit()
						.putInt(key, Integer.parseInt(value.toString()))
						.commit();
			} else if (value instanceof Long) {
				mSharedPreferences.edit()
						.putLong(key, Long.parseLong(value.toString()))
						.commit();
			} else if (value instanceof Boolean) {
				mSharedPreferences
						.edit()
						.putBoolean(key, Boolean.parseBoolean(value.toString()))
						.commit();
			}
		}

		public String getString(String key, String defauleValue) {
			return mSharedPreferences.getString(key, defauleValue);
		}
	}

	/**
	 * 身份证验证
	 */
	public static final class IDCARD {
		static int[] WI = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

		static String[] VALIDATENUM = { "1", "0", "X", "9", "8", "7", "6", "5",
				"4", "3", "2" };

		public static boolean cardValidate(String cardNum) {
			if (!Utility.isNull(cardNum)) {
				if (cardNum.length() != 18) {
					return false;
				}
				int count = 0;
				for (int i = 0; i < cardNum.length() - 1; i++) {
					count += Integer.parseInt(cardNum.substring(i, i + 1))
							* WI[i];
				}
				int mod = count % 11;
				String validateNum = VALIDATENUM[mod];
				if (!validateNum.equals(cardNum.substring(17))) {
					return false;
				}
			}
			return true;
		}
	}

	public static final class FileUtility {

		public static String sd_card = Environment
				.getExternalStorageDirectory().getAbsolutePath();

		private static String TEMP_IMAGE_DIR_PATH;

		public static void setTempImageDir(String path) {
			TEMP_IMAGE_DIR_PATH = path;
		}

		/**
		 * method desc：获取一个随机的完整的临时图片路径
		 *
		 * @return
		 */
		public static String getUUIDImgPath() {
			createDir(TEMP_IMAGE_DIR_PATH);
			return sd_card + TEMP_IMAGE_DIR_PATH + UUID.randomUUID().toString()
					+ ".png";// ".png.cache";
		}

		/**
		 * method desc：根据指定图片名称创建一个同名的文件
		 *
		 * @return
		 */
		public static String getImgPath(String imagePath) {
			return getImgPath(imagePath, false);
		}

		/**
		 * method desc：根据指定图片名称创建一个同名的文件
		 *
		 * @return
		 */
		public static String getImgPath(String fileName, boolean append) {
			createDir(TEMP_IMAGE_DIR_PATH);
			StringBuilder builder = new StringBuilder();
			if (append) {
				builder.append(sd_card).append(TEMP_IMAGE_DIR_PATH)
						.append(fileName);
			} else {
				int lastSlashIndex = fileName.lastIndexOf("/");
				String imageName = fileName.substring(lastSlashIndex + 1);
				builder.append(sd_card).append(TEMP_IMAGE_DIR_PATH)
						.append(imageName);
			}
			return builder.toString();
		}

		/**
		 * method desc：创建指定的路径的文件夹
		 *
		 * @param path
		 */
		private static void createDir(String path) {
			if (!Utility.isNull(path)) {
				File file = new File(sd_card + path);
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		}
	}

	/**
	 * 设备信息工具类
	 */
	public static final class DeviceInfoUtility {
		/**
		 * method desc：获取设备网卡地址
		 *
		 * @param context
		 * @return
		 */
		public static String getMac(Context context) {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
		}

		/**
		 * method desc：获取设备ip地址
		 *
		 * @return
		 */
		public static String getLocalHostIp() {
			try {
				Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces();
				// 遍历所用的网络接口
				while (en.hasMoreElements()) {
					NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
					Enumeration<InetAddress> inet = nif.getInetAddresses();
					// 遍历每一个接口绑定的所有ip
					while (inet.hasMoreElements()) {
						InetAddress ip = inet.nextElement();
						if (!ip.isLoopbackAddress()
								&& InetAddressUtils.isIPv4Address(ip
										.getHostAddress())) {
							return ip.getHostAddress();
						}
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	/**
	 * 设备震动工具类
	 */
	public static final class DeviceControllerUtility {
		public static void Vibrate(final Context activity, long milliseconds) {
			Vibrator vib = (Vibrator) activity
					.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(milliseconds);
		}

		public static void Vibrate(final Context activity, long[] pattern,
				boolean isRepeat) {
			Vibrator vib = (Vibrator) activity
					.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(pattern, isRepeat ? 1 : -1);
		}
	}

	/**
	 * 系统操作工具类
	 */
	public static final class SystemOperateUtility {

		/**
		 * method desc: 发送短信
		 * 
		 * @param smsBody
		 */
		public static void sendSMS(Context context, String tel, String smsBody) {
			Uri smsToUri = Uri.parse("smsto:" + tel);
			Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
			intent.putExtra("sms_body", smsBody);
			context.startActivity(intent);
		}

		/**
		 * method desc：将内容复制到剪贴板上
		 *
		 * @param activity
		 * @param str
		 * @param tip
		 */
		@SuppressWarnings("deprecation")
		public static void copy2Clipboard(Activity activity, String str,
				String tip) {
			ClipboardManager clipboard = (ClipboardManager) activity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(str);
			UIUtility
					.toast(activity, Utility.isNull(tip) ? "内容已复制到剪贴板中。" : tip);
		}

		/**
		 * method desc：将内容复制到剪贴板上
		 * 
		 * @see {@link #copy2Clipboard(Activity activity, String str, String tip)}
		 *
		 * @param activity
		 * @param str
		 */
		public static void copy2Clipboard(Activity activity, String str) {
			copy2Clipboard(activity, str, null);
		}
	}

	public static final class BitmapOperateUtility {
		/**
		 * 将bitmap放入缓存中 method desc：
		 * 
		 * @param bitmap
		 * @param bitmaps
		 */
		public static void addBitmap(Bitmap bitmap, ArrayList<Bitmap> bitmaps) {
			if (!Utility.isNull(bitmaps)) {
				bitmaps.add(bitmap);
			} else {
				bitmaps = new ArrayList<Bitmap>();
				bitmaps.add(bitmap);
			}
		}

		/**
		 * 销毁指定集合的bitmap，释放内存 method desc：
		 * 
		 * @param bitmaps
		 */
		public static void destoryBitmaps(ArrayList<Bitmap> bitmaps) {
			if (!Utility.isNull(bitmaps)) {
				for (Bitmap bitmap : bitmaps) {
					if (!Utility.isNull(bitmap) && !bitmap.isRecycled()) {
						bitmap.recycle();
					}
					bitmap = null;
				}
				bitmaps.clear();
				bitmaps = null;
			}
		}

		/**
		 * 销毁bitmap，释放内存 method desc：
		 * 
		 * @param bitmap
		 */
		public static void destoryBitmap(Bitmap bitmap) {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}

		/**
		 * method desc：将图片按照指定角度旋转
		 *
		 * @param bitmap
		 * @param degree
		 * @return
		 */
		public static Bitmap rotate(Bitmap bitmap, int degree) {
			if (bitmap.getHeight() < bitmap.getWidth()) {
				Matrix matrix = new Matrix();
				matrix.postRotate(degree);

				Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
				destoryBitmap(bitmap);
				return b;
			} else {
				return bitmap;
			}
		}
	}

	/**
	 * 图片操作工具类
	 */
	public static final class ImageUtility {

		private static final String TAG = "library_image_utility";

		public static int yasuo = 480 * 800;

		/**
		 * Stores an image on the storage
		 * 
		 * @param image
		 *            the image to store.
		 * @param pictureFile
		 *            the file in which it must be stored
		 */
		public static void storeImage(File pictureFile, Bitmap bitmap) {
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(pictureFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				try {
					fos.close();
					fos = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!Utility.isNull(bitmap) && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		}

		/**
		 * method desc：
		 * 
		 * @param path
		 *            absolute file path
		 * @param bitmap
		 */
		public static void storeImage(String path, Bitmap bitmap) {
			// String name = MyHash.mixHashStr(AdName);
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			storeImage(file, bitmap);
		}

		public static byte[] bmpToByteArray(Bitmap bitmap) {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				return baos.toByteArray();
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				bitmap.recycle();
			}
			return null;
		}

		/**
		 * 高斯模糊
		 * 
		 * @param bmp
		 * @param delta
		 *            值越小图片会越亮，越大则越暗
		 * @return
		 */
		public static Bitmap blurBitmap(Bitmap sentBitmap, int radius) {

			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

			if (radius < 1) {
				return (null);
			}

			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			int[] pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);

			int wm = w - 1;
			int hm = h - 1;
			int wh = w * h;
			int div = radius + radius + 1;

			int r[] = new int[wh];
			int g[] = new int[wh];
			int b[] = new int[wh];
			int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
			int vmin[] = new int[Math.max(w, h)];

			int divsum = (div + 1) >> 1;
			divsum *= divsum;
			int dv[] = new int[256 * divsum];
			for (i = 0; i < 256 * divsum; i++) {
				dv[i] = (i / divsum);
			}

			yw = yi = 0;

			int[][] stack = new int[div][3];
			int stackpointer;
			int stackstart;
			int[] sir;
			int rbs;
			int r1 = radius + 1;
			int routsum, goutsum, boutsum;
			int rinsum, ginsum, binsum;

			for (y = 0; y < h; y++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				for (i = -radius; i <= radius; i++) {
					p = pix[yi + Math.min(wm, Math.max(i, 0))];
					sir = stack[i + radius];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					rbs = r1 - Math.abs(i);
					rsum += sir[0] * rbs;
					gsum += sir[1] * rbs;
					bsum += sir[2] * rbs;
					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
				}
				stackpointer = radius;

				for (x = 0; x < w; x++) {

					r[yi] = dv[rsum];
					g[yi] = dv[gsum];
					b[yi] = dv[bsum];

					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;

					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];

					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];

					if (y == 0) {
						vmin[x] = Math.min(x + radius + 1, wm);
					}
					p = pix[yw + vmin[x]];

					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);

					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;

					stackpointer = (stackpointer + 1) % div;
					sir = stack[(stackpointer) % div];

					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];

					yi++;
				}
				yw += w;
			}
			for (x = 0; x < w; x++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				yp = -radius * w;
				for (i = -radius; i <= radius; i++) {
					yi = Math.max(0, yp) + x;

					sir = stack[i + radius];

					sir[0] = r[yi];
					sir[1] = g[yi];
					sir[2] = b[yi];

					rbs = r1 - Math.abs(i);

					rsum += r[yi] * rbs;
					gsum += g[yi] * rbs;
					bsum += b[yi] * rbs;

					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}

					if (i < hm) {
						yp += w;
					}
				}
				yi = x;
				stackpointer = radius;
				for (y = 0; y < h; y++) {
					// Preserve alpha channel: ( 0xff000000 & pix[yi] )
					pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
							| (dv[gsum] << 8) | dv[bsum];

					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;

					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];

					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];

					if (x == 0) {
						vmin[y] = Math.min(y + r1, hm) * w;
					}
					p = x + vmin[y];

					sir[0] = r[p];
					sir[1] = g[p];
					sir[2] = b[p];

					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;

					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer];

					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];

					yi += w;
				}
			}
			bitmap.setPixels(pix, 0, w, 0, 0, w, h);
			return (bitmap);
		}

		/**
		 * method desc：将图片地址format，适用于imageloader
		 *
		 * @param path
		 * @return
		 */
		public static String formatUrl(String path) {
			if (!path.startsWith("http")) {
				path = "file://" + path;
			}
			return path;
		}
	}

	/**
	 * 对话框工具类
	 */
	public static final class DialogUtility {

		/**
		 * method desc：确认对话框，显示确认和取消按钮，可以给确认按钮添加点击事件
		 *
		 * @see {@link #confirm(Context, String)}
		 * @param context
		 * @param strRes
		 * @return
		 */
		public static DialogExt confirm(Context context, int strRes) {
			DialogExt dialog = confirm(context, context.getString(strRes));
			return dialog;
		}

		/**
		 * method desc：确认对话框，显示确认和取消按钮，可以给确认按钮添加点击事件
		 *
		 * @see {@link #confirm(Context, int)}
		 * @param context
		 * @param strRes
		 * @return
		 */
		public static DialogExt confirm(Context context, String str) {
			DialogExt dialog = DialogExt.createDialog(context);
			dialog.setMessage(str);
			dialog.setTitle(R.string.s_dialog_title_tip);
			dialog.show();
			return dialog;
		}

		/**
		 * method desc：提示对话框，显示取消按钮
		 * 
		 * @see {@link #tip(Context, String)}
		 *
		 * @param context
		 * @param strRes
		 * @return
		 */
		public static DialogExt tip(Context context, int strRes) {
			return tip(context, context.getString(strRes));
		}

		/**
		 * method desc：提示对话框，显示取消按钮
		 *
		 * @param context
		 * @param str
		 * @return
		 */
		public static DialogExt tip(Context context, String str) {
			DialogExt dialog = DialogExt.createDialog(context);
			dialog.setSingleBtn(DialogExt.OK);
			dialog.setMessage(str);
			dialog.setTitle(R.string.s_dialog_title_tip);
			dialog.show();
			return dialog;
		}

		/**
		 * method desc：自定义view的对话框
		 *
		 * @param context
		 * @param view
		 * @return
		 */
		public static DialogExt customerDialog(Context context, View view) {
			DialogExt dialog = DialogExt.createDialog(context);
			dialog.setView(view);
			dialog.show();
			return dialog;
		}

		/**
		 * method desc：自定义view的对话框
		 *
		 * @param context
		 * @param layoutRes
		 * @return
		 */
		public static DialogExt customerDialog(Context context, int layoutRes) {
			DialogExt dialog = DialogExt.createDialog(context);
			dialog.setView(LayoutInflater.from(context)
					.inflate(layoutRes, null));
			dialog.show();
			return dialog;
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static final class CalendarUtility {
		/**
		 * method desc： 获取当前时间
		 * 
		 * @return 2012-01-01
		 */
		public static String getCurrentDate() {
			return new Timestamp(System.currentTimeMillis()).toString()
					.substring(0, 10);
		}

		/**
		 * method desc：获取当前时间，并根据起始索引和结束索引截取字符串
		 * 
		 * @return 2012-01-01
		 */
		public static String getCurrentDate(int start, int end) {
			return new Timestamp(System.currentTimeMillis()).toString()
					.substring(start, end);
		}

		/**
		 * method desc：计算两个日期相差的天数
		 * 
		 * @param startTime
		 * @param endTime
		 * @param format
		 */
		@SuppressLint("SimpleDateFormat")
		public static long dateDiff(String startTime, String endTime,
				String format) {
			// 按照传入的格式生成一个simpledateformate对象
			SimpleDateFormat sd = new SimpleDateFormat(format);
			long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
			long diff;
			try {
				// 获得两个时间的毫秒时间差异
				diff = sd.parse(endTime).getTime()
						- sd.parse(startTime).getTime();
				long day = diff / nd;// 计算差多少天
				// 输出结果
				return day;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
		}

		/**
		 * method desc：获取当前格式化的时间 ps: "yyyy-M-d HH:mm"
		 * 
		 * @return 2012-01-01 10:10
		 */
		public static String getCurrentDateFormat(String format) {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat(format,
					Locale.getDefault());
			return df.format(date);
		}

		/**
		 * method desc： 根据年月获取当月最大天数
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		public static int getMaxDay(int year, int month) {
			Calendar c = Calendar.getInstance();
			c.set(year, month, 1);
			c.add(Calendar.DAY_OF_YEAR, -1);
			return c.get(Calendar.DAY_OF_MONTH);
		}

		/**
		 * method desc: 根据时间返回
		 * 刚刚，1分钟前-59分钟前，1小时前-23小时前，昨天，前天，4天前-90天前，3个月前，04-20。 method desc：
		 * 
		 * @param date
		 * @return
		 */
		public static String getTimeDiff(String date, int start, int end) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault());
			Date d = null;
			try {
				d = format.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			long diff = 0;
			Date dnow = cal.getTime();
			String str = "";
			diff = dnow.getTime() - d.getTime();

			if (diff > 2678400000L) {
				str = date.substring(start, end);
			} else if (diff > 2592000000L) {// 30 * 24 * 60 * 60 *
											// 1000=2592000000
											// 毫秒
				str = "1个月前";
			} else if (diff > 1814400000) {// 21 * 24 * 60 * 60 *
											// 1000=1814400000 毫秒
				str = "3周前";
			} else if (diff > 1209600000) {// 14 * 24 * 60 * 60 *
											// 1000=1209600000 毫秒
				str = "2周前";
			} else if (diff > 604800000) {// 7 * 24 * 60 * 60 * 1000=604800000
											// 毫秒
				str = "1周前";
			} else if (diff > 86400000) { // 24 * 60 * 60 * 1000=86400000 毫秒
				int day = (int) Math.floor(diff / 86400000f);
				if (day == 1) {
					str = "昨天";
				} else if (day == 2) {
					str = "前天";
				} else {
					str = day + "天前";
				}
			} else if (diff > 3600000) {// 60 * 60 * 1000=18000000 毫秒
				str = (int) Math.floor(diff / 3600000f) + "小时前";
			} else if (diff > 60000) {// 1 * 60 * 1000=60000 毫秒
				str = (int) Math.floor(diff / 60000) + "分钟前";
			} else {
				str = "刚刚";
			}
			return str;
		}

		public static String getAge(int year, int month, int day) {
			String birthday = year + "-" + month + "-" + day;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			try {
				date = sdf.parse(birthday);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 使用calendar进行计算
			Calendar calendar = Calendar.getInstance();
			// 获取当前时间毫秒值
			long now = (new Date()).getTime();
			long Birthdate = date.getTime();
			long time = now - Birthdate;
			int count = 0;
			// 时间换算
			long days = time / 1000 / 60 / 60 / 24;
			// 判断闰年
			for (int i = calendar.get(Calendar.YEAR); i >= year; i--) {
				if ((i % 4 == 0 && !(i % 100 == 0)) || (i % 400 == 0)) {
					count++;
				}
			}
			// 加入闰年因素进行整理换算
			int age = ((int) days - count) / 365;
			return age + "";
		}

		public static String getBirthdayByUnixTime(long unixTimestamp) {
			long timestamp = unixTimestamp * 1000;
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new java.util.Date(timestamp));
		}
	}

	/**
	 *
	 * @{# CommonUtility.java Create on 2015年1月17日 下午5:47:08
	 *
	 * @author <a href="mailto:evan0502@qq.com">Evan</a>
	 * @version 1.0
	 * @description json 取值的简单封装，主要是封装每个key的异常处理
	 *
	 */
	public static final class JSONObjectUtility {
		public static String optString(JSONObject object, String key) {
			try {
				String text = object.getString(key);
				return Utility.isNull(text) ? null : text;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

}
