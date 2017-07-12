package com.hifleet.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.hifleet.map.Algorithms;
import com.hifleet.plus.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


public class MoveFilesToDifferentDirectory extends AsyncTask<Void, Void, Boolean>{
	private File to;
	private Context ctx;
	private File from;
	//protected ProgressDialogImplementation progress;
	private Runnable runOnSuccess;

	public MoveFilesToDifferentDirectory(Context ctx, File from, File to) {
		this.ctx = ctx;
		this.from = from;
		this.to = to;
	}
	
	public void setRunOnSuccess(Runnable runOnSuccess) {
		this.runOnSuccess = runOnSuccess;
	}
	
	@Override
	protected void onPreExecute() {
//		progress = ProgressDialogImplementation.createProgressDialog(
//				ctx, ctx.getString(R.string.copying_osmand_files),
//				ctx.getString(R.string.copying_osmand_files_descr, to.getPath()),
//				ProgressDialog.STYLE_HORIZONTAL);
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result != null) {
			if (result.booleanValue() && runOnSuccess != null) {
				runOnSuccess.run();
			} else if (!result.booleanValue()) {
				Toast.makeText(ctx, R.string.input_output_error, Toast.LENGTH_LONG).show();
			}
		}
//		if(progress.getDialog().isShowing()) {
//			progress.getDialog().dismiss();
//		}
	}
	
	private void movingFiles(File f, File t, int depth) throws IOException {
		if(depth <= 2) {
			//progress.startTask(ctx.getString(R.string.copying_osmand_one_file_descr, t.getName()), -1);
		}
		if (f.isDirectory()) {
			t.mkdirs();
			File[] lf = f.listFiles();
			if (lf != null) {
				for (int i = 0; i < lf.length; i++) {
					if (lf[i] != null) {
						movingFiles(lf[i], new File(t, lf[i].getName()), depth + 1);
					}
				}
			}
			f.delete();
		} else if (f.isFile()) {
			if(t.exists()) {
				Algorithms.removeAllFiles(t);
			}
			boolean rnm = false;
			try {
				rnm = f.renameTo(t);
			} catch(RuntimeException e) {
			}
			if (!rnm) {
				FileInputStream fin = new FileInputStream(f);
				FileOutputStream fout = new FileOutputStream(t);
				try {
					Algorithms.streamCopy(fin, fout);
				} finally {
					fin.close();
					fout.close();
				}
				f.delete();
			}
		}
		if(depth <= 2) {
			//progress.finishTask();
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		to.mkdirs();
		try {
			movingFiles(from, to, 0);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
