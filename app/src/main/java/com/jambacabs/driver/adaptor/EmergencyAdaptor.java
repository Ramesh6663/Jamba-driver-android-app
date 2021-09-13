package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jambacabs.driver.R;

import java.util.ArrayList;
import java.util.List;

public class EmergencyAdaptor extends ArrayAdapter<String>
{
    private Context context;
    private List<String> arr_list_data;
    private List<Integer> arrEmergencyIcon;

    public EmergencyAdaptor(Context context, int resource, ArrayList<String> arr_list_data, ArrayList<Integer> arrEmergencyIcon)
    {
        super(context, resource,  arr_list_data);
        this.context = context;
        this.arr_list_data = arr_list_data;
        this.arrEmergencyIcon = arrEmergencyIcon;
    }

    class ViewHolder
    {
        TextView tvName;
        ImageView ivEmergencyIcon;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;
        holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        row = inflater.inflate(R.layout.emergency_list_item, parent, false);

        holder.tvName = row.findViewById(R.id.tvName);
        holder.ivEmergencyIcon = row.findViewById(R.id.ivEmergencyIcon);
        String name = arr_list_data.get(position);
        int icon = arrEmergencyIcon.get(position);
        switch (name)
        {
            case "112":
                name = context.getString(R.string.police) + name;
                break;
            case "101":
                name = context.getString(R.string.fire) + name;
                break;
            case "102":
                name = context.getString(R.string.ambulance) + name;
                break;
        }
        holder.tvName.setText(name);
        holder.ivEmergencyIcon.setImageResource(icon);
        return row;
    }

    @Override
    public int getCount() {
        return arr_list_data.size();
    }
}