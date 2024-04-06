package com.theindiecorp.khelodapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Activities.HomeActivity;
import com.theindiecorp.khelodapp.R;
import com.theindiecorp.khelodapp.Activities.SignUpActivity;

import static android.app.Activity.RESULT_OK;

public class UserDetailsFragment1 extends Fragment {

    private FirebaseAuth auth;
    private RadioGroup gender;
    private RadioButton male,female;
    private EditText usernameEt,phoneNo;
    private CheckBox interest;
    private Button nextButton;
    private TextView date;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Spinner ageSpinner;
    CircularImageView profilePhoto;
    private String ageGroup = "5 to 10";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_details_step_1, container, false);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        nextButton=view.findViewById(R.id.button);
        usernameEt = view.findViewById(R.id.username_et);
        phoneNo=view.findViewById(R.id.phonenumber);
        gender=view.findViewById(R.id.radioGrp);
        male=view.findViewById(R.id.radioM);
        female=view.findViewById(R.id.radioF);
        ageSpinner = view.findViewById(R.id.age_spinner);
        profilePhoto = view.findViewById(R.id.profile_photo);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        FirebaseStorage.getInstance().getReference().child("users/" + HomeActivity.userId + "/images/profile_pic/profile_pic.jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).into(profilePhoto);
            }
        });

        final ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(getActivity()
                ,R.array.age_array
                ,android.R.layout.simple_spinner_item);

        ageAdapter.setDropDownViewResource(R.layout.location_spinner_layout);
        ageSpinner.setAdapter(ageAdapter);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ageGroup = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton CheckedGender= view.findViewById(checkedId);
                String text =CheckedGender.getText().toString();
                databaseReference.child("users").child(userId).child("Gender").setValue(text);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ageGroup.isEmpty()){
                    Toast.makeText(getContext(), "Please select an age group", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(usernameEt.getText())) {
                   usernameEt.setError("Enter Username");
                   return;
                }
                if(TextUtils.isEmpty(phoneNo.getText())){
                   phoneNo.setError("Enter Phone Number");
                   return;
                }
                databaseReference.child("users").child(userId).child("username").setValue(usernameEt.getText().toString());
                databaseReference.child("users").child(userId).child("phoneNumber").setValue(phoneNo.getText().toString());
                databaseReference.child("users").child(userId).child("ageGroup").setValue(ageGroup);

                Fragment fragment = new UserDetailsFragment2();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SignUpActivity.PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SignUpActivity.PICK_IMAGE) {
            profilePhoto.setImageURI(data.getData());
            String path = SignUpActivity.updateProfilePick(HomeActivity.userId, profilePhoto);
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(path)).build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(request);
        }
    }
}
