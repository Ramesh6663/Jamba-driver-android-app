
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class DriverDataBean {

    @Expose
    private Data data;
    @Expose
    private String message;
    @Expose
    private String type;

    public Data getData() {
        return data;
    }

    public void Spl(Data data) {
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

    public class AccountDetails {

        @Expose
        private String accountName;
        @Expose
        private Long accountNumber;
        @Expose
        private String ifsc;

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public Long getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(Long accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIfsc() {
            return ifsc;
        }

        public void setIfsc(String ifsc) {
            this.ifsc = ifsc;
        }

    }

    public class Data {

        @Expose
        private Object aadhaarCard;
        @Expose
        private Object aadhaarCardBack;
        @Expose
        private Object bankPassbook;
        @Expose
        private String accessToken;
        @Expose
        private AccountDetails accountDetails;
        @Expose
        private Boolean available;
        @Expose
        private Object avatar;
        @Expose
        private String city;
        @Expose
        private Object drivingLicenseBack;
        @Expose
        private long drivingLicenseExpairy;
        @Expose
        private String drivingLicenseFront;
        @Expose
        private String email;
        @Expose
        private String firstName;
        @Expose
        private String inviteCode;
        @Expose
        private String lastName;
        @Expose
        private Long mobileNumber;
        @Expose
        private double driverWalet;
        @Expose
        private String notificationId;
        @Expose
        private String password;
        @Expose
        private String razorPayAccountStatus;
        @Expose
        private String razorpayAccountNumber;
        @Expose
        private String refferalCode;
        @Expose
        private Object state;
        @Expose
        private String status;
        @Expose
        private Object statusReason;
        @Expose
        private double averageRating;

        public Object getAadhaarCardBack() {
            return aadhaarCardBack;
        }

        public void setAadhaarCardBack(Object aadhaarCardBack) {
            this.aadhaarCardBack = aadhaarCardBack;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(double averageRating) {
            this.averageRating = averageRating;
        }

        public Object getAadhaarCard() {
            return aadhaarCard;
        }

        public void setAadhaarCard(Object aadhaarCard) {
            this.aadhaarCard = aadhaarCard;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public AccountDetails getAccountDetails() {
            return accountDetails;
        }

        public void setAccountDetails(AccountDetails accountDetails) {
            this.accountDetails = accountDetails;
        }

        public Boolean getAvailable() {
            return available;
        }

        public void setAvailable(Boolean available) {
            this.available = available;
        }

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(Object avatar) {
            this.avatar = avatar;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Object getDrivingLicenseBack() {
            return drivingLicenseBack;
        }

        public void setDrivingLicenseBack(Object drivingLicenseBack) {
            this.drivingLicenseBack = drivingLicenseBack;
        }

        public long getDrivingLicenseExpairy() {
            return drivingLicenseExpairy;
        }

        public void setDrivingLicenseExpairy(long drivingLicenseExpairy) {
            this.drivingLicenseExpairy = drivingLicenseExpairy;
        }

        public String getDrivingLicenseFront() {
            return drivingLicenseFront;
        }

        public void setDrivingLicenseFront(String drivingLicenseFront) {
            this.drivingLicenseFront = drivingLicenseFront;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getInviteCode() {
            return inviteCode;
        }

        public void setInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Long getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(Long mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(String notificationId) {
            this.notificationId = notificationId;
        }

        public double getDriverWalet() {
            return driverWalet;
        }

        public void setDriverWalet(double driverWalet) {
            this.driverWalet = driverWalet;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRazorPayAccountStatus() {
            return razorPayAccountStatus;
        }

        public void setRazorPayAccountStatus(String razorPayAccountStatus) {
            this.razorPayAccountStatus = razorPayAccountStatus;
        }

        public String getRazorpayAccountNumber() {
            return razorpayAccountNumber;
        }

        public void setRazorpayAccountNumber(String razorpayAccountNumber) {
            this.razorpayAccountNumber = razorpayAccountNumber;
        }

        public String getRefferalCode() {
            return refferalCode;
        }

        public void setRefferalCode(String refferalCode) {
            this.refferalCode = refferalCode;
        }

        public Object getState() {
            return state;
        }

        public void setState(Object state) {
            this.state = state;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getStatusReason() {
            return statusReason;
        }

        public void setStatusReason(Object statusReason) {
            this.statusReason = statusReason;
        }
        public Object getBankPassbook() {
            return bankPassbook;
        }

        public void setBankPassbook(Object bankPassbook) {
            this.bankPassbook = bankPassbook;
        }
    }

}
