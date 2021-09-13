package com.jambacabs.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jambacabs.driver.adaptor.ChooseMediaAdaptor;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.AddVehiclePostBean;
import com.jambacabs.driver.models.ImageUploadBean;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.google.gson.Gson;
import com.jambacabs.driver.singleton.Constants;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public class ShowVehicleDocActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTakePhoto;
    private ImageView ivDocument;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static int GALLERY_REQUEST = 101;
    private File imageFile;
    private RelativeLayout rlProgress;
    private TextView tvDocumentName;
    private TextView tv_firstText;
    private int position = 0;
    private AddVehiclePostBean addVehiclePostBean;
    private boolean isFullUpdate = false;
    private Gson gson = null;
    private Uri cameraUri;
    private RelativeLayout rlHome;
    private String typeOfVehicle;
    private boolean fromSplash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vehicle_doc);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getIntentData();
        initUI();
        setDocTitle();
    }

    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            position = getIntent().getExtras().getInt("position");
            isFullUpdate = getIntent().getExtras().getBoolean("full_update");
            typeOfVehicle = getIntent().getExtras().getString("typeOfVehicle");
            fromSplash = getIntent().getExtras().getBoolean("fromSplash");
        }
    }

    private void initUI() {
        Utilities.setFirebaseAnalytics(ShowVehicleDocActivity.this);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        ivDocument = findViewById(R.id.ivDocument);
        rlProgress = findViewById(R.id.rlProgress);
        tvDocumentName = findViewById(R.id.tvDocumentName);
        tv_firstText = findViewById(R.id.tv_firstText);
        rlHome = findViewById(R.id.rlHome);


        try {
            gson = new Gson();
            String json = new UserSession(this).getFirstVehicleData();
            addVehiclePostBean = gson.fromJson(json, AddVehiclePostBean.class);

        } catch (Throwable t)
        {
            Timber.v("t%s", t.getLocalizedMessage());
        }
    }


    private void setDocTitle() {
        if (position == 1) {
            tvDocumentName.setText(getResources().getString(R.string.doc_vehicle_front));
            String text = "1. Get your " + getResources().getString(R.string.doc_vehicle_front);
            tv_firstText.setText(text);
            if (addVehiclePostBean.getRegistrationCertificateFront() != null) {
                Glide.with(getApplicationContext()).load(addVehiclePostBean.getRegistrationCertificateFront()).asBitmap().error(R.drawable.id_proof).fitCenter().into(ivDocument);
            }
        } else if (position == 2) {
            tvDocumentName.setText(getResources().getString(R.string.doc_vehicle_back));
            String text = "1. Get your " + getResources().getString(R.string.doc_vehicle_back);
            tv_firstText.setText(text);
            if (addVehiclePostBean.getRegistrationCertificateBack() != null) {
                Glide.with(getApplicationContext()).load(addVehiclePostBean.getRegistrationCertificateBack()).asBitmap().error(R.drawable.id_proof).fitCenter().into(ivDocument);
            }
        } else if (position == 3) {
            tvDocumentName.setText(getResources().getString(R.string.insurance_policy));
            String text = "1. Get your " + getResources().getString(R.string.insurance_policy);
            tv_firstText.setText(text);
            if (addVehiclePostBean.getInsurancePolicy() != null) {
                Glide.with(getApplicationContext()).load(addVehiclePostBean.getInsurancePolicy()).asBitmap().error(R.drawable.id_proof).fitCenter().into(ivDocument);
            }
        } else if (position == 4) {
            tvDocumentName.setText(getResources().getString(R.string.pollution_certificate));
            String text = "1. Get your " + getResources().getString(R.string.pollution_certificate);
            tv_firstText.setText(text);
            if (addVehiclePostBean.getPollutionUnderControl() != null) {
                Glide.with(getApplicationContext()).load(addVehiclePostBean.getPollutionUnderControl()).asBitmap().error(R.drawable.id_proof).fitCenter().into(ivDocument);
            }
        }else if (position == 5) {
            tvDocumentName.setText(getResources().getString(R.string.permit_certificate));
            String text = "1. Get your " + getResources().getString(R.string.permit_certificate);
            tv_firstText.setText(text);
            if (addVehiclePostBean.getPermitCertificate() != null) {
                Glide.with(getApplicationContext()).load(addVehiclePostBean.getPermitCertificate()).asBitmap().error(R.drawable.id_proof).fitCenter().into(ivDocument);
            }
        }else if (position == 6) {
            tvDocumentName.setText(getResources().getString(R.string.fitness_certificate));
            String text = "1. Get your " + getResources().getString(R.string.fitness_certificate);
            tv_firstText.setText(text);
            if (addVehiclePostBean.getFitnessCertificate() != null) {
                Glide.with(getApplicationContext()).load(addVehiclePostBean.getFitnessCertificate()).asBitmap().error(R.drawable.id_proof).fitCenter().into(ivDocument);
            }
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnTakePhoto) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
            } else {
                if (btnTakePhoto.getText().toString().equals(getResources().getString(R.string.upload_photo))) {
                    if (imageFile != null) {
                        if (Utilities.isInternetAvailable(ShowVehicleDocActivity.this))
                        {
                            showLoading();
                            uploadDocumentTask();
                        }else
                        {
                            Utilities.showMessage(ivDocument, getResources().getString(R.string.internet_error));
                        }
                    }
                } else {
                    showCustomDialog();
                }
            }
        } else if (id == R.id.ivBack) {
            onBackPressed();
        }
    }

    public void launchCamera() {
        try {
            imageFile = createImageFile();
        } catch (IOException ex) {
            Timber.v("exxx%s", ex.getLocalizedMessage());
        }

        if (imageFile != null) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
//            File photoURI = imageFile;
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void uploadDocumentTask() {

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);

        Call<ImageUploadBean> call = apiService.imageUpload(new UserSession(ShowVehicleDocActivity.this).getAccessToken(),body);
        call.enqueue(new Callback<ImageUploadBean>() {
            @Override
            public void onResponse(@NonNull Call<ImageUploadBean> call, @NonNull retrofit2.Response<ImageUploadBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(ivDocument, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(ivDocument);
                    }else
                    {
                        if (response.body() != null) {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                hideLoading();
                                setUploadedData(response.body());
                            } else {
                                hideLoading();
                                Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    Timber.v("ParseError%s", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageUploadBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
            }
        });

    }

    private void setUploadedData(ImageUploadBean body) {

        if (addVehiclePostBean == null)
            addVehiclePostBean = new AddVehiclePostBean();
        switch (position)
        {
            case 1:
                addVehiclePostBean.setRegistrationCertificateFront(body.getImage());
                break;
            case 2:
                addVehiclePostBean.setRegistrationCertificateBack(body.getImage());
                break;
            case 3:
                addVehiclePostBean.setInsurancePolicy(body.getImage());
                break;
            case 4:
                addVehiclePostBean.setPollutionUnderControl(body.getImage());
                break;
            case 5:
                addVehiclePostBean.setPermitCertificate(body.getImage());
                break;
            default:
                addVehiclePostBean.setFitnessCertificate(body.getImage());
                break;
        }

        String vehicleData = gson.toJson(addVehiclePostBean);
        new UserSession(this).setFirstVehicleData(vehicleData);

        onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCustomDialog();
            } else {
                Utilities.showMessage(rlHome, "camera permission denied");
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageFile = new File(Objects.requireNonNull(result.getUri().getPath()));
                Glide.with(getApplicationContext()).load(result.getUri()).into(ivDocument);
                btnTakePhoto.setText(getResources().getString(R.string.upload_photo));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Timber.v("error%s", error.getLocalizedMessage());
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            CropImage.activity(cameraUri)
                    .start(this);
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            cameraUri = data.getData();
            CropImage.activity(cameraUri)
                    .start(this);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void showCustomDialog() {
        final Dialog custom_dialog = new Dialog(this);
        custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custom_dialog.setContentView(R.layout.singleton_custom_dialog_layout);
        custom_dialog.setCanceledOnTouchOutside(true);
        custom_dialog.setCancelable(true);

        RelativeLayout rlDialogBack = custom_dialog.findViewById(R.id.rlDialogBack);
        rlDialogBack.setVisibility(View.GONE);

        Window window = custom_dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        final ListView listView = custom_dialog.findViewById(R.id.listview);
        final CustomTextView tvTitle = custom_dialog.findViewById(R.id.tvTitle);

        tvTitle.setText(getResources().getString(R.string.select));
        tvTitle.setGravity(Gravity.CENTER);

        ArrayList<String> arr_options = new ArrayList<>();
        arr_options.add(getResources().getString(R.string.camera));
        arr_options.add(getResources().getString(R.string.gallery));

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = arr_options.get(position);
            if (item.equals(getResources().getString(R.string.camera))) {
                launchCamera();
            } else {
                chooseGallery();
            }
            custom_dialog.dismiss();
        });
        ArrayAdapter adaptor = new ChooseMediaAdaptor(ShowVehicleDocActivity.this, R.layout.single_list_item, arr_options);
        listView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
        custom_dialog.show();
    }

    private void chooseGallery() {
        if (ActivityCompat.checkSelfPermission(ShowVehicleDocActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ShowVehicleDocActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST);
        } else {
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                Timber.v("ParseError%s", e.getLocalizedMessage());
            }
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
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
        Intent intent = new Intent(ShowVehicleDocActivity.this, VehicleDocumentsList.class);
        intent.putExtra("fromSplash", fromSplash);
        intent.putExtra("full_update", isFullUpdate);
        intent.putExtra("typeOfVehicle", typeOfVehicle);
        startActivity(intent);
        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
        finish();
    }
}
