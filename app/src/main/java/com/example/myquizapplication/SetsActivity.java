package com.example.myquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myquizapplication.Adapters.GrideAdapter;
import com.example.myquizapplication.databinding.ActivitySetsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class SetsActivity extends AppCompatActivity {

    private ActivitySetsBinding binding;
    private FirebaseDatabase database;
    private GrideAdapter adapter;
    private String key;
    private static final long LONG_PRESS_DURATION = 10000; // 10 seconds
    private Handler longPressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");

        ImageView setBackArrow = findViewById(R.id.imageSetBack);
        setBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetsActivity.this, AddQuiz.class);
                startActivity(intent);
            }
        });

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
                                    adapter.sets++;
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


            @Override
            public void deleteSet(int position) {
                // TODO: Implement your logic to delete the set data in the Realtime Database
                String setNodeKey = "categories/" + key + "/sets/" + position; // Adjust the path based on your database structure

                database.getReference().child(setNodeKey).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Update the UI after successful deletion
                                    int updatedSets = getIntent().getIntExtra("sets", 0) - 1;
                                    getIntent().putExtra("sets", updatedSets);
                                    adapter.sets = updatedSets;
                                    adapter.notifyDataSetChanged();

                                    Toast.makeText(SetsActivity.this, "Set deleted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SetsActivity.this, "Error deleting set: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


            // setRef.removeValue();



            @Override
            public void onSetLongPress(int position) {
                showDeleteConfirmationDialog(position);
            }
        });

        binding.gridView.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this set?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSet(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        // Schedule the deletion after a certain time
        longPressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deleteSet(position);
            }
        }, LONG_PRESS_DURATION);
    }

    private void deleteSet(int position) {
        adapter.onSetLongPress(position);
        longPressHandler.removeCallbacksAndMessages(null);
    }
}
