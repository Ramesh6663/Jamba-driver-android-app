package com.jambacabs.driver;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.Constants;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;

public class ForgotPassword extends Activity implements View.OnClickListener {

    EditText etUserEmail;
    RelativeLayout rlProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pswd_activity);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
    }

    private void initUI() {
        Utilities.setFirebaseAnalytics(ForgotPassword.this);
        etUserEmail = findViewById(R.id.etUserEmail);
        rlProgress = findViewById(R.id.rlProgress);
    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        Utilities.hideSoftKeyboard(this);
        int id = view.getId();
        if (id == R.id.btnNextForgot) {
            if (Utilities.isInternetAvailable(this)) {
                String strUserEmail = etUserEmail.getText().toString().trim();
                if (checkFormValidation(strUserEmail)) {
                    if (Utilities.isInternetAvailable(ForgotPassword.this))
                    {
                        showLoading();
                        forgotPasswordTask(strUserEmail);
                    }else
                    {
                        Utilities.showMessage(etUserEmail, getString(R.string.internet_fail));
                    }
                }
            }
        }
    }


    private boolean checkFormValidation(String email) {
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.trim().length() == 0) {
            etUserEmail.requestFocus();
            Utilities.showMessage(etUserEmail, getString(R.string.emailid_errortxt));
            return false;
        } else if (!email.matches(email_pattern)) {
            etUserEmail.requestFocus();
            Utilities.showMessage(etUserEmail, getString(R.string.emailid_invalidtxt));
            return false;
        } else {
            return true;
        }
    }

    private void forgotPasswordTask(String userEmail) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        HashMap<String,String > forgotData=new HashMap<>();
        forgotData.put("email",userEmail);

        Call<CommonResponseBean> call = apiService.forgotPassword(forgotData);
        call.enqueue(new Callback<CommonResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                try {
                    if (response.body() != null) {
                        if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                            hideLoading();
                            Utilities.showMessage(etUserEmail, response.body().getMessage());
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> onBackPressed(), 2000);
                        } else {
                            hideLoading();
                            Utilities.showMessage(etUserEmail, response.body().getMessage());
                        }
                    }
                } catch (NullPointerException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                    hideLoading();
                    Utilities.showMessage(etUserEmail, getResources().getString(R.string.fail_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(etUserEmail, getResources().getString(R.string.fail_error));
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);

    }
}

