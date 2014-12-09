package com.alcatel.master.antispam;

import android.os.Parcel;
import android.os.Parcelable;

public class CallLogItem implements Parcelable{
	public int type;
	public String name;
	public String number;
	public long time;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(type);
		dest.writeString(name);
		dest.writeString(number);
		dest.writeLong(time);
	}
	
	public static final Parcelable.Creator<CallLogItem> CREATOR = new Parcelable.Creator<CallLogItem>() {
		@Override  
		public CallLogItem createFromParcel(Parcel source) {
			CallLogItem p = new CallLogItem();
			p.type=source.readInt();
			p.name=source.readString(); 
			p.number=source.readString();
			p.time=source.readLong();
			return p;
		} 
		@Override
		public CallLogItem[] newArray(int size) {
			// TODO Auto-generated method stub
				return null;
			}
	};
}
