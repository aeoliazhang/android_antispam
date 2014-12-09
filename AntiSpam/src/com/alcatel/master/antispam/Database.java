package com.alcatel.master.antispam;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class Database {
	//private final String SMS_URI_ALL   = "content://mms-sms/conversations"; 
	private final String SMS_URI_INBOX   = "content://sms/inbox";
	
	private Context mContext = null;
	private DatabaseHelper mDbhelper = null;

	private ArrayList<String> mBlockList = new ArrayList<String>();
	private ArrayList<String> mAllowList = new ArrayList<String>();
	private List<ContactItem> mContactsList = new ArrayList<ContactItem>();
	private Set<String> mSmsKeywordList = new HashSet<String>();
	private List<SmsItem> mSmsHistoryList = new ArrayList<SmsItem>();
	private List<PhoneItem> mPhoneHistoryList = new ArrayList<PhoneItem>();
	private List<CallLogItem> mCallLogList = new ArrayList<CallLogItem>();
	private List<FromSmsItem> mFromSmsList = new ArrayList<FromSmsItem>();

	public Database(Context context) {
		mContext = context;
		mDbhelper = new DatabaseHelper(context);
		/*registeContactsObserver();
		registCallLogObserver();
		registReceiveSmsObserver();*/
		//loadCallLogList();
		//loadSmsList();
		loadBlockList();
		loadAllowList();
		loadSmsKeywordList();	
		loadContactsList();
		loadSmsHistoryList();
		loadPhoneHistoryList();
	}
	
	public void registerOBserver() {
		registeContactsObserver();
		//registCallLogObserver();
		//registReceiveSmsObserver();
	}

	// block data
	public ArrayList<String> getBlockList() {
		return mBlockList;
	}

	public void addBlockNumberToDB(String number) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"insert into %s([Number]) values('%s');",
					DatabaseHelper.BLOCK_TABLE, number);
			db.execSQL(strSql);

			mBlockList.add(number);

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public void deleteBlockNumberAll() {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"delete from %s;",
					DatabaseHelper.BLOCK_TABLE);
			db.execSQL(strSql);

			mBlockList.clear();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void deleteBlockNumberFromDB(String number) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"delete from %s where Number = '%s';",
					DatabaseHelper.BLOCK_TABLE, number);
			db.execSQL(strSql);

			mBlockList.remove(number);

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	// allow data
	public ArrayList<String> getAllowList() {
		return mAllowList;
	}

	public void addAllowNumberToDB(String number) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();
			String strSql = String.format(
					"insert into %s([Number]) values('%s');",
					DatabaseHelper.ALLOW_TABLE, number);
			db.execSQL(strSql);
			mAllowList.add(number);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public void deleteAllowNumberAll() {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"delete from %s",
					DatabaseHelper.ALLOW_TABLE);
			db.execSQL(strSql);

			mAllowList.clear();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void deleteAllowNumberFromDB(String number) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"delete from %s where Number = '%s';",
					DatabaseHelper.ALLOW_TABLE, number);
			db.execSQL(strSql);

			mAllowList.remove(number);

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	// contacts data
	public List<ContactItem> getContactsList() {

		return mContactsList;
	}
	
	// Get call log list
	public List<CallLogItem> getCallLogList()
	{
		/*if(mCallLogList.size() == 0) {
			loadCallLogList();
		}*/
		
		loadCallLogList();
			
		return mCallLogList;
	}
	
	//get from sms list which add to black or allow name list 
	public List<FromSmsItem> getFromSmsList()
	{
		/*if(mFromSmsList.size() == 0) {
			loadSmsList();
		}*/
		loadSmsList();
		
		return mFromSmsList;
	}
	
	// sms keyword
	public Set<String> getSmsKeywordList() {
		return mSmsKeywordList;
	}

	public void addSmsKeywordToDB(String keyword) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();
			String strSql = String.format(
					"insert into %s([Keyword]) values('%s');",
					DatabaseHelper.SMSKEYWORD_TABLE, keyword);
			db.execSQL(strSql);
			mSmsKeywordList.add(keyword);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void deleteSmsKeywordFromDB(String keyword) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"delete from %s where Number = '%s';",
					DatabaseHelper.SMSKEYWORD_TABLE, keyword);
			db.execSQL(strSql);

			mSmsKeywordList.remove(keyword);

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	// sms history
	public List<SmsItem> getSmsHistoryList() {

		return mSmsHistoryList;
	}

	public void addSmsHistoryItemToDB(SmsItem item) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String
					.format("insert into %s([number],[Time],[Content]) values('%s', '%s','%s');",
							DatabaseHelper.SMS_HISTORY_TABLE, item.number,
							item.time, item.content);
			db.execSQL(strSql);

			mSmsHistoryList.add(item);

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void clearSmsHistoyList() {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();
			String strSql = String.format("delete from %s;",
					DatabaseHelper.SMS_HISTORY_TABLE);
			db.execSQL(strSql);
			mSmsHistoryList.clear();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public void deleteSmsHistoyById(int id) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();
			String strSql = String.format("delete from %s where ID == %d;",
					DatabaseHelper.SMS_HISTORY_TABLE,id);
			db.execSQL(strSql);
			for(int i = 0;i < mSmsHistoryList.size();i++) {
				if(mSmsHistoryList.get(i).id == id) {
					mSmsHistoryList.remove(i);
					break;
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	// Phone history
	public List<PhoneItem> getPhoneHistoryList() {
		return mPhoneHistoryList;
	}

	public void addPhoneHistoryItemToDB(PhoneItem item) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();

			String strSql = String.format(
					"insert into %s([Number],[Time]) values('%s', '%s');",
					DatabaseHelper.PHONE_HISTORY_TABLE, item.number, item.time);
			db.execSQL(strSql);

			this.mPhoneHistoryList.add(item);

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void clearPhoneHistoyList() {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();
			String strSql = String.format("delete from %s;",
					DatabaseHelper.PHONE_HISTORY_TABLE);
			db.execSQL(strSql);
			mPhoneHistoryList.clear();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public void deletePhoneHistoyById(int id) {
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getWritableDatabase();
			String strSql = String.format("delete from %s where ID == %d;",
					DatabaseHelper.PHONE_HISTORY_TABLE,id);
			db.execSQL(strSql);
			for(int i = 0;i < mPhoneHistoryList.size();i++) {
				if(mPhoneHistoryList.get(i).id == id) {
					mPhoneHistoryList.remove(i);
					break;
				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	// load data
	private void loadBlockList() {
		mBlockList.clear();
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getReadableDatabase();
			String strSql = "select * from " + DatabaseHelper.BLOCK_TABLE;
			Cursor cursor = db.rawQuery(strSql, null);
			while (cursor != null && cursor.moveToNext()) {
				String number = cursor
						.getString(DatabaseHelper.BLOCK_TABLE_NUMBER);
				mBlockList.add(number);
			}
			cursor.close();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void loadAllowList() {
		mAllowList.clear();
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getReadableDatabase();
			String strSql = "select * from " + DatabaseHelper.ALLOW_TABLE;
			Cursor cursor = db.rawQuery(strSql, null);
			while (cursor != null && cursor.moveToNext()) {
				String number = cursor
						.getString(DatabaseHelper.ALLOW_TABLE_NUMBER);
				mAllowList.add(number);
			}
			cursor.close();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void loadSmsKeywordList() {
		mSmsKeywordList.clear();
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getReadableDatabase();
			String strSql = "select * from " + DatabaseHelper.SMSKEYWORD_TABLE;
			Cursor cursor = db.rawQuery(strSql, null);
			while (cursor != null && cursor.moveToNext()) {
				String keyword = cursor
						.getString(DatabaseHelper.SMSKEYWORD_TABLE_KEYWORD);
				mSmsKeywordList.add(keyword);
			}
			cursor.close();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private void registeContactsObserver()
	{
		Handler handler= new Handler() {
			public void handleMessage(Message msg) {			
				loadContactsList();
			}
		};
		
		mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,
	            true, new ContactsObserver(handler));	
	}
	 
	@SuppressLint("HandlerLeak")
	private void registReceiveSmsObserver() {
		Handler handler= new Handler() {
			public void handleMessage(Message msg) {			
				loadSmsList();
			}
		};
		
		//Uri uri = Uri.parse(SMS_URI_INBOX);
		Uri uri = Uri.parse("content://sms");
		
		mContext.getContentResolver().registerContentObserver(uri,
	            true, new SmsObserver(handler));
	}
	
	@SuppressLint("HandlerLeak")
	private void registCallLogObserver() {
		Handler handler= new Handler() {
			public void handleMessage(Message msg) {			
				loadCallLogList();
			}
		};
		
		mContext.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
	            true, new CallLogObserver(handler));
	}
	
	private String matchContactName(String strNumber) {
		String strName = null;
		try {
			Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,strNumber);
		    Cursor localCursor = mContext.getContentResolver().query(personUri, new String[] {PhoneLookup.DISPLAY_NAME }, null, null, null);
		    
		    if (localCursor.getCount()!=0) {
		    	localCursor.moveToFirst();
		    	strName = localCursor.getString(localCursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		    }         
		    localCursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return strName;
	}
	
	private void loadSmsList() {    
	    String[] projection = new String[]{ "address",    
                "body", "date", "type"};
	    mFromSmsList.clear();
	    
		try {
			Uri uri = Uri.parse(SMS_URI_INBOX);
			Cursor cursor = mContext.getContentResolver().query(uri,
					projection, 
					null, 
					null, 
					"date desc");
			   
            int phoneNumberColumn = cursor.getColumnIndex("address");   
            int smsbodyColumn = cursor.getColumnIndex("body");   
            int dateColumn = cursor.getColumnIndex("date");   
            int typeColumn = cursor.getColumnIndex("type");
			
			while(cursor.moveToNext()) {
				FromSmsItem item = new FromSmsItem();
				item.number = cursor.getString(phoneNumberColumn);
				item.name = matchContactName(item.number);
				item.type = cursor.getInt(typeColumn);
				item.time = cursor.getLong(dateColumn);
				item.body = cursor.getString(smsbodyColumn);
				if(item.number == null || 
						item.number.trim().length() == 0  ) {
					continue;
				}
				if( item.type != 1 && item.type != 2) {
					continue;
				}
				item.number = item.number.replaceAll("[(,), ,-]", "");
				boolean bExist = false;
				for(int i = 0;i < mFromSmsList.size();i++) {
					if((item.name!=null && item.name.equalsIgnoreCase(mFromSmsList.get(i).name) ||
							(item.name == null && mFromSmsList.get(i).name == null))&&
							(item.number!=null && item.number.equalsIgnoreCase(mFromSmsList.get(i).number) ||
							(item.number == null && mFromSmsList.get(i).number == null))) {
						bExist = true;
						if(item.time > mFromSmsList.get(i).time) {
							mFromSmsList.remove(i);
							mFromSmsList.add(item);
						}
						break;
					}
				}
				if(bExist == false) {
					mFromSmsList.add(item);
				}
			}
			cursor.close();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadCallLogList() {
		mCallLogList.clear();
		try {
			Cursor cursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
					new String[] { CallLog.Calls.CACHED_NAME,
					CallLog.Calls.NUMBER,
					CallLog.Calls.TYPE,
					CallLog.Calls.DATE}, 
					null, 
					null, 
					CallLog.Calls.DATE);
			
			while(cursor.moveToNext()) {
				CallLogItem item = new CallLogItem();
				item.name = cursor.getString(0);
				item.number = cursor.getString(1);
				item.type = cursor.getInt(2);
				item.time = cursor.getLong(3);
				if(item.number == null || 
						item.number.trim().length() == 0  ) {
					continue;
				}
				item.number = item.number.trim();
				if(item.number.indexOf('-') == 0) {
					continue;
				}
				item.number = item.number.replaceAll("[(,), ,-]", "");
				boolean bExist = false;
				for(int i = 0;i < mCallLogList.size();i++) {
					if((item.name!=null && item.name.equalsIgnoreCase(mCallLogList.get(i).name) ||
							(item.name == null && mCallLogList.get(i).name == null))&&
							(item.number!=null && item.number.equalsIgnoreCase(mCallLogList.get(i).number) ||
							(item.number == null && mCallLogList.get(i).number == null))) {
						bExist = true;
						if(item.time > mCallLogList.get(i).time) {
							mCallLogList.remove(i);
							mCallLogList.add(item);
						}
						break;
					}
				}
				if(bExist == false) {
					mCallLogList.add(item);
				}
			}
			cursor.close();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadContactsList() {	
		mContactsList.clear();
		try
		{
	
		Cursor cursor = mContext.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		int contactIdIndex = 0;

		if (cursor.getCount() > 0) {
			contactIdIndex = cursor
					.getColumnIndex(ContactsContract.Contacts._ID);
		}
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(contactIdIndex);
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

			Cursor phones = mContext.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			int phoneIndex = 0;
			if (phones.getCount() > 0) {
				phoneIndex = phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			}
			while (phones.moveToNext()) {
				String number = phones.getString(phoneIndex);
				number = number.replaceAll("[(,), ,-]", "");
				if(number == null || number.trim().length() == 0  ) {
					continue;
				}
				
				ContactItem item = new ContactItem();
				item.name = name;
				item.number = number;
				
				mContactsList.add(item);

			}
			phones.close();
		}
		cursor.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	

	private void loadSmsHistoryList() {
		mSmsHistoryList.clear();
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getReadableDatabase();
			String strSql = "select * from " + DatabaseHelper.SMS_HISTORY_TABLE;
			Cursor cursor = db.rawQuery(strSql, null);
			while (cursor != null && cursor.moveToNext()) {
				SmsItem item = new SmsItem();
				item.id = cursor
						.getInt(DatabaseHelper.SMS_HISTORY_TABLE_ID);
				item.number = cursor
						.getString(DatabaseHelper.SMS_HISTORY_TABLE_NUMBER);
				item.time = cursor
						.getString(DatabaseHelper.SMS_HISTORY_TABLE_TIME);
				item.content = cursor
						.getString(DatabaseHelper.SMS_HISTORY_TABLE_CONTENT);
				mSmsHistoryList.add(item);
			}
			cursor.close();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void loadPhoneHistoryList() {
		mPhoneHistoryList.clear();
		SQLiteDatabase db = null;
		try {
			db = mDbhelper.getReadableDatabase();
			String strSql = "select * from "
					+ DatabaseHelper.PHONE_HISTORY_TABLE;
			Cursor cursor = db.rawQuery(strSql, null);
			while (cursor != null && cursor.moveToNext()) {
				PhoneItem item = new PhoneItem();
				item.id = cursor
						.getInt(DatabaseHelper.PHONE_HISTORY_TABLE_ID);
				item.number = cursor
						.getString(DatabaseHelper.PHONE_HISTORY_TABLE_NUMBER);
				item.time = cursor
						.getString(DatabaseHelper.PHONE_HISTORY_TABLE_TIME);
				mPhoneHistoryList.add(item);
			}
			cursor.close();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
}
