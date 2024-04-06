package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Adapter.TransactionsAdapter;
import com.theindiecorp.khelodadmin.Data.Notification;
import com.theindiecorp.khelodadmin.Data.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllPendingTransactionActivity extends AppCompatActivity implements TransactionsAdapter.RvListener{

    private ArrayList<Transaction> transactions = new ArrayList<>();
    private TransactionsAdapter adapter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pending_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TransactionsAdapter(this, new ArrayList<Transaction>(), this);
        recyclerView.setAdapter(adapter);

        Query query = databaseReference.child("transactions");
        query.orderByChild("pending").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    transactions = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Transaction transaction = snapshot.getValue(Transaction.class);
                        transaction.setId(snapshot.getKey());
                        transactions.add(transaction);
                    }
                    adapter.setTransactions(transactions);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateTransaction(Transaction transaction, String status, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if(status.equals("Approved")){
            databaseReference.child("transactions").child(transaction.getId()).child("pending").setValue(false);
            sendApproveNotification(transaction.getUserId());
        }
        else{
            databaseReference.child("transactions").child(transaction.getId()).child("pending").setValue(false);
        }

        adapter.notifyItemRemoved(position);
    }

    private void sendApproveNotification(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String id = databaseReference.push().getKey();

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Notification notification = new Notification();
        notification.setId(id);
        notification.setDate(format.format(date));
        notification.setType("Transaction Approved");
        notification.setText("Redeeming amount successful!");

        databaseReference.child("notifications").child(userId).child(id).setValue(notification);
    }
}
