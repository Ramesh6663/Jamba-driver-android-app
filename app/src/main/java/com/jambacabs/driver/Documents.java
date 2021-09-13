package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.models.AddVehiclePostBean;
import com.jambacabs.driver.models.DriverDataBean;
import com.jambacabs.driver.models.VehicleTypes;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public class Documents extends Activity implements View.OnClickListener {

    private LinearLayout llFrontLicense;
    private LinearLayout llNote;
    private RelativeLayout rlProgress;
    private ImageView iconLicenseFront, iconLicenseBack, iconProfilePhoto, iconAadharCard, iconAadharCardBack, iconBankPassbook, ivBack;
    private TextView tvNote, tvFrontExpiry, tvBackSideExpiry, tvAddVehicle;
    private boolean fromSpash=false;
    private boolean doubleBackToExitPressedOnce = false;
    private AddVehiclePostBean addVehiclePostBean;
    private boolean isBackPressed;
    private String typeOfVehicle;
    private ArrayList<VehicleTypes> arrVehicleTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_activity);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        initUI();
        getIntentData();
    }

    private void getIntentData() {
        if (getIntent().getExtras()!=null){
            fromSpash=getIntent().getExtras().getBoolean("fromSplash");
            if (fromSpash){
                ivBack.setVisibility(View.GONE);
            }else {
                ivBack.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initUI() {
        Utilities.setFirebaseAnalytics(Documents.this);
        llFrontLicense = findViewById(R.id.llFrontLicense);
        rlProgress = findViewById(R.id.rlProgress);
        llNote = findViewById(R.id.llNote);
        iconLicenseFront = findViewById(R.id.iconLicenseFront);
        iconLicenseBack = findViewById(R.id.iconLicenseBack);
        iconProfilePhoto = findViewById(R.id.iconProfilePhoto);
        iconAadharCard = findViewById(R.id.iconAadharCard);
        iconAadharCardBack = findViewById(R.id.iconAadharCardBack);
        iconBankPassbook = findViewById(R.id.iconBankPassbook);
        tvNote = findViewById(R.id.tvNote);
        tvFrontExpiry = findViewById(R.id.tvFrontExpiry);
        tvBackSideExpiry = findViewById(R.id.tvBackSideExpiry);
        tvAddVehicle = findViewById(R.id.tvAddVehicle);
        ivBack = findViewById(R.id.ivBack);
        arrVehicleTypes = new ArrayList<>();
        if (Utilities.isInternetAvailable(Documents.this))
        {
            showLoading();
            getDriverDocumentDataTask();
        }else
        {
            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.internet_error));
        }
    }

    private void getDriverDocumentDataTask() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);

        Call<DriverDataBean> call = apiService.getParticularDriver(new UserSession(Documents.this).getAccessToken(), new UserSession(this).getUserId());
        call.enqueue(new Callback<DriverDataBean>() {

            @Override
            public void onResponse(@NonNull Call<DriverDataBean> call, @NonNull retrofit2.Response<DriverDataBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(llFrontLicense, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(llFrontLicense);
                    }else
                    {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();

                                DriverDataBean.Data data = response.body().getData();
                                String str_user_data = new UserSession(Documents.this).getUserData();
                                JSONObject dict_user_data = new JSONObject(str_user_data);

                                dict_user_data.put("drivingLicenseFront", data.getDrivingLicenseFront());
                                dict_user_data.put("drivingLicenseBack", data.getDrivingLicenseBack());
                                dict_user_data.put("aadhaarCard", data.getAadhaarCard());
                                dict_user_data.put("aadhaarCardBack", data.getAadhaarCardBack());
                                dict_user_data.put("avatar", data.getAvatar());
                                dict_user_data.put("bankPassbook", data.getBankPassbook());

                                new UserSession(Documents.this).removeUserData();
                                new UserSession(Documents.this).setUserData(dict_user_data.toString());

                                setDocumentData(response.body().getData());
                            } else {
                                hideLoading();
                                Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                            }
                        }else {
                            hideLoading();
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException | JSONException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverDataBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
            }
        });
    }

    private void  getVehicleTypes(String tag) {
        String appendUrl = Constants.URL + Constants.VEHICLE_TYPES;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, appendUrl, null,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals(getResources().getString(R.string.success))) {
                            hideLoading();
                            JSONArray arrData = response.optJSONArray("data");
                            assert arrData != null;
                            arrVehicleTypes = new ArrayList<>();
                            for (int i = 0; i < arrData.length(); i++) {
                                JSONObject dictData = arrData.optJSONObject(i);
                                String vehicleModel = dictData.optString("vehicleModel");
                                String avatar = dictData.optString("Avatar");
                                boolean availability = dictData.optBoolean("Availability");
                                String strSeats = dictData.optString("Seats");
                                String strTypes = dictData.optString("Types");
                                VehicleTypes vt = new VehicleTypes();
                                vt.setVehicleModel(vehicleModel);
                                vt.setAvatar(avatar);
                                vt.setAvailability(availability);
                                vt.setTypes(strTypes);
                                vt.setSeats(strSeats);
                                arrVehicleTypes.add(vt);
                            }

                            for (int i=0; i<arrVehicleTypes.size(); i++)
                            {
                                VehicleTypes vt = arrVehicleTypes.get(i);
                                String model = vt.getVehicleModel();
                                if (tag.equals("vehicle"))
                                {
                                    if (model.equals(addVehiclePostBean.getVehicleType()))
                                    {
                                        typeOfVehicle = vt.getTypes();
                                    }
                                }else
                                {
                                    String vehicle_data = new UserSession(Documents.this).getFirstVehicleData();
                                    try {
                                        JSONObject dictData = new JSONObject(vehicle_data);
                                        String oldModel = dictData.optString("vehicleType");
                                        if (model.equals(oldModel))
                                        {
                                            typeOfVehicle = vt.getTypes();
                                        }
                                    } catch (JSONException e) {
                                        Timber.v("Ex%s", e.getLocalizedMessage());
                                    }
                                }

                            }
                            tvAddVehicle.setText(getResources().getString(R.string.vehicle_documents_one));
                            tvAddVehicle.setVisibility(View.VISIBLE);
                        } else {
                            hideLoading();
                            String strMessage = response.optString(getResources().getString(R.string.message));
                            Utilities.showMessage(rlProgress, strMessage);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(rlProgress, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            if (Utilities.isInternetAvailable(Documents.this))
                            {
                                Utilities.showMessage(rlProgress, getResources().getString(R.string.session_expired));
                                JambaCabApplication.getInstance().removeDriver(rlProgress);
                            }else
                            {
                                Utilities.showMessage(rlProgress, getResources().getString(R.string.internet_fail));
                            }
                        }else
                        {
                            hideLoading();
                            Utilities.showMessage(rlProgress, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        hideLoading();
                        Utilities.showMessage(rlProgress, getResources().getString(R.string.internet_fail));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.xCustomToken), new UserSession(Documents.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void setDocumentData(DriverDataBean.Data data) {

        if (!fromSpash)
        {
            String status = data.getStatus();
            if (status.equals("accept"))
            {
                showDocuments(data);
            }
        }else
        {
            String status = data.getStatus();
            switch (status) {
                case "accept":
                    Utilities.showMessage(rlProgress, getResources().getString(R.string.accept_text));

                    new Handler().postDelayed(() -> {
                        new UserSession(Documents.this).removeDocumentsPending();
                        new UserSession(Documents.this).removeScreen();
                        new UserSession(Documents.this).remoVeFirstVehicleData();
                        Intent intent = new Intent(Documents.this, Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                        finish();
                    }, 100);
                    break;
                case "reject":
                    Utilities.showMessage(rlProgress, getResources().getString(R.string.reject_text));
                    new Handler().postDelayed(() -> {
                        JSONObject dict_data = new JSONObject();
                        try {
                            dict_data.put("driverId", new UserSession(Documents.this).getUserId());
                        } catch (JSONException e) {
                           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                        }
                        if (Utilities.isInternetAvailable(Documents.this)) {
                            logout(dict_data);
                        } else {
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.internet_error));
                        }
                    }, 100);
                    break;
                case "pending":
                    showDocuments(data);
                    break;
            }
        }
    }

    private void showDocuments(DriverDataBean.Data data)
    {
        try {
            boolean isAllDocumentsVerified = true;
            if (data.getDrivingLicenseFront() == null) {
                iconLicenseFront.setImageResource(R.drawable.ic_cross);
                isAllDocumentsVerified =false;
            } else {
                iconLicenseFront.setImageResource(R.drawable.ic_check);
                if (data.getDrivingLicenseExpairy()>0) {
                    String expiryDate = Utilities.getDate(data.getDrivingLicenseExpairy());
                    tvFrontExpiry.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, true);
                    String txt = "Expires: " + expiryDate;
                    tvFrontExpiry.setText(txt);
                }
            }

            if (data.getDrivingLicenseBack() == null) {
                iconLicenseBack.setImageResource(R.drawable.ic_cross);
                isAllDocumentsVerified =false;
            } else {
                iconLicenseBack.setImageResource(R.drawable.ic_check);
                isAllDocumentsVerified =true;
                if (data.getDrivingLicenseExpairy()>0) {
                    String expiryDate = Utilities.getDate(data.getDrivingLicenseExpairy());
                    tvBackSideExpiry.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String txt = "Expires: " + expiryDate;
                    tvBackSideExpiry.setText(txt);
                }
            }
            if (data.getAvatar() == null) {
                iconProfilePhoto.setImageResource(R.drawable.ic_cross);
                isAllDocumentsVerified =false;
            } else {
                iconProfilePhoto.setImageResource(R.drawable.ic_check);
            }

            if (data.getAadhaarCard() == null) {
                iconAadharCard.setImageResource(R.drawable.ic_cross);
                isAllDocumentsVerified =false;
            } else {
                iconAadharCard.setImageResource(R.drawable.ic_check);
            }

            if (data.getAadhaarCardBack() == null) {
                iconAadharCardBack.setImageResource(R.drawable.ic_cross);
                isAllDocumentsVerified =false;
            } else {
                iconAadharCardBack.setImageResource(R.drawable.ic_check);
            }

            if (data.getBankPassbook() == null) {
                iconBankPassbook.setImageResource(R.drawable.ic_cross);
                isAllDocumentsVerified =false;
            } else {
                iconBankPassbook.setImageResource(R.drawable.ic_check);
            }

            if (data.getStatus().equals(Constants.DRIVER_STATUS_PENDING) || data.getStatus().equals(Constants.DRIVER_STATUS_REJECT)) {
                llNote.setVisibility(View.VISIBLE);
                String txt = "" + data.getStatusReason();
                tvNote.setText(txt);
            } else {
                llNote.setVisibility(View.GONE);
            }

            if (!fromSpash)
            {
                tvAddVehicle.setVisibility(View.GONE);
            }else
            {
                if (isAllDocumentsVerified){

                    String vehicle_data = new UserSession(Documents.this).getFirstVehicleData();
                    if (vehicle_data.equals(""))
                    {
                        if (Utilities.isInternetAvailable(Documents.this))
                        {
                            showLoading();
                            getVehicles();
                        }else
                        {
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        if (Utilities.isInternetAvailable(Documents.this))
                        {
                            showLoading();
                            getVehicleTypes("exist");
                        }
                        tvAddVehicle.setText(getResources().getString(R.string.vehicle_documents_one));
                        tvAddVehicle.setVisibility(View.VISIBLE);
                    }
                }else {
                    tvAddVehicle.setVisibility(View.GONE);
                }
            }
        } catch (NullPointerException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }
    }

    private void getVehicles() {
        String user_id = new UserSession(this).getUserId();
        String append_url = Constants.URL + Constants.VEHICLES + "/" + user_id;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, append_url, new JSONObject(),
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            JSONArray arr_data = response.optJSONArray("data");

                            if (arr_data != null) {
                                if (arr_data.length()>0)
                                {
                                    JSONObject dict_vehicle_data = arr_data.optJSONObject(0);
                                    addVehiclePostBean = new AddVehiclePostBean();
                                    addVehiclePostBean.setLicenseNumber(dict_vehicle_data.optString("licenseNumber"));
                                    addVehiclePostBean.setDriverId(dict_vehicle_data.optLong("driverId"));
                                    addVehiclePostBean.setMake(dict_vehicle_data.optString("make"));
                                    addVehiclePostBean.setModel(dict_vehicle_data.optString("model"));
                                    addVehiclePostBean.setVehicleType(dict_vehicle_data.optString("vehicleType"));
                                    addVehiclePostBean.setYear(dict_vehicle_data.optLong("year"));
                                    addVehiclePostBean.setRegistrationCertificateFront(dict_vehicle_data.optString("registrationCertificateFront"));
                                    addVehiclePostBean.setRegistrationCertificateBack(dict_vehicle_data.optString("registrationCertificateBack"));
                                    addVehiclePostBean.setInsurancePolicy(dict_vehicle_data.optString("insurancePolicy"));
                                    addVehiclePostBean.setPollutionUnderControl(dict_vehicle_data.optString("pollutionUnderControl"));
                                    addVehiclePostBean.setFitnessCertificate(dict_vehicle_data.optString("fitnessCertificate"));
                                    addVehiclePostBean.setPermitCertificate(dict_vehicle_data.optString("permitCertificate"));

                                    addVehiclePostBean.setAvailable(dict_vehicle_data.optBoolean("available"));
                                    addVehiclePostBean.setInsurancePolicyExpiryDate(dict_vehicle_data.optLong("insurancePolicyExpiryDate"));
                                    addVehiclePostBean.setRegistrationCertificateExpiryDate(dict_vehicle_data.optLong("registrationCertificateExpiryDate"));
                                    addVehiclePostBean.setPollutionUnderControlExpiryDate(dict_vehicle_data.optLong("pollutionUnderControlExpiryDate"));

                                    Gson gson = new Gson();
                                    String vehicleData = gson.toJson(addVehiclePostBean);
                                    new UserSession(Documents.this).setFirstVehicleData(vehicleData);
                                    getVehicleTypes("vehicle");
                                }else
                                {
                                    hideLoading();
                                    tvAddVehicle.setText(getResources().getString(R.string.add_vehicle));
                                    tvAddVehicle.setVisibility(View.VISIBLE);
                                }
                            }else
                            {
                                hideLoading();
                                Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                            }

                        } else {
                            hideLoading();
                            Utilities.showMessage(llFrontLicense, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            showLoading();
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(llFrontLicense);
                        }else
                        {
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Documents.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void logout(JSONObject dict_params) {
        String append_url = Constants.PAYMENTS_URL + Constants.DRIVER_LOGOUT;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, dict_params,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        if (status.equals("success")) {
                            new UserSession(Documents.this).clearUserSession();
                            generateToken();
                            Intent intent = new Intent(Documents.this, Splash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            String message = response.optString("message");
                            Utilities.showMessage(llFrontLicense, message);
                        }
                    } else {
                        Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
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
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(llFrontLicense);
                        }else
                        {
                            Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(llFrontLicense, getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(Documents.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void generateToken() {
        String token = new UserSession(this).getToken();
        if (token.equals("")) {
            FirebaseApp.initializeApp(Documents.this);
            Task<InstanceIdResult> data = FirebaseInstanceId.getInstance().getInstanceId();
            data.addOnCompleteListener(task -> {
                try {
                    String token1 = Objects.requireNonNull(task.getResult()).getToken();
                    if (token1.isEmpty()) {
                        token1 = "";
                    }
                    new UserSession(Documents.this).setToken(token1);
                } catch (Exception e) {
                        Log.v("exception", "exception" + e.getLocalizedMessage());
                }
            });
        }
    }


    private void checkDocumentExpiryDate(String documentDate, boolean isFront) {
        int totalDays = Integer.parseInt(Objects.requireNonNull(Utilities.getDatesDifferent(Utilities.getCurrentDate(), documentDate)));
        if (totalDays < 30) {
            if (isFront) {
                tvFrontExpiry.setTextColor(ContextCompat.getColor(this, R.color.red));
                iconLicenseFront.setImageResource(R.drawable.ic_warning);
            } else {
                tvBackSideExpiry.setTextColor(ContextCompat.getColor(this, R.color.red));
                iconLicenseBack.setImageResource(R.drawable.ic_warning);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlDocumentsBack) {
            onBackPressed();
        } else if (id == R.id.llFrontLicense) {
            startActivity(new Intent(Documents.this, ShowDriverDocument.class).putExtra("position", 1).putExtra("fromSplash", fromSpash));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llBackLicense) {
            startActivity(new Intent(Documents.this, ShowDriverDocument.class).putExtra("position", 2).putExtra("fromSplash", fromSpash));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llProfilePhoto) {
            startActivity(new Intent(Documents.this, ShowDriverDocument.class).putExtra("position", 3).putExtra("fromSplash", fromSpash));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llAadharCard) {
            startActivity(new Intent(Documents.this, ShowDriverDocument.class).putExtra("position", 4).putExtra("fromSplash", fromSpash));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        }  else if (id == R.id.llAadharCardBack) {
            startActivity(new Intent(Documents.this, ShowDriverDocument.class).putExtra("position", 5).putExtra("fromSplash", fromSpash));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llBankPassbook) {
            startActivity(new Intent(Documents.this, ShowDriverDocument.class).putExtra("position", 6).putExtra("fromSplash", fromSpash));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.tvAddVehicle) {
            if (tvAddVehicle.getText().toString().equals(getResources().getString(R.string.vehicle_documents_one)))
            {
                startActivity(new Intent(Documents.this, VehicleDocumentsList.class).putExtra("fromSplash", fromSpash).putExtra("fromDocuments", true).putExtra("typeOfVehicle", typeOfVehicle));
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            }else
            {
                startActivity(new Intent(Documents.this, AddVehicleActivity.class).putExtra("fromSplash", fromSpash));
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            }
        }
    }

    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (fromSpash) {
            if (doubleBackToExitPressedOnce) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.txt_back_to_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
        else {
            if (!isBackPressed)
            {
                isBackPressed = true;
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(Documents.this, Dashboard.class);
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
}
