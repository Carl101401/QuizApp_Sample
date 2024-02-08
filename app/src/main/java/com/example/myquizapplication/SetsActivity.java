package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myquizapplication.Adapters.GrideAdapter;
import com.example.myquizapplication.databinding.ActivitySetsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class SetsActivity extends AppCompatActivity {

    private ActivitySetsBinding binding;
    private String key;
    private FirebaseDatabase database;
    private GrideAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        key = getIntent().getStringExtra("key");
        database = FirebaseDatabase.getInstance();

        ImageView setBackArrow = findViewById(R.id.imageSetBack);
        setBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetsActivity.this, AddQuiz.class);
                startActivity(intent);
            }
        });

        // Assuming gridView is your GridView instance
        adapter = new GrideAdapter(getIntent().getIntExtra("sets", 0),
                getIntent().getStringExtra("category"), key, new GrideAdapter.GridListener() {


            @Override
            public void addSets() {
                database.getReference().child("categories").child(key).child("setNum")
                        .setValue(getIntent().getIntExtra("sets", 0) + 1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Increment the number of sets
                                    getIntent().putExtra("sets", getIntent().getIntExtra("sets", 0) + 1);
                                    // Notify the adapter and show toast message
                                    adapter.sets++;
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(SetsActivity.this, "Quiz Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }




            @Override
            public void onSetLongPress(int position) {
                // Add your logic here if needed
            }
        });

        binding.gridView.setAdapter(adapter);
    }
    public void onBackPressed() {
        // Do nothing or add a message if you want
    }
}
