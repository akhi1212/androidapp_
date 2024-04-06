package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;
import com.theindiecorp.khelodapp.Activities.ViewProfileActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<String> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private double latitude =0d , longitude = 0d;
    Float distance = 0f;

    public int setUserIds(ArrayList<String> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView bioTv,nameTv,sexTv,ageTv,emailTv,phoneTv,likeCountTv, sportsTv;
        private ImageButton followBtn, removeBtn, reportBtn;
        private ImageView imageView;
        private CardView cardView;

        public MyViewHolder(View view){
            super(view);
            bioTv = view.findViewById(R.id.description);
            nameTv = view.findViewById(R.id.name);
            followBtn = view.findViewById(R.id.follow_btn);
            removeBtn = view.findViewById(R.id.next_btn);
            imageView = view.findViewById(R.id.profile_pic);
            sexTv = view.findViewById(R.id.sex);
            ageTv = view.findViewById(R.id.age);
            emailTv = view.findViewById(R.id.email);
            phoneTv = view.findViewById(R.id.phonenumber);
            likeCountTv = view.findViewById(R.id.like_count_tv);
            sportsTv = view.findViewById(R.id.sports);
            reportBtn = view.findViewById(R.id.report_btn);
            cardView = view.findViewById(R.id.card_view);
        }
    }

    public SearchUserAdapter(Context context, ArrayList<String> dataSet){
        this.dataSet = dataSet;
        this.context = context;
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

//                    distance = playerLoc.distanceTo(HomeActivity.userLoc);
                }

                if(TextUtils.isEmpty(holder.sexTv.getText().toString()))
                    holder.sexTv.setVisibility(View.GONE);

                if(u.getBio() != null){
                    if(u.getBio().isEmpty())
                        holder.bioTv.setVisibility(View.GONE);
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

        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("sport", holder.sportsTv.getText().toString());
                context.startActivity(intent);
            }
        });

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("users").child(HomeActivity.userId).child("following").child(userId).removeValue();
                databaseReference.child("users").child(userId).child("followers").child(HomeActivity.userId).removeValue();
                holder.removeBtn.setVisibility(View.GONE);
                holder.followBtn.setVisibility(View.VISIBLE);
                dataSet.remove(listPosition);
                notifyItemRemoved(listPosition);
            }
        });

        holder.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("reportedUsers").child(userId).child(HomeActivity.userId).setValue(true);
                Toast.makeText(context,holder.nameTv.getText().toString() + " has been reported",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addNotification(final String userId) {
        Calendar mCurrentTime = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(mCurrentTime.getTime());
        final String[] name = new String[1];

        final Notification notification = new Notification();
        notification.setType("Liked profile");
        notification.setPostId("");
        notification.setId(databaseReference.push().getKey());
        notification.setDate(date);


        databaseReference.child("users").child(HomeActivity.userId).child("displayName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name[0] = dataSnapshot.getValue(String.class);
                notification.setText(name[0] + " liked your profile");
                databaseReference.child("notifications").child(userId).child(notification.getId()).setValue(notification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
