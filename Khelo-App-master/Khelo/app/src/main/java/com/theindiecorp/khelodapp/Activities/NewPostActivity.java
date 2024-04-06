package com.theindiecorp.khelodapp.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.theindiecorp.khelodapp.Adapters.ViewPagerAdapter;
import com.theindiecorp.khelodapp.Fragments.NewArticleFragment;
import com.theindiecorp.khelodapp.Fragments.NewQuizFragment;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;

public class NewPostActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_post);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.setupWithViewPager(viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new NewArticleFragment());
        fragments.add(new NewQuizFragment());

        ArrayList<String> fragmentTitles = new ArrayList<>();
        fragmentTitles.add("Article");
        fragmentTitles.add("Quiz");

        viewPagerAdapter.setFragments(fragments, fragmentTitles);
        viewPager.setAdapter(viewPagerAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
