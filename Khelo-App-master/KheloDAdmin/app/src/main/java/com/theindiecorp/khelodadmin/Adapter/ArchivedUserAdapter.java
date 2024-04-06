package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.ArchivedUserActivity;
import com.theindiecorp.khelodadmin.R;

import java.util.ArrayList;

public class ArchivedUserAdapter extends RecyclerView.Adapter<ArchivedUserAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dataset;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    RvListener rvListener;

    public interface RvListener{
        public void unArchiveUser(String userId, int position);
    }

    public void setUserIds(ArrayList<String> dataset){
        this.dataset = dataset;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv;
        private TextView scoreTv;

        public MyViewHolder(View itemView){
            super(itemView);
            nameTv = itemView.findViewById(R.id.attendee_item_title);
            scoreTv = itemView.findViewById(R.id.points);
        }
    }

    public ArchivedUserAdapter(Context context, ArrayList<String> dataset, RvListener rvListener){
        this.context = context;
        this.dataset = dataset;
        this.rvListener = rvListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.players_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final String userId = dataset.get(position);
        holder.scoreTv.setVisibility(View.GONE);

        databaseReference.child("users").child(userId).child("displayName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.nameTv.setText(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Unarchive user?");
                builder.setMessage("Are you sure you want to unarchive this user?");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rvListener.unArchiveUser(userId, position);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
