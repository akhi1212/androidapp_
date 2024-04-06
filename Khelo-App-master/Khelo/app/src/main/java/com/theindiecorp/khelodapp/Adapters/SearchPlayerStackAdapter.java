package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

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
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;
import com.theindiecorp.khelodapp.Activities.ViewProfileActivity;

import java.io.File;
import java.util.ArrayList;

public class SearchPlayerStackAdapter extends ArrayAdapter {

    private ArrayList<String> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private double latitude =0d , longitude = 0d;
    private Context context;

    public void setUserIds(ArrayList<String> dataSet){
        this.dataSet = dataSet;
    }

    public SearchPlayerStackAdapter(Context context, ArrayList<String> dataSet){
        super(context, R.layout.search_user_item);
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_item, parent,false);
        }

        final TextView bioTv = convertView.findViewById(R.id.description);
        final TextView nameTv = convertView.findViewById(R.id.name);
        final Button followBtn = convertView.findViewById(R.id.follow_btn);
        final Button removeBtn = convertView.findViewById(R.id.next_btn);
        final ImageView imageView = convertView.findViewById(R.id.profile_pic);
        final TextView sexTv = convertView.findViewById(R.id.sex);
        final TextView ageTv = convertView.findViewById(R.id.age);
        final TextView emailTv = convertView.findViewById(R.id.email);
        final TextView phoneTv = convertView.findViewById(R.id.phonenumber);
        final TextView likeCountTv = convertView.findViewById(R.id.like_count_tv);
        final TextView sportsTv = convertView.findViewById(R.id.sports);
        Button reportBtn = convertView.findViewById(R.id.report_btn);
        CardView cardView = convertView.findViewById(R.id.card_view);

        final String userId = dataSet.get(position);
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
                        .into(imageView);
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
                nameTv.setText(u.getDisplayName());
                bioTv.setText(u.getBio());
                ageTv.setText(u.getAge() + " years old");
                sexTv.setText(dataSnapshot.child("Gender").getValue(String.class));
                emailTv.setText(u.getEmail());
                phoneTv.setText(u.getPhoneNumber());
                sportsTv.setText(u.getPrimarySport());

                if(dataSnapshot.child("latitude").exists()){
                    latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    Location playerLoc = new Location("");
                    playerLoc.setLongitude(longitude);
                    playerLoc.setLatitude(latitude);

//                    distance = playerLoc.distanceTo(HomeActivity.userLoc);
                }

                if(TextUtils.isEmpty(sexTv.getText().toString()))
                    sexTv.setVisibility(View.GONE);

                if(u.getBio() != null){
                    if(u.getBio().isEmpty())
                        bioTv.setVisibility(View.GONE);
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
                likeCountTv.setText(followers.size() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("sport", sportsTv.getText().toString());
                context.startActivity(intent);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("users").child(HomeActivity.userId).child("following").child(userId).removeValue();
                databaseReference.child("users").child(userId).child("followers").child(HomeActivity.userId).removeValue();
                removeBtn.setVisibility(View.GONE);
                followBtn.setVisibility(View.VISIBLE);
                dataSet.remove(position);
            }
        });


        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("reportedUsers").child(userId).child(HomeActivity.userId).setValue(true);
                Toast.makeText(context,nameTv.getText().toString() + " has been reported",Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
}
