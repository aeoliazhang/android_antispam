package com.alcatel.master.antispam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AntiAddFromMessageLog extends Activity  implements OnClickListener{
	public static final String KEY_FROM_ACTIVITY = "com.alcatel.master.antispam.from_activity";
	public static final String KEY_SMS_DATA = "com.alcatel.master.antispam.sms_data";
	
	private List<Map<String, Object>> m_lstData;
	
	private Button m_addBtn;
	private Button m_cancelBtn;
	private ListView m_lstView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.anti_add_from_message_history); 
        m_addBtn = (Button)findViewById(R.id.btn_add);
        m_addBtn.setOnClickListener(this);
        m_cancelBtn = (Button)findViewById(R.id.btn_cancel);
        m_cancelBtn.setOnClickListener(this);
        
        m_lstView = (ListView)findViewById(R.id.lv_sms_history_list);
        m_lstData = new ArrayList<Map<String, Object>>();
        SmsHistoryListAdapter adapter = new SmsHistoryListAdapter(this);
        m_lstView.setAdapter(adapter);
        getListData();
    }
	
	private void getListData() {
		//List<FromSmsItem> smsLst = AntiSpamConfig.getInstance(this).getFromSmsList();
		ArrayList<FromSmsItem> smsLst = this.getIntent().getParcelableArrayListExtra(KEY_SMS_DATA);
		m_lstData.clear();
		
		for(int i = 0; i < smsLst.size();i++) {
			Map<String, Object> map = new HashMap<String, Object>();		
			map.put("checked", false);
			map.put("type", smsLst.get(i).type);
			map.put("name", smsLst.get(i).name);
			map.put("number", smsLst.get(i).number);
			map.put("contentAbstract", smsLst.get(i).body);
			Date time = new Date(smsLst.get(i).time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdf.format(time);
			map.put("date", strDate);
			m_lstData.add(map);
		}
		
		setUIStatus();
		((SmsHistoryListAdapter)m_lstView.getAdapter()).notifyDataSetChanged();
	}

	public void setUIStatus() {
		int nCheckCount = 0;
		for(int i = 0;i < m_lstData.size();i++) {
			if((Boolean)m_lstData.get(i).get("checked")) {
				nCheckCount++;
			}
		}
		
		String btnText = getResources().getString(R.string.anti_add);
		if(nCheckCount == 0) {
			m_addBtn.setText(btnText);
			m_addBtn.setEnabled(false);
		}else{
			btnText = btnText + "(" + String.valueOf(nCheckCount) + ")";
			m_addBtn.setText(btnText);
			m_addBtn.setEnabled(true);
		}
	}
	
	private void saveBlackRecord() {
		ArrayList<String> setDB = AntiSpamConfig.getInstance(this).getBlockList();
		for(int i = 0;i < m_lstData.size();i++) {
			if((Boolean)m_lstData.get(i).get("checked")) {
				boolean isExist = false;
				String strNumber = (String)m_lstData.get(i).get("number");
				Iterator<String> it = setDB.iterator();
				while(it.hasNext()){
					if(strNumber != null && it.next().toString().equalsIgnoreCase(strNumber)) {
						isExist = true;
						break;
					}
				}
				if(!isExist) {
					AntiSpamConfig.getInstance(this).addBlockNumberToDB(strNumber.toString());
					Intent data=new Intent();  
					setResult(20, data);
				}
			}
		}
	}
	
	private void saveAllowRecord() {
		ArrayList<String> setDB = AntiSpamConfig.getInstance(this).getAllowList();
		for(int i = 0;i < m_lstData.size();i++) {
			if((Boolean)m_lstData.get(i).get("checked")) {
				boolean isExist = false;
				String strNumber = (String)m_lstData.get(i).get("number");
				Iterator<String> it = setDB.iterator();
				while(it.hasNext()){
					if(strNumber != null && it.next().toString().equalsIgnoreCase(strNumber)) {
						isExist = true;
						break;
					}
				}
				if(!isExist) {
					AntiSpamConfig.getInstance(this).addAllowNumberToDB(strNumber.toString());
					Intent data=new Intent();  
					setResult(30, data);
				}
			}
		}
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
    	case R.id.btn_add:
    		String from = getIntent().getStringExtra(KEY_FROM_ACTIVITY);
    		//from black list
    		if(from != null && from.equalsIgnoreCase(BlackListActivty.ACTIVITY_NAME)) {
    			saveBlackRecord();
    			finish();
    		}
    		//from allow list
    		if(from != null && from.equalsIgnoreCase(AllowListActivty.ACTIVITY_NAME)) {
    			saveAllowRecord();
    			finish();
    		}
    		break;
    	case R.id.btn_cancel:
    		finish();
    		break;
		}
	}
	
	private class SmsHistoryListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public SmsHistoryListAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return m_lstData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public CheckBox check;
			public ImageView img;
			public TextView nameNumber;
			public TextView contentAbstract;
			public TextView date;
			public RelativeLayout layout;
		}


		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.anti_from_sms_list_adapter, null);
				holder.check = (CheckBox)convertView.findViewById(R.id.checked_add);
				holder.img = (ImageView)convertView.findViewById(R.id.image_contact_type);
				holder.nameNumber = (TextView)convertView.findViewById(R.id.text_sms_name_number);
				holder.contentAbstract = (TextView)convertView.findViewById(R.id.text_sms_abstract);
				holder.date = (TextView)convertView.findViewById(R.id.text_sms_date);
				holder.layout = (RelativeLayout)convertView.findViewById(R.id.sms_adapter_layout);
				
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			Integer nType = (Integer)m_lstData.get(position).get("type");
			if(nType.intValue() == 1) {
				holder.img.setImageResource(R.drawable.icon_sms_in);
			}else if(nType.intValue() == 2){
				holder.img.setImageResource(R.drawable.icon_sms_out);
			}else{
				holder.img.setImageResource(R.drawable.icon_sms_in);
			}
			
			final String strName = (String)m_lstData.get(position).get("name");
			final String strNumber = (String)m_lstData.get(position).get("number");
			if(strName != null && strName.length() != 0) {
				String strNameNumber = strName + "(" + strNumber + ")";
				holder.nameNumber.setText(strNameNumber);
			}else{
				holder.nameNumber.setText(strNumber);
			}
			
			String strAbstract = (String)m_lstData.get(position).get("contentAbstract");
			if(strAbstract.length() > 8) {
				strAbstract = strAbstract.substring(0, 8);
			}
			holder.contentAbstract.setText(strAbstract);
			final String strDate = (String)m_lstData.get(position).get("date");
			holder.date.setText(strDate);
			
			final CheckBox checkBox =  holder.check;
			holder.layout.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Boolean bCheck = (Boolean)m_lstData.get(position).get("checked");
					checkBox.setChecked(!bCheck);
					m_lstData.get(position).put("checked",!bCheck);
					setUIStatus();
				}
			});
			
			holder.check.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					m_lstData.get(position).put("checked",((CheckBox)v).isChecked());
					setUIStatus();
				}
			});
			
			holder.check.setChecked((Boolean)m_lstData.get(position).get("checked"));
			
			return convertView;
		}
	}
}
