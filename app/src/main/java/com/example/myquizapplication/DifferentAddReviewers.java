package com.example.myquizapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class DifferentAddReviewers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_different_add_reviewers);

        Button btnAddVideoReviewer = findViewById(R.id.btnAddVideoReviewer);
        Button btnAddImageReviewer = findViewById(R.id.btnAddImageReviewer);
        Button btnAddTextReviewer = findViewById(R.id.btnAddTextReviewer);

        btnAddVideoReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Add Video Reviewer Activity
                Intent intent = new Intent(DifferentAddReviewers.this, AddVideoReviewer.class);
                startActivity(intent);
            }
        });

        btnAddImageReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Add Image Reviewer Activity
                Intent intent = new Intent(DifferentAddReviewers.this, AddImageReviewer.class);
                startActivity(intent);
            }
        });

        btnAddTextReviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Add Text Reviewer Activity
                Intent intent = new Intent(DifferentAddReviewers.this, AddTextReviewer.class);
                startActivity(intent);
            }
        });
    }
}
