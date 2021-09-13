package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.jambacabs.driver.JambaCabApplication;
import com.jambacabs.driver.R;
import com.jambacabs.driver.api.ApiClient;
import com.jambacabs.driver.api.ApiInterface;
import com.jambacabs.driver.callbacks.iTrips;
import com.jambacabs.driver.font.CustomButton;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.CustomerProfileBean;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.UserSession;
import com.jambacabs.driver.singleton.Utilities;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class TripsAdaptor extends ArrayAdapter<JSONObject>
{
    private Context context;
    private List<JSONObject> arr_list_data;
    private iTrips itrips;
    private String tagFrom;

    public TripsAdaptor(Context context, int resource, ArrayList<JSONObject> arr_list_data, iTrips itrips, String tagFrom)
    {
        super(context, resource,  arr_list_data);
        this.context = context;
        this.arr_list_data = arr_list_data;
        this.itrips = itrips;
        this.tagFrom = tagFrom;
    }

    static class ViewHolder
    {
        CustomTextView tvTimeFrame, tvTVehicleType, tvRideStatus, tvFromSource, tvToDestination;
        CustomButton btnStatus;
        RelativeLayout rlCancelled;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View row = inflater.inflate(R.layout.rides_listitem, parent, false);

        holder.tvTimeFrame = row.findViewById(R.id.tvTimeFrame);
        holder.tvTVehicleType = row.findViewById(R.id.tvTVehicleType);
        holder.tvRideStatus = row.findViewById(R.id.cost_tv);
        holder.tvFromSource = row.findViewById(R.id.pick_up_location_tv);
        holder.tvToDestination = row.findViewById(R.id.drop_location_tv);
        holder.rlCancelled = row.findViewById(R.id.rlCancelled);
        holder.btnStatus = row.findViewById(R.id.btnStatus);

        JSONObject dict_params = arr_list_data.get(position);

        String status = dict_params.optString("status");
        if (status.equals("ONGOING"))
        {
            holder.tvRideStatus.setText(status);
            holder.rlCancelled.setVisibility(View.GONE);
        }else
        {
            String cost = String.format(Locale.getDefault(), "%.2f", dict_params.optDouble("cost"));
            cost = context.getString(R.string.rupee_unicode) + cost;

            holder.rlCancelled.setVisibility(View.VISIBLE);
            holder.btnStatus.setText(dict_params.optString("status"));

            if (dict_params.optString("status").equals("CANCELLED"))
            {
                cost = context.getString(R.string.rupee_unicode) + "0";
                holder.btnStatus.setTextColor(ContextCompat.getColor(context, R.color.goclr_red));
            }else if (dict_params.optString("status").equals("COMPLETED"))
            {
                holder.btnStatus.setTextColor(ContextCompat.getColor(context, R.color.goclr_green));
            }
            holder.tvRideStatus.setText(cost);
        }

        String vehicle_type = dict_params.optString("vehicleType");
        holder.tvTVehicleType.setText(vehicle_type);
        holder.tvTimeFrame.setText(Utilities.getFullTripDate(Long.parseLong(dict_params.optString("startTime"))));
        String source_location = dict_params.optString("pickUpPoint");
        String dest_location = dict_params.optString("dropPoint");
        holder.tvFromSource.setText(source_location);
        holder.tvToDestination.setText(dest_location);


        holder.btnStatus.setOnClickListener(v -> {
            if (tagFrom == null)
            {
                if (dict_params.optString("status").equals("ACCEPTED") || dict_params.optString("status").equals("STARTED") || dict_params.optString("status").equals("INPROGRESS"))
                {
                    String pushParams = new UserSession(getContext()).getPushParams();
                    if (pushParams.equals(""))
                    {
                        String screen;
                        switch (dict_params.optString("status"))
                        {
                            case "ACCEPTED":
                                screen = "ride_success";
                                break;
                            case "STARTED":
                                screen = "ride_started";
                                break;
                            default:
                                screen = "ride_pickup";
                                break;
                        }
                        String userID = dict_params.optString("userId");
                        getPerticularUserDetails(userID, screen, holder.btnStatus, dict_params);
                    }else
                    {
                        itrips.onItemClicked();
                    }
                }
            }
        });

        return row;
    }

    private void getPerticularUserDetails(String userId, String screen, View view, JSONObject dictRideData) {
        ApiInterface apiService = ApiClient.retrofitBaseUrl(Constants.USERS_URL).create(ApiInterface.class);

        Call<CustomerProfileBean> call = apiService.getCustomerProfile(new UserSession(getContext()).getAccessToken(), Long.valueOf(userId));
        call.enqueue(new Callback<CustomerProfileBean>() {

            @Override
            public void onResponse(@NonNull Call<CustomerProfileBean> call, @NonNull retrofit2.Response<CustomerProfileBean> response) {
                try {
                    if (response.code() == 403) {
                        Utilities.showMessage(view, getContext().getResources().getString(R.string.session_expired));
                        JambaCabApplication.getInstance().removeDriver(view);
                    }else
                    {
                        if (response.body() != null)
                        {
                            if (response.body().getType().equals(Constants.RESPONSE_SUCCESS)) {
                                String strUserData = new Gson().toJson(response.body(), CustomerProfileBean.class);
                                JSONObject dictUserData = new JSONObject(strUserData);
                                JSONObject dictData = dictUserData.optJSONObject("data");
                                JSONObject dictParams = new JSONObject();
                                dictParams.put("rideData", dictRideData.toString());
                                if (dictData != null) {
                                    dictParams.put("user", dictData.toString());
                                }

                                new UserSession(getContext()).setScreen(screen);
                                new UserSession(getContext()).setTagFrom("push");
                                new UserSession(getContext()).setPushParams(dictParams.toString());
                                itrips.onItemClicked();
                            } else {
                                Utilities.showMessage(view, response.body().getMessage());
                            }
                        }else
                        {
                            Utilities.showMessage(view, context.getResources().getString(R.string.fetch_error));
                        }
                    }
                } catch (NullPointerException | JSONException e) {
                    Utilities.showMessage(view, context.getResources().getString(R.string.fetch_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileBean> call, @NonNull Throwable t) {
                Utilities.showMessage(view, context.getResources().getString(R.string.fetch_error));
            }
        });
    }

    @Override
    public int getCount() {
        return arr_list_data.size();
    }
}
