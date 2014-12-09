package com.alcatel.master.antispam;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;

public class AlertDialogCustom extends Dialog {
	public AlertDialogCustom(Context context, int theme) {
		super(context, theme);
	}
	
	public AlertDialogCustom(Context context) {
		super(context);
	}
	
	public static class Builder { 
		private AlertDialogCustom m_dialog;
		private Context m_context;  
		private String m_title;  
		private String m_message;
		private String m_positiveButtonText;  
		private String m_negativeButtonText;
		private String m_neutralButtonText;
		private View m_contentView;
		private CharSequence[] m_items;
		private int m_nDialogWidth;//-1:no set
		
		private DialogInterface.OnClickListener   
			m_positiveButtonClickListener,
			m_neutralButtonClickListener,
			m_negativeButtonClickListener,
			m_itemsClickListener;
		
		public Builder(Context context) {  
			m_context = context;
			m_nDialogWidth= -1;
		}
		
		public Builder setDialogWidth(int nWidth) {
			m_nDialogWidth = nWidth;
			return this;
		}
		
		public Builder setItems(CharSequence[] items, final OnClickListener listener) {
            m_items = items;
            m_itemsClickListener = listener;
            return this;
        }
		
		public Builder setMessage(String message) {  
			m_message = message;  
			return this;  
		}  

		public Builder setMessage(int message) {  
			this.m_message = (String) m_context.getText(message);  
			return this;  
		}
		
		public Builder setTitle(String title) {  
			this.m_title = title;  
			return this;  
		}
		
		public Builder setTitle(int title) {  
			this.m_title = (String) m_context.getText(title);  
			return this;  
		}

		public Builder setContentView(View v) {  
			this.m_contentView = v;  
			return this;  
		}  
		
		public Builder setNeutralButton(int neutralButtonText,DialogInterface.OnClickListener listener) {
			this.m_neutralButtonText = (String) m_context.getText(neutralButtonText);  
			this.m_neutralButtonClickListener = listener;  
			return this;
		}
		
		public Builder setNeutralButton(String neutralButtonText,DialogInterface.OnClickListener listener) {  
			this.m_neutralButtonText = neutralButtonText;  
			this.m_neutralButtonClickListener = listener;  
			return this;  
		}

		public Builder setPositiveButton(int positiveButtonText,DialogInterface.OnClickListener listener) {
			this.m_positiveButtonText = (String) m_context.getText(positiveButtonText);  
			this.m_positiveButtonClickListener = listener;  
			return this;
		}
		
		public Builder setPositiveButton(String positiveButtonText,DialogInterface.OnClickListener listener) {  
			this.m_positiveButtonText = positiveButtonText;  
			this.m_positiveButtonClickListener = listener;  
			return this;  
		}
		
		public Builder setNegativeButton(int negativeButtonText,DialogInterface.OnClickListener listener) {  
			this.m_negativeButtonText = (String) m_context.getText(negativeButtonText);
			this.m_negativeButtonClickListener = listener;
			return this;  
		}
		
		public Builder setNegativeButton(String negativeButtonText,DialogInterface.OnClickListener listener) {  
			this.m_negativeButtonText = negativeButtonText;  
			this.m_negativeButtonClickListener = listener;  
			return this;  
		}
		
		private void createPositiveButton(View layout,final AlertDialogCustom dialog) {
			if (m_positiveButtonText != null) {
				layout.findViewById(R.id.buttonPanel).setVisibility(View.VISIBLE);
				((Button) layout.findViewById(R.id.positiveButton)).setText(m_positiveButtonText);  
				if (m_positiveButtonClickListener != null) {  
					((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {  
							public void onClick(View v) {  
								m_positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);  
							}
					});  
				}
			} else {  
				layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);  
			}  
		}
		
		private void createNegativeButton(View layout,final AlertDialogCustom dialog) {
			if (m_negativeButtonText != null) {
				layout.findViewById(R.id.buttonPanel).setVisibility(View.VISIBLE);
				((Button) layout.findViewById(R.id.negativeButton)).setText(m_negativeButtonText);  
				if (m_negativeButtonClickListener != null) {  
					((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {  
							public void onClick(View v) {  
								m_negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);  
							}
					});  
				}
			} else {  
				layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);  
			}  
		}
		
		private void createNeutralButton(View layout,final AlertDialogCustom dialog) {
			if (m_neutralButtonText != null) {
				layout.findViewById(R.id.buttonPanel).setVisibility(View.VISIBLE);
				((Button) layout.findViewById(R.id.neutralButton)).setText(m_neutralButtonText);  
				if (m_neutralButtonClickListener != null) {  
					((Button) layout.findViewById(R.id.neutralButton)).setOnClickListener(new View.OnClickListener() {  
							public void onClick(View v) {  
								m_neutralButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEUTRAL);  
							}
					});  
				}
			} else {  
				layout.findViewById(R.id.neutralButton).setVisibility(View.GONE);  
			}  
		}
		
		private void createButtonDivider(View layout) {
			if (m_neutralButtonText != null || m_negativeButtonText != null || m_positiveButtonText != null) {
				layout.findViewById(R.id.buttonTopDivider).setVisibility(View.VISIBLE);
			}
			
			if(m_neutralButtonText != null && m_negativeButtonText != null || 
					m_neutralButtonText != null && m_positiveButtonText != null ) {
				layout.findViewById(R.id.buttonDivider1).setVisibility(View.VISIBLE);
			}
			
			if(m_negativeButtonText != null && m_positiveButtonText != null) {
				layout.findViewById(R.id.buttonDivider2).setVisibility(View.VISIBLE);
			}
		}
		
		private void createMessage(View layout) {
			if(m_message != null) {
				((TextView)layout.findViewById(R.id.message)).setText(m_message);
			}else{
				layout.findViewById(R.id.messageScrollView).setVisibility(View.GONE);  
			}
		}
		
		private void createCustomContent(View layout) {
			if(m_contentView != null) {
				LinearLayout contentLayout = (LinearLayout)layout.findViewById(R.id.customPanel);
				contentLayout.setVisibility(View.VISIBLE);
				contentLayout.removeAllViews();
				contentLayout.addView(m_contentView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			}
		}
		
		private void createItems(View layout) {
			if(m_items != null) {
				ListView itemsLstView = (ListView)layout.findViewById(R.id.itemsListView);
				ItemsListAdapter adapter = new ItemsListAdapter(m_dialog.getContext());
				itemsLstView.setAdapter(adapter);
			}
		}
		
		private void setTitleDisplay(View layout) {
			if( m_title == null ) {
				((LinearLayout)layout.findViewById(R.id.topPanel)).setVisibility(View.GONE);
			}
		}
		
		private void setDialogParams() {
			WindowManager.LayoutParams params = m_dialog.getWindow().getAttributes();
			if(m_nDialogWidth != -1) {
				params.width = m_nDialogWidth;
			}
			m_dialog.getWindow().setAttributes(params);
		}
		
		public AlertDialogCustom create() {  
			LayoutInflater inflater = LayoutInflater.from(m_context);  
			//final AlertDialogCustom dialog = new AlertDialogCustom(m_context,R.style.CustomDialog);  
			m_dialog = new AlertDialogCustom(m_context,R.style.CustomDialog);
			View layout = inflater.inflate(R.layout.alert_dialog_custom, null); 
			LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			m_dialog.addContentView(layout, layoutParam);
			
			setDialogParams();
			
			((TextView) layout.findViewById(R.id.alertTitle)).setText(m_title);  
			setTitleDisplay(layout);
			createPositiveButton(layout,m_dialog);
			createNegativeButton(layout,m_dialog);
			createNeutralButton(layout,m_dialog);
			createButtonDivider(layout);
			createMessage(layout);
			createCustomContent(layout);
			createItems(layout);
			m_dialog.setContentView(layout); 
			return m_dialog;  
		}  
		
		private class ItemsListAdapter extends BaseAdapter{

			private LayoutInflater mInflater;
			
			
			public ItemsListAdapter(Context context){
				this.mInflater = LayoutInflater.from(context);
			}
			
			public int getCount() {
				return m_items.length;
			}

			public Object getItem(int arg0) {
				return null;
			}

			public long getItemId(int arg0) {
				return 0;
			}
			
			public final class ViewHolder{
				public TextView item;
			}


			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {	
					holder=new ViewHolder();  
					convertView = mInflater.inflate(R.layout.alert_dialog_custom_items_adapter, null);
					holder.item = (TextView)convertView.findViewById(R.id.item);
					convertView.setTag(holder);
					
				}else {
					holder = (ViewHolder)convertView.getTag();
				}
				
				final String strItemText = (String)m_items[position];
				holder.item.setText(strItemText);
				
				convertView.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						m_itemsClickListener.onClick(m_dialog, position);				
					}
				});
				
				
				return convertView;
			}
		}
	}
}
