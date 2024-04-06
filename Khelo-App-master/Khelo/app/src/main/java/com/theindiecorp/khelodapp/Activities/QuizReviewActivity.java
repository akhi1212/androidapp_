package com.theindiecorp.khelodapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Adapters.QuizReviewAdapter;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.R;

import java.io.File;
import java.util.ArrayList;

import static com.theindiecorp.khelodapp.Activities.HomeActivity.userId;

public class QuizReviewActivity extends AppCompatActivity {

    TextView title, score;
    RecyclerView recyclerView;
    CircularImageView profilePic;

    String quizId;
    ArrayList<Question> questions = new ArrayList<>();

    QuizReviewAdapter quizReviewAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_review);

        quizId = getIntent().getStringExtra("quizId");

        title = findViewById(R.id.title);
        profilePic = findViewById(R.id.profile_pic);
        score = findViewById(R.id.score);
        recyclerView = findViewById(R.id.quiz_review_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        score.setVisibility(View.GONE);

        quizReviewAdapter = new QuizReviewAdapter(this,new ArrayList<Question>(),quizId);
        recyclerView.setAdapter(quizReviewAdapter);

        databaseReference.child("posts").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                title.setText(dataSnapshot.child("eventName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(quizId).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Question question = snapshot.getValue(Question.class);
                        question.setId(snapshot.getKey());
                        questions.add(question);
                    }
                    quizReviewAdapter.setQuestions(questions);
                    quizReviewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                score.setText("Your score : " + dataSnapshot.child("score").getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(quizId).child("winnerIds").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Dialog dialog = onCreateDialog();
                    dialog.show();
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    dialog.getWindow().setLayout(width, height);
                    dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizReviewActivity.this,HomeActivity.class));
            }
        });

        final Uri file=Uri.fromFile(new File(""));
        final StorageReference profileImageReference = storage.getReference().child("users/" + userId + "/images/profile_pic/profile_pic.jpeg");
        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                profileImageReference.putFile(file);
                Glide.with(QuizReviewActivity.this)
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
    }

    private Dialog onCreateDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView contentTv = dialog.findViewById(R.id.update_tv);
        contentTv.setText("You Won!!!");

        Button continueBtn = dialog.findViewById(R.id.check_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
