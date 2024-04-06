package com.theindiecorp.khelodapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.Model.Transaction;
import com.theindiecorp.khelodapp.Model.User;
import com.theindiecorp.khelodapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventViewActivity extends AppCompatActivity {

    private TextView hostNameTv, eventNameTv, timeTv, locationTv, hostedByTv, peopleCountTv, descriptionTv;
    private TextView readMore;
    private Button joinBtn;
    private CircularImageView profilePic;
    private ImageView mainImage;
    private String name;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Boolean joined;

    int peopleCount, maxPeople;
    static int pointsUser, fees = 0, hostPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        final String eventName = getIntent().getStringExtra("eventName");
        final String hostId = getIntent().getStringExtra("hostId");
        String location =  getIntent().getStringExtra("location");
        final String description =  getIntent().getStringExtra("description");
        String mainImgUrl = getIntent().getStringExtra("mainImgUrl");
        final String eventId = getIntent().getStringExtra("eventId");
        String date =  getIntent().getStringExtra("date");

        hostNameTv = findViewById(R.id.event_view_title);
        eventNameTv = findViewById(R.id.event_view_event_name);
        timeTv = findViewById(R.id.event_view_time);
        locationTv = findViewById(R.id.event_view_location);
        hostedByTv = findViewById(R.id.event_view_host_by_tv);
        peopleCountTv = findViewById(R.id.event_view_people_count);
        descriptionTv = findViewById(R.id.event_view_description);
        readMore = findViewById(R.id.event_view_read_more_tv);
        joinBtn = findViewById(R.id.event_view_join_btn);
        profilePic = findViewById(R.id.event_view_profile_pic);
        mainImage = findViewById(R.id.new_event_main_image);

        eventNameTv.setText(eventName);
        timeTv.setText(date);
        locationTv.setText(location);
        descriptionTv.setText(description);

        if(hostId.equals(HomeActivity.userId)){
            joinBtn.setVisibility(View.GONE);
        }

        final Uri file=Uri.fromFile(new File(""));
        final StorageReference profileImageReference = storage.getReference().child("users/" + hostId + "/images/profile_pic/profile_pic.jpeg");
        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                profileImageReference.putFile(file);
                Glide.with(EventViewActivity.this)
                        .asBitmap()
                        .apply(new RequestOptions()
                                .override(50,50))
                        .load(uri)
                        .into(profilePic);
                profileImageReference.putFile(file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        if(mainImgUrl != null){
            StorageReference imageReference = storage.getReference().child(mainImgUrl);
            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(EventViewActivity.this)
                            .load(uri)
                            .into(mainImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(HomeActivity.TAG, exception.getMessage());
                }
            });
        }
        else
            mainImage.setImageDrawable(getResources().getDrawable(R.drawable.sports_placeholder_img));

        databaseReference.child("users").child(hostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hostNameTv.setText(dataSnapshot.child("displayName").getValue(String.class));
                hostedByTv.setText(dataSnapshot.child("displayName").getValue(String.class));
                hostPoints = dataSnapshot.child("points").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(eventId).child("peopleCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                peopleCount = dataSnapshot.getValue(Integer.class);
                peopleCountTv.setText(peopleCount + " people are going currently");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(eventId).child("totalSpots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    maxPeople = dataSnapshot.getValue(Integer.class);
                }

                if(maxPeople == peopleCount){
                    joinBtn.setText("Out of tickets");
                    joinBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    joinBtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                name = u.getDisplayName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionTv.setMaxLines(100);
                readMore.setVisibility(View.GONE);
            }
        });

        databaseReference.child("attendees").child(eventId).child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                joined = dataSnapshot.exists();
                if (joined) {
                    joinBtn.setText(R.string.exit);
                } else {
                    joinBtn.setText(R.string.join);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join(joined, eventId, peopleCount, databaseReference, eventName, hostId);
            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventViewActivity.this,HomeActivity.class));
            }
        });

    }

    private void addNotification(String hostId, String eventId, String eventName) {

        Calendar mCurrentTime = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(mCurrentTime.getTime());

        Notification notification = new Notification();
        notification.setDate(date);
        notification.setId(databaseReference.push().getKey());
        notification.setPostId(eventId);
        notification.setText(name + " joined your event '" + eventName +"'");
        notification.setType("Joined Event");

        databaseReference.child("notifications").child(hostId).child(notification.getId()).setValue(notification);
    }

    public void join(Boolean joined, String eventId, int peopleCount, DatabaseReference databaseReference, String eventName, String hostId){
        Calendar mCurrentTime = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = formatter.format(mCurrentTime.getTime());

        if (joined != null && joined) {
            exitEvent(eventId, FirebaseAuth.getInstance().getCurrentUser().getUid(), databaseReference);
            databaseReference.child("posts").child(eventId).child("peopleCount").setValue(peopleCount - 1);
        } else {
            Dialog dialog = onCreateDialog(eventId,hostId);
            dialog.show();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        }
    }

    private Dialog onCreateDialog(final String eventId, final String hostId) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.purchase_ticket_dialog);

        final TextView nameTv = dialog.findViewById(R.id.event_name);
        TextView timeOfEventTv = dialog.findViewById(R.id.time_tv);
        TextView locationTv1 = dialog.findViewById(R.id.location);
        TextView peopleCountTv1 = dialog.findViewById(R.id.people_count);
        final TextView costTv = dialog.findViewById(R.id.cost_tv);
        TextView hostNameTv = dialog.findViewById(R.id.host_by_tv);

        nameTv.setText(eventNameTv.getText().toString());
        timeOfEventTv.setText(timeTv.getText().toString());
        locationTv1.setText(locationTv.getText().toString());
        peopleCountTv1.setText(peopleCount + " people are going");
        hostNameTv.setText(hostedByTv.getText().toString());

        databaseReference.child("posts").child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!TextUtils.isEmpty(dataSnapshot.child("fees").getValue(String.class))){
                    costTv.setText(dataSnapshot.child("fees").getValue(String.class) + " Points");
                }
                else{
                    fees = 0;
                    costTv.setText("0 Points");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button purchaseBtn = dialog.findViewById(R.id.purchase_btn);
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(joinEvent(eventId, peopleCount, databaseReference, hostId)){
                    addNotification(hostId,eventId,eventNameTv.getText().toString());
                }
                else{
                    Toast.makeText(EventViewActivity.this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static boolean joinEvent(String eventId, int peopleCount, final DatabaseReference databaseReference, String hostId) {
        databaseReference.child("users").child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pointsUser = dataSnapshot.child("points").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("posts").child(eventId).child("fees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    fees = Integer.parseInt(dataSnapshot.getValue(String.class));
                }
                else{
                    fees = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        int difference = pointsUser - fees;
        if(difference>=0){
            databaseReference.child("posts").child(eventId).child("peopleCount").setValue(peopleCount + 1);
            databaseReference.child("attendees").child(eventId).child(HomeActivity.userId).setValue(true);

            databaseReference.child("users").child(HomeActivity.userId).child("points").setValue(difference);
            databaseReference.child("eventsAttendedByUsers").child(HomeActivity.userId).child(eventId).setValue(true);
            databaseReference.child("users").child(hostId).child("points").setValue(hostPoints + fees);

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Transaction transaction = new Transaction("Joined Event",fees,format.format(date));
            transaction.setUserId(HomeActivity.userId);
            transaction.setPostId(eventId);
            databaseReference.child("transactions").child(databaseReference.push().getKey()).setValue(transaction);

            return true;
        }

        return false;
    }

    public static void exitEvent(String eventId, String userId, DatabaseReference databaseReference) {
        databaseReference.child("attendees").child(eventId).child(userId).removeValue();
        databaseReference.child("eventsAttendedByUsers").child(userId).child(eventId).removeValue();
    }

}
