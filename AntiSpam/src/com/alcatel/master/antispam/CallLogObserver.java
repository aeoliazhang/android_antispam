package com.alcatel.master.antispam;

import android.database.ContentObserver;
import android.os.Handler;

class CallLogObserver extends ContentObserver {

	//
	private Handler mHandler;
	//private long mLastChangeTime = 0l;

	//
	public CallLogObserver(Handler handler) {
		super(handler);
		mHandler = handler;
	}

	@Override
	public void onChange(boolean selfChange) {
		
		//if contacts data is too large and change frequently, may be will :(
		/*if (System.currentTimeMillis() - mLastChangeTime < 60) {
			return;
		}*/
		//mLastChangeTime = System.currentTimeMillis();		
		super.onChange(selfChange);
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public boolean deliverSelfNotifications() {
		return false;
	}
}
