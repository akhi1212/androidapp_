package com.theindiecorp.khelodapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Model.QuestionnaireItem;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

import static com.theindiecorp.khelodapp.Activities.HomeActivity.userId;

public class QuestionnaireActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String sport;
    private ArrayList<QuestionnaireItem> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        sport = getIntent().getStringExtra("sport");

        Toast.makeText(this, sport, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final QuestionnaireAdapter adapter = new QuestionnaireAdapter(this, new ArrayList<QuestionnaireItem>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("sportDetails").child(sport).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    questions = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        QuestionnaireItem item = snapshot.getValue(QuestionnaireItem.class);
                        item.setTitle(snapshot.getKey());
                        questions.add(item);
                    }
                    adapter.setQuestions(questions);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button submitBtn = findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!questions.isEmpty()){
                    for(QuestionnaireItem question : questions){
                        if(question.getAnswer() == null){
                            Toast.makeText(QuestionnaireActivity.this, "Please fill in all the answers", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    Toast.makeText(QuestionnaireActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                    databaseReference.child("careerDetails").child(userId).setValue(questions);
                    finish();
                }
            }
        });
    }
}