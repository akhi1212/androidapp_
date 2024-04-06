package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.theindiecorp.khelodadmin.Adapter.SearchUserAdapter;
import com.theindiecorp.khelodadmin.Data.User;

import java.util.ArrayList;

public class UserViewActivity extends AppCompatActivity implements SearchUserAdapter.RvListener {

    private ArrayList<String> userIds = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private SearchUserAdapter adapter;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.user_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapter = new SearchUserAdapter(this, new ArrayList<String>(), this);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.main_feed_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Query query = FirebaseDatabase.getInstance().getReference("users");
                query.orderByChild("displayName").startAt(queryText)
                        .endAt(queryText + "\uf8ff").addValueEventListener(valueEventListener);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = FirebaseDatabase.getInstance().getReference("users");
                query.orderByChild("displayName").startAt(newText)
                        .endAt(newText + "\uf8ff").addValueEventListener(valueEventListener);
                return false;
            }
        });

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIds = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(!snapshot.child("archived").exists())
                        userIds.add(snapshot.getKey());
                }
                adapter.setUserIds(userIds);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deleteUser(final String userId, int position) {
        adapter.notifyItemRemoved(position);
        databaseReference.child("users").child(userId).child("archived").setValue(true);
    }

    @Override
    public void undeleteUser(String userId, int pos) {

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userIds = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                if(!snapshot.child("archived").exists())
                    userIds.add(snapshot.getKey());
            }
            adapter.setUserIds(userIds);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
