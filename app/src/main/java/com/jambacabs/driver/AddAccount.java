package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomButton;
import com.jambacabs.driver.font.CustomEditTextView;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAccount extends Activity implements View.OnClickListener {
    CustomEditTextView etAccountNumber, etConfirm, etIfsc, etAccountHolder;
    CustomTextView tvAccountError, tvConfirmError, tvIfscError, tvNameError, tvTitle;
    RelativeLayout rlProgress, rlMain;
    String accountNumber, ifsc, holderName;
    String tagFrom;
    CustomButton btnAddAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_account_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        initUI();
    }

    private void initUI() {
        Utilities.setFirebaseAnalytics(AddAccount.this);
        accountNumber = new UserSession(this).getAccount();
        ifsc = new UserSession(this).getAccountIFSC();
        holderName = new UserSession(this).getAccountHolderName();

        etAccountNumber = findViewById(R.id.etAccountNumber);
        etConfirm = findViewById(R.id.etConfirm);
        etIfsc = findViewById(R.id.etIfsc);
        etAccountHolder = findViewById(R.id.etAccountHolder);

        tvAccountError = findViewById(R.id.tvAccountError);
        tvConfirmError = findViewById(R.id.tvConfirmError);
        tvIfscError = findViewById(R.id.tvIfscError);
        tvNameError = findViewById(R.id.tvNameError);
        tvTitle = findViewById(R.id.tvTitle);
        btnAddAccount = findViewById(R.id.btnAddAccount);

        rlProgress = findViewById(R.id.rlProgress);
        rlMain = findViewById(R.id.rlMain);
        Intent intent = getIntent();
        tagFrom = intent.getStringExtra("tag");
        if (tagFrom != null)
        {
            tvTitle.setText(getResources().getString(R.string.edit_bank_account));
            btnAddAccount.setText(getResources().getString(R.string.save));
        }else{
            tvTitle.setText(getResources().getString(R.string.add_bank_account));
            btnAddAccount.setText(getResources().getString(R.string.submit));
        }

        setAccountData();

        etAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    tvAccountError.setVisibility(View.GONE);
                }
            }
        });

        etConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    tvConfirmError.setVisibility(View.GONE);
                }
            }
        });

        etIfsc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    tvIfscError.setVisibility(View.GONE);
                }
            }
        });

        etAccountHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    tvNameError.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setAccountData() {
        if (accountNumber != null) {
            etAccountNumber.setText(accountNumber);
            etConfirm.setText(accountNumber);
        }
        if (holderName != null)
            etAccountHolder.setText(holderName);

        if (ifsc != null)
            etIfsc.setText(ifsc);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlBack) {
            onBackPressed();
        } else if (id == R.id.btnAddAccount) {
            boolean is_validated = validateForm();
            if (is_validated) {
                tvAccountError.setVisibility(View.GONE);
                tvConfirmError.setVisibility(View.GONE);
                tvIfscError.setVisibility(View.GONE);
                tvNameError.setVisibility(View.GONE);
                long number = Long.parseLong(etAccountNumber.getText().toString());
                JSONObject dict_params = new JSONObject();
                try {
                    dict_params.put("driverId", new UserSession(this).getUserId());
                    dict_params.put("ifsc", etIfsc.getText().toString());
                    dict_params.put("accountNumber", number);
                    dict_params.put("accountName", etAccountHolder.getText().toString());
                } catch (JSONException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
                if (Utilities.isInternetAvailable(AddAccount.this))
                {
                    showLoading();
                    submitAccoutDetails(dict_params);
                }else
                {
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
            }
        }
    }

    private void submitAccoutDetails(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.UPDATE_BANK_DETAILS;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            String status = response.optString("type");
                            if (status.equals("success")) {
                                new UserSession(AddAccount.this).setAccount(etAccountNumber.getText().toString());
                                new UserSession(AddAccount.this).setAccountIFSC(etIfsc.getText().toString());
                                new UserSession(AddAccount.this).setAccountHolderName(etAccountHolder.getText().toString());

                                String str_user_data = new UserSession(AddAccount.this).getUserData();
                                try {
                                    JSONObject dict_user_data = new JSONObject(str_user_data);

                                    JSONObject dict_new_account_details = new JSONObject();
                                    dict_new_account_details.put("accountNumber", etAccountNumber.getText().toString());
                                    dict_new_account_details.put("ifsc", etIfsc.getText().toString());
                                    dict_new_account_details.put("accountName", etAccountHolder.getText().toString());
                                    dict_user_data.put("accountDetails", dict_new_account_details);
                                    new UserSession(AddAccount.this).removeUserData();
                                    new UserSession(AddAccount.this).setUserData(dict_user_data.toString());
                                } catch (JSONException e) {
                                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onBackPressed();
                                    }
                                }, 50);
                            } else {
                                String message = response.optString("message");
                                Utilities.showMessage(rlMain, message);
                            }
                        } else {
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                        hideLoading();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                                hideLoading();
                                Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                            }
                        }else
                        {
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(AddAccount.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private boolean validateForm() {
        boolean is_validated = true;

        if (etAccountNumber.getText().toString().equals("")) {
            tvAccountError.setVisibility(View.VISIBLE);
            tvAccountError.setText(getResources().getString(R.string.account_number_placeholder));
            is_validated = false;
        } else {
            if (etAccountNumber.getText().toString().length() < 9) {
                tvAccountError.setVisibility(View.VISIBLE);
                tvAccountError.setText(getResources().getString(R.string.account_number_validation));
                is_validated = false;
            } else {
                if (!etAccountNumber.getText().toString().equals(etConfirm.getText().toString())) {
                    tvAccountError.setVisibility(View.GONE);
                    tvConfirmError.setVisibility(View.VISIBLE);
                    tvConfirmError.setText(getResources().getString(R.string.account_number_validation));
                    is_validated = false;
                } else {
                    if (etIfsc.getText().toString().equals("")) {
                        tvAccountError.setVisibility(View.GONE);
                        tvConfirmError.setVisibility(View.GONE);
                        tvIfscError.setVisibility(View.VISIBLE);
                        is_validated = false;
                    } else {
                        if (etAccountHolder.getText().toString().equals("")) {
                            tvAccountError.setVisibility(View.GONE);
                            tvConfirmError.setVisibility(View.GONE);
                            tvIfscError.setVisibility(View.GONE);
                            tvNameError.setVisibility(View.VISIBLE);
                            is_validated = false;
                        }
                    }
                }
            }
        }
        return is_validated;
    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddAccount.this, Payment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }
}
