
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class UpdateDriverBean {

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
        private String aadhaarCard;
        @Expose
        private String aadhaarCardBack;
        @Expose
        private String avatar;
        @Expose
        private String drivingLicenseBack;
        @Expose
        private String drivingLicenseFront;
        @Expose
        private String bankPassbook;

        public String getAadhaarCard() {
            return aadhaarCard;
        }

        public void setAadhaarCard(String aadhaarCard) {
            this.aadhaarCard = aadhaarCard;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDrivingLicenseBack() {
            return drivingLicenseBack;
        }

        public void setDrivingLicenseBack(String drivingLicenseBack) {
            this.drivingLicenseBack = drivingLicenseBack;
        }

        public String getDrivingLicenseFront() {
            return drivingLicenseFront;
        }

        public void setDrivingLicenseFront(String drivingLicenseFront) {
            this.drivingLicenseFront = drivingLicenseFront;
        }

        public String getBankPassbook() {
            return bankPassbook;
        }

        public void setBankPassbook(String bankPassbook) {
            this.bankPassbook = bankPassbook;
        }

        public String getAadhaarCardBack() {
            return aadhaarCardBack;
        }

        public void setAadhaarCardBack(String aadhaarCardBack) {
            this.aadhaarCardBack = aadhaarCardBack;
        }
    }

}
