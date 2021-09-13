package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomTextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdaptor extends ArrayAdapter<JSONObject> {
    private Context context;
    private List<JSONObject> arr_list_data;

    public NotificationAdaptor(Context context, int resource, ArrayList<JSONObject> arr_list_data) {
        super(context, resource, arr_list_data);
        this.context = context;
        this.arr_list_data = arr_list_data;
    }

    class ViewHolder {
        ImageView ivNotification;
        CustomTextView tvNotification, tvReceivedDate;
        LinearLayout llNotification;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View row = inflater.inflate(R.layout.notifications_list_item, parent, false);

        holder.ivNotification = row.findViewById(R.id.ivNotification);
        holder.tvNotification = row.findViewById(R.id.tvNotification);
        holder.tvReceivedDate = row.findViewById(R.id.tvReceivedDate);
        holder.llNotification = row.findViewById(R.id.llNotification);


        JSONObject dict_element = arr_list_data.get(position);

        String str_message = dict_element.optString("title");
        boolean read = dict_element.optBoolean("read");
        String str_time = dict_element.optString("timeStamp");
        long millisecond = Long.parseLong(str_time);
        Date date = new Date(millisecond);
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());
        String dateString = dateformat.format(date);
        // String str_type = dict_element.optString("type");


        if (read) {
            holder.llNotification.setBackgroundColor(ContextCompat.getColor(context,R.color.notifications_bg_color));
            holder.tvNotification.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.tvReceivedDate.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.ivNotification.setImageDrawable(context.getDrawable(R.drawable.ic_notification_opened));
            holder.ivNotification.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.llNotification.setBackgroundColor(ContextCompat.getColor(context,R.color.notifications_bg_color_one));
            holder.tvNotification.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.tvReceivedDate.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.ivNotification.setImageDrawable(context.getDrawable(R.drawable.ic_notification_close));
            holder.ivNotification.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        holder.tvNotification.setText(str_message);
        holder.tvReceivedDate.setText(dateString);

        return row;
    }

    @Override
    public int getCount() {
        return arr_list_data.size();
    }
}