package com.hvantage2.money4driveeee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hvantage2 on 2018-02-26.
 */

public class UserModal {

    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("login_type")
    @Expose
    private String loginType;

    @SerializedName("login_type_id")
    @Expose
    private String login_type_id;

    @SerializedName("manager_contact_no")
    @Expose
    private String manager_contact_no;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLogin_type_id() {
        return login_type_id;
    }

    public void setLogin_type_id(String login_type_id) {
        this.login_type_id = login_type_id;
    }


    public String getManager_contact_no() {
        return manager_contact_no;
    }

    public void setManager_contact_no(String manager_contact_no) {
        this.manager_contact_no = manager_contact_no;
    }

    @Override
    public String toString() {
        return "UserModal{" +
                "msg='" + msg + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", loginType='" + loginType + '\'' +
                ", login_type_id='" + login_type_id + '\'' +
                ", manager_contact_no='" + manager_contact_no + '\'' +
                '}';
    }
}