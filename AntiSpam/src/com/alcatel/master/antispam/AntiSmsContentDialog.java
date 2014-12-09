package com.alcatel.master.antispam;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class AntiSmsContentDialog extends Dialog {
	private String m_smsContent;
	private float m_posX;
	private float m_posY;
	
	public AntiSmsContentDialog(Context context, int theme,String smsContent) {
		super(context, theme);
		m_smsContent = smsContent;
	}
	
	public void setPos(float fX,float fY) {
		m_posX = fX;
		m_posY = fY;
	}
	
	public static final String SMS_CONTENT = "com.alcatel.master.antispam.sms_content";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anti_sms_body_view);
        TextView smsTextView = (TextView)findViewById(R.id.smsBodyTextView);
        smsTextView.setText(m_smsContent);
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.gravity = Gravity.START | Gravity.TOP;
		params.x = (int)m_posX;
		params.y = (int)m_posY;
		this.getWindow().setAttributes(params);
    }
}
