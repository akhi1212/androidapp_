package com.theindiecorp.khelodapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Model.BuyPointsObject;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class BuyPointsItemAdapter extends RecyclerView.Adapter<BuyPointsItemAdapter.MyViewHolder>{

    private ArrayList<BuyPointsObject> dataSet;
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private BuyPointsListner listner;

    public static String buyPoints;

    int points = 0;

    public int setItems(ArrayList<BuyPointsObject> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public interface BuyPointsListner{
        public void startPayment(Double amount, int points, String orderId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView pointsTv, originalCostTv, discountedCostTv;
        private Button buyBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            pointsTv = itemView.findViewById(R.id.points_text);
            buyBtn = itemView.findViewById(R.id.buy_btn);
            originalCostTv = itemView.findViewById(R.id.original_cost_text);
            discountedCostTv = itemView.findViewById(R.id.discounted_cost_text);
        }
    }

    public BuyPointsItemAdapter(Context context, ArrayList<BuyPointsObject> dataSet, BuyPointsListner listner){
        this.context = context;
        this.dataSet = dataSet;
        this.listner = listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_points_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String cost = dataSet.get(position).getCost();
        final BuyPointsObject object = dataSet.get(position);

        databaseReference.child("users").child(HomeActivity.userId).child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                points = dataSnapshot.getValue(Integer.class);
                //Toast.makeText(context, dataSnapshot.getValue(Integer.class) + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.pointsTv.setText(cost + " points");
        holder.originalCostTv.setText("INR." + cost);

        if(object.getDiscountedPrice() != null){
            holder.originalCostTv.setPaintFlags(holder.originalCostTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discountedCostTv.setText("Rs." + dataSet.get(position).getDiscountedPrice());
        }

        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = onCreateDialog(object);
                dialog.show();
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
            }
        });

    }

    private Dialog onCreateDialog(final BuyPointsObject buyPointsObject) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.confirm_order_dialog);

        TextView pointsTv = dialog.findViewById(R.id.points);
        pointsTv.setText(buyPointsObject.getCost());

        TextView costTv = dialog.findViewById(R.id.cost_tv);
        costTv.setText("INR " + buyPointsObject.getDiscountedPrice());

        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button purchaseBtn = dialog.findViewById(R.id.purchase_btn);
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                buyPoints = buyPointsObject.getCost();
                String orderId = databaseReference.push().getKey();
                    listner.startPayment(Double.parseDouble(buyPointsObject.getDiscountedPrice()), points, orderId);
            }
        });

        return dialog;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
