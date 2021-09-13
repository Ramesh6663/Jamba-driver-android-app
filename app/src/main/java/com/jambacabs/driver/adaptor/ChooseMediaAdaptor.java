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

public class ChooseMediaAdaptor extends ArrayAdapter<String>
{
    private Context context;
    private List<String> arr_list_data;

    public ChooseMediaAdaptor(Context context, int resource, ArrayList<String> arr_list_data)
    {
        super(context, resource,  arr_list_data);
        this.context = context;
        this.arr_list_data = arr_list_data;
    }

    class ViewHolder
    {
        TextView tvName;
        ImageView ivIcon;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View row = inflater.inflate(R.layout.media_layout, parent, false);

        holder.tvName = row.findViewById(R.id.tvName);
        holder.ivIcon = row.findViewById(R.id.ivIcon);
        String name = arr_list_data.get(position);

        if (name.equals(context.getResources().getString(R.string.camera)))
        {
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_camera));
        }else if (name.equals(context.getResources().getString(R.string.gallery)))
        {
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_gallery));
        }
        holder.tvName.setText(name);
        return row;
    }

    @Override
    public int getCount() {
        return arr_list_data.size();
    }
}