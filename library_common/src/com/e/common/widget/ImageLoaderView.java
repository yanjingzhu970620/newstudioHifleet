/**    
 * @{#} ImageLoaderView.java Create on 2014年11月13日 上午10:18:44    
 *    
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 * @description
 *	
 */
package com.e.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.e.common.activity.ImageShowActivity;
import com.e.common.constant.Constants.IDENTITY;
import com.e.common.manager.init.InitManager;
import com.e.common.manager.net.INet;
import com.e.common.utility.CommonUtility.UIUtility;
import com.e.common.utility.CommonUtility.Utility;
import com.e.library_common.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoaderView extends ImageView {

	/**
	 * 是否显示为圆角图片
	 */
	private boolean mIsRoundCorner;

	/**
	 * 是否显示为圆形图片
	 */
	private boolean mIsRound;

	private float mImageLoaderWidthHeightRate;

	// 是否点击跳转到大图预览
	private boolean mImageLoaderClick2preview;

	private String mUrl;

	private Context mContext;

	/**
	 * @return the mIsRoundCorner
	 */
	public boolean ismIsRoundCorner() {
		return mIsRoundCorner;
	}

	/**
	 * @param mIsRoundCorner
	 *            the mIsRoundCorner to set
	 */
	public void setmIsRoundCorner(boolean mIsRoundCorner) {
		this.mIsRoundCorner = mIsRoundCorner;
	}

	/**
	 * @return the mIsRound
	 */
	public boolean ismIsRound() {
		return mIsRound;
	}

	/**
	 * @param mIsRound
	 *            the mIsRound to set
	 */
	public void setmIsRound(boolean mIsRound) {
		this.mIsRound = mIsRound;
	}

	/**
	 * @return the mImageLoaderWidthHeightRate
	 */
	public float getmImageLoaderWidthHeightRate() {
		return mImageLoaderWidthHeightRate;
	}

	/**
	 * @param mImageLoaderWidthHeightRate
	 *            the mImageLoaderWidthHeightRate to set
	 */
	public void setmImageLoaderWidthHeightRate(float mImageLoaderWidthHeightRate) {
		this.mImageLoaderWidthHeightRate = mImageLoaderWidthHeightRate;
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ImageLoaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ImageLoaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public ImageLoaderView(Context context) {
		super(context);
		init(context, null);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mImageLoaderWidthHeightRate > 0) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			height = (int) (width * mImageLoaderWidthHeightRate);
			setMeasuredDimension(width, height);
		}
	}

	/**
	 * 初始化属性 method desc：
	 * 
	 * @param attrs
	 *            isRound isRoundCorner isRound为true时，isRoundCorner则无效
	 */
	public void init(final Context context, AttributeSet attrs) {
		mContext = context;
		if (!Utility.isNull(attrs)) {
			TypedArray attributes = context.obtainStyledAttributes(attrs,
					R.styleable.ImageLoaderAttr, 0, 0);
			mIsRound = attributes.getBoolean(
					R.styleable.ImageLoaderAttr_imageLoader_round, false);
			mIsRoundCorner = attributes.getBoolean(
					R.styleable.ImageLoaderAttr_imageLoader_roundCorner, false);
			mImageLoaderWidthHeightRate = attributes.getFloat(
					R.styleable.ImageLoaderAttr_imageLoader_width_height_rate,
					0);
			mImageLoaderClick2preview = attributes.getBoolean(
					R.styleable.ImageLoaderAttr_imageLoader_click2preview,
					false);
			attributes.recycle();
		}

		if (mImageLoaderClick2preview) {
			setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, ImageShowActivity.class);
					intent.putExtra(IDENTITY.IDENTITY_FILEPATH, mUrl);
					context.startActivity(intent);
				}
			});
		}
	}

	/**
	 * 根据uri地址加载图片，根据options进行压缩 method desc：
	 * 
	 * @param url
	 *            图片源地址
	 * @param options
	 *            图片压缩对象
	 */
	public void loadImage(String url, DisplayImageOptions options,
			ImageLoadingListener listener) {
		if (!Utility.isNull(url)) {
			mUrl = url;
			if (!mUrl.startsWith("http:")) {
				mUrl = ((INet) ((Activity) mContext).getApplication())
						.getBasePicturePath() + mUrl;
			}
			ImageLoader.getInstance().displayImage(mUrl, this, options,
					listener);
		}
	}

	/**
	 * 根据uri地址加载图片，无压缩 method desc：
	 * 
	 * @param url
	 *            图片源地址
	 */
	public void loadImage(String url, ImageLoadingListener listener) {
		if (!Utility.isNull(url)) {
			mUrl = url;
			if (!mUrl.startsWith("http:")) {
				mUrl = ((INet) ((Activity) mContext).getApplication())
						.getBasePicturePath() + mUrl;
			}
			DisplayImageOptions options = null;
			if (mIsRound) {
				options = InitManager.getInstance().getOptions(100);
			} else if (mIsRoundCorner) {
				options = InitManager.getInstance().getOptions(
						UIUtility.dip2px(mContext, 10));
			} else {
				options = InitManager.getInstance().getOptions(0);
			}
			ImageLoader.getInstance().displayImage(mUrl, this, options,
					listener);
		}
	}

	public void loadImage(String url) {
		if (!Utility.isNull(url)) {
			loadImage(url, null);
		}
	}

}
