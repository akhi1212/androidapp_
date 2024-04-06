package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Adapter.PlayersAdapter;
import com.theindiecorp.khelodadmin.Adapter.QuestionPreviewAdapter;
import com.theindiecorp.khelodadmin.Data.Notification;
import com.theindiecorp.khelodadmin.Data.Question;
import com.theindiecorp.khelodadmin.Data.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QuizViewActivity extends AppCompatActivity implements PlayersAdapter.RvListener{

    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<Question> questions = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String quizId, hostId;
    private int winnerPoints, hostPoints, playerPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_view);

        quizId = getIntent().getStringExtra("quizId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseReference.child("posts").child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    winnerPoints = Integer.parseInt(dataSnapshot.child("winnerPoints").getValue(String.class));
//                    hostId = dataSnapshot.child("hostId").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final RecyclerView playersList = findViewById(R.id.recent_quiz_player_recycler);
        playersList.setLayoutManager(new LinearLayoutManager(this));

        final PlayersAdapter playersAdapter = new PlayersAdapter(this, new ArrayList<String>(), quizId, this);
        playersList.setAdapter(playersAdapter);

        databaseReference.child("leaderboards").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIds = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        userIds.add(snapshot.getKey());
                    }
                    playersAdapter.setPlayers(userIds);
                    playersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final RecyclerView questionsList = findViewById(R.id.recent_quiz_question_recycler);
        questionsList.setLayoutManager(new LinearLayoutManager(this));

        final QuestionPreviewAdapter questionPreviewAdapter = new QuestionPreviewAdapter(this, new ArrayList<Question>());
        questionsList.setAdapter(questionPreviewAdapter);

        databaseReference.child("posts").child(quizId).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    questions = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Question question = snapshot.getValue(Question.class);
                        question.setId(snapshot.getKey());
                        questions.add(question);
                    }
                    questionPreviewAdapter.setQuestions(questions);
                    questionPreviewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Button questionBtn = findViewById(R.id.show_questions_btn);
        final Button playersBtn = findViewById(R.id.show_players_btn);

        questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionsList.setVisibility(View.VISIBLE);
                playersList.setVisibility(View.GONE);
                questionBtn.setTextColor(getResources().getColor(android.R.color.white));
                playersBtn.setTextColor(getResources().getColor(android.R.color.black));
            }
        });

        playersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playersList.setVisibility(View.VISIBLE);
                questionsList.setVisibility(View.GONE);
                playersBtn.setTextColor(getResources().getColor(android.R.color.white));
                questionBtn.setTextColor(getResources().getColor(android.R.color.black));
            }
        });


    }

    @Override
    public void pickWinner(final String playerId, final String displayName) {
        final int[] found = {0};
        databaseReference.child("users").child(playerId).child("archived").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(QuizViewActivity.this, "This user is archived", Toast.LENGTH_SHORT).show();
                    found[0] = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(found[0] == 1) return;

        found[0] = 0;

        databaseReference.child("posts").child(quizId).child("winners").child(playerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(QuizViewActivity.this, "This user is already a winner", Toast.LENGTH_SHORT).show();
                    found[0] = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(found[0] == 1) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Winner of the Quiz");
        builder.setMessage("Are you sure you want to pick this player as the winner?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("posts").child(quizId).child("winners").child(playerId).setValue(true);
                sendNotification(playerId, quizId, displayName);
                sendPrizeMoney(quizId, playerId);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendPrizeMoney(final String quizId, final String playerId) {
        databaseReference.child("users").child(playerId).child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    playerPoints = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        int newPlayerPoints = winnerPoints +  playerPoints;
        databaseReference.child("users").child(playerId).child("points").setValue(newPlayerPoints);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Transaction transaction = new Transaction("Quiz Prize Money", winnerPoints, format.format(date));
        transaction.setPostId(quizId);
        databaseReference.child("transactions").child(playerId).child(quizId).setValue(transaction);
    }

    private void sendNotification(String userId, String quizId, String username) {
        Calendar mCurrentTime = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(mCurrentTime.getTime());

        String id = databaseReference.push().getKey();

        Notification notification = new Notification();
        notification.setId(id);
        notification.setPostId(quizId);
        notification.setText("You won the quiz!!");
        notification.setType("Won quiz");
        notification.setDate(date);

        databaseReference.child("notifications").child(userId).child(id).setValue(notification);

    }
}
