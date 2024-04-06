package com.theindiecorp.khelodadmin.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Data.Notification;
import com.theindiecorp.khelodadmin.Data.Transaction;
import com.theindiecorp.khelodadmin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Transaction> dataset;
    private RvListener rvListener;

    public interface RvListener{
        public void updateTransaction(Transaction transaction, String status, int position);
    }

    public void setTransactions(ArrayList<Transaction> dataset){
        this.dataset = dataset;
    }

    public void addTransactions(ArrayList<Transaction> dataset){
        this.dataset.addAll(dataset);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView typeTv, pointsTv, dateTv;
        private ImageView iconImg;
        private ImageButton approveBtn, rejectBtn;

        MyViewHolder(View itemView){
            super(itemView);
            typeTv = itemView.findViewById(R.id.transaction_item_title);
            pointsTv = itemView.findViewById(R.id.transaction_item_points);
            dateTv = itemView.findViewById(R.id.transaction_item_date);
            iconImg = itemView.findViewById(R.id.transaction_item_icon);
            approveBtn = itemView.findViewById(R.id.approve_btn);
            rejectBtn = itemView.findViewById(R.id.reject_btn);
        }
    }

    public TransactionsAdapter(Context context, ArrayList<Transaction> dataSet, RvListener rvListener){
        this.dataset = dataSet;
        this.context = context;
        this.rvListener = rvListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Transaction transaction = dataset.get(position);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(transaction.isPending()){
                    Dialog dialog = onCreateDialog(transaction, position);
                    dialog.show();
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    dialog.getWindow().setLayout(width, height);
                    dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
                }
            }
        });
    }

    private Dialog onCreateDialog(final Transaction transaction, final int position){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.view_transaction_details_dialog);

        TextView typeTv = dialog.findViewById(R.id.transaction_title);
        TextView pointsTv = dialog.findViewById(R.id.transaction_points);
        TextView dateTv = dialog.findViewById(R.id.transaction_date);
        final TextView currentPointsTv = dialog.findViewById(R.id.transaction_current_points);
        TextView pointsRedeemedTv = dialog.findViewById(R.id.transaction_item_amount);
        final TextView paymentMethodTv = dialog.findViewById(R.id.transaction_payment_method);
        final TextView upiIdTv = dialog.findViewById(R.id.transaction_upi_id);
        final TextView bankAccTv = dialog.findViewById(R.id.transaction_acc_no);
        final TextView ifscTv = dialog.findViewById(R.id.transaction_payment_ifsc);
        final TextView recipientNameTv = dialog.findViewById(R.id.transaction_recipient_name);
        final LinearLayout upiLayout = dialog.findViewById(R.id.upi_info);
        final LinearLayout bankLayout = dialog.findViewById(R.id.bank_info);

        typeTv.setText(transaction.getType());
        pointsTv.setText(transaction.getPoints() + "");
        dateTv.setText(transaction.getDate());
        pointsRedeemedTv.setText(transaction.getPoints() + "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("paymentDetails").child(transaction.getUserId()).child(transaction.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String method = dataSnapshot.child("payment_method").getValue(String.class);
                paymentMethodTv.setText(method);
                if(paymentMethodTv.getText().equals("UPI")){
                    bankLayout.setVisibility(View.GONE);
                    upiIdTv.setText(dataSnapshot.child("upiId").getValue(String.class));
                }
                else{
                    bankAccTv.setText(dataSnapshot.child("accountNumber").getValue(String.class));
                    ifscTv.setText(dataSnapshot.child("ifscCode").getValue(String.class));
                    recipientNameTv.setText(dataSnapshot.child("recipientName").getValue(String.class));
                    upiLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("users").child(transaction.getUserId()).child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPointsTv.setText(dataSnapshot.getValue(Integer.class) + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageButton approveBtn = dialog.findViewById(R.id.approve_btn);
        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListener.updateTransaction(transaction, "Approved", position);
                dialog.dismiss();
            }
        });

        ImageButton rejectBtn = dialog.findViewById(R.id.reject_btn);
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListener.updateTransaction(transaction, "Rejected", position);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    void updateTransaction(Transaction transaction, String status){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if(status.equals("Approved")){
            databaseReference.child("transactions").child(transaction.getId()).child("pending").setValue(false);
            sendApproveNotification(transaction.getUserId());
        }
        else{
            databaseReference.child("transactions").child(transaction.getId()).child("pending").setValue(false);
        }

    }

    private void sendApproveNotification(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String id = databaseReference.push().getKey();

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Notification notification = new Notification();
        notification.setId(id);
        notification.setDate(format.format(date));
        notification.setType("Transaction Approved");
        notification.setText("Redeeming amount successful!");

        databaseReference.child("notifications").child(userId).child(id).setValue(notification);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
