package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Earnings extends Activity implements View.OnClickListener
{
    private RelativeLayout rlProgress, rlMain;
    private CustomTextView tvAmount;
    private boolean  is_cash_enable;
    private CustomTextView tvCashOut;
    private double wallet_amount;
    private CustomTextView tvWalletAmount;
    private String account_number;
    private JSONObject dict_user_data;
    private boolean isBackPressed;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earnings_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(Earnings.this);
        CustomTextView tvTransactionDate = findViewById(R.id.tvTransactionDate);
        tvCashOut = findViewById(R.id.tvCashOut);
        rlProgress = findViewById(R.id.rlProgress);
        rlMain = findViewById(R.id.rlMain);
        tvAmount = findViewById(R.id.tvAmount);
        tvWalletAmount = findViewById(R.id.tvWalletAmount);

        is_cash_enable = false;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String[] days = new String[7];
        String start_date = "";
        String end_date = "";
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            start_date = days[0];
            end_date = days[6];
        }
        tvAmount.setText("0");

        String str_user_data = new UserSession(Earnings.this).getUserData();
        try {
            dict_user_data = new JSONObject(str_user_data);
            wallet_amount = dict_user_data.optDouble("driverWalet");
            String amount = "₹ " + wallet_amount + " balance";
            tvWalletAmount.setText(amount);
            String razorPayAccountStatus = dict_user_data.optString("razorPayAccountStatus");
            account_number = dict_user_data.optString("razorpayAccountNumber");

            /*if (razorPayAccountStatus != null)
            {*/
            if (!razorPayAccountStatus.equals("null") && !razorPayAccountStatus.equals(""))
            {
                if (razorPayAccountStatus.equals("accept"))
                {
                    is_cash_enable = true;
                }
            }
//            }
            if (wallet_amount>0 && is_cash_enable)
            {
                tvCashOut.setBackground(ContextCompat.getDrawable(this,R.drawable.signin_corners));
            }else
            {
                tvCashOut.setBackground(ContextCompat.getDrawable(this,R.drawable.cashout_gary_corners));
            }
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }

        if (!start_date.equals("") && !end_date.equals(""))
        {
            format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date date1 = format.parse(start_date);
                Date date2 = format.parse(end_date);

                format = new SimpleDateFormat("MMM dd", Locale.getDefault());
                assert date1 != null;
                String str_start_date = format.format(date1);
                assert date2 != null;
                String str_end_date = format.format(Objects.requireNonNull(date2));
                String str = str_start_date + " - " + str_end_date;
                tvTransactionDate.setText(str);

                format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date11 = format.parse(start_date);
                Date date22 = format.parse(end_date);
                assert date11 != null;
                long start_stamp = date11.getTime();
                assert date22 != null;
                long end_stamp = date22.getTime();

                String str_user_id = new UserSession(this).getUserId();
                double user_id = Double.parseDouble(str_user_id);
                JSONObject dict_params = new JSONObject();
                dict_params.put("driverId", user_id);
                dict_params.put("fromDate", start_stamp);
                dict_params.put("endDate", end_stamp);
                if (Utilities.isInternetAvailable(Earnings.this))
                {
                    showLoading();
                    getDriverEarnings(dict_params);
                }else
                {
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
            } catch (ParseException | JSONException e) {
               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
            }
        }
    }


    private void getDriverEarnings(JSONObject dict_params)
    {
        String append_url = Constants.RIDES_URL + Constants.GET_DRIVER_EARNINGS;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success"))
                        {
                            JSONObject dict_data = response.optJSONObject("data");
                            assert dict_data != null;

                            double amount = dict_data.optDouble("totalErnings");

                            String totalEarnings = String.format(Locale.getDefault(), "%.2f", amount);
//                            String totalRides = dict_data.optString("totalRides");
                            tvAmount.setText(totalEarnings);
                        }else
                        {
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
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Earnings.this).getAccessToken());
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
    public void onClick(@NonNull View v)
    {
        int id = v.getId();
        if (id == R.id.rlEarningClose)
        {
            onBackPressed();
        }else if (id == R.id.rlEarnings)
        {
            Intent intent = new Intent(Earnings.this, EarningDetails.class);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        }else if (id == R.id.rlRecentTransactions)
        {
            Intent intent = new Intent(Earnings.this, TransactionDetails.class);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            //finish();
        }else if (id == R.id.rlCashOut)
        {
            if (wallet_amount>0 && is_cash_enable)
            {
                String str_user_id = new UserSession(this).getUserId();
                double user_id = Double.parseDouble(str_user_id);

                JSONObject dict_params = new JSONObject();
                try {
                    dict_params.put("driverId", user_id);
                    dict_params.put("amount", wallet_amount);
                    dict_params.put("currency", "INR");
                    dict_params.put("account", account_number);
                    if (Utilities.isInternetAvailable(Earnings.this))
                    {
                        showLoading();
                        withDrawAmount(dict_params);
                    }else
                    {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                } catch (JSONException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }else
            {
                Utilities.showMessage(rlMain, getResources().getString(R.string.cashout_enable_error));
            }
        }
    }

    private void withDrawAmount(JSONObject dict_params)
    {
        String append_url = Constants.PAYMENTS_URL + Constants.DRIVER_WITHDRAW;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success"))
                        {
                            Utilities.showMessage(rlMain, message);
                            wallet_amount = 0;
                            String amount = "₹ " + wallet_amount + " balance";
                            tvWalletAmount.setText(amount);

                            try {
                                dict_user_data.put("driverWalet", wallet_amount);
                                new UserSession(Earnings.this).removeUserData();
                                new UserSession(Earnings.this).setUserData(dict_user_data.toString());
                            } catch (JSONException e) {
                               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                            }

                            if (is_cash_enable && wallet_amount>0)
                            {
                                tvCashOut.setBackground(ContextCompat.getDrawable(Earnings.this,R.drawable.signin_corners));
                            }else
                            {
                                tvCashOut.setBackground(ContextCompat.getDrawable(Earnings.this,R.drawable.cashout_gary_corners));
                            }
                        }else
                        {
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
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Earnings.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showLoading()
    {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading()
    {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed()
    {
        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Earnings.this, Dashboard.class);
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