package com.alcatel.master.antispam;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	public static final String MESSAGE_SMS_SPAM_ACTION = "com.alcatel.master.antispam.action.smsspam";
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {	
			AntiSpamConfig config = AntiSpamConfig.getInstance(context);
			if(config.isBlockEnable())
			{
				handleSmsReceived(context, intent);	
			}
		}
	}
	
	public void handleSmsReceived(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage smsMessages[] = getSmsMessage(bundle);
		SmsItem item = new SmsItem();
		StringBuilder sb = new StringBuilder();
		for(SmsMessage sms: smsMessages)
		{		
			item.number = sms.getDisplayOriginatingAddress();				
			sb.append(sms.getMessageBody());
			Date date = new Date(sms.getTimestampMillis());
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			item.time = format.format(date);				
		}
		
		item.content = sb.toString();
		SmsFilter smsFilter = new SmsFilter(context);
		
		if(smsFilter.filterSms(item))
		{
			abortBroadcast();	
			smsFilter.saveFilterInfo(item);	
			smsFilter.showNotification(BlockHistoryActivty.HISTORY_TYPE_SMS);

			Intent megIntent= new Intent(MESSAGE_SMS_SPAM_ACTION);
			context.sendBroadcast(megIntent);
		}
	}

	private SmsMessage[] getSmsMessage(Bundle bundle) {
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessages[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessages[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		return smsMessages;
	}
}
