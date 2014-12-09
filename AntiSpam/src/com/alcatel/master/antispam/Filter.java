package com.alcatel.master.antispam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Filter {

	private Context mContext = null;
	AntiSpamConfig mConfig = null;

	public Filter(Context context) {
		mContext = context;
		mConfig = AntiSpamConfig.getInstance(context);
	}

	@SuppressWarnings("deprecation")
	public void showNotification(int nBlockType) {

		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);	
		
		Intent it = new Intent(mContext, BlockHistoryActivty.class);
		it.putExtra(BlockHistoryActivty.HISTORY_TYPE_STRING, nBlockType);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				it, PendingIntent.FLAG_ONE_SHOT);		
	
		String msgRes = mContext.getString(R.string.anti_notifaction_msg);  

		String msg = String.format(msgRes,
				mConfig.getSmsHistoryList().size(), mConfig
						.getPhoneHistoryList().size());

		Notification n = new Notification();
		n.icon = R.drawable.ic_launcher;
		n.tickerText = msg;
		n.when = System.currentTimeMillis();
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		String title = mContext.getString(R.string.app_name);
		n.setLatestEventInfo(mContext, title, msg, contentIntent);

		// no need sound
		// n.defaults = Notification.DEFAULT_SOUND;
		nm.notify(0, n);

	}
	
	public boolean isFilterNumber(String number) {
		boolean bFilter = false;
		switch (mConfig.getRuleType()) {
		case AntiSpamSettings.RULE_ONLY_BLOCKLIST:
			bFilter = isBlockList(number);
			break;

		case AntiSpamSettings.RULE_ONLY_ALLOWLIST:
			bFilter = !isAllowList(number);
			break;

		case AntiSpamSettings.RULE_ONLY_CONTACTS_AND_ALLOWLIST:
			bFilter = !(isContactsList(number) || isAllowList(number));
			break;

		default:
			break;

		}
		return bFilter;
	}

	public boolean isBlockList(String number) {
		boolean bIsInBlockList = false;

		ArrayList<String> list = mConfig.getBlockList();

		for (String blockNumber : list) {
				if(isNumberMatched(number, blockNumber))
				{
					bIsInBlockList = true;
					break;
				}
			}	
	
		return bIsInBlockList;
	}

	public boolean isAllowList(String number) {
		boolean bIsAllowList = false;
		ArrayList<String> list = mConfig.getAllowList();
		for (String allowNumber : list) {
			if(isNumberMatched(number, allowNumber))
			{
				bIsAllowList = true;
				break;
			}
		}
		return bIsAllowList;
	}

	public boolean isContactsList(String number) {
		boolean bIsContactsList = false;
		List<ContactItem> list = mConfig.getContactsList();
		for (ContactItem contactNumber : list) {
			
			if(isNumberMatched(number, contactNumber.number))
			{
				bIsContactsList = true;
				break;
			}			
		}
		return bIsContactsList;
	}

	public boolean isFilterKeyword(String content) {
		boolean bFilter = false;
		Set<String> list = mConfig.getSmsKeywordList();
		for (String keyword : list) {
			keyword.toLowerCase();
			content.toLowerCase();
			if (content.contains(keyword)) {
				bFilter = true;
				break;
			}
		}
		return bFilter;
	}	
	
	private boolean isNumberMatched(String src, String dst)
	{
		boolean bMatched = false;	
		
		if(src.equalsIgnoreCase(dst))
		{
			bMatched = true;
		}
		else
		{
			String newSrc = removeCountryCode(src);
			String newDst = removeCountryCode(dst);		
			
			if( newSrc.equalsIgnoreCase(newDst) )					
			{
				bMatched = true;
			}	
		}
	
		return bMatched;
	}
	
	private String removeCountryCode(String number)
	{
		String result;
		if (number.startsWith("+86"))
		{
			result = number.substring(3, number.length());
		}
		else if(number.startsWith("0086"))
		{
			result = number.substring(4, number.length());
		}
		else
		{
			result = number;
		}
		return result;
		
	}
}
