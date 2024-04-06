package com.theindiecorp.khelodadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theindiecorp.khelodadmin.Adapter.SportsListAdapter;

import java.sql.Struct;
import java.util.ArrayList;

public class SportsListActivity extends AppCompatActivity {

    private ArrayList<String> sports = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final SportsListAdapter adapter = new SportsListAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("sports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sports = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    sports.add(snapshot.getKey());
                }
                adapter.setSports(sports);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = onCreateDialog();
                dialog.show();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            }
        });
    }

    private Dialog onCreateDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_input_dialog);

        Toolbar toolbar = dialog.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle("Add Sport");

        final TextInputEditText contentEt = dialog.findViewById(R.id.content_et);
        contentEt.setHint("Sport name");

        Button submitBtn = dialog.findViewById(R.id.add_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("sports").child(contentEt.getText().toString()).setValue(true);
                dialog.dismiss();
                startActivity(new Intent(SportsListActivity.this, SportsDetailsActivity.class).putExtra("sport", contentEt.getText().toString()));
            }
        });

        return dialog;
    }
}