package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theindiecorp.khelodadmin.Data.Post;

public class EditArticleActivity extends AppCompatActivity {

    private String postId;
    private Post post, updatedPost;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);

        postId = getIntent().getStringExtra("postId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView descriptionTv = findViewById(R.id.article_text);
        final TextView referenceUrl = findViewById(R.id.article_url);
        final ImageView articleImg = findViewById(R.id.new_article_main_image);

        databaseReference.child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                descriptionTv.setText(post.getDescription());
                referenceUrl.setText(post.getReferenceLink());

                if(post.getImgUrl() != null) {
                    StorageReference imageReference = storage.getReference().child(post.getImgUrl());
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(EditArticleActivity.this)
                                    .load(uri)
                                    .into(articleImg);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(MainActivity.TAG, exception.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(descriptionTv.getText().toString())){
                    return;
                }

                databaseReference.child("posts").child(postId).child("description").setValue(descriptionTv.getText().toString());
                databaseReference.child("posts").child(postId).child("referenceLink").setValue(referenceUrl.getText().toString());

                finish();
            }
        });

    }
}