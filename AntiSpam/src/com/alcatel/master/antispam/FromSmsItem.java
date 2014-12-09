package com.alcatel.master.antispam;

import android.os.Parcel;
import android.os.Parcelable;

public class FromSmsItem implements Parcelable{
	public String name;
	public String number;
	public int type;//1:receive2:send 3:""
	public long time;
	public String body;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeString(number);
		dest.writeInt(type);
		dest.writeLong(time);
		dest.writeString(body);
	}
	
	public static final Parcelable.Creator<FromSmsItem> CREATOR = new Parcelable.Creator<FromSmsItem>() {
		@Override  
		public FromSmsItem createFromParcel(Parcel source) {
			FromSmsItem p = new FromSmsItem();
			p.name=source.readString(); 
			p.number=source.readString();
			p.type=source.readInt();
			p.time=source.readLong();
			p.body=source.readString();
			return p;
		} 
		@Override
		public FromSmsItem[] newArray(int size) {
			// TODO Auto-generated method stub
				return null;
			}
	};
}
