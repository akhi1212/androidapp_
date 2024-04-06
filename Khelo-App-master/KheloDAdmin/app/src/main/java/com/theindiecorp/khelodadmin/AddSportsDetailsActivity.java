package com.theindiecorp.khelodadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theindiecorp.khelodadmin.Adapter.DetailsSportsAdapter;

import java.util.ArrayList;

public class AddSportsDetailsActivity extends AppCompatActivity {

    String detailType = "Text field";
    ArrayList<String> options = new ArrayList<>();
    DetailsSportsAdapter detailsSportsAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sports_details);

        final String sport = getIntent().getStringExtra("sport");

        Toolbar toolbar = findViewById(R.id.dialog_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText detailNameEt = findViewById(R.id.content_et);
        Spinner detailTypeSpinner = findViewById(R.id.type_spinner);
        final Button addDetailBtn = findViewById(R.id.add_detail);
        final RecyclerView optionsRecycler = findViewById(R.id.options_recycler);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.detail_type_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        detailTypeSpinner.setAdapter(adapter);

        detailTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                detailType = adapterView.getItemAtPosition(i).toString();
                if(detailType.equals("Text field")){
                    addDetailBtn.setVisibility(View.GONE);
                    optionsRecycler.setVisibility(View.GONE);
                }
                else{
                    addDetailBtn.setVisibility(View.VISIBLE);
                    optionsRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addDetailBtn.setOnClickListener(new View.OnClickListener() {
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

        optionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        detailsSportsAdapter = new DetailsSportsAdapter(this, new ArrayList<String>());
        optionsRecycler.setAdapter(detailsSportsAdapter);

        Button submitBtn = findViewById(R.id.add_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(detailNameEt.getText().toString())){
                    Toast.makeText(AddSportsDetailsActivity.this, "Enter name of the detail", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(detailType.equals("Text field")){
                    databaseReference.child("sportDetails").child(sport).child(detailNameEt.getText().toString()).child("type").setValue(detailType);
                    finish();
                }
                else{
                    if(options.isEmpty()){
                        Toast.makeText(AddSportsDetailsActivity.this, "Please add atleast one option", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    databaseReference.child("sportDetails").child(sport).child(detailNameEt.getText().toString()).child("type").setValue(detailType);
                    databaseReference.child("sportDetails").child(sport).child(detailNameEt.getText().toString()).child("options").setValue(options);
                    finish();
                }
            }
        });
    }

    private Dialog onCreateDialog(){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.custom_input_dialog);

        Toolbar toolbar = dialog.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle("Add Option");

        final TextInputEditText contentEt = dialog.findViewById(R.id.content_et);
        contentEt.setHint("Enter Here");

        Button submitBtn = dialog.findViewById(R.id.add_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(contentEt.getText().toString())){
                    options.add(contentEt.getText().toString());
                    detailsSportsAdapter.setDetails(options);
                    detailsSportsAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }
}