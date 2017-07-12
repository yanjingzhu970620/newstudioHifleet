/**
 * @{#} ItemView.java Create on 2014-10-9 下午10:04:16    
 *
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0
 */
package com.e.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.library_common.R;

public class ItemLayout extends LinearLayout {

	private Activity mContext;

	ImageView mImageLeft1;
	TextView mTextLeft2, mTextRight1, mTextRight2;

	// 右侧箭头
	private ImageView mImageViewForward;

	/**
	 * @param context
	 */
	public ItemLayout(Context context) {
		super(context);
		this.mContext = (Activity) context;
		init(null);
	}

	public ItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = (Activity) context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		LinearLayout layout = (LinearLayout) inflate(mContext,
				R.layout.layout_item_view, null);
		TypedArray types = mContext.obtainStyledAttributes(attrs,
				R.styleable.ItemLayout, 0, 0);

		mImageLeft1 = (ImageView) layout
				.findViewById(R.id.itemLayout_image_left_1);
		mTextLeft2 = (TextView) layout
				.findViewById(R.id.itemLayout_text_left_2);
		mTextRight1 = (TextView) layout
				.findViewById(R.id.itemLayout_text_right_1);
		mTextRight2 = (TextView) layout
				.findViewById(R.id.itemLayout_text_right_2);

		int colorLeft1 = types.getColor(
				R.styleable.ItemLayout_itemLayout_left1_textColor, Color.BLACK);
		int colorLeft2 = types.getColor(
				R.styleable.ItemLayout_itemLayout_left2_textColor, Color.BLACK);
		int colorRight1 = types
				.getColor(R.styleable.ItemLayout_itemLayout_right1_textColor,
						Color.BLACK);
		int colorRight2 = types
				.getColor(R.styleable.ItemLayout_itemLayout_right2_textColor,
						Color.BLACK);

		// mImageLeft1.setTextColor(colorLeft1);
		mTextLeft2.setTextColor(colorLeft2);
		mTextRight1.setTextColor(colorRight1);
		mTextRight2.setTextColor(colorRight2);

		float normalTextSize = 17;
		float textSizeLeft1 = types.getDimension(
				R.styleable.ItemLayout_itemLayout_left1_textSize,
				normalTextSize);
		float textSizeLeft2 = types.getDimension(
				R.styleable.ItemLayout_itemLayout_left2_textSize,
				normalTextSize);
		float textSizeRight1 = types.getDimension(
				R.styleable.ItemLayout_itemLayout_right1_textSize,
				normalTextSize);
		float textSizeRight2 = types.getDimension(
				R.styleable.ItemLayout_itemLayout_right2_textSize,
				normalTextSize);

		// mImageLeft1.setTextSize(textSizeLeft1);
		mTextLeft2.setTextSize(textSizeLeft2);
		mTextRight1.setTextSize(textSizeRight1);
		mTextRight2.setTextSize(textSizeRight2);

		String strHintLeft1 = types
				.getString(R.styleable.ItemLayout_itemLayout_left1_textHint);
		String strHintLeft2 = types
				.getString(R.styleable.ItemLayout_itemLayout_left2_textHint);
		String strHintRight1 = types
				.getString(R.styleable.ItemLayout_itemLayout_right1_textHint);
		String strHintRight2 = types
				.getString(R.styleable.ItemLayout_itemLayout_right2_textHint);

		// mImageLeft1.setHint(strHintLeft1);
		mTextLeft2.setHint(strHintLeft2);
		mTextRight1.setHint(strHintRight1);
		mTextRight2.setHint(strHintRight2);

		String strLeft1 = types
				.getString(R.styleable.ItemLayout_itemLayout_left1_text);
		String strLeft2 = types
				.getString(R.styleable.ItemLayout_itemLayout_left2_text);
		String strRight1 = types
				.getString(R.styleable.ItemLayout_itemLayout_right1_text);
		String strRight2 = types
				.getString(R.styleable.ItemLayout_itemLayout_right2_text);

		// mImageLeft1.setText(strLeft1);
		mTextLeft2.setText(strLeft2);
		mTextRight1.setText(strRight1);
		mTextRight2.setText(strRight2);

		boolean needArrow = types.getBoolean(
				R.styleable.ItemLayout_itemLayout_need_arrow, true);

		mImageViewForward = (ImageView) layout
				.findViewById(R.id.include_arrow_right);

		if (!needArrow) {
			mImageViewForward.setVisibility(View.GONE);
		}
		addView(layout);
		types.recycle();
	}

	public ImageView getImageLeft1() {
		return mImageLeft1;
	}

	public TextView getTextLeft2() {
		return mTextLeft2;
	}

	public TextView getTextRight1() {
		return mTextRight1;
	}

	public TextView getTextRight2() {
		return mTextRight2;
	}

}
