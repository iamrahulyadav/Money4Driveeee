package com.hvantage2.money4driveeee.model;

import android.graphics.Bitmap;

/**
 * Created by Hvantage on 3/1/2018.
 */

public class ImageUploadModel {
    Bitmap image=null;
    String image_url="";
    String dimension="";
    String remark="";
    String datetime="";
    String status="new";
    String update_id="";

    public String getUpdate_id() {
        return update_id;
    }

    public void setUpdate_id(String update_id) {
        this.update_id = update_id;
    }

    @Override
    public String toString() {
        return "ImageUploadModel{" +
                "image=" + image +
                ", image_url='" + image_url + '\'' +
                ", dimension='" + dimension + '\'' +
                ", remark='" + remark + '\'' +
                ", datetime='" + datetime + '\'' +
                ", status='" + status + '\'' +
                ", update_id='" + update_id + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public ImageUploadModel(Bitmap image, String dimension, String remark) {
        this.image = image;
        this.dimension = dimension;
        this.remark = remark;
        status="new";
    }



    public ImageUploadModel(String image_url, String dimention, String remark, String datetime, String update_id) {
        this.image_url = image_url;
        this.dimension = dimention;
        this.remark = remark;
        this.datetime = datetime;
        this.update_id = update_id;
        status="edit";
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
