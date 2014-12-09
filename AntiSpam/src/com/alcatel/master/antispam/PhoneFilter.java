package com.alcatel.master.antispam;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;

public class PhoneFilter extends Filter {
	AntiSpamConfig mConfig;

	public PhoneFilter(Context context) {
		super(context);
		mConfig = AntiSpamConfig.getInstance(context);
	}

	public boolean filterPhone(PhoneItem item) {
		boolean bBlocked = false;
		if (isFilterNumber(item.number)) {
			bBlocked = true;
		}

		return bBlocked;
	}

	public void saveFilterInfo(PhoneItem item) {
		mConfig.addPhoneHistoryItem(item);
	}

	public void deleteCallLog(Context context, String number) {
		
		DeleteCallLogRunnable run = new DeleteCallLogRunnable(context, number);
		new Handler().postDelayed(run, 2000);
	}

	private class DeleteCallLogRunnable extends Thread {

		private Context m_context;
		private String m_number;

		public DeleteCallLogRunnable(Context context, String number) {
			m_context = context;
			m_number = number;
		}

		@Override
		public void run() {

			try {

				Cursor cursor = m_context.getContentResolver().query(
						CallLog.Calls.CONTENT_URI,
						new String[] { "_id", "number", "type" }, null, null,
						null);
				if (cursor != null && cursor.moveToLast()) {
					String id = cursor.getString(0);
					String numberCurr = cursor.getString(1);
					int type = cursor.getInt(2);
					if (numberCurr.equals(m_number)
							&& type == CallLog.Calls.INCOMING_TYPE) {
						m_context.getContentResolver().delete(
								CallLog.Calls.CONTENT_URI, "_id=" + id, null);
					}
					cursor.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
