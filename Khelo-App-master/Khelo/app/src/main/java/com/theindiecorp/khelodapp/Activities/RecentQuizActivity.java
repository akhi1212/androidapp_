package com.theindiecorp.khelodapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.PlayersAdapter;
import com.theindiecorp.khelodapp.Adapters.QuestionPreviewAdapter;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class RecentQuizActivity extends AppCompatActivity {

    Button showQuestionBtn, showPlayerBtn;
    RecyclerView questionRecycler, playerRecycler;
    TextView titleTv;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    QuestionPreviewAdapter questionPreviewAdapter;
    PlayersAdapter playersAdapter;

    String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_quiz);

        quizId = getIntent().getStringExtra("quizId");

        titleTv = findViewById(R.id.title);
        showQuestionBtn = findViewById(R.id.show_questions_btn);
        showPlayerBtn = findViewById(R.id.show_players_btn);
        questionRecycler = findViewById(R.id.recent_quiz_question_recycler);
        playerRecycler = findViewById(R.id.recent_quiz_player_recycler);

        questionRecycler.setLayoutManager(new LinearLayoutManager(this));
        playerRecycler.setLayoutManager(new LinearLayoutManager(this));

        questionPreviewAdapter = new QuestionPreviewAdapter(this,new ArrayList<Question>());
        questionRecycler.setAdapter(questionPreviewAdapter);

        playersAdapter = new PlayersAdapter(this,new ArrayList<String>(), quizId);
        playerRecycler.setAdapter(playersAdapter);

        databaseReference.child("posts").child(quizId).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Question> questions = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Question question = snapshot.getValue(Question.class);
                    question.setId(snapshot.getKey());
                    questions.add(question);
                }
                questionPreviewAdapter.setQuestions(questions);
                questionPreviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("leaderboards").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userIds = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if(snapshot.child("submitted").exists()){
                            userIds.add(snapshot.getKey());
                        }
                    }
                    playersAdapter.setPlayers(userIds);
                    playersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titleTv.setText(dataSnapshot.child("eventName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        showQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuestionBtn.setTextColor(getResources().getColor(android.R.color.white));
                showPlayerBtn.setTextColor(getResources().getColor(android.R.color.black));
                playerRecycler.setVisibility(View.GONE);
                questionRecycler.setVisibility(View.VISIBLE);
            }
        });

        showPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerBtn.setTextColor(getResources().getColor(android.R.color.white));
                showQuestionBtn.setTextColor(getResources().getColor(android.R.color.black));
                playerRecycler.setVisibility(View.VISIBLE);
                questionRecycler.setVisibility(View.GONE);
            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecentQuizActivity.this,HomeActivity.class));
            }
        });

    }
}
