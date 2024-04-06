package com.theindiecorp.khelodapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.TransactionsAdapter;
import com.theindiecorp.khelodapp.Model.Transaction;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionsFragment extends Fragment {
    
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transactions,container,false);

        recyclerView = v.findViewById(R.id.transaction_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getContext(), new ArrayList<Transaction>());
        recyclerView.setAdapter(transactionsAdapter);

        Query query = FirebaseDatabase.getInstance().getReference("transactions");
        query.orderByChild("userId").equalTo(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Transaction> transactions = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    transactions.add(transaction);
                }
                Collections.reverse(transactions);
                transactionsAdapter.setItems(transactions);
                transactionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }
}
