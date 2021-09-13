package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.adaptor.TransactionsAdaptor;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.DriverDataBean;
import com.jambacabs.driver.models.TransactionResponseBean;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class TransactionDetails extends Activity implements View.OnClickListener {
    private CustomTextView tvBalance, tvNoTransactions;
    private RelativeLayout rlProgress;
    private RecyclerView rvTransactions;
    private boolean is_cash_enable;
    private double balance;
    private CustomTextView tvTransactionCashOut;
    private String account_number;
    private JSONObject dict_user_data;
    private String tagFom;
    private RelativeLayout rlMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_details_layout);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
    }

    private void initUI() {
        Utilities.setFirebaseAnalytics(TransactionDetails.this);
        tvBalance = findViewById(R.id.tvBalance);
        rlProgress = findViewById(R.id.rlProgress);
        rvTransactions = findViewById(R.id.rvTransactions);
        tvNoTransactions = findViewById(R.id.tvNoTransactions);
        tvTransactionCashOut = findViewById(R.id.tvTransactionCashOut);
        rlMain = findViewById(R.id.rlMain);
        is_cash_enable = false;
        Intent intent = getIntent();
        tagFom = intent.getStringExtra("tagFrom");

        if (Utilities.isInternetAvailable(TransactionDetails.this))
        {
            showLoading();
            getTransactionsTask();
        }else
        {
            Utilities.showMessage(tvBalance, getResources().getString(R.string.internet_error));
        }
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.rlTransactionsBack) {
            onBackPressed();
        } else if (id == R.id.tvTransactionCashOut)
        {
            if (balance>0 && is_cash_enable)
            {
                String str_user_id = new UserSession(this).getUserId();
                double user_id = Double.parseDouble(str_user_id);

                JSONObject dict_params = new JSONObject();
                try {
                    dict_params.put("driverId", user_id);
                    dict_params.put("amount", balance);
                    dict_params.put("currency", "INR");
                    dict_params.put("account", account_number);
                    if (Utilities.isInternetAvailable(TransactionDetails.this))
                    {
                        showLoading();
                        withDrawAmount(dict_params);
                    }else {
                        Utilities.showMessage(tvBalance, getResources().getString(R.string.internet_error));
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
                            Utilities.showMessage(tvBalance, message);
                            balance = 0;
                            String bal = "₹ " + balance;
                            tvBalance.setText(bal);

                            try {
                                dict_user_data.put("driverWalet", balance);
                                new UserSession(TransactionDetails.this).removeUserData();
                                new UserSession(TransactionDetails.this).setUserData(dict_user_data.toString());
                            } catch (JSONException e) {
                               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                            }
                            if (is_cash_enable && balance>0)
                            {
                                tvTransactionCashOut.setBackground(ContextCompat.getDrawable(TransactionDetails.this,R.drawable.signin_corners));
                            }else
                            {
                                tvTransactionCashOut.setBackground(ContextCompat.getDrawable(TransactionDetails.this,R.drawable.cashout_gary_corners));
                            }
                        }else
                        {
                            Utilities.showMessage(tvBalance, message);
                        }
                    } else {
                        Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
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
                            Utilities.showMessage(tvBalance, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(tvBalance);
                        }else
                        {
                            Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
                    }

                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(TransactionDetails.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void getTransactionsTask() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        String str_user_id = new UserSession(TransactionDetails.this).getUserId();
        Long user_id = Long.parseLong(str_user_id);

        Call<TransactionResponseBean> call = apiService.getTransactions(new UserSession(TransactionDetails.this).getAccessToken(), user_id);
        call.enqueue(new Callback<TransactionResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<TransactionResponseBean> call, @NonNull retrofit2.Response<TransactionResponseBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(tvBalance, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(tvBalance);
                    }else
                    {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                getDriverDocumentDataTask();
                                if (response.body().getData().getTransactions().size() > 0) {
                                    setTransactionData(response.body().getData());
                                    visibleTransactionData();
                                } else {
                                    hideTransactionData();
                                }
                            } else {
                                hideLoading();
                                hideTransactionData();
                                Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void getDriverDocumentDataTask() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<DriverDataBean> call = apiService.getParticularDriver(new UserSession(TransactionDetails.this).getAccessToken(), new UserSession(this).getUserId());
        call.enqueue(new Callback<DriverDataBean>() {

            @Override
            public void onResponse(@NonNull Call<DriverDataBean> call, @NonNull retrofit2.Response<DriverDataBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(tvBalance, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(tvBalance);
                    }else
                    {
                        assert response.body() != null;
                        if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                            hideLoading();

                            DriverDataBean.Data data = response.body().getData();

                            balance = data.getDriverWalet();
                            String bal = "₹ " + balance;
                            tvBalance.setText(bal);

                            String razor_pay_account = data.getRazorPayAccountStatus();
                            account_number = data.getRazorpayAccountNumber();
                            if (razor_pay_account != null)
                            {
                                if (!razor_pay_account.equals("") && balance>0)
                                {
                                    if (razor_pay_account.equals("accept"))
                                    {
                                        is_cash_enable = true;
                                    }
                                }
                            }

                            if (is_cash_enable && balance>0)
                            {
                                tvTransactionCashOut.setBackground(ContextCompat.getDrawable(TransactionDetails.this,R.drawable.signin_corners));
                            }else
                            {
                                tvTransactionCashOut.setBackground(ContextCompat.getDrawable(TransactionDetails.this,R.drawable.cashout_gary_corners));
                            }

                            String str_user_data = new UserSession(TransactionDetails.this).getUserData();
                            dict_user_data = new JSONObject(str_user_data);

                            dict_user_data.put("driverWalet", balance);
                            dict_user_data.put("razorPayAccountStatus", data.getRazorPayAccountStatus());
                            dict_user_data.put("razorpayAccountNumber", data.getRazorpayAccountNumber());
                            new UserSession(TransactionDetails.this).removeUserData();
                            new UserSession(TransactionDetails.this).setUserData(dict_user_data.toString());
                        } else {
                            hideLoading();
                            Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException | JSONException e) {
                    hideLoading();
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverDataBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(tvBalance, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void visibleTransactionData() {
        tvNoTransactions.setVisibility(View.GONE);
        rvTransactions.setVisibility(View.VISIBLE);
    }

    public void hideTransactionData() {
        tvNoTransactions.setVisibility(View.VISIBLE);
        rvTransactions.setVisibility(View.VISIBLE);
    }

    private void setTransactionData(TransactionResponseBean.Data data) {
        rvTransactions.setHasFixedSize(true);
        rvTransactions.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvTransactions.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new TransactionsAdaptor(this, data.getTransactions());
        rvTransactions.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (tagFom != null)
        {
            Intent intent = new Intent(TransactionDetails.this, EarningDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        }else
        {
            super.onBackPressed();
        }

    }
}
