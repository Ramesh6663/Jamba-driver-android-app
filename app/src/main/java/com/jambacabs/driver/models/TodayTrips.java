package com.jambacabs.driver.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TodayTrips {

    private String amount;
    private String count;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public static TodayTrips parse(String userData) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(userData, TodayTrips.class);
    }

    public static String toJson(TodayTrips user) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(user);
    }

}
