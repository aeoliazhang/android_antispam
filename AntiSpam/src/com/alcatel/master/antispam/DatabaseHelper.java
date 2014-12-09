package com.alcatel.master.antispam;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "AntiSpam.db";
	private static final int    DB_VERSION = 1;
	
	public static final String  BLOCK_TABLE 		= "BlockTable";	
	public static final String  ALLOW_TABLE		 	= "AllowTable";	
	public static final String  SMSKEYWORD_TABLE	= "SmsKeywordTable";
	public static final String  SMS_HISTORY_TABLE 	= "SmsHistoryTable";
	public static final String  PHONE_HISTORY_TABLE = "PhoneHistoryTable";
	
	//block table column
	public static final int  BLOCK_TABLE_ID = 0;
	public static final int  BLOCK_TABLE_NUMBER = 1;	
	
	//allow table column
	public static final int  ALLOW_TABLE_ID = 0;
	public static final int  ALLOW_TABLE_NUMBER = 1;
	
	//keyword table column
	public static final int  SMSKEYWORD_TABLE_ID = 0;
	public static final int  SMSKEYWORD_TABLE_KEYWORD = 1;	
	
	//sms history table column
	public static final int  SMS_HISTORY_TABLE_ID = 0;
	public static final int  SMS_HISTORY_TABLE_NUMBER = 1;	
	public static final int  SMS_HISTORY_TABLE_TIME = 2;	
	public static final int  SMS_HISTORY_TABLE_CONTENT = 3;	
	
	//phone history table column
	public static final int  PHONE_HISTORY_TABLE_ID = 0;
	public static final int  PHONE_HISTORY_TABLE_NUMBER = 1;	
	public static final int  PHONE_HISTORY_TABLE_TIME = 2;	
	
	private Context mContext;
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);	
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createBlockTable(db);
		createAllowTable(db);
		createSmsKeywordTable(db);	
		createSmsHistoryTable(db);
		createPhoneHistoryTable(db);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String path = mContext.getFilesDir().getAbsolutePath() + "/databases/"+DB_NAME;
		File file = new File(path);
		file.delete();
		onCreate(db);
	}	
	
	private void  createBlockTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+ BLOCK_TABLE+"(ID INTEGER PRIMARY KEY NOT NULL, Number TEXT NOT NULL UNIQUE)";			
		db.execSQL(sql);
	}
	
	private void  createAllowTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+ ALLOW_TABLE+"(ID INTEGER PRIMARY KEY NOT NULL, Number TEXT NOT NULL UNIQUE)";			
		db.execSQL(sql);
	}

	private void  createSmsKeywordTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+ SMSKEYWORD_TABLE+"(ID INTEGER PRIMARY KEY NOT NULL, Keyword TEXT NOT NULL UNIQUE)";			
		db.execSQL(sql);
	}
	
	private void  createSmsHistoryTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+ SMS_HISTORY_TABLE+
				"(ID INTEGER PRIMARY KEY NOT NULL, Number TEXT, Time TEXT, Content TEXT)";			
		db.execSQL(sql);
	}

	private void  createPhoneHistoryTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+ PHONE_HISTORY_TABLE+"(ID INTEGER PRIMARY KEY NOT NULL, Number TEXT, Time TEXT)";			
		db.execSQL(sql);
	}
}