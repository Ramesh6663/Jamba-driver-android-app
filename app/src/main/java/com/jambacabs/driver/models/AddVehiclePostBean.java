
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class AddVehiclePostBean {

    @Expose
    private Boolean available;
    @Expose
    private Long driverId;
    @Expose
    private String insurancePolicy;
    @Expose
    private Long insurancePolicyExpiryDate;
    @Expose
    private String licenseNumber;
    @Expose
    private String make;
    @Expose
    private String model;
    @Expose
    private String pollutionUnderControl;
    @Expose
    private Long pollutionUnderControlExpiryDate;
    @Expose
    private String registrationCertificateBack;
    @Expose
    private Long registrationCertificateExpiryDate;
    @Expose
    private String registrationCertificateFront;
    @Expose
    private String vehicleType;
    @Expose
    private String permitCertificate;
    @Expose
    private String fitnessCertificate;
    @Expose
    private long fitnessCertificateExpiryDate;
    @Expose
    private long permitCertificateExpiryDate;
    @Expose
    private Long year;

    public long getFitnessCertificateExpiryDate() {
        return fitnessCertificateExpiryDate;
    }

    public void setFitnessCertificateExpiryDate(long fitnessCertificateExpiryDate) {
        this.fitnessCertificateExpiryDate = fitnessCertificateExpiryDate;
    }

    public long getPermitCertificateExpiryDate() {
        return permitCertificateExpiryDate;
    }

    public void setPermitCertificateExpiryDate(long permitCertificateExpiryDate) {
        this.permitCertificateExpiryDate = permitCertificateExpiryDate;
    }

    public String getPermitCertificate() {
        return permitCertificate;
    }

    public void setPermitCertificate(String permitCertificate) {
        this.permitCertificate = permitCertificate;
    }

    public String getFitnessCertificate() {
        return fitnessCertificate;
    }

    public void setFitnessCertificate(String fitnessCertificate) {
        this.fitnessCertificate = fitnessCertificate;
    }

    @Expose
    private boolean availableForDelivery;


    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getInsurancePolicy() {
        return insurancePolicy;
    }

    public void setInsurancePolicy(String insurancePolicy) {
        this.insurancePolicy = insurancePolicy;
    }


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPollutionUnderControl() {
        return pollutionUnderControl;
    }

    public void setPollutionUnderControl(String pollutionUnderControl) {
        this.pollutionUnderControl = pollutionUnderControl;
    }


    public String getRegistrationCertificateBack() {
        return registrationCertificateBack;
    }

    public void setRegistrationCertificateBack(String registrationCertificateBack) {
        this.registrationCertificateBack = registrationCertificateBack;
    }



    public String getRegistrationCertificateFront() {
        return registrationCertificateFront;
    }

    public void setRegistrationCertificateFront(String registrationCertificateFront) {
        this.registrationCertificateFront = registrationCertificateFront;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }


    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getInsurancePolicyExpiryDate() {
        return insurancePolicyExpiryDate;
    }

    public void setInsurancePolicyExpiryDate(Long insurancePolicyExpiryDate) {
        this.insurancePolicyExpiryDate = insurancePolicyExpiryDate;
    }

    public Long getPollutionUnderControlExpiryDate() {
        return pollutionUnderControlExpiryDate;
    }

    public void setPollutionUnderControlExpiryDate(Long pollutionUnderControlExpiryDate) {
        this.pollutionUnderControlExpiryDate = pollutionUnderControlExpiryDate;
    }

    public Long getRegistrationCertificateExpiryDate() {
        return registrationCertificateExpiryDate;
    }

    public void setRegistrationCertificateExpiryDate(Long registrationCertificateExpiryDate) {
        this.registrationCertificateExpiryDate = registrationCertificateExpiryDate;
    }

    public boolean isAvailableForDelivery() {
        return availableForDelivery;
    }

    public void setAvailableForDelivery(boolean availableForDelivery) {
        this.availableForDelivery = availableForDelivery;
    }
}
