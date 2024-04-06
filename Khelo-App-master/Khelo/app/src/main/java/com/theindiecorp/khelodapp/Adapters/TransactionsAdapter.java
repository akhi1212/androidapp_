package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodapp.Model.Transaction;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Transaction> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public int setItems(ArrayList<Transaction> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView typeTv, pointsTv, dateTv;
        private ImageView iconImg;

        public MyViewHolder(View itemView){
            super(itemView);
            typeTv = itemView.findViewById(R.id.transaction_item_title);
            pointsTv = itemView.findViewById(R.id.transaction_item_points);
            dateTv = itemView.findViewById(R.id.transaction_item_date);
            iconImg = itemView.findViewById(R.id.transaction_item_icon);
        }
    }

    public TransactionsAdapter(Context context, ArrayList<Transaction> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Transaction transaction = dataSet.get(position);

        holder.typeTv.setText(transaction.getType());
        holder.dateTv.setText(transaction.getDate());
        if(transaction.getType().equals("Buy Points")){
            holder.pointsTv.setText("+" + transaction.getPoints() + " Points");
            holder.pointsTv.setTextColor(context.getResources().getColor(R.color.dark_green));
        }
        else if(transaction.getType().equals("Won Quiz")){
            holder.pointsTv.setText("+" + transaction.getPoints() + " Points");
            holder.pointsTv.setTextColor(context.getResources().getColor(R.color.dark_green));
        }
        else if(transaction.getType().equals("Played Quiz")){
            holder.pointsTv.setText("-" + transaction.getPoints() + " Points");
            holder.pointsTv.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if(transaction.getType().equals("Joined Event")){
            holder.pointsTv.setText("-" + transaction.getPoints() + " Points");
            holder.pointsTv.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if(transaction.getType().equals("Points Redeemed")){
            holder.pointsTv.setText("-" + transaction.getPoints() + " Points");
            holder.pointsTv.setTextColor(context.getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
