package com.jambacabs.driver;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.jambacabs.driver.adaptor.EarningsPagerAdapter;
import com.jambacabs.driver.singleton.Utilities;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Objects;


public class EarningsDialogFragment extends DialogFragment {

    private ViewPager mPager;
    private DotsIndicator mIndicator;


    static EarningsDialogFragment newInstance() {
        EarningsDialogFragment fragment = new EarningsDialogFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_dialog, container, false);
        Utilities.setFirebaseAnalytics(Objects.requireNonNull(getActivity()));
        mPager =view. findViewById(R.id.pager);
        mIndicator =view. findViewById(R.id.dotsIndicator);
        initViews();
        return view;
    }


    private void initViews(){

        EarningsPagerAdapter mAdapter = new EarningsPagerAdapter(getChildFragmentManager(), getDialog());
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin(20);
        mIndicator.setViewPager(mPager);


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if  (dialog!=null){

            getDialog().setCanceledOnTouchOutside(true);
            Objects.requireNonNull(getDialog().getWindow()).setGravity(Gravity.TOP);
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
//            params.y = dpToPx(15);
            params.y = dpToPx();
//            params.x = dpToPx(-10);
            params.dimAmount = 0.4f;
            getDialog().getWindow().setAttributes(params);
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.trans));

        }
        super.onStart();
    }

    private int dpToPx() {
        DisplayMetrics metrics = Objects.requireNonNull(getActivity()).getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
    }
}
