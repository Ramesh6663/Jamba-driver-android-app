
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class EarningPostBean {

    @Expose
    private Long driverId;
    @Expose
    private Long endDate;
    @Expose
    private Long fromDate;

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

}
