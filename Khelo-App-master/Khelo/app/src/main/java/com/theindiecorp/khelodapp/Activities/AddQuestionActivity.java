package com.theindiecorp.khelodapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.QuestionPreviewAdapter;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity{

    private String quizId, sport;

    EditText questionEt, firstOptionEt, secondOptionEt, thirdOptionEt, fourthOptionEt;
    Button submitQuestionBtn, endQuizBtn;
    CheckBox firstBox, secondBox, thirdBox, fourthBox;
    RecyclerView questionsRecycler;
    private int correctAnswer = 0;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    QuestionPreviewAdapter questionPreviewAdapter;
    ArrayList<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        quizId = getIntent().getStringExtra("quizId");
        sport = getIntent().getStringExtra("sport");

        questionEt = findViewById(R.id.question);
        firstOptionEt = findViewById(R.id.option1);
        secondOptionEt = findViewById(R.id.option2);
        thirdOptionEt = findViewById(R.id.option3);
        fourthOptionEt = findViewById(R.id.option4);
        firstBox = findViewById(R.id.option1_box);
        secondBox = findViewById(R.id.option2_box);
        thirdBox = findViewById(R.id.option3_box);
        fourthBox = findViewById(R.id.option4_box);
        submitQuestionBtn = findViewById(R.id.submit_btn);
        endQuizBtn = findViewById(R.id.end_btn);

        questionsRecycler = findViewById(R.id.recent_questions_recycler);
        questionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        questionPreviewAdapter = new QuestionPreviewAdapter(this,new ArrayList<Question>(), quizId);
        questionsRecycler.setAdapter(questionPreviewAdapter);

        databaseReference.child("quizQuestions").child(sport).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Question q = snapshot.getValue(Question.class);
                        q.setId(databaseReference.push().getKey());
                        databaseReference.child("posts").child(quizId).child("questions").child(q.getId()).setValue(q);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submitQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(questionEt.getText().toString().contains("(") || questionEt.getText().toString().contains(")") || questionEt.getText().toString().contains("@") || questionEt.getText().toString().contains("#")
                        || questionEt.getText().toString().contains("$") || questionEt.getText().toString().contains("&") || questionEt.getText().toString().contains("-") || questionEt.getText().toString().contains("*")){
                    questionEt.setError("Symbols are not allowed");
                    return;
                }

                if(firstOptionEt.getText().toString().contains("(") || firstOptionEt.getText().toString().contains(")") || firstOptionEt.getText().toString().contains("@") || firstOptionEt.getText().toString().contains("#")
                        || firstOptionEt.getText().toString().contains("$") || firstOptionEt.getText().toString().contains("&") || firstOptionEt.getText().toString().contains("-") || firstOptionEt.getText().toString().contains("*")){
                    firstOptionEt.setError("Symbols are not allowed");
                    return;
                }

                if(secondOptionEt.getText().toString().contains("(") || secondOptionEt.getText().toString().contains(")") || secondOptionEt.getText().toString().contains("@") || secondOptionEt.getText().toString().contains("#")
                        || secondOptionEt.getText().toString().contains("$") || secondOptionEt.getText().toString().contains("&") || secondOptionEt.getText().toString().contains("-") || secondOptionEt.getText().toString().contains("*")){
                    secondOptionEt.setError("Symbols are not allowed");
                    return;
                }

                if(thirdOptionEt.getText().toString().contains("(") || thirdOptionEt.getText().toString().contains(")") || thirdOptionEt.getText().toString().contains("@") || thirdOptionEt.getText().toString().contains("#")
                        || thirdOptionEt.getText().toString().contains("$") || thirdOptionEt.getText().toString().contains("&") || thirdOptionEt.getText().toString().contains("-") || thirdOptionEt.getText().toString().contains("*")){
                    thirdOptionEt.setError("Symbols are not allowed");
                    return;
                }

                if(fourthOptionEt.getText().toString().contains("(") || fourthOptionEt.getText().toString().contains(")") || fourthOptionEt.getText().toString().contains("@") || fourthOptionEt.getText().toString().contains("#")
                        || fourthOptionEt.getText().toString().contains("$") || fourthOptionEt.getText().toString().contains("&") || fourthOptionEt.getText().toString().contains("-") || fourthOptionEt.getText().toString().contains("*")){
                    fourthOptionEt.setError("Symbols are not allowed");
                    return;
                }

                if(TextUtils.isEmpty(questionEt.getText())){
                    questionEt.setError("Enter a question");
                    return;
                }

                if(TextUtils.isEmpty(firstOptionEt.getText())){
                    firstOptionEt.setError("Enter first option");
                    return;
                }

                if(TextUtils.isEmpty(secondOptionEt.getText())){
                    secondOptionEt.setError("Enter second option");
                    return;
                }

                if(TextUtils.isEmpty(thirdOptionEt.getText().toString())){
                    thirdOptionEt.setError("Enter third option");
                    return;
                }

                if(TextUtils.isEmpty(questionEt.getText())){
                    fourthOptionEt.setError("Enter fourth option");
                    return;
                }

                if(correctAnswer == 0){
                    Toast.makeText(AddQuestionActivity.this, "Select the correct answer", Toast.LENGTH_LONG).show();
                    return;
                }

                if(questions.size()>=10){
                    Toast.makeText(AddQuestionActivity.this, "You have reached the maximum number of questions", Toast.LENGTH_LONG).show();
                    return;
                }

                addQuestion(quizId);
            }
        });

        databaseReference.child("posts").child(quizId).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questions = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Question q = snapshot.getValue(Question.class);
                        q.setId(snapshot.getKey());
                        questions.add(q);
                    }
                    questionPreviewAdapter.setQuestions(questions);
                    questionPreviewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        endQuizBtn.setOnClickListener(view -> {
            if(questions.size()>=4){
                databaseReference.child("posts").child(quizId).child("finished").setValue(true);
                startActivity(new Intent(AddQuestionActivity.this,HomeActivity.class));
            }
            else{
                Toast.makeText(AddQuestionActivity.this,"Quiz must contain atleast 4 questions",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addQuestion(String quizId) {
        String questionId = databaseReference.push().getKey();

        Question question = new Question();
        question.setQuestion(questionEt.getText().toString());
        ArrayList<String> answers = new ArrayList<>();
        answers.add(firstOptionEt.getText().toString());
        answers.add(secondOptionEt.getText().toString());
        answers.add(thirdOptionEt.getText().toString());
        answers.add(fourthOptionEt.getText().toString());
        question.setAnswers(answers);
        question.setCorrectOption(correctAnswer);

        databaseReference.child("posts").child(quizId).child("questions").child(questionId).setValue(question);

        Toast.makeText(AddQuestionActivity.this, "Question Added", Toast.LENGTH_LONG).show();

        questionEt.setText("");
        firstOptionEt.setText("");
        secondOptionEt.setText("");
        thirdOptionEt.setText("");
        fourthOptionEt.setText("");

        firstBox.setChecked(false);
        secondBox.setChecked(false);
        thirdBox.setChecked(false);
        fourthBox.setChecked(false);

        correctAnswer = 0;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()){
            case R.id.option1_box : {
                if(checked){
                    correctAnswer = 1;
                    secondBox.setChecked(false);
                    thirdBox.setChecked(false);
                    fourthBox.setChecked(false);
                    break;
                }
            }
            case R.id.option2_box : {
                if(checked){
                    correctAnswer = 2;
                    firstBox.setChecked(false);
                    thirdBox.setChecked(false);
                    fourthBox.setChecked(false);
                    break;
                }
            }
            case R.id.option3_box : {
                if(checked){
                    correctAnswer = 3;
                    firstBox.setChecked(false);
                    secondBox.setChecked(false);
                    fourthBox.setChecked(false);
                    break;
                }
            }
            case R.id.option4_box : {
                if(checked){
                    correctAnswer = 4;
                    firstBox.setChecked(false);
                    secondBox.setChecked(false);
                    thirdBox.setChecked(false);
                    break;
                }
            }
        }
    }
}