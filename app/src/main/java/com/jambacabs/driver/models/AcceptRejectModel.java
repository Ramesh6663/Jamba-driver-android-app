package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

public class AcceptRejectModel
{
    @Expose
    private String notificationId;
    @Expose
    private long driverId;
    @Expose
    private long fleetOwnerId;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public long getFleetOwnerId() {
        return fleetOwnerId;
    }

    public void setFleetOwnerId(long fleetOwnerId) {
        this.fleetOwnerId = fleetOwnerId;
    }
}
