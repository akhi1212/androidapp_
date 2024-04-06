package com.theindiecorp.khelodapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.theindiecorp.khelodapp.Fragments.MainFeedFragment;
import com.theindiecorp.khelodapp.Fragments.UserDetailsFragment1;
import com.theindiecorp.khelodapp.R;

public class CompleteProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                new UserDetailsFragment1()).commit();

    }
}
