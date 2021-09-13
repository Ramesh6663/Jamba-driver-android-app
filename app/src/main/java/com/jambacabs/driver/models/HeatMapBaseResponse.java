package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

import java.util.List;

public class HeatMapBaseResponse {
    @Expose
    private String type;

    @Expose
    private String message;

    @Expose
    private List<HeatMapData> data;


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

    public List<HeatMapData> getData() {
        return data;
    }

    public void setData(List<HeatMapData> data) {
        this.data = data;
    }
}
