package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theindiecorp.khelodadmin.Data.Question;
import com.theindiecorp.khelodadmin.R;

import java.util.ArrayList;

public class QuestionPreviewAdapter extends RecyclerView.Adapter<QuestionPreviewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Question> dataSet;

    public int setQuestions(ArrayList<Question> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView question, option1, option2, option3, option4;

        MyViewHolder(View itemView){
            super(itemView);
            this.question = itemView.findViewById(R.id.question);
            this.option1 = itemView.findViewById(R.id.option1);
            this.option2 = itemView.findViewById(R.id.option2);
            this.option3 = itemView.findViewById(R.id.option3);
            this.option4 = itemView.findViewById(R.id.option4);
        }
    }

    public QuestionPreviewAdapter(Context context, ArrayList<Question> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_participation_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int listPosition) {

        Question question = dataSet.get(listPosition);

        holder.question.setText(question.getQuestion());
        holder.option1.setText(question.getAnswers().get(0));
        holder.option2.setText(question.getAnswers().get(1));
        holder.option3.setText(question.getAnswers().get(2));
        holder.option4.setText(question.getAnswers().get(3));

        switch (question.getCorrectOption()){
            case 1:
            {
                holder.option1.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            }
            case 2:
            {
                holder.option2.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            }
            case 3:
            {
                holder.option3.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            }
            case 4:
            {
                holder.option4.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
