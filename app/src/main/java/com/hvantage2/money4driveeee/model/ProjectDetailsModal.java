package com.hvantage2.money4driveeee.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hvantage2 on 2018-02-27.
 */

public class ProjectDetailsModal implements Parcelable {
  String  project_title,city,address,created_date,allotted_date,project_desc,media_name,media_id,media_type_name,media_type_id,shop_name,shop_id,allocationMedia,project_id;

    public ProjectDetailsModal() {

    }

    public String getProject_title() {
        return project_title;
    }

    public void setProject_title(String project_title) {
        this.project_title = project_title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getAllotted_date() {
        return allotted_date;
    }

    public void setAllotted_date(String allotted_date) {
        this.allotted_date = allotted_date;
    }

    public String getProject_desc() {
        return project_desc;
    }

    public void setProject_desc(String project_desc) {
        this.project_desc = project_desc;
    }

    public String getMedia_name() {
        return media_name;
    }

    public void setMedia_name(String media_name) {
        this.media_name = media_name;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getMedia_type_name() {
        return media_type_name;
    }

    public void setMedia_type_name(String media_type_name) {
        this.media_type_name = media_type_name;
    }

    public String getMedia_type_id() {
        return media_type_id;
    }

    public void setMedia_type_id(String media_type_id) {
        this.media_type_id = media_type_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getAllocationMedia() {
        return allocationMedia;
    }

    public void setAllocationMedia(String allocationMedia) {
        this.allocationMedia = allocationMedia;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    @Override
    public String toString() {
        return "ProjectDetailsModal{" +
                "project_title='" + project_title + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", created_date='" + created_date + '\'' +
                ", allotted_date='" + allotted_date + '\'' +
                ", project_desc='" + project_desc + '\'' +
                ", media_name='" + media_name + '\'' +
                ", media_id='" + media_id + '\'' +
                ", media_type_name='" + media_type_name + '\'' +
                ", media_type_id='" + media_type_id + '\'' +
                ", shop_name='" + shop_name + '\'' +
                ", shop_id='" + shop_id + '\'' +
                ", allocationMedia='" + allocationMedia + '\'' +
                ", project_id='" + project_id + '\'' +
                '}';
    }

    public ProjectDetailsModal(Parcel in) {
        project_title = in.readString();
        city = in.readString();
        address = in.readString();
        created_date = in.readString();
        allotted_date = in.readString();
        project_desc = in.readString();
        media_name = in.readString();
        media_id = in.readString();
        media_type_name = in.readString();
        media_type_id = in.readString();
        shop_name = in.readString();
        shop_id = in.readString();
        allocationMedia = in.readString();
        project_id = in.readString();
    }

    public static final Creator<ProjectDetailsModal> CREATOR = new Creator<ProjectDetailsModal>() {
        @Override
        public ProjectDetailsModal createFromParcel(Parcel in) {
            return new ProjectDetailsModal(in);
        }

        @Override
        public ProjectDetailsModal[] newArray(int size) {
            return new ProjectDetailsModal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(project_title);
        parcel.writeString(city);
        parcel.writeString(address);
        parcel.writeString(created_date);
        parcel.writeString(allotted_date);
        parcel.writeString(project_desc);
        parcel.writeString(media_name);
        parcel.writeString(media_id);
        parcel.writeString(media_type_name);
        parcel.writeString(media_type_id);
        parcel.writeString(shop_name);
        parcel.writeString(shop_id);
        parcel.writeString(allocationMedia);
        parcel.writeString(project_id);
    }
}
