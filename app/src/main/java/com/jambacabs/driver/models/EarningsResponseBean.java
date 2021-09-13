
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class EarningsResponseBean {

    @Expose
    private Data data;
    @Expose
    private String message;
    @Expose
    private String type;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public class Data {

        @SerializedName("List")
        private java.util.List<List> list;
        @Expose
        private String  totalErnings;
        @Expose
        private String totalRides;

        public java.util.List<List> getList() {
            return list;
        }

        public void setList(java.util.List<List> list) {
            this.list = list;
        }


        public String getTotalErnings() {
            return totalErnings;
        }

        public void setTotalErnings(String totalErnings) {
            this.totalErnings = totalErnings;
        }

        public String getTotalRides() {
            return totalRides;
        }

        public void setTotalRides(String totalRides) {
            this.totalRides = totalRides;
        }
    }

    public class List {

        @Expose
        private Long date;
        @Expose
        private Long driverId;
        @Expose
        private String ernings;
        @Expose
        private String id;
        @Expose
        private String rides;

        public Long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }

        public Long getDriverId() {
            return driverId;
        }

        public void setDriverId(Long driverId) {
            this.driverId = driverId;
        }


        public String getErnings() {
            return ernings;
        }

        public void setErnings(String ernings) {
            this.ernings = ernings;
        }

        public String getRides() {
            return rides;
        }

        public void setRides(String rides) {
            this.rides = rides;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


    }

}
