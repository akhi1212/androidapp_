package com.theindiecorp.khelodapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodapp.R;

public class UserDetailsFragment2 extends Fragment {

    private Spinner registerSpinner, involvementSpinner;
    String registered, involved;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String userId = user.getUid();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details_step_2, container, false);

        registerSpinner = view.findViewById(R.id.registering_spinner);
        involvementSpinner = view.findViewById(R.id.involved_spinner);

        ArrayAdapter<CharSequence> registeredAdapter = ArrayAdapter.createFromResource(getContext(),R.array.register_array, android.R.layout.simple_spinner_item);
        registeredAdapter.setDropDownViewResource(R.layout.spinner_item);
        registerSpinner.setAdapter(registeredAdapter);

        registerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                registered = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> involvementAdapter = ArrayAdapter.createFromResource(getContext(),R.array.involvement_array, android.R.layout.simple_spinner_item);
        involvementAdapter.setDropDownViewResource(R.layout.spinner_item);
        involvementSpinner.setAdapter(involvementAdapter);

        involvementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                involved = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button saveBtn = view.findViewById(R.id.button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(involved.isEmpty()){
                    return;
                }
                if(registered.isEmpty()){
                    return;
                }

                databaseReference.child("users").child(userId).child("involvementAs").setValue(involved);
                databaseReference.child("users").child(userId).child("registerAs").setValue(registered);
                Fragment fragment = new UserDetailsFragment3();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return view;
    }
}
