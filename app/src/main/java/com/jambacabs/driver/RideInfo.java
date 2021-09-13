package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.font.CustomButton;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.CustomerProfileBean;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class RideInfo extends Activity implements View.OnClickListener
{
    CustomTextView tvRideInKM, tvRideDate, tvRideKM, tvRideMin, tvFromSource, tvToDestination, tvFullAmount, tvPaymentType;
    CustomTextView tvEarnings, tvBaseFare, tvDistanceFare, tvTimeFare, tvPlatForm, tvDistanceStatic, tvTimeStatic, tvRideStatus;
    boolean isFromAllTrips = false;
    LinearLayout llPayentDetails;
    CustomButton btnStatus;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_info_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getIntentData();
        initUI();
    }

    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            isFromAllTrips = getIntent().getExtras().getBoolean("from_all_trips");
        }
    }

    private void initUI()
    {
        tvRideInKM = findViewById(R.id.tvRideInKM);
        tvRideDate = findViewById(R.id.tvRideDate);
        tvRideKM = findViewById(R.id.tvRideKM);
        tvRideMin = findViewById(R.id.tvRideMin);
        tvFromSource = findViewById(R.id.tvFromSource);
        tvToDestination = findViewById(R.id.tvToDestination);
        tvFullAmount = findViewById(R.id.tvFullAmount);
        tvPaymentType = findViewById(R.id.tvPaymentType);
        tvEarnings = findViewById(R.id.tvEarnings);
        tvBaseFare = findViewById(R.id.tvBaseFare);
        tvDistanceFare = findViewById(R.id.tvDistanceFare);
        tvTimeFare = findViewById(R.id.tvTimeFare);
        tvPlatForm = findViewById(R.id.tvPlatForm);
        tvDistanceStatic = findViewById(R.id.tvDistanceStatic);
        tvTimeStatic = findViewById(R.id.tvTimeStatic);
        llPayentDetails = findViewById(R.id.llPayentDetails);
        tvRideStatus = findViewById(R.id.tvRideStatus);
        btnStatus = findViewById(R.id.btnStatus);

        RelativeLayout rlDistance = findViewById(R.id.rlDistance);
        rlDistance.bringToFront();
        if (isFromAllTrips)
            setDynamicPaymentDetails();
    }

    private void setDynamicPaymentDetails() {

        String tripData = Objects.requireNonNull(getIntent().getExtras()).getString("trip_data");
        try {
            assert tripData != null;
            JSONObject trip = new JSONObject(tripData);
            setTripData(trip);
            String status = trip.optString("status");
            if (!status.equals("COMPLETED"))
            {
                llPayentDetails.setVisibility(View.GONE);
                if (status.equals("CANCELLED"))
                {
                    btnStatus.setVisibility(View.GONE);
                    tvRideStatus.setVisibility(View.VISIBLE);
                    tvRideStatus.setText(status);
                }else
                {
                    tvRideStatus.setVisibility(View.GONE);
                    btnStatus.setVisibility(View.VISIBLE);
                    btnStatus.setText(status);
                }
            }else
            {
                llPayentDetails.setVisibility(View.VISIBLE);
                tvRideStatus.setVisibility(View.GONE);
                btnStatus.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }

    }

    private void setTripData(JSONObject tripDetails) {
        try {
            double total = tripDetails.optDouble("totalCost");
            double earnings = tripDetails.optDouble("driverEarnings");

            double distance_amount = tripDetails.optDouble("distanceCost");
            double time_amount = tripDetails.optDouble("timeCost");

            String total_cost = "₹ " + String.format(Locale.getDefault(), "%.2f", total);
            String driver_earnings = "₹ " + String.format(Locale.getDefault(), "%.2f", earnings);

            String distance_cost = "₹ " + String.format(Locale.getDefault(), "%.2f", distance_amount);
            String time_cost = "₹ " + String.format(Locale.getDefault(), "%.2f", time_amount);

            JSONObject dict_cost_object = tripDetails.optJSONObject("costObject");
            assert dict_cost_object != null;
            double platform_amount = 0;
            if (dict_cost_object.has("platformCharges"))
            {
                platform_amount = dict_cost_object.optDouble("platformCharges");
            }
            double base = dict_cost_object.optDouble("baseCost");
            String base_cost = "₹ " + String.format(Locale.getDefault(), "%.2f", base);
            String platform_charges = "- ₹ " + String.format(Locale.getDefault(), "%.2f", platform_amount);
            tvPlatForm.setText(platform_charges);

            tvFullAmount.setText(total_cost);
            tvEarnings.setText(driver_earnings);
            tvBaseFare.setText(base_cost);
            tvDistanceFare.setText(distance_cost);
            tvTimeFare.setText(time_cost);


            String payment_type = tripDetails.getString("paymentType");

            switch (payment_type)
            {
                case "online":
                    tvPaymentType.setText(getResources().getString(R.string.payment_type_online));
                    break;
                case "free":
                    tvPaymentType.setText(getResources().getString(R.string.payment_type_free));
                    break;
                default:
                    tvPaymentType.setText(getResources().getString(R.string.payment_type_cash));
                    break;
            }

            String rideStartTime = tripDetails.optString("startTime");
            String trip_date = Utilities.getFullTripDate(Long.parseLong(rideStartTime));
            tvRideDate.setText(trip_date);

            String rideEndTime = tripDetails.optString("endTime");
            String duration = "0 minute(s)";
            if (!rideEndTime.equals(""))
            {
                duration = getRideTime(Long.parseLong(rideStartTime), Long.parseLong(rideEndTime));

            }
            tvRideMin.setText(duration);


            double trip_distance = tripDetails.optDouble("distance");
            String str_trip_distance = String.format(Locale.getDefault(), "%.2f", trip_distance);
            tvRideInKM.setText(str_trip_distance);
            str_trip_distance = str_trip_distance + " km";
            tvRideKM.setText(str_trip_distance);
            String str_distance_info = getResources().getString(R.string.distance_fare) + " ( " + str_trip_distance + " )";
            tvDistanceStatic.setText(str_distance_info);

            String str_time_info = getResources().getString(R.string.time_fare) + " ( " + duration + " )";
            tvTimeStatic.setText(str_time_info);


            tvFromSource.setText(tripDetails.getString("pickUpPoint"));
            tvToDestination.setText(tripDetails.getString("dropPoint"));

        } catch (Throwable t) {
            Log.v("Throwable", "Throwable" + t.getLocalizedMessage());

        }

    }

    public static String getRideTime(long startTime, long endTime)
    {
        long different = endTime - startTime;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

//        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
//        different = different % minutesInMilli;

//        long elapsedSeconds = different / secondsInMilli;
        String time;
        if (elapsedHours>0)
        {
            time = elapsedHours + " hours" + elapsedMinutes + " minutes)";
        }else
        {
            time = elapsedMinutes + " minutes";
        }
        return  time;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlInfoBack)
        {
            onBackPressed();
        }else if (id == R.id.btnStatus)
        {
            String tripData = Objects.requireNonNull(getIntent().getExtras()).getString("trip_data");
            try {
                if (tripData != null) {
                    JSONObject trip = new JSONObject(tripData);
                    String pushParams = new UserSession(RideInfo.this).getPushParams();
                    if (pushParams.equals(""))
                    {
                        String screen;
                        switch (trip.optString("status"))
                        {
                            case "ACCEPTED":
                                screen = "ride_success";
                                break;
                            case "STARTED":
                                screen = "ride_started";
                                break;
                            default:
                                screen = "ride_pickup";
                                break;
                        }
                        String userID = trip.optString("userId");
                        getPerticularUserDetails(userID, screen, llPayentDetails, trip);
                    }else
                    {
                        navigateToDashboard();
                    }
                }

            } catch (JSONException e) {
               Utilities.showMessage(llPayentDetails, getResources().getString(R.string.fetch_error));
            }
        }
    }

    private void getPerticularUserDetails(String userId, String screen, View view, JSONObject dictRideData) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.USERS_URL).create(ApiInterface.class);

        Call<CustomerProfileBean> call = apiService.getCustomerProfile(new UserSession(this).getAccessToken(), Long.valueOf(userId));
        call.enqueue(new Callback<CustomerProfileBean>() {

            @Override
            public void onResponse(@NonNull Call<CustomerProfileBean> call, @NonNull retrofit2.Response<CustomerProfileBean> response) {
                try {
                    if (response.code() == 403) {
                        Utilities.showMessage(view, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(view);
                    }else
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                String strUserData = new Gson().toJson(response.body(), CustomerProfileBean.class);
                                JSONObject dictUserData = new JSONObject(strUserData);
                                JSONObject dictData = dictUserData.optJSONObject("data");
                                JSONObject dictParams = new JSONObject();
                                dictParams.put("rideData", dictRideData.toString());
                                if (dictData != null) {
                                    dictParams.put("user", dictData.toString());
                                }

                                new UserSession(RideInfo.this).setScreen(screen);
                                new UserSession(RideInfo.this).setTagFrom("push");
                                new UserSession(RideInfo.this).setPushParams(dictParams.toString());
                                navigateToDashboard();
                            } else {
                                Utilities.showMessage(view, response.body().getMessage());
                            }
                        }else
                        {
                            Utilities.showMessage(view, getResources().getString(R.string.fetch_error));
                        }
                    }
                } catch (NullPointerException | JSONException e) {
                    Utilities.showMessage(view, getResources().getString(R.string.fetch_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileBean> call, @NonNull Throwable t) {
                Utilities.showMessage(view, getResources().getString(R.string.fetch_error));
            }
        });
    }

    private void navigateToDashboard()
    {
        Intent intent = new Intent(RideInfo.this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isFromAllTrips)
            finish();
        else
        {
            Intent intent = new Intent(RideInfo.this, Trips.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        }
    }
}
