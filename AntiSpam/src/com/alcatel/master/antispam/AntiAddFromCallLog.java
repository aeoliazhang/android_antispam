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
import android.provider.CallLog;
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

public class AntiAddFromCallLog extends Activity  implements OnClickListener{
	public static final String KEY_FROM_ACTIVITY = "com.alcatel.master.antispam.from_activity";
	public static final String KEY_CALL_LOG_DATA = "com.alcatel.master.antispam.call_log_data";
	private List<Map<String, Object>> m_lstData;
	
	private Button m_addBtn;
	private Button m_cancelBtn;
	private ListView m_lstView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.anti_add_from_call_history); 
        m_addBtn = (Button)findViewById(R.id.btn_add);
        m_addBtn.setOnClickListener(this);
        m_cancelBtn = (Button)findViewById(R.id.btn_cancel);
        m_cancelBtn.setOnClickListener(this);
        
        m_lstView = (ListView)findViewById(R.id.lv_call_history_list);
        m_lstData = new ArrayList<Map<String, Object>>();
        CallLogListAdapter adapter = new CallLogListAdapter(this);
        m_lstView.setAdapter(adapter);
        getListData();
    }
	
	private void getListData() {
		//List<CallLogItem> callLogLst = AntiSpamConfig.getInstance(this).getCallLogList();
		/*Bundle bundle = this.getIntent().getBundleExtra(KEY_CALL_LOG_DATA);
		@SuppressWarnings("unchecked")
		ArrayList<CallLogItem> callLogLst = (ArrayList<CallLogItem>)bundle.getSerializable(KEY_CALL_LOG_DATA);*/
		ArrayList<CallLogItem> callLogLst = this.getIntent().getParcelableArrayListExtra(KEY_CALL_LOG_DATA);
		m_lstData.clear();
		
		for(int i = callLogLst.size() - 1;i >= 0;i--) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("checked", false);
			map.put("type", callLogLst.get(i).type);
			map.put("name", callLogLst.get(i).name);
			map.put("number", callLogLst.get(i).number);
			Date time = new Date(callLogLst.get(i).time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdf.format(time);
			map.put("date", strDate);
			m_lstData.add(map);	
		}
		
		setUIStatus();
		((CallLogListAdapter)m_lstView.getAdapter()).notifyDataSetChanged();
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
	
	private class CallLogListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public CallLogListAdapter(Context context){
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
			public TextView name;
			public TextView number;
			public TextView date;
			public RelativeLayout layout;
		}


		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.anti_from_call_log_list_adapter, null);
				holder.check = (CheckBox)convertView.findViewById(R.id.checked_add);
				holder.img = (ImageView)convertView.findViewById(R.id.image_calllog_type);
				holder.name = (TextView)convertView.findViewById(R.id.text_calllog_name);
				holder.number = (TextView)convertView.findViewById(R.id.text_calllog_number);
				holder.date = (TextView)convertView.findViewById(R.id.text_calllog_date);
				holder.layout = (RelativeLayout)convertView.findViewById(R.id.call_log_adapter_layout);
				
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			//holder.img.setBackgroundResource((Integer)m_lstData.get(position).get("img"));
			
			Integer nType = (Integer)m_lstData.get(position).get("type");
			if(nType.intValue() == CallLog.Calls.INCOMING_TYPE) {
				holder.img.setImageResource(R.drawable.icon_call_incmoing);
			}else if(nType.intValue() == CallLog.Calls.OUTGOING_TYPE){
				holder.img.setImageResource(R.drawable.icon_call_outgoing);
			}else{
				holder.img.setImageResource(R.drawable.icon_call_miss);
			}
			final String strName = (String)m_lstData.get(position).get("name");
			final String strNumber = (String)m_lstData.get(position).get("number");
			if(strName != null && strName.length() != 0) {
				holder.name.setText(strName);
				holder.number.setText(strNumber);
			}else{
				holder.name.setText(strNumber);
				holder.number.setText("");
			}
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
