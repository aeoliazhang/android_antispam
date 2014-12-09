package com.alcatel.master.antispam;


import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

public class MainActivity extends Activity implements OnClickListener{
	private TextView m_textCallCount;
	private TextView m_textSmsCount;
	private TextView m_textBlockTip;
	private CallSmsBroadcastReceiver m_callAndSmsSpamBroadcastReceiver;
	
	private class CallSmsBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {
	    	
        	if(intent.getAction().equals(SmsReceiver.MESSAGE_SMS_SPAM_ACTION) || 
        			intent.getAction().equals(PhoneReceiver.MESSAGE_CALL_SPAM_ACTION)) {  
        		setHistoryCount();
           	}  
        }  	
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_main);
        m_textCallCount = (TextView)findViewById(R.id.text_call_number);
        m_textSmsCount = (TextView)findViewById(R.id.text_sms_count);
        m_textBlockTip = (TextView)findViewById(R.id.block_tip);
        
        initView();   
        setHistoryCount();
    }
    
    @Override
	public void onResume() {
    	super.onResume();
    	setViewStatus();
    	
    	m_callAndSmsSpamBroadcastReceiver = new CallSmsBroadcastReceiver();
    	this.registerReceiver(m_callAndSmsSpamBroadcastReceiver, new  IntentFilter(SmsReceiver.MESSAGE_SMS_SPAM_ACTION));
    	this.registerReceiver(m_callAndSmsSpamBroadcastReceiver, new  IntentFilter(PhoneReceiver.MESSAGE_CALL_SPAM_ACTION));
	}
    
    public void setViewStatus() {
    	setHistoryCount();
    	boolean bBlockEnable = AntiSpamConfig.getInstance(this).isBlockEnable();
    	int nBlockTipId = R.string.anti_block_disabled;
    	if(bBlockEnable) {
    		int nRuleType = AntiSpamConfig.getInstance(this).getRuleType();
	   		switch(nRuleType) {
	   		case AntiSpamSettings.RULE_ONLY_BLOCKLIST:
	   			nBlockTipId =  R.string.anti_blocked_list_enable;
	   			 break;
	   		 case AntiSpamSettings.RULE_ONLY_ALLOWLIST: 
	   			nBlockTipId =  R.string.anti_allowed_list_enable;
	   			break;
	   		 case AntiSpamSettings.RULE_ONLY_CONTACTS_AND_ALLOWLIST:
	   			nBlockTipId =  R.string.anti_allowed_list_and_contacts_enable;
	   			break;
	   		 }
   		 
    	}
    	m_textBlockTip.setText(nBlockTipId);
    }
    
    @Override
	public void onPause() {
    	super.onPause();
		this.unregisterReceiver(m_callAndSmsSpamBroadcastReceiver);
	}
    
	private void initView() {
		//set up click listeners for all the items
        View item1 = findViewById(R.id.layout_block_history);
        item1.setOnClickListener(this);
        View item2 = findViewById(R.id.layout_black_list);
        item2.setOnClickListener(this);
        View item3 = findViewById(R.id.layout_white_list);
        item3.setOnClickListener(this);
        View item4 = findViewById(R.id.layout_settings);
        item4.setOnClickListener(this);
	}
	
	public void setHistoryCount() {
		int nCallHistory = AntiSpamConfig.getInstance(this).getPhoneHistoryList().size();
		int nSmsHistory = AntiSpamConfig.getInstance(this).getSmsHistoryList().size();
		m_textCallCount.setText(String.valueOf(nCallHistory));
		m_textSmsCount.setText(String.valueOf(nSmsHistory));
	}
	
	public void onClick(View v) {
    	switch(v.getId()) {
    	case R.id.layout_block_history:
    		Intent intentBlockHistory = new Intent(this,BlockHistoryActivty.class);
    		startActivity(intentBlockHistory);
    		break;
    	case R.id.layout_black_list:
    		Intent intentBlackList = new Intent(this,BlackListActivty.class);
    		startActivity(intentBlackList);
    		break;
    	case R.id.layout_white_list:
    		Intent intentAllowList = new Intent(this,AllowListActivty.class);
    		startActivity(intentAllowList);
    		break;
    	case R.id.layout_settings:
    		Intent intentSettings = new Intent(this,AntiSettingsActivty.class);
    		startActivity(intentSettings);
    		break;
    	}
    }
}
