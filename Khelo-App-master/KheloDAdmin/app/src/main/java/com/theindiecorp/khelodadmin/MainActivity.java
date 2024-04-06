package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Adapter.PostsAdapter;
import com.theindiecorp.khelodadmin.Data.Post;
import com.theindiecorp.khelodadmin.Data.User;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements PostsAdapter.PostListener{

    public static final String TAG = "HomeActivity";
    private ArrayList<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;
    private Button quizBtn, articleBtn, eventsBtn;
    Query query = FirebaseDatabase.getInstance().getReference("posts");
    private boolean viewReported = false;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private String postType = "quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.filter_item:
                        Dialog dialog = createFilterDialog();
                        dialog.show();
                }

                return false;
            }
        });

        drawerLayout = findViewById(R.id.activity_main);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        navigationView = findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.view_profiles_item:
                        startActivity(new Intent(MainActivity.this, UserViewActivity.class));
                        break;
                    case R.id.view_archived_item:
                        startActivity(new Intent(MainActivity.this, ArchivedUserActivity.class));
                        break;
                    case R.id.pending_transaction_item:
                        startActivity(new Intent(MainActivity.this, AllPendingTransactionActivity.class));
                        break;
                    case R.id.buy_points_item:
                        startActivity(new Intent(MainActivity.this, BuyPointsActivity.class));
                        break;
                    case R.id.sports_list_item:
                        startActivity(new Intent(MainActivity.this, SportsListActivity.class));
                        break;
                    case R.id.quiz_question_item:
                        startActivity(new Intent(MainActivity.this, SportQuizActivity.class));
                        break;
                }

                return true;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostsAdapter(this, new ArrayList<Post>(), this);
        recyclerView.setAdapter(adapter);

        quizBtn = findViewById(R.id.search_view_quiz_btn);
        eventsBtn = findViewById(R.id.search_view_post_btn);
        articleBtn = findViewById(R.id.search_view_article_btn);

        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postType = "quiz";
                if(viewReported)
                    query.orderByChild("type").equalTo(postType).addValueEventListener(reportedListener);
                else
                    query.orderByChild("type").equalTo(postType).addValueEventListener(valueEventListener);
                quizBtn.setTextColor(getResources().getColor(android.R.color.white));
                eventsBtn.setTextColor(getResources().getColor(android.R.color.black));
                articleBtn.setTextColor(getResources().getColor(android.R.color.black));
            }
        });

        eventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postType = "event";
                if(viewReported)
                    query.orderByChild("type").equalTo(postType).addValueEventListener(reportedListener);
                else
                    query.orderByChild("type").equalTo(postType).addValueEventListener(valueEventListener);
                quizBtn.setTextColor(getResources().getColor(android.R.color.black));
                eventsBtn.setTextColor(getResources().getColor(android.R.color.white));
                articleBtn.setTextColor(getResources().getColor(android.R.color.black));
            }
        });

        articleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postType = "article";
                if(viewReported)
                    query.orderByChild("type").equalTo(postType).addValueEventListener(reportedListener);
                else
                    query.orderByChild("type").equalTo(postType).addValueEventListener(valueEventListener);
                quizBtn.setTextColor(getResources().getColor(android.R.color.black));
                eventsBtn.setTextColor(getResources().getColor(android.R.color.black));
                articleBtn.setTextColor(getResources().getColor(android.R.color.white));
            }
        });

    }

    private Dialog createFilterDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.filter_dialog);

        CheckBox viewReportedCb = dialog.findViewById(R.id.view_reported_cb);
        viewReportedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    viewReported = true;
                    dialog.dismiss();
                }
                else{
                    viewReported = false;
                    dialog.dismiss();
                }
            }
        });

        if(viewReported)
            viewReportedCb.setChecked(true);
        else
            viewReportedCb.setChecked(false);

        return dialog;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            posts = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Post p = snapshot.getValue(Post.class);
                p.setPostId(snapshot.getKey());
                posts.add(p);
            }
            Collections.reverse(posts);
            adapter.setPosts(posts);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener reportedListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            posts = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Post p = snapshot.getValue(Post.class);
                p.setPostId(snapshot.getKey());
                if(p.getNumberOfRepots() > 0)
                    posts.add(p);

            }
            Collections.reverse(posts);
            adapter.setPosts(posts);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void deletePost(Post post, int position) {
        posts.remove(position);
        adapter.notifyDataSetChanged();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("deletedPosts").child(post.getPostId()).setValue(post);
        databaseReference.child("posts").child(post.getPostId()).removeValue();
    }
}
