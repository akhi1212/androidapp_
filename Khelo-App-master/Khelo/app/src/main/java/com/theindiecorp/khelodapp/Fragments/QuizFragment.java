package com.theindiecorp.khelodapp.Fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.theindiecorp.khelodapp.R;

public class QuizFragment extends Fragment {

    Animation rotateAnimation;
    ImageView imageView;

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_view, container, false);

        LinearLayout linearlayout = view.findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearlayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        imageView=view.findViewById(R.id.basketball);

        rotateAnimation();
        return view;
    }

    private void rotateAnimation() {

        rotateAnimation= AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        imageView.startAnimation(rotateAnimation);

    }
}
