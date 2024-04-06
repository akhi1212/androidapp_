package com.theindiecorp.khelodapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Adapters.LatestMatchAdapter;
import com.theindiecorp.khelodapp.Adapters.MainFeedAdapter;
import com.theindiecorp.khelodapp.Model.Constants;
import com.theindiecorp.khelodapp.Model.GetCricketMatchesResponse;
import com.theindiecorp.khelodapp.Model.Match;
import com.theindiecorp.khelodapp.Model.Post;


import com.theindiecorp.khelodapp.R;
import com.theindiecorp.khelodapp.Activities.SearchActivity;
import com.theindiecorp.khelodapp.ViewModel.CricketViewModel;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static com.theindiecorp.khelodapp.Activities.HomeActivity.MY_PREFS_NAME;
import static com.theindiecorp.khelodapp.Activities.HomeActivity.postType;


public class MainFeedFragment extends Fragment implements LatestMatchAdapter.MatchListener {

    private FrameLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    Fragment fragment;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Button share,searchBtn;
    CircularImageView profileImg;
    MainFeedAdapter mainFeedAdapter;

    private CricketViewModel viewModel;

    ArrayList<Post> posts = new ArrayList<>();
    ShimmerFrameLayout matchShimmer, mainFeedShimmer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_feed,container,false);

        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        share = view.findViewById(R.id.share_Post);
        profileImg = view.findViewById(R.id.main_item_profile_pic);
        searchBtn = view.findViewById(R.id.search_btn);
        matchShimmer = view.findViewById(R.id.shimmer);
        mainFeedShimmer = view.findViewById(R.id.feedShimmer);

        matchShimmer.startShimmer();
        mainFeedShimmer.startShimmer();

        //Loading the feed
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mainFeedAdapter = new MainFeedAdapter(getContext(), new ArrayList<Post>());
        mRecyclerView.setAdapter(mainFeedAdapter);

        databaseReference.child("posts").addValueEventListener(allPostsListener);

        //loading matches
        RecyclerView matchRecycler = view.findViewById(R.id.new_match_list);
        matchRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        LatestMatchAdapter latestMatchAdapter = new LatestMatchAdapter(getContext(), new ArrayList<>(), this);
        matchRecycler.setAdapter(latestMatchAdapter);

        viewModel = new ViewModelProvider(getActivity()).get(CricketViewModel.class);
        viewModel.init();
        viewModel.getCricketMatches().observe(getActivity(), new Observer<GetCricketMatchesResponse>() {
            @Override
            public void onChanged(GetCricketMatchesResponse getCricketMatchesResponse) {
                matchShimmer.stopShimmer();
                matchShimmer.setVisibility(View.GONE);
                matchRecycler.setVisibility(View.VISIBLE);
                latestMatchAdapter.setMatches(filterMatches((ArrayList<Match>) getCricketMatchesResponse.getMatches()));
                latestMatchAdapter.notifyDataSetChanged();
            }
        });

        //First time user
        SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Boolean firstTime = prefs.getBoolean("firstTime", true);

        if(firstTime){
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.quiz_banner);

            Button createQuizBtn = dialog.findViewById(R.id.create_quiz_btn);

            createQuizBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            firstTime = false;
            SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("firstTime", firstTime);
            editor.apply();
    }

        share.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              fragment =new NewArticleFragment();
            loadFragment(fragment);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        Button menuBtn = view.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Swipe right to open the navigation menu",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    ValueEventListener allPostsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mainFeedShimmer.stopShimmer();
            mainFeedShimmer.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            posts = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Post p = snapshot.getValue(Post.class);
                p.setPostId(snapshot.getKey());
                posts.add(p);
                if(p.getType().equals("quiz") && p.getFinished() ==  null && p.getWinnerId() == null){
                    posts.remove(p);
                }
            }
            Collections.reverse(posts);
            mainFeedAdapter.setPosts(posts);
            mRecyclerView.setAdapter(mainFeedAdapter);
            mainFeedAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame_container, fragment);
        transaction.commit();
    }

    private ArrayList<Match> filterMatches(ArrayList<Match> matches){
        Constants constants = new Constants();
        ArrayList<Match> filteredMatches = new ArrayList<>();

        for(Match match : matches)
            if(constants.getIplTeams().contains(match.getTeam1()))
                filteredMatches.add(match);

        return filteredMatches;
    }

    private void loadMatchDetails(long uniqueId, String type){
        TextView team1 = bottomSheet.findViewById(R.id.team_one_tv);
        TextView team2 = bottomSheet.findViewById(R.id.team_two_tv);
        TextView score = bottomSheet.findViewById(R.id.score_tv);
        TextView stat = bottomSheet.findViewById(R.id.stat_tv);
        TextView typeTv = bottomSheet.findViewById(R.id.match_type_tv);

        viewModel.getMatchDetails(uniqueId).observe(getActivity(), getMatchDetailsResponse -> {
            team1.setText(getMatchDetailsResponse.getTeam1());
            team2.setText(getMatchDetailsResponse.getTeam2());
            score.setText(getMatchDetailsResponse.getScore());
            stat.setText(getMatchDetailsResponse.getStat() + "");
            typeTv.setText(type);
        });

        Toolbar toolbar = bottomSheet.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        Button createAPollBtn = bottomSheet.findViewById(R.id.create_a_poll_btn);
        createAPollBtn.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            loadFragment(new NewPollFragment());
        });
    }

    @Override
    public void showMatchDetails(long uniqueId, String type) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        loadMatchDetails(uniqueId, type);
    }
}