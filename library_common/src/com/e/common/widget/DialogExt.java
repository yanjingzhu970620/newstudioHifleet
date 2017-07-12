package com.e.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e.common.utility.CommonUtility.Utility;
import com.e.library_common.R;

public class DialogExt extends Dialog implements OnClickListener {

	private Context context;
	private TextView title, message, btn_divider;
	private Button okBtn, cancelBtn;
	public final static int OK = 1;
	public final static int CANCEL = 2;

	private RelativeLayout ll_content_view, rl_btns;
	public View customerView;

	public static DialogExt createDialog(Context context) {
		return new DialogExt(context, R.style.custome_dialog);
	}

	private DialogExt(Context context, int theme) {
		super(context, theme);
		this.context = context;
		setContentView(R.layout.layout_custome_dialog);
		title = (TextView) findViewById(R.id.id_text_title);
		message = (TextView) findViewById(R.id.id_text_message);
		okBtn = (Button) findViewById(R.id.id_btn_ok);
		cancelBtn = (Button) findViewById(R.id.id_btn_cancel);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		btn_divider = (TextView) findViewById(R.id.id_btn_divider);
		ll_content_view = (RelativeLayout) findViewById(R.id.ll_content_view);
		rl_btns = (RelativeLayout) findViewById(R.id.rl_btns);
		setCancelable(false);
	}

	public void setOnClickListener(android.view.View.OnClickListener okListener) {
		if (!Utility.isNull(okListener)) {
			okBtn.setOnClickListener(okListener);
		}
	}

	public void setOnClickListener(
			android.view.View.OnClickListener okListener,
			android.view.View.OnClickListener cancelListener) {
		if (!Utility.isNull(okListener)) {
			okBtn.setOnClickListener(okListener);
		}
		if (!Utility.isNull(cancelListener)) {
			cancelBtn.setOnClickListener(cancelListener);
		}
	}

	public void setOnClickListener(
			android.view.View.OnClickListener okListener, Object okTag,
			android.view.View.OnClickListener cancelListener, Object cancelTag) {
		if (!Utility.isNull(okListener)) {
			okBtn.setTag(okTag);
			okBtn.setOnClickListener(okListener);
		}
		if (!Utility.isNull(cancelListener)) {
			cancelBtn.setTag(cancelTag);
			cancelBtn.setOnClickListener(cancelListener);
		}
	}

	public void setTitle(String str) {
		title.setText(str);
		title.setVisibility(View.VISIBLE);
	}

	public void setTitle(int resStr) {
		title.setText(context.getString(resStr));
		title.setVisibility(View.VISIBLE);
	}

	public void setMessage(String str) {
		message.setText(str);
		title.setVisibility(View.VISIBLE);
	}

	public void setMessage(int resStr) {
		message.setText(context.getString(resStr));
		title.setVisibility(View.VISIBLE);
	}

	public void setView(View view) {
		ll_content_view.removeAllViews();
		ll_content_view.addView(view);
		this.customerView = view;
	}

	public Button getCancelBtn() {
		return cancelBtn;
	}

	public Button getOKBtn() {
		return okBtn;
	}

	/**
	 * 设置显示单个按钮
	 * 
	 * @param btnType
	 *            显示按钮的类型
	 */
	public void setSingleBtn(int btnType) {
		if (btnType == OK) {
			cancelBtn.setVisibility(View.GONE);
			okBtn.setBackgroundResource(R.drawable.xml_btn_dialog_single);
		} else if (btnType == CANCEL) {
			okBtn.setVisibility(View.GONE);
			cancelBtn.setBackgroundResource(R.drawable.xml_btn_dialog_single);
		}
		btn_divider.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		dismiss();
	}

	public void showBtns(int visibility) {
		rl_btns.setVisibility(visibility);
	}

}
