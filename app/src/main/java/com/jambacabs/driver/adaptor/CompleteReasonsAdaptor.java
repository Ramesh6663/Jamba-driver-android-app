package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.jambacabs.driver.R;
import com.jambacabs.driver.callbacks.ICancel;
import com.jambacabs.driver.font.CustomTextView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CompleteReasonsAdaptor extends RecyclerView.Adapter<CompleteReasonsAdaptor.MyViewHolder>
{
    private List<String> arrDetails;
    private LayoutInflater inflater;
    private ICancel mCancel;
    private Context mContext;
    private int selectedPosition;

    public CompleteReasonsAdaptor(Context context, ICancel iCancel)
    {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        setiCancel(iCancel);
    }

    public void setListContent(ArrayList<String> arrDetails) {
        this.arrDetails = arrDetails;
        notifyItemRangeChanged(0, arrDetails.size());
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cancel_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {

        holder.tvName.setText(arrDetails.get(position));
        holder.imgvwIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_close));

        holder.imgvwIcon.setImageDrawable(selectedPosition == position ? ContextCompat.getDrawable(mContext,R.drawable.ic_radio_checked_white) : ContextCompat.getDrawable(mContext,R.drawable.ic_radio_white));

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            String strSelectedElement = arrDetails.get(position);
            mCancel.onClickReason(strSelectedElement);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return arrDetails.size();
    }

    private void setiCancel(ICancel iCancel)
    {
        this.mCancel = iCancel;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgvwIcon;
        CustomTextView tvName;
        MyViewHolder(View itemView) {
            super(itemView);
            imgvwIcon = itemView.findViewById(R.id.imgvw_icon);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
