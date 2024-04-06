package com.theindiecorp.khelodapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Adapters.MainFeedAdapter;
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.Activities.QuestionnaireActivity;
import com.theindiecorp.khelodapp.R;
import com.theindiecorp.khelodapp.Activities.UserDataActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.theindiecorp.khelodapp.Activities.HomeActivity.userId;

public class ProfileFragment extends Fragment {
    Button editProfileBtn;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String bio,phone;
    private TextView usernameTv,displayNameTv,bioTv,phoneNoTv;
    DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CircularImageView profilePic;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        displayNameTv = view.findViewById(R.id.profile_display_name_tv);
        editProfileBtn = view.findViewById(R.id.profile_edit_porfile_btn);
        bioTv = view.findViewById(R.id.profile_description_tv);
        usernameTv = view.findViewById(R.id.username);
        phoneNoTv = view.findViewById(R.id.profile_phone_tv);
        profilePic = view.findViewById(R.id.profile_photo);
        final Button careerDetailBtn = view.findViewById(R.id.profile_career_btn);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final MainFeedAdapter mainFeedAdapter = new MainFeedAdapter(getContext(), new ArrayList<Post>());
        mRecyclerView.setAdapter(mainFeedAdapter);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity(), UserDataActivity.class));
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                displayNameTv.setText(u.getDisplayName());
                if(u.getUsername() != null){
                    usernameTv.setText(u.getDisplayName());
                }
                if(u.getBio() != null){
                    bioTv.setText(u.getBio());
                }
                if(u.getPhoneNumber() != null){
                    phoneNoTv.setText(u.getPhoneNumber());
                }
                if(!u.getInvolvementAs().equals("Player")){
                    careerDetailBtn.setVisibility(View.GONE);
                }
                else{
                    HomeActivity.involvedAs = u.getInvolvementAs();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference("posts");
        query.orderByChild("hostId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Post> posts = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Post p = eventSnapshot.getValue(Post.class);
                    p.setPostId(eventSnapshot.getKey());
                    posts.add(p);
                }
                Collections.reverse(posts);
                mainFeedAdapter.setPosts(posts);
                mainFeedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Uri file=Uri.fromFile(new File(""));
        final StorageReference profileImageReference = storage.getReference().child("users/" + userId + "/images/profile_pic/profile_pic.jpeg");
        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(getActivity() == null){
                    return;
                }
                profileImageReference.putFile(file);
                Glide.with(getContext())
                        .asBitmap()
                        .apply(new RequestOptions()
                                .override(50,50))
                        .load(uri)
                        .into(profilePic);
                profileImageReference.putFile(file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        careerDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(HomeActivity.userId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //Toast.makeText(HomeActivity.this, dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), QuestionnaireActivity.class);
                                intent.putExtra("sport", dataSnapshot.child("primarySport").getValue(String.class));
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        return view;

    }
}
