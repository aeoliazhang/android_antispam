package com.alcatel.master.antispam;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;

public class AppLoadingActivity extends Activity {

	AnimationDrawable m_ad;
	Handler m_handler = null;
	InitDataReceiver mDataRev = new InitDataReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_loading);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this,
				R.anim.page_loading_anim);
		ImageView loadingImage = (ImageView) this
				.findViewById(R.id.loading_image);
		loadingImage.startAnimation(hyperspaceJumpAnimation);
		initApp();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!AntiSpamConfig.getInstance(getApplicationContext()).isDataDone()) {
			unregisterReceiver(mDataRev);
		}

	}

	private void initApp() {

		if (AntiSpamConfig.isServiceRunning(getApplicationContext())
				&& AntiSpamConfig.getInstance(getApplicationContext())
						.isDataDone()) {
			Log.e("test", "OK");
			startMainActivity();
		} else {
			
			this.registerReceiver(mDataRev, new IntentFilter(
					"com.alcatel.master.antispam.INITDATA_DONE"));
			AntiSpamConfig.startFilterService(getApplicationContext());
		}

	}

	public class InitDataReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"com.alcatel.master.antispam.INITDATA_DONE")) {
				startMainActivity();
				unregisterReceiver(mDataRev);
			}
		}
	}

	private void startMainActivity() {
		AntiSpamConfig.getInstance(getApplicationContext()).registerOBserver();
		Intent mainIntent = new Intent(this, MainActivity.class);
		this.startActivity(mainIntent);
		finish();
	}
}
