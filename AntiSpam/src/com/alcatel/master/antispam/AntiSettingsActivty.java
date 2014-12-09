package com.alcatel.master.antispam;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class AntiSettingsActivty extends Activity implements OnClickListener{
	private CheckBox m_blockCB;
	private RadioButton m_onlyBlackRB;
	private RadioButton m_onlyAllowRB;
	private RadioButton m_onlyAllowAndContactsRB;
	private RadioGroup  m_groupRG;
	private RelativeLayout m_layoutSettingEnableBlocker;
	
	private RadioButton m_radioBusyTone;
	private RadioButton m_radioEmptyNumberTone;
	private RadioButton m_radioShutDownTone;
	private RadioButton m_radioPhoneDownTone;
	private RadioGroup  m_groupTone;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_settings); 
        initViews();
        iniSettingsView();
        changeViewStaus();
    }
	 
	 @Override
	 public void onResume() {
		 super.onResume();
		 TelephonyManager tel = (TelephonyManager) this 
                 .getSystemService(Context.TELEPHONY_SERVICE); 
		 String strCountryCode = tel.getNetworkCountryIso(); 
		 //cn:china
		 if(strCountryCode == null || strCountryCode.equalsIgnoreCase("cn") == false) {
			 m_groupTone.setVisibility(View.INVISIBLE);
		 }else{
			 m_groupTone.setVisibility(View.VISIBLE);
		 }
	 }
	 
	 private void initViews() {
		 m_blockCB = (CheckBox)findViewById(R.id.checkbox_enable_blocker);
		 m_blockCB.setOnClickListener(this);
		 m_onlyBlackRB = (RadioButton)findViewById(R.id.radio_block_black_list);
		 m_onlyBlackRB.setOnClickListener(this);
		 m_onlyAllowRB = (RadioButton)findViewById(R.id.radio_accept_white_list);
		 m_onlyAllowRB.setOnClickListener(this);
		 m_onlyAllowAndContactsRB = (RadioButton)findViewById(R.id.radio_accept_white_list_contacts);
		 m_onlyAllowAndContactsRB.setOnClickListener(this);
		 m_groupRG = (RadioGroup)findViewById(R.id.radiogroup_settings_block_way);
		 m_radioBusyTone = (RadioButton)findViewById(R.id.radio_tone_busy);
		 m_radioBusyTone.setOnClickListener(this);
		 m_radioEmptyNumberTone = (RadioButton)findViewById(R.id.radio_tone_empty);
		 m_radioEmptyNumberTone.setOnClickListener(this);
		 m_radioShutDownTone = (RadioButton)findViewById(R.id.radio_tone_shut_down);
		 m_radioShutDownTone.setOnClickListener(this);
		 m_radioPhoneDownTone = (RadioButton)findViewById(R.id.radio_tone_stop);
		 m_radioPhoneDownTone.setOnClickListener(this);
		 m_groupTone = (RadioGroup)findViewById(R.id.radiogroup_settings_block_tone);
		 
		 m_layoutSettingEnableBlocker = (RelativeLayout)findViewById(R.id.layout_setting_enable_blocker);
		 m_layoutSettingEnableBlocker.setOnClickListener(this);
	 }
	 
	 private void changeViewStaus() {
		 boolean bBlockEnable = AntiSpamConfig.getInstance(this).isBlockEnable();
		 m_groupRG.setEnabled(bBlockEnable);
		 m_onlyBlackRB.setEnabled(bBlockEnable);
		 m_onlyAllowRB.setEnabled(bBlockEnable);
		 m_onlyAllowAndContactsRB.setEnabled(bBlockEnable);
		 m_groupTone.setEnabled(bBlockEnable);
		 m_radioBusyTone.setEnabled(bBlockEnable);
		 m_radioEmptyNumberTone.setEnabled(bBlockEnable);
		 m_radioShutDownTone.setEnabled(bBlockEnable);
		 m_radioPhoneDownTone.setEnabled(bBlockEnable);
	 }
	 
	 private void iniSettingsView() {
		 boolean bBlockEnable = AntiSpamConfig.getInstance(this).isBlockEnable();
		 m_blockCB.setChecked(bBlockEnable);
		 int nRuleType = AntiSpamConfig.getInstance(this).getRuleType();
		 switch(nRuleType) {
		 case AntiSpamSettings.RULE_ONLY_BLOCKLIST:
			 m_onlyBlackRB.setChecked(true);
			 break;
		 case AntiSpamSettings.RULE_ONLY_ALLOWLIST: 
			 m_onlyAllowRB.setChecked(true);
			 break;
		 case AntiSpamSettings.RULE_ONLY_CONTACTS_AND_ALLOWLIST:
			 m_onlyAllowAndContactsRB.setChecked(true);
			 break;
		 }
		 
		 int nToneType = AntiSpamConfig.getInstance(this).getBackToneMode();
		 switch(nToneType) {
		 case AntiSpamSettings.BACKTONE_MODE_BUSY:
			 m_radioBusyTone.setChecked(true);
			 break;
		 case AntiSpamSettings.BACKTONE_MODE_EMPTY:
			 m_radioEmptyNumberTone.setChecked(true);
			 break;
		 case AntiSpamSettings.BACKTONE_MODE_SHUTDOWN:
			 m_radioShutDownTone.setChecked(true);
			 break;
		 case AntiSpamSettings.BACKTONE_MODE_STOP:
			 m_radioPhoneDownTone.setChecked(true);
			 break;
		 }
	 }
	 
	 public void onClick(View v) {
	    	switch(v.getId()) {
	    	case R.id.layout_setting_enable_blocker:
	    		boolean bBlockLayout = m_blockCB.isChecked();
	    		m_blockCB.setChecked(!bBlockLayout);
	    		AntiSpamConfig.getInstance(this).setBlockEnable(!bBlockLayout);
	    		break;
	    	case R.id.checkbox_enable_blocker:
	    		boolean bBlock = m_blockCB.isChecked();
	    		AntiSpamConfig.getInstance(this).setBlockEnable(bBlock);
	    		break;
	    	case R.id.radio_block_black_list:
	    		boolean bOnlyBlack = m_onlyBlackRB.isChecked();
	    		if(bOnlyBlack)
	    			AntiSpamConfig.getInstance(this).setRuleType(AntiSpamSettings.RULE_ONLY_BLOCKLIST);
	    		break;
	    	case R.id.radio_accept_white_list:
	    		boolean bOnlyAllow = m_onlyAllowRB.isChecked();
	    		if(bOnlyAllow)
	    			AntiSpamConfig.getInstance(this).setRuleType(AntiSpamSettings.RULE_ONLY_ALLOWLIST);
	    		break;
	    	case R.id.radio_accept_white_list_contacts:
	    		boolean bOnlyAllowAndContacts = m_onlyAllowAndContactsRB.isChecked();
	    		if(bOnlyAllowAndContacts)
	    			AntiSpamConfig.getInstance(this).setRuleType(AntiSpamSettings.RULE_ONLY_CONTACTS_AND_ALLOWLIST);
	    		break;
	    	case R.id.radio_tone_busy:
	    		boolean bToneBusy = m_radioBusyTone.isChecked();
	    		if(bToneBusy)
	    			AntiSpamConfig.getInstance(this).SetBackToneMode(AntiSpamSettings.BACKTONE_MODE_BUSY);
	    		break;
	    	case R.id.radio_tone_empty:
	    		boolean bToneEmpty = m_radioEmptyNumberTone.isChecked();
	    		if(bToneEmpty)
	    			AntiSpamConfig.getInstance(this).SetBackToneMode(AntiSpamSettings.BACKTONE_MODE_EMPTY);
	    		break;
	    	case R.id.radio_tone_shut_down:
	    		boolean bToneShutDown = m_radioShutDownTone.isChecked();
	    		if(bToneShutDown)
	    			AntiSpamConfig.getInstance(this).SetBackToneMode(AntiSpamSettings.BACKTONE_MODE_SHUTDOWN);
	    		break;
	    	case R.id.radio_tone_stop:
	    		boolean bTonePhoneDown = m_radioPhoneDownTone.isChecked();
	    		if(bTonePhoneDown)
	    			AntiSpamConfig.getInstance(this).SetBackToneMode(AntiSpamSettings.BACKTONE_MODE_STOP);
	    		break;
	    	}
	    	
	    	changeViewStaus();
	 }
}