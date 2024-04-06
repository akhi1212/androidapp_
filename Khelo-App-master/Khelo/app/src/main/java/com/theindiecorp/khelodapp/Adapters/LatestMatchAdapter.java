package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theindiecorp.khelodapp.Model.Match;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class LatestMatchAdapter extends RecyclerView.Adapter<LatestMatchAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Match> dataSet;
    private MatchListener matchListener;

    public interface MatchListener{
        public void showMatchDetails(long uniqueId, String type);
    }

    public void setMatches(ArrayList<Match> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView teamOneTv, teamTwoTv, tossTv, matchTypeTv;

        public  MyViewHolder(View itemView){
            super(itemView);
            teamOneTv = itemView.findViewById(R.id.team_one_tv);
            teamTwoTv = itemView.findViewById(R.id.team_two_tv);
            tossTv = itemView.findViewById(R.id.toss_tv);
            matchTypeTv = itemView.findViewById(R.id.match_type_tv);
        }
    }

    public LatestMatchAdapter(Context context, ArrayList<Match> dataSet, MatchListener matchListener){
        this.dataSet = dataSet;
        this.context = context;
        this.matchListener = matchListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Match match = dataSet.get(position);

        holder.matchTypeTv.setText(match.getType());
        holder.teamOneTv.setText(match.getTeam1());
        holder.teamTwoTv.setText(match.getTeam2());
        holder.tossTv.setText(match.getTossWinnerTeam() + " won the toss");

        if(match.getTossWinnerTeam() == null)
            holder.tossTv.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchListener.showMatchDetails(match.getUniqueId(), match.getType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
