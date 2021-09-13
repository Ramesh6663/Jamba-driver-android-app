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
import androidx.core.content.ContextCompat;

import com.jambacabs.driver.R;

import java.util.ArrayList;
import java.util.List;

public class CancelAdaptor extends ArrayAdapter<String>
{
    private Context context;
    private List<String> arr_list_data;
    private int current;

    public CancelAdaptor(Context context, int resource, ArrayList<String> arr_list_data, int current)
    {
        super(context, resource, arr_list_data);
        this.context = context;
        this.arr_list_data = arr_list_data;
        this.current = current;
    }

    class ViewHolder
    {
        TextView tvName;
        ImageView imgvw_icon;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View row = inflater.inflate(R.layout.cancel_list_item, parent, false);
        holder.tvName = row.findViewById(R.id.tvName);
        holder.imgvw_icon = row.findViewById(R.id.imgvw_icon);
        holder.tvName.setText(arr_list_data.get(position));

        if (position == current)
        {
            holder.imgvw_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_radio_checked_white));
        }else
        {
            holder.imgvw_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_radio_white));
        }

        return row;
    }

    @Override
    public int getCount() {
        return arr_list_data.size();
    }
}
