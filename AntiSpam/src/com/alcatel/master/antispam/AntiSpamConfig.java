package com.alcatel.master.antispam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AntiSpamConfig {

	//
	private static AntiSpamConfig mInstance = null;
	private AntiSpamSettings mSettings = null;
	private Database mDb = null;
	private Context mContext;
	private boolean mIsDataDone = false;
	public  boolean mIsAppRunning = false;

	// get instance
	public static AntiSpamConfig getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new AntiSpamConfig(context);

		}
		return mInstance;
	}

	public AntiSpamConfig(Context context) {
		mContext = context;
		mIsAppRunning = true;
	}

	// initialize configure include settings and data
	public void initConfig() {

		mIsDataDone = false;
		Log.e("test", "mIsDataDone:" + mIsDataDone);
		initSettings();
		initData();
		mIsDataDone = true;
		Log.e("test", "mIsDataDone:" + mIsDataDone);
	}

	public boolean isDataDone() {
		return mIsDataDone;
	}

	// settings
	private void initSettings() {

		mSettings = new AntiSpamSettings(mContext);
	}

	// data
	private void initData() {
		mDb = new Database(mContext);
	}

	// block setting
	public void setBlockEnable(boolean bEnable) {
		mSettings.setBlockEnable(bEnable);
	}

	public boolean isBlockEnable() {
		return mSettings.isBlockEnable();
	}

	// block rule setting
	public void setRuleType(int nType) {
		mSettings.setRuleType(nType);
	}

	public int getRuleType() {
		return mSettings.getRuleType();
	}

	// sms keyword setting
	public void setSmsKeywordEnable(boolean bEnable) {
		mSettings.setBlockEnable(bEnable);
	}

	public boolean isSmsKeywordEnable() {
		return mSettings.isSmsKeywordEnable();
	}

	// back tone setting
	public void SetBackToneMode(int nMode) {
		mSettings.SetBackToneMode(nMode);
	}

	public int getBackToneMode() {
		return mSettings.getBackToneMode();
	}

	// block number data
	public ArrayList<String> getBlockList() {
		return mDb.getBlockList();
	}

	public void addBlockNumberToDB(String number) {
		mDb.addBlockNumberToDB(number);
	}

	public void deleteBlockNumberFromDB(String number) {
		mDb.deleteBlockNumberFromDB(number);
	}

	public void deleteBlockNumberAll() {
		mDb.deleteBlockNumberAll();
	}

	// allow list data
	public ArrayList<String> getAllowList() {
		return mDb.getAllowList();
	}

	public void addAllowNumberToDB(String number) {
		mDb.addAllowNumberToDB(number);
	}

	public void deleteAllowNumberFromDB(String number) {
		mDb.deleteAllowNumberFromDB(number);
	}

	public void deleteAllowNumberAll() {
		mDb.deleteAllowNumberAll();
	}

	// Contacts data
	public List<ContactItem> getContactsList() {
		return mDb.getContactsList();
	}

	// sms keyword
	public Set<String> getSmsKeywordList() {
		return mDb.getSmsKeywordList();
	}

	public void addSmsKeywordToDB(String keyword) {
		mDb.addSmsKeywordToDB(keyword);
	}

	public void deleteSmsKeywordFromDB(String keyword) {
		mDb.deleteSmsKeywordFromDB(keyword);
	}

	// sms history
	public List<SmsItem> getSmsHistoryList() {

		return mDb.getSmsHistoryList();
	}

	public void addSmsHistoryItem(SmsItem item) {
		mDb.addSmsHistoryItemToDB(item);
	}

	public void clearSmsHistoyList() {
		mDb.clearSmsHistoyList();
	}

	public void deleteSmsHistoyById(int id) {
		mDb.deleteSmsHistoyById(id);
	}

	// phone history
	public List<PhoneItem> getPhoneHistoryList() {

		return mDb.getPhoneHistoryList();
	}

	public void addPhoneHistoryItem(PhoneItem item) {
		mDb.addPhoneHistoryItemToDB(item);
	}

	public void clearPhoneHistoyList() {
		mDb.clearPhoneHistoyList();
	}

	public void deletePhoneHistoyById(int id) {
		mDb.deletePhoneHistoyById(id);
	}

	// get call log list
	public List<CallLogItem> getCallLogList() {
		return mDb.getCallLogList();
	}

	// get from sms list which add to black or allow name list
	public List<FromSmsItem> getFromSmsList() {
		return mDb.getFromSmsList();
	}

	public void registerOBserver() {
		mDb.registerOBserver();
	}

	static void startFilterService(Context context) {
		Intent fltService = new Intent();
		fltService.setClass(context, FilterService.class);
		context.startService(fltService);
	}

	static boolean isServiceRunning(Context context) {
		boolean bRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(1000);
		for (RunningServiceInfo info : infos) {
			if (info.service.getClassName().equalsIgnoreCase(
					"com.alcatel.master.antispam.FilterService")) {
				bRunning = true;
				break;
			}
		}

		return bRunning;
	}
}
