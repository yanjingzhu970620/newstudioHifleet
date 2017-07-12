/**    
 * @{#} LoadingView.java Create on 2014-6-27 下午3:17:59    
 *          
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 */
package com.e.common.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.e.common.utility.CommonUtility.UIUtility;
import com.e.common.utility.CommonUtility.Utility;
import com.e.library_common.R;

@SuppressLint("InflateParams")
public class LoadingView extends View {

	private static View view;
	private static ImageView loadingImage;

	static LoadingView loading;

	private static Activity lastActivity;

	/**
	 * @param context
	 */
	public LoadingView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		view = LayoutInflater.from(context)
				.inflate(R.layout.view_loading, null);
		loadingImage = (ImageView) view.findViewById(R.id.img_loading);
		// final AnimationDrawable ad = (AnimationDrawable) loading
		// .getBackground();
		// ad.start();
	}

	public static void show(Context context) {
		Activity activity = (Activity) context;
		if (Utility.isNull(loading)) {
			loading = new LoadingView(activity);
		}
		if (Utility.isNull(lastActivity)) {
			hide(activity);
		} else {
			hide(lastActivity);
		}
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				activity, R.anim.loading_anim);
		// 使用ImageView显示动画
		loadingImage.startAnimation(hyperspaceJumpAnimation);
		UIUtility.addView(activity, view, -1);
		lastActivity = activity;
	}

	public static void hide(Context context) {
		if (!Utility.isNull(view)) {
			ViewGroup rootView = (ViewGroup) ((Activity) context).getWindow()
					.getDecorView();
			rootView.removeView(view);
		}
	}
}
