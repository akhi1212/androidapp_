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
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Adapter.BuyPointsAdapter;
import com.theindiecorp.khelodadmin.Data.BuyPointsObject;

import java.util.ArrayList;

public class BuyPointsActivity extends AppCompatActivity {

    private ArrayList<BuyPointsObject> dataset = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_points);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final BuyPointsAdapter buyPointsAdapter = new BuyPointsAdapter(this, new ArrayList<BuyPointsObject>());
        recyclerView.setAdapter(buyPointsAdapter);

        databaseReference.child("buyMorePoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataset = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    BuyPointsObject object = snapshot.getValue(BuyPointsObject.class);
                    object.setId(snapshot.getKey());
                    dataset.add(object);
                }
                buyPointsAdapter.setItems(dataset);
                buyPointsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
