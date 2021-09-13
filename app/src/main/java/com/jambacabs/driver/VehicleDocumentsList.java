package com.jambacabs.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.models.AddVehiclePostBean;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.models.UpdateVehiclePostBean;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.google.gson.Gson;
import com.jambacabs.driver.singleton.Constants;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.jambacabs.driver.singleton.Constants.URL;

public class VehicleDocumentsList extends AppCompatActivity implements View.OnClickListener {

    private TextView tvFrontExpiry;
    private TextView tvBackSideExpiry;
    private TextView tvPolicyExpire;
    private TextView tvPollutionExpire;
    private TextView tvGetRides;
    private ImageView ivLicenseRegistration;
    private ImageView ivRegistrationBack;
    private ImageView ivInsurancePolicy;
    private ImageView ivPollutionCert;
    private ImageView ivPermitCertificate;
    private ImageView ivFitnessCertificate;
    private RelativeLayout rlProgress;
    private boolean fromSplash = false;
    private boolean isFullUpdate = false;
    private boolean isFromAddVehicle = false;
    private AddVehiclePostBean addVehiclePostBean;
    private String typeOfVehicle;
    private LinearLayout llBackRegistration;
    private LinearLayout llPermitCertificate;
    private LinearLayout llFitnessCertificate;
    private TextView tvTitleRegistrationFront;
    private TextView tvPermitExpire;
    private TextView tvFitnessExpire;
    private View viewFifth;
    private View viewSix;
    private boolean fromDocuments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_vehicle_documents);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getIntentData();
        initUI();
    }

    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            fromSplash = getIntent().getExtras().getBoolean("fromSplash");
            isFullUpdate = getIntent().getExtras().getBoolean("full_update");
            isFromAddVehicle = getIntent().getExtras().getBoolean("isFromAddVehicle");
            typeOfVehicle = getIntent().getExtras().getString("typeOfVehicle");
            fromDocuments = getIntent().getExtras().getBoolean("fromDocuments");
        }
    }

    private void initUI() {
        try {
            Gson gson = new Gson();
            String json = new UserSession(this).getFirstVehicleData();
            addVehiclePostBean = gson.fromJson(json, AddVehiclePostBean.class);

            tvTitleRegistrationFront = findViewById(R.id.tvTitleRegistrationFront);
            tvFrontExpiry = findViewById(R.id.tvFrontExpiry);
            tvBackSideExpiry = findViewById(R.id.tvBackSideExpiry);
            llBackRegistration = findViewById(R.id.llBackRegistration);
            ivLicenseRegistration = findViewById(R.id.ivLicenseRegistration);
            ivRegistrationBack = findViewById(R.id.ivRegistrationBack);
            ivInsurancePolicy = findViewById(R.id.ivInsurancePolicy);
            ivPollutionCert = findViewById(R.id.ivPollutionCert);
            llPermitCertificate = findViewById(R.id.llPermitCertificate);
            llFitnessCertificate = findViewById(R.id.llFitnessCertificate);
            ivFitnessCertificate = findViewById(R.id.ivFitnessCertificate);
            ivPermitCertificate = findViewById(R.id.ivPermitCertificate);
            tvPermitExpire = findViewById(R.id.tvPermitExpire);
            tvFitnessExpire = findViewById(R.id.tvFitnessExpire);
            viewFifth = findViewById(R.id.viewFifth);
            viewSix = findViewById(R.id.viewSix);

            rlProgress = findViewById(R.id.rlProgress);
            TextView tvDocumentTitle = findViewById(R.id.tvDocumentTitle);
            tvPolicyExpire = findViewById(R.id.tvPolicyExpire);
            tvPollutionExpire = findViewById(R.id.tvPollutionExpire);
            tvGetRides = findViewById(R.id.tvGetRides);
            RelativeLayout rlDocumentsBack = findViewById(R.id.rlShowDocumentsBack);
            if (fromSplash) {
                rlDocumentsBack.setVisibility(View.GONE);
            } else  {
                rlDocumentsBack.setVisibility(View.VISIBLE);
            }

            if (isFromAddVehicle) {
                rlDocumentsBack.setVisibility(View.VISIBLE);
            }

            if (fromDocuments)
                rlDocumentsBack.setVisibility(View.VISIBLE);

            tvDocumentTitle.setText(addVehiclePostBean.getLicenseNumber());
            tvDocumentTitle.setPaintFlags(tvDocumentTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            checkDocumentAvailable();

        } catch (Throwable t) {
            Timber.v("ParseError%s", t.getLocalizedMessage());
        }
        //showLoading();
        //getVehicleDocumentDataTask();
    }

    private void checkDocumentAvailable()
    {
        boolean isAllDocumentsVerified;
        if (typeOfVehicle.equals("bike"))
        {
            llBackRegistration.setVisibility(View.VISIBLE);
            llPermitCertificate.setVisibility(View.GONE);
            llFitnessCertificate.setVisibility(View.GONE);
            viewFifth.setVisibility(View.GONE);
            viewSix.setVisibility(View.GONE);

            tvTitleRegistrationFront.setText(getResources().getString(R.string.doc_vehicle_front));

            if (addVehiclePostBean.getRegistrationCertificateFront() == null) {
                isAllDocumentsVerified = false;
                ivLicenseRegistration.setImageResource(R.drawable.ic_cross);
            } else {
                ivLicenseRegistration.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getRegistrationCertificateExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvFrontExpiry.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, true);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvFrontExpiry.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getRegistrationCertificateBack() == null) {
                isAllDocumentsVerified = false;
                ivRegistrationBack.setImageResource(R.drawable.ic_cross);
            } else {
                ivRegistrationBack.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getRegistrationCertificateExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(addVehiclePostBean.getRegistrationCertificateExpiryDate());
                    tvBackSideExpiry.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvBackSideExpiry.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getInsurancePolicy() == null) {
                isAllDocumentsVerified = false;
                ivInsurancePolicy.setImageResource(R.drawable.ic_cross);
            } else {
                ivInsurancePolicy.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getInsurancePolicyExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvPolicyExpire.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvPolicyExpire.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getPollutionUnderControl() == null) {
                isAllDocumentsVerified = false;
                ivPollutionCert.setImageResource(R.drawable.ic_cross);
            } else {
                ivPollutionCert.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getPollutionUnderControlExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvPollutionExpire.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvPollutionExpire.setText(strExpiryDate);
                }
            }

        }else
        {
            llBackRegistration.setVisibility(View.GONE);
            llPermitCertificate.setVisibility(View.VISIBLE);
            llFitnessCertificate.setVisibility(View.VISIBLE);
            viewFifth.setVisibility(View.VISIBLE);
            viewSix.setVisibility(View.VISIBLE);

            tvTitleRegistrationFront.setText(getResources().getString(R.string.doc_vehicle_regis));

            if (addVehiclePostBean.getRegistrationCertificateFront() == null) {
                isAllDocumentsVerified = false;
                ivLicenseRegistration.setImageResource(R.drawable.ic_cross);
            } else {
                ivLicenseRegistration.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getRegistrationCertificateExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvFrontExpiry.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, true);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvFrontExpiry.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getInsurancePolicy() == null) {
                isAllDocumentsVerified = false;
                ivInsurancePolicy.setImageResource(R.drawable.ic_cross);
            } else {
                ivInsurancePolicy.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getInsurancePolicyExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvPolicyExpire.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvPolicyExpire.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getPollutionUnderControl() == null) {
                isAllDocumentsVerified = false;
                ivPollutionCert.setImageResource(R.drawable.ic_cross);
            } else {
                ivPollutionCert.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getPollutionUnderControlExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvPollutionExpire.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvPollutionExpire.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getPermitCertificate() == null) {
                isAllDocumentsVerified = false;
                ivPermitCertificate.setImageResource(R.drawable.ic_cross);
            } else {
                ivPermitCertificate.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getPermitCertificateExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvPermitExpire.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvPermitExpire.setText(strExpiryDate);
                }
            }

            if (addVehiclePostBean.getFitnessCertificate() == null) {
                isAllDocumentsVerified = false;
                ivFitnessCertificate.setImageResource(R.drawable.ic_cross);
            } else {
                ivFitnessCertificate.setImageResource(R.drawable.ic_check);
                long longExpiry = addVehiclePostBean.getFitnessCertificateExpiryDate();
                String expiry = String.valueOf(longExpiry);
                isAllDocumentsVerified = true;
                if (!expiry.equals("0")) {
                    String expiryDate = Utilities.getDate(longExpiry);
                    tvFitnessExpire.setVisibility(View.VISIBLE);
                    checkDocumentExpiryDate(expiryDate, false);
                    String strExpiryDate = "Expires: " + expiryDate;
                    tvFitnessExpire.setText(strExpiryDate);
                }
            }
        }

        if (isAllDocumentsVerified) {
            tvGetRides.setText(getResources().getString(R.string.save_vehicle));
            tvGetRides.setVisibility(View.VISIBLE);
        } else {
            tvGetRides.setVisibility(View.GONE);
        }
    }

    private void checkDocumentExpiryDate(String documentDate, boolean isFront) {
        int totalDays = Integer.parseInt(Objects.requireNonNull(Utilities.getDatesDifferent(Utilities.getCurrentDate(), documentDate)));
        if (totalDays < 30) {
            if (isFront) {
                tvFrontExpiry.setTextColor(ContextCompat.getColor(this, R.color.red));
                ivLicenseRegistration.setImageResource(R.drawable.ic_warning);
            } else {
                tvBackSideExpiry.setTextColor(ContextCompat.getColor(this, R.color.red));
                ivRegistrationBack.setImageResource(R.drawable.ic_warning);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlDocumentsBack) {
            onBackPressed();
        } else if (id == R.id.llFrontRegistration) {
            startActivity(new Intent(VehicleDocumentsList.this, ShowVehicleDocActivity.class)
                    .putExtra("position", 1)
                    .putExtra("fromSplash", fromSplash)
                    .putExtra("full_update", isFullUpdate)
                    .putExtra("typeOfVehicle", typeOfVehicle));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llBackRegistration) {
            startActivity(new Intent(VehicleDocumentsList.this, ShowVehicleDocActivity.class).putExtra("position", 2).putExtra("fromSplash", fromSplash).putExtra("typeOfVehicle", typeOfVehicle).putExtra("full_update", isFullUpdate));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llInsurancePolicy) {
            startActivity(new Intent(VehicleDocumentsList.this, ShowVehicleDocActivity.class).putExtra("position", 3).putExtra("fromSplash", fromSplash).putExtra("typeOfVehicle", typeOfVehicle).putExtra("full_update", isFullUpdate));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.llPollutionCert) {
            startActivity(new Intent(VehicleDocumentsList.this, ShowVehicleDocActivity.class).putExtra("position", 4).putExtra("fromSplash", fromSplash).putExtra("typeOfVehicle", typeOfVehicle).putExtra("full_update", isFullUpdate));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        }else if (id == R.id.llPermitCertificate) {
            startActivity(new Intent(VehicleDocumentsList.this, ShowVehicleDocActivity.class).putExtra("position", 5).putExtra("fromSplash", fromSplash).putExtra("typeOfVehicle", typeOfVehicle).putExtra("full_update", isFullUpdate));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        }else if (id == R.id.llFitnessCertificate) {
            startActivity(new Intent(VehicleDocumentsList.this, ShowVehicleDocActivity.class).putExtra("position", 6).putExtra("fromSplash", fromSplash).putExtra("typeOfVehicle", typeOfVehicle).putExtra("full_update", isFullUpdate));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();
        } else if (id == R.id.tvGetRides) {
            showLoading();
            if (isFullUpdate) {
                if (Utilities.isInternetAvailable(VehicleDocumentsList.this)) {
                    updateVehicleData();
                } else {
                    Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
                }
            }
            else {
                if (Utilities.isInternetAvailable(VehicleDocumentsList.this)) {
                    addVehicleData();
                } else {
                    Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
                }
            }
            /*startActivity(new Intent(VehicleDocumentsList.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            finish();*/
        } else if (id == R.id.tvDocumentTitle) {
            startActivity(new Intent(VehicleDocumentsList.this, AddVehicleActivity.class).putExtra("isEditMode", true).putExtra("fromDocuments", true).putExtra("fromSplash", fromSplash));
//            finish();
//            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            //finish();
        }
    }

    private void updateVehicleData() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(URL).create(ApiInterface.class);
        UpdateVehiclePostBean updateVehicle = new UpdateVehiclePostBean();
        updateVehicle.setId(addVehiclePostBean.getLicenseNumber());
        updateVehicle.setObj(addVehiclePostBean);

        Call<CommonResponseBean> call = apiService.updateVehicle(new UserSession(VehicleDocumentsList.this).getAccessToken(), updateVehicle);
        call.enqueue(new Callback<CommonResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(ivInsurancePolicy);
                    }else
                    {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                navigateToVehicles();
                            } else {
                                hideLoading();
                                Utilities.showMessage(ivInsurancePolicy, response.body().getMessage());
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    Timber.v("ParseError%s", e.getLocalizedMessage());
                    hideLoading();
                    Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
            }
        });
    }


    private void addVehicleData() {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(URL).create(ApiInterface.class);

        Call<CommonResponseBean> call = apiService.addVehicle(new UserSession(VehicleDocumentsList.this).getAccessToken(), addVehiclePostBean);
        call.enqueue(new Callback<CommonResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(ivInsurancePolicy);
                    }else
                    {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                moveToDashboard();
                            } else {
                                hideLoading();
                                Utilities.showMessage(ivInsurancePolicy, response.body().getMessage());
                            }
                        }else
                        {
                            hideLoading();
                            Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException e) {
                    Timber.v("ParseError%s", e.getLocalizedMessage());
                    hideLoading();
                    Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(ivInsurancePolicy, getResources().getString(R.string.fail_error));
            }
        });
    }


    private void navigateToVehicles() {
        Intent intent = new Intent(VehicleDocumentsList.this, Vehicles.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!fromSplash && !new UserSession(this).getScreen().equals("")) {
            new UserSession(VehicleDocumentsList.this).remoVeFirstVehicleData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromSplash && !fromDocuments) {
            Intent intent = new Intent(VehicleDocumentsList.this, AddVehicleActivity.class);
            intent.putExtra("fromSplash", fromSplash);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        } else if (isFullUpdate) {
            finish();
        } else {
            if (fromDocuments)
            {
                Intent intent = new Intent(VehicleDocumentsList.this, Documents.class);
                intent.putExtra("fromSplash", fromSplash);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
                finish();
            }else
            {
                Intent intent = new Intent(VehicleDocumentsList.this, AddVehicleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            }
        }
    }

    public void moveToDashboard() {
        startActivity(new Intent(VehicleDocumentsList.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        new UserSession(VehicleDocumentsList.this).removeScreen();
        new UserSession(VehicleDocumentsList.this).createUserLoginSession();
        new UserSession(VehicleDocumentsList.this).remoVeFirstVehicleData();
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
    }
}
