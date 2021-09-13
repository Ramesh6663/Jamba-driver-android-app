package com.jambacabs.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.libraries.places.api.model.Place;
import com.jambacabs.driver.R;
import com.jambacabs.driver.adaptor.PlaceAutocompleteAdapter;
import com.jambacabs.driver.callbacks.IPlaces;
import com.jambacabs.driver.font.CustomButton;
import com.jambacabs.driver.font.CustomEditTextView;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;


public class Register extends Activity implements View.OnClickListener, IPlaces {
    CustomEditTextView etFirst;
    CustomEditTextView etLast;
    CustomEditTextView etEmail;
    CustomEditTextView etMobile;
    CustomEditTextView etPassword;
    CustomEditTextView etInvite;
    CustomEditTextView etCity;
    CustomTextView tvTerms;
    CustomTextView tvEmail;
    CustomTextView tvFirst;
    CustomTextView tvLast;
    CustomTextView tvMobile;
    CustomTextView tvPassword;
    CustomTextView tvCity;
    CustomTextView tvVerify;
    CustomButton btnContinue;
    boolean isNumberVerified;
    ImageView imgvwError;
    int paddingPixel;
    private RelativeLayout rlProgress, rlMain;
    boolean is_first, is_last, is_email, is_password, is_mobile, is_city;
    private PlaceAutocompleteAdapter mAutoCompleteAdapter;
    private RecyclerView rvResult;
    private ArrayList<JSONObject> arrPlacesList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        Utilities.setFirebaseAnalytics(Register.this);
        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        etCity = findViewById(R.id.etCity);
        rvResult = findViewById(R.id.rvResult);
        etInvite = findViewById(R.id.etInvite);
        rlProgress = findViewById(R.id.rlProgress);
        rlMain = findViewById(R.id.rlMain);

        tvTerms = findViewById(R.id.tvTerms);
        tvEmail = findViewById(R.id.tvEmail);
        tvFirst = findViewById(R.id.tvFirst);
        tvLast = findViewById(R.id.tvLast);
        tvMobile = findViewById(R.id.tvMobile);
        tvPassword = findViewById(R.id.tvPassword);
        tvCity = findViewById(R.id.tvCity);
        tvVerify = findViewById(R.id.tvVerify);
        imgvwError = findViewById(R.id.imgvwError);

        btnContinue = findViewById(R.id.btnContinue);
        arrPlacesList = new ArrayList<>();

        mAutoCompleteAdapter = new PlaceAutocompleteAdapter(Register.this, "");
        rvResult.setLayoutManager(new LinearLayoutManager(Register.this));
        mAutoCompleteAdapter.setClickListener(this);
        rvResult.setAdapter(mAutoCompleteAdapter);

        boolean is_verified = new UserSession(Register.this).getNumberVerified();
        if (is_verified)
        {
            imgvwError.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle));
            tvVerify.setTextColor(getResources().getColor(R.color.verified_color));
            tvVerify.setText(getResources().getString(R.string.verified_label));
            new UserSession(Register.this).removeNumberVerified();
            String strInputData = new UserSession(Register.this).getInputData();
            try {
                JSONObject dictInputData = new JSONObject(strInputData);
                String firstName = dictInputData.optString("firstName");
                String lastName = dictInputData.optString("lastName");
                String email = dictInputData.optString("email");
                String mobile = dictInputData.optString("mobile");
                String password = dictInputData.optString("password");
                String city = dictInputData.optString("city");
                String invite = dictInputData.optString("invite");
                etFirst.setText(firstName);
                etLast.setText(lastName);
                etEmail.setText(email);
                etMobile.setText(mobile);
                etPassword.setText(password);
                etCity.setText(city);
                etInvite.setText(invite);
                new UserSession(Register.this).removeInputData();
                new UserSession(Register.this).setInoutNumber(mobile);
            } catch (JSONException e) {
                Log.v("Parse Exception", "Parser Exception"+e.getLocalizedMessage());
            }
            isNumberVerified = true;
        }else
        {
            String strInputData = new UserSession(Register.this).getInputData();
            if (!strInputData.equals(""))
            {
                try {
                    JSONObject dictInputData = new JSONObject(strInputData);
                    String firstName = dictInputData.optString("firstName");
                    String lastName = dictInputData.optString("lastName");
                    String email = dictInputData.optString("email");
                    String mobile = dictInputData.optString("mobile");
                    String password = dictInputData.optString("password");
                    String city = dictInputData.optString("city");
                    String invite = dictInputData.optString("invite");
                    etFirst.setText(firstName);
                    etLast.setText(lastName);
                    etEmail.setText(email);
                    etMobile.setText(mobile);
                    etPassword.setText(password);
                    etCity.setText(city);
                    etInvite.setText(invite);
                    new UserSession(Register.this).removeInputData();
                    new UserSession(Register.this).setInoutNumber(mobile);
                } catch (JSONException e) {
                    Log.e("e", "e"+e.getLocalizedMessage());
                }
            }
            imgvwError.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
            tvVerify.setTextColor(getResources().getColor(R.color.goclr_red));
            tvVerify.setText(getResources().getString(R.string.verify_label));
        }
        is_first = false;
        is_last = false;
        is_email = false;
        is_password = false;
        is_mobile = false;
        is_city = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            int paddingDp = 10;
            float density = Register.this.getResources().getDisplayMetrics().density;
            paddingPixel = (int) (paddingDp * density);


            etFirst.setOnFocusChangeListener((v, hasFocus) -> {

                if (is_first && is_last && is_mobile && is_email && is_password && is_city) {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);

                Utilities.showSoftKeyboard(Register.this, etFirst);
            });

            etLast.setOnFocusChangeListener((v, hasFocus) -> {

                if (is_first && is_last && is_mobile && is_email && is_password && is_city) {
                    etLast.setBackground(getResources().getDrawable(R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                Utilities.showSoftKeyboard(Register.this, etFirst);
            });

            etEmail.setOnFocusChangeListener((v, hasFocus) -> {

                if (is_first && is_last && is_mobile && is_email && is_password && is_city) {
                    etEmail.setBackground(getResources().getDrawable(R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                Utilities.showSoftKeyboard(Register.this, etEmail);
            });

            etMobile.setOnFocusChangeListener((v, hasFocus) -> {

                if (is_first && is_last && is_mobile && is_email && is_password && is_city) {
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                Utilities.showSoftKeyboard(Register.this, etMobile);
            });

            etPassword.setOnFocusChangeListener((v, hasFocus) -> {

                if (is_first && is_last && is_mobile && is_email && is_password && is_city) {
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                Utilities.showSoftKeyboard(Register.this, etPassword);
            });

            etCity.setOnFocusChangeListener((v, hasFocus) -> {
                etCity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals(""))
                        {
                            arrPlacesList.clear();
                            mAutoCompleteAdapter.notifyDataSetChanged();
                            rvResult.setVisibility(View.GONE);
                        }else
                        {
                            rvResult.setVisibility(View.VISIBLE);
                            mAutoCompleteAdapter.getFilter().filter(s.toString());
                        }
                        /*else if (s.length()>2)
                        {
                            rvResult.setVisibility(View.VISIBLE);
//                                showLoading();
                            fetchPlaces(s.toString());
                        }*/
                    }
                });

                if (is_first && is_last && is_mobile && is_email && is_password && is_city) {
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                Utilities.showSoftKeyboard(Register.this);
            });

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            /*etCity.setAdapter(new GooglePlacesAutocompleteAdapter(Register.this, R.layout.simple_list_item));

            etCity.setOnItemClickListener(Register.this);*/
            etCity.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Register.this.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
                return false;
            });

            etInvite.setOnFocusChangeListener((v, hasFocus) -> {

                if (!is_first && !is_last && !is_mobile && !is_email && !is_password && !is_city) {
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                } else {
                    etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                    etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                    etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
                }
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                Utilities.showSoftKeyboard(Register.this, etInvite);
            });

            etFirst.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                        tvFirst.setVisibility(View.GONE);
                        is_first = true;
                        etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                    }
                }
            });

            etLast.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                        tvLast.setVisibility(View.GONE);
                        is_last = true;
                        etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                    }
                }
            });

            etMobile.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                        tvMobile.setVisibility(View.GONE);
                        is_email = true;
                        etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);

                        String old_mobile = new UserSession(Register.this).getInputNumber();
                        if (!old_mobile.equals(s.toString()))
                        {
                            imgvwError.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                            tvVerify.setTextColor(getResources().getColor(R.color.goclr_red));
                            tvVerify.setText(getResources().getString(R.string.verify_label));
                            isNumberVerified = false;
                        }else
                        {
                            imgvwError.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle));
                            tvVerify.setTextColor(getResources().getColor(R.color.verified_color));
                            tvVerify.setText(getResources().getString(R.string.verified_label));
                            new UserSession(Register.this).removeNumberVerified();
                            isNumberVerified = true;
                        }

                    }
                }
            });

            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                        tvPassword.setVisibility(View.GONE);
                        is_password = true;
                        etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                    }
                }
            });

            etEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_selected_color));
                        tvEmail.setVisibility(View.GONE);
                        is_email = true;
                        etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                    }
                }
            });

            SpannableStringBuilder builder = new SpannableStringBuilder();

            String substring_two = getResources().getString(R.string.spanner_string_one);
            SpannableString blueSpannable = new SpannableString(substring_two);
            builder.append(blueSpannable);

            StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);

            ClickableSpan span1 = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View textView) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.legal_url)));
                    startActivity(browserIntent);
                }
            };

            String substring = getResources().getString(R.string.spanner_string_two);
            SpannableString redSpannable = new SpannableString(substring);
            redSpannable.setSpan(span1, 0, redSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            redSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(Register.this,R.color.terms_conditions)), 0, substring.length(), 0);
            builder.append(redSpannable);

            String substring_three = getResources().getString(R.string.spanner_string_three);
            SpannableString blueSpannable_one = new SpannableString(substring_three);
            builder.append(blueSpannable_one);

            ClickableSpan span2 = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View textView) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.legal_url)));
                    startActivity(browserIntent);
                }
            };

            String substring_one = getResources().getString(R.string.spanner_string_four);
            SpannableString redSpannable_one = new SpannableString(substring_one);
            redSpannable_one.setSpan(span2, 0, substring_one.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            redSpannable_one.setSpan(new ForegroundColorSpan(ContextCompat.getColor(Register.this,R.color.terms_conditions)), 0, substring_one.length(), 0);
            builder.append(redSpannable_one);

            tvTerms.setText(builder, TextView.BufferType.SPANNABLE);
            tvTerms.setMovementMethod(LinkMovementMethod.getInstance());


        }, 200);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlMain) {
            Utilities.hideSoftKeyboard(this);
            etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
            etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
            etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
            etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
            etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
            etInvite.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));
            etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_card));

            etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etInvite.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);


        } else if (id == R.id.rlBack) {
            onBackPressed();
        } else if (id == R.id.btnContinue) {
            boolean is_validated = validateForm();
            if (is_validated)
            {
                JSONObject dict_params = new JSONObject();
                try {
                    dict_params.put("firstName", Objects.requireNonNull(etFirst.getText()).toString());
                    dict_params.put("lastName", Objects.requireNonNull(etLast.getText()).toString());
                    dict_params.put("mobileNumber", Long.parseLong(Objects.requireNonNull(etMobile.getText()).toString()));
                    dict_params.put("email", Objects.requireNonNull(etEmail.getText()).toString());
                    dict_params.put("password", Objects.requireNonNull(etPassword.getText()).toString());
                    dict_params.put("city", etCity.getText().toString());
                    dict_params.put("inviteCode", Objects.requireNonNull(etInvite.getText()).toString());
                    dict_params.put("notificationId", new UserSession(this).getToken());
                } catch (JSONException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
                if (Utilities.isInternetAvailable(Register.this))
                {
                    showLoading();
                    signup(dict_params);
                }else
                {
                    Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                }
            }
        }else if (id == R.id.rlVerify)
        {
            if (Objects.requireNonNull(etMobile.getText()).toString().equals(""))
            {
                Utilities.showMessage(rlMain, getResources().getString(R.string.register_mobileno_text));
            }else if (etMobile.getText().toString().length()<10)
            {
                Utilities.showMessage(rlMain, getResources().getString(R.string.register_valid_mobileno_text));
            }else
            {
                if (!isNumberVerified)
                {
                    if(Utilities.isInternetAvailable(Register.this))
                    {
                        storeInputData();
                        Utilities.hideSoftKeyboard(Register.this);
                        showLoading();
                        generateOTP();
                    }else
                    {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                    }
                }

            }
        }
    }

    private void storeInputData()
    {
        JSONObject dictInput = new JSONObject();
        try {
            dictInput.put("firstName", etFirst.getText());
            dictInput.put("lastName", etLast.getText());
            dictInput.put("email", etEmail.getText());
            dictInput.put("mobile", etMobile.getText());
            dictInput.put("password", etPassword.getText());
            dictInput.put("city", etCity.getText());
            dictInput.put("invite", etInvite.getText());
            new UserSession(Register.this).setInputData(dictInput.toString());
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }
    }

    private void generateOTP()
    {
        String appendUrl =  Constants.USERS_URL + Constants.GET_OTP + "/" + Objects.requireNonNull(etMobile.getText()).toString();
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, appendUrl, null,
                response -> {
                    if (response != null) {
                        if (response.optString("type").equals("success")) {
                            String str = getResources().getString(R.string.resend_otp_success) + " " + etMobile.getText().toString();
                            Utilities.showMessage(rlMain, str);
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(Register.this, Otp.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("tag", etMobile.getText().toString());
                                startActivity(intent);
                                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                                finish();
                            }, 300);
                        } else {
                            Utilities.showMessage(rlMain, response.optString("message"));
                        }
                    } else {
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void signup(JSONObject dict_params)
    {
        String append_url = Constants.PAYMENTS_URL + Constants.SIGNUP;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        hideLoading();
                        if (response.optString("type").equals("success"))
                        {
                            new UserSession(Register.this).removeInputNumber();
                            JSONObject dict_data = response.optJSONObject("data");
                            assert dict_data != null;
                            new UserSession(Register.this).setUserData(dict_data.toString());
                            new UserSession(Register.this).setAccessToken(dict_data.optString("accessToken"));
                            new UserSession(Register.this).setUserID(dict_data.optString("mobileNumber"));
                            long expiry = 0;
                            if (dict_data.has("drivingLicenseExpairy"))
                            {
                                expiry = dict_data.optLong("drivingLicenseExpairy");
                            }
                            new UserSession(Register.this).setDriverExpiryDate(expiry);
                            new UserSession(Register.this).setDriverAvailability(dict_data.optBoolean("available", false));
                            JSONObject dict_account_details = dict_data.optJSONObject("accountDetails");
                            if (dict_account_details != null)
                            {
                                if (dict_account_details.length()>0)
                                {
                                    String accountNumber = dict_account_details.optString("accountNumber");
                                    new UserSession(Register.this).setAccount(accountNumber);
                                }
                            }
                            Utilities.showMessage(rlMain, getResources().getString(R.string.signup_success));
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (dict_data.opt("status").equals("reject"))
                                    {
                                        Utilities.showMessage(rlMain, getResources().getString(R.string.reject_text));
                                    }else
                                    {
                                        if (dict_data.opt("status").equals("pending"))
                                        {
                                            new UserSession(Register.this).setDocumentsPending(true);
                                            Intent intent = new Intent(Register.this, Documents.class);
                                            intent.putExtra("tag", "login");
                                            intent.putExtra("fromSplash", true);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            new UserSession(Register.this).setScreen(Constants.MOVE_SCREEN_DOCUMENTS);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                                            finish();
                                        }else
                                        {
                                            navigateToDashboard();
                                        }
                                    }
                                }
                            },500);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
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
        Intent intent = new Intent(Register.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
        finish();
    }

    private boolean validateForm() {

        boolean is_validated = true;


        if (Objects.requireNonNull(etFirst.getText()).toString().equals("") && Objects.requireNonNull(etLast.getText()).toString().equals("") &&
                Objects.requireNonNull(etEmail.getText()).toString().equals("") && Objects.requireNonNull(etMobile.getText()).toString().equals("") &&
                Objects.requireNonNull(etPassword.getText()).toString().equals("") && etCity.getText().toString().equals("")) {
            is_validated = false;
            etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
            etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
            etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
            etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
            etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
            etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));

            etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);

            tvFirst.setVisibility(View.VISIBLE);
            tvLast.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            tvMobile.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);
            tvCity.setVisibility(View.VISIBLE);

            is_first = false;
            is_last = false;
            is_mobile = false;
            is_email = false;
            is_password = false;
            is_city = false;
        } else {
            if (etFirst.getText().toString().equals("")) {
                etFirst.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                etFirst.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                etFirst.requestFocus();
                tvFirst.setVisibility(View.VISIBLE);
                is_first = false;
                is_validated = false;
            } else {
                if (etLast.getText().toString().equals("")) {
                    etLast.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                    etLast.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                    etLast.requestFocus();
                    tvLast.setVisibility(View.VISIBLE);
                    is_last = false;
                    is_validated = false;
                } else {

                    if (Objects.requireNonNull(etEmail.getText()).toString().equals("")) {
                        tvEmail.setText(R.string.register_emailid_text);
                        tvEmail.setVisibility(View.VISIBLE);
                        etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                        etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                        etEmail.requestFocus();
                        is_validated = false;
                        is_email = false;
                    } else {
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        String email = etEmail.getText().toString().trim();
                        if (!(email.matches(emailPattern))) {
                            tvEmail.setText(R.string.register_valid_emailid_text);
                            tvEmail.setVisibility(View.VISIBLE);
                            etEmail.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                            etEmail.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                            etEmail.requestFocus();
                            is_validated = false;
                            is_email = false;
                        } else {
                            if (etMobile.getText().toString().equals("")) {
                                tvMobile.setText(R.string.register_mobileno_text);
                                tvMobile.setVisibility(View.VISIBLE);
                                etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                                etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                                etMobile.requestFocus();
                                is_validated = false;
                                is_mobile = false;
                            } else {
                                if (etMobile.getText().toString().length() < 10) {
                                    tvMobile.setText(R.string.register_valid_mobileno_text);
                                    tvMobile.setVisibility(View.VISIBLE);
                                    etMobile.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                                    etMobile.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                                    etMobile.requestFocus();
                                    is_validated = false;
                                    is_mobile = false;
                                } else {
                                    if (Objects.requireNonNull(etPassword.getText()).toString().equals("")) {
                                        tvPassword.setText(R.string.register_password_text);
                                        tvPassword.setVisibility(View.VISIBLE);
                                        etPassword.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                                        etPassword.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                                        etPassword.requestFocus();
                                        is_validated = false;
                                        is_password = false;
                                    } else {
                                        if (Objects.requireNonNull(etCity.getText()).toString().equals(""))
                                        {
                                            tvCity.setText(R.string.register_city_text);
                                            tvCity.setVisibility(View.VISIBLE);
                                            etCity.setBackground(ContextCompat.getDrawable(Register.this,R.drawable.edit_text_validation_color));
                                            etCity.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                                            etCity.requestFocus();
                                            is_validated = false;
                                            is_city = false;
                                        }else if (!isNumberVerified)
                                        {
                                            is_validated = false;
                                            Utilities.showMessage(rlMain, getResources().getString(R.string.verify_number));
                                        }else if (!new UserSession(Register.this).getInputNumber().equals(etMobile.getText().toString()))
                                        {
                                            is_validated = false;
                                            Utilities.showMessage(rlMain, getResources().getString(R.string.verify_number));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return is_validated;
    }

    private void fetchPlaces(String key)
    {
        String input = key;
        input = input.replaceAll("([^\\d])[0-9]{5,6}([^\\d])", ",");
        input = input.replaceAll(", India", "");
        String url = Constants.PLACES_URL + Constants.PLACE_SEARCH;
        JSONObject dictParams = new JSONObject();
        try {
            dictParams.put("val", input);
        } catch (JSONException e) {
            Timber.v("Parser Error Places%s", e.getLocalizedMessage());
        }
        String finalInput = input;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, dictParams,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals(getResources().getString(R.string.success))) {
                            hideLoading();

                            JSONArray arrList = response.optJSONArray("data");
                            if (!arrPlacesList.isEmpty())
                            {
                                arrPlacesList.clear();
                            }
                            if (arrList != null)
                            {
                                if (arrList.length()>0)
                                {
                                    if (!arrPlacesList.isEmpty())
                                    {
                                        arrPlacesList.clear();
                                    }

                                    for (int i=0; i<arrList.length(); i++)
                                    {
                                        arrPlacesList.add(arrList.optJSONObject(i));
                                    }

                                    if (arrPlacesList != null)
                                    {
                                        mAutoCompleteAdapter.notifyDataSetChanged();

                                    }
                                }else
                                {
                                    String googleURL = Utilities.getURL(Register.this, finalInput);
                                    fetchGooglePlaces(googleURL);
                                }
                            }else
                            {
                                hideLoading();
                                Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                            }
                        } else {
                            rvResult.setVisibility(View.GONE);
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }

                    } else {
                        rvResult.setVisibility(View.GONE);
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }

                },
                error -> {
                    rvResult.setVisibility(View.GONE);
                    hideLoading();
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void fetchGooglePlaces(String url)
    {
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {

                        if (!response.optString("status").equals("ZERO_RESULTS"))
                        {
                            JSONArray arrPredictions = response.optJSONArray("predictions");
                            if (arrPredictions != null && arrPredictions.length()>0)
                            {
                                if (!arrPlacesList.isEmpty())
                                {
                                    arrPlacesList.clear();
                                }
                                JSONArray arrPlaces = new JSONArray();
                                for (int i=0; i<arrPredictions.length(); i++)
                                {
                                    JSONObject dictPredictions = arrPredictions.optJSONObject(i);
                                    String description = dictPredictions.optString("description");
                                    String placeId = dictPredictions.optString("place_id");
                                    JSONArray arrTerms = dictPredictions.optJSONArray("terms");
                                    String area = "";
                                    if (arrTerms != null)
                                    {
                                        if (arrTerms.length()>0)
                                        {
                                            JSONObject dictTerms = arrTerms.optJSONObject(0);
                                            area = dictTerms.optString("value");
                                        }
                                    }

                                    if (area.equals(""))
                                    {
                                        JSONObject dictStructure = dictPredictions.optJSONObject("structured_formatting");
                                        if (dictStructure != null) {
                                            area = dictStructure.optString("main_text");
                                        }
                                    }

                                    JSONObject dictPlaces = new JSONObject();
                                    try {
                                        dictPlaces.put("area", area);
                                        dictPlaces.put("address", description);
                                        dictPlaces.put("placeId", placeId);
                                    } catch (JSONException e) {
                                        Log.v("Places Exception", "Places Exception"+e.getLocalizedMessage());
                                    }

                                    arrPlaces.put(dictPlaces);

                                    arrPlacesList.add(dictPlaces);
                                }

                                if (arrPlacesList != null)
                                {
                                    mAutoCompleteAdapter.notifyDataSetChanged();
                                }
                                JSONObject dictParams = new JSONObject();

                                JSONObject dictValues = new JSONObject();
                                try {
                                    dictValues.put("values", arrPlaces);
                                    dictParams.put("data", dictValues);
                                    sendPlaceDetailsToServer(dictParams);
                                } catch (JSONException e) {
                                    Log.v("Values Exception", "Values Exception"+e.getLocalizedMessage());
                                }
                            }else
                            {
                                hideLoading();
                            }
                        }else
                        {
                            hideLoading();
                        }

                    } else {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void sendPlaceDetailsToServer(JSONObject dictParams)
    {
        String url = Constants.PLACES_URL + Constants.PLACE_UPDATE;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, dictParams,
                response -> {
                    if (response != null) {
                        hideLoading();

                    } else {
                        hideLoading();
                    }
                },
                error -> {
                    hideLoading();
                }
        );
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

    private void  hideLoading()
    {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Utilities.hideSoftKeyboard(this);
        Intent intent = new Intent(Register.this, Home.class);
        intent.putExtra("tag", "ripple");
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new UserSession(Register.this).removeInputNumber();
    }

    @Override
    public void onClick(Place place, String area, String tag)
    {
        if (place != null)
        {
            String name = place.getName();
            if (name != null && !name.equals(""))
            {
                etCity.setText(name);
            }else
            {
                etCity.setText(place.getAddress());
            }
        }else
        {
            if (area != null)
                etCity.setText(area);
            else
                etCity.setText("");
        }

        etCity.setSelection(Objects.requireNonNull(etCity.getText()).toString().length());
        arrPlacesList.clear();
        mAutoCompleteAdapter.notifyDataSetChanged();
        rvResult.setVisibility(View.GONE);
//        etCity.addTextChangedListener(null);
        Utilities.hideSoftKeyboard(this);
    }
}
