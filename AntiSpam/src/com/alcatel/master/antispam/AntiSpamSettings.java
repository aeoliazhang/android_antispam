package com.alcatel.master.antispam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

public class AntiSpamSettings {
	
	private static final String SETTING_FILE = "AntiSpam";
	private static final String ITEM_BLOCK_ENABLE = "Block";
	private static final String ITEM_BLOCK_RULE = "Rule";
	private static final String ITEM_SMSKEYWORD_ENALBE = "Sms Keyword";
	private static final String ITEM_BACKTONE_MODE = "Back tone";
	
	public static final int RULE_ONLY_BLOCKLIST = 0;
	public static final int RULE_ONLY_ALLOWLIST = 1;
	public static final int RULE_ONLY_CONTACTS_AND_ALLOWLIST = 2;
	
	public static final int BACKTONE_MODE_BUSY = 0;
	public static final int BACKTONE_MODE_EMPTY = 1;
	public static final int BACKTONE_MODE_SHUTDOWN = 2;
	public static final int BACKTONE_MODE_STOP = 3;
	
	//遇忙转移号码	
	private static final String[] SPECIAL_NUMBER_LIST = {		
			//取消
			"%23%2367%23", 
			//空号
			"**67*13800000000%23", 
			//关机
			"**67*13810538911%23",
			//停机
			"**67*13701110216%23" };
	
	
	private Context mContext = null;
	private boolean mIsBlockEnable = true;
	private int     mRuleType = RULE_ONLY_BLOCKLIST;
	private boolean mIsSmsKeywordEnable = true;
	private int     mBacktoneMode = BACKTONE_MODE_BUSY;
	
	public AntiSpamSettings(Context context)
	{
		mContext = context;
		loadSettings();
	}
	
	public void setBlockEnable(boolean bEnable)
	{
		mIsBlockEnable = bEnable;
		saveSettings();
	}
	
	public boolean isBlockEnable () {
		return mIsBlockEnable;
	}
	
	public void setRuleType(int  nType)
	{
		mRuleType = nType;
		saveSettings();
	}
	
	public int getRuleType () {
		return mRuleType;
	}	
	
	public void setSmsKeywordEnable(boolean bEnable)
	{
		mIsSmsKeywordEnable = bEnable;
		saveSettings();
	}
	
	public boolean isSmsKeywordEnable () {
		return mIsSmsKeywordEnable;
	}
	
	public void SetBackToneMode(int nMode)
	{
		mBacktoneMode = nMode;
		setBackToneInstruction(nMode);
	}
	
	public int getBackToneMode()
	{
		return mBacktoneMode;
	}
	
	private void setBackToneInstruction(int nMode) {
		Intent it = new Intent(Intent.ACTION_CALL);
		it.setData(Uri.parse("tel:"+SPECIAL_NUMBER_LIST[nMode]));
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(it);
	}
	
	private void loadSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
		mIsBlockEnable = sp.getBoolean(ITEM_BLOCK_ENABLE, true);
		mRuleType = sp.getInt(ITEM_BLOCK_RULE, RULE_ONLY_BLOCKLIST);
		mIsSmsKeywordEnable = sp.getBoolean(ITEM_SMSKEYWORD_ENALBE, true);
		mBacktoneMode = sp.getInt(ITEM_BACKTONE_MODE, BACKTONE_MODE_BUSY);
	}
	
	private void saveSettings()
	{
		SharedPreferences sp = mContext.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
		Editor edt = sp.edit();
		edt.putBoolean(ITEM_BLOCK_ENABLE, mIsBlockEnable);
		edt.putInt(ITEM_BLOCK_RULE, mRuleType);
		edt.putBoolean(ITEM_SMSKEYWORD_ENALBE, mIsSmsKeywordEnable);
		edt.putInt(ITEM_BACKTONE_MODE, mBacktoneMode);
		edt.commit();	
	}
	
}
