package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodadmin.R;

import java.util.ArrayList;

public class DetailsSportsAdapter extends RecyclerView.Adapter<DetailsSportsAdapter.MyViewHolder> {

    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> dataSet;

    public void setDetails(ArrayList<String> dataSet){
        this.dataSet = dataSet;
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

    public DetailsSportsAdapter(Context context, ArrayList<String> dataSet){
        this.context = context;
        this.dataSet = dataSet;
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
        String sport = dataSet.get(position);

        holder.editBtn.setVisibility(View.GONE);
        holder.nameTv.setText(sport);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemRemoved(position);
                dataSet.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
