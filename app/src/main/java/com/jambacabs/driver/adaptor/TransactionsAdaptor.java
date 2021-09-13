package com.jambacabs.driver.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.jambacabs.driver.R;
import com.jambacabs.driver.callbacks.IAccount;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.models.TransactionResponseBean;
import com.jambacabs.driver.singleton.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransactionsAdaptor  extends RecyclerView.Adapter<TransactionsAdaptor.MyViewHolder>
{
    private LayoutInflater inflater;
    private IAccount mAccount;
    private List<TransactionResponseBean.Transaction> transactionList;

    public TransactionsAdaptor(Context context, List<TransactionResponseBean.Transaction> transactionList)
    {
        this.transactionList = transactionList;
        inflater = LayoutInflater.from(context);

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transactions_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {
        TransactionResponseBean.Transaction transaction = transactionList.get(position);
        //holder.tvTransactions.setText(transaction.getTransactionId());
        holder.tvTransactionDate.setText(Utilities.getTransactionDate(transaction.getTime()));
        String amount = "-\u20B9"+transaction.getAmount();
        holder.tvAmount_one.setText(amount);
        holder.tvAmount_two.setText(amount);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTransaction;
        CustomTextView tvTransactions, tvTransactionDate, tvAmount_one, tvAmount_two;
        MyViewHolder(View itemView) {
            super(itemView);
            ivTransaction = itemView.findViewById(R.id.ivTransaction);
            tvTransactions = itemView.findViewById(R.id.tvTransactions);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount_one = itemView.findViewById(R.id.tvAmount_one);
            tvAmount_two = itemView.findViewById(R.id.tvAmount_two);
        }

    }
}
