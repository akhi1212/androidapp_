package com.theindiecorp.khelodadmin.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodadmin.R;
import com.theindiecorp.khelodadmin.SportsDetailsActivity;
import com.theindiecorp.khelodadmin.SportsListActivity;

import java.util.ArrayList;

public class SportsDetailsAdapter extends RecyclerView.Adapter<SportsDetailsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dataset;
    private String sport;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public void setItems(ArrayList<String> dataset){
        this.dataset = dataset;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv;
        private ImageButton deleteBtn, editBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_of_sports);
            deleteBtn = itemView.findViewById(R.id.del_btn);
            editBtn = itemView.findViewById(R.id.edit_btn);
        }
    }

    public SportsDetailsAdapter(Context context, ArrayList<String> dataset, String sport){
        this.dataset = dataset;
        this.context = context;
        this.sport = sport;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sports_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String detail = dataset.get(position);

        holder.nameTv.setText(detail);
        holder.editBtn.setVisibility(View.GONE);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setCancelable(true)
                        .setTitle("Delete Sport")
                        .setMessage("Are you sure you want to delete this sport?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("sportDetails");
                                reference.child(sport).child(detail).removeValue();
                                notifyItemRemoved(position);
                                dataset.remove(position);
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
