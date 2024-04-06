package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theindiecorp.khelodadmin.Data.User;
import com.theindiecorp.khelodadmin.R;
import com.theindiecorp.khelodadmin.TransactionActivity;

import java.io.File;
import java.util.ArrayList;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<String> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private double latitude =0d , longitude = 0d;
    private RvListener rvListener;

    public interface RvListener{
        public void deleteUser(String userId, int pos);
        public void undeleteUser(String userId, int pos);
    }

    public int setUserIds(ArrayList<String> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView bioTv,nameTv,sexTv,ageTv,emailTv,phoneTv,likeCountTv, sportsTv;
        private ImageView imageView;
        private Button deleteBtn, transactionsButton;

        public MyViewHolder(View view){
            super(view);
            bioTv = view.findViewById(R.id.description);
            nameTv = view.findViewById(R.id.name);
            imageView = view.findViewById(R.id.profile_pic);
            sexTv = view.findViewById(R.id.sex);
            ageTv = view.findViewById(R.id.age);
            emailTv = view.findViewById(R.id.email);
            phoneTv = view.findViewById(R.id.phonenumber);
            likeCountTv = view.findViewById(R.id.like_count_tv);
            sportsTv = view.findViewById(R.id.sports);
            deleteBtn = view.findViewById(R.id.delete_btn);
            transactionsButton = view.findViewById(R.id.view_transactions);
        }
    }

    public SearchUserAdapter(Context context, ArrayList<String> dataSet, RvListener rvListener){
        this.dataSet = dataSet;
        this.context = context;
        this.rvListener = rvListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_user_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int listPosition) {

        final String userId = dataSet.get(listPosition);
        final ArrayList<String> followers = new ArrayList<>();

        final Uri file=Uri.fromFile(new File(""));
        final StorageReference profileImageReference = storage.getReference().child("users/" + userId + "/images/profile_pic/profile_pic.jpeg");
        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                profileImageReference.putFile(file);
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .apply(new RequestOptions()
                                .override(50,50))
                        .load(uri)
                        .into(holder.imageView);
                profileImageReference.putFile(file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User u = dataSnapshot.getValue(User.class);
                    holder.nameTv.setText(u.getDisplayName());
                    holder.bioTv.setText(u.getBio());
                    holder.ageTv.setText(u.getAge() + " years old");
                    holder.sexTv.setText(dataSnapshot.child("Gender").getValue(String.class));
                    holder.emailTv.setText(u.getEmail());
                    holder.phoneTv.setText(u.getPhoneNumber());
                    holder.sportsTv.setText(u.getPrimarySport());

                    if(dataSnapshot.child("latitude").exists()){
                        latitude = dataSnapshot.child("latitude").getValue(Double.class);
                        longitude = dataSnapshot.child("longitude").getValue(Double.class);
                        Location playerLoc = new Location("");
                        playerLoc.setLongitude(longitude);
                        playerLoc.setLatitude(latitude);
                    }

                    if(TextUtils.isEmpty(holder.sexTv.getText().toString()))
                        holder.sexTv.setVisibility(View.GONE);

                    if(u.getBio() != null){
                        if(u.getBio().isEmpty())
                            holder.bioTv.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(userId).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        followers.add(snapshot.getKey());
                    }
                }
                holder.likeCountTv.setText(followers.size() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListener.deleteUser(userId, listPosition);
            }
        });

        holder.transactionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TransactionActivity.class);
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
