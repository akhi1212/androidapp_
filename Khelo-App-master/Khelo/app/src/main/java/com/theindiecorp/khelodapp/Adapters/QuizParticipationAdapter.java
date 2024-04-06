package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class QuizParticipationAdapter extends RecyclerView.Adapter<QuizParticipationAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Question> dataSet;
    private String quizId;
    private Boolean correct = false;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private int selectedOption = 0;

    public int setQuestions(ArrayList<Question> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView question, option1, option2 ,option3, option4;

        public MyViewHolder(View itemView){
            super(itemView);
            question = itemView.findViewById(R.id.question);
            option1 = itemView.findViewById(R.id.option1);
            option2 = itemView.findViewById(R.id.option2);
            option3 = itemView.findViewById(R.id.option3);
            option4 = itemView.findViewById(R.id.option4);
        }
    }

    public QuizParticipationAdapter(Context context, ArrayList<Question> dataSet, String quizId){
        this.context = context;
        this.dataSet = dataSet;
        this.quizId = quizId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_participation_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int listPosition) {

        final Question question = dataSet.get(listPosition);

        holder.question.setText(question.getQuestion());
        holder.option1.setText(question.getAnswers().get(0));
        holder.option2.setText(question.getAnswers().get(1));
        holder.option3.setText(question.getAnswers().get(2));
        holder.option4.setText(question.getAnswers().get(3));

        holder.option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedOption = 1;

                holder.option1.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.option2.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option3.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option4.setBackgroundColor(context.getResources().getColor(android.R.color.white));

                if(selectedOption == question.getCorrectOption()){
                    correct = true;
                }
                else{
                    correct = false;
                }

                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("selectedOption").setValue(selectedOption);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correct").setValue(correct);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correctOption").setValue(question.getCorrectOption());
            }
        });

        holder.option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption = 2;

                if(selectedOption == question.getCorrectOption()){
                    correct = true;
                }
                else
                    correct = false;

                holder.option2.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.option1.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option3.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option4.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("selectedOption").setValue(selectedOption);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correct").setValue(correct);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correctOption").setValue(question.getCorrectOption());
            }
        });

        holder.option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption = 3;

                if(selectedOption == question.getCorrectOption()){
                    correct = true;
                }
                else
                    correct = false;

                holder.option3.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.option1.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option2.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option4.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("selectedOption").setValue(selectedOption);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correct").setValue(correct);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correctOption").setValue(question.getCorrectOption());
            }
        });

        holder.option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption = 4;

                if(selectedOption == question.getCorrectOption()){
                    correct = true;
                }
                else
                    correct = false;

                holder.option4.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.option1.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option3.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                holder.option2.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("selectedOption").setValue(selectedOption);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correct").setValue(correct);
                databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).child("correctOption").setValue(question.getCorrectOption());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}