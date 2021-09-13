package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

public class DriverVehicleModel
{
    @Expose
    private String vehicleModel;
    @Expose
    private String Avatar;
    @Expose
    private String Types;
    @Expose
    private String Seats;
    @Expose
    private String Availability;

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getTypes() {
        return Types;
    }

    public void setTypes(String types) {
        Types = types;
    }

    public String getSeats() {
        return Seats;
    }

    public void setSeats(String seats) {
        Seats = seats;
    }

    public String getAvailability() {
        return Availability;
    }

    public void setAvailability(String availability) {
        Availability = availability;
    }
}
