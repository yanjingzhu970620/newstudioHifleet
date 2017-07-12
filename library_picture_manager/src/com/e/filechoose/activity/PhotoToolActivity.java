package com.e.filechoose.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.byb.filechoose.R;
import com.e.common.activity.BaseActivity;
import com.e.common.constant.Constants;
import com.e.common.constant.Constants.IDENTITY;
import com.e.common.utility.CommonUtility.FileUtility;
import com.e.common.utility.CommonUtility.ImageUtility;
import com.e.common.utility.CommonUtility.Utility;
import com.e.filechoose.activity.camera.TakePhotoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoToolActivity extends BaseActivity {

	private String temp_file_path;
	private boolean isCap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Utility.isNull(savedInstanceState)) {
			temp_file_path = savedInstanceState
					.getString(Constants.IDENTITY.IDENTITY_FILEPATH);
			isCap = savedInstanceState
					.getBoolean(Constants.IDENTITY.IDENTITY_CAP);
		}
		setContentView(R.layout.picture_choose_panel);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		isCap = getIntent().getBooleanExtra(Constants.IDENTITY.IDENTITY_CAP,
				false);
	}

	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_paizhao) {
			startTokePhotoActivity();
		} else if (id == R.id.btn_choose_from_device) {
			startAlbumActivity();
		} else if (id == R.id.btn_cancel) {
			finish();
		}
	}

	private void startTokePhotoActivity() {
		startActivityForResult(new Intent(activity, TakePhotoActivity.class), 0);
	}

	private void startAlbumActivity() {
		startActivityForResult(getIntent().setClass(this, AlbumActivity.class),
				0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 3:
				if (data != null) {
					setPicToView(data);
				}
				break;
			default:
				temp_file_path = data
						.getStringExtra(Constants.IDENTITY.IDENTITY_FILEPATH);
				if (!Utility.isNull(temp_file_path)) {
					Bitmap b = ImageLoader.getInstance().loadImageSync(
							ImageUtility.formatUrl(temp_file_path));
					try {
						ImageUtility.storeImage(temp_file_path, b);
						addBitmap(b);
						operateImg();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					} finally {
						destoryBitmap(b);
					}
				}
				break;
			}
		} else if (resultCode == AlbumActivity.RESULT_CODE_FILE) {
			if (!Utility.isNull(data)) {
				String path = data.getStringExtra(AlbumActivity.IDENTITY_FILE);
				if (!path.contains(AlbumActivity.PATH_SLITE)) {
					if (isCap) {
						startPhotoZoom(Uri.fromFile(new File(path)));
					} else {
						Bitmap b = ImageLoader.getInstance().loadImageSync(
								ImageUtility.formatUrl(path));
						try {
							ImageUtility.storeImage(path, b);
							addBitmap(b);
							Intent intent = new Intent();
							intent.putExtra(IDENTITY.IDENTITY_FILEPATH, path);
							setResult(IDENTITY.ACTIVITY_CHOOSEFILE_CODE, intent);
							finish();
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						} finally {
							destoryBitmap(b);
						}
					}
				} else {
					Intent intent = new Intent();
					intent.putExtra(IDENTITY.IDENTITY_FILEPATH, path);
					setResult(IDENTITY.ACTIVITY_CHOOSEFILE_CODE, intent);
					finish();
				}
			}
		} else {
			finish();
		}
	}

	String temp_cap_pic;

	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		temp_cap_pic = FileUtility.getUUIDImgPath();
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(temp_cap_pic)));
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			// 兼容旧版本以及一些pad，剪切后不生成图片文件，通过data拿到bitmap再保存
			Bitmap b = (Bitmap) extras.get("data");
			ImageUtility.storeImage(temp_cap_pic, b);
			Intent intent = new Intent();
			intent.putExtra(IDENTITY.IDENTITY_FILEPATH, temp_cap_pic);
			setResult(IDENTITY.ACTIVITY_CHOOSEFILE_CODE, intent);
			finish();
		}
	}

	void operateImg() {
		if (isCap) {
			startPhotoZoom(Uri.fromFile(new File(temp_file_path)));
		} else {
			Intent intent = new Intent();
			intent.putExtra(IDENTITY.IDENTITY_FILEPATH, temp_file_path);
			// intent.putExtra(Constants.IDENTITY_FILEPATH,
			// FilesUtil.compressionImage(temp_file_path));
			setResult(IDENTITY.ACTIVITY_CHOOSEFILE_CODE, intent);
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(IDENTITY.IDENTITY_FILEPATH, temp_file_path);
		outState.putBoolean(IDENTITY.IDENTITY_CAP, isCap);
	}

	/**
	 * method desc：
	 *
	 * @param context
	 * @param isCap
	 *            是否需要裁切
	 */
	public static void go2PhotoToolActivity(Activity activity, boolean isCap) {
		Intent intent = new Intent(activity, PhotoToolActivity.class);
		intent.putExtra(Constants.IDENTITY.IDENTITY_CAP, isCap);
		activity.startActivityForResult(intent, 0);
	}

	/**
	 * method desc：
	 *
	 * @param context
	 * @param isCap
	 *            是否需要裁切
	 */
	public static void go2PhotoToolActivity(Fragment fragment, boolean isCap) {
		Intent intent = new Intent(fragment.getActivity(),
				PhotoToolActivity.class);
		intent.putExtra(Constants.IDENTITY.IDENTITY_CAP, isCap);
		fragment.startActivityForResult(intent, 0);
	}
}
