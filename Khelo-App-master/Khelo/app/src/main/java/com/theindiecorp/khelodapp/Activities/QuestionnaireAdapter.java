package com.theindiecorp.khelodapp.Activities;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theindiecorp.khelodapp.Adapters.SportsSpinnerAdapter;
import com.theindiecorp.khelodapp.Model.QuestionnaireItem;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<QuestionnaireItem> dataSet;

    public void setQuestions(ArrayList<QuestionnaireItem> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView questionTv;
        private Spinner optionsSpinner;
        private EditText answerEt;

        public MyViewHolder(View itemView){
            super(itemView);
            questionTv = itemView.findViewById(R.id.question);
            optionsSpinner = itemView.findViewById(R.id.spinner);
            answerEt = itemView.findViewById(R.id.answer);
        }
    }

    public QuestionnaireAdapter(Context context, ArrayList<QuestionnaireItem> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questionnaire_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        QuestionnaireItem question = dataSet.get(position);
        final String[] answer = {""};

        holder.questionTv.setText(question.getTitle());

        if(question.getType().equals("Text field")){
            holder.optionsSpinner.setVisibility(View.GONE);
            holder.answerEt.setVisibility(View.VISIBLE);
        }
        else{
            holder.optionsSpinner.setVisibility(View.VISIBLE);
            holder.answerEt.setVisibility(View.GONE);

            SportsSpinnerAdapter adapter = new SportsSpinnerAdapter(context, question.getOptions());
            holder.optionsSpinner.setAdapter(adapter);
        }

        holder.optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                answer[0] = adapterView.getItemAtPosition(i).toString();
                dataSet.get(position).setAnswer(answer[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.answerEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataSet.get(position).setAnswer(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
