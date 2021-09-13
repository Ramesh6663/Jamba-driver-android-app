package com.jambacabs.driver.models;

import com.anychart.editor.Step;
import com.google.gson.annotations.Expose;

public class RideModel
{
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
        private String rideId;
        @Expose
        private String status;
        @Expose
        private long startTime;
        @Expose
        private long endTime;
        @Expose
        private long userId;
        @Expose
        private long driverId;
        @Expose
        private Object pickUpLocation;
        @Expose
        private Object dropLocation;
        @Expose
        private String pickUpPoint;
        @Expose
        private String dropPoint;
        @Expose
        private String cancledBy;
        @Expose
        private String cancleReason;
        @Expose
        private double distance;
        @Expose
        private double time;
        @Expose
        private double cost;
        @Expose
        private double totalCost;
        @Expose
        private String userNotificationId;
        @Expose
        private String driverNotificationId;
        @Expose
        private String vehicleType;
        @Expose
        private String paymentType;
        @Expose
        private Object costObject;
        @Expose
        private double returnTime;
        @Expose
        private String rideType;
        @Expose
        private String discountType;
        @Expose
        private double discount;
        @Expose
        private double discountAmount;
        @Expose
        private boolean roundTrip;
        @Expose
        private long bookingTime;
        @Expose
        private long acceptedTime;
        @Expose
        private int serialNumberOfDriver;
        @Expose
        private double rideGst;
        @Expose
        private double rideCommission;
        @Expose
        private String vehicleAvatar;

        public String getRideId() {
            return rideId;
        }

        public void setRideId(String rideId) {
            this.rideId = rideId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public long getDriverId() {
            return driverId;
        }

        public void setDriverId(long driverId) {
            this.driverId = driverId;
        }

        public Object getPickUpLocation() {
            return pickUpLocation;
        }

        public void setPickUpLocation(Object pickUpLocation) {
            this.pickUpLocation = pickUpLocation;
        }

        public Object getDropLocation() {
            return dropLocation;
        }

        public void setDropLocation(Object dropLocation) {
            this.dropLocation = dropLocation;
        }

        public String getPickUpPoint() {
            return pickUpPoint;
        }

        public void setPickUpPoint(String pickUpPoint) {
            this.pickUpPoint = pickUpPoint;
        }

        public String getDropPoint() {
            return dropPoint;
        }

        public void setDropPoint(String dropPoint) {
            this.dropPoint = dropPoint;
        }

        public String getCancledBy() {
            return cancledBy;
        }

        public void setCancledBy(String cancledBy) {
            this.cancledBy = cancledBy;
        }

        public String getCancleReason() {
            return cancleReason;
        }

        public void setCancleReason(String cancleReason) {
            this.cancleReason = cancleReason;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getTime() {
            return time;
        }

        public void setTime(double time) {
            this.time = time;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        public String getUserNotificationId() {
            return userNotificationId;
        }

        public void setUserNotificationId(String userNotificationId) {
            this.userNotificationId = userNotificationId;
        }

        public String getDriverNotificationId() {
            return driverNotificationId;
        }

        public void setDriverNotificationId(String driverNotificationId) {
            this.driverNotificationId = driverNotificationId;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public Object getCostObject() {
            return costObject;
        }

        public void setCostObject(Object costObject) {
            this.costObject = costObject;
        }

        public double getReturnTime() {
            return returnTime;
        }

        public void setReturnTime(double returnTime) {
            this.returnTime = returnTime;
        }

        public String getRideType() {
            return rideType;
        }

        public void setRideType(String rideType) {
            this.rideType = rideType;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public double getDiscount() {
            return discount;
        }

        public void setDiscount(double discount) {
            this.discount = discount;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
        }

        public boolean isRoundTrip() {
            return roundTrip;
        }

        public void setRoundTrip(boolean roundTrip) {
            this.roundTrip = roundTrip;
        }

        public long getBookingTime() {
            return bookingTime;
        }

        public void setBookingTime(long bookingTime) {
            this.bookingTime = bookingTime;
        }

        public long getAcceptedTime() {
            return acceptedTime;
        }

        public void setAcceptedTime(long acceptedTime) {
            this.acceptedTime = acceptedTime;
        }

        public int getSerialNumberOfDriver() {
            return serialNumberOfDriver;
        }

        public void setSerialNumberOfDriver(int serialNumberOfDriver) {
            this.serialNumberOfDriver = serialNumberOfDriver;
        }

        public double getRideGst() {
            return rideGst;
        }

        public void setRideGst(double rideGst) {
            this.rideGst = rideGst;
        }

        public double getRideCommission() {
            return rideCommission;
        }

        public void setRideCommission(double rideCommission) {
            this.rideCommission = rideCommission;
        }

        public String getVehicleAvatar() {
            return vehicleAvatar;
        }

        public void setVehicleAvatar(String vehicleAvatar) {
            this.vehicleAvatar = vehicleAvatar;
        }
    }
}
