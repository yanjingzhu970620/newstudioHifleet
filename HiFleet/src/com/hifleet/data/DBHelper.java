package com.hifleet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "MultiDownLoad.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE fileDownloading(_id integer primary key autoincrement,"
				+ "downPath varchar(100),downLength INTEGER,progress INTEGER)");
		
		db.execSQL("CREATE TABLE fileDownloadWaitingQueue(_id integer primary key autoincrement,"
				+ "fileName varchar(100),description varchar(100)," +
				"size varchar(30),date varchar(30),rlon varchar(20),rlat varchar(20)," +
				"llon varchar(20),llat varchar(20),filename1 varchar(20),downPath varchar(100))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}