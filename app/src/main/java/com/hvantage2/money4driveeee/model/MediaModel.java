package com.hvantage2.money4driveeee.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hvantage2 on 2018-02-28.
 */

public class MediaModel implements Parcelable {
    String media_type_name,media_type_id;

    public MediaModel(Parcel in) {
        media_type_name = in.readString();
        media_type_id = in.readString();
    }

    public MediaModel() {

    }

    public MediaModel(String media_type_id, String media_type_name) {
        this.media_type_name = media_type_name;
        this.media_type_id = media_type_id;
    }

    public String getMedia_name() {
        return media_type_name;
    }

    public void setMedia_name(String media_type_name) {
        this.media_type_name = media_type_name;
    }

    public String getMedia_id() {
        return media_type_id;
    }

    public void setMedia_id(String media_type_id) {
        this.media_type_id = media_type_id;
    }

    @Override
    public String toString() {
        return "TransitMediaModel{" +
                "media_type_name='" + media_type_name + '\'' +
                ", media_type_id='" + media_type_id + '\'' +
                '}';
    }

    public static final Creator<MediaModel> CREATOR = new Creator<MediaModel>() {
        @Override
        public MediaModel createFromParcel(Parcel in) {
            return new MediaModel(in);
        }

        @Override
        public MediaModel[] newArray(int size) {
            return new MediaModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(media_type_name);
        parcel.writeString(media_type_id);
    }
}
