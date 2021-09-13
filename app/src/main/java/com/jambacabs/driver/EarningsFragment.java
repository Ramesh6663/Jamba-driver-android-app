package com.jambacabs.driver;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.LastTrips;
import com.jambacabs.driver.models.TodayTrips;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import com.jambacabs.driver.singleton.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EarningsFragment extends Fragment implements View.OnClickListener {

    private static final String IS_LAST_TRIP = "IS_FROM_BSP";

    private RelativeLayout rlProgress;
    private LastTrips mLastTips;
    private TodayTrips mTodaytips;
    private Dialog dialog;
    private CustomTextView priceTV, tripNameTV, timeTV, vechileTypeTV, seeAllTripTV;

    private Boolean isLastTrip;

    private EarningsFragment(Dialog dialog) {
        this.dialog = dialog;
    }

    public static EarningsFragment newInstance(Boolean isLastTrip, Dialog dialog) {
        EarningsFragment fragment = new EarningsFragment(dialog);
        Bundle args = new Bundle();
        args.putBoolean(IS_LAST_TRIP, isLastTrip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLastTrip = getArguments().getBoolean(IS_LAST_TRIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_earnings_fargment, container, false);
        Utilities.setFirebaseAnalytics(requireActivity());

        initViews(view);


        return view;
    }

    private void initViews(View view) {
        priceTV = view.findViewById(R.id.price_tv);
        tripNameTV = view.findViewById(R.id.tripNameTV);
        timeTV = view.findViewById(R.id.time_tv);
        vechileTypeTV = view.findViewById(R.id.tvTVehicleType);
        seeAllTripTV = view.findViewById(R.id.see_all_trips);
        ImageView ivVisibility = view.findViewById(R.id.ivVisibility);

        rlProgress = view.findViewById(R.id.rlProgress);

        ivVisibility.setOnClickListener(view1 -> {
            if (dialog != null)
                dialog.dismiss();
        });

        try {
            String str_user_id = new UserSession(requireContext()).getUserId();
            String user_id = String.valueOf(str_user_id);
            JSONObject dict_params = new JSONObject();
            dict_params.put("driverId", user_id);
            if (Utilities.isInternetAvailable(getActivity()))
            {
                showLoading();
                getData(dict_params);
            }else
            {
                Utilities.showMessage(getView(), getResources().getString(R.string.internet_error));
            }
        } catch (JSONException e) {
           Log.v("parseError", "ParseError"+e.getLocalizedMessage());
        }


        seeAllTripTV.setOnClickListener(view12 -> {
            if (isLastTrip) {
                Intent intent = new Intent(getActivity(), Trips.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                requireActivity().finish();

            } else {
                Intent intent = new Intent(getActivity(), Earnings.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                requireActivity().finish();
            }
        });
        //setTripsData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setTripsData() {

        if (isLastTrip) {

            if (mLastTips == null) {
                vechileTypeTV.setVisibility(View.GONE);
                String defaultValue = " 0.00";
                priceTV.setText(defaultValue);
                tripNameTV.setText(getResources().getString(R.string.no_last_trips));
                timeTV.setVisibility(View.GONE);
            } else {
                vechileTypeTV.setVisibility(View.VISIBLE);
                String earnings = " " + mLastTips.getDriverEarnings();
                priceTV.setText(earnings);
                String time = Utilities.getEndTripDate(mLastTips.getStartTime()) + " at " + Utilities.getEndTripTime(mLastTips.getStartTime());
                timeTV.setText(time);
                vechileTypeTV.setText(mLastTips.getVehicleType());
            }
        } else {
            if (mTodaytips == null) {
                vechileTypeTV.setVisibility(View.GONE);
                String defaultValue = " 0.00";
                priceTV.setText(defaultValue);
                tripNameTV.setText(getResources().getString(R.string.today));
                String completedTrips = getResources().getString(R.string.zero_placeholder) + " " + getResources().getString(R.string.trips_completed);
                timeTV.setText(completedTrips);
                seeAllTripTV.setText(getResources().getString(R.string.weekly_summary));
            } else {
                vechileTypeTV.setVisibility(View.GONE);
                String amount = " " + mTodaytips.getAmount();
                priceTV.setText(amount);
                tripNameTV.setText(getResources().getString(R.string.today));
                String time = mTodaytips.getCount() + " " + getResources().getString(R.string.trips_completed);
                timeTV.setText(time);
                seeAllTripTV.setText(getResources().getString(R.string.weekly_summary));
            }


        }


    }

    private void getData(JSONObject jsonObject) {
        String append_url = Constants.RIDES_URL + Constants.TODAY_TRIPS;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, append_url, jsonObject,
                response -> {
                    if (response != null) {
                        String status = response.optString("type");
                        String message = response.optString("message");
                        if (status.equals("success")) {
                            JSONObject dict_data = response.optJSONObject("data");
                            assert dict_data != null;
                            if (!dict_data.optString("lastTrip").equals("")) {
                                mLastTips = LastTrips.parse(dict_data.optString("lastTrip"));
                            } else {
                                Log.d("Last trips", "Empty Last Trip");
                            }
                            if (!dict_data.optString("today").equals("")) {
                                mTodaytips = TodayTrips.parse(dict_data.optString("today"));
                            } else {
                                Log.d("Today trips", "Empty today trips");
                            }
                            setTripsData();
                        } else {
                            Utilities.showMessage(getView(), message);
                        }
                    } else {
                        Utilities.showMessage(getView(), getResources().getString(R.string.fail_error));
                    }
                    hideLoading();
                },
                error -> {
                    hideLoading();
                    NetworkResponse res = error.networkResponse;
                    if (res != null)
                    {
                        if (res.statusCode == 403)
                        {
                            showLoading();
                            Utilities.showMessage(getView(), getResources().getString(R.string.session_expired));
                            JambaCabApplication.getInstance().removeDriver(getView());
                        }else
                        {
                            Utilities.showMessage(getView(), getResources().getString(R.string.fail_error));
                        }
                    }else
                    {
                        Utilities.showMessage(getView(), getResources().getString(R.string.fail_error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("x-custom-token", new UserSession(requireContext()).getAccessToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


    }


    private void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rlProgress.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
       /* int id = view.getId();
        if (id == R.id.see_all_trips) {
            if (isLastTrip) {
                Intent intent = new Intent(getActivity(), EarningDetails.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                getActivity().finish();

            } else {
                Intent intent = new Intent(getActivity(), Trips.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit);
                getActivity().finish();
            }

        }*/
    }
}

