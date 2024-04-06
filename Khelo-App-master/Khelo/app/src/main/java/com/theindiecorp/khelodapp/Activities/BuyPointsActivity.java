package com.theindiecorp.khelodapp.Activities;

import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.theindiecorp.khelodapp.Adapters.BuyPointsItemAdapter;
import com.theindiecorp.khelodapp.Model.BuyPointsObject;
import com.theindiecorp.khelodapp.Model.Transaction;
import com.theindiecorp.khelodapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class BuyPointsActivity extends AppCompatActivity implements BuyPointsItemAdapter.BuyPointsListner, PaymentResultListener {

    private RecyclerView recyclerView;
    private BuyPointsItemAdapter buyPointsItemAdapter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ArrayList<BuyPointsObject> arrayList = new ArrayList<>();

    private int points;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_points);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.buy_points_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        buyPointsItemAdapter = new BuyPointsItemAdapter(this,new ArrayList<BuyPointsObject>(), this);
        recyclerView.setAdapter(buyPointsItemAdapter);

        databaseReference.child("buyMorePoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BuyPointsObject object = new BuyPointsObject(snapshot.child("cost").getValue(String.class));
                    if(snapshot.child("discountedPrice").exists()){
                        object.setDiscountedPrice(snapshot.child("discountedPrice").getValue(String.class));
                    }
                    arrayList.add(object);
                }
                Collections.reverse(arrayList);
                buyPointsItemAdapter.setItems(arrayList);
                buyPointsItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Dialog onCreateDialogPurchaseSuccessful(BuyPointsObject buyPointsObject, String paymentId){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        points = points + Integer.parseInt(buyPointsObject.getCost());
        databaseReference.child("users").child(HomeActivity.userId).child("points").setValue(points);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Transaction transaction = new Transaction("Buy Points",Integer.parseInt(buyPointsObject.getCost()),format.format(date));
        transaction.setUserId(HomeActivity.userId);
        databaseReference.child("transactions").child(paymentId).setValue(transaction);

        Button doneBtn = dialog.findViewById(R.id.check_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void startPayment(Double amount, int points, String orderId) {

        this.points = points;

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_yK2ngaOlF2YtpR");
        checkout.setImage(R.mipmap.ic_launcher);

        Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("currency", "INR");
            options.put("amount", amount * 100);
            options.put("name", "KheloD");
            checkout.open(activity, options);
        } catch (JSONException e) {
            Log.d("ErrorPayment",  e.getMessage());
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Dialog dialog1 = onCreateDialogPurchaseSuccessful(new BuyPointsObject(BuyPointsItemAdapter.buyPoints), s);
        dialog1.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog1.getWindow().setLayout(width, height);
        dialog1.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
    }
}
