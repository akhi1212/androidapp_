package com.theindiecorp.khelodapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theindiecorp.khelodapp.Adapters.SportsSpinnerAdapter;
import com.theindiecorp.khelodapp.Activities.AddQuestionActivity;
import com.theindiecorp.khelodapp.Model.Post;
import com.theindiecorp.khelodapp.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static com.theindiecorp.khelodapp.Activities.SignUpActivity.PICK_IMAGE;

public class NewQuizFragment extends Fragment {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ImageView image;
    int PLACE_PICKER_REQUEST = 12;
    private EditText nameOfQuizEt,maxApplicantsEt,winnerPointsEt;
    private Spinner sportSpinner;
    private String selectedSport;
    TextView feesTv;
    CheckBox paidCheckBox;
    LinearLayout paidBox;
    private CheckBox checkBox;
    private TextView termsTv, isThisPaidQuiz;
    TextView imageUploadText;

    private ArrayList<String> sports = new ArrayList<>();

    int userPoints = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_quiz,container,false);

        Button addImg = view.findViewById(R.id.uploadImage);
        nameOfQuizEt = view.findViewById(R.id.quiz_name);
        maxApplicantsEt = view.findViewById(R.id.quiz_max_number_of_applicants);
        feesTv = view.findViewById(R.id.fees);
        winnerPointsEt = view.findViewById(R.id.winner_points_et);
        sportSpinner = view.findViewById(R.id.sports_spinner);
        image = view.findViewById(R.id.new_quiz_main_image);
        checkBox = view.findViewById(R.id.t_and_c_box);
        termsTv = view.findViewById(R.id.terms_tv);
        imageUploadText = view.findViewById(R.id.image_text);
        paidCheckBox = view.findViewById(R.id.checkbox_paid);
        paidBox = view.findViewById(R.id.paid_box);
        isThisPaidQuiz = view.findViewById(R.id.is_this_paid_quiz_tv);

        paidCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    paidBox.setVisibility(View.VISIBLE);
                }
                else{
                    paidBox.setVisibility(View.GONE);
                }
            }
        });

        termsTv.setOnClickListener(view1 -> {
            String url = "https://www.youtube.com/";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        databaseReference.child("sports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    sports.add(snapshot.getKey());
                }

                SportsSpinnerAdapter sportsSpinnerAdapter = new SportsSpinnerAdapter(getContext(), sports);
                sportSpinner.setAdapter(sportsSpinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addImg.setOnClickListener(v -> openGallery());

        Button createQuiz = view.findViewById(R.id.create_quiz_btn);
        createQuiz.setOnClickListener(view12 -> {
            addQuiz();
        });

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSport = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void addQuiz() {
        String id = databaseReference.push().getKey();

        if(!checkBox.isChecked()){
            Toast.makeText(getContext(), "Please accept the terms and conditions and privacy policy", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(nameOfQuizEt.getText().toString())){
            nameOfQuizEt.setError("Please enter name of the quiz");
            return;
        }

        if(TextUtils.isEmpty(winnerPointsEt.getText().toString())){
            winnerPointsEt.setText("0");
            //return;
        }

        if(TextUtils.isEmpty(feesTv.getText().toString())){
            feesTv.setText("0");
            //return;
        }

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String date = day + "/" + month + "/" + year;

        String imgPath = uploadImage(id);

        Post post = new Post();
        post.setEventName(nameOfQuizEt.getText().toString());
        post.setType("quiz");
        post.setPublishDate(date);
        post.setMaxNumberOfParticipants(Integer.parseInt(maxApplicantsEt.getText().toString()));
        if (!TextUtils.isEmpty(imgPath))
            post.setImgUrl(imgPath);
        post.setHostId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setLikeCount(0);
        post.setSport(selectedSport);
        post.setPeopleCount(0);
        post.setParticipants(new ArrayList<String>());
        post.setFees(feesTv.getText().toString());
        post.setEventMonth(month);
        post.setEventYear(year);
        post.setNumberOfRepots(0);

        databaseReference.child("posts").child(id).setValue(post);
        databaseReference.child("posts").child(id).child("winnerPoints").setValue(winnerPointsEt.getText().toString());

        startActivity(new Intent(getContext(),AddQuestionActivity.class)
                .putExtra("quizId",id)
                .putExtra("sport", selectedSport));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, getContext());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            } else if (requestCode == PICK_IMAGE) {
                image.setImageURI(data.getData());
            }
        }
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

        imageUploadText.setVisibility(View.GONE);
    }


    public String uploadImage(String eventId) {

        if (image.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "events/" + eventId + "/images/image.jpeg";
            StorageReference storageReference = storage.getReference(path);

            UploadTask uploadTask = storageReference.putBytes(bitmapdata);

            return path;
        }

        return "";
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame_container, fragment);
        transaction.commit();
    }
}
