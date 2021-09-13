package com.jambacabs.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends Activity implements View.OnClickListener {

    String tag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(EditProfile.this);
        Intent intent = getIntent();
        if (intent.hasExtra("tag"))
        {
            tag = intent.getStringExtra("tag");
        }else
        {
            tag = "";
        }
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        CircleImageView ivProfile = findViewById(R.id.ivProfile);
        CustomTextView tvFnLn = findViewById(R.id.tvFnLn);
        String str_data = new UserSession(this).getUserData();
        try {
            JSONObject obj_data = new JSONObject(str_data);
            String str_avatar = obj_data.optString("avatar");
            String str_first_name = obj_data.optString("firstName");
            String str_last_name = obj_data.optString("lastName");
            String full_name = str_first_name + " " + str_last_name;
            tvFnLn.setText(full_name);
            Uri uri = Uri.parse(str_avatar);
            Glide.with(getApplicationContext()).load(uri).asBitmap().error(R.drawable.ic_account_gray).placeholder(R.drawable.ic_account_gray).centerCrop().into(new BitmapImageViewTarget(ivProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivProfile.setImageDrawable(circularBitmapDrawable);
                }
            });
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivBack || id == R.id.ivWallpaper) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Utilities.hideSoftKeyboard(this);
        if (!tag.equals(""))
        {
            Intent intent = new Intent(EditProfile.this, Account.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        }else
        {
            Intent intent = new Intent(EditProfile.this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.move_right_enter, R.anim.move_right_exit);
            finish();
        }

    }
}