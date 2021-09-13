package com.jambacabs.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jambacabs.driver.singleton.Utilities;

public class DynamicPagesActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView wvDynamicPage;
    private String htmlData,title;
    private RelativeLayout rlProgress;
    private boolean isBackPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_pages);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        Utilities.setFirebaseAnalytics(DynamicPagesActivity.this);

        getIntentData();
        initUI();
        setWebViewData();
    }


    private void getIntentData() {
        if (getIntent().getExtras()!=null){
            htmlData=getIntent().getExtras().getString("pageData");
            title=getIntent().getExtras().getString("title");
        }
    }

    private void initUI() {
        wvDynamicPage=findViewById(R.id.wvDynamicPage);
        TextView actionbarTitle = findViewById(R.id.actionbarTitle);
        rlProgress=findViewById(R.id.rlProgress);
        actionbarTitle.setText(title);
    }



    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewData() {
        showLoading();
        wvDynamicPage.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url)
            {
                hideLoading();
                super.onPageFinished(view, url);
            }
        });
        wvDynamicPage.getSettings().setJavaScriptEnabled(true);
        wvDynamicPage.loadData(htmlData, "text/html", "UTF-8");

    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlVehicleBack) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isBackPressed)
        {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(DynamicPagesActivity.this, Dashboard.class);
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
