package com.e.common.widget.pullrefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.e.library_common.R;

@SuppressLint("InflateParams")
public class HeaderLoadingLayout extends LoadingLayout {
	
	/** Header的容器 */
	private RelativeLayout mHeaderContainer;
	/** 箭头图片 */
	private ImageView mArrowImageView;
	/** 进度条 */
	private ProgressBar mProgressBar;
	/** 状态提示TextView */
	private TextView mHintTextView;

	private RelativeLayout mHeadLoaderLinearLayout;

	Matrix mHeaderImageMatrix;
	
	private float mRotationPivotX, mRotationPivotY;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public HeaderLoadingLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public HeaderLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            context
	 */
	private void init(Context context) {
		mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
		mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_refresh_header_progressbar);
		mHeadLoaderLinearLayout = (RelativeLayout) findViewById(R.id.ll_prepare_loading);

		// float pivotValue = 0.5f; // SUPPRESS CHECKSTYLE
		// float toDegree = -180f; // SUPPRESS CHECKSTYLE
		// 初始化旋转动画
		// mRotateUpAnim = new RotateAnimation(0.0f, toDegree,
		// Animation.RELATIVE_TO_SELF, pivotValue,
		// Animation.RELATIVE_TO_SELF, pivotValue);
		// mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		// mRotateUpAnim.setFillAfter(true);
		// mRotateDownAnim = new RotateAnimation(toDegree, 0.0f,
		// Animation.RELATIVE_TO_SELF, pivotValue,
		// Animation.RELATIVE_TO_SELF, pivotValue);
		// mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		// mRotateDownAnim.setFillAfter(true);
		mArrowImageView.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mArrowImageView.setImageMatrix(mHeaderImageMatrix);
		
		Drawable imageDrawable = mArrowImageView.getDrawable();
		
		mRotationPivotX = imageDrawable.getIntrinsicWidth() / 2f;
		mRotationPivotY = imageDrawable.getIntrinsicHeight() / 2f;
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		// 如果最后更新的时间的文本是空的话，隐藏前面的标题
		// mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ?
		// View.INVISIBLE : View.VISIBLE);
		// mHeaderTimeView.setText(label);
	}

	@Override
	public int getContentSize() {
		if (null != mHeaderContainer) {
			return mHeaderContainer.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 60);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header_e, null);
		return container;
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mHeadLoaderLinearLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);

		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mArrowImageView.clearAnimation();
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.byb.commonproject.e.pullrefresh.LoadingLayout#onPull(float)
	 */
	@Override
	public void onPull(float scale) {
		// TODO Auto-generated method stub
		mHeaderImageMatrix.setRotate(scale * 90, mRotationPivotX,
				mRotationPivotY);
		mArrowImageView.setImageMatrix(mHeaderImageMatrix);
	}

	@Override
	protected void onPullToRefresh() {
		if (State.RELEASE_TO_REFRESH == getPreState()) {
			mArrowImageView.clearAnimation();
		}

		mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
	}

	@Override
	protected void onReleaseToRefresh() {
		mArrowImageView.clearAnimation();
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
	}

	@Override
	protected void onRefreshing() {
		mArrowImageView.clearAnimation();
		mHeadLoaderLinearLayout.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
	}
}
