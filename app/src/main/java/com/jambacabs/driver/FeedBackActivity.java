package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import org.json.JSONObject;

import java.util.Locale;

public class FeedBackActivity extends Activity implements View.OnClickListener
{
    private CustomTextView price_tv, tvRidePaymentMode, date_tv, pickupLocation, dropLocation;
    private ImageView carIV, userProfileIV;
    private boolean isBackPressed;
    private CustomTextView tvRideSuccess;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        price_tv = findViewById(R.id.price_tv);
        tvRidePaymentMode = findViewById(R.id.tvRidePaymentMode);
        date_tv = findViewById(R.id.date_tv);
        pickupLocation = findViewById(R.id.pickupLocation);
        dropLocation = findViewById(R.id.dropLocation);
        carIV = findViewById(R.id.carIV);
        userProfileIV = findViewById(R.id.userProfileIV);
        tvRideSuccess = findViewById(R.id.tvRideSuccess);

        setPaymentDetails();
    }

    private void setPaymentDetails()
    {
        String getAllTripData = new UserSession(FeedBackActivity.this).getPushParams();
        try {
            JSONObject obj = new JSONObject(getAllTripData);
            String user = obj.getString("user");
            JSONObject userDetails = new JSONObject(user);
            String ride = new UserSession(FeedBackActivity.this).getTripSuccessData();
            JSONObject tripDetails = new JSONObject(ride);

            Glide.with(getApplicationContext()).load(userDetails.getString("avatar")).asBitmap().error(R.mipmap.ic_profile).placeholder(R.mipmap.ic_profile).centerCrop().into(new BitmapImageViewTarget(userProfileIV));

            String cost = tripDetails.getString("totalCost");
            if (!cost.equals(""))
            {
                double dCost = Double.parseDouble(cost);
                cost = String.format(Locale.getDefault(), "%.2f", dCost);
            }
            price_tv.setText(cost);

            String rideStartTime = tripDetails.optString("startTime");
            String trip_date = Utilities.getRideTime(Long.parseLong(rideStartTime));
            date_tv.setText(trip_date);
            Uri vehicle_uri = Uri.parse(tripDetails.optString("vehicleAvatar"));
            Glide.with(getApplicationContext()).load(vehicle_uri).asBitmap().placeholder(R.drawable.car_place_holder).error(R.drawable.car_place_holder).fitCenter().into(carIV);
            String paymentMode = tripDetails.optString("paymentType");
            if (paymentMode.equals("cash"))
                tvRideSuccess.setText(getResources().getString(R.string.cash_btn));
            else
                tvRideSuccess.setText(getResources().getString(R.string.done));
            if (!paymentMode.equals(""))
            {
                if (paymentMode.length()>1)
                {
                    String s1 = paymentMode.substring(0, 1).toUpperCase();
                    paymentMode = s1 + paymentMode.substring(1);
                }
            }
            tvRidePaymentMode.setText(paymentMode);
            pickupLocation.setText(tripDetails.getString("pickUpPoint"));
            dropLocation.setText(tripDetails.getString("dropPoint"));
        }catch (Throwable t) {
            Log.v("Throwable", "Throwable" + t.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvRideSuccess)
        {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        new UserSession(FeedBackActivity.this).removeTripSuccessData();
        new UserSession(FeedBackActivity.this).removePushparams();

        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(FeedBackActivity.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 200);
        }
    }
}