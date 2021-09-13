package com.jambacabs.driver.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RideData {

    LastTrips lastTrips;
    TodayTrips todayTrips;

    public LastTrips getLastTrips() {
        return lastTrips;
    }

    public void setLastTrips(LastTrips lastTrips) {
        this.lastTrips = lastTrips;
    }

    public TodayTrips getTodayTrips() {
        return todayTrips;
    }

    public void setTodayTrips(TodayTrips todayTrips) {
        this.todayTrips = todayTrips;
    }

    public static RideData parse(String userData) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(userData, RideData.class);
    }

    public static String toJson(RideData user) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(user);
    }

}
