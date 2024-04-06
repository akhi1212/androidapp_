package com.theindiecorp.khelodapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodapp.Model.Question;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class QuestionPreviewAdapter extends RecyclerView.Adapter<QuestionPreviewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Question> dataSet;
    private String quizId;

    public int setQuestions(ArrayList<Question> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView question, option1, option2, option3, option4;
        private ImageView editBtn, deleteBtn;

        MyViewHolder(View itemView){
            super(itemView);
            this.question = itemView.findViewById(R.id.question);
            this.option1 = itemView.findViewById(R.id.option1);
            this.option2 = itemView.findViewById(R.id.option2);
            this.option3 = itemView.findViewById(R.id.option3);
            this.option4 = itemView.findViewById(R.id.option4);
            this.editBtn = itemView.findViewById(R.id.edit_btn);
            this.deleteBtn = itemView.findViewById(R.id.del_btn);
        }
    }

    public QuestionPreviewAdapter(Context context, ArrayList<Question> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    public QuestionPreviewAdapter(Context context, ArrayList<Question> dataSet, String quizId){
        this.context = context;
        this.dataSet = dataSet;
        this.quizId = quizId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_question_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int listPosition) {

        final Question question = dataSet.get(listPosition);

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

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = onCreateDialog(listPosition);
                dialog.show();
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete Question");
                builder.setMessage("Are you sure you want to delete this question? ");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference("posts").child(quizId)
                                .child("questions").child(question.getId()).removeValue();
                        dataSet.remove(listPosition);
                        notifyItemRemoved(listPosition);
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private Dialog onCreateDialog(final int position){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_quiz_layout);

        final Question question = dataSet.get(position);
        final int[] correctAnswer = {question.getCorrectOption()};

        final CheckBox firstBox, secondBox, thirdBox, fourthBox;

        Toolbar toolbar = dialog.findViewById(R.id.toolbar);
        final EditText questionEt = dialog.findViewById(R.id.question);
        final EditText firstOptionEt = dialog.findViewById(R.id.option1);
        final EditText secondOptionEt = dialog.findViewById(R.id.option2);
        final EditText thirdOptionEt = dialog.findViewById(R.id.option3);
        final EditText fourthOptionEt = dialog.findViewById(R.id.option4);
        firstBox = dialog.findViewById(R.id.option1_box);
        secondBox = dialog.findViewById(R.id.option2_box);
        thirdBox = dialog.findViewById(R.id.option3_box);
        fourthBox = dialog.findViewById(R.id.option4_box);
        Button submitQuestionBtn = dialog.findViewById(R.id.submit_btn);

        questionEt.setText(question.getQuestion());
        firstOptionEt.setText(question.getAnswers().get(0));
        secondOptionEt.setText(question.getAnswers().get(1));
        thirdOptionEt.setText(question.getAnswers().get(2));
        fourthOptionEt.setText(question.getAnswers().get(3));

        switch(question.getCorrectOption()){
            case 1:
                firstBox.setChecked(true);
                break;
            case 2:
                secondBox.setChecked(true);
                break;
            case 3:
                thirdBox.setChecked(true);
                break;
            case 4:
                fourthBox.setChecked(true);
                break;
        }

        firstBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    correctAnswer[0] = 1;
                    question.setCorrectOption(1);
                    secondBox.setChecked(false);
                    thirdBox.setChecked(false);
                    fourthBox.setChecked(false);
                }
            }
        });

        secondBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    correctAnswer[0] = 2;
                    question.setCorrectOption(2);
                    firstBox.setChecked(false);
                    thirdBox.setChecked(false);
                    fourthBox.setChecked(false);
                }
            }
        });

        thirdBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    correctAnswer[0] = 3;
                    question.setCorrectOption(3);
                    secondBox.setChecked(false);
                    firstBox.setChecked(false);
                    fourthBox.setChecked(false);
                }
            }
        });

        fourthBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    correctAnswer[0] = 4;
                    question.setCorrectOption(4);
                    secondBox.setChecked(false);
                    thirdBox.setChecked(false);
                    firstBox.setChecked(false);
                }
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

                if(correctAnswer[0] == 0){
                    Toast.makeText(context, "Select the correct answer", Toast.LENGTH_LONG).show();
                    return;
                }

                ArrayList<String> answers = new ArrayList<>();
                answers.add(firstOptionEt.getText().toString());
                answers.add(secondOptionEt.getText().toString());
                answers.add(thirdOptionEt.getText().toString());
                answers.add(fourthOptionEt.getText().toString());

                question.setQuestion(questionEt.getText().toString());
                question.setAnswers(answers);
                question.setCorrectOption(correctAnswer[0]);

                editQuestion(question);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private void editQuestion(Question question) {
        FirebaseDatabase.getInstance().getReference("posts").child(quizId)
                .child("questions").child(question.getId()).setValue(question);

        notifyDataSetChanged();
    }

}