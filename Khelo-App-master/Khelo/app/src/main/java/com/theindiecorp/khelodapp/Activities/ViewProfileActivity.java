package com.theindiecorp.khelodapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Adapters.CareerDetailsAdapter;
import com.theindiecorp.khelodapp.Adapters.MainFeedAdapter;
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.Model.QuestionnaireItem;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ViewProfileActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RecyclerView postsRecycler, careerRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String sport = getIntent().getStringExtra("sport");
        final String userId = getIntent().getStringExtra("user_id");

        final TextView userNameTv = findViewById(R.id.user_name_tv);
        final TextView bioTv = findViewById(R.id.bio_tv);
        final TextView sportsTv = findViewById(R.id.sport_tv);
        final TextView involvedAs = findViewById(R.id.involved_tv);
        final CircularImageView displayPic = findViewById(R.id.profile_photo);
        careerRecycler = findViewById(R.id.career_recycler);
        postsRecycler = findViewById(R.id.profile_posts_recycler);
        final Button careerBtn = findViewById(R.id.career_btn);
        final Button postsBtn = findViewById(R.id.posts_btn);

        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userNameTv.setText(user.getDisplayName());
                bioTv.setText(user.getBio());
                sportsTv.setText(user.getPrimarySport());
                involvedAs.setText(user.getInvolvementAs());
                if(user.getInvolvementAs().equals("Sports Fan")){
                    careerBtn.setVisibility(View.GONE);
                    careerRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postsRecycler.setLayoutManager(new LinearLayoutManager(this));
        final MainFeedAdapter mainFeedAdapter = new MainFeedAdapter(this, new ArrayList<Post>());
        postsRecycler.setAdapter(mainFeedAdapter);

        Query query = databaseReference.child("posts");
        query.orderByChild("hostId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Post> posts = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Post p = snapshot.getValue(Post.class);
                        p.setPostId(snapshot.getKey());
                        posts.add(p);
                    }
                    Collections.reverse(posts);
                    mainFeedAdapter.setPosts(posts);
                    mainFeedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        careerRecycler.setLayoutManager(new LinearLayoutManager(this));
        final CareerDetailsAdapter careerDetailsAdapter = new CareerDetailsAdapter(this, new ArrayList<QuestionnaireItem>());
        careerRecycler.setAdapter(careerDetailsAdapter);

        databaseReference.child("careerDetails").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<QuestionnaireItem> questions = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        QuestionnaireItem item = snapshot.getValue(QuestionnaireItem.class);
                        questions.add(item);
                    }
                    careerDetailsAdapter.setItems(questions);
                    careerDetailsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        careerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                careerBtn.setTextColor(getResources().getColor(R.color.dark_green));
                postsBtn.setTextColor(getResources().getColor(android.R.color.black));
                careerRecycler.setVisibility(View.VISIBLE);
                postsRecycler.setVisibility(View.GONE);
            }
        });

        postsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postsBtn.setTextColor(getResources().getColor(R.color.dark_green));
                careerBtn.setTextColor(getResources().getColor(android.R.color.black));
                postsRecycler.setVisibility(View.VISIBLE);
                careerRecycler.setVisibility(View.GONE);
            }
        });

        final Button likeProfileBtn = findViewById(R.id.follow_btn);

        databaseReference.child("users").child(HomeActivity.userId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child(userId).exists()){
                        likeProfileBtn.setText("Unlike Profile");
                    }
                    else{
                        likeProfileBtn.setText("Like Profile");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        likeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(likeProfileBtn.getText().toString().equals("Like Profile")){
                    databaseReference.child("users").child(HomeActivity.userId).child("following").child(userId).setValue(true);
                    databaseReference.child("users").child(userId).child("followers").child(HomeActivity.userId).setValue(true);
                    addNotification(userId);
                }
                else{
                    databaseReference.child("users").child(HomeActivity.userId).child("following").child(userId).removeValue();
                    databaseReference.child("users").child(userId).child("followers").child(HomeActivity.userId).removeValue();
                }
            }
        });

        final Uri file=Uri.fromFile(new File(""));
        final StorageReference profileImageReference = storage.getReference().child("users/" + userId + "/images/profile_pic/profile_pic.jpeg");
        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                profileImageReference.putFile(file);
                Glide.with(ViewProfileActivity.this)
                        .asBitmap()
                        .apply(new RequestOptions()
                                .override(50,50))
                        .load(uri)
                        .into(displayPic);
                profileImageReference.putFile(file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
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
}