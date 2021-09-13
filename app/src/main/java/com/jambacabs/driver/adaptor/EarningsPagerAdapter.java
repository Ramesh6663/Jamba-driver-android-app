package com.jambacabs.driver.adaptor;

import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jambacabs.driver.EarningsFragment;

public class EarningsPagerAdapter  extends FragmentPagerAdapter {


    private Dialog dialog;
    public EarningsPagerAdapter(@NonNull FragmentManager fm, Dialog dialog) {
        super(fm);
        this.dialog=dialog;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return EarningsFragment.newInstance(true,dialog);
            case 1: return EarningsFragment.newInstance(false,dialog);
        }
        return EarningsFragment.newInstance(false,dialog);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
