package com.jambacabs.driver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jambacabs.driver.font.CustomEditTextView;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.helper.AppSignatureHashHelper;
import com.jambacabs.driver.helper.ReceiveSMSBroadcastReceiver;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import java.util.Objects;

/**
 * Class Name : Otp
 * Created By : Dharma Tea Kanneganti
 * Modified On: 28-12-2019
 */

public class Otp extends AppCompatActivity implements View.OnClickListener, ReceiveSMSBroadcastReceiver.OTPReceiveListener {
    private CustomEditTextView etOne;
    private CustomEditTextView etTwo;
    private CustomEditTextView etThree;
    private CustomEditTextView etFour;
    private CustomTextView tvError;
    private CustomTextView tvStatic;
    private CustomTextView tvMessage;
    private RelativeLayout rlHome;
    private RelativeLayout rlProgress;
    private String str_type;
    private CountDownTimer timer;
//    GoogleApiClient mGoogleApiClient;
    boolean isOtpVerifying;

    /**
     * Class Name : onCreate
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mobile_otp_layout);

        initUI();
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);

        Log.d("Hashkey", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));
//        Utilities.showMessage(rlHome, appSignatureHashHelper.getAppSignatures().get(0));
    }

    /**
     * Class Name : initUI
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void initUI() {
        Utilities.setFirebaseAnalytics(Otp.this);
        etOne = findViewById(R.id.etOne);
        etTwo = findViewById(R.id.etTwo);
        etThree = findViewById(R.id.etThree);
        etFour = findViewById(R.id.etFour);
        tvError = findViewById(R.id.tvError);
        tvStatic = findViewById(R.id.tvStatic);
        tvMessage = findViewById(R.id.tvMessage);
        rlHome = findViewById(R.id.rlHome);
        rlProgress = findViewById(R.id.rlProgress);
        tvMessage.setOnClickListener(null);
        Intent intent = getIntent();
        str_type = intent.getStringExtra("tag");
        if (str_type != null) {
            String str = " +91 " + str_type + ".";
            String str_new = getResources().getString(R.string.otp_static_text) + "<b>" + str + "<b>";
            tvStatic.setText(HtmlCompat.fromHtml(str_new, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        String str_time = "00:15";
        str_time = tvMessage.getText() + " " + str_time;
        tvMessage.setText(str_time);

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();*/
        etOne.requestFocus();
        Utilities.changeEditTextLineColor(this, "hide", etOne);
        etOne.setCursorVisible(false);
        etTwo.setCursorVisible(false);
        etThree.setCursorVisible(false);
        etFour.setCursorVisible(false);
        setupFourDigitEditText();


        etOne.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                etOne.setText("");
            }
            return false;
        });

        etTwo.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                etTwo.setText("");
                etOne.requestFocus();
            }
            return false;
        });

        etThree.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                etThree.setText("");
                etTwo.requestFocus();
            }
            return false;
        });

        etFour.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                etFour.setText("");
                etThree.requestFocus();
            }
            return false;
        });

        timer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                String str_time;
                if (time < 10) {
                    str_time = "0" + time;
                } else {
                    str_time = String.valueOf(time);
                }
                str_time = getResources().getString(R.string.resend_message) + " 00:" + str_time;

                if (time != 0) {
                    tvMessage.setText(str_time);
                }

                if (time == 1) {
                    tvMessage.setTextColor(ContextCompat.getColor(Otp.this, R.color.trouble_code));
                    tvMessage.setText(getResources().getString(R.string.trouble_code));
                    SpannableStringBuilder builder = new SpannableStringBuilder();

                    String substring_two = getResources().getString(R.string.otp_static_text);
                    SpannableString blueSpannable = new SpannableString(substring_two);
                    builder.append(blueSpannable);

                    StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                    String number;
                    number = " +91 " + str_type + ".";
                    SpannableString numspan = new SpannableString(number);
                    numspan.setSpan(boldSpan, 0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(numspan);

                    String substring = " " + getResources().getString(R.string.number_error);
                    SpannableString redSpannable = new SpannableString(substring);
                    redSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(Otp.this, R.color.number_error_color)), 0, substring.length(), 0);
                    builder.append(redSpannable);
                    tvStatic.setText(builder, TextView.BufferType.SPANNABLE);
                }
            }

            public void onFinish() {

                tvMessage.setOnClickListener(Otp.this);
            }
        }.start();
    }

    private void setupFourDigitEditText() {
        etOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                setButtonContinueClickableOrNot();
                if (editable.toString().length() == 1) {
                    etTwo.requestFocus();
                }
            }
        });
        etTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                setButtonContinueClickableOrNot();
                if (editable.toString().length() == 1) {
                    etThree.requestFocus();
                }
            }
        });

        etThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                setButtonContinueClickableOrNot();
                if (editable.toString().length() == 1) {
                    etFour.requestFocus();
                }
            }
        });

        etFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() == 1) {
                    setButtonContinueClickableOrNot();
                } else {
                    etThree.requestFocus();
                }
            }
        });


        etOne.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode != KeyEvent.KEYCODE_DEL)
            {
                if (Objects.requireNonNull(etOne.getText()).toString().trim().length() == 1) {
                    etTwo.requestFocus();
                }
            }
            return false;
        });
        etTwo.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (Objects.requireNonNull(etTwo.getText()).toString().trim().length() == 0)
                    etOne.requestFocus();
            } else {
                if (Objects.requireNonNull(etTwo.getText()).toString().trim().length() == 1) {
                    etThree.requestFocus();
                }
            }
            return false;
        });
        etThree.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (Objects.requireNonNull(etThree.getText()).toString().trim().length() == 0)
                    etTwo.requestFocus();
            } else {
                if (Objects.requireNonNull(etThree.getText()).toString().trim().length() == 1) {
                    etFour.requestFocus();
                }
            }
            return false;
        });
        etFour.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (Objects.requireNonNull(etFour.getText()).toString().trim().length() == 0)
                    etThree.requestFocus();
            }
            return false;
        });
    }

    private void setButtonContinueClickableOrNot() {
        if (validate()) {
            if (!isOtpVerifying) {
                isOtpVerifying = true;
                submitAction();
            }
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(Objects.requireNonNull(etOne.getText()).toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(Objects.requireNonNull(etTwo.getText()).toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(Objects.requireNonNull(etThree.getText()).toString().trim())) {
            return false;
        } else return !TextUtils.isEmpty(Objects.requireNonNull(etFour.getText()).toString().trim());
    }


    /**
     * Class Name : onResume
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    @Override
    protected void onResume() {
        super.onResume();
        startSMSListener();
    }

    private void startSMSListener()
    {
        try {
            ReceiveSMSBroadcastReceiver smsBroadcast = new ReceiveSMSBroadcastReceiver();
            smsBroadcast.initOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            getApplicationContext().registerReceiver(smsBroadcast, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);
            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(aVoid -> Log.v("aVoid", "aVoid"+aVoid));

            task.addOnFailureListener(e -> Log.v("e", "e"+e.getLocalizedMessage()));
        } catch (Exception e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }
    }

    /**
     * Class Name : submitAction
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void submitAction() {
        if (Objects.requireNonNull(etOne.getText()).toString().equals("") || Objects.requireNonNull(etTwo.getText()).toString().equals("")
                || Objects.requireNonNull(etThree.getText()).toString().equals("") || Objects.requireNonNull(etFour.getText()).toString().equals("")) {

            Utilities.changeEditTextLineColor(this, "show", etOne);
            Utilities.changeEditTextLineColor(this, "show", etTwo);
            Utilities.changeEditTextLineColor(this, "show", etThree);
            Utilities.changeEditTextLineColor(this, "show", etFour);

            etFour.setText("");
            etOne.setText("");
            etTwo.setText("");
            etThree.setText("");
            etOne.requestFocus();
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getResources().getString(R.string.verification));
        } else
         {
            Utilities.hideSoftKeyboard(this);

            if (Utilities.isInternetAvailable(Otp.this)) {
                showLoading();
                String otp = etOne.getText().toString() + etTwo.getText().toString() + etThree.getText().toString() + etFour.getText().toString();
                if (timer != null) {
                    timer.cancel();
                }
                showLoading();
                verifyOTP(otp);
            } else {
                Utilities.showMessage(rlHome, getResources().getString(R.string.fail_error));
            }
        }
    }

    /**
     * Class Name : verifyOTP
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void verifyOTP(final String otp) {
        String appendUrl = Constants.USERS_URL + Constants.VERIFY_OTP + "/" + str_type + "&" + otp;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, appendUrl, null,
                response -> {
                    if (response != null) {
                        isOtpVerifying = true;
                        String str_user_type = response.optString("type", "");
                            if (str_user_type.equals("success")) {
                                new UserSession(Otp.this).setNumberVerified(true);
                                onBackPressed();
                                hideLoading();
                            } else {
                                etOne.setText("");
                                etTwo.setText("");
                                etThree.setText("");
                                etFour.setText("");
                                etOne.requestFocus();
                                tvError.setText(response.optString("message"));
                                tvError.setVisibility(View.VISIBLE);
                                hideLoading();
                                isOtpVerifying = false;
                            }
                    } else {
                        isOtpVerifying = false;
                        hideLoading();
                        Utilities.showMessage(rlHome, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    isOtpVerifying = false;
                    hideLoading();
                    Utilities.showMessage(rlHome, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /**
     * Class Name : onClick
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.rlBack) {
            Utilities.hideSoftKeyboard(this);
            onBackPressed();
        } else if (id == R.id.rlHome) {
            Utilities.hideSoftKeyboard(this);
        } else if (id == R.id.tvMessage) {
            Utilities.hideSoftKeyboard(this);
            openResendDialog();
        } else if (id == R.id.fab) {
            submitAction();
        }
    }

    /**
     * Class Name : openResendDialog
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void openResendDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(Otp.this);
        dialog.setContentView(R.layout.resend_dialog_layout);

        CustomTextView tvResendMobile = dialog.findViewById(R.id.tvResendMobile);
        String str = getResources().getString(R.string.resend_message_two) + "<b>" + " +91" + str_type + ".<b>";
        assert tvResendMobile != null;
        tvResendMobile.setText(HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY));

        RelativeLayout rlCall = dialog.findViewById(R.id.rlCall);
        CustomTextView tvResend = dialog.findViewById(R.id.tvResend);
        CustomTextView tvCall = dialog.findViewById(R.id.tvCall);
        CustomTextView tvCancel = dialog.findViewById(R.id.tvCancel);

        if (rlCall != null)
        {
            rlCall.setVisibility(View.GONE);
        }
        Window window = dialog.getWindow();
        assert window != null;
        Objects.requireNonNull(window).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        assert tvResend != null;
        tvResend.setOnClickListener(v -> {
            dialog.dismiss();

            if (Utilities.isInternetAvailable(Otp.this)) {
                etOne.setText("");
                etTwo.setText("");
                etThree.setText("");
                etFour.setText("");
                tvMessage.setOnClickListener(null);
                showLoading();
                generateOTP();
                timer = new CountDownTimer(16000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long time = millisUntilFinished / 1000;
                        String str_time;
                        if (time < 10) {
                            str_time = "0" + time;
                        } else {
                            str_time = String.valueOf(time);
                        }
                        str_time = getResources().getString(R.string.resend_message) + " 00:" + str_time;

                        if (time != 0) {
                            tvMessage.setText(str_time);
                        }

                        if (time == 1) {
                            tvMessage.setTextColor(ContextCompat.getColor(Otp.this, R.color.trouble_code));
                            tvMessage.setText(getResources().getString(R.string.trouble_code));
                        }
                    }

                    public void onFinish() {
                        tvMessage.setOnClickListener(Otp.this);
                    }
                }.start();

                startSMSListener();

            } else {
                Utilities.showMessage(rlHome, getResources().getString(R.string.fail_error));
            }
        });

        assert tvCall != null;
        tvCall.setOnClickListener(v -> {
            dialog.dismiss();
            tvMessage.setOnClickListener(Otp.this);
        });

        assert tvCancel != null;
        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
            tvMessage.setOnClickListener(Otp.this);
        });
        dialog.show();

        dialog.setOnDismissListener(dialog1 -> {

        });
    }

    /**
     * Class Name : showLoading
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Class Name : hideLoading
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    /**
     * Class Name : generateOTP
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    private void generateOTP() {
        String appendUrl = Constants.USERS_URL + Constants.RESEND_OTP + "/" + str_type;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, appendUrl, null,
                response -> {
                    if (response != null) {
                        if (response.optString("type").equals("success")) {
                            String str = getResources().getString(R.string.resend_otp_success) + " " + str_type;
                            Utilities.showMessage(rlHome, str);
                            etOne.requestFocus();
                        } else {

                            tvError.setText(response.optString("message"));
                        }
                    } else {
                        Utilities.showMessage(rlHome, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(rlHome, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /**
     * Class Name : onBackPressed
     * Created By : Dharma Tea Kanneganti
     * Modified On: 28-12-2019
     */

    @Override
    public void onBackPressed() {
        Utilities.hideSoftKeyboard(this);
        Intent intent = new Intent(Otp.this, Register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }

    @Override
    public void onOTPReceived(String message) {
        char[] arr = message.toCharArray();
        for (int i=0; i<arr.length; i++)
        {
            char val = arr[i];
            switch (i)
            {
                case 0:
                    etOne.setText(String.valueOf(val));
                    break;
                case 1:
                    etTwo.setText(String.valueOf(val));
                    break;
                case 2:
                    etThree.setText(String.valueOf(val));
                    break;
                default:
                    etFour.setText(String.valueOf(val));
                    break;
            }
        }
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }
}
