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
import com.theindiecorp.khelodadmin.Adapter.ArchivedUserAdapter;
import com.theindiecorp.khelodadmin.Adapter.SearchUserAdapter;
import com.theindiecorp.khelodadmin.Data.User;

import java.util.ArrayList;

public class ArchivedUserActivity extends AppCompatActivity implements ArchivedUserAdapter.RvListener{

    private ArrayList<String> userIds = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArchivedUserAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.user_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ArchivedUserAdapter(this, new ArrayList<String>(),this);
        recyclerView.setAdapter(adapter);

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userIds = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String userId = snapshot.getKey();
                        if(snapshot.child("archived").exists())
                            userIds.add(userId);
                    }
                    adapter.setUserIds(userIds);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void unArchiveUser(final String userId, int position) {
        adapter.notifyItemRemoved(position);
        databaseReference.child("users").child(userId).child("archived").removeValue();
    }
}
