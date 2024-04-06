package com.theindiecorp.khelodapp.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.MainFeedAdapter;
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity {

    MainFeedAdapter articleAdapter,quizAdapter;
    Button articleBtn,quizBtn;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    RecyclerView articleRecycler,quizRecycler;
    SearchView searchView;

    ArrayList<Post> articles = new ArrayList<>();
    ArrayList<Post> quizes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        articleBtn = findViewById(R.id.search_view_article_btn);
        quizBtn = findViewById(R.id.search_view_quiz_btn);
        searchView = findViewById(R.id.main_feed_search_view);

        articleRecycler = findViewById(R.id.article_recycler);
        quizRecycler = findViewById(R.id.quiz_recycler);

        articleRecycler.setLayoutManager(new LinearLayoutManager(this));
        quizRecycler.setLayoutManager(new LinearLayoutManager(this));

        articleAdapter = new MainFeedAdapter(this,new ArrayList<Post>());
        articleRecycler.setAdapter(articleAdapter);

        quizAdapter = new MainFeedAdapter(this,new ArrayList<Post>());
        quizRecycler.setAdapter(quizAdapter);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleRecycler.setVisibility(View.GONE);
                quizRecycler.setVisibility(View.VISIBLE);
                articleBtn.setTextColor(getResources().getColor(android.R.color.black));
                quizBtn.setTextColor(getResources().getColor(android.R.color.white));
            }
        });

        articleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleRecycler.setVisibility(View.VISIBLE);
                quizRecycler.setVisibility(View.GONE);
                articleBtn.setTextColor(getResources().getColor(android.R.color.white));
                quizBtn.setTextColor(getResources().getColor(android.R.color.black));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryTxt) {
                Query query2 = databaseReference.child("posts")
                        .orderByChild("description")
                        .startAt(queryTxt)
                        .endAt(queryTxt + "\uf8ff");

                query2.addValueEventListener(articleEventListener);

                Query query3 = databaseReference.child("posts")
                        .orderByChild("eventName")
                        .startAt(queryTxt)
                        .endAt(queryTxt + "\uf8ff");

                query3.addValueEventListener(quizListener);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query2 = databaseReference.child("posts")
                        .orderByChild("description")
                        .startAt(newText)
                        .endAt(newText + "\uf8ff");

                query2.addValueEventListener(articleEventListener);

                Query query3 = databaseReference.child("posts")
                        .orderByChild("eventName")
                        .startAt(newText)
                        .endAt(newText + "\uf8ff");

                query3.addValueEventListener(quizListener);

                return false;
            }
        });

        if(articles.isEmpty()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
            reference.addValueEventListener(articleEventListener);
        }

        if(quizes.isEmpty()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
            reference.addValueEventListener(quizListener);
        }

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this,HomeActivity.class));
            }
        });

    }

    ValueEventListener articleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            articles = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Post p = snapshot.getValue(Post.class);
                p.setPostId(snapshot.getKey());
                if(p.getType().equals("article")){
                    articles.add(p);
                }
            }
            Collections.reverse(articles);
            articleAdapter.setPosts(articles);
            articleAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    ValueEventListener quizListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            quizes = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Post p = snapshot.getValue(Post.class);
                p.setPostId(snapshot.getKey());
                if(p.getType().equals("quiz")){
                    if(p.getFinished() != null)
                        quizes.add(p);
                }
            }
            Collections.reverse(quizes);
            quizAdapter.setPosts(quizes);
            quizAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
