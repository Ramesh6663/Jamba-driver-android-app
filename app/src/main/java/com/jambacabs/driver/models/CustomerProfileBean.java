
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class CustomerProfileBean {

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

        @Expose
        private String avatar;
        @Expose
        private String email;
        @Expose
        private String firstName;
        @Expose
        private String lastName;
        @Expose
        private Long mobileNumber;
        @Expose
        private String status;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
