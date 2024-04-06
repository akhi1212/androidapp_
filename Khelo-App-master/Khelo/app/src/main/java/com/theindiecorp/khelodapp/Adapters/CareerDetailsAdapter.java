package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theindiecorp.khelodapp.Model.QuestionnaireItem;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class CareerDetailsAdapter extends RecyclerView.Adapter<CareerDetailsAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<QuestionnaireItem> dataSet;

    public void setItems(ArrayList<QuestionnaireItem> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView questionTv, answerTv;

        public MyViewHolder(View itemView){
            super(itemView);
            questionTv = itemView.findViewById(R.id.question);
            answerTv = itemView.findViewById(R.id.answer);
        }
    }

    public CareerDetailsAdapter(Context context, ArrayList<QuestionnaireItem> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.career_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QuestionnaireItem item = dataSet.get(position);
        holder.answerTv.setText(item.getAnswer());
        holder.questionTv.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
