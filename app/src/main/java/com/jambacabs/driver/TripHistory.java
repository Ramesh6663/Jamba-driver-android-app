package com.jambacabs.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.jambacabs.driver.R;
import com.jambacabs.driver.adaptor.EarningsPagerAdapter;
import com.jambacabs.driver.singleton.Utilities;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class TripHistory extends AppCompatActivity {

    private ViewPager mPager;
    private DotsIndicator mIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        mPager = findViewById(R.id.pager);
        mIndicator = findViewById(R.id.dotsIndicator);

        initViews();

    }

    private void initViews(){
        Utilities.setFirebaseAnalytics(TripHistory.this);
        EarningsPagerAdapter mAdapter = new EarningsPagerAdapter(getSupportFragmentManager(), null);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin(20);
        
        mIndicator.setViewPager(mPager);

    }
}
