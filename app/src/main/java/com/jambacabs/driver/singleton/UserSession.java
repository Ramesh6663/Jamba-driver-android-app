package com.jambacabs.driver.singleton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;


public class UserSession
{
    // Shared Preferences reference
    private  SharedPreferences pref;

    // Editor reference for Shared preferences
    private SharedPreferences.Editor editor;

    // Shared preferences file location_name
    private static final String PREFER_NAME = "JAMBA";

    private static final String IS_USER_LOGIN = "login";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_DATA = "user_data";
    private static final String TOKEN = "token";
    private static final String USER_ID = "user_id";
    private static final String AVAILABILITY = "availability";
    private static final String VEHICLE_TYPE = "vehicle_type";
    private static final String DELIVERY_AVAILABLE = "delivery_available";
    private static final String DRIVER_VEHICLE = "driver_vehicle";
    private static final String VEHICLE_NAME = "veicle_name";
    private static final String TAGFROM = "tag_from";
    private static final String RIDE_INFO = "ride_info";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SELECTED_VEHICLE_INFO = "selected_vehicle_info";
    private static final String SCREEN = "screen";
    private static final String PUSH_PARAMS = "push_params";
    private static final String NOTIFICATION_RECEIVED_TIME = "notification_received_time";
    private static final String VEHICLE_AVAILABILITY = "vehicle_availability";
    private static final String ACCOUNT = "account";
    private static final String ACCOUNT_IFSC = "account_ifsc";
    private static final String ACCOUNT_HOLDER_NAME = "account_holder_name";
    private static final String RIDE_ACCEPT = "ride_accept";
    private static final String RIDE_START = "ride_Start";
    private static final String LOCATION_REACHED = "location_reached";
    private static final String NAVIGATION_STARTED = "navigation_started";
    private static final String TRIP_SUCCESS = "trip_success";
    private static final String VEHICLE_DATA = "vehicle_data";
    private static final String INCITY_SELECTED = "incity_selected";
    private static final String OUT_STATION_SELECTED = "out_station_selected";
    private static final String USER_PICKUP = "is_user_pickup";
    private static final String PICKUP_PARAMS = "pickup_params";
    private static final String COMPLETE_PARAMS = "complete_params";
    private static final String DOCUMENTS_PENDING = "documents_pending";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String VERIFIED = "verified";
    private static final String INPUTDATA = "inputData";
    private static final String INPUTNUMBER = "inputNumber";
    private static final String FLOATING = "floating";
    private static final String CHAT_DETAILS = "chatDetails";
    private static final String MAPAPI = "map_api";
    private static final String DECLINED_CLICKED = "decliend_clicked";
    private static final String MAPBOXKEY = "MAPBOXKEY";

    @SuppressLint("CommitPrefEdits")
    public UserSession(Context context) {
        // Context
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);
        // commit changes
        editor.apply();
    }

    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }


    public void setAccessToken(String value) {
        editor.putString(ACCESS_TOKEN, value);
        editor.apply();
    }

    public String getAccessToken() {
        return pref.getString(ACCESS_TOKEN, "");
    }

    public void removeAccessToken()
    {
        editor.remove(ACCESS_TOKEN);
        editor.apply();
    }

    public void setUserData(String value) {
        editor.putString(USER_DATA, value);
        editor.apply();
    }
    public String getUserData() {
        return pref.getString(USER_DATA, "");
    }

    public void removeUserData()
    {
        editor.remove(USER_DATA);
        editor.apply();
    }

    public void clearUserSession() {
        editor.clear();
        editor.apply();
    }

    public void setToken(String token)
    {
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return pref.getString(TOKEN, "");
    }

    public void removeToken()
    {
        editor.remove(TOKEN);
        editor.apply();
    }

    public void setUserID(String number)
    {
        editor.putString(USER_ID, number);
        editor.apply();
    }

    public String getUserId() {
        return pref.getString(USER_ID, "");
    }

    public void removeUserID()
    {
        editor.remove(USER_ID);
        editor.apply();
    }


    public void setDriverExpiryDate(long number)
    {
        editor.putLong(EXPIRY_DATE, number);
        editor.apply();
    }

    public long getDriverExpiryDate() {
        return pref.getLong(EXPIRY_DATE, 0);
    }

    public void removeExpiryDate()
    {
        editor.remove(EXPIRY_DATE);
        editor.apply();
    }


    public void setDriverAvailability(boolean status)
    {
        editor.putBoolean(AVAILABILITY, status);
        editor.apply();
    }

    public boolean getDriverAvailability() {
        return pref.getBoolean(AVAILABILITY, false);
    }

    public void setVehicleType(String status)
    {
        editor.putString(VEHICLE_TYPE, status);
        editor.apply();
    }

    public String getVehicleType() {
        return pref.getString(VEHICLE_TYPE, "");
    }

    public void removeVehicleType()
    {
        editor.remove(VEHICLE_TYPE);
        editor.apply();
    }


    public void setAvailableDelivery(boolean status)
    {
        editor.putBoolean(DELIVERY_AVAILABLE, status);
        editor.apply();
    }

    public boolean getAvailableDelivery() {
        return pref.getBoolean(DELIVERY_AVAILABLE, false);
    }

    public void removeAvailableDelivery()
    {
        editor.remove(DELIVERY_AVAILABLE);
        editor.apply();
    }

    public void setDriverVehicle(String status)
    {
        editor.putString(DRIVER_VEHICLE, status);
        editor.apply();
    }

    public String getDriverVehicle() {
        return pref.getString(DRIVER_VEHICLE, "");
    }

    public void removeDriverVehicle()
    {
        editor.remove(DRIVER_VEHICLE);
        editor.apply();
    }


    public void setVehicleName(String status)
    {
        editor.putString(VEHICLE_NAME, status);
        editor.apply();
    }

    public String getVehicleName() {
        return pref.getString(VEHICLE_NAME, "");
    }

    public void removeVehicleName()
    {
        editor.remove(VEHICLE_NAME);
        editor.apply();
    }

    public void setTagFrom(String status)
    {
        editor.putString(TAGFROM, status);
        editor.apply();
    }

    public String getTagFrom() {
        return pref.getString(TAGFROM, "");
    }

    public void removeTagFrom()
    {
        editor.remove(TAGFROM);
        editor.apply();
    }

    public void setRideInfo(String status)
    {
        editor.putString(RIDE_INFO, status);
        editor.apply();
    }

    public String getRideInfo() {
        return pref.getString(RIDE_INFO, "");
    }

    public void removeRideInfo()
    {
        editor.remove(RIDE_INFO);
        editor.apply();
    }

    public void setLatitude(String latitude)
    {
        editor.putString(LATITUDE, latitude);
        editor.apply();
    }

    public String getLatitude() {
        return pref.getString(LATITUDE, "");
    }

    public void removeLatitude()
    {
        editor.remove(LATITUDE);
        editor.apply();
    }

    public void setLongitude(String latitude)
    {
        editor.putString(LONGITUDE, latitude);
        editor.apply();
    }

    public String getLongitude() {
        return pref.getString(LONGITUDE, "");
    }

    public void removeLongitude()
    {
        editor.remove(LONGITUDE);
        editor.apply();
    }


    public void setSelectedVehicleInfo(String str_vehicle)
    {
        editor.putString(SELECTED_VEHICLE_INFO, str_vehicle);
        editor.apply();
    }

    public String getSelectedVehicleInfo() {
        return pref.getString(SELECTED_VEHICLE_INFO, "");
    }

    public void removeSelectedVehicleInfo()
    {
        editor.remove(SELECTED_VEHICLE_INFO);
        editor.apply();
    }

    public void setScreen(String screen)
    {
        editor.putString(SCREEN, screen);
        editor.apply();
    }

    public String getScreen() {
        return pref.getString(SCREEN, "");
    }

    public void removeScreen()
    {
        editor.remove(SCREEN);
        editor.apply();
    }

    public void setPushParams(String params)
    {
        editor.putString(PUSH_PARAMS, params);
        editor.apply();
    }

    public String getPushParams() {
        return pref.getString(PUSH_PARAMS, "");
    }

    public void removePushparams()
    {
        editor.remove(PUSH_PARAMS);
        editor.apply();
    }

    public void setNotificationReceivedTime(long time)
    {
        editor.putLong(NOTIFICATION_RECEIVED_TIME, time);
        editor.apply();
    }

    public long getNotificationReceivedTime() {
        return pref.getLong(NOTIFICATION_RECEIVED_TIME, 0);
    }

    public void removeNotificaionReceiedTime()
    {
        editor.remove(NOTIFICATION_RECEIVED_TIME);
        editor.apply();
    }

    public void setVehicleAvailability(boolean status)
    {
        editor.putBoolean(VEHICLE_AVAILABILITY, status);
        editor.apply();
    }

    public boolean getVehicleAvailability() {
        return pref.getBoolean(VEHICLE_AVAILABILITY, false);
    }

    public void removeVehicleAvailability()
    {
        editor.remove(VEHICLE_AVAILABILITY);
        editor.apply();
    }


    public void setAccountIFSC(String account)
    {
        editor.putString(ACCOUNT_IFSC, account);
        editor.apply();
    }

    public String getAccountIFSC() {
        return pref.getString(ACCOUNT_IFSC, "");
    }

    public void setAccountHolderName(String account)
    {
        editor.putString(ACCOUNT_HOLDER_NAME, account);
        editor.apply();
    }

    public String getAccountHolderName() {
        return pref.getString(ACCOUNT_HOLDER_NAME, "");
    }

    public void setAccount(String account)
    {
        editor.putString(ACCOUNT, account);
        editor.apply();
    }

    public String getAccount() {
        return pref.getString(ACCOUNT, "");
    }

    public void removeAccount()
    {
        editor.remove(ACCOUNT);
        editor.apply();
    }

    public void setAcceptRide(boolean status)
    {
        editor.putBoolean(RIDE_ACCEPT, status);
        editor.apply();
    }

    public boolean getRideAccept() {
        return pref.getBoolean(RIDE_ACCEPT, false);
    }

    public void removeRideAccept()
    {
        editor.remove(RIDE_ACCEPT);
        editor.apply();
    }

    public void setStartRide(boolean status)
    {
        editor.putBoolean(RIDE_START, status);
        editor.apply();
    }

    public boolean getRideStatus() {
        return pref.getBoolean(RIDE_START, false);
    }

    public void removeRideStatus()
    {
        editor.remove(RIDE_START);
        editor.apply();
    }


    public void setLocationReached(boolean status)
    {
        editor.putBoolean(LOCATION_REACHED, status);
        editor.apply();
    }

    public boolean getLocationReached() {
        return pref.getBoolean(LOCATION_REACHED, false);
    }

    public void removeLocationReached()
    {
        editor.remove(LOCATION_REACHED);
        editor.apply();
    }

    public void setNavigationStarted(boolean status)
    {
        editor.putBoolean(NAVIGATION_STARTED, status);
        editor.apply();
    }

    public boolean getNavigationStarted() {
        return pref.getBoolean(NAVIGATION_STARTED, false);
    }

    public void removeNavigationStarted()
    {
        editor.remove(NAVIGATION_STARTED);
        editor.apply();
    }

    public void setTripSuccessData(String str)
    {
        editor.putString(TRIP_SUCCESS, str);
        editor.apply();
    }

    public String getTripSuccessData() {
        return pref.getString(TRIP_SUCCESS, "");
    }

    public void removeTripSuccessData()
    {
        editor.remove(TRIP_SUCCESS);
        editor.apply();
    }

    public void setFirstVehicleData(String value) {
        editor.putString(VEHICLE_DATA, value);
        editor.apply();
    }
    public String getFirstVehicleData() {
        return pref.getString(VEHICLE_DATA, "");
    }

    public void remoVeFirstVehicleData(){
        editor.remove(VEHICLE_DATA);
        editor.apply();
    }

    public void setIncityStatus(boolean status)
    {
        editor.putBoolean(INCITY_SELECTED, status);
        editor.apply();
    }

    public boolean getInCityStatus() {
        return pref.getBoolean(INCITY_SELECTED, false);
    }

    public void removeInCityStatus()
    {
        editor.remove(INCITY_SELECTED);
        editor.apply();
    }

    public void setOutstationStatus(boolean status)
    {
        editor.putBoolean(OUT_STATION_SELECTED, status);
        editor.apply();
    }

    public boolean getOutStationStatus() {
        return pref.getBoolean(OUT_STATION_SELECTED, false);
    }

    public void removeOutstationStatus()
    {
        editor.remove(OUT_STATION_SELECTED);
        editor.apply();
    }

    public void setIsUserPickup(boolean value) {
        editor.putBoolean(USER_PICKUP, value);
        editor.apply();
    }
    public boolean getUserPickUp() {
        return pref.getBoolean(USER_PICKUP, false);
    }

    public void removeUserPickup(){
        editor.remove(USER_PICKUP);
        editor.apply();
    }

    public void setPickupParams(String strParams) {
        editor.putString(PICKUP_PARAMS, strParams);
        editor.apply();
    }
    public String getPickupParams() {
        return pref.getString(PICKUP_PARAMS, "");
    }

    public void removePickupParams(){
        editor.remove(PICKUP_PARAMS);
        editor.apply();
    }

    public void setCompleteParams(String strParams) {
        editor.putString(COMPLETE_PARAMS, strParams);
        editor.apply();
    }
    public String getCompleteParams() {
        return pref.getString(COMPLETE_PARAMS, "");
    }

    public void removeCompleteParams(){
        editor.remove(COMPLETE_PARAMS);
        editor.apply();
    }


    public void setDocumentsPending(boolean value) {
        editor.putBoolean(DOCUMENTS_PENDING, value);
        editor.apply();
    }
    public boolean getDocumentsPending() {
        return pref.getBoolean(DOCUMENTS_PENDING, false);
    }

    public void removeDocumentsPending(){
        editor.remove(DOCUMENTS_PENDING);
        editor.apply();
    }

    public void setNumberVerified(boolean value) {
        editor.putBoolean(VERIFIED, value);
        editor.apply();
    }
    public boolean getNumberVerified() {
        return pref.getBoolean(VERIFIED, false);
    }

    public void removeNumberVerified(){
        editor.remove(VERIFIED);
        editor.apply();
    }

    public void setInputData(String value) {
        editor.putString(INPUTDATA, value);
        editor.apply();
    }
    public String getInputData() {
        return pref.getString(INPUTDATA, "");
    }

    public void removeInputData()
    {
        editor.remove(INPUTDATA);
        editor.apply();
    }

    public void setInoutNumber(String value) {
        editor.putString(INPUTNUMBER, value);
        editor.apply();
    }
    public String getInputNumber() {
        return pref.getString(INPUTNUMBER, "");
    }

    public void removeInputNumber()
    {
        editor.remove(INPUTNUMBER);
        editor.apply();
    }

    public void setFloating(boolean value) {
        editor.putBoolean(FLOATING, value);
        editor.apply();
    }
    public boolean getFloating() {
        return pref.getBoolean(FLOATING, false);
    }

    public void removeFloating(){
        editor.remove(FLOATING);
        editor.apply();
    }

    public void setChatDetails(String value) {
        editor.putString(CHAT_DETAILS, value);
        editor.apply();
    }
    public String getChatDetails() {
        return pref.getString(CHAT_DETAILS, "");
    }

    public void removeChatDetails()
    {
        editor.remove(CHAT_DETAILS);
        editor.apply();
    }
    public void setAPI(String value) {
        editor.putString(MAPAPI, value);
        editor.apply();
    }
    public String getAPI() {
        return pref.getString(MAPAPI, "");
    }

    public void setUserDeclinedClicked(boolean value) {
        editor.putBoolean(DECLINED_CLICKED, value);
        editor.apply();
    }
    public boolean getDeclined() {
        return pref.getBoolean(DECLINED_CLICKED, false);
    }

    public void removeDeclined(){
        editor.remove(DECLINED_CLICKED);
        editor.apply();
    }

    public void setMapBoxKey(String value) {
        editor.putString(MAPBOXKEY, value);
        editor.apply();
    }
    public String getMapboxkey() {
        return pref.getString(MAPBOXKEY, "");
    }
}
