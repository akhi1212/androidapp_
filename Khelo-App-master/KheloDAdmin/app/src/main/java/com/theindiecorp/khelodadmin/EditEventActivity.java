package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import static java.security.AccessController.getContext;

public class EditEventActivity extends AppCompatActivity {

    private EditText eventName, eventDescription,locationSpinner,fees;
    private Button addBtn, addImg;
    private ImageView image;
    private static final int PICK_IMAGE = 100;
    private EditText totalSpots;
    private Spinner venueSpinner, sportSpinner;
    private TextView eventDate, eventTime;
    private int eventYear, eventMonth;
    private CheckBox checkBox, paidCheckBox;
    LinearLayout paidBox;
    private TextView termsTv;
    TextView imageUploadText;
    private String selectedSport;
    private ArrayList<String> sports = new ArrayList<>();

    private DatePickerDialog.OnDateSetListener dateSetListener;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    int PLACE_PICKER_REQUEST = 12;
    private String postId, hostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        postId = getIntent().getStringExtra("postId");
        hostId = getIntent().getStringExtra("hostId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescription);
        addBtn = findViewById(R.id.addBtn);
        image = findViewById(R.id.new_event_main_image);
        addImg = findViewById(R.id.addImg);
        totalSpots = findViewById(R.id.totalSpots);
        eventDate = findViewById(R.id.dateText);
        eventTime = findViewById(R.id.new_event_time_tv);
        locationSpinner = findViewById(R.id.new_event_location_tv);
        fees = findViewById(R.id.fees);
        checkBox = findViewById(R.id.t_and_c_box);
        termsTv = findViewById(R.id.terms_tv);
        imageUploadText = findViewById(R.id.image_text);
        paidCheckBox = findViewById(R.id.checkbox_paid);
        paidBox = findViewById(R.id.paid_box);
        venueSpinner = findViewById(R.id.venue_spinner);
        sportSpinner = findViewById(R.id.sports_spinner);

        mDatabase.child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                eventName.setText(post.getEventName());
                eventDescription.setText(post.getDescription());
                totalSpots.setText(post.getTotalSpots() + "");
                eventDate.setText(post.getEventDate());
                locationSpinner.setText(post.getLocation());

                if(!post.getFees().isEmpty()){
                    paidCheckBox.setChecked(true);
                    fees.setText(post.getFees());
                }

                if(post.getImgUrl() != null) {
                    StorageReference imageReference = storage.getReference().child(post.getImgUrl());
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(EditEventActivity.this)
                                    .load(uri)
                                    .into(image);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(MainActivity.TAG, exception.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("sports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    sports.add(snapshot.getKey());
                }

                SportsSpinnerAdapter sportsSpinnerAdapter = new SportsSpinnerAdapter(EditEventActivity.this, sports);
                sportSpinner.setAdapter(sportsSpinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EditEventActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        eventTime.setText(time);
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(eventDate);
            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addEvent();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private DatePickerDialog.OnDateSetListener getDate(final TextView view) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                view.setText(date);

                eventYear = year;
                eventMonth = month;
            }
        };
    }

    public void setDate(TextView view){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(EditEventActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                getDate(view), year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, EditEventActivity.this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(EditEventActivity.this, toastMsg, Toast.LENGTH_LONG).show();
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

    private void addEvent() throws ParseException{
        mDatabase = FirebaseDatabase.getInstance().getReference("posts");

        String eventName = this.eventName.getText().toString();

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        if(eventYear<year){
            Toast.makeText(EditEventActivity.this, "Select an appropriate date", Toast.LENGTH_LONG).show();
            return;
        }

        if(eventMonth<month){
            Toast.makeText(EditEventActivity.this, "Select an appropriate date", Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkBox.isChecked()){
            Toast.makeText(EditEventActivity.this, "Please accept the terms and conditions and privacy policy", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(fees.getText().toString())){
            fees.setText("0");
        }

        if (!TextUtils.isEmpty(eventName) && !TextUtils.isEmpty(eventDate.getText().toString())
                && !TextUtils.isEmpty(eventTime.getText().toString())) {

            String id = postId;
            String description = eventDescription.getText().toString();
            String d = eventDate.getText().toString() + " " + eventTime.getText().toString();

            int total = Integer.parseInt(totalSpots.getText().toString());
            String userId = hostId;
            String imgPath = uploadImage(id);

            Post event = new Post();
            event.setHostId(userId);
            event.setEventName(eventName);
            event.setDescription(description);
            if (!TextUtils.isEmpty(imgPath))
                event.setImgUrl(imgPath);
            event.setEventDate(d);
            event.setPeopleCount(0);
            event.setTotalSpots(total);
            event.setType("event");
            event.setLocation(locationSpinner.getText().toString());
            event.setSport(selectedSport);
            event.setFees(fees.getText().toString());
            event.setEventMonth(month);
            event.setEventYear(year);
            event.setNumberOfRepots(0);

            mDatabase.child(id).setValue(event);
            Toast.makeText(EditEventActivity.this, "Event Edited", Toast.LENGTH_LONG).show();
            startActivity(new Intent(EditEventActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(EditEventActivity.this, "You should enter a Event Name", Toast.LENGTH_LONG).show();
        }
    }
}