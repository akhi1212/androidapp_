package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class QuizReviewAdapter extends RecyclerView.Adapter<QuizReviewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Question> dataSet;
    private String quizId;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private int selectedOption = 0;
    private int correctOption = 0;

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

    public QuizReviewAdapter(Context context, ArrayList<Question> dataSet, String quizId){
        this.context = context;
        this.quizId = quizId;
        this.dataSet = dataSet;
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

        databaseReference.child("leaderboards").child(quizId).child(HomeActivity.userId).child(question.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    correctOption = dataSnapshot.child("correctOption").getValue(Integer.class);
                    selectedOption = dataSnapshot.child("selectedOption").getValue(Integer.class);

                    switch(selectedOption){
                        case 1:
                            holder.option1.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                            break;
                        case 2:
                            holder.option2.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                            break;
                        case 3:
                            holder.option3.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                            break;
                        case 4:
                            holder.option4.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                            break;
                    }

//                    switch(correctOption){
//                        case 1:
//                            holder.option1.setBackgroundColor(context.getResources().getColor(R.color.green));
//                            break;
//                        case 2:
//                            holder.option2.setBackgroundColor(context.getResources().getColor(R.color.green));
//                            break;
//                        case 3:
//                            holder.option3.setBackgroundColor(context.getResources().getColor(R.color.green));
//                            break;
//                        case 4:
//                            holder.option4.setBackgroundColor(context.getResources().getColor(R.color.green));
//                            break;
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}