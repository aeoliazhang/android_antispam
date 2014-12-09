package com.alcatel.master.antispam;

import android.content.Context;


public class SmsFilter extends Filter {
	
	AntiSpamConfig mConfig;

	public SmsFilter(Context context) {
		super(context);
		mConfig = AntiSpamConfig.getInstance(context);
	}

	public boolean filterSms(SmsItem item) {
		boolean bBlocked = false;		
		if(isFilterNumber(item.number) ||
				mConfig.isSmsKeywordEnable() && isFilterKeyword(item.content) )
		{
			bBlocked = true;	
		}
	
		return bBlocked;
	}	
	
	public void saveFilterInfo(SmsItem item) {
		mConfig.addSmsHistoryItem(item);	
	}
}