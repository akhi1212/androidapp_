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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.BuyPointsItemAdapter;
import com.theindiecorp.khelodapp.Model.BuyPointsObject;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class BuyPointsFragment extends Fragment{

    private RecyclerView recyclerView;
    private BuyPointsItemAdapter buyPointsItemAdapter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    ArrayList<BuyPointsObject> arrayList = new ArrayList<>();

    int points;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_points,container,false);

        recyclerView = view.findViewById(R.id.buy_points_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

//        buyPointsItemAdapter = new BuyPointsItemAdapter(getContext(),new ArrayList<BuyPointsObject>(), this);
        recyclerView.setAdapter(buyPointsItemAdapter);

        databaseReference.child("buyMorePoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BuyPointsObject object = new BuyPointsObject(snapshot.child("cost").getValue(String.class));
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

        return view;
    }
}
