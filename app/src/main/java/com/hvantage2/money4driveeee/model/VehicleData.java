package com.hvantage2.money4driveeee.model;

/**
 * Created by Hvantage on 3/22/2018.
 */

public class VehicleData {

    String vehicle_name;
    String vehicle_no;
    String vehicle_id;

    public String getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(String vehicle_status) {
        this.vehicle_status = vehicle_status;
    }

    String vehicle_status;


    public VehicleData() {
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    @Override
    public String toString() {
        return "VehicleData{" +
                "vehicle_name='" + vehicle_name + '\'' +
                ", vehicle_no='" + vehicle_no + '\'' +
                ", vehicle_id='" + vehicle_id + '\'' +
                ", vehicle_status='" + vehicle_status + '\'' +
                '}';
    }
}
