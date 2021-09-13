package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

public class StatusModel
{
    @Expose
    private Data data;
    @Expose
    private Long mobileNumber;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public static class Data {

        @Expose
        private String status;
        @Expose
        private String statusReason;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusReason() {
            return statusReason;
        }

        public void setStatusReason(String statusReason) {
            this.statusReason = statusReason;
        }
    }
}
