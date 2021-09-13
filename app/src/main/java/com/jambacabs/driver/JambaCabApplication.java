package com.jambacabs.driver;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import androidx.multidex.MultiDexApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JambaCabApplication extends MultiDexApplication {

    public static FirebaseAnalytics mFirebaseAnalytics;
    private static JambaCabApplication mInstance;
    int count;
    @Override
    public void onCreate() {
        super.onCreate();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mInstance = this;
    }

    public static FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public static JambaCabApplication getInstance() {
        return mInstance;
    }

    public void setNotificationCount(int count)
    {
        this.count = count;
    }


    public void removeDriver(View view) {
        String append_url = Constants.DRIVER_URL + Constants.REMOVE_DRIVER_LOCATION;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, getData(),
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            boolean is_driver_available = new UserSession(JambaCabApplication.this).getDriverAvailability();
                            if (is_driver_available) {
                                JSONObject dict_data = new JSONObject();
                                String mobile = new UserSession(JambaCabApplication.this).getUserId();
                                double user_id = Double.parseDouble(mobile);
                                try {
                                    dict_data.put("id", user_id);
                                    JSONObject dict_available = new JSONObject();
                                    dict_available.put("available", false);
                                    dict_data.put("obj", dict_available);
                                } catch (JSONException e) {
                                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                                }
                                updateDriver(dict_data, view);
                            } else {
                                JSONObject dict_data = new JSONObject();
                                try {
                                    dict_data.put("driverId", new UserSession(JambaCabApplication.this).getUserId());
                                } catch (JSONException e) {
                                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                                }
                                logout(dict_data, view);
                            }
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(view, message);
                        }
                    } else {
                        Utilities.showMessage(view, getResources().getString(R.string.fail_error));
                    }
                },
                error -> Utilities.showMessage(view, getResources().getString(R.string.fail_error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(JambaCabApplication.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateDriver(JSONObject dict_params, View view) {
        String append_url = Constants.PAYMENTS_URL + Constants.UPDATE_DRIVER;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            JSONObject dict_data = new JSONObject();
                            try {
                                dict_data.put("driverId", new UserSession(JambaCabApplication.this).getUserId());
                            } catch (JSONException e) {
                               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                            }
                            logout(dict_data, view);
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(view, message);
                        }
                    } else {
                        Utilities.showMessage(view, getResources().getString(R.string.fail_error));
                    }
                },
                error -> Utilities.showMessage(view, getResources().getString(R.string.fail_error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(JambaCabApplication.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void logout(JSONObject dict_params, View view) {
        String append_url = Constants.PAYMENTS_URL + Constants.DRIVER_LOGOUT;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            new UserSession(JambaCabApplication.this).clearUserSession();
                            Intent intent = new Intent(JambaCabApplication.this, Splash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(view, message);
                        }
                    } else {
                        Utilities.showMessage(view, getResources().getString(R.string.fail_error));
                    }
                },
                error -> Utilities.showMessage(view, getResources().getString(R.string.fail_error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(JambaCabApplication.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private JSONObject getData()
    {
        double latitude = Double.parseDouble(new UserSession(JambaCabApplication.this).getLatitude());
        double longitude = Double.parseDouble(new UserSession(JambaCabApplication.this).getLongitude());
        String notificationId = new UserSession(JambaCabApplication.this).getToken();
        String str_driverId = new UserSession(JambaCabApplication.this).getUserId();
        double driverId = Double.parseDouble(str_driverId);
        String subLocality = "";
        JSONObject dict_params = new JSONObject();
        try {
            List<Address> addresses = new Geocoder(JambaCabApplication.this, Locale.getDefault()).getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
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
            dict_params.put("vehicleType", new UserSession(JambaCabApplication.this).getVehicleType());
            dict_params.put("notificationId", notificationId);
            dict_params.put("availableForDelivery", new UserSession(JambaCabApplication.this).getAvailableDelivery());
        } catch (IOException | JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }
        return dict_params;
    }
}
