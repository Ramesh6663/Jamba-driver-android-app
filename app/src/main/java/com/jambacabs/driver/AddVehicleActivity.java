package com.jambacabs.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import androidx.annotation.Nullable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.adaptor.ModelsAdaptor;
import com.jambacabs.driver.adaptor.SingletonAdaptor;
import com.jambacabs.driver.font.CustomEditTextView;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.AddVehiclePostBean;
import com.jambacabs.driver.models.VehicleTypes;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;
import com.google.gson.Gson;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class AddVehicleActivity extends Activity implements View.OnClickListener {

    private RelativeLayout rlMain;
    private RelativeLayout rlDelivery;
    private CustomTextView tvMake, tvModel, tvYear;
    private CustomEditTextView etLicence;
    private ArrayList<JSONObject> arr_make;
    private ArrayList<JSONObject> arr_model;
    private  RelativeLayout rlProgress;
    private String vehicle_type;
    private boolean fromSplash;
    private boolean isEditMode;
    private boolean fromDoc;
    private boolean isFullUpdate;
    private AddVehiclePostBean addVehiclePostBean;
    private boolean isApproved;
    private ArrayList<VehicleTypes> arrVehicleTypes;
    private Switch swDelivery;
    private String typeOfVehicle;
    private boolean fromDocuments;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(AddVehicleActivity.this);

        tvMake = findViewById(R.id.tvMake);
        tvModel = findViewById(R.id.tvModel);
        tvYear = findViewById(R.id.tvYear);
        rlMain = findViewById(R.id.rlMain);
        etLicence = findViewById(R.id.etLicence);
        rlProgress = findViewById(R.id.rlProgress);
        rlDelivery = findViewById(R.id.rlDelivery);
        swDelivery = findViewById(R.id.swDelivery);
        RelativeLayout rlBack = findViewById(R.id.rlBack);
        arr_make = new ArrayList<>();
        arr_model = new ArrayList<>();
        arrVehicleTypes = new ArrayList<>();
        vehicle_type = "";

        if (getIntent().getExtras() != null) {
            fromSplash = getIntent().getExtras().getBoolean("fromSplash");
            isEditMode = getIntent().getExtras().getBoolean("isEditMode");
            fromDoc = getIntent().getExtras().getBoolean("fromDoc");
            isFullUpdate = getIntent().getExtras().getBoolean("is_full_update");
            fromDocuments = getIntent().getExtras().getBoolean("fromDocuments");
//            boolean isFromDriverDoc = getIntent().getExtras().getBoolean("fromDriverDoc");
            isApproved = getIntent().getExtras().getBoolean("isApproved");
            if (fromDoc) {
                rlBack.setVisibility(View.GONE);
            }
        }

        if (isEditMode) {
            try {
                showLoading();
                getVehicleModels();
                Gson gson = new Gson();
                String json = new UserSession(this).getFirstVehicleData();
                addVehiclePostBean = gson.fromJson(json, AddVehiclePostBean.class);
                tvMake.setText(addVehiclePostBean.getMake());
                tvModel.setText(addVehiclePostBean.getModel());
                String year = "" + addVehiclePostBean.getYear();
                tvYear.setText(year);
                etLicence.setText(addVehiclePostBean.getLicenseNumber());
            } catch (NullPointerException e) {
                Timber.v("ex%s", e.getLocalizedMessage());
            }

        } else if (isFullUpdate) {
            try {
                showLoading();
                getVehicleModels();
                Gson gson = new Gson();
                String json = getIntent().getExtras().getString("vehicle_data");
                addVehiclePostBean = gson.fromJson(json, AddVehiclePostBean.class);
                tvMake.setText(addVehiclePostBean.getMake());
                tvModel.setText(addVehiclePostBean.getModel());
                String year = "" + addVehiclePostBean.getYear();
                tvYear.setText(year);
                etLicence.setText(addVehiclePostBean.getLicenseNumber());
                etLicence.setEnabled(false);

                if (isApproved)
                {
                    tvMake.setEnabled(false);
                    tvModel.setEnabled(false);
                    tvYear.setEnabled(false);
                }else {
                    tvMake.setEnabled(true);
                    tvModel.setEnabled(true);
                    tvYear.setEnabled(true);
                }

            } catch (NullPointerException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }
        } else {
            if (Utilities.isInternetAvailable(AddVehicleActivity.this))
            {
                showLoading();
                getVehicleModels();
            }else
            {
                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
            }
        }
        if (addVehiclePostBean == null)
            addVehiclePostBean = new AddVehiclePostBean();

        swDelivery.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                addVehiclePostBean.setAvailableForDelivery(true);
            else
                addVehiclePostBean.setAvailableForDelivery(false);
        });
    }

    private void getVehicleModels() {
        String url = Constants.URL + Constants.VEHECLE_MODELS_NEW;
        JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        String type = response.optString("type");
                        String message = response.optString("message");
                        JSONArray arr_data = response.optJSONArray("data");
                        if (type.equals("success")) {
                            if (arr_data != null) {
                                arr_make = new ArrayList<>();
                                for (int i = 0; i < arr_data.length(); i++) {
                                    JSONObject dict_models = arr_data.optJSONObject(i);
                                    arr_make.add(dict_models);
                                }
                                if (arr_make.size() == 0) {
                                    Utilities.showMessage(rlMain, message);
                                }else {
                                    if (isEditMode||isFullUpdate){
                                        for (int i=0;i<arr_make.size();i++){
                                            try {
                                                if (arr_make.get(i).get("make").toString().equalsIgnoreCase(tvMake.getText().toString())){
                                                    showModel(i);
                                                }
                                            } catch (JSONException e) {
                                                Timber.v("ParseError%s", e.getLocalizedMessage());
                                            }
                                        }
                                    }
                                }
                                getVehicleTypes();
                            } else {
                                hideLoading();
                                Utilities.showMessage(rlMain, message);
                            }
                        } else {
                            hideLoading();
                            Utilities.showMessage(rlMain, message);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null) {
                        if (res.statusCode == 403) {
                            showLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(rlMain);
                        } else {
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.internet_error));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(AddVehicleActivity.this).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void  getVehicleTypes() {
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

                            if (isFullUpdate || isEditMode)
                            {
                                for (int i=0; i<arrVehicleTypes.size(); i++)
                                {
                                    VehicleTypes vt = arrVehicleTypes.get(i);
                                    String model = vt.getVehicleModel();
                                    if (model.equals(addVehiclePostBean.getVehicleType()))
                                    {
                                        String type = vt.getTypes();
                                        typeOfVehicle = type;
                                        if (type.equals("bike") && vt.getAvailability())
                                        {
                                            rlDelivery.setVisibility(View.VISIBLE);
                                            if (addVehiclePostBean.isAvailableForDelivery())
                                            {
                                                swDelivery.setChecked(true);
                                            }
                                        }else
                                        {
                                            rlDelivery.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        } else {
                            hideLoading();
                            String strMessage = response.optString(getResources().getString(R.string.message));
                            Utilities.showMessage(rlMain, strMessage);
                        }
                    } else {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                    }
                },
                error -> {
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            if (Utilities.isInternetAvailable(AddVehicleActivity.this))
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.session_expired));
                                JambaCabApplication.getInstance().removeDriver(rlMain);
                            }else
                            {
                                Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                            }
                        }else
                        {
                            hideLoading();
                            Utilities.showMessage(rlMain, getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        hideLoading();
                        Utilities.showMessage(rlMain, getResources().getString(R.string.internet_fail));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(getResources().getString(R.string.xCustomToken), new UserSession(AddVehicleActivity.this).getAccessToken());
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlAddVehicleBack) {
            onBackPressed();
        } else if (id == R.id.tvMake) {
            ArrayAdapter make_adaptor = new SingletonAdaptor(AddVehicleActivity.this, R.layout.single_list_item, arr_make, tvMake);
            showCustomDialog(make_adaptor, "Make");
        } else if (id == R.id.tvModel) {
            if (tvMake.getText().toString().equals("")) {
                Utilities.showMessage(rlMain, getResources().getString(R.string.make_error));
            } else {
                ArrayAdapter make_adaptor = new ModelsAdaptor(AddVehicleActivity.this, R.layout.single_list_item, arr_model, tvModel);
                showCustomDialog(make_adaptor, "Model");
            }
        } else if (id == R.id.tvYear) {
            if (tvMake.getText().toString().equals("")) {
                Utilities.showMessage(rlMain, getResources().getString(R.string.make_error));
            } else {
                if (tvModel.getText().toString().equals("")) {
                    Utilities.showMessage(rlMain, getResources().getString(R.string.model_error));
                } else {
                    Calendar cal = Calendar.getInstance();
                    int cyear = cal.get(Calendar.YEAR);
                    int cMonth = cal.get(Calendar.MONTH);
                    showYearPicker(cyear, cMonth);
                }
            }
        } else if (id == R.id.btnContinue) {
            Utilities.hideSoftKeyboard(AddVehicleActivity.this);
            String error_message = validateForm();
            if (!error_message.equals("")) {
                Utilities.showMessage(rlMain, error_message);
            } else {
                String mobile = new UserSession(this).getUserId();
                double user_id = Double.parseDouble(mobile);
                Long year = Long.valueOf(tvYear.getText().toString());
                String licenceNumber = Objects.requireNonNull(etLicence.getText()).toString();
                licenceNumber = licenceNumber.replace(" ", "");
                JSONObject dict_params = new JSONObject();
                try {
                    dict_params.put("licenseNumber", licenceNumber);
                    dict_params.put("make", tvMake.getText().toString());
                    dict_params.put("model", tvModel.getText().toString());
                    dict_params.put("vehicleType", vehicle_type);
                    dict_params.put("driverId", user_id);
                    dict_params.put("year", year);
                } catch (JSONException e) {
                    Timber.e("json exception%s", e.getLocalizedMessage());
                }
                addVehiclePostBean.setLicenseNumber(licenceNumber);
                addVehiclePostBean.setDriverId((long) user_id);
                addVehiclePostBean.setMake(tvMake.getText().toString());
                addVehiclePostBean.setModel(tvModel.getText().toString());
                addVehiclePostBean.setYear(year);
                if (!isFullUpdate) {
                    addVehiclePostBean.setAvailable(false);
                    addVehiclePostBean.setInsurancePolicyExpiryDate((long) 0);
                    addVehiclePostBean.setRegistrationCertificateExpiryDate((long) 0);
                    addVehiclePostBean.setPollutionUnderControlExpiryDate((long) 0);
                }

                Gson gson = new Gson();
                String vehicleData = gson.toJson(addVehiclePostBean);

                new UserSession(this).setFirstVehicleData(vehicleData);
                Intent intentVehicleDocuments = new Intent(AddVehicleActivity.this, VehicleDocumentsList.class);
                //intentVehicleDocuments.putExtra("vehicle_data", dict_params.toString() );
                if (fromSplash)
//                    new UserSession(AddVehicleActivity.this).setScreen(MOVE_SCREEN_VEHICLE_DOC);

                intentVehicleDocuments.putExtra("fromSplash", fromSplash);
                intentVehicleDocuments.putExtra("full_update", isFullUpdate);
                intentVehicleDocuments.putExtra("isFromAddVehicle", true);
                intentVehicleDocuments.putExtra("typeOfVehicle", typeOfVehicle);
                intentVehicleDocuments.putExtra("fromDocuments", fromDocuments);
                startActivity(intentVehicleDocuments);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
            }
        }
    }

    private String validateForm() {
        String error_message = "";

        if (tvMake.getText().toString().equals("")) {
            error_message = getResources().getString(R.string.make_error);
        } else {
            if (tvModel.getText().toString().equals("")) {
                error_message = getResources().getString(R.string.model_error);
            } else {
                if (tvYear.getText().toString().equals("")) {
                    error_message = getResources().getString(R.string.year_error);
                } else {
                    if (Objects.requireNonNull(etLicence.getText()).toString().equals("")) {
                        error_message = getResources().getString(R.string.error_license);
                    }
                }
            }
        }
        return error_message;
    }

    private void showYearPicker(final int year, final int month) {
        final MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(this,
                (selectedMonth, selectedYear) -> {
                    String str_year = Integer.toString(selectedYear);
                    tvYear.setText(str_year);
                }, year, month);


        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1890)
                .setActivatedYear(year)
                .setMaxYear(year)
                .setTitle("Choose Vehicle Year")
                .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                .setYearRange(1890, year)
                .showYearOnly()
                .build().show();
    }


    private void showCustomDialog(final ArrayAdapter adaptor, final String flag) {
        final Dialog custom_dialog = new Dialog(this);
        custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custom_dialog.setContentView(R.layout.singleton_custom_dialog_layout);
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.setCancelable(false);

        RelativeLayout rlDialogBack = custom_dialog.findViewById(R.id.rlDialogBack);
        rlDialogBack.setOnClickListener(v -> custom_dialog.dismiss());

        Window window = custom_dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        final CustomTextView tvTitle = custom_dialog.findViewById(R.id.tvTitle);
        final ListView listView = custom_dialog.findViewById(R.id.listview);
        final CustomTextView tvNodata = custom_dialog.findViewById(R.id.tvNodata);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            if (flag.equals("Make")) {
                JSONObject dict_element = arr_make.get(position);
                String selected_item = dict_element.optString("make");
                String s1 = selected_item.substring(0, 1).toUpperCase();
                selected_item = s1 + selected_item.substring(1);
                tvMake.setText(selected_item);
                arr_model = new ArrayList<>();
                JSONArray arr_model_data = dict_element.optJSONArray("models");
                assert arr_model_data != null;
                for (int i = 0; i < Objects.requireNonNull(arr_model_data).length(); i++) {
                    JSONObject dict_model_element = arr_model_data.optJSONObject(i);
                    arr_model.add(dict_model_element);
                }

            } else {
                JSONObject dict_element = arr_model.get(position);
                String selected_item = dict_element.optString("model");
                String s1 = selected_item.substring(0, 1).toUpperCase();
                selected_item = s1 + selected_item.substring(1);
                vehicle_type = dict_element.optString("vehicleType");
                addVehiclePostBean.setVehicleType(vehicle_type);

                if (!tvModel.getText().equals(selected_item))
                {
                    for (int i=0; i<arrVehicleTypes.size(); i++)
                    {
                        VehicleTypes vt = arrVehicleTypes.get(i);
                        String model = vt.getVehicleModel();
                        if (model.equals(vehicle_type))
                        {
                            String type = vt.getTypes();
                            typeOfVehicle = type;
                            if (type.equals("bike") && vt.getAvailability())
                            {
                                rlDelivery.setVisibility(View.VISIBLE);
                            }else
                            {
                                rlDelivery.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                tvModel.setText(selected_item);

            }
            custom_dialog.dismiss();
        });

        switch (flag) {
            case "Make":
                tvTitle.setText(getResources().getString(R.string.vehicle_makeHeading));
                listView.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
                if (arr_make.size() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    tvNodata.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.GONE);
                    tvNodata.setVisibility(View.VISIBLE);
                }
                break;
            case "Model":
                tvTitle.setText(getResources().getString(R.string.vehicle_modelHeading));
                listView.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
                if (arr_model.size() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    tvNodata.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.GONE);
                    tvNodata.setVisibility(View.VISIBLE);
                }
                break;
        }
        custom_dialog.show();
    }

    public void showModel(int position){
        JSONObject dict_element = arr_make.get(position);
        String selected_item = dict_element.optString("make");
        String s1 = selected_item.substring(0, 1).toUpperCase();
        selected_item = s1 + selected_item.substring(1);
        tvMake.setText(selected_item);
        arr_model = new ArrayList<>();
        JSONArray arr_model_data = dict_element.optJSONArray("models");
        assert arr_model_data != null;
        for (int i = 0; i < Objects.requireNonNull(arr_model_data).length(); i++) {
            JSONObject dict_model_element = arr_model_data.optJSONObject(i);
            arr_model.add(dict_model_element);
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
        if (fromDoc || fromSplash) {
            finish();
        } else {
            Intent intent = new Intent(AddVehicleActivity.this, Vehicles.class);
            intent.putExtra("fromSplash", Objects.requireNonNull(getIntent().getExtras()).getBoolean("fromSplash"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        }
    }
}