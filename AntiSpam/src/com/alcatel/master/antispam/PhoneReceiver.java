package com.alcatel.master.antispam;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import com.android.internal.telephony.ITelephony;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneReceiver extends BroadcastReceiver {
	public static final String MESSAGE_CALL_SPAM_ACTION = "com.alcatel.master.antispam.action.callspam";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {

			AntiSpamConfig config = AntiSpamConfig.getInstance(context);
			if (config.isBlockEnable()) {
				handlePhoneReceived(context, intent);
			}
		}
	}

	public void handlePhoneReceived(Context context, Intent intent) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Service.TELEPHONY_SERVICE);

		// incoming calls
		if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {

			PhoneItem item = new PhoneItem();
			item.number = intent.getStringExtra("incoming_number");
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			item.time = format.format(System.currentTimeMillis());

			PhoneFilter phoneFilter = new PhoneFilter(context);
			if (phoneFilter.filterPhone(item)) {
				if (endCall(context, tm)) {
					phoneFilter.saveFilterInfo(item);
					phoneFilter.showNotification(BlockHistoryActivty.HISTORY_TYPE_CALL);					
				
					phoneFilter.deleteCallLog(context, item.number);
					
					Intent megIntent= new Intent(MESSAGE_CALL_SPAM_ACTION);
					context.sendBroadcast(megIntent);
				}
			}
		}
	}

	private boolean endCall(Context context, TelephonyManager tm) {
		boolean bSucess = true;
	/*	AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int nOldMode = am.getRingerMode();
		if (nOldMode != AudioManager.RINGER_MODE_SILENT) {
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
*/
		Class<TelephonyManager> c = TelephonyManager.class;
		Method mthEndCall = null;
		try {
			mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
			mthEndCall.setAccessible(true);
			ITelephony iTel = (ITelephony) mthEndCall.invoke(tm,
					(Object[]) null);
			iTel.endCall();

		} catch (Exception e) {
			bSucess = false;
			e.printStackTrace();
		} finally {
			//am.setRingerMode(nOldMode);
		}
		return bSucess;
	}
}
