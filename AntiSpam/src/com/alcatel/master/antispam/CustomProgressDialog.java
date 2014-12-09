package com.alcatel.master.antispam;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class CustomProgressDialog extends  Dialog{
	private Context context = null; 
	//private CustomProgressDialog m_dialog;
	
	public CustomProgressDialog(Context context){  
        super(context,R.style.CustomProgressDialog);  
        this.context = context; 
        setContentView(R.layout.customprogressdialog); 
        getWindow().getAttributes().gravity = Gravity.CENTER;
        this.setOnKeyListener(new DialogInterface.OnKeyListener() {
        	@Override
     	   public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
     		   if (keyCode == KeyEvent.KEYCODE_SEARCH) {
     			   return true;
     		   }else{
     			   return false;
     		   }
     	   }
        });
        setCancelable(false);
    }  
      
    public CustomProgressDialog(Context context, int theme) {  
        super(context, theme); 
        this.context = context;
        setContentView(R.layout.customprogressdialog); 
        getWindow().getAttributes().gravity = Gravity.CENTER; 
        this.setOnKeyListener(new DialogInterface.OnKeyListener() {
        	@Override
     	   public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
     		   if (keyCode == KeyEvent.KEYCODE_SEARCH) {
     			   return true;
     		   }else{
     			   return false;
     		   }
     	   }
        });
        setCancelable(false);
    }  
      
    public void onWindowFocusChanged(boolean hasFocus){  
          
        /*if (m_dialog == null){  
            return;  
        }  */
          
        /*ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);  
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();  
        animationDrawable.start(); */
        
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
        		context, R.anim.page_loading_anim);
		ImageView loadingImage = (ImageView) this
				.findViewById(R.id.loadingImageView);
		loadingImage.startAnimation(hyperspaceJumpAnimation);
    }  

}
