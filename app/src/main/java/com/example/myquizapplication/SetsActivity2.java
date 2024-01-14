package com.example.myquizapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquizapplication.Adapters.GrideAdapter2;
import com.example.myquizapplication.databinding.ActivitySets2Binding;
import com.google.firebase.database.FirebaseDatabase;

public class SetsActivity2 extends AppCompatActivity {
    ActivitySets2Binding binding;
    FirebaseDatabase database;
    GrideAdapter2 adapter;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySets2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");

        adapter = new GrideAdapter2(getIntent().getIntExtra("sets", 0),
                getIntent().getStringExtra("category"));

        binding.gridView.setAdapter(adapter);



    }
}