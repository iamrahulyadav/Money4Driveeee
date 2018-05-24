package com.hvantage2.money4driveeee.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProjectModel implements Parcelable {

    public static final Creator<ProjectModel> CREATOR = new Creator<ProjectModel>() {
        @Override
        public ProjectModel createFromParcel(Parcel in) {
            return new ProjectModel(in);
        }

        @Override
        public ProjectModel[] newArray(int size) {
            return new ProjectModel[size];
        }
    };
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("project_title")
    @Expose
    private String projectTitle;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("project_desc")
    @Expose
    private String projectDesc;

    public ProjectModel(Parcel in) {
        projectId = in.readString();
        projectTitle = in.readString();
        city = in.readString();
        createdDate = in.readString();
        projectDesc = in.readString();
    }

    public ProjectModel() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(projectId);
        dest.writeString(projectTitle);
        dest.writeString(city);
        dest.writeString(createdDate);
        dest.writeString(projectDesc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "projectId='" + projectId + '\'' +
                ", projectTitle='" + projectTitle + '\'' +
                ", city='" + city + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", projectDesc='" + projectDesc + '\'' +
                '}';
    }
}
