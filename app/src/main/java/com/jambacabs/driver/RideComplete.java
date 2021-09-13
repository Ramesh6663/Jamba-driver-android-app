package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.models.CustomerProfileBean;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class RideComplete extends Activity implements View.OnClickListener {
    private TextView tvCustomerName, tvVehicleName, tvRidePrice, tvPaymentType, tvTotalPayable, tvTotalPayment,
            tvFromSource, tvToDestination, tvRideStartTime, tvRideEndTime, tvSuccessDone;
    private ImageView ivProfile;
    private LinearLayout llUserDetails;
    private RelativeLayout rlProgress;
    private boolean isFromAllTrips = false;
    private boolean isBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_success_layout);
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

    private void initUI() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvRidePrice = findViewById(R.id.tvRidePrice);
        tvPaymentType = findViewById(R.id.tvPaymentType);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        tvFromSource = findViewById(R.id.tvFromSource);
        tvToDestination = findViewById(R.id.tvToDestination);
        tvRideStartTime = findViewById(R.id.tvRideStartTime);
        tvRideEndTime = findViewById(R.id.tvRideEndTime);
        ivProfile = findViewById(R.id.ivProfile);
        tvSuccessDone = findViewById(R.id.tvSuccessDone);
        llUserDetails = findViewById(R.id.llUserDetails);
        rlProgress = findViewById(R.id.rlProgress);

        if (isFromAllTrips)
            setDynamicPaymentDetails();
        else
            setPaymentDetails();

    }

    private void setDynamicPaymentDetails() {
        showLoading();
        tvSuccessDone.setVisibility(View.GONE);
        if (getIntent() != null)
        {
            String tripData = Objects.requireNonNull(getIntent().getExtras()).getString("trip_data");
            try {
                if (tripData != null) {
                    JSONObject trip = new JSONObject(tripData);
                    setTripData(trip);
                }
            } catch (JSONException e) {
               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
            }
        }
    }

    private void setPaymentDetails() {
        llUserDetails.setVisibility(View.VISIBLE);
        tvSuccessDone.setVisibility(View.VISIBLE);
        String getAllTripData = new UserSession(RideComplete.this).getPushParams();
        try {
            JSONObject obj = new JSONObject(getAllTripData);
            String user = obj.getString("user");
            JSONObject userDetails = new JSONObject(user);
            String ride = new UserSession(RideComplete.this).getTripSuccessData();
            JSONObject tripDetails = new JSONObject(ride);
            String first_name = userDetails.getString("firstName");
            String last_name = userDetails.getString("lastName");
            String s1 = first_name.substring(0, 1).toUpperCase();
            first_name = s1 + first_name.substring(1);
            String s2 = last_name.substring(0, 1).toUpperCase();
            last_name = s2 + last_name.substring(1);
            String full_name = first_name + " " + last_name;
            tvCustomerName.setText(full_name);
            Glide.with(getApplicationContext()).load(userDetails.getString("avatar")).asBitmap().error(R.mipmap.ic_profile).placeholder(R.mipmap.ic_profile).centerCrop().into(new BitmapImageViewTarget(ivProfile));
            setTripData(tripDetails);
        } catch (Throwable t) {
            Log.v("Throwable", "Throwable" + t.getLocalizedMessage());
        }
    }

    private void setTripData(JSONObject tripDetails) {
        try {
            String cost = "₹ " + tripDetails.getString("totalCost");
            tvPaymentType.setText(tripDetails.getString("paymentType"));
            tvTotalPayment.setText(cost);
            tvTotalPayable.setText(cost);
            tvRidePrice.setText(cost);

            String rideStartTime = tripDetails.optString("startTime");
            String trip_date = Utilities.getRideTime(Long.parseLong(rideStartTime));
            tvRideStartTime.setText(trip_date);

            String rideEndTime = tripDetails.optString("endTime");
            String endTime = Utilities.getRideTime(Long.parseLong(rideEndTime));
            tvRideEndTime.setText(endTime);

            tvFromSource.setText(tripDetails.getString("pickUpPoint"));
            tvToDestination.setText(tripDetails.getString("dropPoint"));
            tvVehicleName.setText(tripDetails.getString("vehicleType"));
            if (Utilities.isInternetAvailable(RideComplete.this))
            {
                getCustomerProfileData(tripDetails.getString("userId"));
            }else
            {
                Utilities.showMessage(tvCustomerName, getResources().getString(R.string.internet_error));
            }

        } catch (Throwable t) {
            Log.v("Throwable", "Throwable" + t.getLocalizedMessage());
        }
    }

    private void getCustomerProfileData(String userId) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.USERS_URL).create(ApiInterface.class);

        Call<CustomerProfileBean> call = apiService.getCustomerProfile(new UserSession(RideComplete.this).getAccessToken(), Long.valueOf(userId));
        call.enqueue(new Callback<CustomerProfileBean>() {

            @Override
            public void onResponse(@NonNull Call<CustomerProfileBean> call, @NonNull retrofit2.Response<CustomerProfileBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(tvCustomerName, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(tvCustomerName);
                    }else
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                CustomerProfileBean.Data customerData = response.body().getData();
                                String firstName = customerData.getFirstName();
                                String lastName = customerData.getLastName();
                                String s1 = firstName.substring(0, 1).toUpperCase();
                                firstName = s1 + firstName.substring(1);
                                String s2 = lastName.substring(0, 1).toUpperCase();
                                lastName = s2 + lastName.substring(1);
                                String fullName = firstName + " " + lastName;
                                tvCustomerName.setText(fullName);

                                Glide.with(getApplicationContext()).load(customerData.getAvatar()).asBitmap().error(R.mipmap.ic_profile).placeholder(R.mipmap.ic_profile).centerCrop().into(new BitmapImageViewTarget(ivProfile));
                            } else {
                                hideLoading();
                                Utilities.showMessage(tvCustomerName, response.body().getMessage());
                            }
                        }else
                        {
                            Utilities.showMessage(tvCustomerName, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException e) {
                    hideLoading();
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(tvCustomerName, getResources().getString(R.string.fail_error));
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
    protected void onDestroy() {
        super.onDestroy();
        new UserSession(RideComplete.this).removeTripSuccessData();
        new UserSession(RideComplete.this).removePushparams();
    }

    @Override
    public void onBackPressed() {
        if (isFromAllTrips) {
            Intent intent = new Intent(RideComplete.this, Trips.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else {
            new UserSession(RideComplete.this).removeTripSuccessData();
            new UserSession(RideComplete.this).removePushparams();

            if (!isBackPressed)
            {
                isBackPressed = true;
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(RideComplete.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                    finish();
                }, 200);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rlVehicleBack) {
            onBackPressed();
        } else if (id == R.id.tvSuccessDone) {
            onBackPressed();
        }
    }
}
