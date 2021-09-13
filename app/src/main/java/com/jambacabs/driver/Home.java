package com.jambacabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.Utilities;

public class Home extends Activity implements View.OnClickListener {

    private CustomTextView tvSignin;
    private CustomTextView tvRegister;
    private RelativeLayout rlBack;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        initUI();
    }

    private void initUI()
    {
        Utilities.setFirebaseAnalytics(Home.this);
        rlBack = findViewById(R.id.rlBack);
        tvSignin = findViewById(R.id.tvSignin);
        tvRegister = findViewById(R.id.tvRegister);

        Intent intent = getIntent();
        String tag = intent.getStringExtra("tag");
        if (tag != null)
        {
            rlBack.setVisibility(View.VISIBLE);
            handler.postDelayed(this::animate, 50);

        }
    }

    private void animate() {

        FlingAnimation flingX =
                new FlingAnimation(rlBack, DynamicAnimation.X)
                        .setStartVelocity((float) -5.5*100f)
                        .setFriction(0.5f);
        flingX.start();

        rlBack.animate().alpha(1f).setDuration(1000).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvRegister)
        {
            tvRegister.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.signin_corners));
            tvRegister.setTextColor(ContextCompat.getColor(this,R.color.white));

            tvSignin.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.border));
            tvSignin.setTextColor(ContextCompat.getColor(this,R.color.dark_blue));

            handler.postDelayed(() -> {
                Intent registerActivity = new Intent(Home.this, Register.class);
                startActivity(registerActivity);
                overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                finish();
            }, 50);
        }else if (id == R.id.tvSignin)
        {
            tvSignin.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.signin_corners));
            tvSignin.setTextColor(ContextCompat.getColor(this,R.color.white));

            tvRegister.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.border));
            tvRegister.setTextColor(ContextCompat.getColor(this,R.color.dark_blue));


            handler.postDelayed(() -> {
                Intent launchActivity1 = new Intent(Home.this, Driver_SignIn.class);
                startActivity(launchActivity1);
            }, 50);
        }else if (id == R.id.tvConnect)
        {
            String pakageName = "com.jambacabs.customer";

            Intent intent = getPackageManager().getLaunchIntentForPackage(pakageName);
            if (intent == null) {
                // Bring user to the market or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + pakageName));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
