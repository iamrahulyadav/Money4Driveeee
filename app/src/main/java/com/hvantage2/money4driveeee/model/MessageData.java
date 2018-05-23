package com.hvantage2.money4driveeee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageData implements Serializable{

    @SerializedName("msg_id")
    @Expose
    private String msgId;
    @SerializedName("msg_text")
    @Expose
    private String msgText;
    @SerializedName("msg_date")
    @Expose
    private String msgDate;
    @SerializedName("msg_time")
    @Expose
    private String msgTime;
    @SerializedName("msg_sender_id")
    @Expose
    private String msgSenderId;
    @SerializedName("msg_sender_name")
    @Expose
    private String msgSenderName;

    public MessageData(String msgId, String msgText, String msgDate, String msgTime, String msgSenderId, String msgSenderName) {
        this.msgId = msgId;
        this.msgText = msgText;
        this.msgDate = msgDate;
        this.msgTime = msgTime;
        this.msgSenderId = msgSenderId;
        this.msgSenderName = msgSenderName;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgSenderId() {
        return msgSenderId;
    }

    public void setMsgSenderId(String msgSenderId) {
        this.msgSenderId = msgSenderId;
    }

    public String getMsgSenderName() {
        return msgSenderName;
    }

    public void setMsgSenderName(String msgSenderName) {
        this.msgSenderName = msgSenderName;
    }


    @Override
    public String toString() {
        return "MessageData{" +
                "msgId='" + msgId + '\'' +
                ", msgText='" + msgText + '\'' +
                ", msgDate='" + msgDate + '\'' +
                ", msgTime='" + msgTime + '\'' +
                ", msgSenderId='" + msgSenderId + '\'' +
                ", msgSenderName='" + msgSenderName + '\'' +
                '}';
    }
}
