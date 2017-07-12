package com.e.filechoose.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byb.filechoose.R;
import com.e.common.utility.CommonUtility.DialogUtility;
import com.e.common.utility.CommonUtility.UIUtility;
import com.e.common.utility.CommonUtility.Utility;
import com.e.filechoose.adapter.MyAdapter;
import com.e.filechoose.adapter.MyAdapter.onSelectedImagesClickListener;
import com.e.filechoose.bean.ImageFloder;
import com.e.filechoose.utils.ListImageDirPopupWindow;
import com.e.filechoose.utils.ListImageDirPopupWindow.OnImageDirSelected;

@SuppressLint({ "HandlerLeak", "InflateParams" })
public class AlbumActivity extends Activity implements OnImageDirSelected,
		onSelectedImagesClickListener {
	private ProgressDialog mProgressDialog;

	/**
	 * �洢�ļ����е�ͼƬ����
	 */
	private int mPicsSize;
	/**
	 * ͼƬ���������ļ���
	 */
	private File mImgDir;
	/**
	 * ���е�ͼƬ
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * ��ʱ�ĸ����࣬���ڷ�ֹͬһ���ļ��еĶ��ɨ��
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * ɨ���õ����е�ͼƬ�ļ���
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	public final static String IDENTITY_FILE = "identity_file";
	public final static String IDENTITY_TITLE_NAME = "identity_title_name";
	public final static int RESULT_CODE_FILE = 0x101010;
	public final static String PATH_SLITE = "@@";

	public int maxNum = 1;
	public static final String IDENTITY_PIC_NUM = "IDENTITY_PIC_NUM";

	private TextView mAlbumTitle;

	/**
	 * �û�ѡ���ͼƬ���洢ΪͼƬ������·��
	 */
	public List<String> mSelectedImage = new LinkedList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		maxNum = getIntent().getIntExtra(IDENTITY_PIC_NUM, 1);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();
	}

	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_close) {
			finish();
		} else if (id == R.id.btn_ok) {
			String path = null;
			if (maxNum <= 1) {
				if (mSelectedImage.size() > 0) {
					path = mSelectedImage.get(0);
				}
				if (Utility.isNull(path)) {
					UIUtility.toast(this, "��ѡ��ͼƬ");
					return;
				}
			} else {
				if (mSelectedImage.size() > 0) {
					StringBuilder sb = new StringBuilder();
					for (String p : mSelectedImage) {
						sb.append(p).append(PATH_SLITE);
					}
					path = sb.substring(0, sb.length() - PATH_SLITE.length())
							.toString();
				} else {
					UIUtility.toast(this, "��ѡ��ͼƬ");
					return;
				}
			}
			Intent intent = new Intent();
			intent.putExtra(IDENTITY_FILE, path);
			setResult(RESULT_CODE_FILE, intent);
			finish();
		}
	}

	/**
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� ���ͼƬ��ɨ�裬���ջ��jpg�����Ǹ��ļ���
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "�����ⲿ�洢", Toast.LENGTH_SHORT).show();
			return;
		}
		// ��ʾ������
		mProgressDialog = ProgressDialog.show(this, null, "���ڼ���...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = AlbumActivity.this
						.getContentResolver();

				// ֻ��ѯjpeg��png��ͼƬ
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// ��ȡͼƬ��·��
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// �õ���һ��ͼƬ��·��
					if (firstImage == null)
						firstImage = path;
					// ��ȡ��ͼƬ�ĸ�·����
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// ����һ��HashSet��ֹ���ɨ��ͬһ���ļ��У���������жϣ�ͼƬ�����������൱�ֲ���~~��
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// ��ʼ��imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					if (!Utility.isNull(parentFile.list())) {
						int picSize = parentFile.list(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String filename) {
								if (filename.endsWith(".jpg")
										|| filename.endsWith(".png")
										|| filename.endsWith(".jpeg"))
									return true;
								return false;
							}
						}).length;
						totalCount += picSize;

						imageFloder.setCount(picSize);
						mImageFloders.add(imageFloder);

						if (picSize > mPicsSize) {
							mPicsSize = picSize;
							mImgDir = parentFile;
						}
					}
				}
				mCursor.close();

				// ɨ����ɣ�������HashSetҲ�Ϳ����ͷ��ڴ���
				mDirPaths = null;

				// ֪ͨHandlerɨ��ͼƬ���
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// ΪView������
			data2View();
			// ��ʼ��չʾ�ļ��е�popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * ΪView������
	 */
	private void data2View() {
		if (mImgDir == null) {
			UIUtility.toast(this, "û��ɨ�赽ͼƬ��");
			return;
		}

		mImgs = Arrays.asList(mImgDir.list());
		/**
		 * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ�
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mAdapter.setOnSelectedImagesClickListener(this);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText("��" + totalCount + "��");
	};

	/**
	 * ��ʼ��չʾ�ļ��е�popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// ���ñ�����ɫ�䰵
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// ����ѡ���ļ��еĻص�
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	/**
	 * ��ʼ��View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

		mAlbumTitle = (TextView) findViewById(R.id.album_name);
		String title = getIntent().getStringExtra(IDENTITY_TITLE_NAME);
		if (!Utility.isNull(title)) {
			mAlbumTitle.setText(title);
		} else {
			mAlbumTitle.setText("ѡ����Ƭ");
		}

	}

	private void initEvent() {
		/**
		 * Ϊ�ײ��Ĳ������õ���¼�������popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// ���ñ�����ɫ�䰵
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ�
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mAdapter.setOnSelectedImagesClickListener(this);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText("��" + floder.getCount() + "��");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.filechoose.adapter.MyAdapter.onSelectedImagesClickListener#onSelect
	 * (java.lang.String)
	 */
	@Override
	public void onSelect(String path) {
		// TODO Auto-generated method stub
		if (mSelectedImage.size() >= maxNum) {
			DialogUtility.tip(this, "���ѡ��" + maxNum + "ͼƬ");
		} else {
			mSelectedImage.add(path);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.filechoose.adapter.MyAdapter.onSelectedImagesClickListener#onRemove
	 * (java.lang.String)
	 */
	@Override
	public void onRemove(String path) {
		// TODO Auto-generated method stub
		mSelectedImage.remove(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.filechoose.adapter.MyAdapter.onSelectedImagesClickListener#isExist
	 * (java.lang.String)
	 */
	@Override
	public boolean isExist(String path) {
		// TODO Auto-generated method stub
		return mSelectedImage.contains(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.byb.filechoose.adapter.MyAdapter.onSelectedImagesClickListener#
	 * isOverStack()
	 */
	@Override
	public boolean isOverStack() {
		// TODO Auto-generated method stub
		return mSelectedImage.size() >= maxNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.filechoose.adapter.MyAdapter.onSelectedImagesClickListener#errorTip
	 * ()
	 */
	@Override
	public void errorTip() {
		// TODO Auto-generated method stub
		if (mSelectedImage.size() >= maxNum) {
			DialogUtility.tip(this, "���ѡ��" + maxNum + "ͼƬ");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.byb.filechoose.adapter.MyAdapter.onSelectedImagesClickListener#getMaxNum
	 * ()
	 */
	@Override
	public int getMaxNum() {
		// TODO Auto-generated method stub
		return maxNum;
	}

}
