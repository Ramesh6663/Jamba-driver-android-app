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
import com.jambacabs.driver.font.CustomTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SingletonAdaptor extends ArrayAdapter<JSONObject>
{
    private Context context;
    private List<JSONObject> arr_list_data;
    private CustomTextView textView;

    public SingletonAdaptor(Context context, int resource, ArrayList<JSONObject> arr_list_data, CustomTextView textView)
    {
        super(context, resource,  arr_list_data);
        this.context = context;
        this.arr_list_data = arr_list_data;
        this.textView = textView;
    }

    static class ViewHolder
    {
        TextView tvName;
        ImageView imgvwSelected;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View row = inflater.inflate(R.layout.single_list_item, parent, false);

        holder.tvName = row.findViewById(R.id.tvName);
        holder.imgvwSelected = row.findViewById(R.id.imgvwSelected);
        String name = "";
        if (textView != null)
        {
            name = textView.getText().toString();
        }
        JSONObject dict_element = arr_list_data.get(position);
        String selected_item = dict_element.optString("make");
        String s1 = selected_item.substring(0, 1).toUpperCase();
        selected_item = s1 + selected_item.substring(1);


        holder.tvName.setText(selected_item);
        if (name.equals(selected_item))
        {
            holder.imgvwSelected.setVisibility(View.VISIBLE);
        }else
        {
            holder.imgvwSelected.setVisibility(View.GONE);
        }


        return row;
    }

    @Override
    public int getCount() {
        return arr_list_data.size();
    }
}