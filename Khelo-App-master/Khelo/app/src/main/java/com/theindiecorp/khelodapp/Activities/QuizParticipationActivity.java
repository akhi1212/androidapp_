package com.theindiecorp.khelodapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.QuizParticipationAdapter;
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class QuizParticipationActivity extends AppCompatActivity {

    private TextView title;
    private RecyclerView recyclerView;
    private  Button endQuizBtn;

    private ArrayList<Question> questions = new ArrayList<>();
    private int score = 0, winnerPoints = 0, userPoints = 0;
    private String name,userId;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private Boolean wonQuiz = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_participation);

        final String quizId = getIntent().getStringExtra("quizId");
        userId = getIntent().getStringExtra("hostId");

        title = findViewById(R.id.title);
        endQuizBtn = findViewById(R.id.end_btn);
        recyclerView = findViewById(R.id.quiz_participation_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final QuizParticipationAdapter quizParticipationAdapter = new QuizParticipationAdapter(this,new ArrayList<Question>(),quizId);
        recyclerView.setAdapter(quizParticipationAdapter);

        databaseReference.child("posts").child(quizId).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Question question = snapshot.getValue(Question.class);
                        question.setId(snapshot.getKey());
                        questions.add(question);
                    }
                    quizParticipationAdapter.setQuestions(questions);
                    quizParticipationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                title.setText(dataSnapshot.child("eventName").getValue(String.class));
                winnerPoints = Integer.parseInt(dataSnapshot.child("winnerPoints").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getDisplayName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        endQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotification(userId,quizId,title.getText().toString());
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child("submitted").setValue(true);

                for(int i = 0;i<questions.size();i++){
                    databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(questions.get(i).getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.child("correct").getValue(Boolean.class)){
                                    score = score + 1;
                                }
                            }
                            databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child("score").setValue(score);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                Dialog dialog = onCreateDialog();
                dialog.show();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizParticipationActivity.this,HomeActivity.class));
                finish();
            }
        });
    }

    private Dialog onCreateDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView contentTv = dialog.findViewById(R.id.update_tv);
        contentTv.setText("Thank you for playing! \n Winners will be declared in 2-3 Business Days");

        Button continueBtn = dialog.findViewById(R.id.check_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        return dialog;
    }

    private void addNotification(String hostId, String quizId, String quizName) {

        Calendar mCurrentTime = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(mCurrentTime.getTime());

        Notification notification = new Notification();
        notification.setDate(date);
        notification.setId(databaseReference.push().getKey());
        notification.setPostId(quizId);
        notification.setText(name + " played your quiz '" + quizName +"'");
        notification.setType("Quiz");

        databaseReference.child("notifications").child(hostId).child(notification.getId()).setValue(notification);
    }

}
