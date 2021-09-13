package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jambacabs.driver.R;
import com.jambacabs.driver.callbacks.IVehicles;
import com.jambacabs.driver.font.CustomTextView;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VehiclesAdaptor  extends RecyclerView.Adapter<VehiclesAdaptor.MyViewHolder>
{
    private List<JSONObject> arr_list_data;
    private IVehicles iVehicles;
    private LayoutInflater inflater;
    private int selected_position = 0;
    private boolean is_any_selected = false;
    private boolean is_approved;
    private Context mContext;
    public VehiclesAdaptor(Context context, IVehicles iVehicles, boolean is_approved)
    {
        inflater = LayoutInflater.from(context);
        this.iVehicles = iVehicles;
        this.is_approved = is_approved;
        this.mContext = context;
    }

    public void setListContent(ArrayList<JSONObject> arr_approved_vehicles) {
        this.arr_list_data = arr_approved_vehicles;
        notifyItemRangeChanged(0, arr_list_data.size());
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vehicles_info_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {

        JSONObject dict_element = arr_list_data.get(position);
        String selected_item_make = dict_element.optString("make");
        String selected_item_model = dict_element.optString("model");
        String selected_item = selected_item_make + " " + selected_item_model;
        String reg_number = dict_element.optString("licenseNumber");
        holder.tvVehicle.setText(selected_item);
        holder.tvNumber.setText(reg_number);
        boolean is_vehicle_available = dict_element.optBoolean("available");

        if (is_approved)
        {
            holder.swVehicle.setVisibility(View.VISIBLE);
        }else
        {
            holder.swVehicle.setVisibility(View.GONE);
        }

        if (is_vehicle_available)
        {
            selected_position = position;
            is_any_selected = true;
            holder.swVehicle.setChecked(true);
        }else
        {
            holder.swVehicle.setChecked(false);
        }
        String type = dict_element.optString("type");
        populateVehicleIcon(type, holder.ivVehicle);

        holder.llVehicleView.setOnClickListener(v -> {
                int current_position  = holder.getAdapterPosition();

                JSONObject dict_selected_element = arr_list_data.get(position);

                iVehicles.onVehiclesClicked(dict_selected_element, current_position, selected_position,true, is_approved);
        });

        holder.swVehicle.setOnCheckedChangeListener((compoundButton, b) -> {
            int current_position  = holder.getAdapterPosition();
            if (!is_any_selected)
            {
                selected_position = current_position;
            }

            JSONObject dict_selected_element = arr_list_data.get(position);

            if (dict_selected_element.optBoolean("available"))
            {
                holder.swVehicle.setChecked(false);
            }else
            {
                holder.swVehicle.setChecked(true);
            }
            iVehicles.onVehiclesClicked(dict_selected_element, current_position, selected_position,false, is_approved);
            selected_position = current_position;
        });
    }

    private void populateVehicleIcon(String type, ImageView imageView)
    {
        switch (type)
        {
            case "car":
                Glide.with(mContext.getApplicationContext()).load(R.drawable.car_uber).asBitmap().placeholder(R.drawable.car_uber).error(R.drawable.car_uber).fitCenter().into(imageView);
                break;
            case "bike":
                Glide.with(mContext.getApplicationContext()).load(R.drawable.ic_bike).asBitmap().placeholder(R.drawable.ic_bike).error(R.drawable.ic_bike).fitCenter().into(imageView);
                break;
            case "auto":
                Glide.with(mContext.getApplicationContext()).load(R.drawable.ic_auto).asBitmap().placeholder(R.drawable.ic_auto).error(R.drawable.ic_auto).fitCenter().into(imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arr_list_data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView tvVehicle, tvNumber;
        private LinearLayout llVehicleView;
        private Switch swVehicle;
        private ImageView ivVehicle;
        MyViewHolder(View itemView) {
            super(itemView);
            llVehicleView = itemView.findViewById(R.id.llVehicleView);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            swVehicle = itemView.findViewById(R.id.swVehicle);
            ivVehicle = itemView.findViewById(R.id.ivVehicle);
        }

    }
}
