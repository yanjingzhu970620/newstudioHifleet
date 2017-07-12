package com.e.common.widget.effect.button;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.e.library_common.R;

/**
 * @{# EffectColorButton.java Create on 2014年11月21日 下午1:00:07
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class EffectColorButton extends TextView {

	private Context mContext;

	private float[] radiusArr;

	public EffectColorButton(Context context) {
		super(context);
		mContext = context;
	}

	public EffectColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs, 0);
	}

	public EffectColorButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {

		// 读取配置
		TypedArray array = mContext.obtainStyledAttributes(attrs,
				R.styleable.EffectColorBtn, 0, 0);

		boolean clickable = array.getBoolean(
				R.styleable.EffectColorBtn_effectColor_clickable, true);

		setClickable(clickable);

		// 默认背景颜色
		int bgNormalColor = array.getColor(
				R.styleable.EffectColorBtn_effectColor_bgNormalColor,
				0xffffffff);
		// 按下背景颜色
		int bgPressedColor = array.getColor(
				R.styleable.EffectColorBtn_effectColor_bgPressedColor,
				0xdd000000);
		// 导角
		float radius = array.getDimension(
				R.styleable.EffectColorBtn_effectColor_radius, 0);

		// 默认字体颜色
		int textNormalColor = array.getColor(
				R.styleable.EffectColorBtn_effectColor_textNormalColor,
				getTextColors().getDefaultColor());
		// 按下字体颜色
		int textPressedColor = array.getColor(
				R.styleable.EffectColorBtn_effectColor_textPressedColor,
				textNormalColor);

		// 初始化导角
		radiusArr = new float[] { radius, radius, radius, radius, radius,
				radius, radius, radius };

		setBgColor(new int[] { bgNormalColor, bgPressedColor });

		setTextColor(new int[] { textPressedColor, textNormalColor });

		array.recycle();
	}

	public void setTextColor(int[] colors) {
		int[][] textColorStates = new int[2][];
		textColorStates[0] = new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled };
		textColorStates[1] = new int[] { android.R.attr.state_enabled };
		ColorStateList textColorList = new ColorStateList(textColorStates,
				colors);
		setTextColor(textColorList);
	}

	@SuppressWarnings("deprecation")
	public void setBgColor(int[] colors) {
		// 默认状态
		ShapeDrawable shapeNormal = new ShapeDrawable(new RoundRectShape(
				radiusArr, null, null));
		shapeNormal.getPaint().setColor(colors[0]);

		Drawable[] drawableNormal = { shapeNormal };
		LayerDrawable normal = new LayerDrawable(drawableNormal);

		// 点击状态
		ShapeDrawable shapePressed = new ShapeDrawable(new RoundRectShape(
				radiusArr, null, null));
		shapePressed.getPaint().setColor(colors[1]);

		Drawable[] drawablePressed = { shapePressed };
		LayerDrawable pressed = new LayerDrawable(drawablePressed);

		// 不可点击状态
		ShapeDrawable shapeDisable = new ShapeDrawable(new RoundRectShape(
				radiusArr, null, null));
		shapeDisable.getPaint().setColor(0xffaaaaaa);

		Drawable[] drawableDisable = { shapeDisable };
		LayerDrawable disabled = new LayerDrawable(drawableDisable);

		StateListDrawable bgStates = new StateListDrawable();

		bgStates.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, pressed);
		bgStates.addState(new int[] { android.R.attr.state_focused,
				android.R.attr.state_enabled }, pressed);

		bgStates.addState(new int[] { android.R.attr.state_enabled }, normal);
		bgStates.addState(new int[] { -android.R.attr.state_enabled }, disabled);

		// set Background
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			setBackgroundDrawable(bgStates);
		else
			setBackground(bgStates);
	}

}
