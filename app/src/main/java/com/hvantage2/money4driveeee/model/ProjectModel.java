package com.hvantage2.money4driveeee.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hvantage2 on 2018-02-19.
 */

public class ProjectModel implements Parcelable {
    String projectTittle,projectCity,projectDate,project_id,Project_desc;

    public ProjectModel() {

    }


    @Override
    public String toString() {
        return "ProjectModel{" +
               /* "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +*/
                "projectTittle='" + projectTittle + '\'' +
                ", projectCity='" + projectCity + '\'' +
                ", projectDate='" + projectDate + '\'' +
                ", project_id='" + project_id + '\'' +
                ", Project_desc='" + Project_desc + '\'' +
                '}';
    }

  /*  public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }*/

    public String getProjectTittle() {
        return projectTittle;
    }

    public void setProjectTittle(String projectTittle) {
        this.projectTittle = projectTittle;
    }

    public String getProjectCity() {
        return projectCity;
    }

    public void setProjectCity(String projectCity) {
        this.projectCity = projectCity;
    }

    public String getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(String projectDate) {
        this.projectDate = projectDate;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProject_desc() {
        return Project_desc;
    }

    public void setProject_desc(String project_desc) {
        Project_desc = project_desc;
    }

    public ProjectModel(Parcel in) {
      /*  name = in.readString();
        message = in.readString();
        date = in.readString();*/
        projectTittle = in.readString();
        projectCity = in.readString();
        projectDate = in.readString();
        project_id = in.readString();
        Project_desc = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
      /*  parcel.writeString(name);
        parcel.writeString(message);
        parcel.writeString(date);*/
        parcel.writeString(projectTittle);
        parcel.writeString(projectCity);
        parcel.writeString(projectDate);
        parcel.writeString(project_id);
        parcel.writeString(Project_desc);
    }
}
