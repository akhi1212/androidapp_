package com.theindiecorp.khelodapp.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Notification> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public int setNotifications(ArrayList<Notification> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView title, message;
        private ImageView icon;

        MyViewHolder(View itemView){
            super(itemView);
            this.title = itemView.findViewById(R.id.notification_type);
            this.message = itemView.findViewById(R.id.notification_message);
            this.icon = itemView.findViewById(R.id.notification_type_icon);
        }
    }

    public NotificationAdapter(Context context, ArrayList<Notification> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int listPosition) {

        Notification notification = dataSet.get(listPosition);

        holder.title.setText(notification.getType());
        holder.message.setText(notification.getText());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
