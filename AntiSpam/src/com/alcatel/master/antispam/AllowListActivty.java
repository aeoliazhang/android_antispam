package com.alcatel.master.antispam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AllowListActivty extends Activity implements OnClickListener{
	public static final String ACTIVITY_NAME = "AllowListActivity";
	private List<Map<String, Object>> m_lstData;
	private ListView m_lstView;
	private View m_viewNoRecord;
	private Button m_btnClear;
	
	private ArrayList<CallLogItem> m_callLogLst = null;
	private ArrayList<FromSmsItem> m_fromSmsLst = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_allow_list); 
        Button btnAdd = (Button)findViewById(R.id.btn_allow_add);
        btnAdd.setOnClickListener(this);
        m_btnClear = (Button)findViewById(R.id.btn_allow_clear);
        m_btnClear.setOnClickListener(this);
        m_viewNoRecord = (View)findViewById(R.id.no_allow_name_layout);
        m_lstView = (ListView)findViewById(R.id.lv_white_list);
        m_lstData = new ArrayList<Map<String, Object>>();
        WhiteListAdapter adapter = new WhiteListAdapter(this);
        m_lstView.setAdapter(adapter);
        getListData();
    }
	
	private void setUIStatus() {
		m_viewNoRecord.setVisibility(m_lstData.size() > 0?View.GONE:View.VISIBLE);
		if(m_lstData.size() > 0) {
			m_btnClear.setVisibility(View.VISIBLE);
		}else{
			m_btnClear.setVisibility(View.GONE);
		}
		//m_btnClear.setEnabled(m_lstData.size() > 0?true:false);
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
    	case R.id.btn_allow_add:
    		addBtnClicked();  
    		break;
    	case R.id.btn_allow_clear:
    		clearBtnClick();
    		break;
		}
	}
	
	private void clearBtnClick() {
		AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
		builder.setTitle(R.string.anti_clear);
		builder.setMessage(R.string.anti_clear_message);
		builder.setNeutralButton(R.string.anti_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				AntiSpamConfig.getInstance(AllowListActivty.this).deleteAllowNumberAll();
				getListData();
				dialog.dismiss();
			}
		});
		builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int id) {  
				dialog.cancel();  
			}  
		});  

		AlertDialogCustom dialog = builder.create();
		dialog.setOwnerActivity(this);
		dialog.show();
	}
	
	private void addBtnClicked() {
		final CharSequence[] items = {getResources().getString(R.string.anti_add_from_calllog),
				getResources().getString(R.string.anti_add_from_contacts),
				getResources().getString(R.string.anti_add_from_sms),
				getResources().getString(R.string.anti_add_manually)}; 
		AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
		builder.setTitle(R.string.anti_settings_add_button);
		builder.setItems(items, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int item) {  
			     //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				if(items[item] == getResources().getString(R.string.anti_add_manually))
				{
					Intent intent = new Intent(AllowListActivty.this,AntiAddManually.class);
					intent.putExtra(AntiAddManually.KEY_FROM_ACTIVITY, ACTIVITY_NAME);
					startActivityForResult(intent,100);
					dialog.dismiss();
				}
				
				if(items[item] == getResources().getString(R.string.anti_add_from_calllog)) {
					addFromCallLog((Dialog)dialog);
				}
				
				if(items[item] == getResources().getString(R.string.anti_add_from_sms)) {
					addFromMessages((Dialog)dialog);
				}
				
				if(items[item] == getResources().getString(R.string.anti_add_from_contacts)) {
					addFromContacts();
					dialog.dismiss();
				}
				
				//dialog.dismiss();
		   }  
		}); 
		AlertDialogCustom dialog = builder.create();
		dialog.setOwnerActivity(this);
		dialog.show();
	}
	
	private void addFromContacts() {
		List<ContactItem> contactLst = AntiSpamConfig.getInstance(this).getContactsList();
		if(contactLst.size() == 0) {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_no_contacts);
			builder.setMessage(R.string.anti_add_allowlist_no_contacts_msg);
			builder.setNeutralButton(R.string.anti_btn_select_method, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					addBtnClicked();
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.cancel();  
				}  
			});
			
			AlertDialogCustom dialog = builder.create();
			dialog.setOwnerActivity(this);
			dialog.show();  
		}else{
			Intent intent = new Intent(AllowListActivty.this,AntiAddFromContact.class);
			intent.putExtra(AntiAddFromContact.KEY_FROM_ACTIVITY, ACTIVITY_NAME);
			startActivityForResult(intent,100);
		}
	}
	
	private void endFromMessages() {
		if(m_fromSmsLst.size() == 0) {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_no_sms);
			builder.setMessage(R.string.anti_add_allowlist_no_messages_msg);
			builder.setNeutralButton(R.string.anti_btn_select_method, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					addBtnClicked();
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.cancel();  
				}  
			});
			
			AlertDialogCustom dialog = builder.create();
			dialog.setOwnerActivity(this);
			dialog.show();  
		}else{
			Intent intent = new Intent(AllowListActivty.this,AntiAddFromMessageLog.class);
			intent.putExtra(AntiAddFromMessageLog.KEY_FROM_ACTIVITY, ACTIVITY_NAME);
			intent.putParcelableArrayListExtra(AntiAddFromMessageLog.KEY_SMS_DATA, m_fromSmsLst);
			startActivityForResult(intent,100);
		}
	}
	
	private void addFromMessages(final Dialog selectDialog) {
		final CustomProgressDialog progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		final Handler handler = new Handler(){
		    public void handleMessage(Message msg) {
		    	if(selectDialog != null){
		    		selectDialog.dismiss();
		    	}
		    	endFromMessages();
		    	if(progressDialog != null) {
		    		progressDialog.dismiss();
		    	}
			}};
		new Thread(){
			    public void run() {
			    	m_fromSmsLst = (ArrayList<FromSmsItem>)AntiSpamConfig.getInstance(AllowListActivty.this).getFromSmsList();
			    	if(handler != null) {
			    		handler.sendEmptyMessage(0);
			    	}
			    }
			}.start();
		/*if(fromSmsLst.size() == 0) {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_no_sms);
			builder.setMessage(R.string.anti_add_allowlist_no_messages_msg);
			builder.setNeutralButton(R.string.anti_btn_select_method, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					addBtnClicked();
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.cancel();  
				}  
			});
			
			AlertDialogCustom dialog = builder.create();
			dialog.setOwnerActivity(this);
			dialog.show();  
		}else{
			Intent intent = new Intent(AllowListActivty.this,AntiAddFromMessageLog.class);
			intent.putExtra(AntiAddFromMessageLog.KEY_FROM_ACTIVITY, ACTIVITY_NAME);
			startActivityForResult(intent,100);
		}*/
	}
	
	private void endFromCallLog() {
		if(m_callLogLst.size() == 0) {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_no_calllog);
			builder.setMessage(R.string.anti_add_allowlist_no_calllog_msg);
			builder.setNeutralButton(R.string.anti_btn_select_method, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					addBtnClicked();
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.cancel();  
				}  
			});
			
			AlertDialogCustom dialog = builder.create();
			dialog.setOwnerActivity(this);
			dialog.show();  
		}else{
			Intent intent = new Intent(AllowListActivty.this,AntiAddFromCallLog.class);
			intent.putExtra(AntiAddFromCallLog.KEY_FROM_ACTIVITY, ACTIVITY_NAME);
			intent.putParcelableArrayListExtra(AntiAddFromCallLog.KEY_CALL_LOG_DATA, m_callLogLst);
			startActivityForResult(intent,100);
		}
	}
	
	private void addFromCallLog(final Dialog selectDialog) {
		final CustomProgressDialog progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		final Handler handler = new Handler(){
		    public void handleMessage(Message msg) {
		    	if(selectDialog != null){
		    		selectDialog.dismiss();
		    	}
		    	endFromCallLog();
		    	if(progressDialog != null) {
		    		progressDialog.dismiss();
		    	}
			}};
		new Thread(){
			    public void run() {
			    	m_callLogLst = (ArrayList<CallLogItem>)AntiSpamConfig.getInstance(AllowListActivty.this).getCallLogList();
			    	if(handler != null) {
			    		handler.sendEmptyMessage(0);
			    	}
			    }
			}.start();

		/*if(callLogLst.size() == 0) {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_no_calllog);
			builder.setMessage(R.string.anti_add_allowlist_no_calllog_msg);
			builder.setNeutralButton(R.string.anti_btn_select_method, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					addBtnClicked();
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.cancel();  
				}  
			});
			
			AlertDialogCustom dialog = builder.create();
			dialog.setOwnerActivity(this);
			dialog.show();  
		}else{
			Intent intent = new Intent(AllowListActivty.this,AntiAddFromCallLog.class);
			intent.putExtra(AntiAddFromCallLog.KEY_FROM_ACTIVITY, ACTIVITY_NAME);
			intent.putParcelableArrayListExtra(AntiAddFromCallLog.KEY_CALL_LOG_DATA, callLogLst);
			startActivityForResult(intent,100);
		}*/
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  { 
		//AntiAddManually.java setResult(30, data);
	     if(30==resultCode)  
	     {  
	    	 getListData();
	     }  
	     super.onActivityResult(requestCode, resultCode, data);  
    }  
	
	private void getListData() {
		ArrayList<String> setDB = AntiSpamConfig.getInstance(this).getAllowList();
		m_lstData.clear();
		
		for(int i = setDB.size() - 1;i >= 0;i--) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", 0);
			map.put("img", R.drawable.icon_delete);
			map.put("number", setDB.get(i).toString());
			m_lstData.add(map);
		}
		
		((WhiteListAdapter)m_lstView.getAdapter()).notifyDataSetChanged();
		
		setUIStatus();
	}
	
	public void showInfo(final String strNumber){
		AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
		builder.setTitle(R.string.anti_remove);
		builder.setMessage(R.string.anti_remove_message);
		builder.setNeutralButton(R.string.anti_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				AntiSpamConfig.getInstance(AllowListActivty.this).deleteAllowNumberFromDB(strNumber);
				getListData();
				dialog.dismiss();
			}
		});
		builder.setPositiveButton(R.string.anti_cancel, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int id) {  
				dialog.cancel();  
			}  
		});  

		AlertDialogCustom dialog = builder.create();
		dialog.setOwnerActivity(this);
		dialog.show();
	}

	private class WhiteListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
			
		public WhiteListAdapter(Context context){
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
			public ImageView img;
			public TextView number;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate
					    (R.layout.anti_black_white_list_adapter,null);	
				//convertView = mInflater.inflate(R.layout.anti_black_white_list_adapter, null);
				holder.img = (ImageView)convertView.findViewById(R.id.image_delete);
				holder.number = (TextView)convertView.findViewById(R.id.text_number);
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			//holder.img.setBackgroundResource((Integer)m_lstData.get(position).get("img"));
			final String strNumber = (String)m_lstData.get(position).get("number");
			holder.number.setText(strNumber);
			
			holder.img.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					showInfo(strNumber);					
				}
			});
			
			
			return convertView;
		}
	}
}