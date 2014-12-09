package com.alcatel.master.antispam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class BlockHistoryActivty extends Activity implements OnClickListener,OnTabChangeListener{
	
	
	private class CallSmsBroadcastReceiver extends BroadcastReceiver
	{
	    @Override  
        public void onReceive(Context context, Intent intent) {
	    	
        	if(intent.getAction().equals(SmsReceiver.MESSAGE_SMS_SPAM_ACTION)) {  
        	   	getSmsListData();
           	}
        	
        	if(intent.getAction().equals(PhoneReceiver.MESSAGE_CALL_SPAM_ACTION)) {  
        		getCallListData();
        	}  
        }  	
	}
	
	private TabHost m_TabHost;
	private List<Map<String, Object>> m_lstSmsData;
	private List<Map<String, Object>> m_lstCallData;
	private ListView m_lstSmsView;
	private ListView m_lstCallView;
	private Button m_btnDeleteCall;
	private Button m_btnDeleteSMS;
	private TextView m_txtNoRecordSMS;
	private TextView m_txtNoRecordCall;
	
	public static final String HISTORY_TYPE_STRING = "com.alcatel.master.antispam.historyType";
	public static final int HISTORY_TYPE_SMS = 1;
	public static final int HISTORY_TYPE_CALL = 0;
	
	private float m_mouseX;
	private float m_mouseY;
	
	private CallSmsBroadcastReceiver m_callAndSmsSpamBroadcastReceiver;
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_block_history);
        
        m_btnDeleteCall = (Button)findViewById(R.id.btn_delete_call);
        m_btnDeleteCall.setOnClickListener(this);
        
        m_btnDeleteSMS = (Button)findViewById(R.id.btn_delete_sms);
        m_btnDeleteSMS.setOnClickListener(this);
        m_txtNoRecordSMS = (TextView)findViewById(R.id.no_record_sms);
        m_txtNoRecordCall = (TextView)findViewById(R.id.no_record_call);
        //sms history adapter
        m_lstSmsView = (ListView)findViewById(R.id.list_history_sms);
        m_lstSmsData = new ArrayList<Map<String, Object>>();
        SmsListAdapter smsAdapter = new SmsListAdapter(this);
        m_lstSmsView.setAdapter(smsAdapter);
        getSmsListData();
        
        //call history adapter
        m_lstCallView = (ListView)findViewById(R.id.list_history_call);
        m_lstCallData = new ArrayList<Map<String, Object>>();
        CallListAdapter callAdapter = new CallListAdapter(this);
        m_lstCallView.setAdapter(callAdapter);
        getCallListData();
        
        iniTab();
      } 
	
	@Override
	public void onResume() {
    	super.onResume();
    	getSmsListData();
    	getCallListData();
    	
    	m_callAndSmsSpamBroadcastReceiver = new CallSmsBroadcastReceiver();
    	this.registerReceiver(m_callAndSmsSpamBroadcastReceiver, new  IntentFilter(SmsReceiver.MESSAGE_SMS_SPAM_ACTION));
    	this.registerReceiver(m_callAndSmsSpamBroadcastReceiver, new  IntentFilter(PhoneReceiver.MESSAGE_CALL_SPAM_ACTION));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.unregisterReceiver(m_callAndSmsSpamBroadcastReceiver);
	}
	

	 
	 private void iniTab() {
	     m_TabHost = (TabHost)findViewById(R.id.tabhost);
	     m_TabHost.setup(); 
	     
	     View view1 = (View) LayoutInflater.from(this).inflate(R.layout.anti_tab_mini, null);  
	     TextView text1 = (TextView) view1.findViewById(R.id.tv_indicator); 
	     text1.setText(R.string.anti_calllog_tab); 
	     
	     View view2 = (View) LayoutInflater.from(this).inflate(R.layout.anti_tab_mini, null);  
	     TextView text2 = (TextView) view2.findViewById(R.id.tv_indicator); 
	     text2.setText(R.string.anti_sms_tab); 
	     
	     TabSpec tab1 = m_TabHost.newTabSpec("call_tab").setIndicator(view1);
	     tab1.setContent(R.id.tab_calls);
	     m_TabHost.addTab(tab1);
	     
	     TabSpec tab2 = m_TabHost.newTabSpec("sms_tab").setIndicator(view2);
	     tab2.setContent(R.id.tab_sms);
	     m_TabHost.addTab(tab2);
	
	     m_TabHost.setOnTabChangedListener(this);
	     int nTabIndex = this.getIntent().getIntExtra(HISTORY_TYPE_STRING, HISTORY_TYPE_CALL);
	     m_TabHost.setCurrentTab(nTabIndex);
	     if(nTabIndex == HISTORY_TYPE_CALL) {
	    	 onTabChanged("call_tab");
	     }else{
	    	 onTabChanged("sms_tab");
	     }
	 }
	 
	 public void onTabChanged(String tabId){
		TabWidget tabWidget = m_TabHost.getTabWidget();
		View view1 = tabWidget.getChildTabViewAt(0);
		ImageView image1 = (ImageView) view1.findViewById(R.id.image_arrow);
		View view2 = tabWidget.getChildTabViewAt(1);
		ImageView image2 = (ImageView) view2.findViewById(R.id.image_arrow);
		if(tabId.equalsIgnoreCase("call_tab"))	{
			image1.setVisibility(View.VISIBLE);
       	 	image2.setVisibility(View.INVISIBLE); 
		}
		
		if(tabId.equalsIgnoreCase("sms_tab"))	{
			image1.setVisibility(View.INVISIBLE);
       	 	image2.setVisibility(View.VISIBLE);
		}
	 }
	 
	 public void onClick(View v) {
		switch(v.getId()) {
	    case R.id.btn_delete_call:
	    	clearPhoneList();
	    	break;
	    case R.id.btn_delete_sms:
	    	clearMessagesList();
	    	break;
		}
	 }
	 
	 private void clearMessagesList() {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_delete);
			builder.setMessage(R.string.anti_delete_message);
			builder.setNeutralButton(R.string.anti_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
			    	AntiSpamConfig.getInstance(BlockHistoryActivty.this).clearSmsHistoyList();
			    	getSmsListData();
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
	 
	 private void clearPhoneList() {
			AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
			builder.setTitle(R.string.anti_clear);
			builder.setMessage(R.string.anti_clear_message);
			builder.setNeutralButton(R.string.anti_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					AntiSpamConfig.getInstance(BlockHistoryActivty.this).clearPhoneHistoyList();
			    	getCallListData();
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
	 
	 private void getCallListData() {
		 List<PhoneItem> setDB = AntiSpamConfig.getInstance(this).getPhoneHistoryList();
		 m_lstCallData.clear();
		 
		 for(int i = setDB.size() - 1;i >= 0;i--) {
			 Map<String, Object> map = new HashMap<String, Object>();
			 //map.put("readImg",R.drawable.icon_unread_record);
			 //map.put("contactImg", R.drawable.icon_contacts);
			 //map.put("callName", "name");
			 //map.put("callType", "type");
			 //map.put("callAddress", "nainaide");
			 //map.put("callDate", "2012-12-07");
			 map.put("id",setDB.get(i).id);
			 map.put("readImg",0);
			 map.put("contactImg", R.drawable.icon_contacts);
			 map.put("callName", setDB.get(i).number);
			 map.put("callType", "");
			 map.put("callAddress", "");
			 String strDate = setDB.get(i).time;
			 map.put("callDate", strDate);
			 m_lstCallData.add(map);
		}
		((CallListAdapter)m_lstCallView.getAdapter()).notifyDataSetChanged();
		m_txtNoRecordCall.setVisibility(setDB.size() > 0?View.GONE:View.VISIBLE);
		m_btnDeleteCall.setEnabled(setDB.size() > 0?true:false); 
	}
	 
	 private void getSmsListData() {
		 List<SmsItem> setDB = AntiSpamConfig.getInstance(this).getSmsHistoryList();
		 m_lstSmsData.clear();
		 for(int i = setDB.size() - 1;i >= 0;i--) {
			 Map<String, Object> map = new HashMap<String, Object>();
			 //map.put("readImg",R.drawable.icon_unread_record);
			 //map.put("contactImg", R.drawable.icon_contacts);
			 //map.put("smsName", "name");
			 //map.put("smsType", "type");
			 //map.put("smsContent", "nainaide");
			 //map.put("smsDate", "2012-12-07");
			 map.put("id",setDB.get(i).id);
			 map.put("readImg",0);
			 map.put("contactImg", R.drawable.icon_contacts);
			 map.put("smsName", setDB.get(i).number);
			 map.put("smsType", "");
			 map.put("smsContent", setDB.get(i).content);
			 String strDate = setDB.get(i).time;
			 map.put("smsDate", strDate);
			 m_lstSmsData.add(map);
		}
		((SmsListAdapter)m_lstSmsView.getAdapter()).notifyDataSetChanged();
		m_txtNoRecordSMS.setVisibility(setDB.size() > 0?View.GONE:View.VISIBLE);
		m_btnDeleteSMS.setEnabled(setDB.size() > 0?true:false); 
	}
	
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//Status Bar height
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int nStatusHeight = frame.top;
			//15:arrow width
			m_mouseX = event.getX() - 15;
			m_mouseY = event.getY() - nStatusHeight;
		}
		return super.dispatchTouchEvent(event);
	}
	
	
	private void showSmsContent(String strContent) {
		AntiSmsContentDialog dialog = new AntiSmsContentDialog(this,R.style.SmsContentDialog,strContent);
		dialog.setPos(this.m_mouseX,this.m_mouseY);
		dialog.show();
	}
	
	
	private void deleteCallHistoryAlert(String strCallNumber,final int id) {
		AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
		builder.setTitle(R.string.anti_delete);
		String strContent = this.getResources().getString(R.string.anti_delete_message) + "("+ strCallNumber + ")";
		builder.setMessage(strContent);
		builder.setNeutralButton(R.string.anti_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		    	AntiSpamConfig.getInstance(BlockHistoryActivty.this).deletePhoneHistoyById(id);
		    	getCallListData();
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
	
	private void deleteSmsHistoryAlert(String strSmsNumber,final int id) {
		AlertDialogCustom.Builder builder = new AlertDialogCustom.Builder(this);
		builder.setTitle(R.string.anti_delete);
		String strContent = this.getResources().getString(R.string.anti_delete_message) + "("+ strSmsNumber + ")";
		builder.setMessage(strContent);
		builder.setNeutralButton(R.string.anti_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		    	AntiSpamConfig.getInstance(BlockHistoryActivty.this).deleteSmsHistoyById(id);
		    	getSmsListData();
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
	
	private void callDialPanel(String strCallNumber) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel://" + strCallNumber));
        startActivity(intent);
	}
	
	private void sendSMSPanel(String strNumber) {    
        Uri smsToUri = Uri.parse("smsto:" + strNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        startActivity(intent);
	}
	
	private class SmsListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public SmsListAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return m_lstSmsData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public ImageView readImg;
			public ImageView contactImg;
			public TextView smsName;
			public ImageView smsDeleteImg;
			public TextView smsContent;
			public TextView smsDate;
			public ImageView smsCallImg;
			public ImageView smsSendImg;
			
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.anti_history_list_sms_adapter, null);
				holder.readImg = (ImageView)convertView.findViewById(R.id.image_sms_read_type);
				holder.contactImg = (ImageView)convertView.findViewById(R.id.image_sms_type);
				holder.smsName = (TextView)convertView.findViewById(R.id.text_sms_name);
				holder.smsDeleteImg = (ImageView)convertView.findViewById(R.id.image_delete_sms);
				holder.smsContent = (TextView)convertView.findViewById(R.id.text_sms_body);
				holder.smsDate = (TextView)convertView.findViewById(R.id.text_sms_date);
				holder.smsCallImg = (ImageView)convertView.findViewById(R.id.image_sms_call);
				holder.smsSendImg = (ImageView)convertView.findViewById(R.id.image_sms_sms);
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			int readImgId = (Integer)m_lstSmsData.get(position).get("readImg");
			if(readImgId != 0) {
				holder.readImg.setBackgroundResource(readImgId);
			}
			holder.contactImg.setBackgroundResource((Integer)m_lstSmsData.get(position).get("contactImg"));
			final String strName = (String)m_lstSmsData.get(position).get("smsName");
			holder.smsName.setText(strName);
			//holder.smsType.setText((String)m_lstSmsData.get(position).get("smsType"));
			final int id = ((Integer)m_lstSmsData.get(position).get("id")).intValue();
			final String strContent = (String)m_lstSmsData.get(position).get("smsContent");
			String strSub = strContent;
			if(strSub.length() > 8) {
				 strSub = strContent.substring(0, 8);
			}
			holder.smsContent.setText(strSub);
			holder.smsDate.setText((String)m_lstSmsData.get(position).get("smsDate"));
			
			convertView.setOnLongClickListener(new OnLongClickListener() {
		
					@Override
					public boolean onLongClick(View v) {
						showSmsContent(strContent);	
						return false;
					}
				});
			
			holder.smsDeleteImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					deleteSmsHistoryAlert(strName,id);					
				}
			});
			
			holder.smsCallImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					callDialPanel(strName);					
				}
			});
			holder.smsSendImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					sendSMSPanel(strName);					
				}
			});
			
			return convertView;
		}
	}

	private class CallListAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public CallListAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return m_lstCallData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}
		
		public final class ViewHolder{
			public ImageView readImg;
			public ImageView contactImg;
			public TextView callName;
			public ImageView callDeleteImg;
			public TextView callAddress;
			public TextView callDate;
			public ImageView callImg;
			public ImageView callSendsmsImg;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {	
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.anti_history_list_call_adapter, null);
				holder.readImg = (ImageView)convertView.findViewById(R.id.image_calllog_read_type);
				holder.contactImg = (ImageView)convertView.findViewById(R.id.image_calllog_type);
				holder.callName = (TextView)convertView.findViewById(R.id.text_calllog_name);
				holder.callDeleteImg = (ImageView)convertView.findViewById(R.id.image_delete_call);
				holder.callAddress = (TextView)convertView.findViewById(R.id.text_calllog_address);
				holder.callDate = (TextView)convertView.findViewById(R.id.text_calllog_date);
				holder.callImg = (ImageView)convertView.findViewById(R.id.image_calllog_call);
				holder.callSendsmsImg = (ImageView)convertView.findViewById(R.id.image_calllog_sms);
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			int nReadId = (Integer)m_lstCallData.get(position).get("readImg");
			if(nReadId != 0) {
				holder.readImg.setBackgroundResource(nReadId);
			}
			holder.contactImg.setBackgroundResource((Integer)m_lstCallData.get(position).get("contactImg"));
			final String strCallNumber = (String)m_lstCallData.get(position).get("callName");
			holder.callName.setText(strCallNumber);
			//holder.callType.setText((String)m_lstCallData.get(position).get("callType"));
			final int id = ((Integer)m_lstCallData.get(position).get("id")).intValue();
			holder.callAddress.setText((String)m_lstCallData.get(position).get("callAddress"));
			holder.callDate.setText((String)m_lstCallData.get(position).get("callDate"));
			holder.callImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					callDialPanel(strCallNumber);					
				}
			});
			holder.callSendsmsImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					sendSMSPanel(strCallNumber);					
				}
			});
			
			holder.callDeleteImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					deleteCallHistoryAlert(strCallNumber,id);					
				}
			});
			
			return convertView;
		}
	}
}
