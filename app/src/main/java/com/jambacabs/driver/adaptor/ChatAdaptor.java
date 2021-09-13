package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.ChatModel;
import com.jambacabs.driver.singleton.Constants;
import com.jambacabs.driver.singleton.Utilities;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdaptor extends RecyclerView.Adapter<ChatAdaptor.MyViewHolder>
{
    private List<ChatModel> arrChatDetails;
    private LayoutInflater inflater;
    private Activity activity;
    private Context mContext;

    public ChatAdaptor(Context context, Activity activity)
    {
        this.mContext = context;
        this.activity = activity;
        this.inflater = LayoutInflater.from(context);
    }

    public void setListContent(ArrayList<ChatModel> arrChatDetails) {
        this.arrChatDetails = arrChatDetails;
        notifyItemRangeChanged(0, arrChatDetails.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == 0)
        {
            view = inflater.inflate(R.layout.driver_chat_layout, parent, false);
        }else
        {
            view = inflater.inflate(R.layout.user_chat_layout, parent, false);
        }
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {

        ChatModel model = arrChatDetails.get(position);
        holder.tvMessage.setText(model.getMessage());
        long time = model.getChatId();
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String chatTime = formatter.format(date);
        holder.tvTime.setText(chatTime);
        if (model.getRole().equals(Constants.USER))
        {
            holder.ivCheck.setVisibility(View.GONE);
        }else
        {
            holder.ivCheck.setVisibility(View.VISIBLE);
            if (model.isRead())
            {
                holder.ivCheck.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_blue));
            }else
            {
                holder.ivCheck.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_gray));
            }
        }
        holder.itemView.setOnClickListener(v -> Utilities.hideSoftKeyboard(activity));
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel model = arrChatDetails.get(position);
        int type;
        if (model.getRole().equals("driver"))
        {
            type = 0;
        }else {
            type = 1;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return arrChatDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvMessage;
        CustomTextView tvTime;
        ImageView ivCheck;
        MyViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }
    }
}
