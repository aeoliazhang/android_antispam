package com.alcatel.master.antispam;

import android.app.Application;

public class AntiSpamApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());	
	}
}