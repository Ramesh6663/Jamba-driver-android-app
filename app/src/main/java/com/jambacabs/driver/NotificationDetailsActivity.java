package com.jambacabs.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.AcceptRejectModel;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

public class NotificationDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivNotification;
    private CustomTextView tvNotificationTitle;
    private CustomTextView tvNotiDescription;
    private LinearLayout llNotificationDetails;
    private LinearLayout llFleetAction;
    private LinearLayout llAction;
    private CustomTextView tvTitle;
    private CustomTextView tvFleetText;
    private CustomTextView tvFleetDescription;
    private ImageView ivOwner;
    private RelativeLayout rlProgress;
    private String notificationID;
    private long userID;
    private long fleetOwnerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        llNotificationDetails = findViewById(R.id.llNotificationDetails);
        llFleetAction = findViewById(R.id.llFleetAction);
        llAction = findViewById(R.id.llAction);
        tvTitle = findViewById(R.id.tvTitle);
        ivOwner = findViewById(R.id.ivOwner);
        tvFleetText = findViewById(R.id.tvFleetText);
        tvFleetDescription = findViewById(R.id.tvFleetDescription);
        rlProgress = findViewById(R.id.rlProgress);

        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
        getIntentData();

    }

    private void getIntentData() {
        if (getIntent().getExtras()!=null){
            String tagFrom = getIntent().getStringExtra("tagFrom");
            String notificationObject = getIntent().getExtras().getString("notification_data");
            if (tagFrom != null)
            {
                llAction.setVisibility(View.GONE);
                llFleetAction.setVisibility(View.VISIBLE);
                llNotificationDetails.setVisibility(View.GONE);
                try {
                    if (notificationObject != null) {
                        JSONObject notificationData = new JSONObject(notificationObject);
                        String strNotificationDetails = notificationData.optString("notificationDetails");
                        JSONObject dictNotification = new JSONObject(strNotificationDetails);
                        notificationID = dictNotification.optString("_id");
                        String strRequestData = dictNotification.optString("fleetRequest");
                        JSONObject dictNotificationDetails = new JSONObject(strRequestData);
                        setNotificationData(dictNotificationDetails);
                    }
                } catch (Throwable t) {
                    Log.v("exec", "exec"+t.getLocalizedMessage());
                }
            }else
            {
                try {
                    if (notificationObject != null) {
                        JSONObject notificationData  = new JSONObject(notificationObject);
                        setNotificationData(notificationData);
                    }
                } catch (JSONException e) {
                    Log.v("e", "e"+e.getLocalizedMessage());
                }
            }


        }
    }

    private void setNotificationData(JSONObject notificationData)
    {
        String tagFrom = getIntent().getStringExtra("tagFrom");
        if (tagFrom != null)
        {
            Uri image = Uri.parse(notificationData.optString("fleetOwnerAvatar"));
            String name = notificationData.optString("fleetOwnerName");
            String title = notificationData.optString("title");
            String description = notificationData.optString("body");
            fleetOwnerID = notificationData.optLong("fleetOwnerId");
            boolean isAccepted = notificationData.optBoolean("accepted");
            boolean isRejected = notificationData.optBoolean("rejected");
            if (!isAccepted && !isRejected)
            {
                llAction.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(name);
            tvFleetText.setText(title);
            tvFleetDescription.setText(description);
            Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivOwner);
        }else
        {
            if (notificationData.has("fleetRequest"))
            {
                llAction.setVisibility(View.GONE);
                llFleetAction.setVisibility(View.VISIBLE);
                llNotificationDetails.setVisibility(View.GONE);
                notificationID = notificationData.optString("_id");
                JSONObject dictFleetRequest= notificationData.optJSONObject("fleetRequest");
                if (dictFleetRequest != null) {
                    Uri image = Uri.parse(dictFleetRequest.optString("fleetOwnerAvatar"));
                    String name = dictFleetRequest.optString("fleetOwnerName");
                    String title = dictFleetRequest.optString("title");
                    String description = dictFleetRequest.optString("body");
                    fleetOwnerID = notificationData.optLong("fleetOwnerId");
                    boolean isAccepted = dictFleetRequest.optBoolean("accepted");
                    boolean isRejected = dictFleetRequest.optBoolean("rejected");
                    if (!isAccepted && !isRejected)
                    {
                        llAction.setVisibility(View.VISIBLE);
                    }
                    tvTitle.setText(name);
                    tvFleetText.setText(title);
                    tvFleetDescription.setText(description);
                    Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivOwner);
                }
            }else
            {
                llAction.setVisibility(View.GONE);
                llFleetAction.setVisibility(View.GONE);
                llNotificationDetails.setVisibility(View.VISIBLE);

                Uri image = Uri.parse(notificationData.optString("image"));
                Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.logo).error(R.drawable.logo).into(ivNotification);
                tvNotificationTitle.setText(notificationData.optString("title"));

                String notificationDesc=notificationData.optString("description");
                if (!TextUtils.isEmpty(notificationDesc)) {
                    tvNotiDescription.setText(HtmlCompat.fromHtml(notificationDesc, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    tvNotiDescription.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }

    }

    private void initUI() {
        ivNotification=findViewById(R.id.ivNotification);
        tvNotificationTitle=findViewById(R.id.tvNotificationTitle);
        tvNotiDescription=findViewById(R.id.tvNotiDescription);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rlNotificationClose)
        {
            onBackPressed();
        }else if (id == R.id.rlAccept)
        {
            showLoading(true);
            AcceptRejectModel model = new AcceptRejectModel();
            String userId = new UserSession(NotificationDetailsActivity.this).getUserId();
            long driverId = Long.parseLong(userId);
            model.setDriverId(driverId);
            model.setNotificationId(notificationID);
            model.setFleetOwnerId(fleetOwnerID);
            acceptNotification(model, true);
        }else if (id == R.id.rlReject)
        {
            showLoading(true);
            AcceptRejectModel model = new AcceptRejectModel();
            String userId = new UserSession(NotificationDetailsActivity.this).getUserId();
            long driverId = Long.parseLong(userId);
            model.setDriverId(driverId);
            model.setNotificationId(notificationID);
            model.setFleetOwnerId(fleetOwnerID);
            acceptNotification(model, false);
        }
    }

    private void acceptNotification(AcceptRejectModel model, boolean isAccept)
    {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);
        Call<CommonResponseBean> call;
        if (isAccept)
            call = apiService.acceptNotification(new UserSession(NotificationDetailsActivity.this).getAccessToken(),model);
        else
            call = apiService.rejectNotification(new UserSession(NotificationDetailsActivity.this).getAccessToken(),model);
        call.enqueue(new Callback<CommonResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                if (response.body() != null) {
                    if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                        Utilities.showMessage(rlProgress, response.body().getMessage());
                        new Handler().postDelayed(() -> onBackPressed(), 500);
                    } else {
                        Utilities.showMessage(rlProgress, response.body().getMessage());
                    }
                    showLoading(false);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                showLoading(false);
                Utilities.showMessage(rlProgress, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void showLoading(boolean show)
    {
        if (show)
            rlProgress.setVisibility(View.VISIBLE);
        else
            rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Inbox.class);
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }
}
