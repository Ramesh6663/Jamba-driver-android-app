package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.adaptor.VehiclesAdaptor;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.callbacks.IVehicles;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.DriverVehicleModel;
import com.jambacabs.driver.models.VehicleTypes;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public class Vehicles extends Activity implements View.OnClickListener, IVehicles {
    private SearchView searchBar;

    private RecyclerView rvApproved, rvPending;
    private RelativeLayout rlMain, rlProgress;
    private CustomTextView tvApproved, tvRejected;
    private VehiclesAdaptor adaptor;
    private VehiclesAdaptor rejectadaptor;
    private ArrayList<JSONObject> arr_approved;
    private ArrayList<JSONObject> arr_rejected;
    private boolean fromSplash = false;
    private boolean is_driver_available;
    private JSONObject dict_selected_vehicle_element;
    private int current_pos, previous_pos;
    private Geocoder geocoder;
    private boolean is_called;
    private boolean isBackPressed;
    private ArrayList<VehicleTypes> arrVehicleTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicles_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(Vehicles.this);

        searchBar = findViewById(R.id.searchBar);
        rvApproved = findViewById(R.id.rvApproved);
        rvPending = findViewById(R.id.rvPending);
        RelativeLayout rlVehicleBack = findViewById(R.id.rlVehicleBack);
        rlProgress = findViewById(R.id.rlProgress);
        tvApproved = findViewById(R.id.tvApproved);
        tvRejected = findViewById(R.id.tvRejected);
        rlMain = findViewById(R.id.rlMain);
        arrVehicleTypes = new ArrayList<>();
        is_called = false;
        geocoder = new Geocoder(Vehicles.this, Locale.getDefault());
        arr_approved = new ArrayList<>();
        arr_rejected = new ArrayList<>();
        searchBar.setVisibility(View.GONE);
        is_driver_available = new UserSession(Vehicles.this).getDriverAvailability();
        if (getIntent().getExtras() != null) {
            fromSplash = getIntent().getExtras().getBoolean("fromSplash");
            if (fromSplash) {
                rlVehicleBack.setVisibility(View.GONE);
            }
        }
        if (Utilities.isInternetAvailable(Vehicles.this))
        {
            showLoading();
            getVehicleTypes();
//            getVehicles();
        }else
        {
            Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
        }
    }

    private void getVehicleTypes()
    {
        String appendUrl = Constants.URL + Constants.VEHICLE_TYPES;

        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, appendUrl, null,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals(getResources().getString(R.string.success))) {
                            JSONArray arrData = response.optJSONArray("data");
                            assert arrData != null;
                            arrVehicleTypes = new ArrayList<>();
                            for (int i = 0; i < arrData.length(); i++) {
                                JSONObject dictData = arrData.optJSONObject(i);
                                String vehicleModel = dictData.optString("vehicleModel");
                                String avatar = dictData.optString("Avatar");
                                boolean availability = dictData.optBoolean("Availability");
                                String strSeats = dictData.optString("Seats");
                                String strTypes = dictData.optString("Types");
                                VehicleTypes vt = new VehicleTypes();
                                vt.setVehicleModel(vehicleModel);
                                vt.setAvatar(avatar);
                                vt.setAvailability(availability);
                                vt.setTypes(strTypes);
                                vt.setSeats(strSeats);
                                arrVehicleTypes.add(vt);
                            }
                            if (arrVehicleTypes.size()>0)
                            {
                                getVehicles();
                            }else
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                            }


                        }
                    } else {
                        hideLoading();
                    }

                },
                error -> {

                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            if (Utilities.isInternetAvailable(Vehicles.this))
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                                JambaCabApplication.getInstance().removeDriver(rlMain);
                            }else
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                            }
                        }else
                        {
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.xCustomToken), new UserSession(Vehicles.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getVehicles() {
        String user_id = new UserSession(this).getUserId();
        String append_url = Constants.URL + Constants.VEHICLES + "/" + user_id;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, append_url, new JSONObject(),
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            JSONArray arr_data = response.optJSONArray("data");
                            if (arr_data != null && arrVehicleTypes != null)
                            {

                                for (int i = 0; i < arr_data.length(); i++) {
                                    JSONObject dict_data = arr_data.optJSONObject(i);
                                    boolean is_available = dict_data.optBoolean("available");
                                    String vehicleType = dict_data.optString("vehicleType");

                                    for (int x=0; x<arrVehicleTypes.size(); x++)
                                    {
                                        VehicleTypes vt = arrVehicleTypes.get(x);
                                        String vehicleModel = vt.getVehicleModel();
                                        if (vehicleType.equals(vehicleModel))
                                        {
                                            try {
                                                dict_data.put("type", vt.getTypes());
                                            } catch (JSONException e) {
                                                Timber.v("type error%s", e.getLocalizedMessage());
                                            }
                                        }
                                    }

                                    if (is_available) {

                                        String make = dict_data.optString("make");
                                        String model = dict_data.optString("model");
                                        String type = dict_data.optString("type");
                                        String vehicle_name = make + " " + model;
                                        new UserSession(Vehicles.this).setSelectedVehicleInfo(dict_data.toString());
                                        new UserSession(Vehicles.this).setVehicleType(vehicleType);
                                        new UserSession(Vehicles.this).setVehicleName(vehicle_name);
                                        new UserSession(Vehicles.this).setVehicleAvailability(is_available);
                                        if (dict_data.has("availableForDelivery") && type.equals("bike"))
                                        {
                                            new UserSession(Vehicles.this).setAvailableDelivery(dict_data.optBoolean("availableForDelivery"));
                                        }else
                                        {
                                            try {
                                                dict_data.put("availableForDelivery", false);
                                            } catch (JSONException e) {
                                                Timber.v("ParseError%s", e.getLocalizedMessage());
                                            }
                                        }
                                        if (Utilities.isInternetAvailable(Vehicles.this))
                                        {
                                            getDriverVehicle(vehicleType);
                                        }else
                                        {
                                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                                        }
                                    }else
                                    {
                                        if (!dict_data.has("availableForDelivery"))
                                        {
                                            try {
                                                dict_data.put("availableForDelivery", false);
                                            } catch (JSONException e) {
                                                Timber.v("ParseError%s", e.getLocalizedMessage());
                                            }
                                        }
                                    }
                                    if (dict_data.optBoolean("approved"))
                                    {
                                        arr_approved.add(dict_data);
                                    }else
                                    {
                                        arr_rejected.add(dict_data);
                                    }
                                }
                            }

                            if (arr_approved.size() == 0) {
                                searchBar.setVisibility(View.GONE);
                                Utilities.hideSoftKeyboard(Vehicles.this);
                                rvApproved.setVisibility(View.GONE);
                                tvApproved.setVisibility(View.VISIBLE);
                            } else {
                                searchBar.setVisibility(View.GONE);
                                rvApproved.setVisibility(View.VISIBLE);
                                tvApproved.setVisibility(View.GONE);
                                adaptor = new VehiclesAdaptor(Vehicles.this, Vehicles.this, true);
                                rvApproved.setLayoutManager(new LinearLayoutManager(Vehicles.this));
                                adaptor.setListContent(arr_approved);
                                rvApproved.setAdapter(adaptor);
                                adaptor.notifyDataSetChanged();
                            }
                            if (arr_rejected.size() == 0) {
                                rvPending.setVisibility(View.GONE);
                                tvRejected.setVisibility(View.VISIBLE);
                            } else {
                                rvPending.setVisibility(View.VISIBLE);
                                tvRejected.setVisibility(View.GONE);

                                rejectadaptor = new VehiclesAdaptor(Vehicles.this, Vehicles.this, false);
                                rvPending.setLayoutManager(new LinearLayoutManager(Vehicles.this));
                                rejectadaptor.setListContent(arr_rejected);
                                rvPending.setAdapter(rejectadaptor);
                                rejectadaptor.notifyDataSetChanged();

                            }
                        } else {
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            showLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(rlMain);
                        }else
                        {
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Vehicles.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getDriverVehicle(String type)
    {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);
        Call<DriverVehicleModel> call = apiService.getPerticularVehicle(new UserSession(Vehicles.this).getAccessToken(), type);
        call.enqueue(new Callback<DriverVehicleModel>() {

            @Override
            public void onResponse(@NonNull Call<DriverVehicleModel> call, @NonNull retrofit2.Response<DriverVehicleModel> response) {
                if (response.code() == 403) {
                    showLoading();
                    Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                    JambaCabApplication.getInstance().removeDriver(rlMain);
                }else
                {
                    if (response.body() != null)
                    {
                        DriverVehicleModel model = response.body();
                        new UserSession(Vehicles.this).setDriverVehicle(model.getTypes());
                    }else
                    {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<DriverVehicleModel> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
            }
        });
    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlVehicleBack) {
            onBackPressed();
        } else if (id == R.id.searchBar) {
            searchBar.setIconified(false);
        } else if (id == R.id.rlAddVehicle) {
            Intent intent = new Intent(Vehicles.this, AddVehicleActivity.class);
            intent.putExtra("fromSplash", fromSplash);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        }
    }

    @Override
    public void onVehiclesClicked(JSONObject dict_element, int current_position, int previous_position, boolean isUpdateVehicle, boolean isApproved) {
        if (isUpdateVehicle) {
            Intent intentVehicleUpdate = new Intent(Vehicles.this, AddVehicleActivity.class);
            intentVehicleUpdate.putExtra("is_full_update", true);
            intentVehicleUpdate.putExtra("vehicle_data", dict_element.toString());
            intentVehicleUpdate.putExtra("isApproved", isApproved);
            startActivity(intentVehicleUpdate);
        } else {
            double latitude = Double.parseDouble(new UserSession(Vehicles.this).getLatitude());
            double longitude = Double.parseDouble(new UserSession(Vehicles.this).getLongitude());
            String str_vehicle_type = new UserSession(this).getVehicleType();
            String notificationId = new UserSession(this).getToken();
            String str_driverId = new UserSession(this).getUserId();
            double driverId = Double.parseDouble(str_driverId);
            String subLocality = "";
            JSONObject dict_params = new JSONObject();
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    subLocality = addresses.get(0).getSubLocality();
                    if (subLocality == null || subLocality.equals("")) {
                        subLocality = addresses.get(0).getLocality();
                    }
                }
                dict_params.put("driverId", driverId);
                dict_params.put("latitude", latitude);
                dict_params.put("longitude", longitude);
                dict_params.put("city", subLocality);
                dict_params.put("vehicleType", str_vehicle_type);
                dict_params.put("notificationId", notificationId);
//                dict_params.put("availableForDelivery", new UserSession(Vehicles.this).getAvailableDelivery());


            } catch (IOException | JSONException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }

            String vehicle_info = new UserSession(this).getSelectedVehicleInfo();
            if (!vehicle_info.equals("")) {
                try {
                    JSONObject dict_vehicle_info = new JSONObject(vehicle_info);
                    boolean previous_selected_vehicle_available = dict_vehicle_info.optBoolean("available");
                    String previous_license = dict_vehicle_info.optString("licenseNumber");
                    String current_licence_number = dict_element.optString("licenseNumber");

                    if (previous_license.equals(current_licence_number)) {
                        if (previous_selected_vehicle_available) {
                            Utilities.showMessage(rlMain, getResources().getString(R.string.vehicle_selection_error));
                            adaptor = new VehiclesAdaptor(Vehicles.this, Vehicles.this, true);
                            rvApproved.setLayoutManager(new LinearLayoutManager(Vehicles.this));
                            adaptor.setListContent(arr_approved);
                            rvApproved.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();
                        } else {
                            dict_selected_vehicle_element = dict_element;
                            current_pos = current_position;
                            previous_pos = previous_position;
                            if (is_driver_available)
                            {
                                is_called = true;
                                if (Utilities.isInternetAvailable(Vehicles.this))
                                {
                                    dict_params.put("availableForDelivery", dict_vehicle_info.optBoolean("availableForDelivery"));
                                    boolean is_in_city_status = new UserSession(Vehicles.this).getInCityStatus();
                                    boolean is_out_station = new UserSession(Vehicles.this).getOutStationStatus();
                                    String str_ride_type = "";
                                    if (is_in_city_status)
                                    {
                                        str_ride_type = "inCity";
                                    }
                                    if (is_out_station)
                                    {
                                        str_ride_type = "outstation";
                                    }
                                    dict_params.put("rideType", str_ride_type);

                                    updateDriverLocationWithRideType(dict_params);
                                }else
                                {
                                    Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                                }
                            }else
                            {
                                updateVehicleData(dict_element, current_position, previous_position);
                            }
//
                        }
                    } else {
                        dict_selected_vehicle_element = dict_element;
                        current_pos = current_position;
                        previous_pos = previous_position;
                        if (is_driver_available)
                        {
                            is_called = true;
                            if (Utilities.isInternetAvailable(Vehicles.this))
                            {
                                dict_params.put("availableForDelivery", dict_vehicle_info.optBoolean("availableForDelivery"));
                                boolean is_in_city_status = new UserSession(Vehicles.this).getInCityStatus();
                                boolean is_out_station = new UserSession(Vehicles.this).getOutStationStatus();
                                String str_ride_type = "";
                                if (is_in_city_status)
                                {
                                    str_ride_type = "inCity";
                                }
                                if (is_out_station)
                                {
                                    str_ride_type = "outstation";
                                }
                                dict_params.put("rideType", str_ride_type);
                                updateDriverLocationWithRideType(dict_params);
                            }else
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                            }
                        }else
                        {
                            updateVehicleData(dict_element, current_position, previous_position);
                        }
//
                    }
                } catch (JSONException e) {
                    Timber.v("ParseError%s", e.getLocalizedMessage());
                }
            } else {
                dict_selected_vehicle_element = dict_element;
                current_pos = current_position;
                previous_pos = previous_position;
                if (is_driver_available)
                {
                    is_called = true;
                    if (Utilities.isInternetAvailable(Vehicles.this))
                    {
                        try {
                            dict_params.put("availableForDelivery", false);
                            boolean is_in_city_status = new UserSession(Vehicles.this).getInCityStatus();
                            boolean is_out_station = new UserSession(Vehicles.this).getOutStationStatus();
                            String str_ride_type = "";
                            if (is_in_city_status)
                            {
                                str_ride_type = "inCity";
                            }
                            if (is_out_station)
                            {
                                str_ride_type = "outstation";
                            }
                            dict_params.put("rideType", str_ride_type);
                            updateDriverLocationWithRideType(dict_params);
                        } catch (JSONException e) {
                            Timber.v("execution exception%s", e.getLocalizedMessage());
                        }
                    }else
                    {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                    }
                }else
                {
                    updateVehicleData(dict_element, current_position, previous_position);
                }
//
            }
        }
    }

    private void updateDriverLocationWithRideType(JSONObject dict_params) {
        String append_url;
        if (!is_driver_available) {
            append_url = Constants.DRIVER_URL + Constants.UPDATE_DRIVER_LOCATION;

        } else {
            append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            if (is_driver_available)
                            {
                                is_driver_available = false;
                            }else
                            {
                                is_called = false;
                                is_driver_available = true;
                            }
                            if (is_called)
                            {
                                updateVehicleData(dict_selected_vehicle_element, current_pos, previous_pos);
                            }
                        } else {

                            hideLoading();
                            String message = response.optString("message");
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            showLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(rlMain);
                        }else
                        {
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Vehicles.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void updateVehicleData(JSONObject dict_element, int current_position, int previous_position) {
        if (current_position == previous_position) {
            boolean is_available = false;
            if (!dict_element.optBoolean("available")) {
                is_available = true;
            }

            JSONObject dict_params = new JSONObject();
            try {
                dict_params.put("id", dict_element.optString("licenseNumber"));
                JSONObject dict_available = new JSONObject();
                dict_available.put("available", is_available);
                dict_params.put("obj", dict_available);
            } catch (JSONException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }
            if (Utilities.isInternetAvailable(Vehicles.this))
            {
                updateVehicleAvailablity(dict_params, dict_element, current_position, false);
            }else
            {
                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
            }

        } else {
            JSONObject dict_previous_element = arr_approved.get(previous_position);
            boolean is_available = false;
            if (!dict_previous_element.optBoolean("available")) {
                is_available = true;
            }
            JSONObject dict_params = new JSONObject();
            try {
                dict_params.put("id", dict_previous_element.optString("licenseNumber"));
                JSONObject dict_available = new JSONObject();
                dict_available.put("available", is_available);
                dict_params.put("obj", dict_available);
            } catch (JSONException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }
            if (Utilities.isInternetAvailable(Vehicles.this))
            {
                showLoading();
                updateVehicleAvailablity(dict_params, dict_previous_element, current_position, true);
            }else
            {
                adaptor = new VehiclesAdaptor(Vehicles.this, Vehicles.this, true);
                rvApproved.setLayoutManager(new LinearLayoutManager(Vehicles.this));
                adaptor.setListContent(arr_approved);
                rvApproved.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
            }
        }
    }

    private void updateVehicleAvailablity(JSONObject dict_params, final JSONObject dict_selected_element, final int current_position, final boolean is_element) {
        String append_url = Constants.URL + Constants.UPDATE_VEHICLES;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        String data = response.optString("data");
                        if (status.equals("success")) {
                            int index = arr_approved.indexOf(dict_selected_element);
                            new UserSession(Vehicles.this).setSelectedVehicleInfo(data);
                            try {
                                String vehicleType = dict_selected_element.optString("vehicleType");
                                if (dict_selected_element.optBoolean("available")) {
                                    dict_selected_element.put("available", false);
                                    new UserSession(Vehicles.this).removeVehicleType();
                                    new UserSession(Vehicles.this).removeVehicleName();
                                    new UserSession(Vehicles.this).removeSelectedVehicleInfo();
                                    new UserSession(Vehicles.this).setVehicleAvailability(false);
                                    new UserSession(Vehicles.this).removeDriverVehicle();
                                    new UserSession(Vehicles.this).removeAvailableDelivery();
                                } else {
                                    String make = dict_selected_element.optString("make");
                                    String model = dict_selected_element.optString("model");
                                    String vehicle_name = make + " " + model;
                                    dict_selected_element.put("available", true);
                                    new UserSession(Vehicles.this).setVehicleType(vehicleType);
                                    new UserSession(Vehicles.this).setVehicleName(vehicle_name);
                                    new UserSession(Vehicles.this).setSelectedVehicleInfo(dict_selected_element.toString());
                                    new UserSession(Vehicles.this).setVehicleAvailability(true);
                                    new UserSession(Vehicles.this).setAvailableDelivery(dict_selected_element.optBoolean("availableForDelivery"));
                                    if (Utilities.isInternetAvailable(Vehicles.this))
                                    {
                                        getDriverVehicle(vehicleType);
                                    }else
                                    {
                                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                                    }
                                }
                                if (is_element) {
                                    JSONObject dict_element = arr_approved.get(current_position);
                                    boolean is_available = false;
                                    if (!dict_element.optBoolean("available")) {
                                        is_available = true;
                                    }
                                    JSONObject dict_current_params = new JSONObject();
                                    try {
                                        dict_current_params.put("id", dict_element.optString("licenseNumber"));
                                        JSONObject dict_available = new JSONObject();
                                        dict_available.put("available", is_available);
                                        dict_current_params.put("obj", dict_available);
                                    } catch (JSONException e) {
                                        Timber.v("ParseError%s", e.getLocalizedMessage());
                                    }
                                    hideLoading();
                                    if (Utilities.isInternetAvailable(Vehicles.this))
                                    {
                                        updateVehicleAvailablity(dict_current_params, dict_element, current_position, false);
                                    }else
                                    {
                                        Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                                    }
                                } else {

                                    if (is_called)
                                    {
                                        double latitude = Double.parseDouble(new UserSession(Vehicles.this).getLatitude());
                                        double longitude = Double.parseDouble(new UserSession(Vehicles.this).getLongitude());
                                        String str_vehicle_type = new UserSession(Vehicles.this).getVehicleType();
                                        String notificationId = new UserSession(Vehicles.this).getToken();
                                        String str_driverId = new UserSession(Vehicles.this).getUserId();
                                        double driverId = Double.parseDouble(str_driverId);
                                        String subLocality = "";
                                        JSONObject dict_params1 = new JSONObject();
                                        try {

                                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                            if (addresses.size() > 0) {
                                                subLocality = addresses.get(0).getSubLocality();
                                                if (subLocality == null || subLocality.equals("")) {
                                                    subLocality = addresses.get(0).getLocality();
                                                }
                                            }
                                            dict_params1.put("driverId", driverId);
                                            dict_params1.put("latitude", latitude);
                                            dict_params1.put("longitude", longitude);
                                            dict_params1.put("city", subLocality);
                                            dict_params1.put("vehicleType", str_vehicle_type);
                                            dict_params1.put("notificationId", notificationId);
                                            dict_params1.put("availableForDelivery", dict_selected_element.optBoolean("availableForDelivery"));
                                            boolean is_in_city_status = new UserSession(Vehicles.this).getInCityStatus();
                                            boolean is_out_station = new UserSession(Vehicles.this).getOutStationStatus();
                                            String str_ride_type = "";
                                            if (is_in_city_status)
                                            {
                                                str_ride_type = "inCity";
                                            }
                                            if (is_out_station)
                                            {
                                                str_ride_type = "outstation";
                                            }
                                            dict_params1.put("rideType", str_ride_type);

                                        } catch (IOException | JSONException e) {
                                            Timber.v("ParseError%s", e.getLocalizedMessage());
                                        }
                                        if (Utilities.isInternetAvailable(Vehicles.this))
                                        {
                                            updateDriverLocationWithRideType(dict_params1);
                                        }else
                                        {
                                            Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                                        }

                                    }else
                                    {
                                        hideLoading();
                                    }
                                }
                            } catch (JSONException e) {
                                Timber.v("ParseError%s", e.getLocalizedMessage());
                            }
                            arr_approved.remove(dict_selected_element);
                            arr_approved.add(index, dict_selected_element);
                        } else {
                            hideLoading();
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }

                    adaptor = new VehiclesAdaptor(Vehicles.this, Vehicles.this, true);
                    rvApproved.setLayoutManager(new LinearLayoutManager(Vehicles.this));
                    adaptor.setListContent(arr_approved);
                    rvApproved.setAdapter(adaptor);
                    adaptor.notifyDataSetChanged();

                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            showLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(rlMain);
                        }else
                        {
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Vehicles.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onBackPressed() {
        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Vehicles.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
                finish();
            }, 200);
        }
    }
}
