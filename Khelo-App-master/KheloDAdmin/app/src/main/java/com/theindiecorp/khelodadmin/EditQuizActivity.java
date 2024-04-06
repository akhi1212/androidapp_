package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
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
import com.theindiecorp.khelodadmin.Adapter.SportsSpinnerAdapter;
import com.theindiecorp.khelodadmin.Data.Post;
import com.theindiecorp.khelodadmin.Data.Question;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static java.security.AccessController.getContext;

public class EditQuizActivity extends AppCompatActivity {

    private String postId, hostId;
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
    private TextView termsTv;
    TextView imageUploadText;

    private ArrayList<String> sports = new ArrayList<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final int PICK_IMAGE = 100;
    ArrayList<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);

        postId = getIntent().getStringExtra("postId");
        hostId = getIntent().getStringExtra("hostId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button addImg = findViewById(R.id.uploadImage);
        nameOfQuizEt = findViewById(R.id.quiz_name);
        maxApplicantsEt = findViewById(R.id.quiz_max_number_of_applicants);
        feesTv = findViewById(R.id.fees);
        winnerPointsEt = findViewById(R.id.winner_points_et);
        sportSpinner = findViewById(R.id.sports_spinner);
        image = findViewById(R.id.new_quiz_main_image);
        checkBox = findViewById(R.id.t_and_c_box);
        termsTv = findViewById(R.id.terms_tv);
        imageUploadText = findViewById(R.id.image_text);
        paidCheckBox = findViewById(R.id.checkbox_paid);
        paidBox = findViewById(R.id.paid_box);

        databaseReference.child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = new Post();
                nameOfQuizEt.setText(dataSnapshot.child("eventName").getValue(String.class));
                maxApplicantsEt.setText(post.getMaxNumberOfParticipants() + "");
                feesTv.setText(dataSnapshot.child("fees").getValue(String.class));
                if(!feesTv.getText().toString().equals("0")){
                    paidCheckBox.setChecked(true);
                    paidBox.setVisibility(View.VISIBLE);
                }
                winnerPointsEt.setText(dataSnapshot.child("winnerPoints").getValue(String.class));
                checkBox.setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(postId).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    questions = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Question q = snapshot.getValue(Question.class);
                        q.setId(snapshot.getKey());
                        questions.add(q);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        termsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        databaseReference.child("sports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    sports.add(snapshot.getKey());
                }

                SportsSpinnerAdapter sportsSpinnerAdapter = new SportsSpinnerAdapter(EditQuizActivity.this, sports);
                sportSpinner.setAdapter(sportsSpinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Button createQuiz = findViewById(R.id.create_quiz_btn);
        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuiz();
            }
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
    }

    private void addQuiz() {
        String id = postId;

        if(!checkBox.isChecked()){
            Toast.makeText(EditQuizActivity.this, "Please accept the terms and conditions and privacy policy", Toast.LENGTH_SHORT).show();
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
        post.setHostId(hostId);
        post.setLikeCount(0);
        post.setSport(selectedSport);
        post.setPeopleCount(0);
        post.setParticipants(new ArrayList<String>());
        post.setFees(feesTv.getText().toString());
        post.setEventMonth(month);
        post.setEventYear(year);
        post.setNumberOfRepots(0);
        post.setFinished(true);

        databaseReference.child("posts").child(id).setValue(post);
        databaseReference.child("posts").child(id).child("winnerPoints").setValue(winnerPointsEt.getText().toString());
        for(Question q : questions){
            databaseReference.child("posts").child(id).child("questions").child(q.getId()).setValue(q);
        }

        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, EditQuizActivity.this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(EditQuizActivity.this, toastMsg, Toast.LENGTH_LONG).show();
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
}