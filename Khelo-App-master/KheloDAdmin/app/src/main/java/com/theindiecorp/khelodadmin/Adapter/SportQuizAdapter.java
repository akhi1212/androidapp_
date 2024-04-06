package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodadmin.EditQuestionsActivity;
import com.theindiecorp.khelodadmin.R;
import com.theindiecorp.khelodadmin.SportsDetailsActivity;

import java.util.ArrayList;

public class SportQuizAdapter extends RecyclerView.Adapter<SportQuizAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dataset;

    public void setSports(ArrayList<String> dataset){
        this.dataset = dataset;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv;
        private ImageButton editBtn, deleteBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_of_sports);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.del_btn);
        }
    }

    public SportQuizAdapter(Context context, ArrayList<String> dataset){
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sports_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String sport = dataset.get(position);

        holder.nameTv.setText(sport);
        holder.deleteBtn.setVisibility(View.GONE);

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, EditQuestionsActivity.class).putExtra("sport", sport));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
