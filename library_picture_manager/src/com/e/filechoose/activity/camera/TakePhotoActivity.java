/**
 * 
 * @author Evan
 * @date 2013/4/29
 */
package com.e.filechoose.activity.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.byb.filechoose.R;
import com.e.common.activity.BaseActivity;
import com.e.common.constant.Constants.IDENTITY;
import com.e.common.utility.CommonUtility.BitmapOperateUtility;
import com.e.common.utility.CommonUtility.FileUtility;
import com.e.common.utility.CommonUtility.ImageUtility;
import com.e.common.utility.CommonUtility.UIUtility;
import com.e.common.utility.CommonUtility.Utility;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class TakePhotoActivity extends BaseActivity {
	private Camera camera;

	private int WIDTH, HEIGHT;
	private SurfaceHolder mHolder;
	SurfaceView mSurfaceView;
	int mCameraCount = 0;

	MediaPlayer mMpTakePhoto, mMpFocus;
	public static int mCameraCurrentId;
	private boolean mIsAutoFocus = true;

	private Button mBtnOk;
	private Button mBtnShutter;
	private Button mBtnSwitchCamera;
	private Button mBtnCameraReTake;
	private ImageView mImage;
	Bitmap rotateBitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		mBtnOk = (Button) findViewById(R.id.id_btn_ok);
		mBtnShutter = (Button) findViewById(R.id.id_shutter);

		mBtnCameraReTake = (Button) findViewById(R.id.id_camera_retake);
		mBtnSwitchCamera = (Button) findViewById(R.id.id_switch_camera);
		mImage = (ImageView) findViewById(R.id.id_img);

		WIDTH = UIUtility.getScreenHeight(this);
		HEIGHT = UIUtility.getScreenWidth(this);
		mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		mHolder = mSurfaceView.getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.setFixedSize(WIDTH, HEIGHT);
		mHolder.setKeepScreenOn(true);
		mHolder.addCallback(new SurfaceCallback());// add callback for
													// SurfaceView

		mCameraCount = Camera.getNumberOfCameras();// get count of camera

		if (mCameraCount < 2) {
			mBtnSwitchCamera.setVisibility(View.GONE);
		}
		// init sound
		mMpTakePhoto = new MediaPlayer();
		mMpTakePhoto.setAudioStreamType(AudioManager.STREAM_MUSIC);

		AssetFileDescriptor file = getResources().openRawResourceFd(
				R.raw.camera_click);
		try {
			mMpTakePhoto.setDataSource(file.getFileDescriptor(),
					file.getStartOffset(), file.getLength());
			file.close();
			mMpTakePhoto.prepare();
		} catch (IOException e) {
			mMpTakePhoto = null;
		}

		mMpFocus = new MediaPlayer();
		mMpFocus.setAudioStreamType(AudioManager.STREAM_MUSIC);

		AssetFileDescriptor fileFouce = getResources().openRawResourceFd(
				R.raw.camera_focus);
		try {
			mMpFocus.setDataSource(fileFouce.getFileDescriptor(),
					fileFouce.getStartOffset(), fileFouce.getLength());
			fileFouce.close();
			mMpFocus.prepare();
		} catch (IOException e) {
			mMpFocus = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.evan.honda.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mIsAutoFocus = false;
		mCameraCurrentId = Camera.CameraInfo.CAMERA_FACING_BACK;
		try {
			if (!Utility.isNull(camera)) {
				camera.setOneShotPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			}
			if (!Utility.isNull(mMpTakePhoto)) {
				mMpTakePhoto.release();
				mMpTakePhoto = null;
			}
			if (!Utility.isNull(mMpFocus)) {
				mMpFocus.release();
				mMpFocus = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// mp.release();
	}

	/**
	 * 按钮被点击触发的事件
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		if (camera != null) {
			int id = v.getId();
			if (id == R.id.id_shutter) {
				mMpTakePhoto.start();
				camera.setOneShotPreviewCallback(new MyPreviewCallback());
			} else if (id == R.id.id_btn_ok) {
				Intent intent = new Intent();
				String filePath = FileUtility.getUUIDImgPath();
				mImage.setVisibility(View.GONE);
				ImageUtility.storeImage(filePath, rotateBitmap);
				intent.putExtra(IDENTITY.IDENTITY_FILEPATH, filePath);
				setResult(Activity.RESULT_OK, intent);
				finish();
			} else if (id == R.id.id_switch_camera) {
				if (camera != null) {
					stopPreview();
					camera.release();
					camera = null;
				}
				mCameraCurrentId = (mCameraCurrentId + 1) % mCameraCount;
				camera = Camera.open(mCameraCurrentId);

				setDesiredCameraParameters(camera);
				LayoutParams lp = (LayoutParams) mSurfaceView.getLayoutParams();
				lp.width = currentH;
				lp.height = currentW;
				mSurfaceView.setLayoutParams(lp);
				try {
					camera.setPreviewDisplay(mHolder);
				} catch (IOException exception) {
					camera.release();
					camera = null;
				}
				camera.setDisplayOrientation(90);
				startPreview();
			} else if (id == R.id.id_camera_retake) {
				try {
					camera.setPreviewDisplay(mHolder);
				} catch (IOException exception) {
					camera.release();
					camera = null;
				}
				camera.setDisplayOrientation(90);
				destoryBitmaps();
				startPreview();
			} else if (id == R.id.surfaceView) {
				if (mBtnShutter.isShown()) {
					mMpFocus.start();
					camera.autoFocus(focusCallback);
				}
			} else if (id == R.id.id_back) {
				finish();
			}
		}
	}

	private final class MyPreviewCallback implements PreviewCallback {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.hardware.Camera.PreviewCallback#onPreviewFrame(byte[],
		 * android.hardware.Camera)
		 */
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Camera.Parameters parameters = camera.getParameters();
			int imageFormat = parameters.getPreviewFormat();
			if (imageFormat == ImageFormat.NV21) {
				// get full picture
				int w = parameters.getPreviewSize().width;
				int h = parameters.getPreviewSize().height;
				Rect rect = new Rect(0, 0, w, h);
				YuvImage img = new YuvImage(data, ImageFormat.NV21, w, h, null);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (!img.compressToJpeg(rect, 100, baos)) {
					return;
				}
				data = baos.toByteArray();
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						baos.size());

				if (mCameraCurrentId == Camera.CameraInfo.CAMERA_FACING_BACK) {
					rotateBitmap = BitmapOperateUtility.rotate(bitmap, 90);
				} else if (mCameraCurrentId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					rotateBitmap = BitmapOperateUtility.rotate(bitmap, -90);
				}
				mImage.setVisibility(View.VISIBLE);
				mImage.setImageBitmap(rotateBitmap);
				addBitmap(rotateBitmap);
				try {
					baos.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			stopPreview();
		}
	}

	private final class SurfaceCallback implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder mHolder, int format,
				int width, int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder mHolder) {
			try {
				camera = Camera.open(); // open the default camera
				camera.setPreviewDisplay(mHolder);
				camera.setDisplayOrientation(90);
				startPreview();
			} catch (Exception e) {
				e.printStackTrace();
				if (!Utility.isNull(camera)) {
					camera.release();
					camera = null;
				}
			}
		}

		// when stop invoke
		@Override
		public void surfaceDestroyed(SurfaceHolder mHolder) {
			if (camera != null) {
				camera.release(); // release camera
				camera = null;
			}
		}
	}

	int currentW, currentH;

	/**
	 * Sets the camera up to take preview images which are used for both preview
	 * and decoding. We detect the preview format here so that
	 * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
	 * In the future we may want to force YUV420SP as it's the smallest, and the
	 * planar Y can be used for barcode scanning without a copy in some cases.
	 */
	void setDesiredCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		List<Size> sizesPre = parameters.getSupportedPreviewSizes();
		if (sizesPre.size() >= 2) {
			int w1 = sizesPre.get(0).width;
			int w2 = sizesPre.get(1).width;
			if (w1 < w2) {
				Collections.reverse(sizesPre);
			}
		}
		for (Size size : sizesPre) {
			if (WIDTH >= size.width && HEIGHT >= size.height) {
				currentW = size.width;
				currentH = size.height;
				parameters.setPreviewSize(currentW, currentH);
				break;
			}
		}
		// setFlash(parameters);
		// setZoom(parameters);

		// setSharpness(parameters);
		camera.setParameters(parameters);
	}

	private Thread currentThread;

	private void startPreview() {
		setDesiredCameraParameters(camera);
		mIsAutoFocus = true;
		currentThread = new Thread(autoFocusRunnable);
		currentThread.start();
		camera.startPreview();
		mBtnCameraReTake.setVisibility(View.GONE);
		mBtnSwitchCamera.setVisibility(View.VISIBLE);
		mBtnShutter.setVisibility(View.VISIBLE);
		mBtnOk.setVisibility(View.GONE);
		mImage.setVisibility(View.GONE);
	}

	private void stopPreview() {
		mIsAutoFocus = false;
		camera.stopPreview();
		try {
			currentThread.interrupt();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		mBtnCameraReTake.setVisibility(View.VISIBLE);
		mBtnSwitchCamera.setVisibility(View.GONE);
		mBtnShutter.setVisibility(View.GONE);
		mBtnOk.setVisibility(View.VISIBLE);
		mImage.setVisibility(View.VISIBLE);
	}

	private Runnable autoFocusRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (mIsAutoFocus) {
				try {
					mMpFocus.start();
					camera.autoFocus(focusCallback);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	AutoFocusCallback focusCallback = new AutoFocusCallback() {// 自动对焦
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			// mp.start();
		}
	};
}
