package com.theindiecorp.khelodapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodapp.Adapters.SearchUserAdapter;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.R;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements CardStackListener {

    SearchView searchView;
    ImageButton filterBtn;
    SearchUserAdapter searchUserAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> userIds = new ArrayList<>();
    CardStackView stackView;

    private ConstraintLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);
        searchView = view.findViewById(R.id.main_feed_search_view);
        filterBtn = view.findViewById(R.id.filter_btn);
        stackView = view.findViewById(R.id.stack_view);

        CardStackLayoutManager layoutManager = new CardStackLayoutManager(getContext(), this);
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        layoutManager.setOverlayInterpolator(new LinearInterpolator());
        stackView.setLayoutManager(layoutManager);

        searchUserAdapter = new SearchUserAdapter(getContext(),new ArrayList<String>());
        stackView.setAdapter(searchUserAdapter);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryTxt) {

                Query query1 = databaseReference.child("users")
                        .orderByChild("displayName")
                        .startAt(queryTxt)
                        .endAt(queryTxt + "\uf8ff");

                query1.addValueEventListener(userEventListener);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Query query1 = databaseReference.child("users")
                        .orderByChild("displayName")
                        .startAt(newText)
                        .endAt(newText + "\uf8ff");

                query1.addValueEventListener(userEventListener);

                return false;
            }
        });

        if(userIds.isEmpty()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            reference.addValueEventListener(userEventListener);
        }

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        final ArrayList<String> sports = new ArrayList<>();

        CheckBox footballBox = bottomSheet.findViewById(R.id.football_box);
        CheckBox cricketBox = bottomSheet.findViewById(R.id.cricket_box);
        CheckBox basketballBox = bottomSheet.findViewById(R.id.basketball_box);

        if(sports.contains("Football")){
            footballBox.setChecked(false);
        }

        if(sports.contains("Cricket")){
            cricketBox.setChecked(false);
        }

        if(sports.contains("Basketball")){
            basketballBox.setChecked(false);
        }

        footballBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    sports.add("Football");
                else
                    sports.remove("Football");
            }
        });

        cricketBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                    sports.add("Cricket");
                else
                    sports.remove("Cricket");
            }
        });

        basketballBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                    sports.add("Basketball");
                else
                    sports.remove("Basketball");
            }
        });

        Button searchBtn = bottomSheet.findViewById(R.id.search_btn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sports.isEmpty()){
                    Toast.makeText(getContext(),"Select at least one sport",Toast.LENGTH_LONG).show();
                    return;
                }

                ValueEventListener filteredListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userIds = new ArrayList<>();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                String userId = snapshot.getKey();
                                User user = snapshot.getValue(User.class);
                                if(sports.contains(user.getPrimarySport()))
                                    userIds.add(userId);

                            }
                        }

                        searchUserAdapter.setUserIds(userIds);
                        searchUserAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference.addValueEventListener(filteredListener);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        return view;
    }

    ValueEventListener userEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userIds = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    String userId = snapshot.getKey();
                    userIds.add(userId);

                }
            }
            searchUserAdapter.setUserIds(userIds);
            searchUserAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {

    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}
