package com.theindiecorp.khelodapp.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Adapters.SportsSpinnerAdapter;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class UserDataActivity extends AppCompatActivity  {

    private FirebaseAuth auth;
    private RadioGroup gender;
    private RadioButton male,female;
    private EditText bio,usernameEt,phoneNo;
    private CheckBox interest;
    private Button save;
    private TextView date;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner sportSpinner, registerSpinner, involvementSpinner, ageSpinner;
    String sports, registered, involved, ageGroup;
    CircularImageView profilePhoto;
    private int age, currentYear, currentMonth, currentDay;
    private ArrayList<String> sportsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_user_data);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        date=findViewById(R.id.textView1);
        bio=findViewById(R.id.editText2);
        save=findViewById(R.id.button);
        usernameEt = findViewById(R.id.username_et);
        phoneNo=findViewById(R.id.phonenumber);
        gender=findViewById(R.id.radioGrp);
        male=findViewById(R.id.radioM);
        female=findViewById(R.id.radioF);
        sportSpinner = findViewById(R.id.sports_spinner);
        registerSpinner = findViewById(R.id.registering_spinner);
        involvementSpinner = findViewById(R.id.involved_spinner);
        ageSpinner = findViewById(R.id.age_spinner);
        profilePhoto = findViewById(R.id.profile_photo);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        FirebaseStorage.getInstance().getReference().child("users/" + HomeActivity.userId + "/images/profile_pic/profile_pic.jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(UserDataActivity.this).load(uri).into(profilePhoto);
            }
        });

        databaseReference.child("sports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    sportsList.add(snapshot.getKey());
                }
                SportsSpinnerAdapter adapter = new SportsSpinnerAdapter(UserDataActivity.this, sportsList);
                sportSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sports = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> registeredAdapter = ArrayAdapter.createFromResource(this,R.array.register_array, android.R.layout.simple_spinner_item);
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

        ArrayAdapter<CharSequence> involvementAdapter = ArrayAdapter.createFromResource(this,R.array.involvement_array, android.R.layout.simple_spinner_item);
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

        ArrayAdapter<CharSequence> ageGroupAdapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item);
        ageGroupAdapter.setDropDownViewResource(R.layout.spinner_item);
        ageSpinner.setAdapter(ageGroupAdapter);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ageGroup = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("users").child(userId).child("bio").setValue(bio.getText().toString());

                if(!TextUtils.isEmpty(usernameEt.getText()))
                {
                    databaseReference.child("users").child(userId).child("username").setValue(usernameEt.getText().toString());
                }
                if(!TextUtils.isEmpty(bio.getText())){
                    databaseReference.child("users").child(userId).child("bio").setValue(bio.getText().toString());
                }
                if(!TextUtils.isEmpty(date.getText())){
                    databaseReference.child("users").child(userId).child("dob").setValue(date.getText().toString());
                }
                if(!TextUtils.isEmpty(phoneNo.getText())){
                    databaseReference.child("users").child(userId).child("phoneNumber").setValue(phoneNo.getText().toString());
                }
                if(!sports.isEmpty()){
                    databaseReference.child("users").child(userId).child("primarySport").setValue(sports);
                }
                if(!involved.isEmpty()){
                    databaseReference.child("users").child(userId).child("involvementAs").setValue(involved);
                }
                if(!registered.isEmpty()){
                    databaseReference.child("users").child(userId).child("registerAs").setValue(registered);
                }
                if(!ageGroup.isEmpty()){
                    databaseReference.child("users").child(userId).child("ageGroup").setValue(ageGroup);
                }

                finish();
            }
        });

        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("dob").exists()){
                    date.setText(dataSnapshot.child("dob").getValue(String.class));
                }

                if(dataSnapshot.child("bio").exists()){
                    bio.setText(dataSnapshot.child("bio").getValue(String.class));
                }

                if(dataSnapshot.child("username").exists()){
                    usernameEt.setText(dataSnapshot.child("username").getValue(String.class));
                }

                if(dataSnapshot.child("phoneNumber").exists()){
                    phoneNo.setText(dataSnapshot.child("phoneNumber").getValue(String.class));
                }

                if(dataSnapshot.child("primarySport").exists()){
                    if(dataSnapshot.child("primarySport").getValue(String.class).equals("Cricket"))
                        sportSpinner.setSelection(0);
                    else if(dataSnapshot.child("primarySport").getValue(String.class).equals("Football"))
                        sportSpinner.setSelection(1);
                    else if(dataSnapshot.child("primarySport").getValue(String.class).equals("Basketball"))
                        sportSpinner.setSelection(2);
                }

                if(dataSnapshot.child("registerAs").exists()) {
                    if (dataSnapshot.child("registerAs").getValue(String.class).equals("Clubs"))
                        registerSpinner.setSelection(0);
                    else if (dataSnapshot.child("registerAs").getValue(String.class).equals("Academy"))
                        registerSpinner.setSelection(1);
                }

                if(dataSnapshot.child("involvementAs").exists()) {
                    if (dataSnapshot.child("involvementAs").getValue(String.class).equals("Coach"))
                        involvementSpinner.setSelection(0);
                    else if (dataSnapshot.child("involvementAs").getValue(String.class).equals("Sports Fan"))
                        involvementSpinner.setSelection(1);
                    else if (dataSnapshot.child("involvementAs").getValue(String.class).equals("Player"))
                        involvementSpinner.setSelection(2);
                }

                if(dataSnapshot.child("Gender").exists()){
                    if((dataSnapshot.child("Gender").getValue(String.class)).equals("Male"))
                        male.setChecked(true);
                    else
                        female.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton CheckedGender= findViewById(checkedId);
            String text =CheckedGender.getText().toString();
            databaseReference.child("users").child(userId).child("Gender").setValue(text);
        }
    });

    mDisplayDate = (TextView) findViewById(R.id.textView1);

    mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                currentYear = year;
                currentDay = day;
                currentMonth = month;

                DatePickerDialog dialog = new DatePickerDialog(
                        UserDataActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

    mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);

                age = currentYear - year;
            }
        };

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDataActivity.this,HomeActivity.class));
            }
        });


    }

    private boolean isUserNameTaken() {
        final Boolean[] var = new Boolean[1];
        var[0] = false;

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User u = snapshot.getValue(User.class);
                    if(u.getUsername().equals(usernameEt.getText().toString())){
                        Toast.makeText(UserDataActivity.this,"TAKEN",Toast.LENGTH_SHORT).show();
                        var[0] = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return var[0];
    }

    public void openGallery() {
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
