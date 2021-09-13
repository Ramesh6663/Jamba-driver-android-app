package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.Objects;

public class Driver_SignIn extends Activity implements View.OnClickListener {

    /// Variables

    private ImageView imgvwClose;
    private CustomEditTextView etEmail, etPassword;
    private RelativeLayout rlProgress;
    private CustomTextView tvError, tvPasswordError;
    boolean isEmailValidated = false;
    boolean isPasswordValidated = false;
    private RelativeLayout rlBack, rlMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        initViews();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        rlBack = findViewById(R.id.rlBack);
        rlMain = findViewById(R.id.rlMain);
        etPassword = findViewById(R.id.etPassword);
        imgvwClose = findViewById(R.id.imgvwClose);
        rlProgress = findViewById(R.id.rlProgress);
        tvError = findViewById(R.id.tvError);
        tvPasswordError = findViewById(R.id.tvPasswordError);

        animate();

        etEmail.requestFocus();
        Utilities.showSoftKeyboard(Driver_SignIn.this, etEmail);


        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String char_text = editable.toString();


                if (char_text.equals("")) {
                    imgvwClose.setVisibility(View.GONE);
                    tvError.setVisibility(View.GONE);
                    isEmailValidated = Utilities.changeEditTextLineColor(Driver_SignIn.this, "hide", etEmail);
                } else {
                    imgvwClose.setVisibility(View.VISIBLE);
                    if (!isEmailValidated) {
                        tvError.setVisibility(View.GONE);
                        isEmailValidated = Utilities.changeEditTextLineColor(Driver_SignIn.this, "hide", etEmail);
                    }
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String char_text = editable.toString();

                if (char_text.equals("")) {
                    tvPasswordError.setVisibility(View.GONE);
                    isPasswordValidated = Utilities.changeEditTextLineColor(Driver_SignIn.this, "hide", etPassword);
                } else {
                    if (!isPasswordValidated) {
                        tvPasswordError.setVisibility(View.GONE);
                        isPasswordValidated = Utilities.changeEditTextLineColor(Driver_SignIn.this, "hide", etPassword);
                    }
                }
            }
        });
    }

    private void animate() {

        FlingAnimation flingX =
                new FlingAnimation(rlBack, DynamicAnimation.X)
                        .setStartVelocity(100f)
                        .setFriction(0.5f);
        flingX.start();

        rlBack.animate().alpha(1f).setDuration(1000).start();
    }

    private void submitAction() {
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (Objects.requireNonNull(etEmail.getText()).toString().equals("")) {
            isEmailValidated = Utilities.changeEditTextLineColor(this, "show", etEmail);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.emailid_errortxt);

        } else if (!etEmail.getText().toString().matches(email_pattern) && etEmail.getText().toString().length() > 0) {
            isEmailValidated = Utilities.changeEditTextLineColor(this, "show", etEmail);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.emailid_errortxt);

        }else if (Objects.requireNonNull(etPassword.getText()).toString().equals("")) {
            isPasswordValidated = Utilities.changeEditTextLineColor(this, "show", etPassword);
            tvPasswordError.setVisibility(View.VISIBLE);
            tvPasswordError.setText(R.string.pswd_errortxt);

        }  else {
            Utilities.hideSoftKeyboard(this);
            JSONObject dict_params = new JSONObject();
            try {
                dict_params.put("email", etEmail.getText().toString());
                dict_params.put("password", etPassword.getText().toString());
                dict_params.put("notificationId", new UserSession(this).getToken());
            } catch (JSONException e) {
               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
            }
            if (Utilities.isInternetAvailable(Driver_SignIn.this))
            {
                showLoading();
                login(dict_params);
            }else
            {
                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
            }
        }
    }

    private void login(JSONObject dict_params)
    {
        String append_url = Constants.PAYMENTS_URL + Constants.LOGIN;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        hideLoading();
                        if (response.optString("type").equals("success"))
                        {
                            JSONObject dict_data = response.optJSONObject("data");
                            assert dict_data != null;
                            new UserSession(Driver_SignIn.this).setAccessToken(dict_data.optString("accessToken"));
                            new UserSession(Driver_SignIn.this).setUserID(dict_data.optString("mobileNumber"));
                            long expiry = 0;
                            if (dict_data.has("drivingLicenseExpairy"))
                            {
                                expiry = dict_data.optLong("drivingLicenseExpairy");
                            }
                            new UserSession(Driver_SignIn.this).setDriverExpiryDate(expiry);
                            new UserSession(Driver_SignIn.this).createUserLoginSession();
                            new UserSession(Driver_SignIn.this).setUserData(dict_data.toString());
                            new UserSession(Driver_SignIn.this).setDriverAvailability(dict_data.optBoolean("available", false));

                            JSONObject dict_account_details = dict_data.optJSONObject("accountDetails");
                            if (dict_account_details != null)
                            {
                                if (dict_account_details.length()>0)
                                {
                                    String accountNumber = dict_account_details.optString("accountNumber");
                                    String ifsc = dict_account_details.optString("ifsc");
                                    String holdername = dict_account_details.optString("accountName");
                                    new UserSession(Driver_SignIn.this).setAccount(accountNumber);
                                    new UserSession(Driver_SignIn.this).setAccountIFSC(ifsc);
                                    new UserSession(Driver_SignIn.this).setAccountHolderName(holdername);
                                }
                            }

                            boolean isTemporary=dict_data.optBoolean("isTemporary");
                            if (isTemporary){
                                startActivity(new Intent(Driver_SignIn.this,ResetPasswordActivity.class)
                                        .putExtra("mobile_no",dict_data.optString("mobileNumber")).putExtra("old_password", Objects.requireNonNull(etPassword.getText()).toString()));
                                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);

                            }else {
                                if (dict_data.opt("status") == null) {
                                    Utilities.showMessage(rlMain, dict_data.optString("statusReason"));
                                } else {
                                    if (Objects.equals(dict_data.opt("status"), "reject")) {
//                                            Utilities.showMessage(rlMain, response.optString("statusReason"));
                                        Utilities.showMessage(rlMain, getResources().getString(R.string.rejected_text));
                                    } else {
                                        if (Objects.equals(dict_data.opt("status"), "pending")) {
                                            new UserSession(Driver_SignIn.this).setDocumentsPending(true);
                                            Intent intent = new Intent(Driver_SignIn.this, Documents.class);
                                            intent.putExtra("tag", "login");
                                            intent.putExtra("fromSplash", true);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            new UserSession(Driver_SignIn.this).setScreen(Constants.MOVE_SCREEN_DOCUMENTS);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                                            finish();
                                        } else {
                                            navigateToDashboard();
                                        }
                                    }
                                }
                            }
                        } else {

                            String message = response.optString("message");
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void navigateToDashboard()
    {
        Intent intent = new Intent(Driver_SignIn.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgvwClose)
        {
            etEmail.setText("");
        }else if (id == R.id.llMain || id == R.id.rlMain)
        {
            Utilities.hideSoftKeyboard(this);
        }else if (id == R.id.fab)
        {
            submitAction();
        }else if (id == R.id.rlBack)
        {
            onBackPressed();
        }else if (id == R.id.tv_forgotPassword)
        {
            Intent forgot_pswd = new Intent(Driver_SignIn.this, ForgotPassword.class);
            startActivity(forgot_pswd);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            //finish();
        }

    }

    private void showLoading()
    {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void  hideLoading()
    {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Driver_SignIn.this, Home.class);
        intent.putExtra("tag", "ripple");
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
    }
}