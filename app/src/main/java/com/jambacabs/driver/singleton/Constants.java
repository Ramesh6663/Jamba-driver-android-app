package com.jambacabs.driver.singleton;

public class Constants {

    /*
     *
     *Database Names
     * Dharma Teja Kanneganti
     * Created On 17-04-2020
     *
     */

    public static final String JOURNEY = "journey";
    public static final String RIDE_DETAILS = "rideDetails";
    public static final String CHAT_DETAILS = "chatDetails";
    public static final String PUSH_URL = "https://fcm.googleapis.com/fcm/";
    public static final String SERVER_URL = "key=AAAAZzbHF_8:APA91bFbM0pI4_pWkMt0vilCI3nC6cLGpDSGwXfX9yT-AHksoO9UXEWwCZDqG3g78Gm4GUfL_kMlRCMeNWAQEAgjyRlnj6nl07oBjB5vS13KcO7WwL5ZvXiVQGpPZLq_8JPGTelMCLzp";
    public static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoidmlsbGFnZWl0IiwiYSI6ImNrYTZhcjAyMjA1NzAydHBmYTM3NWdoMWwifQ.ZD2mmUChBhizE65T0ojVFQ";
    /*
     * Dev URL's
     * Siva Sai Kumar
     * Created on: 10-03-2020
     * Modified By: Dharma Teja Kanneganti
     * Modified On : 10-03-20202020
     */

    /*public static final String URL = "http://ec2-52-66-179-250.ap-south-1.compute.amazonaws.com";
    public static final String DRIVER_URL = "http://ec2-13-233-153-124.ap-south-1.compute.amazonaws.com";
    public static final String RIDES_URL = "http://ec2-15-206-194-250.ap-south-1.compute.amazonaws.com";
    public static final String PAYMENTS_URL = "http://ec2-13-235-77-202.ap-south-1.compute.amazonaws.com";
    public static final String USERS_URL = "http://ec2-52-66-247-14.ap-south-1.compute.amazonaws.com";
    public static final String PLACES_URL = "http://ec2-52-66-253-100.ap-south-1.compute.amazonaws.com";*/

    /*
     * UAT URL's
     * Siva Sai Kumar
     * Created on: 10-03-2020
     * Modified By: Dharma Teja Kanneganti
     * Modified On : 10-03-2020
     */

   /* public static final String URL = "http://jamba-extras-56297847.ap-south-1.elb.amazonaws.com";
    public static final String DRIVER_URL = "http://jamba-service-1277409810.ap-south-1.elb.amazonaws.com";
    public static final String RIDES_URL = "http://jamba-rides-221736455.ap-south-1.elb.amazonaws.com";
    public static final String PAYMENTS_URL = "http://jamba-driver-928406000.ap-south-1.elb.amazonaws.com";
    public static final String USERS_URL = "http://jamba-customer-851321082.ap-south-1.elb.amazonaws.com";*/

    /**
     * Prod URL's
     * Created By : Dharma Teja Kanneganti
     * Created On : 12-03-2020
     */

    /*public static final String URL = "http://jamba-extras-13446224.ap-south-1.elb.amazonaws.com";
    public static final String DRIVER_URL = "http://jamba-service-246615551.ap-south-1.elb.amazonaws.com";
    public static final String RIDES_URL = "http://jamba-rides-351994325.ap-south-1.elb.amazonaws.com";
    public static final String PAYMENTS_URL = "http://jamba-driver-1003943067.ap-south-1.elb.amazonaws.com";
    public static final String USERS_URL = "http://jamba-customer-1787685231.ap-south-1.elb.amazonaws.com";*/

    public static final String URL = "http://13.233.22.74";
    public static final String DRIVER_URL = "http://15.206.95.15";
    public static final String RIDES_URL = "http://3.7.184.101";
    public static final String PAYMENTS_URL = "http://3.7.176.57";
    public static final String USERS_URL = "http://3.6.199.80";
    public static final String PLACES_URL = "http://13.232.245.236";

    public static final String LOGIN = "/drivers/login";
    public static final String SIGNUP = "/drivers/signup";
    public static final String UPDATE_DRIVER_LOCATION = "/updateDriverLocation";
    public static final String REMOVE_DRIVER_LOCATION = "/remove";
    public static final String ADD_VEHICLES = "/addVehicles";
    public static final String VEHICLES = "/vehicles";
    public static final String VEHICLES_NEW = "/vehicles/{id}";
    public static final String VEHICLE_TYPES = "/vehicle-types";
    public static final String UPDATE_VEHICLES = "/updateVehicle";
    public static final String UPDATE_DRIVER = "/updateDriver";
    public static final String UPDATE_DRIVER_DATA = "/updateDriverData";
    public static final String DISTANCE_API = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=";
    public static final String CONFIRM_BOOKING = "/confirmBooking";
    public static final String CANCEL_RIDE = "/cancleRide";
    public static final String START_RIDE = "/startRide";
    public static final String UPDATE_BANK_DETAILS = "/updateBankDetails";
    public static final String NOTIFICATIONS_READ = "/updateParticularNotification";
    public static final String DRIVER_LOGOUT = "/driverLogout";
    public static final String GET_DRIVER_EARNINGS = "/getDriverErnings";
    public static final String DRIVER_WITHDRAW = "/driverWithdraw";
    public static final String TODAY_TRIPS = "/todayTrips";
    public static final String PICKUP_CUSTOMER = "/pickUpCustomer";
    public static final String COMPLETE_RIDE = "/CompleteRide";
    public static final String IMAGE_UPLOAD = "/imageupload";
    public static final String VEHECLE_MODELS_NEW = "/vehicleModels";
    public static final String ALL_RIDES = "/getAllRidesForDrivers";
    public static final String GET_PARTICULAR_RIDE = "/getParticularDrivers/{id}";
    public static final String GET_DRIVER_TRANSACTIONS = "/getTransactions/{driverId}";
    public static final String FORGOT_PASSWORD = "/forgotPassword";
    public static final String RESET_PASSWORD = "/resetPassword";
    public static final String ALL_NOTIFICATIONS = "/allNotification";
    public static final String CANCEL_REASONS = "/cancelReasons";
    public static final String GET_CUSTOMER_PROFILE = "/signups/{id}";
    public static final String GET_DYNAMIC_PAGES_NEW = "/dynamicPages";
    public static final String ALL_NOTOFICATION_COUNT = "/allNotificationsCount";
    public static final String GET_PERTICULAR_VEHICLE = "/vehicle-types/{id}";
    //    public static final String GET_OTP = "/generateOtp";
    public static final String GET_OTP = "/generateOtpForDriver";
    public static final String RESEND_OTP = "/resendOtpForDriver";
    static final String FIREBASE_ANALYTICS_OPEN_SCREEN = "open_screen";
    static final String FIREBASE_ANALYTICS_SCREEN_NAME = "screen_name";
    public static final String RESPONSE_SUCCESS = "success";
    public static final String DRIVER_STATUS_PENDING = "pending";
    public static final String DRIVER_STATUS_REJECT = "reject";
    public static final String MOVE_SCREEN_DOCUMENTS = "document";
    public static final String MOVE_SCREEN_VEHICLE = "vehicle";
    public static final String MOVE_SCREEN_VEHICLE_DOC = "vehicle_doc";
    public static final String DYNAMIC_PAGE_DRIVER = "Driver";
    public static final String DYNAMIC_PAGE_ALL = "All";
    public static final String VERIFY_OTP = "/verifyDriverOtp";
    public static final String SETTINGS = "settings";
    public static final String DRIVER = "driver";
    public static final String USER = "user";
    public static final String SEND = "send";
//    public static final String PLACE_SEARCH = "/placesSearch";
    public static final String PLACE_SEARCH = "/googlePlacesSearch";
    public static final String PLACE_UPDATE = "/googlePlacesUpdate";
    public static final String LOCATIONS = "/userlocations";
    public static final String GET_HEATMAPDATA = "/locations/{usertype}/{online}";
    public static final String ACCEPT_NOTIFICATON = "/drivers/acceptFleetRequest";
    public static final String REJECT_NOTIFICATON = "/drivers/rejectFleetRequest";
}