package com.theindiecorp.khelodapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.Model.Transaction;
import com.theindiecorp.khelodapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuizView extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    int userPoints, fees, differences, hostPoints, playerCount;

    private ArrayList<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_view);

        final String quizId = getIntent().getStringExtra("quizId");
        final String hostId = getIntent().getStringExtra("hostId");

        final TextView quizName = findViewById(R.id.quiz_view_event_name);
        final TextView feesTv = findViewById(R.id.quiz_view_fees_points);
        final TextView winnerPointsTv = findViewById(R.id.quiz_view_winner_points);
        final TextView hostName = findViewById(R.id.quiz_view_host_by_tv);
        final TextView titleTv = findViewById(R.id.event_view_title);
        final TextView numberOfPlayers = findViewById(R.id.quiz_view_people_count);
        final ImageView imageView = findViewById(R.id.new_event_main_image);
        final Button playQuizBtn = findViewById(R.id.quiz_view_join_btn);

        databaseReference.child("posts").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                quizName.setText(post.getEventName());
                feesTv.setText(post.getFees() + " Points");
                playerCount = post.getMaxNumberOfParticipants();

                if(dataSnapshot.child("winnerPoints").exists())
                    winnerPointsTv.setText(dataSnapshot.child("winnerPoints").getValue(String.class) + " Points");

                if(post.getFees()!=null)
                    if(!post.getFees().isEmpty())
                        fees = Integer.parseInt(post.getFees());

                if(post.getImgUrl() != null){
                    StorageReference imageReference = storage.getReference().child(post.getImgUrl());
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(QuizView.this)
                                    .load(uri)
                                    .into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(HomeActivity.TAG, exception.getMessage());
                        }
                    });
                }
                else
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.quiz_placeholder_img));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(hostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hostName.setText(dataSnapshot.child("displayName").getValue(String.class));
                titleTv.setText(dataSnapshot.child("displayName").getValue(String.class));
                hostPoints = dataSnapshot.child("points").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userPoints = dataSnapshot.child("points").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("leaderboards").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                players = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        players.add(snapshot.getKey());
                    }
                }

                if(players.size() == playerCount){
                    playQuizBtn.setText("Quiz Expired");
                    playQuizBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    playQuizBtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        playQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                differences = userPoints - fees;
                if(differences<0){
                    Toast.makeText(QuizView.this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child("submitted").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(QuizView.this);
                            builder1.setMessage("Are you sure want to pay " + fees + " points to play this quiz? ");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            databaseReference.child("users").child(hostId).child("points").setValue(hostPoints + fees);
                                            databaseReference.child("users").child(HomeActivity.userId).child("points").setValue(differences);
                                            Date date = new Date();
                                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                            Transaction transaction = new Transaction("Played Quiz",fees,format.format(date));
                                            transaction.setPostId(quizId);
                                            transaction.setUserId(HomeActivity.userId);
                                            databaseReference.child("transactions").child(databaseReference.push().getKey()).setValue(transaction);
                                            dialog.cancel();
                                            startActivity(new Intent(QuizView.this, QuizParticipationActivity.class).putExtra("quizId",quizId).putExtra("hostId",hostId));
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                        else{
                            startActivity(new Intent(QuizView.this, QuizReviewActivity.class).putExtra("quizId",quizId));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
