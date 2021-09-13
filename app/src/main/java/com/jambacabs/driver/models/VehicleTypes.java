package com.jambacabs.driver.models;

public class VehicleTypes
{
    private String vehicleModel = "";
    private String avatar = "";
    private boolean Availability = false;

    public String getTypes() {
        return Types;
    }

    public void setTypes(String types) {
        Types = types;
    }

    private String Types = "";

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    private String seats = "";

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean getAvailability() {
        return Availability;
    }

    public void setAvailability(boolean availability) {
        Availability = availability;
    }
}
