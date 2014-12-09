package com.alcatel.master.antispam;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class FilterService extends Service {

	@Override
	public IBinder onBind(Intent intent) {	
		return null;		
	}

	@Override
	public void onCreate() {		
		super.onCreate();
		regSmsReceiver();
		regPhoneReceiver();
		new Thread()
		{
			@Override
			public void run()
			{
				AntiSpamConfig.getInstance(getApplicationContext()).initConfig();
				Intent intent = new Intent("com.alcatel.master.antispam.INITDATA_DONE");		
				getApplicationContext().sendBroadcast(intent);
			}
		}.start();	

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);		
	}
	
	 public int onStartCommand(Intent intent, int flags, int startId) {
		return 	super.onStartCommand(intent, flags,  startId);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private void regSmsReceiver() {
		
		SmsReceiver  smsRev = new SmsReceiver();
		IntentFilter smsIntent = new IntentFilter();
		smsIntent.addAction("android.provider.Telephony.SMS_RECEIVED");
		smsIntent.setPriority(2147483647);
		getApplicationContext().registerReceiver(smsRev, smsIntent);	
	}
	
	private void regPhoneReceiver() {
	
		PhoneReceiver  phoneRev = new PhoneReceiver();
		IntentFilter phoneIntent = new IntentFilter();
		phoneIntent.addAction("android.intent.action.PHONE_STATE");
		phoneIntent.setPriority(2147483647);
		getApplicationContext().registerReceiver(phoneRev, phoneIntent);	
	}
}
