package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jambacabs.driver.R;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.callbacks.ISuggestions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsAdaptor extends RecyclerView.Adapter<SuggestionsAdaptor.MyViewHolder>
{
    private List<String> arrSuggestions;
    private LayoutInflater inflater;
    private ISuggestions iSuggestions;

    public SuggestionsAdaptor(Context context, ISuggestions iSuggestions)
    {
        this.iSuggestions = iSuggestions;
        this.inflater = LayoutInflater.from(context);
    }

    public void setListContent(ArrayList<String> arrSuggestions) {
        this.arrSuggestions = arrSuggestions;
        notifyItemRangeChanged(0, arrSuggestions.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.suggestions_layout, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {
        String message = arrSuggestions.get(position);
        holder.tvName.setText(message);
        holder.itemView.setOnClickListener(v ->
                iSuggestions.onSuggestionClicked(message));
    }

    @Override
    public int getItemCount() {
        return arrSuggestions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvName;
        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
