package com.alcatel.master.antispam;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class AntiAddFromContact extends Activity  implements OnClickListener{
	public static final String KEY_FROM_ACTIVITY = "com.alcatel.master.antispam.from_activity";
	private List<Map<String, Object>> m_lstData;
	
	private Button m_addBtn;
	private Button m_cancelBtn;
	private ListView m_lstView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.anti_add_from_contact); 
        m_addBtn = (Button)findViewById(R.id.btn_add);
        m_addBtn.setOnClickListener(this);
        m_cancelBtn = (Button)findViewById(R.id.btn_cancel);
        m_cancelBtn.setOnClickListener(this);
        
        m_lstView = (ListView)findViewById(R.id.lv_contact_list);
        m_lstData = new ArrayList<Map<String, Object>>();
        ContactListAdapter adapter = new ContactListAdapter(this);
        m_lstView.setAdapter(adapter);
        //getListData();
        initData();
    }
	
	private void initData() {
		final CustomProgressDialog progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		final Handler handler = new Handler(){
		    public void handleMessage(Message msg) {
		    	refreshUi();
		    	if(progressDialog != null) {
		    		progressDialog.dismiss();
		    	}
			}};
		new Thread(){
			    public void run() {
			    	List<ContactItem> contactLst = AntiSpamConfig.getInstance(AntiAddFromContact.this).getContactsList();
					sortContactLst(contactLst);
					m_lstData.clear();
					
					for(int i = 0; i < contactLst.size();i++) {
						Map<String, Object> map = new HashMap<String, Object>();		
						map.put("checked", false);
						map.put("photo", null);
						map.put("name", contactLst.get(i).name);
						map.put("number", contactLst.get(i).number);
						m_lstData.add(map);
					}
			    	if(handler != null) {
			    		handler.sendEmptyMessage(0);
			    	}
			    }
			}.start();
	}
	
	public class ComparatorList implements Comparator {
		@Override
		public int compare(Object value1, Object value2) 
		{
			String s1 = ((ContactItem)value1).name;
			String s2 = ((ContactItem)value2).name;
		    return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
		}
	 };
	
	private void sortContactLst(List<ContactItem> contactLst) {
		ComparatorList comper = new ComparatorList();
		Collections.sort(contactLst, comper);
	}
	
	private void refreshUi(){
		setUIStatus();
		((ContactListAdapter)m_lstView.getAdapter()).notifyDataSetChanged();
	}
	
	private void getListData() {
		List<ContactItem> contactLst = AntiSpamConfig.getInstance(this).getContactsList();
		sortContactLst(contactLst);
		m_lstData.clear();
		
		for(int i = 0; i < contactLst.size();i++) {
			Map<String, Object> map = new HashMap<String, Object>();		
			map.put("checked", false);
			map.put("photo", null);
			map.put("name", contactLst.get(i).name);
			map.put("number", contactLst.get(i).number);
			m_lstData.add(map);
		}
		
		setUIStatus();
		((ContactListAdapter)m_lstView.getAdapter()).notifyDataSetChanged();
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
	
	private class ContactListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public ContactListAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return m_lstData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return arg0;
		}
		
		public final class ViewHolder{
			public CheckBox check;
			public ImageView img;
			public TextView name;
			public TextView number;
			public RelativeLayout layout;
		}


		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.anti_from_contact_list_adapter, null);
				holder.check = (CheckBox)convertView.findViewById(R.id.checked_add);
				holder.img = (ImageView)convertView.findViewById(R.id.image_contact_photo);
				holder.name = (TextView)convertView.findViewById(R.id.text_contact_name);
				holder.number = (TextView)convertView.findViewById(R.id.text_contact_number);
				holder.layout = (RelativeLayout)convertView.findViewById(R.id.contact_adapter_layout);
				
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			Bitmap photo = (Bitmap)m_lstData.get(position).get("photo");
			if(photo != null) {
				holder.img.setImageBitmap(photo);
			}else{
				holder.img.setImageResource(R.drawable.icon_contact_null);
			}
			
			final String strName = (String)m_lstData.get(position).get("name");
			final String strNumber = (String)m_lstData.get(position).get("number");
			holder.name.setText(strName);
			holder.number.setText(strNumber);
			
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
