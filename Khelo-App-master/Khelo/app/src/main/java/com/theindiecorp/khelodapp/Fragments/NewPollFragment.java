package com.theindiecorp.khelodapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodapp.R;

public class NewPollFragment extends Fragment {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private EditText nameOfQuizEt;
    private CheckBox paidCheckBox;
    private LinearLayout paidBox;
    private CheckBox checkBox;
    private TextView termsTv, isThisPaidQuiz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_new_poll, container, false);

        nameOfQuizEt = view.findViewById(R.id.quiz_name);
        checkBox = view.findViewById(R.id.t_and_c_box);
        termsTv = view.findViewById(R.id.terms_tv);
        paidCheckBox = view.findViewById(R.id.checkbox_paid);
        paidBox = view.findViewById(R.id.paid_box);
        isThisPaidQuiz = view.findViewById(R.id.is_this_paid_quiz_tv);

        paidCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                paidBox.setVisibility(View.VISIBLE);
            }
            else{
                paidBox.setVisibility(View.GONE);
            }
        });

        termsTv.setOnClickListener(view1 -> {
            String url = "https://www.youtube.com/";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });


        return view;
    }
}
