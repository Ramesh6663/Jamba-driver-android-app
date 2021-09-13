package com.jambacabs.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.models.ResetPassPostBean;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.Constants;

import retrofit2.Call;
import retrofit2.Callback;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etNewPassword,etConfirmPassword;
    RelativeLayout rlProgress;
    String mobileNumber,oldPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        getIntentData();
        iniitUI();
    }

    private void getIntentData() {
        if (getIntent().getExtras()!=null){
            mobileNumber=getIntent().getExtras().getString("mobile_no");
            oldPassword=getIntent().getExtras().getString("old_password");
        }
    }

    private void iniitUI() {
        Utilities.setFirebaseAnalytics(ResetPasswordActivity.this);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rlProgress = findViewById(R.id.rlProgress);
    }

    @Override
    public void onClick(View view) {
        Utilities.hideSoftKeyboard(this);
        int id = view.getId();
        if (id == R.id.btnNextForgot) {
            if (Utilities.isInternetAvailable(this)) {
                String strNewPassword = etNewPassword.getText().toString().trim();
                String strConfirmPassword = etConfirmPassword.getText().toString().trim();
                if (checkFormValidation(strNewPassword,strConfirmPassword)) {
                    if (Utilities.isInternetAvailable(ResetPasswordActivity.this))
                    {
                        showLoading();
                        resetPasswordTask(strNewPassword);
                    }else {
                        Utilities.showMessage(etNewPassword, getResources().getString(R.string.fail_error));
                    }

                }
            }
        }
    }

    private boolean checkFormValidation(String newPassword,String confirmPassword) {
        if (newPassword.trim().length() == 0) {
            etNewPassword.requestFocus();
            Utilities.showMessage(etNewPassword, getString(R.string.pswd_errortxt));
            return false;
        } else if (confirmPassword.trim().length() == 0) {
            etConfirmPassword.requestFocus();
            Utilities.showMessage(etConfirmPassword, getString(R.string.error_confirm_password));
            return false;
        }else if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.requestFocus();
            Utilities.showMessage(etConfirmPassword, getString(R.string.error_pass_not_match));
            return false;
        } else {
            return true;
        }
    }

    private void resetPasswordTask(String newPassword) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        ResetPassPostBean resetPassPostBean=new ResetPassPostBean();
        resetPassPostBean.setMobileNumber(Long.valueOf(mobileNumber));
        resetPassPostBean.setOldPassword(oldPassword);
        resetPassPostBean.setNewPassword(newPassword);

        Call<CommonResponseBean> call = apiService.resetPassword(resetPassPostBean);
        call.enqueue(new Callback<CommonResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                try {
                    if (response.body() != null) {
                        if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                            hideLoading();
                            Utilities.showMessage(etNewPassword, response.body().getMessage());
                            moveToDashboard();
                        } else {
                            hideLoading();
                            Utilities.showMessage(etNewPassword, response.body().getMessage());
                        }
                    }
                } catch (NullPointerException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                    hideLoading();
                    Utilities.showMessage(etNewPassword, getResources().getString(R.string.fail_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(etNewPassword, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void moveToDashboard() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(ResetPasswordActivity.this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        }, 1000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }
}
