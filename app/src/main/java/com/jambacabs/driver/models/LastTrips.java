package com.jambacabs.driver.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LastTrips {
    private String rideId;
    private String status;
    private long startTime;
    private long endTime;
    private long userId;
    private long driverId;
    private PickUpLocation pickUpLocation;
    private DropLocation dropLocation;
    private String pickUpPoint;
    private String dropPoint;
    private String cancledBy;
    private String cancleReason;
    private double distance;

    public double getDriverEarnings() {
        return driverEarnings;
    }

    public void setDriverEarnings(double driverEarnings) {
        this.driverEarnings = driverEarnings;
    }

    private double driverEarnings;
    private double time;
    private double cost;
    private String userNotificationId;
    private String driverNotificationId;
    private String vehicleType;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public PickUpLocation getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(PickUpLocation pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public DropLocation getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(DropLocation dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getPickUpPoint() {
        return pickUpPoint;
    }

    public void setPickUpPoint(String pickUpPoint) {
        this.pickUpPoint = pickUpPoint;
    }

    public String getDropPoint() {
        return dropPoint;
    }

    public void setDropPoint(String dropPoint) {
        this.dropPoint = dropPoint;
    }

    public String getCancledBy() {
        return cancledBy;
    }

    public void setCancledBy(String cancledBy) {
        this.cancledBy = cancledBy;
    }

    public String getCancleReason() {
        return cancleReason;
    }

    public void setCancleReason(String cancleReason) {
        this.cancleReason = cancleReason;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getUserNotificationId() {
        return userNotificationId;
    }

    public void setUserNotificationId(String userNotificationId) {
        this.userNotificationId = userNotificationId;
    }

    public String getDriverNotificationId() {
        return driverNotificationId;
    }

    public void setDriverNotificationId(String driverNotificationId) {
        this.driverNotificationId = driverNotificationId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public static LastTrips parse(String userData) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(userData, LastTrips.class);
    }

    public static String toJson(LastTrips user) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(user);
    }

}


