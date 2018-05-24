package com.hvantage2.money4driveeee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardModel {


    @SerializedName("total_project")
    @Expose
    private Integer totalProject;
    @SerializedName("complete_project")
    @Expose
    private Integer completeProject;
    @SerializedName("pending_project")
    @Expose
    private Integer pendingProject;
    @SerializedName("newaggign_project")
    @Expose
    private Integer newaggignProject;

    public Integer getTotalProject() {
        return totalProject;
    }

    public void setTotalProject(Integer totalProject) {
        this.totalProject = totalProject;
    }

    public Integer getCompleteProject() {
        return completeProject;
    }

    public void setCompleteProject(Integer completeProject) {
        this.completeProject = completeProject;
    }

    public Integer getPendingProject() {
        return pendingProject;
    }

    public void setPendingProject(Integer pendingProject) {
        this.pendingProject = pendingProject;
    }

    public Integer getNewaggignProject() {
        return newaggignProject;
    }

    public void setNewaggignProject(Integer newaggignProject) {
        this.newaggignProject = newaggignProject;
    }

    @Override
    public String toString() {
        return "DashboardModel{" +
                ", totalProject=" + totalProject +
                ", completeProject=" + completeProject +
                ", pendingProject=" + pendingProject +
                ", newaggignProject=" + newaggignProject +
                '}';
    }
}
