package com.jambacabs.driver.models;

public class RideDetails {

    String type;
    String message;
    RideData rideData;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RideData getRideData() {
        return rideData;
    }

    public void setRideData(RideData rideData) {
        this.rideData = rideData;
    }
}
