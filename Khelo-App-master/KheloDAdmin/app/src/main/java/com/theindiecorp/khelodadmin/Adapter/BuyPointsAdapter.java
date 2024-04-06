package com.theindiecorp.khelodadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Data.BuyPointsObject;
import com.theindiecorp.khelodadmin.R;

import java.util.ArrayList;

public class BuyPointsAdapter extends RecyclerView.Adapter<BuyPointsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<BuyPointsObject> dataset;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public void setItems(ArrayList<BuyPointsObject> dataset){
        this.dataset = dataset;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView originalCostTv, pointsValTv;
        private TextInputEditText discountedPriceText;
        private Button submitBtn;

        MyViewHolder(View itemView){
            super(itemView);
            originalCostTv = itemView.findViewById(R.id.points_cost_tv);
            pointsValTv = itemView.findViewById(R.id.points_value_tv);
            discountedPriceText = itemView.findViewById(R.id.discounted_price_text);
            submitBtn = itemView.findViewById(R.id.submit_btn);
        }
    }

    public BuyPointsAdapter(Context context, ArrayList<BuyPointsObject> dataset){
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.buy_points_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final BuyPointsObject object = dataset.get(position);
        holder.originalCostTv.setText("Original Cost : INR." + object.getCost());
        holder.pointsValTv.setText(object.getCost() + " Points");

        holder.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("buyMorePoints").child(object.getId()).child("discountedPrice").setValue(holder.discountedPriceText.getText().toString());
                Snackbar snackbar = Snackbar.make(holder.itemView, "Discounted price updated for this item", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        databaseReference.child("buyMorePoints").child(object.getId()).child("discountedPrice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.discountedPriceText.setText(dataSnapshot.getValue(String.class));
                }
                else
                    holder.discountedPriceText.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
