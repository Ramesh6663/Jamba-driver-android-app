package com.jambacabs.driver.models;

public class JourneyDetails
{
    private double latitude;
    private double longitude;
    private String date_time;
    private double duration;
    private boolean isOffRoute;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private double distance;

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isOffRoute() {
        return isOffRoute;
    }

    public void setOffRoute(boolean offRoute) {
        isOffRoute = offRoute;
    }
}
