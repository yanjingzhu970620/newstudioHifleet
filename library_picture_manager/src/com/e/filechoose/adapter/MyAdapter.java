package com.e.filechoose.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.byb.filechoose.R;
import com.e.common.utility.CommonUtility.Utility;
import com.e.filechoose.utils.ViewHolder;

public class MyAdapter extends CommonAdapter<String> {

	private onSelectedImagesClickListener listener;

	public interface onSelectedImagesClickListener {
		// 将图片路径添加到集合中
		public void onSelect(String path);

		// 从集合中移除指定路径
		public void onRemove(String path);

		// 判断此路径是否已经记录下来
		public boolean isExist(String path);

		// 判断是否超出了设置的最大数字
		public boolean isOverStack();

		// 通知错误提示
		public void errorTip();

		public int getMaxNum();
	}

	public void setOnSelectedImagesClickListener(
			onSelectedImagesClickListener listener) {
		this.listener = listener;
	}

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	private ImageView preImageView, preSelect;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select,
				R.drawable.picture_unselected);

		String p = mDirPath + "/" + item;

		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, p);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				ImageView imageView = (ImageView) v;
				String path = mDirPath + "/" + item;
				imageView.setTag(path);
				// 已经选择过该图片
				if (listener.isExist(path)) {
					unSelected(mSelect, mImageView, path);
				} else {// 未选择该图片
					if (listener.getMaxNum() == 1) {
						if (!Utility.isNull(preImageView)
								&& !Utility.isNull(preSelect)) {
							unSelected(preSelect, preImageView, preImageView
									.getTag().toString());
						}
						selected(mSelect, mImageView, path);
						preImageView = imageView;
						preSelect = mSelect;
					} else {
						if (!listener.isOverStack()) {
							selected(mSelect, mImageView, path);
						} else {
							listener.errorTip();
						}
					}

				}
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (listener.isExist(p)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}

	void selected(ImageView mSelect, ImageView imageView, String path) {
		listener.onSelect(path);
		mSelect.setImageResource(R.drawable.pictures_selected);
		imageView.setColorFilter(Color.parseColor("#77000000"));
	}

	void unSelected(ImageView mSelect, ImageView imageView, String path) {
		listener.onRemove(path);
		mSelect.setImageResource(R.drawable.picture_unselected);
		imageView.setColorFilter(null);
	}
}
