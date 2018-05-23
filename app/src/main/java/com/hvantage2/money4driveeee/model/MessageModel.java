package com.hvantage2.money4driveeee.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hvantage2 on 2018-02-19.
 */

public class MessageModel{
    String id,message,user,date_time;

    public MessageModel(String id, String message, String user, String date_time) {
        this.id = id;
        this.message = message;
        this.user = user;
        this.date_time = date_time;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", user='" + user + '\'' +
                ", date_time='" + date_time + '\'' +
                '}';
    }
}
