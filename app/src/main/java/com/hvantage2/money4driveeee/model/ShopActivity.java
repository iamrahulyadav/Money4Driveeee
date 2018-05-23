package com.hvantage2.money4driveeee.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ShopActivity implements Parcelable {

    String activity_id="";
    String activity_name="";
    int activity_status=0;

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }

    boolean isSeleted=false;



    public ShopActivity(String activity_id, String activity_name, int activity_status) {
        this.activity_id = activity_id;
        this.activity_name = activity_name;
        this.activity_status = activity_status;
    }

    protected ShopActivity(Parcel in) {
        activity_id = in.readString();
        activity_name = in.readString();
        activity_status = in.readInt();
    }

    public static final Creator<ShopActivity> CREATOR = new Creator<ShopActivity>() {
        @Override
        public ShopActivity createFromParcel(Parcel in) {
            return new ShopActivity(in);
        }

        @Override
        public ShopActivity[] newArray(int size) {
            return new ShopActivity[size];
        }
    };

    public ShopActivity() {

    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public int getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(int activity_status) {
        this.activity_status = activity_status;
    }

    @Override
    public String toString() {
        return "ShopActivity{" +
                "activity_id='" + activity_id + '\'' +
                ", activity_name='" + activity_name + '\'' +
                ", activity_status=" + activity_status +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(activity_id);
        parcel.writeString(activity_name);
        parcel.writeInt(activity_status);
    }
}