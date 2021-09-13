package com.jambacabs.driver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.adaptor.AccountAdaptor;
import com.jambacabs.driver.callbacks.IAccount;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Account extends AppCompatActivity implements View.OnClickListener, IAccount, LocationListener {
    private static final int REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private Geocoder geocoder;
    private RelativeLayout rlProgress, rlMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(Account.this);
        CustomTextView tvVehicle = findViewById(R.id.tvVehicle);
        RecyclerView rvAccount = findViewById(R.id.rvAccount);
        rlProgress = findViewById(R.id.rlProgress);
        rlMain = findViewById(R.id.rlMain);

        ArrayList<String> arr_account = new ArrayList<>();
        arr_account.add(getString(R.string.documents));
        arr_account.add(getString(R.string.payment));
        arr_account.add(getString(R.string.edit_info));
        arr_account.add(getString(R.string.report_issue));
        arr_account.add(getString(R.string.about));
        arr_account.add(getString(R.string.security_privacy));
        arr_account.add(getString(R.string.app_settings));

        String name = new UserSession(Account.this).getVehicleName();
        if (name.equals("")) {
            name = getResources().getString(R.string.select_vehicle);
        }
        tvVehicle.setText(name);
        AccountAdaptor adaptor = new AccountAdaptor(this, this);
        rvAccount.setLayoutManager(new LinearLayoutManager(this));
        adaptor.setListContent(arr_account);
        rvAccount.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();

        LocationManager locationManager = (LocationManager) Account.this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Account.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Account.this);
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 1, Account.this);
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Account.this);
        geocoder = new Geocoder(Account.this, Locale.getDefault());
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(Account.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Account.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Account.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                String latitude = String.valueOf(currentLocation.getLatitude());
                String longitude = String.valueOf(currentLocation.getLongitude());
                new UserSession(Account.this).setLatitude(latitude);
                new UserSession(Account.this).setLongitude(longitude);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(Account.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    Task<Location> task = fusedLocationProviderClient.getLastLocation();
                    task.addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = location;
                            String latitude = String.valueOf(currentLocation.getLatitude());
                            String longitude = String.valueOf(currentLocation.getLongitude());
                            new UserSession(Account.this).setLatitude(latitude);
                            new UserSession(Account.this).setLongitude(longitude);
                        }
                    });

                }

                LocationManager locationManager = (LocationManager) Account.this.getSystemService(Context.LOCATION_SERVICE);

                assert locationManager != null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, Account.this);
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 1, Account.this);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_LOW);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);

                String provider = locationManager.getBestProvider(criteria, true);
                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled && !isNetworkEnabled) {

                    new AlertDialog.Builder(Account.this)
                            .setTitle(R.string.location_permission_needed)
                            .setCancelable(false)
                            .setMessage(R.string.location_permission_message)
                            .setPositiveButton(R.string.txt_ok, (dialogInterface, i) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE)).create()
                            .show();
                } else {
                    assert provider != null;
                    currentLocation = locationManager.getLastKnownLocation(provider);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.rlAccountBack) {
            onBackPressed();
        } else if (id == R.id.llVehicles) {
            Intent intent = new Intent(Account.this, Vehicles.class);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();

        } else if (id == R.id.rlSignout) {
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String notificationId = new UserSession(this).getToken();
                String str_driverId = new UserSession(this).getUserId();
                double driverId = Double.parseDouble(str_driverId);
                String subLocality = "";
                JSONObject dict_params = new JSONObject();
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
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
                    dict_params.put("vehicleType", new UserSession(this).getVehicleType());
                    dict_params.put("notificationId", notificationId);
                    dict_params.put("availableForDelivery", new UserSession(Account.this).getAvailableDelivery());
                } catch (IOException | JSONException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
                if (Utilities.isInternetAvailable(Account.this))
                {
                    showLoading();
                    removeDriver(dict_params);
                }else
                {
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
            } else {
                Utilities.showMessage(rlMain, getResources().getString(R.string.location_not_availabe));
            }
        }
    }

    private void generateToken() {
        String token = new UserSession(this).getToken();
        if (token.equals("")) {
            FirebaseApp.initializeApp(Account.this);
            Task<InstanceIdResult> data = FirebaseInstanceId.getInstance().getInstanceId();
            data.addOnCompleteListener(task -> {
                try {
                    String token1 = Objects.requireNonNull(task.getResult()).getToken();
                    if (token1.isEmpty()) {
                        token1 = "";
                    }
                    new UserSession(Account.this).setToken(token1);
                } catch (Exception e) {
                        Log.v("exception", "exception" + e.getLocalizedMessage());
                }
            });
        }
    }

    private void removeDriver(JSONObject dict_params) {
        String append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            boolean is_driver_available = new UserSession(Account.this).getDriverAvailability();
                            if (is_driver_available) {
                                JSONObject dict_data = new JSONObject();
                                String mobile = new UserSession(Account.this).getUserId();
                                double user_id = Double.parseDouble(mobile);
                                try {
                                    dict_data.put("id", user_id);
                                    JSONObject dict_available = new JSONObject();
                                    dict_available.put("available", false);
                                    dict_data.put("obj", dict_available);
                                } catch (JSONException e) {
                                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                                }
                                if (Utilities.isInternetAvailable(Account.this))
                                {
                                    updateDriver(dict_data);
                                }else
                                {
                                    Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                                }
                            } else {
                                JSONObject dict_data = new JSONObject();
                                try {
                                    dict_data.put("driverId", new UserSession(Account.this).getUserId());
                                } catch (JSONException e) {
                                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                                }
                                if (Utilities.isInternetAvailable(Account.this))
                                {
                                    logout(dict_data);
                                }else
                                {
                                    Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                                }
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
                params.put("x-custom-token", new UserSession(Account.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateDriver(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.UPDATE_DRIVER;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            JSONObject dict_data = new JSONObject();
                            try {
                                dict_data.put("driverId", new UserSession(Account.this).getUserId());
                            } catch (JSONException e) {
                               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                            }
                            if (Utilities.isInternetAvailable(Account.this))
                            {
                                logout(dict_data);
                            }else
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                            }
                        } else {
                            String message = response.optString("message");
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
                params.put("x-custom-token", new UserSession(Account.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void logout(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.DRIVER_LOGOUT;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            new UserSession(Account.this).clearUserSession();
                            generateToken();
                            Intent intent = new Intent(Account.this, Splash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Account.this).getAccessToken());
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
    public void onClickAccount(String str_selected_element) {
        Intent intent;
        switch (str_selected_element) {
            case "Documents":
                intent = new Intent(Account.this, Documents.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
                break;
            case "Payment":
                intent = new Intent(Account.this, Payment.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
                break;
            case "Edit Info":
                intent = new Intent(Account.this, EditProfile.class);
                intent.putExtra("tag", "account");
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Account.this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }
}
