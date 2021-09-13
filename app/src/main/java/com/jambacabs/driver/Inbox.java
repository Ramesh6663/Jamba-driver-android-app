package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.adaptor.NotificationAdaptor;
import com.jambacabs.driver.connections.ConnectivityReceiver;
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

import timber.log.Timber;

public class Inbox extends Activity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    NotificationAdaptor adaptor;
    private ArrayList<JSONObject> arr_notifications;
    private ListView lvNotifications;
    private CustomTextView tvNoNotifications, tvNotificationCount;
    private RelativeLayout rlProgress, rlMain;
    private boolean is_notiifications;
    private int count;
    private boolean isBackPressed;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initUI() {
        Utilities.setFirebaseAnalytics(Inbox.this);
        lvNotifications = findViewById(R.id.lvNotificaions);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        rlProgress = findViewById(R.id.rlProgress);
        rlMain = findViewById(R.id.rlMain);
        tvNotificationCount = findViewById(R.id.tvNotificationCount);
        count = 0;
        String str_count = count + " New";
        tvNotificationCount.setText(str_count);
        is_notiifications = false;
        if (Utilities.isInternetAvailable(Inbox.this))
        {
            showLoading();
            getNotifications();
        }else
        {
            tvNoNotifications.setVisibility(View.VISIBLE);
            lvNotifications.setVisibility(View.GONE);
            str_count = count + " New";
            tvNotificationCount.setText(str_count);
            hideLoading();
            Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
        }


        lvNotifications.setOnItemClickListener((parent, view, position, id) -> {
            JSONObject dict_selected_object = arr_notifications.get(position);
            String notification_id = dict_selected_object.optString("_id");
            boolean read = dict_selected_object.optBoolean("read");
            if (!read) {
                JSONObject dict_params = new JSONObject();
                try {
                    JSONObject dict_obj = new JSONObject();
                    dict_obj.put("read", true);
                    dict_params.put("id", notification_id);
                    dict_params.put("obj", dict_obj);
                } catch (JSONException e) {
                    Timber.v("ParseError%s", e.getLocalizedMessage());
                }
                if (Utilities.isInternetAvailable(Inbox.this))
                {
                    showLoading();
                    setNotificationRead(dict_params, dict_selected_object, position);
                }else
                {
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
            }else
            {
                navigateToNotificationDetails(dict_selected_object);
            }
        });
    }

    private void setNotificationRead(JSONObject dict_params, final JSONObject dict_selected_element, final int position) {
        String append_url = Constants.URL + Constants.NOTIFICATIONS_READ;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            arr_notifications.remove(position);
                            try {
                                dict_selected_element.put("read", true);
                                arr_notifications.add(position, dict_selected_element);
                                adaptor = new NotificationAdaptor(Inbox.this, R.layout.notifications_list_item, arr_notifications);
                                lvNotifications.setAdapter(adaptor);
                                adaptor.notifyDataSetChanged();
                                count = count - 1;
                                JambaCabApplication.getInstance().setNotificationCount(count);
                                String str_count = count + " New";
                                tvNotificationCount.setText(str_count);
                                navigateToNotificationDetails(dict_selected_element);
                            } catch (JSONException e) {
                                Timber.v("ParseError%s", e.getLocalizedMessage());
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
                params.put("x-custom-token", new UserSession(Inbox.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void navigateToNotificationDetails(JSONObject dict_selected_element) {
        Intent intent = new Intent(Inbox.this, NotificationDetailsActivity.class);
        intent.putExtra("notification_data",dict_selected_element.toString());
        startActivity(intent);
       /* overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        finish();*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getNotifications() {
        String user_id = new UserSession(this).getUserId();
//        user_id = "8686309238";
        String append_url = Constants.URL + Constants.ALL_NOTIFICATIONS + "/" + user_id + "/driver";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, append_url, new JSONObject(),
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            arr_notifications = new ArrayList<>();
                            is_notiifications = true;
                            JSONArray arr_notification = response.optJSONArray("data");
                            if (arr_notification != null) {

                                for (int i = 0; i < arr_notification.length(); i++) {
                                    JSONObject dict_notifications = arr_notification.optJSONObject(i);
                                    boolean is_read = dict_notifications.optBoolean("read");
                                    if (!is_read) {
                                        count = count + 1;
                                    }
                                    arr_notifications.add(dict_notifications);
                                }
                                JambaCabApplication.getInstance().setNotificationCount(count);
                            }
                            if (arr_notifications.size() > 0) {
                                descendingNotifications();
                                adaptor = new NotificationAdaptor(Inbox.this, R.layout.notifications_list_item, arr_notifications);
                                lvNotifications.setAdapter(adaptor);
                                adaptor.notifyDataSetChanged();
                                tvNoNotifications.setVisibility(View.GONE);
                                lvNotifications.setVisibility(View.VISIBLE);
                                String str_count = count + " New";
                                tvNotificationCount.setText(str_count);
                            } else {
                                tvNoNotifications.setVisibility(View.VISIBLE);
                                lvNotifications.setVisibility(View.GONE);
                                String str_count = count + " New";
                                tvNotificationCount.setText(str_count);
                            }
                        } else {
                            tvNoNotifications.setVisibility(View.VISIBLE);
                            lvNotifications.setVisibility(View.GONE);
                            String str_count = count + " New";
                            tvNotificationCount.setText(str_count);
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        tvNoNotifications.setVisibility(View.VISIBLE);
                        lvNotifications.setVisibility(View.GONE);
                        String str_count = count + " New";
                        tvNotificationCount.setText(str_count);
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
                            tvNoNotifications.setVisibility(View.VISIBLE);
                            lvNotifications.setVisibility(View.GONE);
                            String str_count = count + " New";
                            tvNotificationCount.setText(str_count);
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        tvNoNotifications.setVisibility(View.VISIBLE);
                        lvNotifications.setVisibility(View.GONE);
                        String str_count = count + " New";
                        tvNotificationCount.setText(str_count);
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));                        }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Inbox.this).getAccessToken());
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
        if (id == R.id.rlInboxClose) {
            onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void descendingNotifications() {
        Collections.sort(arr_notifications, ((Comparator<JSONObject>) (o1, o2) -> {
            Long dateFirst = o1.optLong("timeStamp");
            Long dateSecond = o2.optLong("timeStamp");
            return dateFirst.compareTo(dateSecond);
        }).reversed());
    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (!is_notiifications) {
                getNotifications();
            }
        }
    }

    @Override
    public void onProviderChange(boolean isConected) {

    }

    @Override
    public void onBackPressed() {
        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Inbox.this, Dashboard.class);
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