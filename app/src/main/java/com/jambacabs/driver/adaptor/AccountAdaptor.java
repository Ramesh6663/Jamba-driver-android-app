package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;


import com.jambacabs.driver.R;
import com.jambacabs.driver.callbacks.IAccount;
import com.jambacabs.driver.font.CustomTextView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AccountAdaptor extends RecyclerView.Adapter<AccountAdaptor.MyViewHolder>
{
    private List<String> arr_details;
    private LayoutInflater inflater;
    private IAccount mAccount;
    private Context mContext;

    public AccountAdaptor(Context context, IAccount iAccount)
    {
        mContext = context;
        inflater = LayoutInflater.from(context);
        setIAccount(iAccount);
    }

    public void setListContent(ArrayList<String> arr_account) {
        this.arr_details = arr_account;
        notifyItemRangeChanged(0, arr_details.size());
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.account_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {
        String element = arr_details.get(position);
        holder.tvItemName.setText(element);
        switch (element)
        {
            case "Documents":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_document));
                break;
            case "Payment":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_payment));
                break;
            case "Edit Info":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_info));
                break;
            case "Report app issues":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_report));
                break;
            case "About":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_info));
                break;
            case "Security and Privacy":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_privacy));
                break;
            case "App Settings":
                holder.ivItemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_settings));
                break;
        }

        holder.llAccount.setOnClickListener(v -> {
            String str_selected_element = arr_details.get(position);
            mAccount.onClickAccount(str_selected_element);

        });
    }

    @Override
    public int getItemCount() {
        return arr_details.size();
    }

    private void setIAccount(IAccount iAccount)
    {
        mAccount = iAccount;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        CustomTextView tvItemName;
        LinearLayout llAccount;
        MyViewHolder(View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            llAccount = itemView.findViewById(R.id.llAccount);
            tvItemName = itemView.findViewById(R.id.tvItemName);
        }

    }
}
