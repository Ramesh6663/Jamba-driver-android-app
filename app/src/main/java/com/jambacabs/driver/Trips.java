package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.adaptor.TripsAdaptor;
import com.jambacabs.driver.callbacks.iTrips;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Trips extends Activity implements View.OnClickListener, iTrips {
    private CustomTextView tvNoRides;
    private ListView lvRides;
    private RelativeLayout rlProgress, rlMain;
    private ArrayList<JSONObject> arr_trips;
    private TripsAdaptor adaptor;
    private String tagFrom;
    private boolean isBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rides_list);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
        getAllTripsTask();
    }

    private void initUI() {
        tvNoRides = findViewById(R.id.tvNoRides);
        lvRides = findViewById(R.id.lvRides);
        rlProgress = findViewById(R.id.rlProgress);
        rlMain = findViewById(R.id.rlMain);
        Intent intent = getIntent();
        tagFrom = intent.getStringExtra("tagFrom");


        lvRides.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent1 = new Intent(Trips.this, RideInfo.class);
            intent1.putExtra("from_all_trips", true);
            intent1.putExtra("trip_data", String.valueOf(arr_trips.get(position)));
            startActivity(intent1);
           /* overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();*/
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAllTripsTask() {

        String user_number = new UserSession(Trips.this).getUserId();
        long user_id = Long.parseLong(user_number);
        JSONObject dict_params = new JSONObject();
        try {
            dict_params.put("driverId", user_id);
            if (Utilities.isInternetAvailable(Trips.this))
            {
                showLoading();
                getAllTrips(dict_params);
            }else
            {
                lvRides.setVisibility(View.GONE);
                tvNoRides.setVisibility(View.VISIBLE);
                hideLoading();
                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
            }
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAllTrips(JSONObject dict_params) {

        String url = Constants.RIDES_URL + Constants.ALL_RIDES;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, dict_params,
                response -> {
                    if (response != null) {
                        String type = response.optString("type");
                        String message = response.optString("message");
                        JSONArray arr_data = response.optJSONArray("data");
                        if (type.equals("success")) {
                            hideLoading();
                            if (arr_data != null) {
                                arr_trips = new ArrayList<>();
                                for (int i = 0; i < arr_data.length(); i++) {
                                    JSONObject dict_models = arr_data.optJSONObject(i);
                                    arr_trips.add(dict_models);
                                }

                                if (arr_trips.size() == 0) {
                                    Utilities.showMessage(rlMain, message);
                                    lvRides.setVisibility(View.GONE);
                                    tvNoRides.setVisibility(View.VISIBLE);
                                } else {
                                    descendingOrderTripsDate();
                                    adaptor = new TripsAdaptor(Trips.this, R.layout.rides_listitem, arr_trips, Trips.this, tagFrom);
                                    lvRides.setAdapter(adaptor);
                                    adaptor.notifyDataSetChanged();

                                    lvRides.setVisibility(View.VISIBLE);
                                    tvNoRides.setVisibility(View.GONE);
                                }
                            } else {
                                lvRides.setVisibility(View.GONE);
                                tvNoRides.setVisibility(View.VISIBLE);
                                Utilities.showMessage(rlMain, message);
                            }
                        } else {
                            lvRides.setVisibility(View.GONE);
                            tvNoRides.setVisibility(View.VISIBLE);
                            hideLoading();
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        lvRides.setVisibility(View.GONE);
                        tvNoRides.setVisibility(View.VISIBLE);
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
                            lvRides.setVisibility(View.GONE);
                            tvNoRides.setVisibility(View.VISIBLE);
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        lvRides.setVisibility(View.GONE);
                        tvNoRides.setVisibility(View.VISIBLE);
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Trips.this).getAccessToken());
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivTripBack) {
            onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void descendingOrderTripsDate() {
        Collections.sort(arr_trips, ((Comparator<JSONObject>) (o1, o2) -> {
            Long dateFirst = o1.optLong("startTime");
            Long dateSecond = o2.optLong("startTime");
            return dateFirst.compareTo(dateSecond);
        }).reversed());
    }

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (tagFrom != null) {
            Intent intent = new Intent(Trips.this, EarningDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        } else {
            if (!isBackPressed)
            {
                isBackPressed = true;
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(Trips.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
                    finish();
                }, 200);
            }
        }
    }

    @Override
    public void onItemClicked() {
        onBackPressed();
    }
}
