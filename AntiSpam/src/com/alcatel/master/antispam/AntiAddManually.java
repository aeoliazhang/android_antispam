package com.alcatel.master.antispam;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AntiAddManually extends Activity  implements OnClickListener, TextWatcher{

	public static final String KEY_FROM_ACTIVITY = "com.alcatel.master.antispam.from_activity";
	
	private Button m_saveBtn;
	private Button m_cancelBtn;
	private EditText m_numberEdit;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.anti_add_manually); 
        m_saveBtn = (Button)findViewById(R.id.btn_save);
        m_saveBtn.setOnClickListener(this);
        m_cancelBtn = (Button)findViewById(R.id.btn_cancel);
        m_cancelBtn.setOnClickListener(this);
        m_numberEdit = (EditText)findViewById(R.id.edit_number);
        m_numberEdit.addTextChangedListener(this);
        setUIStatus();
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
    	case R.id.btn_save:
    		String from = getIntent().getStringExtra(KEY_FROM_ACTIVITY);
    		//from black list
    		if(from.equalsIgnoreCase(BlackListActivty.ACTIVITY_NAME)) {
    			if(saveBlackRecord()) {
    				finish();
    			}else{
    				Toast.makeText(getApplicationContext(), 
    						getResources().getString(R.string.anti_add_failed), 
    						Toast.LENGTH_SHORT).show();
    			}
    		}
    		//from allow list
    		if(from.equalsIgnoreCase(AllowListActivty.ACTIVITY_NAME)) {
    			if(saveAllowRecord()) {
    				finish();
    			}else{
    				Toast.makeText(getApplicationContext(), 
    						getResources().getString(R.string.anti_add_failed), 
    						Toast.LENGTH_SHORT).show();
    			}
    		}
    		break;
    	case R.id.btn_cancel:
    		finish();
    		break;
		}
	}
	
	private boolean saveAllowRecord() {
		String strNumber = m_numberEdit.getText().toString();
		ArrayList<String> setDB = AntiSpamConfig.getInstance(this).getAllowList();
		boolean isExist = false;
		Iterator<String> it = setDB.iterator();
		while(it.hasNext()){
			if(it.next().toString().equalsIgnoreCase(strNumber)) {
				isExist = true;
				break;
			}
		}
		if(!isExist) {
			AntiSpamConfig.getInstance(this).addAllowNumberToDB(strNumber.toString());
			Intent data=new Intent();  
			setResult(30, data);
			return true;
		}else{
			return false;
		}
	}
	
	private boolean saveBlackRecord() {
		String strNumber = m_numberEdit.getText().toString();
		ArrayList<String> setDB = AntiSpamConfig.getInstance(this).getBlockList();
		boolean isExist = false;
		Iterator<String> it = setDB.iterator();
		while(it.hasNext()){
			if(it.next().toString().equalsIgnoreCase(strNumber)) {
				isExist = true;
				break;
			}
		}
		if(!isExist) {
			AntiSpamConfig.getInstance(this).addBlockNumberToDB(strNumber.toString());
			Intent data=new Intent();  
			setResult(20, data);
			return true;
		}else{
			return false;
		}
	}
	
	public void setUIStatus() {
		Editable s = m_numberEdit.getText();
		m_saveBtn.setEnabled(s.length() > 0?true:false);
	}

	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		setUIStatus();
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
