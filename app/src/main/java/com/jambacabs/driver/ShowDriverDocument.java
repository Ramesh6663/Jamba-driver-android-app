package com.jambacabs.driver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jambacabs.driver.adaptor.ChooseMediaAdaptor;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.helper.CircleTransform;
import com.jambacabs.driver.models.CommonResponseBean;
import com.jambacabs.driver.models.ImageUploadBean;
import com.jambacabs.driver.models.UpdateDriverBean;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ShowDriverDocument extends AppCompatActivity implements View.OnClickListener {

    private Button btnTakePhoto;
    private ImageView ivDocument;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int GALLAEY_REQUEST = 101;
    private File imageFile;
    private RelativeLayout rlProgress;
    private TextView tvDocumentName, tv_firstText;
    private int position = 0;
    private String tagFrom;
    private Uri cameraUri;
    private boolean isBackPressed;
    private LinearLayout llSecond;
    private CustomTextView tvDeliveryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_documents);
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
            if (getIntent().hasExtra("tagFrom")) {
                tagFrom = getIntent().getStringExtra("tagFrom");
            }
        }
    }

    private void initUI() {
        Utilities.setFirebaseAnalytics(ShowDriverDocument.this);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        ivDocument = findViewById(R.id.ivDocument);
        rlProgress = findViewById(R.id.rlProgress);
        tvDocumentName = findViewById(R.id.tvDocumentName);
        tv_firstText = findViewById(R.id.tv_firstText);
        llSecond = findViewById(R.id.llSecond);
        tvDeliveryName = findViewById(R.id.tvDeliveryName);
    }


    private void setDocTitle() {
        String str_user_data = new UserSession(ShowDriverDocument.this).getUserData();
        Uri dl_uri = null, dl_back_uri = null, aadhar_uri = null, bank_uri = null,  avatar_uri = null, aadharBack_uri = null;
        try {
            JSONObject dict_user_data = new JSONObject(str_user_data);

            String dl_front = dict_user_data.getString("drivingLicenseFront");
            dl_uri = Uri.parse(dl_front);

            String dl_back = dict_user_data.getString("drivingLicenseBack");
            dl_back_uri = Uri.parse(dl_back);

            String aadhar = dict_user_data.getString("aadhaarCard");
            aadhar_uri = Uri.parse(aadhar);

            String aadharBack = dict_user_data.getString("aadhaarCardBack");
            aadharBack_uri = Uri.parse(aadharBack);

            String avatar = dict_user_data.getString("avatar");
            showLoading();
            avatar_uri = Uri.parse(avatar);

            String bank_front = dict_user_data.getString("bankPassbook");
            bank_uri = Uri.parse(bank_front);


        } catch (JSONException e) {
           Log.v("e", "e"+e.getLocalizedMessage());
        }

        if (position == 1) {
            tvDocumentName.setText(getString(R.string.doc_license_front));
            String txt = "1. Get your " + getString(R.string.doc_license_front);
            tv_firstText.setText(txt);
            Glide.with(getApplicationContext()).load(dl_uri).asBitmap().placeholder(R.drawable.id_proof).error(R.drawable.id_proof).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).fitCenter().into(ivDocument);
        } else if (position == 2) {
            tvDocumentName.setText(getString(R.string.doc_license_back));
            String txt = "1. Get your " + getString(R.string.doc_license_back);
            tv_firstText.setText(txt);
            Glide.with(getApplicationContext()).load(dl_back_uri).asBitmap().placeholder(R.drawable.id_proof).error(R.drawable.id_proof).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).fitCenter().into(ivDocument);
        } else if (position == 3) {
            tvDocumentName.setText(getString(R.string.profile_photo));
            String txt = "1. Get your " + getString(R.string.profile_photo);
            tv_firstText.setText(txt);

            Glide.with(getApplicationContext()).load(avatar_uri).asBitmap().error(R.drawable.ic_account_black).placeholder(R.drawable.ic_account_black).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).centerCrop().into(new BitmapImageViewTarget(ivDocument) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivDocument.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else if (position == 4) {
            tvDocumentName.setText(getString(R.string.aadhar_card));
            String txt = "1. Get your " + getString(R.string.aadhar_card);
            tv_firstText.setText(txt);
            Glide.with(getApplicationContext()).load(aadhar_uri).asBitmap().placeholder(R.drawable.aadhar_card).error(R.drawable.aadhar_card).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).fitCenter().into(ivDocument);
        }else if (position == 5) {
            tvDocumentName.setText(getString(R.string.aadhar_card_back));
            String txt = "1. Get your " + getString(R.string.aadhar_card_back);
            tv_firstText.setText(txt);
            Glide.with(getApplicationContext()).load(aadharBack_uri).asBitmap().placeholder(R.drawable.aadhar_card).error(R.drawable.aadhar_card).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).fitCenter().into(ivDocument);
        }else if (position == 6) {
            tvDocumentName.setText(getString(R.string.bank_book));
            String txt = "1. Get your " + getString(R.string.bank_book);
            tv_firstText.setText(txt);
            Glide.with(getApplicationContext()).load(bank_uri).asBitmap().placeholder(R.drawable.id_proof).error(R.drawable.id_proof).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).fitCenter().into(ivDocument);
        }else if (position == 7) {
            llSecond.setVisibility(View.GONE);
            if(getIntent().hasExtra("value"))
            {
                String value = getIntent().getStringExtra("value");
                if (value != null) {
                    tvDeliveryName.setVisibility(View.VISIBLE);
                    if (value.equals("pickup"))
                    {
                        tvDeliveryName.setText(getResources().getString(R.string.pickup_note));
                    }else
                    {
                        tvDeliveryName.setText(getResources().getString(R.string.delivery_note));
                    }
                }
            }
            Uri uri = Uri.parse("");
            Glide.with(getApplicationContext()).load(uri).asBitmap().placeholder(R.drawable.ic_delivery).error(R.drawable.ic_delivery).listener(new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoading();
                    return false;
                }
            }).fitCenter().into(ivDocument);
        }
    }
    
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnTakePhoto) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
            } else {
                if (btnTakePhoto.getText().toString().equals(getString(R.string.upload_photo))) {
                    if (imageFile != null) {
                        if (Utilities.isInternetAvailable(ShowDriverDocument.this))
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
            Log.v("exx===", "exxx==="+ex);
        }

        if (imageFile != null) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void uploadDocumentTask() {

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.URL).create(ApiInterface.class);

        Call<ImageUploadBean> call = apiService.imageUpload(new UserSession(ShowDriverDocument.this).getAccessToken(), body);
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
                                if (tagFrom != null)
                                {
                                    if (tagFrom.equals("ridePic"))
                                    {
                                        hideLoading();
                                        ImageUploadBean bean = response.body();
                                        String image = bean.getImage();
                                        Intent intent = new Intent(ShowDriverDocument.this, Dashboard.class);
                                        intent.putExtra("image", image);
                                        setResult(Dashboard.IMAGE_CODE, intent);
                                        finish();
                                    }else
                                    {
                                        if (Utilities.isInternetAvailable(ShowDriverDocument.this))
                                        {
                                            updateDriverTask(response.body());
                                        }else
                                        {
                                            Utilities.showMessage(ivDocument, getResources().getString(R.string.internet_error));
                                        }
                                    }
                                }else
                                {
                                    if (Utilities.isInternetAvailable(ShowDriverDocument.this))
                                    {
                                        updateDriverTask(response.body());
                                    }else
                                    {
                                        Utilities.showMessage(ivDocument, getResources().getString(R.string.internet_error));
                                    }
                                }
                            } else {
                                hideLoading();
                                Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
                            }
                        }else
                        {
                            hideLoading();
                            Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ImageUploadBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
            }
        });

    }

    private void updateDriverTask(ImageUploadBean body) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.PAYMENTS_URL).create(ApiInterface.class);


        UpdateDriverBean updateDriverBean = new UpdateDriverBean();
        updateDriverBean.setMobileNumber(Long.valueOf(new UserSession(ShowDriverDocument.this).getUserId()));

        UpdateDriverBean.Data data = new UpdateDriverBean.Data();

        String str_user_data = new UserSession(ShowDriverDocument.this).getUserData();
        try {
            JSONObject dict_user_data = new JSONObject(str_user_data);

            if (position == 1) {
                data.setDrivingLicenseFront(body.getImage());
                dict_user_data.put("drivingLicenseFront", body.getImage());
            } else if (position == 2) {
                data.setDrivingLicenseBack(body.getImage());
                dict_user_data.put("drivingLicenseBack", body.getImage());
            } else if (position == 3) {
                data.setAvatar(body.getImage());
                dict_user_data.put("avatar", body.getImage());
            } else if (position == 4) {
                data.setAadhaarCard(body.getImage());
                dict_user_data.put("aadhaarCard", body.getImage());
            }else if (position == 5) {
                data.setAadhaarCardBack(body.getImage());
                dict_user_data.put("aadhaarCardBack", body.getImage());
            }else if (position == 6) {
                data.setBankPassbook(body.getImage());
                dict_user_data.put("bankPassbook", body.getImage());
            }
            new UserSession(ShowDriverDocument.this).removeUserData();
            new UserSession(ShowDriverDocument.this).setUserData(dict_user_data.toString());
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }

        updateDriverBean.setData(data);

        Call<CommonResponseBean> call = apiService.updateDriver(new UserSession(ShowDriverDocument.this).getAccessToken(), updateDriverBean);
        call.enqueue(new Callback<CommonResponseBean>() {

            @Override
            public void onResponse(@NonNull Call<CommonResponseBean> call, @NonNull retrofit2.Response<CommonResponseBean> response) {
                try {
                    if (response.code() == 403) {
                        showLoading();
                        Utilities.showMessage(ivDocument, getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(ivDocument);
                    }else
                    {
                        assert response.body() != null;
                        if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                            hideLoading();
                            Utilities.showMessage(ivDocument, response.body().getMessage());
                            onBackPressed();
                        } else {
                            hideLoading();
                            Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
                        }
                    }
                } catch (NullPointerException e) {
                   Log.v("parseError", "ParseError"+e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseBean> call, @NonNull Throwable t) {
                hideLoading();
                Utilities.showMessage(ivDocument, getResources().getString(R.string.fail_error));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCustomDialog();
            } else {
                Utilities.showMessage(rlProgress, "camera permission denied");
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
                if (position == 3)
                    Glide.with(getApplicationContext()).load(result.getUri()).transform(new CircleTransform(this)).into(ivDocument);
                else
                    Glide.with(getApplicationContext()).load(result.getUri()).into(ivDocument);
                btnTakePhoto.setText(getString(R.string.upload_photo));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.v("code", "code");
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (position == 3) {
                CropImage.activity(cameraUri).setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(5, 5)
                        .start(this);
            } else {
                CropImage.activity(cameraUri)
                        .start(this);
            }


        } else if (requestCode == GALLAEY_REQUEST && resultCode == RESULT_OK) {
            cameraUri = data.getData();
            if (position == 3) {
                CropImage.activity(cameraUri).setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(5, 5)
                        .start(this);
            } else {
                CropImage.activity(cameraUri)
                        .start(this);
            }
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
                imageFileName,
                ".jpg",
                storageDir
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
        if (tagFrom != null)
        {
            if (!tagFrom.equals("ridePic"))
            {
                arr_options.add(getResources().getString(R.string.gallery));
            }
        }else
        {
            arr_options.add(getResources().getString(R.string.gallery));
        }
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = arr_options.get(position);
            if (item.equals(getResources().getString(R.string.camera))) {
                launchCamera();
            } else {
                chooseGallery();
            }
            custom_dialog.dismiss();
        });
        ArrayAdapter adaptor = new ChooseMediaAdaptor(ShowDriverDocument.this, R.layout.single_list_item, arr_options);
        listView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
        custom_dialog.show();
    }

    private void chooseGallery() {
        if (ActivityCompat.checkSelfPermission(ShowDriverDocument.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ShowDriverDocument.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLAEY_REQUEST);
        } else {
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
               Log.v("parseError", "ParseError"+e.getLocalizedMessage());
            }
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLAEY_REQUEST);
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
        if (tagFrom != null) {
            if (tagFrom.equals("ridePic"))
            {
                finish();
            }else
            {
                if (!isBackPressed)
                {
                    isBackPressed = true;
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(ShowDriverDocument.this, Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
                        finish();
                    }, 200);
                }
            }
        } else {
            Intent intent = new Intent(ShowDriverDocument.this, Documents.class);
            intent.putExtra("fromSplash", Objects.requireNonNull(getIntent().getExtras()).getBoolean("fromSplash"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        }
    }

}
