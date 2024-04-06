package com.theindiecorp.khelodapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Activities.BuyPointsActivity;
import com.theindiecorp.khelodapp.Model.Transaction;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PointsFragment extends Fragment{

    TextView pointsTv;
    Button redeemBtn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    int userPoints;

    private static String payment_method = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_points,container,false);

        pointsTv = view.findViewById(R.id.points_text);
        redeemBtn = view.findViewById(R.id.redeem_btn);

        databaseReference.child("users").child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("points").exists()){
                    pointsTv.setText(dataSnapshot.child("points").getValue(Integer.class) + " Points");
                    userPoints = dataSnapshot.child("points").getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = onCreateDialog();
                dialog.show();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            }
        });

        Button buyPointsBtn = view.findViewById(R.id.buy_points_btn);
        buyPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BuyPointsActivity.class));
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame_container, fragment);
        transaction.commit();
    }

    private Dialog onCreateDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.buy_points_dialog);

        LinearLayout upiLayout = dialog.findViewById(R.id.upi_option_layout);
        LinearLayout bankOptionLayout = dialog.findViewById(R.id.bank_option_layout);
        final LinearLayout bankInputLayout = dialog.findViewById(R.id.bank_input_layout);
        final EditText pointsEt = dialog.findViewById(R.id.points);
        final EditText upiIdEt = dialog.findViewById(R.id.upi_et);
        final EditText bankAccountNoEt = dialog.findViewById(R.id.account_no_et);
        final EditText ifscEt = dialog.findViewById(R.id.ifsc_et);
        final EditText recipientNameEt = dialog.findViewById(R.id.recipient_et);

        upiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_method = "UPI";
                upiIdEt.setVisibility(View.VISIBLE);
                bankInputLayout.setVisibility(View.GONE);
            }
        });

        bankOptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_method = "BANK TRANSFER";
                upiIdEt.setVisibility(View.GONE);
                bankInputLayout.setVisibility(View.VISIBLE);
            }
        });

        Button redeemBtn = dialog.findViewById(R.id.purchase_btn);
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(payment_method.isEmpty()){
                    Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(pointsEt.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Please enter points", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(payment_method.equals("UPI")){
                    if(upiIdEt.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Please Enter your UPI id", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(payment_method.equals("BANK TRANSFER")){
                    if(bankAccountNoEt.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Please Enter your Bank Account Number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(ifscEt.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Please Enter your IFSC COde", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (recipientNameEt.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Please Enter your name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                int redeemPoints = Integer.parseInt(pointsEt.getText().toString());

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Transaction transaction = new Transaction("Points Redeemed",redeemPoints,format.format(date), true);
                transaction.setUserId(HomeActivity.userId);

                String id = databaseReference.push().getKey();

                databaseReference.child("transactions").child(id).setValue(transaction);
                databaseReference.child("transactions").child(id).child("pending").setValue(true);

                if(payment_method.equals("UPI")){
                    databaseReference.child("paymentDetails").child(HomeActivity.userId).child(id).child("payment_method").setValue(payment_method);
                    databaseReference.child("paymentDetails").child(HomeActivity.userId).child(id).child("upiId").setValue(upiIdEt.getText().toString());
                }
                else if(payment_method.equals("BANK TRANSFER")){
                    databaseReference.child("paymentDetails").child(HomeActivity.userId).child(id).child("payment_method").setValue(payment_method);
                    databaseReference.child("paymentDetails").child(HomeActivity.userId).child(id).child("accountNumber").setValue(bankAccountNoEt.getText().toString());
                    databaseReference.child("paymentDetails").child(HomeActivity.userId).child(id).child("ifscCode").setValue(ifscEt.getText().toString());
                    databaseReference.child("paymentDetails").child(HomeActivity.userId).child(id).child("recipientName").setValue(recipientNameEt.getText().toString());
                }

                dialog.dismiss();
                Dialog dialog1 = onCreateDialogPurchaseSuccessful(pointsEt.getText().toString());
                dialog1.show();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog1.getWindow().setLayout(width, height);
                dialog1.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            }
        });

        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private Dialog onCreateDialogPurchaseSuccessful(String pointsRedeemed){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView tv = dialog.findViewById(R.id.update_tv);
        tv.setText("Your transaction will be processed with in 48-60 hours.");

        Button doneBtn = dialog.findViewById(R.id.check_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }


}
