package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomTextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter
{
    private Context context;
    private String str_time, str_location;

    public CustomInfoWindowGoogleMap(Context ctx, String time, String str_location){
        this.context = ctx;
        this.str_time = time;
        this.str_location = str_location;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.info_winow_layout, null);
//        view.setBackgroundColor(context.getResources().getColor(R.color.black));
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout ll_time = view.findViewById(R.id.llTime);
        if (str_time.equals(""))
        {
            ll_time.setVisibility(View.GONE);
        }else
        {
            ll_time.setVisibility(View.VISIBLE);
        }
        CustomTextView tvTime = view.findViewById(R.id.tvMarkerTime);
        CustomTextView tvLocation = view.findViewById(R.id.tvMarkerLocation);
        tvTime.setText(str_time);
        tvLocation.setText(str_location);
        return view;
    }
}
