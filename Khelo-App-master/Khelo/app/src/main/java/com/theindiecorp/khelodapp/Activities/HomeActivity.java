package com.theindiecorp.khelodapp.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.theindiecorp.khelodapp.Adapters.NotificationAdapter;
import com.theindiecorp.khelodapp.Model.Notification;
import com.theindiecorp.khelodapp.Fragments.MainFeedFragment;
import com.theindiecorp.khelodapp.Fragments.NewArticleFragment;
import com.theindiecorp.khelodapp.Fragments.PointsFragment;
import com.theindiecorp.khelodapp.Fragments.ProfileFragment;
import com.theindiecorp.khelodapp.Fragments.SearchFragment;
import com.theindiecorp.khelodapp.Fragments.TransactionsFragment;
import com.theindiecorp.khelodapp.R;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "stupid";
    public static String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String eventIdFromIntent = "";
    public static String postType = "article";
    public static String involvedAs = "";

    private DrawerLayout drawer;

    private LocationManager locationManager;

    private static final String USER_ID = "USER_ID";
    private static final String EDITABLE = "EDITABLE";

    Toolbar toolbar;
    BottomNavigationView navigationView;
    NavigationView nv;

    public static Location userLoc;
    public static final String MY_PREFS_NAME = "FirstUserPref";

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame_container, fragment);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    fragment = new MainFeedFragment();
                    if(!TextUtils.isEmpty(eventIdFromIntent)){
                        Bundle args = new Bundle();
                        args.putString("eventId", eventIdFromIntent);
                        fragment.setArguments(args);
                    }
                    loadFragment(fragment);
                    return true;
                case R.id.nav_search:
//                    toolbar.setTitle("Explore");
                    fragment = new SearchFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nav_new_post:
                    startActivity(new Intent(HomeActivity.this, NewPostActivity.class));
                    return true;
                case R.id.nav_profile:
//                    toolbar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nav_your_events:
                    loadNotifications();
                    return true;
            }
            return false;
        }
    };

    private void loadNotifications() {
        LinearLayout notificationSheet = findViewById(R.id.notification_sheet);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(notificationSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        RecyclerView recyclerView = notificationSheet.findViewById(R.id.notification_recycler);
        Toolbar toolbar = notificationSheet.findViewById(R.id.notifi_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final NotificationAdapter adapter = new NotificationAdapter(this, new ArrayList<Notification>());
        recyclerView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("notifications").child(HomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Notification> notifications = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Notification n = snapshot.getValue(Notification.class);
                        n.setId(snapshot.getKey());
                        notifications.add(n);
                    }
                    Collections.reverse(notifications);
                    adapter.setNotifications(notifications);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(userId).child("archived").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(HomeActivity.this, "Your account is Archived by the Admin.", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    FirebaseDatabase.getInstance().getReference("messageTokens").child(userId).setValue(token);
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);

        nv = (NavigationView) findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.nav_home){
                    loadFragment(new MainFeedFragment());
                    drawer.closeDrawers();
                }
                else if(id == R.id.nav_search){
                    loadFragment(new SearchFragment());
                    drawer.closeDrawers();
                }
                else if(id == R.id.nav_new_post){
                    loadFragment(new NewArticleFragment());
                    drawer.closeDrawers();
                }
                else if(id == R.id.nav_profile){
                    loadFragment(new ProfileFragment());
                    drawer.closeDrawers();
                }
                else if(id == R.id.points){
                    loadFragment(new PointsFragment());
                    drawer.closeDrawers();
                }
                else if(id == R.id.transactions){
                    loadFragment(new TransactionsFragment());
                    drawer.closeDrawers();
                }
                else if(id == R.id.nav_reset){
                    startActivity(new Intent(HomeActivity.this,ResetActivity.class));
                    drawer.closeDrawers();
                }
                else if(id == R.id.nav_logout){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    drawer.closeDrawers();
                }
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        locationManager = (LocationManager) this.
                getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Enable Location Service");
                builder.setMessage("Enable location service to update your location?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                builder.show();
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }

        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (userLoc != null) {
                    saveLocation(userLoc);
                }
                else
                    start();
            }
        }.start();

        // added from here
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(listener);
        loadFragment(new MainFeedFragment());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container,
                    new MainFeedFragment()).commit();

        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container,
                        new MainFeedFragment()).commit();
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container,
                        new SearchFragment()).commit();
                break;
            case R.id.nav_new_post:
                fragment = new NewArticleFragment();
                loadFragment(fragment);
                return true;
            case R.id.nav_your_events:
                fragment = new ProfileFragment();
                loadFragment(fragment);
                return true;
            case R.id.nav_profile:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_reset:
                startActivity(new Intent(this, ResetActivity.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            userLoc = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void saveLocation(Location location) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userId).child("latitude").setValue(location.getLatitude());
        databaseReference.child("users").child(userId).child("longitude").setValue(location.getLongitude());
    }

}

